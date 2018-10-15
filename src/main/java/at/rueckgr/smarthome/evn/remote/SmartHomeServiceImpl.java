package at.rueckgr.smarthome.evn.remote;

import de.eq3.max.cl.ClientException;
import de.eq3.max.cl.IMaxRemote;
import de.eq3.max.cl.dto.HeatingThermostatDeviceState;
import de.eq3.max.cl.dto.MaxCubeState;
import de.eq3.max.cl.dto.PushButtonDeviceState;
import de.eq3.max.cl.dto.ShutterContactDeviceState;
import de.eq3.max.cl.dto.command.SetRoomAutoModeWithTemperature;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.schema.beans.MaxRemote;

import java.util.ArrayList;
import java.util.List;

public class SmartHomeServiceImpl implements SmartHomeService {
    private static final Logger logger = LogManager.getLogger(SmartHomeServiceImpl.class);

    private final InternalState state;
    private final IMaxRemote remoteService;

    public SmartHomeServiceImpl(final String username, final String password) {
        Validate.notBlank(username);
        Validate.notBlank(password);

        state = new InternalState();
        state.setUsername(username);
        state.setPassword(password);

        final MaxRemote maxRemote = new MaxRemote();
        remoteService = maxRemote.getIMaxRemotePort();
    }

    @Override
    public void login() {
        logger.info("Logging in with username {}", state.getUsername());

        String sessionToken;
        try {
            sessionToken = remoteService.login(state.getUsername(), state.getPassword());
        } catch (ClientException e) {
            throw new SmartHomeException(e);
        }

        if (StringUtils.isBlank(sessionToken)) {
            throw new SmartHomeException();
        }

        state.setSessionToken(sessionToken);

        addInterceptor(state.getSessionToken());
    }

    private void addInterceptor(final String sessionToken) {
        final HeaderInterceptor headerInterceptor = new HeaderInterceptor(sessionToken);

        final Client proxy = ClientProxy.getClient(remoteService);
        proxy.getOutInterceptors().clear();
        proxy.getOutInterceptors().add(headerInterceptor);
    }

    @Override
    public SmartHomeState getState() {
        logger.info("Trying to obtain state");

        try {
            final MaxCubeState maxCubeState = remoteService.getMaxCubeState();
            return transformState(maxCubeState);
        }
        catch (ClientException e) {
            throw new SmartHomeException(e);
        }
    }

    private SmartHomeState transformState(final MaxCubeState maxCubeState) {
        final SmartHomeState smartHomeState = new SmartHomeState();

        final List<Room> rooms = new ArrayList<>();
        for (de.eq3.max.cl.dto.Room serviceRoom : maxCubeState.getRooms().getValue().getRoom()) {
            final Room room = new Room();
            room.setId(serviceRoom.getId());
            room.setName(serviceRoom.getName().getValue());
            room.setTemperatureSettings(getTemperatureSettings(serviceRoom));

            List<Device> devices = new ArrayList<>();
            for (de.eq3.max.cl.dto.Device serviceDevice : serviceRoom.getDevices().getValue().getDevice()) {
                final Device device = new Device();
                device.setSerialNumber(serviceDevice.getSerialNumber().getValue());
                device.setName(serviceDevice.getName().getValue());

                device.setState(determineState(serviceDevice));

                devices.add(device);
            }
            room.setDevices(devices);

            rooms.add(room);
        }
        smartHomeState.setRooms(rooms);
        return smartHomeState;
    }

    private TemperatureSettings getTemperatureSettings(de.eq3.max.cl.dto.Room room) {
        final TemperatureSettings temperatureSettings = new TemperatureSettings();

        temperatureSettings.setAutoTemperature(room.getCurrentAutoTemperature());
        temperatureSettings.setComfortTemperature(room.getComfortTemperature());
        temperatureSettings.setEcoTemperature(room.getEcoTemperature());
        temperatureSettings.setTemperatureMode(getTemperatureMode(room));

        return temperatureSettings;
    }

    private TemperatureSettings.TemperatureMode getTemperatureMode(de.eq3.max.cl.dto.Room room) {
        switch (room.getTemperatureMode().getValue()) {
            case NORMAL:
                return TemperatureSettings.TemperatureMode.NORMAL;
            case COMFORT:
                return TemperatureSettings.TemperatureMode.COMFORT;
            case ECO:
                return TemperatureSettings.TemperatureMode.ECO;
            case NOT_AVAILABLE:
                return TemperatureSettings.TemperatureMode.NOT_AVAILABLE;
            default:
                throw new SmartHomeException("Unknown temperature mode: " + room.getTemperatureMode().getValue());
        }
    }

    private DeviceState determineState(final de.eq3.max.cl.dto.Device device) {
        String value = device.getRadioState().getValue().value();
        if(!StringUtils.equalsIgnoreCase(value, "ok")) {
            return DeviceState.RADIO_ERROR;
        }

        de.eq3.max.cl.dto.DeviceState deviceState = device.getState().getValue();
        if(deviceState instanceof HeatingThermostatDeviceState) {
            HeatingThermostatDeviceState heatingThermostatDeviceState = (HeatingThermostatDeviceState) deviceState;
            if(heatingThermostatDeviceState.isBatteryLow()) {
                return DeviceState.BATTERY_LOW;
            }
        }

        if(deviceState instanceof PushButtonDeviceState) {
            PushButtonDeviceState pushButtonDeviceState = (PushButtonDeviceState) deviceState;
            if(pushButtonDeviceState.isBatteryLow()) {
                return DeviceState.BATTERY_LOW;
            }
        }

        if(deviceState instanceof ShutterContactDeviceState) {
            ShutterContactDeviceState shutterContactDeviceState = (ShutterContactDeviceState) deviceState;
            if(shutterContactDeviceState.isBatteryLow()) {
                return DeviceState.BATTERY_LOW;
            }
        }

        return DeviceState.OK;
    }

    @Override
    public void setSessionToken(final String sessionToken) {
        state.setSessionToken(sessionToken);

        addInterceptor(state.getSessionToken());
    }

    @Override
    public String getSessionToken() {
        return state.getSessionToken();
    }

    @Override
    public void setTemperatureSettings(final Room room, final TemperatureSettings oldSettings, final TemperatureSettings newSettings) {
        try {
            final double oldTemperature = getTargetTemperature(oldSettings);
            final SetRoomAutoModeWithTemperature setRoomAutoModeWithTemperature = new SetRoomAutoModeWithTemperature();
            setRoomAutoModeWithTemperature.setRoomId(room.getId());
            final double targetTemperature = getTargetTemperature(newSettings);
            setRoomAutoModeWithTemperature.setTemperature(targetTemperature);

            logger.info("Setting temperature for room {} from {} to {}", room.getName(), oldTemperature, targetTemperature);

            final boolean b = remoteService.setRoomAutoModeWithTemperature(setRoomAutoModeWithTemperature);
            if(!b) {
                throw new SmartHomeException("Unable to set new target temperature");
            }
        }
        catch (ClientException e) {
            throw new SmartHomeException(e);
        }
    }

    private double getTargetTemperature(final TemperatureSettings temperatureSettings) {
        switch(temperatureSettings.getTemperatureMode()) {
            case NORMAL:
                return temperatureSettings.getAutoTemperature();
            case ECO:
                return temperatureSettings.getEcoTemperature();
            case COMFORT:
                return temperatureSettings.getComfortTemperature();
            default:
                throw new SmartHomeException("Unknown temperature mode: " + temperatureSettings.getTemperatureMode());
        }
    }
}

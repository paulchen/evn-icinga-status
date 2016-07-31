package at.rueckgr.smarthome.evn.remote;

import de.eq3.max.cl.ClientException;
import de.eq3.max.cl.IMaxRemote;
import de.eq3.max.cl.dto.MaxCubeState;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.schema.beans.MaxRemote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SmartHomeService implements Serializable {
    private final InternalState state;
    private final IMaxRemote remoteService;

    public SmartHomeService(final String username, final String password) {
        Validate.notBlank(username);
        Validate.notBlank(password);

        state = new InternalState();
        state.setUsername(username);
        state.setPassword(password);

        final MaxRemote maxRemote = new MaxRemote();
        remoteService = maxRemote.getIMaxRemotePort();
    }

    public void login() {
        String sessionToken = null;
        try {
            sessionToken = remoteService.login(state.getUsername(), state.getPassword());
        }
        catch (ClientException e) {
            throw new SmartHomeException(e);
        }

        if(StringUtils.isBlank(sessionToken)) {
            throw new SmartHomeException();
        }

        final HeaderInterceptor headerInterceptor = new HeaderInterceptor(sessionToken);

        final Client proxy = ClientProxy.getClient(remoteService);
        proxy.getOutInterceptors().clear();
        proxy.getOutInterceptors().add(headerInterceptor);
    }

    public SmartHomeState getState() {
        boolean alreadyTriedToLogin = false;
        if(state.getSessionToken() == null) {
            login();
            alreadyTriedToLogin = true;
        }

        MaxCubeState maxCubeState;
        try {
            maxCubeState = remoteService.getMaxCubeState();
        }
        catch (ClientException e) {
            if(alreadyTriedToLogin) {
                throw new SmartHomeException(e);
            }

            login();
            try {
                maxCubeState = remoteService.getMaxCubeState();
            }
            catch (ClientException e1) {
                throw new SmartHomeException(e1);
            }
        }

        return transformState(maxCubeState);
    }

    private SmartHomeState transformState(final MaxCubeState maxCubeState) {
        final SmartHomeState smartHomeState = new SmartHomeState();

        final List<Room> rooms = new ArrayList<>();
        for (de.eq3.max.cl.dto.Room serviceRoom : maxCubeState.getRooms().getRoom()) {
            final Room room = new Room();
            room.setId(serviceRoom.getId());
            room.setName(serviceRoom.getName());

            List<Device> devices = new ArrayList<>();
            for (de.eq3.max.cl.dto.Device serviceDevice : serviceRoom.getDevices().getDevice()) {
                final Device device = new Device();
                device.setSerialNumber(serviceDevice.getSerialNumber());
                device.setName(serviceDevice.getName());
                final String radioState = serviceDevice.getRadioState().value();
                device.setOk(StringUtils.equalsIgnoreCase(radioState, "ok"));

                devices.add(device);
            }
            room.setDevices(devices);

            rooms.add(room);
        }
        smartHomeState.setRooms(rooms);
        return smartHomeState;
    }
}

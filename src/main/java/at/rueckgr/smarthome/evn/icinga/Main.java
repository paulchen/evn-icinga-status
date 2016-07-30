package at.rueckgr.smarthome.evn.icinga;

import de.eq3.max.cl.IMaxRemote;
import de.eq3.max.cl.dto.Device;
import de.eq3.max.cl.dto.MaxCubeState;
import de.eq3.max.cl.dto.Room;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.schema.beans.MaxRemote;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        MaxRemote maxRemote = new MaxRemote();
        IMaxRemote port = maxRemote.getIMaxRemotePort();

        if (args.length != 2) {
            return;
        }

        String loginResponse = port.login(args[0], args[1]);

        if (StringUtils.isBlank(loginResponse)) {
            return;
        }

        HeaderInterceptor headerInterceptor = new HeaderInterceptor(loginResponse);

        Client proxy = ClientProxy.getClient(port);
        proxy.getOutInterceptors().add(headerInterceptor);

        MaxCubeState maxCubeState = port.getMaxCubeState();

        List<Pair<String, String>> problems = new ArrayList<Pair<String, String>>();
        List<Room> rooms = maxCubeState.getRooms().getRoom();
        for (Room room : rooms) {
            String roomName = room.getName();
            List<Device> devices = room.getDevices().getDevice();
            for (Device device : devices) {
                String deviceName = device.getName();
                String deviceState = device.getRadioState().value();
                if(!StringUtils.equalsIgnoreCase("ok", deviceState)) {
                    problems.add(new ImmutablePair<String, String>(roomName, deviceName));
                }
            }
        }

        System.out.print(problems);
    }
}

package at.rueckgr.smarthome.evn.icinga;

import at.rueckgr.smarthome.evn.remote.Device;
import at.rueckgr.smarthome.evn.remote.Room;
import at.rueckgr.smarthome.evn.remote.SmartHomeService;
import at.rueckgr.smarthome.evn.remote.SmartHomeState;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Wrong number of command line arguments");
        }

        StatusProperties properties = new StatusProperties(args[0]);

        final SmartHomeService service = new SmartHomeService(properties.getUsername(), properties.getPassword());

        if(!StringUtils.isBlank(properties.getSessionToken())) {
            service.setSessionToken(properties.getSessionToken());
        }

        final SmartHomeState state = service.getState();

        final List<Problem> problems = new ArrayList<>();
        final List<Room> rooms = state.getRooms();
        for (Room room : rooms) {
            List<Device> devices = room.getDevices();

            //noinspection Convert2streamapi
            for (Device device : devices) {
                if(!device.isOk()) {
                    problems.add(new Problem(room, device));
                }
            }
        }

        properties.setSessionToken(service.getSessionToken());
        properties.save();

        System.out.print(problems);
    }
}

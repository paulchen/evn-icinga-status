package at.rueckgr.smarthome.evn.icinga;

import at.rueckgr.smarthome.evn.remote.Device;
import at.rueckgr.smarthome.evn.remote.Room;
import at.rueckgr.smarthome.evn.remote.SmartHomeService;
import at.rueckgr.smarthome.evn.remote.SmartHomeState;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            return;
        }

        final SmartHomeService service = new SmartHomeService(args[0], args[1]);
        final SmartHomeState state = service.getState();

        final List<Pair<String, String>> problems = new ArrayList<>();
        final List<Room> rooms = state.getRooms();
        for (Room room : rooms) {
            final String roomName = room.getName();
            List<Device> devices = room.getDevices();

            //noinspection Convert2streamapi
            for (Device device : devices) {
                if(!device.isOk()) {
                    final String deviceName = device.getName();
                    problems.add(new ImmutablePair<>(roomName, deviceName));
                }
            }
        }

        System.out.print(problems);
    }
}

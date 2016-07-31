package at.rueckgr.smarthome.evn.icinga;

import at.rueckgr.smarthome.evn.remote.Device;
import at.rueckgr.smarthome.evn.remote.Room;
import at.rueckgr.smarthome.evn.remote.SmartHomeService;
import at.rueckgr.smarthome.evn.remote.SmartHomeState;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Wrong number of command line arguments");
        }

        final StatusProperties properties = new StatusProperties(args[0]);

        final SmartHomeService service = new SmartHomeService(properties.getUsername(), properties.getPassword());

        final String sessionToken = properties.getSessionToken();
        if (!StringUtils.isBlank(sessionToken)) {
            service.setSessionToken(sessionToken);
        }

        final SmartHomeState state = service.getState();

        final List<Problem> problems = new ArrayList<>();
        final List<Room> rooms = state.getRooms();
        for (Room room : rooms) {
            final List<Device> devices = room.getDevices();

            //noinspection Convert2streamapi
            for (Device device : devices) {
                if (!device.isOk()) {
                    problems.add(new Problem(room, device));
                }
            }
        }

        properties.setSessionToken(service.getSessionToken());
        properties.save();

        final IcingaResult icingaResult = new IcingaResult();
        if (problems.isEmpty()) {
            icingaResult.setStatus(IcingaStatus.OK);
            icingaResult.setText("No problems detected");
        }
        else {
            icingaResult.setStatus(IcingaStatus.CRITICAL);
            icingaResult.setText("Problem(s): " + problems.toString());
        }

        final String statusOutputFile = properties.getStatusOutputFile();
        try (OutputStream outputStream = createOutputStream(statusOutputFile)) {
            IcingaResultWriter.write(outputStream, icingaResult);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static OutputStream createOutputStream(final String statusOutputFile) throws IOException {
        if(StringUtils.isBlank(statusOutputFile)) {
            return System.out;
        }
        return new FileOutputStream(statusOutputFile);
    }
}

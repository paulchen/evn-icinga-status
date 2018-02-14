package at.rueckgr.smarthome.evn.icinga;

import at.rueckgr.smarthome.evn.remote.*;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final int WAIT_SECONDS = 120;
    private static final float TEMPERATURE_DIFFERENCE = .5f;

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

        List<Problem> problems = findProblems(service);
        if(!problems.isEmpty()) {
            problems.stream().map(Problem::getRoom).distinct().forEach(room -> changeTemperatureTwice(service, room));
            problems = findProblems(service);
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

    private static void changeTemperatureTwice(final SmartHomeService service, final Room room) {
        final TemperatureSettings oldSettings = room.getTemperatureSettings();

        final TemperatureSettings temporaryTemperatureSettings = new TemperatureSettings(oldSettings);
        temporaryTemperatureSettings.setTemperatureMode(TemperatureSettings.TemperatureMode.NORMAL);
        temporaryTemperatureSettings.setAutoTemperature(oldSettings.getAutoTemperature() + TEMPERATURE_DIFFERENCE);
        service.setTemperatureSettings(room, temporaryTemperatureSettings);

        sleep(WAIT_SECONDS);

        service.setTemperatureSettings(room, oldSettings);

        sleep(WAIT_SECONDS);
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Problem> findProblems(SmartHomeService service) {
        return service.getState().getRooms()
                .stream()
                .map(Main::findProblems)
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    private static Stream<Problem> findProblems(final Room room) {
        return room.getDevices()
                .stream()
                .filter(device -> !device.getState().isOk())
                .map(device -> new Problem(room, device));
    }

    private static OutputStream createOutputStream(final String statusOutputFile) throws IOException {
        if(StringUtils.isBlank(statusOutputFile)) {
            return System.out;
        }
        return new FileOutputStream(statusOutputFile);
    }
}

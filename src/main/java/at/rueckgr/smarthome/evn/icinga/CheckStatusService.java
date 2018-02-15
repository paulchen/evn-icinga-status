package at.rueckgr.smarthome.evn.icinga;

import at.rueckgr.smarthome.evn.remote.RetryFacade;
import at.rueckgr.smarthome.evn.remote.Room;
import at.rueckgr.smarthome.evn.remote.SmartHomeService;
import at.rueckgr.smarthome.evn.remote.SmartHomeServiceImpl;
import at.rueckgr.smarthome.evn.remote.TemperatureSettings;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckStatusService {
    private static final Logger logger = LogManager.getLogger(CheckStatusService.class);

    private static final int WAIT_SECONDS = 120;
    private static final float TEMPERATURE_DIFFERENCE = .5f;

    private SmartHomeService service;
    private StatusProperties properties;

    public CheckStatusService(final StatusProperties properties) {
        this.properties = properties;
        final SmartHomeServiceImpl smartHomeService = new SmartHomeServiceImpl(properties.getUsername(), properties.getPassword());
        this.service = new RetryFacade(smartHomeService);

        final String sessionToken = properties.getSessionToken();
        if (!StringUtils.isBlank(sessionToken)) {
            this.service.setSessionToken(sessionToken);
        }
    }

    public IcingaResult checkStatus() {
        List<Problem> problems = findProblems();
        logger.info("Problems: {}", problems.toString());

        if(!problems.isEmpty()) {
            problems.stream().map(Problem::getRoom).distinct().forEach(this::changeTemperatureTwice);
            problems = findProblems();

            logger.info("Problems: {}", problems.toString());
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

        return icingaResult;
    }

    private void changeTemperatureTwice(final Room room) {
        logger.info("Executing temperature change cycle for room {}", room.getName());

        final TemperatureSettings oldSettings = room.getTemperatureSettings();

        final TemperatureSettings temporaryTemperatureSettings = new TemperatureSettings(oldSettings);
        temporaryTemperatureSettings.setTemperatureMode(TemperatureSettings.TemperatureMode.NORMAL);
        temporaryTemperatureSettings.setAutoTemperature(oldSettings.getAutoTemperature() + TEMPERATURE_DIFFERENCE);
        service.setTemperatureSettings(room, temporaryTemperatureSettings);

        sleep(WAIT_SECONDS);

        service.setTemperatureSettings(room, oldSettings);

        sleep(WAIT_SECONDS);
    }

    private void sleep(int seconds) {
        try {
            logger.info("Sleeping {} seconds", seconds);
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Problem> findProblems() {
        return service.getState().getRooms()
                .stream()
                .map(this::findProblems)
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    private Stream<Problem> findProblems(final Room room) {
        return room.getDevices()
                .stream()
                .filter(device -> !device.getState().isOk())
                .map(device -> new Problem(room, device));
    }
}

package at.rueckgr.smarthome.evn.icinga;

import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("Wrong number of command line arguments");
        }

        final StatusProperties properties = new StatusProperties(args[0]);
        final CheckStatusService service = new CheckStatusService(properties);
        final IcingaResult icingaResult = service.checkStatus();

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

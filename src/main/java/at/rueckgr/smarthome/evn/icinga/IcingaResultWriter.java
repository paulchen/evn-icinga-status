package at.rueckgr.smarthome.evn.icinga;

import java.io.OutputStream;
import java.io.PrintWriter;

public class IcingaResultWriter {
    private IcingaResultWriter() {
        // don't create instances
    }

    public static void write(final OutputStream outputStream, final IcingaResult icingaResult) {
        PrintWriter writer = new PrintWriter(outputStream);
        writer.println(icingaResult.getStatus().getStatusCode());
        writer.println(icingaResult.getText());
        writer.flush();
    }
}

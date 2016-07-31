package at.rueckgr.smarthome.evn.icinga;

public enum IcingaStatus {
    OK(0),
    WARNING(1),
    CRITICAL(2),
    UNKNOWN(3);

    private final int statusCode;

    IcingaStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

package at.rueckgr.smarthome.evn.remote;

public class SmartHomeException extends RuntimeException {
    public SmartHomeException() {
    }

    public SmartHomeException(String s) {
        super(s);
    }

    public SmartHomeException(Throwable cause) {
        super(cause);
    }
}

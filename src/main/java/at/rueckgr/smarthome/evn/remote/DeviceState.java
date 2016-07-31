package at.rueckgr.smarthome.evn.remote;

public enum DeviceState {
    OK("ok", true),
    BATTERY_LOW("battery", false),
    RADIO_ERROR("radio", false);

    private final String description;
    private final boolean ok;

    DeviceState(final String description, final boolean ok) {
        this.description = description;
        this.ok = ok;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOk() {
        return ok;
    }
}

package at.rueckgr.smarthome.evn.remote;

import java.io.Serializable;

public class Device implements Serializable {
    private String serialNumber;
    private String name;
    private DeviceState state;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceState getState() {
        return state;
    }

    public void setState(DeviceState state) {
        this.state = state;
    }
}

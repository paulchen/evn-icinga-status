package at.rueckgr.smarthome.evn.remote;

import java.io.Serializable;

public class Device implements Serializable {
    private String serialNumber;
    private String name;
    private boolean ok;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = this.serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}

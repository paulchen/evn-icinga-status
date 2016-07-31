package at.rueckgr.smarthome.evn.icinga;

import java.io.Serializable;

public class IcingaResult implements Serializable {
    private IcingaStatus status;
    private String text;

    public IcingaStatus getStatus() {
        return status;
    }

    public void setStatus(IcingaStatus status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

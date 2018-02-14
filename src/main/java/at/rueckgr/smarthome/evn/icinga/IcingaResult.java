package at.rueckgr.smarthome.evn.icinga;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class IcingaResult implements Serializable {
    private IcingaStatus status;
    private String text;
}

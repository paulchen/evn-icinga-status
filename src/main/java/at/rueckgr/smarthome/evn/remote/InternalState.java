package at.rueckgr.smarthome.evn.remote;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class InternalState implements Serializable {
    private String username;
    private String password;
    private String sessionToken;
}

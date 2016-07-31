package at.rueckgr.smarthome.evn.remote;

import java.io.Serializable;

public class InternalState implements Serializable {
    private String username;
    private String password;
    private String sessionToken;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}

package at.rueckgr.smarthome.evn.icinga;

import org.apache.commons.lang3.Validate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Properties;

public class StatusProperties implements Serializable {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SESSION_TOKEN = "sessionToken";

    private final Properties properties;
    private final String filename;

    public StatusProperties(final String filename) {
        Validate.notEmpty(filename);

        this.filename = filename;

        properties = new Properties();
        try {
            properties.load(new FileInputStream(filename));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        checkProperty(USERNAME);
        checkProperty(PASSWORD);
    }

    private void checkProperty(final String key) {
        if(!properties.containsKey(key)) {
            throw new RuntimeException(MessageFormat.format("Missing property {0}", key));
        }
    }

    public void save() {
        try {
            properties.store(new FileOutputStream(filename), null);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return (String) properties.get(USERNAME);
    }

    public String getPassword() {
        return (String) properties.get(PASSWORD);
    }

    public String getSessionToken() {
        return (String) properties.get(SESSION_TOKEN);
    }

    public void setSessionToken(final String sessionToken) {
        properties.setProperty(SESSION_TOKEN, sessionToken);
    }
}
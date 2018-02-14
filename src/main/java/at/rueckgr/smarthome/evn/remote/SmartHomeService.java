package at.rueckgr.smarthome.evn.remote;

public interface SmartHomeService {
    void login();

    SmartHomeState getState();

    void setSessionToken(String sessionToken);

    String getSessionToken();

    void setTemperatureSettings(Room room, TemperatureSettings temperatureSettings);
}

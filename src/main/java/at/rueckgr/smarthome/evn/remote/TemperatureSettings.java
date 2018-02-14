package at.rueckgr.smarthome.evn.remote;

public class TemperatureSettings {
    public enum TemperatureMode {
        COMFORT, ECO, NORMAL
    }

    private double autoTemperature;
    private double ecoTemperature;
    private double comfortTemperature;
    private TemperatureMode temperatureMode;

    public TemperatureSettings() {
    }

    public TemperatureSettings(final TemperatureSettings temperatureSettings) {
        this.autoTemperature = temperatureSettings.getAutoTemperature();
        this.ecoTemperature = temperatureSettings.getEcoTemperature();
        this.comfortTemperature = temperatureSettings.getComfortTemperature();
        this.temperatureMode = temperatureSettings.getTemperatureMode();
    }

    public double getAutoTemperature() {
        return autoTemperature;
    }

    public void setAutoTemperature(double autoTemperature) {
        this.autoTemperature = autoTemperature;
    }

    public double getEcoTemperature() {
        return ecoTemperature;
    }

    public void setEcoTemperature(double ecoTemperature) {
        this.ecoTemperature = ecoTemperature;
    }

    public double getComfortTemperature() {
        return comfortTemperature;
    }

    public void setComfortTemperature(double comfortTemperature) {
        this.comfortTemperature = comfortTemperature;
    }

    public TemperatureMode getTemperatureMode() {
        return temperatureMode;
    }

    public void setTemperatureMode(TemperatureMode temperatureMode) {
        this.temperatureMode = temperatureMode;
    }
}

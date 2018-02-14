package at.rueckgr.smarthome.evn.remote;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TemperatureSettings {
    public enum TemperatureMode {
        COMFORT, ECO, NORMAL
    }

    private double autoTemperature;
    private double ecoTemperature;
    private double comfortTemperature;
    private TemperatureMode temperatureMode;

    public TemperatureSettings(final TemperatureSettings temperatureSettings) {
        this.autoTemperature = temperatureSettings.getAutoTemperature();
        this.ecoTemperature = temperatureSettings.getEcoTemperature();
        this.comfortTemperature = temperatureSettings.getComfortTemperature();
        this.temperatureMode = temperatureSettings.getTemperatureMode();
    }
}

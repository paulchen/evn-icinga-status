package at.rueckgr.smarthome.evn.remote;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Room implements Serializable {
    private Integer id;
    private String name;
    private List<Device> devices;
    private TemperatureSettings temperatureSettings;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public void setTemperatureSettings(TemperatureSettings temperatureSettings) {
        this.temperatureSettings = temperatureSettings;
    }

    public TemperatureSettings getTemperatureSettings() {
        return temperatureSettings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

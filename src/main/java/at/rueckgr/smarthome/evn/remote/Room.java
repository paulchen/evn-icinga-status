package at.rueckgr.smarthome.evn.remote;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Room implements Serializable {
    private Integer id;
    private String name;
    private List<Device> devices;
    private TemperatureSettings temperatureSettings;
}

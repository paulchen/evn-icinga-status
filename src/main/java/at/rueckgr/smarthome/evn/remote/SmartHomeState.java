package at.rueckgr.smarthome.evn.remote;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SmartHomeState implements Serializable {
    private List<Room> rooms;
}

package at.rueckgr.smarthome.evn.remote;

import java.io.Serializable;
import java.util.List;

public class SmartHomeState implements Serializable {
    private List<Room> rooms;

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
}

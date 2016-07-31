package at.rueckgr.smarthome.evn.icinga;

import at.rueckgr.smarthome.evn.remote.Device;
import at.rueckgr.smarthome.evn.remote.Room;

import java.io.Serializable;
import java.text.MessageFormat;

public class Problem implements Serializable {
    private final Room room;
    private final Device device;

    public Problem(final Room room, Device device) {
        this.room = room;
        this.device = device;
    }

    public Room getRoom() {
        return room;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public String toString() {
        final String problem = device.getState().getDescription();
        return MessageFormat.format("room={0}/device={1}/problem={2}", room.getName(), device.getName(), problem);
    }
}

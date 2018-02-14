package at.rueckgr.smarthome.evn.icinga;

import at.rueckgr.smarthome.evn.remote.Device;
import at.rueckgr.smarthome.evn.remote.Room;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Problem implements Serializable {
    private final Room room;
    private final Device device;

    @Override
    public String toString() {
        final String problem = device.getState().getDescription();
        return MessageFormat.format("room={0}/device={1}/problem={2}", room.getName(), device.getName(), problem);
    }
}

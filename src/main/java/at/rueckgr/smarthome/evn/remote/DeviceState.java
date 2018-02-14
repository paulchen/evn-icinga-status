package at.rueckgr.smarthome.evn.remote;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceState {
    OK("ok", true),
    BATTERY_LOW("battery", false),
    RADIO_ERROR("radio", false);

    private final String description;
    private final boolean ok;
}

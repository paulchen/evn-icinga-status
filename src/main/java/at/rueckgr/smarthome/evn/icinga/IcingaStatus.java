package at.rueckgr.smarthome.evn.icinga;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IcingaStatus {
    OK(0),
    WARNING(1),
    CRITICAL(2),
    UNKNOWN(3);

    private final int statusCode;
}

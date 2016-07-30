package at.rueckgr.smarthome.evn.icinga;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HeaderInterceptor extends AbstractPhaseInterceptor<Message> {
    private final String sessionToken;

    public HeaderInterceptor(String sessionToken) {
        super(Phase.POST_PROTOCOL);

        this.sessionToken = sessionToken;
    }

    @SuppressWarnings("unchecked")
    public void handleMessage(Message message) throws Fault {
        Map<String, List<String>> headers = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
        headers.put("SessionToken", Collections.singletonList(sessionToken));

        message.put(Message.PROTOCOL_HEADERS, headers);
    }
}

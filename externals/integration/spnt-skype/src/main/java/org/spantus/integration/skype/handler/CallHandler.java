package org.spantus.integration.skype.handler;

import java.util.List;
import org.spantus.integration.skype.SkypeInfo;
import org.spantus.integration.skype.call.CallStreamServer;

public class CallHandler extends AbstractMessageHandler{

    public static final String GET_CHAT_MESSAGE_RECIEVED = "CALL (\\d+) STATUS RINGING";
    
    public boolean handle(String msg) {
        List<String> list = regexp(GET_CHAT_MESSAGE_RECIEVED, msg);
        if (list.size()!=1) {
            return false;
        }
        String callid = list.get(0);
        answer(callid);

        return true;
    }

    protected void hangup(String callid) {
        String response = getSkypeClient().invoke(SkypeInfo.MSG_CALL_HANGUP, callid);
        log.debug(response);
//        List<String> list = regexp(GET_CHAT_MESSAGE_BODY, response);
    }

    protected void answer(String callid) {
        String response = getSkypeClient().invoke(SkypeInfo.MSG_CALL_ANSWER, callid);
        log.debug(response);
        response = getSkypeClient().invoke(SkypeInfo.MSG_CALL_SETINPUT_FILE, callid, "t_1_2");
        log.debug("set input: " + response);
        response = getSkypeClient().invoke(
                SkypeInfo.MSG_CALL_SETOUTPUT,
                callid, ""+ CallStreamServer.outputPort);
        log.debug("set output : "+ response);

    }

}
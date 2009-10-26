package org.spantus.integration.skype.handler;

import java.util.List;
import org.spantus.integration.skype.SkypeInfo;

public class EchoChatMessageHandler extends AbstractMessageHandler {

    public static final String GET_CHAT_MESSAGE_RECIEVED = "CHATMESSAGE (\\d+) STATUS RECEIVED";
    public static final String GET_CHAT_MESSAGE_BODY = "CHATMESSAGE (\\d+) BODY (.*)";
    public static final String GET_CHAT_MESSAGE_CHATNAME =  "CHATMESSAGE (\\d+) CHATNAME (.+)";



    public boolean handle(String msg) {
        List<String> list = regexp(GET_CHAT_MESSAGE_RECIEVED, msg);
        if (list.size()!=1) {
            return false;
        }

        String messageid = list.get(0);
        log.debug(messageid);
        echoMessage(messageid);
        return true;

    }

    protected void echoMessage(String messageId) {
        String response = getSkypeClient().invoke(SkypeInfo.MSG_GET_CHATMESSAGE, messageId);
        List<String> list = regexp(GET_CHAT_MESSAGE_BODY, response);
        String messageBody = null;
        assertResult(list, 2);
        messageBody = list.get(1);
        log.debug("message body: " + messageBody);
        response = getSkypeClient().invoke(SkypeInfo.MSG_GET_CHATID, messageId);
        log.debug("get chat id resopnse:  " + response);

        list = regexp(GET_CHAT_MESSAGE_CHATNAME, response);
        assertResult(list, 2);
        String chatid = list.get(1);
        log.debug("get chatid: " + chatid);
        
        if (messageBody != null && chatid != null) {
            response = getSkypeClient().invoke(SkypeInfo.MSG_CHATMESSAGE, chatid, messageBody);
            log.debug("resopnse:  " + response);
        }
    }
}
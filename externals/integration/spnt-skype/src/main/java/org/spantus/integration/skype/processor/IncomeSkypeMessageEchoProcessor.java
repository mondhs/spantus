package org.spantus.integration.skype.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.spantus.integration.skype.SkypeClient;
import org.spantus.integration.skype.SkypeInfo;

public class IncomeSkypeMessageEchoProcessor implements IncomeSkypeMessageProcessor {

    private Logger log = Logger.getLogger(IncomeSkypeMessageEchoProcessor.class);
    SkypeClient skypeClient;
    public static final String GET_CHAT_RECIEVED = "CHAT (.+) ACTIVITY_TIMESTAMP (\\d+)";
    public static final String GET_CHAT_MESSAGE_RECIEVED = "CHATMESSAGE (\\d+) STATUS RECEIVED";

    /**
     * Notification from skype
     * @param msg
     */
    public void notify(String msg) {
        log.debug("<" + msg);

       if(handleChat(msg)){
           log.debug("handled as chat");
           return;
       }
       if(handleChatMessage(msg)){
           log.debug("handled as chat message");
           return;
       }
    }

    protected boolean handleChat(String msg) {
        Pattern p = Pattern.compile(GET_CHAT_RECIEVED);
        Matcher m = p.matcher(msg);
        if (m.find()){
           String chatid = null;
            for (int i = 1; i <= m.groupCount(); i++) {
                switch(i){
                    case 1:chatid = m.group(i);break;
                    case 2: break;
                }
            }
            getSkypeClient().invoke(SkypeInfo.MSG_GET_CHATMESSAGES, chatid);
            return true;
        }
        return false;

    }
    protected boolean handleChatMessage(String msg) {
        Pattern p = Pattern.compile(GET_CHAT_MESSAGE_RECIEVED);
        Matcher m = p.matcher(msg);
        if (m.find()) {
            String messageid = null;
            for (int i = 1; i <= m.groupCount(); i++) {
                switch (i) {
                    case 1:
                        messageid = m.group(i);
                        break;
                }
                log.debug("get message with id: " + messageid);
            }
            echoMessage(messageid);
            return true;
        }
        return false;

    }

    protected void echoMessage(String messageId) {
        String response = getSkypeClient().invoke(SkypeInfo.MSG_GET_CHATMESSAGE, messageId);
        log.debug("get msg body resopnse:  " + response);
        Pattern p = Pattern.compile("CHATMESSAGE (\\d+) BODY (.*)");
        Matcher m = p.matcher(response);
        String messageBody = null;
        if (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                switch (i) {
                    case 2:
                        messageBody = m.group(i);
                        break;
                }
            }
            log.debug("message body: " + messageBody);
        }

        response = getSkypeClient().invoke(SkypeInfo.MSG_GET_CHATID, messageId);
        log.debug("get chat id resopnse:  " + response);
        p = Pattern.compile("CHATMESSAGE (\\d+) CHATNAME (.+)");
        m = p.matcher(response);
        String chatid = null;
        if (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                switch (i) {
                    case 2:
                        chatid = m.group(i);
                        break;
                }
            }
            log.debug("get chatid: " + chatid);
        }
        if (messageBody != null && chatid != null) {
            response = getSkypeClient().invoke(SkypeInfo.MSG_CHATMESSAGE, chatid, messageBody);
            log.debug("resopnse:  " + response);
        }
    }

    public SkypeClient getSkypeClient() {
        return skypeClient;
    }

    public void setSkypeClient(SkypeClient skypeClient) {
        this.skypeClient = skypeClient;
    }
}
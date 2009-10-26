package org.spantus.integration.skype.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.spantus.integration.skype.SkypeInfo;

public class ChatHandler extends AbstractMessageHandler{

    public static final String GET_CHAT_RECIEVED = "CHAT (.+) ACTIVITY_TIMESTAMP (\\d+)";

    public boolean handle(String msg) {
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

}
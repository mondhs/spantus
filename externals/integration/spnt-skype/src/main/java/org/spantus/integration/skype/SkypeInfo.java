package org.spantus.integration.skype;

/**
 *
 * @author mondhs
 */
public class SkypeInfo {

    public static final String MSG_CHAT_CREATE = "CHAT CREATE {0}";
    public static final String MSG_CHATMESSAGE = "CHATMESSAGE {0} {1}";
    public static final String MSG_GET_CHATMESSAGES = "GET CHAT {0} CHATMESSAGES";
    public static final String MSG_GET_CHATMESSAGE = "GET CHATMESSAGE {0} BODY";
    public static final String MSG_GET_CHATID = "GET CHATMESSAGE {0} CHATNAME";

    public static final String MSG_CALL_ANSWER = "ALTER CALL {0} ANSWER";
    public static final String MSG_CALL_HANGUP = "ALTER CALL {0} HANGUP";
    
    //public static final String MSG_CALL_SETOUTPUT_FILE = "ALTER CALL {0} SET_OUTPUT file=\"{1}.wav\" SOUNDCARD=\"default\"";
    public static final String MSG_CALL_SETINPUT_FILE = "ALTER CALL {0} SET_INPUT file=\"{1}.wav\" SOUNDCARD=\"default\"";
//    public static final String MSG_CALL_SETOUTPUT_FILE = "ALTER CALL {0} SET_OUTPUT file=\"{1}.wav\",port=\"801235\" SOUNDCARD=\"default\"";
     public static final String MSG_CALL_SETOUTPUT ="ALTER CALL {0} SET_OUTPUT PORT=\"{1}\" SOUNDCARD=\"default\"";
    //public static final String MSG_CALL_SETINPUT_FILE = "ALTER CALL {0} SET_INPUT file=\"{1}.wav\",port=\"801234\" SOUNDCARD=\"default\"";

    public static final String MSG_NAME = "NAME {0}";
    public static final String MSG_PROTOCOL ="PROTOCOL {0}";




    private String appName;
    private String skypeProtocolVersion;

    public SkypeInfo() {
        setAppName("java");
        setSkypeProtocolVersion("8");
    }

    


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getSkypeProtocolVersion() {
        return skypeProtocolVersion;
    }

    public void setSkypeProtocolVersion(String skypeProtocolVersion) {
        this.skypeProtocolVersion = skypeProtocolVersion;
    }
}

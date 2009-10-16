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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.integration.skype;

import java.text.MessageFormat;
import org.apache.log4j.Logger;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 *
 * @author mondhs
 */
public class SkypeClient {

    private DBusConnection conn = null;
    private SkypeAPI skypeAPI = null;
    private SkypeDBusInterface dbussInterface;
    private Logger log = Logger.getLogger(SkypeClient.class);
    private SkypeInfo skypeInfo;

    public SkypeInfo getSkypeInfo() {
        return skypeInfo;
    }

    public void setSkypeInfo(SkypeInfo skypeInfo) {
        this.skypeInfo = skypeInfo;
    }


    public SkypeClient() {
        dbussInterface = new SkypeDBusInterface(this);
        skypeInfo = new SkypeInfo();
    }




    public void connect() {
        try {
            conn = DBusConnection.getConnection(DBusConnection.SESSION);
            skypeAPI = conn.getPeerRemoteObject("com.Skype.API", "/com/Skype", SkypeAPI.class);
            conn.exportObject("/com/Skype/Client", dbussInterface);
            log.debug("Connected");
        } catch (DBusException e) {
            log.error("error connecting", e);
            throw new IllegalArgumentException("error connecting", e);
        }
    }

    public void init() {
        invoke(SkypeInfo.MSG_NAME,getSkypeInfo().getAppName());
        invoke(SkypeInfo.MSG_PROTOCOL,getSkypeInfo().getSkypeProtocolVersion());

    }


    public void writeMessage(String reciever, String message) {
        String chat = invoke(SkypeInfo.MSG_CHAT_CREATE, reciever);
        String[] chatparams = chat.split(" ");
        String chatId = chatparams[1];
        invoke(SkypeInfo.MSG_CHATMESSAGE, chatId, message);


    }
    public void test(){
         String pong = invoke("PING");
         if(!"PONG".equals(pong)){
             throw new IllegalStateException("Skype response not correct " + pong);
         }
    }
    public void notify(String msg){
        log.debug("<" + msg);
    }

    protected String invoke(String pattern, Object... vars){
        return invoke(MessageFormat.format(pattern, vars));
    }

    protected String invoke(String msg){
        log.debug(">"+msg);
        String result = skypeAPI.Invoke(msg);
        log.debug("<" + result);
        return result;
    }

    public void disconnect() {
        if (conn != null) {
            conn.disconnect();
            log.debug("Disconnected");
        }
    }
}
package org.spantus.integration.skype;

import org.freedesktop.dbus.DBusInterface;

/**
 *
 * @author mondhs
 */
public class SkypeDBusInterface implements DBusInterface {

    SkypeClient skypeClient = null;

    public SkypeDBusInterface(SkypeClient skypeClient) {
        this.skypeClient = skypeClient;
    }

    public int Notify(String s1) {
        skypeClient.notify(s1);
        return 0;
    }

    @Override
    public boolean isRemote() {
        return false;
    }
}

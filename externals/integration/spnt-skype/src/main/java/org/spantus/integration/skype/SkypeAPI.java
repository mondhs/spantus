/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.integration.skype;

import org.freedesktop.dbus.DBusInterface;

/**
 *
 * @author mondhs
 */
public interface SkypeAPI extends DBusInterface {
    public String Invoke(String param);

}
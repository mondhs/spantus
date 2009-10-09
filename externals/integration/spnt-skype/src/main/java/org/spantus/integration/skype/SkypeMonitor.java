/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.integration.skype;

import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author mondhs
 */
public class SkypeMonitor implements Runnable {

    private SkypeClient skypeClient;
    Logger log = Logger.getLogger(SkypeMonitor.class);

    public void run() {

        init();

        while (isSkypeAvaible()) {
            skypeClient.writeMessage("mondhs","test");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
              log.debug(e);
            }

        }

    }
    
    public void init() {
        getSkypeClient().connect();
        getSkypeClient().init();
    }

    public void kill() {
        getSkypeClient().disconnect();
        skypeClient = null;
    }

    public boolean isSkypeAvaible() {
        return skypeClient != null;
    }

    public SkypeClient getSkypeClient() {
        if (skypeClient == null) {
            skypeClient = new SkypeClient();
        }
        return skypeClient;
    }

    
}

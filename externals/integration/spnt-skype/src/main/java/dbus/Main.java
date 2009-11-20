/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dbus;

import java.util.Calendar;
import org.spantus.integration.skype.SkypeClientImpl;
import org.spantus.integration.skype.SkypeMonitor;
import org.spantus.integration.skype.call.CallStreamServer;


/**
 *
 * @author mondhs
 */
public class Main {
   

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SkypeMonitor monitor = new SkypeMonitor();
        Thread skypeMonitorThread = new Thread(monitor);
        skypeMonitorThread.start();
        
        CallStreamServer callStreamServer = new CallStreamServer(CallStreamServer.outputPort);
         Thread callStreamServerThread = new Thread(callStreamServer);
        callStreamServerThread.start();
    }

}

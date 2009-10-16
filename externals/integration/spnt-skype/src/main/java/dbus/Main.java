/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dbus;

import java.util.Calendar;
import org.spantus.integration.skype.SkypeClientImpl;
import org.spantus.integration.skype.SkypeMonitor;


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
        Thread t = new Thread(monitor);
        t.start();
    }

}

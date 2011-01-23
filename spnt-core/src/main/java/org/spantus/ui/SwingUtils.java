/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author mondhs
 */
public abstract class SwingUtils {

    /**
     *
     * @param dialog
     */
    public static void centerWindow(JDialog dialog) {
        //      Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // Determine the new location of the window
        int w = dialog.getSize().width;
        int h = dialog.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;

        // Move the window
        dialog.setLocation(x, y);

    }

    public static Dimension currentWindowSize(double widthRatio, double heightRatio) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension rtnDim = new Dimension();
        rtnDim.width = new Double(dim.getWidth() * widthRatio).intValue();
        rtnDim.height = new Double(dim.getHeight() * heightRatio).intValue();
        return rtnDim;
    }

    public static void fullWindow(Frame frame) {
        //      Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(dim.width, dim.height);

        // Move the window
        frame.setLocation(0, 0);
    }
    
    public static void showError(Frame frame, Throwable throwable, String message){
    	JOptionPane.showMessageDialog(frame,
    			message + throwable.getLocalizedMessage(),
			    "Error",
			    JOptionPane.WARNING_MESSAGE);
    	
    }
}

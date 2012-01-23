/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.spantus.core.beans.I18n;
import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;

/**
 * 
 * @author Mindaugas Greibus
 * @since 0.0.1
 * Created on Feb 22, 2009
 */
public class ModifyObjectPopup implements java.awt.event.ActionListener {

    private Logger log = Logger.getLogger(getClass());
    /**
     * As we prompt for user input, this button will appear on the dialog box to
     * signify an approval of input
     */
    private JButton okButton = new JButton("OK");
    /**
     * As we prompt for user input, this button will appear on the dialog box to
     * signify a disapproval of input
     *
     */
    private JButton cancelButton = new JButton("Cancel");
    /**
     * Dialog to present to the user
     */
    private JDialog dlg;
    Set<String> includeFields;
    Set<String> excludeFields;
    /**
     * This boolean reports the result of button presses: isOK will be true if
     * OK was pressed or false if Cancel was pressed
     */
    private boolean isOK = false;

    private I18n i18n;

    /**
     * Creates a new RSwing class instance
     */
    public ModifyObjectPopup() {
        // Add the RSwing class as an action listener to the JButtons
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }


    protected String getStringValue(Object o, Property property) {
        String rtn = null;
        try {
            Object value = property.getGetter().invoke(o);
            if(value != null){
            	rtn = value.toString();
            }
        } catch (IllegalArgumentException e) {
            log.error(e);
        } catch (IllegalAccessException e) {
            log.error(e);
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rtn;
    }

    /**
     * Builds a JPanel containing the fields to query for the specified class
     *
     * @param c
     *            The class for which to build the panel
     * @return
     */
    protected JPanel getPanel(Object o, String labelPrefix, Map<String, Component> propertyControls) {
        // Setup our gridbag layout
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.gridy = 0;
        constraints.ipadx = 2;
        constraints.ipady = 1;
        int height = 0;

        // Create our panel
        JPanel panel = new JPanel(gridbag);

        // Load the properties that we�re going to need to build the panel from
        Map<String, Property> props = ReflectionUtils.getWriteableProperties(o.getClass());

        // Process the properties
        for (Entry<String, Property> propertyEntry : props.entrySet()) {
            String className = propertyEntry.getValue().getClassType();
            if (getExcludeFields() != null && getExcludeFields().contains(propertyEntry.getKey())) {
                continue;
            }
            if (getIncludeFields() != null && !getIncludeFields().contains(propertyEntry.getKey())) {
                continue;
            }
            if (ReflectionUtils.isPrimitiveType(className)) {

                // Build the JLabel and add it to the panel
                JLabel label = new JLabel(createMessage(labelPrefix, propertyEntry.getKey()));
                constraints.gridx = 0;
                constraints.anchor = GridBagConstraints.NORTHEAST;
                gridbag.setConstraints(label, constraints);
                panel.add(label);

                // Handle the Swing component
                constraints.gridx = 1;
                constraints.anchor = GridBagConstraints.NORTHWEST;
                if (ReflectionUtils.isBooleanType(className)) {
                    // Booleans are treated as checkboxes
                    JCheckBox checkbox = new JCheckBox();
                    propertyControls.put(propertyEntry.getKey(), checkbox);
                    gridbag.setConstraints(checkbox, constraints);
                    panel.add(checkbox);
                } else {
                    // All of the rest of the primitive types are handled as
                    // String fields
                    JTextField textField = new JTextField(20);
                    textField.setText(getStringValue(o, propertyEntry.getValue()));
                    propertyControls.put(propertyEntry.getKey(), textField);
                    gridbag.setConstraints(textField, constraints);
                    panel.add(textField);
                }

                // Increment the height of the panel so that we can display the
                // corresponding
                // dialog appropriately
                height += 30;
            }

            // Increment the gridy
            constraints.gridy++;
        }

        // Return the panel that we created
        panel.setSize(400, height);
        return panel;
    }

    protected JDialog createDialog(JFrame frame, String name, boolean modal, JPanel panel) {
        JDialog _dlg = new JDialog(frame, name, true);
        _dlg.getContentPane().setLayout(new BorderLayout());
        _dlg.getContentPane().add(panel, BorderLayout.CENTER);
        // Show the dialog
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension panelSize = panel.getSize();
        int width = panelSize.width+130;
        int height = panelSize.height + 90;
        _dlg.setSize(width, height);
        _dlg.setLocation(screen.width / 2 - width / 2, screen.height / 2
                - height / 2);
        //esc handling
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        _dlg.getRootPane().registerKeyboardAction(this, "cancel", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
       
        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        _dlg.getRootPane().registerKeyboardAction(this, "ok", stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        
        return _dlg;
    }

    /**
     * Builds a dialog box and presents it to the user with fields that all them
     * to build an instance of the specified class. Once the user has built that
     * object then this method constructs an instance of the object and sets its
     * properties.
     *
     * @param className
     * @return
     */
    public Object modifyObject(JFrame frame, String name, Object object) {
        Map<String, Component> propertyControls = new LinkedHashMap<String, Component>();
        JPanel panel = getPanel(object, name, propertyControls);
        this.dlg = createDialog(frame, createMessage(name, "dialogTitle") , true, panel);
        // Add the button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 4, 0));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonContainer.add(buttonPanel);
        dlg.getContentPane().add(buttonContainer, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setVisible(true);

        // If the user cancelled the dialog then don�t build the resultant
        // object
        if (!this.isOK) {
            return null;
        }

        // Build a property map
        Map<String, String> propertyMap = new TreeMap<String, String>();
        for (Iterator<String> i = propertyControls.keySet().iterator(); i.hasNext();) {
            String propertyName = i.next();
            String propertyValue = null;
            JComponent component = (JComponent) propertyControls.get(propertyName);
            if (component instanceof JTextField) {
                propertyValue = ((JTextField) component).getText();
            } else if (component instanceof JCheckBox) {
                propertyValue = Boolean.toString(((JCheckBox) component).isSelected());
            }
            propertyMap.put(propertyName, propertyValue);
        }

        // Build and return the resultant object
        return ReflectionUtils.buildObject(object, propertyMap);
    }

    /**
     * Handle button presses
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        // See what button the user pressed
        if (e.getSource() == this.okButton) {
            log.debug("OK pressed");
            this.isOK = true;
        } else {
            if("ok".equals(e.getActionCommand())){
                this.isOK = true;
            }else{
            //cancel or escape
                log.debug("Cancel pressed");
                this.isOK = false;
            }
            
        }

        // Hide the dialog
        dlg.setVisible(false);
    }

    protected String createMessage(String prefix, String key){
        if(getI18n() == null){
            return key;
        }
        return getI18n().getMessage(prefix +"."+key);
    }


    public Set<String> getIncludeFields() {
        return includeFields;
    }

    public void setIncludeFields(Set<String> includeFields) {
        this.includeFields = includeFields;
    }

    public Set<String> getExcludeFields() {
        return excludeFields;
    }

    public void setExcludeFields(Set<String> excludeFields) {
        this.excludeFields = excludeFields;
    }
    
    public I18n getI18n() {
        return i18n;
    }
    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }


    public static void main(String[] args) {
        ModifyObjectPopup rs = new ModifyObjectPopup();
        Set<String> includeFields = new HashSet<String>(
                Arrays.asList(new String[]{"start", "length", "label"}));
        rs.setIncludeFields(includeFields);
        Marker m = new Marker();
        m.setLabel("Test");
        m.setStart(20L);
        m.setLength(10L);
        Object obj = rs.modifyObject(null, "Popup", m);
        System.out.println(obj);
    }


}

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
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.spantus.core.marker.Marker;

public class ModifyObjectPopup implements java.awt.event.ActionListener {
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

	/**
	 * This boolean reports the result of button presses: isOK will be true if
	 * OK was pressed or false if Cancel was pressed
	 */
	private boolean isOK = false;

	/**
	 * Creates a new RSwing class instance
	 */
	public ModifyObjectPopup() {
		// Add the RSwing class as an action listener to the JButtons
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}


	protected String getLabel(String propertyName) {
		return propertyName;
	}
	
	protected String getStringValue(Object o, Property property){
		String rtn = null;
		try {
			Object value = property.getGetter().invoke(o);
			rtn = value.toString();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	protected JPanel getPanel(Object o, Map<String, Component> propertyControls) {
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

		// Load the properties that we’re going to need to build the panel from
		Map<String, Property> props = ReflectionUtils.getWriteableProperties(o.getClass());

		// Process the properties
		for (Entry<String, Property> propertyEntry : props.entrySet()) {
			String className = propertyEntry.getValue().getClassType();

			if (ReflectionUtils.isPrimitiveType(className)) {

				// Build the JLabel and add it to the panel
				JLabel label = new JLabel(getLabel(propertyEntry.getKey()));
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

	protected JDialog createDialog(JFrame frame, String name, boolean modal,JPanel panel){
		JDialog _dlg = new JDialog(frame, name, true);	
		_dlg.getContentPane().setLayout(new BorderLayout());
		_dlg.getContentPane().add(panel, BorderLayout.CENTER);
		// Show the dialog
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension panelSize = panel.getSize();
		int width = panelSize.width;
		int height = panelSize.height + 60;
		_dlg.setSize(width, height);
		_dlg.setLocation(screen.width / 2 - width / 2, screen.height / 2
				- height / 2);
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
		JPanel panel = getPanel(object, propertyControls);
		this.dlg = createDialog(frame, name, true, panel);
		// Add the button panel
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 4, 0));
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonContainer.add(buttonPanel);
		dlg.getContentPane().add(buttonContainer, BorderLayout.SOUTH);
		dlg.setVisible(true);

		// If the user cancelled the dialog then don’t build the resultant
		// object
		if (!this.isOK) {
			return null;
		}

		// Build a property map
		Map<String, String> propertyMap = new TreeMap<String, String>();
		for (Iterator<String> i = propertyControls.keySet().iterator(); i
				.hasNext();) {
			String propertyName =  i.next();
			String propertyValue = null;
			JComponent component = (JComponent) propertyControls
					.get(propertyName);
			if (component instanceof JTextField) {
				propertyValue = ((JTextField) component).getText();
			} else if (component instanceof JCheckBox) {
				propertyValue = Boolean.toString(((JCheckBox) component)
						.isSelected());
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
			System.out.println("OK pressed");
			this.isOK = true;
		} else {
			System.out.println("Cancel pressed");
			this.isOK = false;
		}

		// Hide the dialog
		dlg.setVisible(false);
	}

	public static void main(String[] args) {
		ModifyObjectPopup rs = new ModifyObjectPopup();
		Marker m = new Marker();
		m.setLabel("Test");
		m.setStart(BigDecimal.valueOf(20f));
		m.setLength(BigDecimal.valueOf(10f));
		Object obj = rs.modifyObject(null, "Popup", m);
		System.out.println(  obj );
	}
}
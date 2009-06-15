package org.spantus.work.ui.container.option;

import javax.swing.JComponent;

public class LabelControlEntry {
	
	private JComponent label;
	private JComponent control;
	
	public LabelControlEntry(JComponent label, JComponent control) {
		super();
		this.label = label;
		this.control = control;
	}

	public LabelControlEntry() {
	}
	
	public JComponent getLabel() {
		return label;
	}
	
	public void setLabel(JComponent label) {
		this.label = label;
	}
	public JComponent getControl() {
		return control;
	}
	public void setControl(JComponent control) {
		this.control = control;
	}

	public boolean isVisible() {
		if(label == null && control == null){
			return false;
		}
		if(label == null ){
			return control.isVisible();
		}
		if( control == null){
			return label.isVisible();
		}

		return control.isVisible() || label.isVisible();
	}

	public void setVisible(boolean visible) {
		if(control != null ){
			control.setVisible(visible);
		}
		if( label != null){
			label.setVisible(visible);
		}
	}

}

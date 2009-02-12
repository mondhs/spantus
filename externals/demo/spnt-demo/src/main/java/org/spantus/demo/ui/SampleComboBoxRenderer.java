package org.spantus.demo.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.spantus.demo.dto.SampleDto;

public class SampleComboBoxRenderer extends JLabel implements ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value != null) {
			SampleDto sample = (SampleDto) value;
			setText(sample.getTitle());
		} else {
			setText(" - ");
		}

		return this;
	}

}

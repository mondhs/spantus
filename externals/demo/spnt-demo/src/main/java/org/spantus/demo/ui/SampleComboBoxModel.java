package org.spantus.demo.ui;

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.spantus.demo.dto.SampleDto;

public class SampleComboBoxModel extends DefaultComboBoxModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<SampleDto> elements;
	
	public SampleComboBoxModel(List<SampleDto> elements) {
		this.elements = elements;
	}
	
	
	public int getSize() {
		return getElements().size();
	}
	
	public Object getElementAt(int index) {
		return getElements().get(index);
	}
	
	public List<SampleDto> getElements() {
		return elements;
	}
	
	
}

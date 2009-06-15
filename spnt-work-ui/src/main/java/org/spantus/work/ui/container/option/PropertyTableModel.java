package org.spantus.work.ui.container.option;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.work.ui.i18n.I18nFactory;

public class PropertyTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	private static final String[] col_names = { "Name", "Value" };
	public List<String> columnNames;
	private ExtractorParam selectedExtractorParam;
	
	@Override
	public int getRowCount() {
		return ExtractorModifiersEnum
		.values().length + 1;
	}
	
	@Override
	public String getColumnName(int column) {
			return col_names[column];
	}
	@Override
	public int getColumnCount() {
		return getColumnNames().size();
	}
	
	public List<String> getColumnNames(){
		if(columnNames == null){
			columnNames = new ArrayList<String>();
			for (String iterable_element : col_names) {
				columnNames.add(iterable_element);
			}
		}
		return columnNames;
	}
	
	public String getPropertyRowName(int row){
		if(row == 0){
			return "FeatureName";
		}
		return ExtractorModifiersEnum.values()[row - 1].name();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		if (getSelectedExtractorParam() == null)
			return null;
		if (col == 0) {
				return getMessage(getPropertyRowName(row));
		} else if (col == 1) {
			if (row == 0) {
				String name = getSelectedExtractorParam().getClassName();
				name = name.split(":")[1];
				return getMessage(name);
			} else {
				Object obj = getSelectedExtractorParam().getProperties().get(getPropertyRowName(row));
				obj = obj == null?Boolean.FALSE:obj;
				return obj;
			}
		}
		return super.getValueAt(row, col);
	}

	String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}
	
	public boolean isCellEditable(int row, int col) {
		if (col == 0 || row == 0)
			return false;
		return true;
	}

	public ExtractorParam getSelectedExtractorParam() {
		return selectedExtractorParam;
	}

	public void setSelectedExtractorParam(ExtractorParam selectedExtractorParam) {
		this.selectedExtractorParam = selectedExtractorParam;
	}

}

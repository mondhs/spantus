package org.spnt.recognition.dtw.ui;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.spnt.recognition.bean.CorpusFileEntry;

public class CorpusEntryTableModel implements TableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<CorpusFileEntry> corpusEntries;
	Class<?>[] columnClasses = new Class<?>[]{Long.class,String.class};
	String[] columnNames = new String[]{"id", "Name"};
	
	
	public CorpusEntryTableModel(List<CorpusFileEntry> corpusEntries) {
		this.corpusEntries = corpusEntries;
	}


	public Class<?> getColumnClass(int i) {
		return columnClasses[i];
	}

	public int getColumnCount() {
		return columnClasses.length;
	}

	public String getColumnName(int i) {
		return columnNames[i];
	}

	public int getRowCount() {
		return corpusEntries.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object obj = null;
		CorpusFileEntry entry = corpusEntries.get(rowIndex);
		switch (columnIndex) {
		case 0:
			obj = (rowIndex+1);
			break;
		case 1:
			obj = entry.getCorpusEntry().getName();
			break;
		default:
			break;
		}
		return obj;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex > 0;
	}


	public void setValueAt(Object obj, int rowIndex, int columnIndex) {
		CorpusFileEntry entry = corpusEntries.get(rowIndex);
		switch (columnIndex) {
		case 0:
			throw new IllegalArgumentException();
		case 1:
			entry.getCorpusEntry().setName((String) obj);
			break;
		default:
			break;
		}
	}


	public void addTableModelListener(TableModelListener l) {
	}


	public void removeTableModelListener(TableModelListener l) {
	}

}

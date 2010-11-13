package org.spantus.externals.recognition.ui;

import java.util.Collection;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.spantus.externals.recognition.bean.CorpusEntry;


public class CorpusEntryTableModel implements TableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Collection<CorpusEntry> corpusEntries;
	Class<?>[] columnClasses = new Class<?>[]{Long.class,String.class};
	String[] columnNames = new String[]{"id", "Name"};
	
	
	public CorpusEntryTableModel(Collection<CorpusEntry> corpusEntries) {
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
		CorpusEntry entry = (CorpusEntry)corpusEntries.toArray()[rowIndex];
		switch (columnIndex) {
		case 0:
			obj = (rowIndex+1);
			break;
		case 1:
			obj = entry.getName();
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
		CorpusEntry entry = (CorpusEntry) corpusEntries.toArray()[rowIndex];
		switch (columnIndex) {
		case 0:
			throw new IllegalArgumentException();
		case 1:
			entry.setName((String) obj);
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

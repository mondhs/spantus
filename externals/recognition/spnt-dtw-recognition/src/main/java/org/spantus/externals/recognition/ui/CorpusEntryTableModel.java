package org.spantus.externals.recognition.ui;

import java.util.Collection;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.spantus.core.beans.SignalSegment;
import org.spantus.core.service.CorpusRepository;

public class CorpusEntryTableModel implements TableModel {
	CorpusRepository corpusRepository;
	SignalSegment[] corpusEntries;

	Class<?>[] columnClasses = new Class<?>[] { Long.class, String.class };
	String[] columnNames = new String[] { "id", "Name" };

	public CorpusEntryTableModel(CorpusRepository corpusRepository) {
		this.corpusRepository = corpusRepository;
		Collection<SignalSegment> entries = corpusRepository.findAllEntries();
		corpusEntries = corpusRepository.findAllEntries().toArray(
				new SignalSegment[entries.size()]);
	}

	public void refresh() {
		corpusRepository.flush();
		Collection<SignalSegment> entries = corpusRepository.findAllEntries();
		corpusEntries = entries.toArray(new SignalSegment[entries.size()]);
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
		return corpusEntries.length;
	}

	public SignalSegment getCorpusEntry(int selectedRow) {
		SignalSegment entry = (SignalSegment) corpusEntries[selectedRow];
		return entry;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object obj = null;

		SignalSegment entry = getCorpusEntry(rowIndex);
		switch (columnIndex) {
		case 0:
			obj = (rowIndex + 1);
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
		SignalSegment entry = getCorpusEntry(rowIndex);
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

	public void delete(int rowIndex) {
		corpusRepository.delete(getCorpusEntry(rowIndex).getId());
		Collection<SignalSegment> entries = corpusRepository.findAllEntries();
		corpusEntries = entries.toArray(new SignalSegment[entries.size()]);
	}

	public void saveAll() {
		for (SignalSegment entry : corpusEntries) {
			corpusRepository.update( entry);
		}

	}

}

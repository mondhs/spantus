package org.spantus.externals.recognition.ui;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.corpus.CorpusRepository;


public class CorpusEntryTableModel implements TableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CorpusRepository corpusRepository;
        Object[] corpusEntries;

	Class<?>[] columnClasses = new Class<?>[]{Long.class,String.class};
	String[] columnNames = new String[]{"id", "Name"};
	
	
	public CorpusEntryTableModel(CorpusRepository corpusRepository) {
		this.corpusRepository = corpusRepository;
                corpusEntries = corpusRepository.findAllEntries().toArray();
	}
        
        public void refresh(){
            corpusRepository.flush();
            corpusEntries = corpusRepository.findAllEntries().toArray();
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
        
        public CorpusEntry getCorpusEntry(int selectedRow) {
            CorpusEntry entry = (CorpusEntry)corpusEntries[selectedRow];
            return entry;
        }


	public Object getValueAt(int rowIndex, int columnIndex) {
		Object obj = null;
                
		CorpusEntry entry = getCorpusEntry(rowIndex);
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
		CorpusEntry entry = getCorpusEntry(rowIndex);
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
        
        public void delete(int rowIndex){
            corpusRepository.delete(getCorpusEntry(rowIndex));
            corpusEntries = corpusRepository.findAllEntries().toArray();
        }

        public void saveAll() {
            for (Object entry : corpusEntries) {
                corpusRepository.update((CorpusEntry)entry);
            }

        }

}

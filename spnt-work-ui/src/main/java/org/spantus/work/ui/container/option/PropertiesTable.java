package org.spantus.work.ui.container.option;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.ExtractorParamUtils;

public class PropertiesTable extends JTable {
	private Logger log = Logger.getLogger(PropertiesTable.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, DefaultCellEditor> editors;

	public PropertiesTable(PropertyTableModel model) {
		super(model);
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		editors = new HashMap<String, DefaultCellEditor>();
		
		for (ExtractorModifiersEnum modifier : ExtractorModifiersEnum.values()) {
			editors.put(modifier.name(), new DefaultCellEditor(
					createChb(modifier.name())));
		}
	}
	
	
	
	protected JCheckBox createChb(String name) {
		JCheckBox chb = new JCheckBox();
		chb.setName(name);
		chb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean valInd = ((JCheckBox) e.getSource()).isSelected();
				ExtractorParam selectedExtractorParam = 
					((PropertyTableModel)getModel()).getSelectedExtractorParam();
				if (selectedExtractorParam != null) {
					ExtractorParamUtils.setValue(selectedExtractorParam,
							((JCheckBox) e.getSource()).getName(), valInd);
					log.debug(selectedExtractorParam.toString());
				}
			}
		});
		return chb;
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if(column == 0){
			//align right names
			DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)super.getCellRenderer(row, column);
			renderer.setHorizontalAlignment(JLabel.RIGHT);
			return renderer;
			
		}else if(column == 1){
			return new PropertyTableCellRenderer();
		}
		return super.getCellRenderer(row, column);
	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (editors != null){
//			String name = getModel().getValueAt(row, 0).toString();
			String name = ((PropertyTableModel)getModel()).getPropertyRowName(row);
			TableCellEditor tmpEditor = editors.get(name);
			if (tmpEditor != null){
				return tmpEditor;
			}
		}
		return super.getCellEditor(row, column);
	}
	
	public class PropertyTableCellRenderer extends DefaultTableCellRenderer{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if(value instanceof String){
				JLabel label = new JLabel((String)value);
				Font f = label.getFont();
				// bold
				label.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
				return label;
			}else if(value instanceof Boolean){
				String name = ((PropertyTableModel)getModel()).getPropertyRowName(row);
				JCheckBox check = (JCheckBox)editors.get(name).getComponent();
				check.setSelected(Boolean.TRUE.equals(value));
				return check;
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
		
	}

	
}

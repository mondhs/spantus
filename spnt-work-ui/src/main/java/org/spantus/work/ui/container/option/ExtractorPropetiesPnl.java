package org.spantus.work.ui.container.option;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.work.ui.i18n.I18nFactory;

public class ExtractorPropetiesPnl extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PropertyTableModel model;
	private PropertiesTable table;

	public ExtractorPropetiesPnl() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(null,
				getMessage("properties"), TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION));

		model = new PropertyTableModel();
		table = new PropertiesTable(model);
		add(table, BorderLayout.CENTER);
	}

	protected String getMessage(String key) {
		return I18nFactory.createI18n().getMessage(key);
	}

	public ExtractorParam getSelectedExtractorParam() {
		return model.getSelectedExtractorParam();
	}

	public void setSelectedExtractorParam(ExtractorParam selectedExtractorParam) {
		model.setSelectedExtractorParam(selectedExtractorParam);
		table.repaint();
	}
	
}

package org.spantus.work.ui.container.option;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.work.reader.SupportableReaderEnum;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

public class ExtractorsOptionPanel extends AbstractOptionPanel {

	private static final long serialVersionUID = 1L;
	private JPanel commonPnl = null;
	private JPanel mpeg7Pnl = null;
	Map<SupportableReaderEnum, List<JCheckBox>> readersChoises = null;
	SpantusWorkInfo config;

	/**
	 * This is the default constructor
	 */
	public ExtractorsOptionPanel() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(getCommonPnl(), null);
		this.add(getMpeg7Pnl(), null);
	}
	public void reload() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCommonPnl() {
		if (commonPnl == null) {
			LayoutManager gridLayout = new GridLayout(0, 3);
			commonPnl = new JPanel();
			commonPnl.setName("Comon Panel");
			commonPnl.setLayout(gridLayout);
			commonPnl.setBorder(BorderFactory.createTitledBorder(null,
					getMessage(SupportableReaderEnum.spantus.name()),
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION));
			commonPnl.add(createSelectAllCombobox(SupportableReaderEnum.spantus));
			for (JCheckBox cmb : getReadersChoises().get(
					SupportableReaderEnum.spantus)) {
				commonPnl.add(cmb);
			}
		}
		return commonPnl;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMpeg7Pnl() {
		if (mpeg7Pnl == null) {
			GridLayout gridLayout1 = new GridLayout(0, 3);
			mpeg7Pnl = new JPanel();
			mpeg7Pnl.setLayout(gridLayout1);
			mpeg7Pnl.setBorder(BorderFactory.createTitledBorder(null, 
					getMessage(SupportableReaderEnum.mpeg7.name()),
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION));
			mpeg7Pnl.add(createSelectAllCombobox(SupportableReaderEnum.mpeg7));
			for (JCheckBox cmb : getReadersChoises().get(
					SupportableReaderEnum.mpeg7)) {
				mpeg7Pnl.add(cmb);
			}
		}
		return mpeg7Pnl;
	}

	private Map<SupportableReaderEnum, List<JCheckBox>> getReadersChoises() {
		if (readersChoises == null) {
			readersChoises = new HashMap<SupportableReaderEnum, List<JCheckBox>>();
			List<JCheckBox> extractors = new ArrayList<JCheckBox>();
			for (ExtractorEnum extractor : ExtractorEnum.values()) {
				JCheckBox cb = new JCheckBox();
				cb.setText(I18nFactory.createI18n()
						.getMessage(extractor.name()));
				cb.setName(extractor.name());
				extractors.add(cb);
			}
			readersChoises.put(SupportableReaderEnum.spantus, extractors);
			extractors = new ArrayList<JCheckBox>();
			for (Mpeg7ExtractorEnum extractor : Mpeg7ExtractorEnum.values()) {
				JCheckBox cb = new JCheckBox();
				cb.setText(I18nFactory.createI18n()
						.getMessage(extractor.name()));
				cb.setName(extractor.name());
				extractors.add(cb);
			}
			readersChoises.put(SupportableReaderEnum.mpeg7, extractors);
		}
		return readersChoises;
	}

	private JCheckBox createSelectAllCombobox(SupportableReaderEnum name){
		JCheckBox selectAllCmb = new JCheckBox();
		selectAllCmb.setText(I18nFactory.createI18n()
				.getMessage("selectAll"));
		selectAllCmb.setName(name.name());
		selectAllCmb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JCheckBox thisCmb = (JCheckBox)e.getSource();
				boolean selected = thisCmb.isSelected();
				for (JCheckBox cmb : getReadersChoises().get(
						SupportableReaderEnum.valueOf(
								thisCmb.getName()
								))) {
					cmb.setSelected(selected);
				}
			}
		});
		return selectAllCmb;
	}
	
	public SpantusWorkInfo getConfig() {
		return config;
	}

	public void setInfo(SpantusWorkInfo config) {
		this.config = config;
		selectCheckboxes(config.getProject().getFeatureReader().getExtractors());
	}
	/**
	 * 
	 * @param extractors
	 */
	public void selectCheckboxes(Set<String> extractors){
		for (Entry<SupportableReaderEnum, List<JCheckBox>> entryExtractors : getReadersChoises().entrySet()) {
			for (JCheckBox cbx : entryExtractors.getValue()) {
				cbx.setSelected(false);
			}
		}
		
		for (String extr : extractors) {
			String[] extractor = extr.split(":");
			SupportableReaderEnum reader = SupportableReaderEnum.valueOf(extractor[0]);
			for (JCheckBox cbx: getReadersChoises().get(reader)) {
				if(extractor[1].equals(cbx.getName())){
					cbx.setSelected(true);
					break;
				}
			}
		}
	}
	/**
	 * 
	 */
	
	public void save() {
		getConfig().getProject().getFeatureReader().getExtractors().clear();
		for (Entry<SupportableReaderEnum, List<JCheckBox>> entryExtractors : getReadersChoises().entrySet()) {
			for (JCheckBox cbx : entryExtractors.getValue()) {
				if(cbx.isSelected()){
					getConfig().getProject().getFeatureReader().getExtractors().add(entryExtractors.getKey()+":"+ cbx.getName());
				}
			}
		}
		
	}

}

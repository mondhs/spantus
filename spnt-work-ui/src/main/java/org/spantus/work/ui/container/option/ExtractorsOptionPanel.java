package org.spantus.work.ui.container.option;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorTypeEnum;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.ui.ModelEntry;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.utils.ExtractorParamUtils.commonParam;
import org.spantus.work.reader.SupportableReaderEnum;
import org.spantus.work.ui.container.ShuttleSelectionPanel;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

public class ExtractorsOptionPanel extends AbstractOptionPanel {

	private static final long serialVersionUID = 1L;
//	private JPanel commonPnl = null;
//	private JPanel mpeg7Pnl = null;
//	private JPanel selectionPnl;
	private ShuttleSelectionPanel shuttle;
	private JPanel propetiesPnl;
	
	JCheckBox smoothedCmb = new JCheckBox();
	JCheckBox meanCmb = new JCheckBox();
	
//	public static final String MEAN_KEY = "mean";
//	public static final String SMOOTHED_KEY = "smoothed";
	
	ExtractorParam selectedExtractorParam;
	
//	Map<SupportableReaderEnum, List<JCheckBox>> readersChoises = null;
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
//		this.setSize(300, 200);
//		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setLayout(new BorderLayout());
		this.add(getSelectionPnl(), BorderLayout.NORTH);
		this.add(getPropertiesPnl(),BorderLayout.SOUTH);
		getPropertiesPnl().setVisible(false);
//		this.add(getCommonPnl(), null);
//		this.add(getMpeg7Pnl(), null);
	}
	public void reload() {
		// TODO Auto-generated method stub
		
	}

	
	private ShuttleSelectionPanel getShuttle() {
		if(shuttle == null){
			shuttle = new ShuttleSelectionPanel();
			shuttle.addListSelectionListener(new SelectionListSelectionListener());
			shuttle.setBorder(BorderFactory.createTitledBorder(null, 
					getMessage("feature"),
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION));
			
			for (ExtractorEnum extractor : ExtractorEnum.values()) {
				ModelEntry entry = new ModelEntry(
					I18nFactory.createI18n()
					.getMessage(extractor.name()),
					SupportableReaderEnum.spantus.name()+":"
					+ extractor.name()
					);
				shuttle.addSourceElement(entry);
			}
			for (Mpeg7ExtractorEnum extractor : Mpeg7ExtractorEnum.values()) {
				ModelEntry entry = new ModelEntry(
						I18nFactory.createI18n()
						.getMessage(extractor.name()),
						SupportableReaderEnum.mpeg7.name()+":"
						+ extractor.name()
						);
				shuttle.addSourceElement(entry);	
			}
		}
		return shuttle;
	}
	
	private JPanel getSelectionPnl() {
//		if (selectionPnl == null) {
//			selectionPnl = new JPanel();
//			selectionPnl.add(getShuttle(),BorderLayout.CENTER);
//		}
		return getShuttle();
	}
	
	private JPanel getPropertiesPnl() {
		if(propetiesPnl == null){
			propetiesPnl = new JPanel(new GridLayout(0, 3));
			propetiesPnl.setBorder(BorderFactory.createTitledBorder(null, 
					getMessage("properties"),
					TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION));
			
			
			smoothedCmb.setName(commonParam.smoothed.name());
			smoothedCmb.setText(getMessage(smoothedCmb.getName()));
			meanCmb.setName(commonParam.mean.name());
			meanCmb.setText(getMessage(meanCmb.getName()));
			meanCmb.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean meanInd = ((JCheckBox)e.getSource()).isSelected();	
					if(selectedExtractorParam != null){
						ExtractorParamUtils.setValue(selectedExtractorParam,
						 meanCmb.getName(), meanInd);
					}
				}
			});
			smoothedCmb.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean meanInd = ((JCheckBox)e.getSource()).isSelected();	
					if(selectedExtractorParam != null){
						ExtractorParamUtils.setValue(selectedExtractorParam,
						 smoothedCmb.getName(), meanInd);
					}
				}
			});
			propetiesPnl.add(smoothedCmb);
			propetiesPnl.add(meanCmb);
			
		}
		return propetiesPnl;
	}
	
//	/**
//	 * This method initializes jPanel
//	 * 
//	 * @return javax.swing.JPanel
//	 */
//	private JPanel getCommonPnl() {
//		if (commonPnl == null) {
//			LayoutManager gridLayout = new GridLayout(0, 3);
//			commonPnl = new JPanel();
//			commonPnl.setName("Comon Panel");
//			commonPnl.setLayout(gridLayout);
//			commonPnl.setBorder(BorderFactory.createTitledBorder(null,
//					getMessage(SupportableReaderEnum.spantus.name()),
//					TitledBorder.DEFAULT_JUSTIFICATION,
//					TitledBorder.DEFAULT_POSITION));
//			commonPnl.add(createSelectAllCombobox(SupportableReaderEnum.spantus));
//			for (JCheckBox cmb : getReadersChoises().get(
//					SupportableReaderEnum.spantus)) {
//				commonPnl.add(cmb);
//			}
//		}
//		return commonPnl;
//	}
//
//	/**
//	 * This method initializes jPanel1
//	 * 
//	 * @return javax.swing.JPanel
//	 */
//	private JPanel getMpeg7Pnl() {
//		if (mpeg7Pnl == null) {
//			GridLayout gridLayout1 = new GridLayout(0, 3);
//			mpeg7Pnl = new JPanel();
//			mpeg7Pnl.setLayout(gridLayout1);
//			mpeg7Pnl.setBorder(BorderFactory.createTitledBorder(null, 
//					getMessage(SupportableReaderEnum.mpeg7.name()),
//					TitledBorder.DEFAULT_JUSTIFICATION,
//					TitledBorder.DEFAULT_POSITION));
//			mpeg7Pnl.add(createSelectAllCombobox(SupportableReaderEnum.mpeg7));
//			for (JCheckBox cmb : getReadersChoises().get(
//					SupportableReaderEnum.mpeg7)) {
//				mpeg7Pnl.add(cmb);
//			}
//		}
//		return mpeg7Pnl;
//	}

//	private Map<SupportableReaderEnum, List<JCheckBox>> getReadersChoises() {
//		if (readersChoises == null) {
//			readersChoises = new HashMap<SupportableReaderEnum, List<JCheckBox>>();
//			List<JCheckBox> extractors = new ArrayList<JCheckBox>();
//			for (ExtractorEnum extractor : ExtractorEnum.values()) {
//				JCheckBox cb = new JCheckBox();
//				cb.setText(I18nFactory.createI18n()
//						.getMessage(extractor.name()));
//				cb.setName(extractor.name());
//				extractors.add(cb);
//			}
//			readersChoises.put(SupportableReaderEnum.spantus, extractors);
//			extractors = new ArrayList<JCheckBox>();
//			for (Mpeg7ExtractorEnum extractor : Mpeg7ExtractorEnum.values()) {
//				JCheckBox cb = new JCheckBox();
//				cb.setText(I18nFactory.createI18n()
//						.getMessage(extractor.name()));
//				cb.setName(extractor.name());
//				extractors.add(cb);
//			}
//			readersChoises.put(SupportableReaderEnum.mpeg7, extractors);
//		}
//		return readersChoises;
//	}

//	private JCheckBox createSelectAllCombobox(SupportableReaderEnum name){
//		JCheckBox selectAllCmb = new JCheckBox();
//		selectAllCmb.setText(I18nFactory.createI18n()
//				.getMessage("selectAll"));
//		selectAllCmb.setName(name.name());
//		selectAllCmb.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				JCheckBox thisCmb = (JCheckBox)e.getSource();
//				boolean selected = thisCmb.isSelected();
//				for (JCheckBox cmb : getReadersChoises().get(
//						SupportableReaderEnum.valueOf(
//								thisCmb.getName()
//								))) {
//					cmb.setSelected(selected);
//				}
//			}
//		});
//		return selectAllCmb;
//	}
	
	public SpantusWorkInfo getConfig() {
		return config;
	}

	public void setInfo(SpantusWorkInfo config) {
		this.config = config;
//		selectCheckboxes(config.getProject().getFeatureReader().getExtractors());
		updateShuttle(config.getProject().getFeatureReader().getExtractors());
	}
	
	/**
	 * 
	 * @param extractors
	 */
	public void updateShuttle(Set<String> extractors){
		getShuttle().addSourceElements(getShuttle().getDestListModel());
		getShuttle().clearDestinationListModel();
		for (ModelEntry modelEntry : getShuttle().getSourceListModel().iteratable()) {
			if(extractors.contains(modelEntry.getValue())){
				getShuttle().addDestinationElement(modelEntry);
			}
			modelEntry.setOrder(0);	
		}
		int order = 0;
		for (ModelEntry modelEntry : getShuttle().getDestListModel().iteratable()) {
			if(getShuttle().getSourceListModel().removeElement(modelEntry)){
				modelEntry.getOrder();
			}
			modelEntry.setOrder(order++);
		}
	}
//	/**
//	 * 
//	 * @param extractors
//	 */
//	public void selectCheckboxes(Set<String> extractors){
//		for (Entry<SupportableReaderEnum, List<JCheckBox>> entryExtractors : getReadersChoises().entrySet()) {
//			for (JCheckBox cbx : entryExtractors.getValue()) {
//				cbx.setSelected(false);
//			}
//		}
//		
//		for (String extr : extractors) {
//			String[] extractor = extr.split(":");
//			SupportableReaderEnum reader = SupportableReaderEnum.valueOf(extractor[0]);
//			for (JCheckBox cbx: getReadersChoises().get(reader)) {
//				if(extractor[1].equals(cbx.getName())){
//					cbx.setSelected(true);
//					break;
//				}
//			}
//		}
//	}
	/**
	 * 
	 */
	
	public void save() {
		getConfig().getProject().getFeatureReader().getExtractors().clear();
		for (ModelEntry modelEntry : getShuttle().getDestListModel().iteratable()) {
			getConfig().getProject().getFeatureReader().getExtractors().add(modelEntry.getValue().toString());
		}
//		for (Entry<SupportableReaderEnum, List<JCheckBox>> entryExtractors : getReadersChoises().entrySet()) {
//			for (JCheckBox cbx : entryExtractors.getValue()) {
//				if(cbx.isSelected()){
//					getConfig().getProject().getFeatureReader().getExtractors().add(entryExtractors.getKey()+":"+ cbx.getName());
//				}
//			}
//		}
		
	}
	
	public class SelectionListSelectionListener implements
			ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			boolean propertiesInd = false;
			Object obj = getShuttle().getDestList().getSelectedValue();
			if (obj instanceof ModelEntry) {
				String extractorName = ((ModelEntry) obj).getValue().toString();
				String[] extractor = extractorName.split(":");
				if (extractor[0].startsWith(SupportableReaderEnum.spantus
						.name())) {

					ExtractorEnum exEnum = ExtractorEnum.valueOf(extractor[1]);
					propertiesInd = ExtractorTypeEnum.SequenceOfScalar
							.equals(exEnum.getType());
					if(propertiesInd){
						selectedExtractorParam = getConfig().getProject().getFeatureReader().getParameters().get(extractorName);
						boolean meanInd = ExtractorParamUtils.getBoolean(selectedExtractorParam,
							 commonParam.mean.name(),false);
						boolean smoothedInd = ExtractorParamUtils.getBoolean(selectedExtractorParam, 
							commonParam.smoothed.name(),false);
						meanCmb.setSelected(meanInd);
						smoothedCmb.setSelected(smoothedInd);
					}
				}
			}
			getPropertiesPnl().setVisible(propertiesInd);
			if(!propertiesInd){
				selectedExtractorParam = null;
			}
		}
	}
}

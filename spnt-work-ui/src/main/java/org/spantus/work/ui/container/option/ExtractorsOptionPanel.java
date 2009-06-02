package org.spantus.work.ui.container.option;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.spantus.core.extractor.ExtractorParam;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorModifiersEnum;
import org.spantus.extractor.impl.ExtractorTypeEnum;
import org.spantus.logger.Logger;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.ui.ModelEntry;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.reader.SupportableReaderEnum;
import org.spantus.work.ui.container.ShuttleSelectionPanel;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

public class ExtractorsOptionPanel extends AbstractOptionPanel {

	private static final long serialVersionUID = 1L;
	private ShuttleSelectionPanel shuttle;
	private JPanel propetiesPnl;
	
	private JLabel extractorLabel = new JLabel();
	private Map<String, JCheckBox> modifiers = new HashMap<String, JCheckBox>();
//	private JCheckBox smoothedCmb ;
//	private JCheckBox meanCmb ;
//	private JCheckBox deltaCmb;
//	private JCheckBox stdevCmb;
	private Logger log = Logger.getLogger(ExtractorsOptionPanel.class);
	
	
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
		this.add(getSelectionPnl(), BorderLayout.CENTER);
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
			
			extractorLabel.setText(selectedExtractorParam==null?"<none>":selectedExtractorParam.getClassName());
			propetiesPnl.add(extractorLabel);
			for (ExtractorModifiersEnum modifier : ExtractorModifiersEnum.values()) {
				JCheckBox chb = createChb(modifier.name());
				modifiers.put(modifier.name(), chb);
				propetiesPnl.add(chb);
			}
			
		}
		return propetiesPnl;
	}
	protected JCheckBox createChb(String name){
		JCheckBox chb = new JCheckBox();
		chb.setName(name);
		chb.setText(getMessage(chb.getName()));
		chb.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				boolean valInd = ((JCheckBox)e.getSource()).isSelected();	
				if(selectedExtractorParam != null){
					ExtractorParamUtils.setValue(selectedExtractorParam,
					((JCheckBox)e.getSource()).getName(), valInd);
					log.debug(selectedExtractorParam.toString());
				}
			}
		});
		return chb;
	}
	

	
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
	/**
	 * 
	 */
	
	public void save() {
		getConfig().getProject().getFeatureReader().getExtractors().clear();
		for (ModelEntry modelEntry : getShuttle().getDestListModel().iteratable()) {
			getConfig().getProject().getFeatureReader().getExtractors().add(modelEntry.getValue().toString());
		}
	}
	
	public class SelectionListSelectionListener implements
			ListSelectionListener {
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
						selectedExtractorParam = ExtractorParamUtils.getSafeParam(
								getConfig().getProject().getFeatureReader().getParameters(),
								extractorName);
						selectedExtractorParam.setClassName(extractorName);
						extractorLabel.setText(extractorName);
						for (ExtractorModifiersEnum modifier : ExtractorModifiersEnum.values()) {
							boolean valInd = ExtractorParamUtils.getBoolean(selectedExtractorParam,
									modifier.name(),false);
							modifiers.get(modifier.name()).setSelected(valInd);
						}
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

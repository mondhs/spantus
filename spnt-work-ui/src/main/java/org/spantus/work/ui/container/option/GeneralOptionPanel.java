package org.spantus.work.ui.container.option;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.spantus.chart.bean.VectorSeriesColorEnum;
import org.spantus.logger.Logger;
import org.spantus.ui.MapComboBoxModel;
import org.spantus.ui.ModelEntry;
import org.spantus.work.ui.i18n.I18n;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.layout.FormLayout;

public class GeneralOptionPanel extends AbstractOptionPanel {
	
	Logger log = Logger.getLogger(GeneralOptionPanel.class);
	private Map<generalLabels, LabelControlEntry> jComponents;

	private MapComboBoxModel laf;
	private MapComboBoxModel locales;

	public GeneralOptionPanel() {
		super();
	}
	
	enum generalLabels {
		advancedMode, locale, lookAndFeel, chartGrid, vectorChartColorType, popupNotification,
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void save() {
		for (Entry<generalLabels, LabelControlEntry> fieldEntry : getJComponents().entrySet()) {
			JComponent cmp = fieldEntry.getValue().getControl();

			switch (fieldEntry.getKey()) {
			case locale:
				Object locale = getLocaleModel().getSelectedObject();
				getInfo().setLocale((Locale) locale);
				break;
			case lookAndFeel:
				getInfo().getEnv().setLaf((String)getLAFModel().getSelectedObject());
				break;
			case advancedMode:
				getInfo().getEnv().setAdvancedMode(((JCheckBox)cmp).isSelected());
				break;
				
			case chartGrid:
				getInfo().getEnv().setGrid(((JCheckBox)cmp).isSelected());
				break;
			case vectorChartColorType:
				getInfo().getEnv().setVectorChartColorTypes((String)getChartColorTypeModel().getSelectedObject());
				break;
			case popupNotification:
				getInfo().getEnv().setPopupNotifications(((JCheckBox)cmp).isSelected());
				break;
			default:
				throw new RuntimeException("Not impl: " + fieldEntry.getKey());
			}
		}
	}

	

	public void initialize() {
		FormLayout layout = new FormLayout(
			    "right:max(40dlu;p), 4dlu, 80dlu, 7dlu, "
//			    +"right:max(40dlu;p), 4dlu, 80dlu"
			    ,
			    "");

		JPanel panelContainer = new JPanel();
		if(log.isDebugMode()){
			panelContainer = new FormDebugPanel();
		}
		DefaultFormBuilder builder = new DefaultFormBuilder(layout, panelContainer);
		builder.setDefaultDialogBorder();


		for (generalLabels label : generalLabels.values()) {
			LabelControlEntry entry = getJComponents().get(label);
			builder.append(entry.getLabel(), entry.getControl());
		}
		add(builder.getPanel());
		reload();
	}

	public void reload() {
		onShowEvent();
	}
	
	//Override
	public void onShowEvent() {
		for (Entry<generalLabels, LabelControlEntry> fieldEntry : getJComponents()
				.entrySet()) {
			
			JComponent cmp = fieldEntry.getValue().getControl();
			
			switch (fieldEntry.getKey()) {
			case locale:
				getLocaleModel().setSelectedObject(
						getInfo().getLocale()
						);
				break;
			case lookAndFeel:
				getLAFModel().setSelectedObject(getInfo().getEnv().getLaf());
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case chartGrid:
				((JCheckBox)cmp).setSelected(Boolean.TRUE.equals(getInfo().getEnv().getGrid()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case advancedMode:
				((JCheckBox)cmp).setSelected(Boolean.TRUE.equals(getInfo().getEnv().getAdvancedMode()));
				break;
			case vectorChartColorType:
				getChartColorTypeModel().setSelectedObject(getInfo().getEnv().getVectorChartColorTypes());
				fieldEntry.getValue().setVisible(getInfo().getEnv().getAdvancedMode());
				break;
			case popupNotification:
				((JCheckBox)cmp).setSelected(Boolean.TRUE.equals(getInfo().getEnv().getPopupNotifications()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			default:
				throw new RuntimeException("Not impl: " + fieldEntry.getKey());
			}
		}

	}

	public boolean  isAdvanced(){
		return Boolean.TRUE.equals(getInfo().getEnv().getAdvancedMode());
	}
	
	public void addFieldList(JComponent component, String labelName ){
		generalLabels labelNameEnum = generalLabels.valueOf(labelName);
		component.setName(labelName);
		JLabel label = new JLabel(getMessage(labelName), JLabel.TRAILING);
		label.setLabelFor(component);
		jComponents.put(labelNameEnum, 
				new LabelControlEntry(label, component));
	}
	
	private Map<generalLabels, LabelControlEntry> getJComponents() {
		if (jComponents == null) {
			jComponents = new LinkedHashMap<generalLabels, LabelControlEntry>();

			JCheckBox advancedModeOn = new JCheckBox();
			advancedModeOn.setSelected(Boolean.TRUE
					.equals(getInfo().getEnv().getAdvancedMode()));
			advancedModeOn.setToolTipText(getMessage(generalLabels.advancedMode.name()+"_tooltip"));
			advancedModeOn.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent e) {
					boolean valInd = ((JCheckBox)e.getSource()).isSelected();	
					getInfo().getEnv().setAdvancedMode(valInd);
					onShowEvent();
				}
			});
			addFieldList(advancedModeOn, generalLabels.advancedMode.name());
			
			JComboBox localeCmb = new JComboBox();
			localeCmb.setModel(getLocaleModel());
			localeCmb.setSelectedItem(getLocaleModel().getLabel(getInfo().getLocale()));
			addFieldList(localeCmb, generalLabels.locale.name());
			
			
			
			JComboBox lookAndFeel = new JComboBox(getLAFModel());	
			lookAndFeel.setSelectedItem(getLAFModel().getLabel(getInfo().getEnv().getLaf()));
			lookAndFeel.getSelectedItem();
			addFieldList(lookAndFeel, generalLabels.lookAndFeel.name());
			
			JCheckBox gridOn = new JCheckBox();
			gridOn.setSelected(Boolean.TRUE
					.equals(getInfo().getEnv().getGrid()));
			addFieldList(gridOn, generalLabels.chartGrid.name());
			
			JComboBox vectorCharColorTypeCmb = new JComboBox(getChartColorTypeModel());	
			vectorCharColorTypeCmb.setSelectedItem(getChartColorTypeModel().getLabel(getInfo().getEnv().getVectorChartColorTypes()));
			addFieldList(vectorCharColorTypeCmb, generalLabels.vectorChartColorType.name());
			
			JCheckBox popupNotificationOn = new JCheckBox();
			popupNotificationOn.setSelected(Boolean.TRUE
					.equals(getInfo().getEnv().getPopupNotifications()));
			popupNotificationOn.setToolTipText(getMessage(generalLabels.popupNotification.name()+"_tooltip"));
			addFieldList(popupNotificationOn, generalLabels.popupNotification.name());
			
		}
		return jComponents;
	}

	/**
	 * 
	 */
	protected MapComboBoxModel getLAFModel() {
		if (laf == null) {
			laf = new MapComboBoxModel();
			UIManager.LookAndFeelInfo looks[] = UIManager
					.getInstalledLookAndFeels();
			for (LookAndFeelInfo lookAndFeelInfo : looks) {
				laf.addElement(new ModelEntry(lookAndFeelInfo.getName(),
						lookAndFeelInfo.getClassName()));
			}
		}
		return laf;
	}
	private MapComboBoxModel chartColorTypes;

	protected MapComboBoxModel getChartColorTypeModel() {
		if(chartColorTypes == null){
			chartColorTypes = new MapComboBoxModel();	
			for (VectorSeriesColorEnum colorType : VectorSeriesColorEnum.values()) {
				String label = getMessage("colorType_" + colorType.name());
				chartColorTypes.addElement(new ModelEntry(label, colorType.name()));
			}
		}
		return chartColorTypes;
		 
	}
	
	protected MapComboBoxModel getLocaleModel() {
		if (locales == null) {
			locales = new MapComboBoxModel();
			for (Locale locale : I18n.LOCALES) {
				String label = getMessage("locale_" + locale.toString());
				locales.addElement(new ModelEntry(label, locale));
			}
		}
		return locales;
	}

}

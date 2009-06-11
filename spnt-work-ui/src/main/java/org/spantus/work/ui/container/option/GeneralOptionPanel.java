package org.spantus.work.ui.container.option;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.spantus.chart.bean.VectorSeriesColorEnum;
import org.spantus.ui.MapComboBoxModel;
import org.spantus.ui.ModelEntry;
import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18n;

public class GeneralOptionPanel extends AbstractOptionPanel {

	private SpantusWorkInfo info;

	private Map<generalLabels, JComponent> jComponents;

	private MapComboBoxModel laf;
	private MapComboBoxModel locales;

	enum generalLabels {
		locale, lookAndFeel, chartGrid, vectorChartColorType, popupNotification, autoSegmentation
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void save() {
		for (Entry<generalLabels, JComponent> field : getJComponents().entrySet()) {

			switch (field.getKey()) {
			case locale:
				Object locale = getLocaleModel().getSelectedObject();
				getInfo().setLocale((Locale) locale);
				break;
			case lookAndFeel:
				getInfo().getEnv().setLaf((String)getLAFModel().getSelectedObject());
				break;
			case chartGrid:
				getInfo().getEnv().setGrid(((JCheckBox)field.getValue()).isSelected());
				break;
			case vectorChartColorType:
				getInfo().getEnv().setVectorChartColorTypes((String)getChartColorTypeModel().getSelectedObject());
				break;
			case popupNotification:
				getInfo().getEnv().setPopupNotifications(((JCheckBox)field.getValue()).isSelected());
				break;
			case autoSegmentation:
				getInfo().getEnv().setAutoSegmentation(((JCheckBox)field.getValue()).isSelected());
				break;
			default:
				throw new RuntimeException("Not impl: " + field.getKey());
			}
		}
	}

	public void setInfo(SpantusWorkInfo info) {
		this.info = info;
	}

	public SpantusWorkInfo getInfo() {
		return info;
	}

	public void initialize() {
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		for (generalLabels label : generalLabels.values()) {
			JLabel l = new JLabel(getMessage(label.name()), JLabel.TRAILING);
			this.add(l);
			JComponent edit = getJComponents().get(label);
			l.setLabelFor(edit);
			this.add(edit);
		}
		

		SpantusWorkSwingUtils.makeCompactGrid(this,
				generalLabels.values().length, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad
		reload();
	}

	public void reload() {
		for (Entry<generalLabels, JComponent> field : getJComponents()
				.entrySet()) {
			switch (field.getKey()) {
			case locale:
				getLocaleModel().setSelectedObject(
						getInfo().getLocale()
						);
				break;
			case lookAndFeel:
				getLAFModel().setSelectedObject(getInfo().getEnv().getLaf());
				break;
			case chartGrid:
				((JCheckBox)field.getValue()).setSelected(Boolean.TRUE.equals(getInfo().getEnv().getGrid()));
				break;
			case vectorChartColorType:
				getChartColorTypeModel().setSelectedObject(getInfo().getEnv().getVectorChartColorTypes());
				break;
			case popupNotification:
				((JCheckBox)field.getValue()).setSelected(Boolean.TRUE.equals(getInfo().getEnv().getPopupNotifications()));
				break;
			case autoSegmentation:
				((JCheckBox)field.getValue()).setSelected(Boolean.TRUE.equals(getInfo().getEnv().getAutoSegmentation()));
				break;
			default:
				throw new RuntimeException("Not impl: " + field.getKey());
			}
		}

	}

	private Map<generalLabels, JComponent> getJComponents() {
		if (jComponents == null) {
			jComponents = new HashMap<generalLabels, JComponent>();

			JComboBox input = new JComboBox();
			input.setModel(getLocaleModel());
			input.setSelectedItem(getLocaleModel().getLabel(getInfo().getLocale()));
			input.setName(generalLabels.locale.name());
			jComponents.put(generalLabels.locale, input);

			JComboBox lookAndFeel = new JComboBox(getLAFModel());	
			lookAndFeel.setSelectedItem(getLAFModel().getLabel(getInfo().getEnv().getLaf()));
			lookAndFeel.getSelectedItem();
			lookAndFeel.setName(generalLabels.lookAndFeel.name());
			jComponents.put(generalLabels.lookAndFeel, lookAndFeel);
			
			JCheckBox gridOn = new JCheckBox();
			gridOn.setSelected(Boolean.TRUE
					.equals(getInfo().getEnv().getGrid()));
			jComponents.put(generalLabels.chartGrid, gridOn);
			
			JComboBox vectorCharColorTypeCmb = new JComboBox(getChartColorTypeModel());	
			vectorCharColorTypeCmb.setSelectedItem(getChartColorTypeModel().getLabel(getInfo().getEnv().getVectorChartColorTypes()));
			vectorCharColorTypeCmb.getSelectedItem();
			vectorCharColorTypeCmb.setName(generalLabels.vectorChartColorType.name());
			jComponents.put(generalLabels.vectorChartColorType, vectorCharColorTypeCmb);
			
			JCheckBox popupNotificationOn = new JCheckBox();
			popupNotificationOn.setSelected(Boolean.TRUE
					.equals(getInfo().getEnv().getPopupNotifications()));
			jComponents.put(generalLabels.popupNotification, popupNotificationOn);
			
			JCheckBox autoSegmentationChb = new JCheckBox();
			popupNotificationOn.setSelected(Boolean.TRUE
					.equals(getInfo().getEnv().getAutoSegmentation()));
			jComponents.put(generalLabels.autoSegmentation, autoSegmentationChb);



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
	MapComboBoxModel chartColorTypes;

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

package org.spantus.work.ui.container.option;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.spantus.ui.MapComboBoxModel;
import org.spantus.ui.ModelEntry;
import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18n;
import org.spantus.work.ui.i18n.I18nFactory;

public class GeneralOptionPanel extends AbstractOptionPanel {

	private SpantusWorkInfo info;

	private Map<generalLabels, JComponent> jComponents;

	private MapComboBoxModel laf;
	private MapComboBoxModel locales;

	enum generalLabels {
		locale, lookAndFeel, chartGrid
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
				getInfo().getEnv().setLaf((String)getLAFModel().getSelectedItem());
				LookAndFeelInfo laf = (LookAndFeelInfo)getLAFModel().getSelectedObject();
				try {
						UIManager.setLookAndFeel(laf.getClassName());
						Frame frame = (Frame)SwingUtilities.getAncestorOfClass(Frame.class, this);
						 SwingUtilities.updateComponentTreeUI(frame);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedLookAndFeelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
               
				break;
			case chartGrid:
				getInfo().getEnv().setGrid(((JCheckBox)field.getValue()).isSelected());
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
		for (Entry<generalLabels, JComponent> comp : getJComponents()
				.entrySet()) {
			switch (comp.getKey()) {
			case locale:
				((JComboBox)comp.getValue()).setSelectedItem(
						getMessage("locale_" + getInfo().getLocale().toString())
						);
				break;
			case lookAndFeel:
				getLAFModel().setSelectedItem(getInfo().getEnv().getLaf());
				break;
			case chartGrid:
				((JCheckBox)comp.getValue()).setSelected(Boolean.TRUE.equals(getInfo().getEnv().getGrid()));
				break;
			default:
				throw new RuntimeException("Not impl: " + comp.getKey());
			}
		}

	}

	private Map<generalLabels, JComponent> getJComponents() {
		if (jComponents == null) {
			jComponents = new HashMap<generalLabels, JComponent>();

			JComboBox input = new JComboBox();
			input.setModel(getLocaleModel());
			input.setName(generalLabels.locale.name());
			jComponents.put(generalLabels.locale, input);

			JCheckBox gridOn = new JCheckBox();
			gridOn.setSelected(Boolean.TRUE
					.equals(getInfo().getEnv().getGrid()));
			jComponents.put(generalLabels.chartGrid, gridOn);

			JComboBox lookAndFeel = new JComboBox(getLAFModel());
			input.setName(generalLabels.lookAndFeel.name());
			jComponents.put(generalLabels.lookAndFeel, lookAndFeel);

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
						lookAndFeelInfo));
			}
		}
		return laf;
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

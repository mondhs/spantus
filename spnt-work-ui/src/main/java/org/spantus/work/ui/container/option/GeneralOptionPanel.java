package org.spantus.work.ui.container.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18n;

public class GeneralOptionPanel extends AbstractOptionPanel {

	SpantusWorkInfo info;
	
	List<JComponent> jComponents;
	
	enum generalLabels{locale} 

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void save() {
		for (JComponent field : getJComponents()) {
			generalLabels lbl = generalLabels.valueOf(field.getName());


			switch (lbl) {
			case locale:
				Object locale = ((JComboBox)field).getSelectedItem();
				getConfig().setLocale((Locale)locale);
				break;
			default:
				throw new RuntimeException("Not impl: "  + lbl.name());
			}
		}
	}

	public void setConfig(SpantusWorkInfo info) {
		this.info = info; 
	}
	public SpantusWorkInfo getConfig() {
		return info; 
	}

	public void initialize() {
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		for (JComponent edit : getJComponents()) {
			 JLabel l = new JLabel(getMessage(edit.getName()), JLabel.TRAILING);
			 this.add(l);
			 l.setLabelFor(edit);
			 this.add(edit);
		}


		SpantusWorkSwingUtils.makeCompactGrid(this,
				generalLabels.values().length, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		reload();
	}

	public void reload() {
		for (JComponent comp : getJComponents()) {
			generalLabels lbl = generalLabels.valueOf(comp.getName());

			switch (lbl) {
			case locale:
				((JComboBox)comp).setSelectedItem(getConfig().getLocale());
				break;
			default:
				throw new RuntimeException("Not impl: "  + lbl.name());
			}
		}
		
	}	
	private List<JComponent> getJComponents() {
		if (jComponents == null) {
			jComponents = new ArrayList<JComponent>();
			
			JComboBox input = new JComboBox(I18n.LOCALES);
			input.setName(generalLabels.locale.name());
			jComponents.add(input);
			
		}
		return jComponents;
	}


}

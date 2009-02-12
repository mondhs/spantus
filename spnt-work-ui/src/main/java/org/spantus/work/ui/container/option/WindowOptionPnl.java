package org.spantus.work.ui.container.option;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.spantus.logger.Logger;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;

public class WindowOptionPnl extends AbstractOptionPanel implements ReloadableComponent{
	
	enum optionsLabels{bufferSize, frameSize, windowSize, windowOverlap} 

	SpantusWorkInfo config;
	
	Logger log = Logger.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	private List<JFormattedTextField> jTextFields = null;

	/**
	 * This is the default constructor
	 */
	public WindowOptionPnl() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		this.setSize(300, 200);
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		for (JTextField edit : getJTextFields()) {
			 JLabel l = new JLabel(getMessage(edit.getName()), JLabel.TRAILING);
			 this.add(l);
			 l.setLabelFor(edit);
			 this.add(edit);
		}


		SpantusWorkSwingUtils.makeCompactGrid(this,
				optionsLabels.values().length, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		reload();
	}
	public void reload() {
		for (JFormattedTextField textField : getJTextFields()) {
			optionsLabels lbl = optionsLabels.valueOf(textField.getName());

			WorkUIExtractorConfig workConfig = getConfig().getProject().getFeatureReader().getWorkConfig();

			switch (lbl) {
			case bufferSize:
				textField.setValue(Integer.valueOf((workConfig.getBufferSize())));
				break;
			case frameSize:
				textField.setValue(Integer.valueOf((workConfig.getFrameSize())));
				break;
			case windowSize:
				textField.setValue(Integer.valueOf((workConfig.getWindowSize())));
				break;
			case windowOverlap:
				textField.setValue(Double.valueOf(((double)workConfig.getWindowOverlap()/100)));
				break;
			default:
				throw new RuntimeException("Not impl: "  + lbl.name());
			}
		}
		
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private List<JFormattedTextField> getJTextFields() {
		if (jTextFields == null) {
			jTextFields = new ArrayList<JFormattedTextField>();
			
			JFormattedTextField textField = new JFormattedTextField(
					getI18n().getDecimalFormat());
			textField.setName(optionsLabels.bufferSize.name());
			jTextFields.add(textField);
			
			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			textField.setName(optionsLabels.frameSize.name());
			jTextFields.add(textField);

			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.windowSize.name());
			jTextFields.add(textField);

			textField = new JFormattedTextField(getI18n().getPercentFormat());
			textField.setName(optionsLabels.windowOverlap.name());
			jTextFields.add(textField);

			
		}
		return jTextFields;
	}

	public SpantusWorkInfo getConfig() {
		if(config == null){
			config = new SpantusWorkInfo();
		}
		return config;
	}

	public void setConfig(SpantusWorkInfo config) {
		this.config = config;
	}

	
	public void save() {
		for (JFormattedTextField field : getJTextFields()) {
			optionsLabels lbl = optionsLabels.valueOf(field.getName());

			WorkUIExtractorConfig workConfig = getConfig().getProject().getFeatureReader().getWorkConfig();

			switch (lbl) {
			case bufferSize:
				Number bufferSize = (Number)(field.getValue());
				workConfig.setBufferSize(bufferSize.intValue());
				break;
			case frameSize:
				workConfig.setFrameSize((Integer)(field.getValue()));
				break;
			case windowSize:
				Number window = (Number)(field.getValue());
				workConfig.setWindowSize(window.intValue());
				break;
			case windowOverlap:
				Double overlap = (((Number)field.getValue()).doubleValue())*100;
				workConfig.setWindowOverlap(overlap.intValue());
				break;
			default:
				throw new RuntimeException("Not impl: "  + lbl.name());
			}
		}
	}
	

}

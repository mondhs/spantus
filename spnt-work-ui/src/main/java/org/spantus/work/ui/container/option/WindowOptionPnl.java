package org.spantus.work.ui.container.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.spantus.logger.Logger;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;

public class WindowOptionPnl extends AbstractOptionPanel implements ReloadableComponent{
	
	enum optionsLabels{bufferSize, frameSize, windowSize, windowOverlap, 
		recordSampleRate, audioPathOutput, thresholdLearningPeriod, thresholdCoef, 
		segmentationMinLength, segmentationMinSpace,
		segmentationExpandStart, segmentationExpandEnd} 

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
		
		this.add(getHelpButton());
		this.add(new JLabel("", JLabel.TRAILING));

		SpantusWorkSwingUtils.makeCompactGrid(this,
				optionsLabels.values().length+1, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
		
		
		
		reload();
	}
	
	JButton  helpButton = null;
	protected JButton getHelpButton(){
		if(helpButton == null){
			helpButton = new JButton(getMessage("help"));
			helpButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					Icon icon =new ImageIcon(getClass().getResource(
							"/org/spantus/work/ui/img/ui-segment-prams."+getI18n().getLocale()+".png"));
					JOptionPane.showMessageDialog(null, 
							"", 
							getMessage("help"), 
							JOptionPane.PLAIN_MESSAGE, icon);
				}
				
			});
		}
		return helpButton;
		
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
			case recordSampleRate:
				textField.setValue(workConfig.getRecordSampleRate());
				break;
			case audioPathOutput:
				textField.setValue(workConfig.getAudioPathOutput());
				break;
			case thresholdLearningPeriod:
				textField.setValue(Integer.valueOf(workConfig.getThresholdLeaningPeriod()));
				break;
			case thresholdCoef:
				textField.setValue(Float.valueOf(workConfig.getThresholdCoef()));
				break;
			case segmentationMinLength:
				textField.setValue(Integer.valueOf(workConfig.getSegmentationMinLength()));
				break;
			case segmentationMinSpace:
				textField.setValue(Integer.valueOf(workConfig.getSegmentationMinSpace()));
				break;
			case segmentationExpandStart:
				textField.setValue(Integer.valueOf(workConfig.getSegmentationExpandStart()));
				break;
			case segmentationExpandEnd:
				textField.setValue(Integer.valueOf(workConfig.getSegmentationExpandEnd()));
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
			
			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			textField.setName(optionsLabels.recordSampleRate.name());
			jTextFields.add(textField);
			
			textField = new JFormattedTextField();
			textField.setName(optionsLabels.audioPathOutput.name());
			jTextFields.add(textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.thresholdLearningPeriod.name());
			jTextFields.add(textField);

			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			textField.setName(optionsLabels.thresholdCoef.name());
			jTextFields.add(textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.segmentationMinLength.name());
			jTextFields.add(textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.segmentationMinSpace.name());
			jTextFields.add(textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.segmentationExpandStart.name());
			jTextFields.add(textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.segmentationExpandEnd.name());
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

	public void setInfo(SpantusWorkInfo config) {
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
			case recordSampleRate:
				Number recordSampleRate = (Number)(field.getValue());
				workConfig.setRecordSampleRate(recordSampleRate.floatValue());
				break;
			case audioPathOutput:
				workConfig.setAudioPathOutput(field.getValue().toString());
				break;
			case thresholdLearningPeriod:
				Number thresholdLeaningPeriod = (Number)(field.getValue());
				workConfig.setThresholdLeaningPeriod(thresholdLeaningPeriod.intValue());
				break;
			case thresholdCoef:
				Number thresholdCoef = (Number)(field.getValue());
				workConfig.setThresholdCoef(thresholdCoef.floatValue());
				break;
			case segmentationMinLength:
				Number segmentationMinLength = (Number)(field.getValue());
				workConfig.setSegmentationMinLength(segmentationMinLength.intValue());
				break;
			case segmentationMinSpace:
				Number segmentationMinSpace = (Number)(field.getValue());
				workConfig.setSegmentationMinSpace(segmentationMinSpace.intValue());
				break;
			case segmentationExpandStart:
				Number segmentationExpandStart = (Number)(field.getValue());
				workConfig.setSegmentationExpandStart(segmentationExpandStart.intValue());
				break;
			case segmentationExpandEnd:
				Number segmentationExpandEnd = (Number)(field.getValue());
				workConfig.setSegmentationExpandEnd(segmentationExpandEnd.intValue());
				break;
			default:
				throw new RuntimeException("Not impl: "  + lbl.name());
			}
		}
	}
	

}

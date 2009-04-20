package org.spantus.work.ui.container.option;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;

import org.spantus.core.threshold.ThresholdEnum;
import org.spantus.logger.Logger;
import org.spantus.ui.MapComboBoxModel;
import org.spantus.ui.ModelEntry;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;

public class WindowOptionPnl extends AbstractOptionPanel implements ReloadableComponent{
	
	enum optionsLabels{bufferSize, frameSize, windowSize, windowOverlap, 
		recordSampleRate, audioPathOutput, thresholdLearningPeriod, thresholdCoef, 
		segmentationMinLength, segmentationMinSpace,
		segmentationExpandStart, segmentationExpandEnd, thresholdType} 

	SpantusWorkInfo config;
	
	Logger log = Logger.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	private Map<optionsLabels, JComponent> jTextFields = null;

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
		for (Entry<optionsLabels, JComponent> fieldEntry : getOptionComponents().entrySet()) {
			 JLabel l = new JLabel(getMessage(fieldEntry.getKey().name()), JLabel.TRAILING);
			 this.add(l);
			 l.setLabelFor(fieldEntry.getValue());
			 this.add(fieldEntry.getValue());
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
		for (Entry<optionsLabels, JComponent> fieldEntry : getOptionComponents().entrySet()) {

			WorkUIExtractorConfig workConfig = getConfig().getProject().getFeatureReader().getWorkConfig();

			JFormattedTextField field = null;
			if(fieldEntry.getValue() instanceof JFormattedTextField){
				field = (JFormattedTextField)fieldEntry.getValue();
			}
			
			switch (fieldEntry.getKey()) {
			case bufferSize:
				field.setValue(Integer.valueOf((workConfig.getBufferSize())));
				break;
			case frameSize:
				field.setValue(Integer.valueOf((workConfig.getFrameSize())));
				break;
			case windowSize:
				field.setValue(Integer.valueOf((workConfig.getWindowSize())));
				break;
			case windowOverlap:
				field.setValue(Double.valueOf(((double)workConfig.getWindowOverlap()/100)));
				break;
			case recordSampleRate:
				field.setValue(workConfig.getRecordSampleRate());
				break;
			case audioPathOutput:
				field.setValue(workConfig.getAudioPathOutput());
				break;
			case thresholdLearningPeriod:
				field.setValue(Integer.valueOf(workConfig.getThresholdLeaningPeriod()));
				break;
			case thresholdCoef:
				field.setValue(workConfig.getThresholdCoef());
				break;
			case segmentationMinLength:
				field.setValue(Integer.valueOf(workConfig.getSegmentationMinLength()));
				break;
			case segmentationMinSpace:
				field.setValue(Integer.valueOf(workConfig.getSegmentationMinSpace()));
				break;
			case segmentationExpandStart:
				field.setValue(Integer.valueOf(workConfig.getSegmentationExpandStart()));
				break;
			case segmentationExpandEnd:
				field.setValue(Integer.valueOf(workConfig.getSegmentationExpandEnd()));
				break;
			case thresholdType:
				((JComboBox)fieldEntry.getValue()).setSelectedItem(
						getMessage("threshold_"
						+getConfig().getProject().getThresholdType()));
				break;
				
			default:
				throw new RuntimeException("Not impl: "  + fieldEntry.getKey().name());
			}
		}
		
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private Map<optionsLabels, JComponent> getOptionComponents() {
		if (jTextFields == null) {
			jTextFields = new LinkedHashMap<optionsLabels, JComponent>();
			
			JFormattedTextField textField = new JFormattedTextField(
					getI18n().getDecimalFormat());
			textField.setName(optionsLabels.bufferSize.name());
			jTextFields.put(optionsLabels.bufferSize, textField);
			
			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			textField.setName(optionsLabels.frameSize.name());
			jTextFields.put(optionsLabels.frameSize, textField);

			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.windowSize.name());
			jTextFields.put(optionsLabels.windowSize,textField);

			textField = new JFormattedTextField(getI18n().getPercentFormat());
			textField.setName(optionsLabels.windowOverlap.name());
			jTextFields.put(optionsLabels.windowOverlap,textField);
			
			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			textField.setName(optionsLabels.recordSampleRate.name());
			jTextFields.put(optionsLabels.recordSampleRate, textField);
			
			textField = new JFormattedTextField();
			textField.setName(optionsLabels.audioPathOutput.name());
			jTextFields.put(optionsLabels.audioPathOutput, textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.thresholdLearningPeriod.name());
			jTextFields.put(optionsLabels.thresholdLearningPeriod,textField);

			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			textField.setName(optionsLabels.thresholdCoef.name());
			jTextFields.put(optionsLabels.thresholdCoef, textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.segmentationMinLength.name());
			jTextFields.put(optionsLabels.segmentationMinLength, textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.segmentationMinSpace.name());
			jTextFields.put(optionsLabels.segmentationMinSpace, textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.segmentationExpandStart.name());
			jTextFields.put(optionsLabels.segmentationExpandStart, textField);
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			textField.setName(optionsLabels.segmentationExpandEnd.name());
			jTextFields.put(optionsLabels.segmentationExpandEnd, textField);
			
			JComboBox thresholdInput = new JComboBox();
			thresholdInput.setModel(getThresholdModel());
			thresholdInput.setName(optionsLabels.thresholdType.name());
			jTextFields.put(optionsLabels.thresholdType, thresholdInput);

			
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
		for (Entry<optionsLabels, JComponent> fieldEntry : getOptionComponents().entrySet()) {

			WorkUIExtractorConfig workConfig = getConfig().getProject().getFeatureReader().getWorkConfig();
			JComponent fieldComponent =  fieldEntry.getValue();
			JFormattedTextField field = null;
			if(fieldComponent instanceof JFormattedTextField){
				field = (JFormattedTextField)fieldComponent;
			}
			
			switch (fieldEntry.getKey()) {
			case bufferSize:
				Number bufferSize = (Number)(field.getValue());
				workConfig.setBufferSize(bufferSize.intValue());
				break;
			case frameSize:
				workConfig.setFrameSize(
						((Number)field.getValue()).intValue()
						);
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
			case thresholdType:
				ThresholdEnum thresholdType = (ThresholdEnum)getThresholdModel().getSelectedObject();
				getConfig().getProject().setThresholdType(thresholdType.name());
				break;
			default:
				throw new RuntimeException("Not impl: "  + fieldEntry.getKey().name());
			}
		}
	}
	
	MapComboBoxModel treasholdType;
	
	protected MapComboBoxModel getThresholdModel() {
		if (treasholdType == null) {
			treasholdType = new MapComboBoxModel();
			for (ThresholdEnum thresholdTypeEnum : ThresholdEnum.values()) {
				String label = getMessage("threshold_" + thresholdTypeEnum.name());
				treasholdType.addElement(new ModelEntry(label, thresholdTypeEnum));
			}
		}
		return treasholdType;
	}
	

}

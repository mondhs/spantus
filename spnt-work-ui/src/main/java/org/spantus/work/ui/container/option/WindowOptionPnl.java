package org.spantus.work.ui.container.option;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.spantus.core.threshold.ThresholdEnum;
import org.spantus.logger.Logger;
import org.spantus.ui.MapComboBoxModel;
import org.spantus.ui.ModelEntry;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.layout.FormLayout;

public class WindowOptionPnl extends AbstractOptionPanel implements ReloadableComponent{
	
	enum optionsLabels{
		bufferSize, frameSize, windowSize, windowOverlap, 
		automatedSignalParameters,
		
		recordSampleRate, audioPathOutput,  
		
		thresholdLearningPeriod, thresholdCoef, thresholdType,
		automatedThresholdParameters,
		
		segmentationMinLength, segmentationMinSpace,
		segmentationExpandStart, segmentationExpandEnd, 
		autoSegmentation,
		automatedSegmentaionParameters,
		
		} 

	
	Logger log = Logger.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	private Map<optionsLabels, LabelControlEntry> jTextFields = null;

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
		optionsLabels[] signalProcessingLabels = new optionsLabels[]{
				optionsLabels.automatedSignalParameters,
				optionsLabels.bufferSize, optionsLabels.frameSize, 
				optionsLabels.windowSize, optionsLabels.windowOverlap,	
		};
		optionsLabels[] recordLabels = new optionsLabels[]{
				optionsLabels.recordSampleRate, optionsLabels.audioPathOutput, 
		};
		optionsLabels[] segmentLabels = new optionsLabels[]{
				optionsLabels.automatedSegmentaionParameters,
				optionsLabels.autoSegmentation,
				optionsLabels.segmentationMinLength, optionsLabels.segmentationMinSpace, 
				optionsLabels.segmentationExpandStart, optionsLabels.segmentationExpandEnd, 

		};
		
		optionsLabels[] thresholdLabels = new optionsLabels[]{
				optionsLabels.automatedThresholdParameters,
				optionsLabels.thresholdLearningPeriod, optionsLabels.thresholdCoef, 
				optionsLabels.thresholdType,
				optionsLabels.automatedThresholdParameters,

		};

		
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
		builder.appendSeparator(getMessage("Signal"));
		for (optionsLabels fieldEntryLabel : signalProcessingLabels) {
			LabelControlEntry fieldEntry = getOptionComponents().get(fieldEntryLabel);
			builder.append(fieldEntry.getLabel(), fieldEntry.getControl());
		}
		builder.nextLine();
		builder.appendSeparator(getMessage("Record"));
		for (optionsLabels fieldEntryLabel : recordLabels) {
			LabelControlEntry fieldEntry = getOptionComponents().get(fieldEntryLabel);
			builder.append(fieldEntry.getLabel(), fieldEntry.getControl());
		}
		builder.nextLine();
		builder.appendSeparator(getMessage("Segmentation"));
		for (optionsLabels fieldEntryLabel : segmentLabels) {
			LabelControlEntry fieldEntry = getOptionComponents().get(fieldEntryLabel);
			builder.append(fieldEntry.getLabel(), fieldEntry.getControl());
		}
		builder.nextLine();
		builder.appendSeparator(getMessage("Threshold"));
		for (optionsLabels fieldEntryLabel : thresholdLabels) {
			LabelControlEntry fieldEntry = getOptionComponents().get(fieldEntryLabel);
			builder.append(fieldEntry.getLabel(), fieldEntry.getControl());
		}
		builder.nextLine();
		

		add(builder.getPanel());
		
		
////		this.add(getHelpButton());
////		this.add(new JLabel("", JLabel.TRAILING));
//
		
		

		
		reload();
	}
	
//	JButton  helpButton = null;
//	protected JButton getHelpButton(){
//		if(helpButton == null){
//			helpButton = new JButton(getMessage("help"));
//			helpButton.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e) {
//					Icon icon =new ImageIcon(getClass().getResource(
//							"/org/spantus/work/ui/img/ui-segment-prams."+getI18n().getLocale()+".png"));
//					JOptionPane.showMessageDialog(null, 
//							"", 
//							getMessage("help"), 
//							JOptionPane.PLAIN_MESSAGE, icon);
//				}
//				
//			});
//		}
//		return helpButton;
//		
//	}
	
	public void reload() {
		onShowEvent();
	}
	
	//Override
	public void onShowEvent() {
		for (Entry<optionsLabels, LabelControlEntry> fieldEntry : getOptionComponents().entrySet()) {

			WorkUIExtractorConfig workConfig = getInfo().getProject().getFeatureReader().getWorkConfig();

			JComponent cmp = fieldEntry.getValue().getControl();
			JFormattedTextField textField = null;
			
			if(fieldEntry.getValue().getControl() instanceof JFormattedTextField){
				textField = (JFormattedTextField)cmp;
			}
			JComponent label = fieldEntry.getValue().getLabel();
			if(label != null && cmp != null && label instanceof JLabel){
				((JLabel)label).setText(getMessage(cmp.getName()));
			}
			switch (fieldEntry.getKey()) {
			case bufferSize:
				textField.setValue(Integer.valueOf((workConfig.getBufferSize())));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case frameSize:
				textField.setValue(Integer.valueOf((workConfig.getFrameSize())));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case windowSize:
				textField.setValue(Integer.valueOf((workConfig.getWindowSize())));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case windowOverlap:
				textField.setValue(Double.valueOf(((double)workConfig.getWindowOverlap()/100)));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case recordSampleRate:
				textField.setValue(workConfig.getRecordSampleRate());
				break;
			case audioPathOutput:
				File file = new File(workConfig.getAudioPathOutput());
				textField.setValue(file.getAbsolutePath());
				break;
			case thresholdLearningPeriod:
				textField.setValue(Integer.valueOf(workConfig.getThresholdLeaningPeriod()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case thresholdCoef:
				textField.setValue(workConfig.getThresholdCoef());
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case segmentationMinLength:
				textField.setValue(Integer.valueOf(workConfig.getSegmentationMinLength()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case segmentationMinSpace:
				textField.setValue(Integer.valueOf(workConfig.getSegmentationMinSpace()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case segmentationExpandStart:
				textField.setValue(Integer.valueOf(workConfig.getSegmentationExpandStart()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case segmentationExpandEnd:
				textField.setValue(Integer.valueOf(workConfig.getSegmentationExpandEnd()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case autoSegmentation:
				((JCheckBox)cmp).setSelected(Boolean.TRUE.equals(getInfo().getEnv().getAutoSegmentation()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case thresholdType:
				((JComboBox)fieldEntry.getValue().getControl()).setSelectedItem(
						getMessage("threshold_"
						+getInfo().getProject().getThresholdType()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case automatedSegmentaionParameters:
			case automatedThresholdParameters:
			case automatedSignalParameters:
				fieldEntry.getValue().setVisible(!isAdvanced());
				break;
				
			default:
				throw new RuntimeException("Not impl: "  + fieldEntry.getKey().name());
			}
		}
		
	}

	public void addFieldList(JComponent component, String labelName ){
		optionsLabels labelNameEnum = optionsLabels.valueOf(labelName);
		component.setName(labelName);
		JLabel label = new JLabel(getMessage(labelName), JLabel.TRAILING);
		label.setLabelFor(component);
		jTextFields.put(labelNameEnum, 
				new LabelControlEntry(label, component));
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private Map<optionsLabels, LabelControlEntry> getOptionComponents() {
		if (jTextFields == null) {
			jTextFields = new LinkedHashMap<optionsLabels, LabelControlEntry>();

			JFormattedTextField textField = new JFormattedTextField(
					getI18n().getDecimalFormat());
			addFieldList(textField, optionsLabels.bufferSize.name() );
			
			
			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			addFieldList(textField, optionsLabels.frameSize.name() );

			

			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			addFieldList(textField, optionsLabels.windowSize.name() );
			

			textField = new JFormattedTextField(getI18n().getPercentFormat());
			addFieldList(textField, optionsLabels.windowOverlap.name() );
			
			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			addFieldList(textField, optionsLabels.recordSampleRate.name() );
			
			textField = new JFormattedTextField();
			addFieldList(textField, optionsLabels.audioPathOutput.name() );
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			addFieldList(textField, optionsLabels.thresholdLearningPeriod.name() );

			textField = new JFormattedTextField(getI18n().getDecimalFormat());
			addFieldList(textField, optionsLabels.thresholdCoef.name() );
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			addFieldList(textField, optionsLabels.segmentationMinLength.name() );
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			addFieldList(textField, optionsLabels.segmentationMinSpace.name() );
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			addFieldList(textField, optionsLabels.segmentationExpandStart.name() );
			
			textField = new JFormattedTextField(getI18n().getMillisecondFormat());
			addFieldList(textField, optionsLabels.segmentationExpandEnd.name() );
			
			JComboBox thresholdInput = new JComboBox();
			thresholdInput.setModel(getThresholdModel());
			addFieldList(thresholdInput, optionsLabels.thresholdType.name() );
			
			JCheckBox autoSegmentationChb = new JCheckBox();
			autoSegmentationChb.setSelected(Boolean.TRUE
					.equals(getInfo().getEnv().getAutoSegmentation()));
			addFieldList(autoSegmentationChb, optionsLabels.autoSegmentation.name());
			
			JCheckBox auto = new JCheckBox("",true);
			auto.setEnabled(false);
			addFieldList(auto, optionsLabels.automatedSignalParameters.name());
			auto = new JCheckBox("",true);
			auto.setEnabled(false);
			addFieldList(auto, optionsLabels.automatedSegmentaionParameters.name());
			auto = new JCheckBox("",true);
			auto.setEnabled(false);
			addFieldList(auto, optionsLabels.automatedThresholdParameters.name());

			
		}
		return jTextFields;
	}

	
	public void save() {
		for (Entry<optionsLabels, LabelControlEntry> fieldEntry : getOptionComponents().entrySet()) {

			WorkUIExtractorConfig workConfig = getInfo().getProject().getFeatureReader().getWorkConfig();
			JComponent cmp =  fieldEntry.getValue().getControl();
			JFormattedTextField field = null;
			if(cmp instanceof JFormattedTextField){
				field = (JFormattedTextField)cmp;
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
				File dirExist = new File(workConfig.getAudioPathOutput());
				File dirNew = new File(field.getValue().toString());
				if(dirNew.compareTo(dirExist.getAbsoluteFile())!=0){
					//hacking. this should be serice layer somewhere
					if(dirNew.exists()){
						if(dirNew.isDirectory()){
							workConfig.setAudioPathOutput(field.getValue().toString());
						}else{
							//file exist
							File createDir = new File(dirNew.getParent(), dirNew.getName()+"."+System.currentTimeMillis());
							createDir.mkdir();
							workConfig.setAudioPathOutput(createDir.getAbsolutePath());
						}
					}else{
						File createDir = new File(dirNew.getParent(), dirNew.getName());
						createDir.mkdir();
						workConfig.setAudioPathOutput(createDir.getAbsolutePath());
					}
				}
				
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
			case autoSegmentation:
				getInfo().getEnv().setAutoSegmentation(((JCheckBox)cmp).isSelected());
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case thresholdType:
				ThresholdEnum thresholdType = (ThresholdEnum)getThresholdModel().getSelectedObject();
				if(thresholdType!=null){
					getInfo().getProject().setThresholdType(thresholdType.name());
				}else{
					getInfo().getProject().setThresholdType(ThresholdEnum.offline.name());
				}
				break;
			case automatedSegmentaionParameters:
			case automatedThresholdParameters:
			case automatedSignalParameters:
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

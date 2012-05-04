/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.work.ui.container.option;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLLocalConstraint;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.ui.MapComboBoxModel;
import org.spantus.ui.ModelEntry;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.dto.RecognitionConfig;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.debug.FormDebugPanel;
import com.jgoodies.forms.layout.FormLayout;
/**
 * Window(Feature) Option Panel
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jan 25, 2010
 *
 */
public class RecognitionOptionPnl extends AbstractOptionPanel implements ReloadableComponent{
	
        private enum WindowOptionSeparators {recognition};
        public static final String PREFIX_dtwWindow = "dtwWindow_";
        public static final String PREFIX_dtwConstraint = "dtwConstraint_";
        

	enum optionsLabels{
		dtwWindow, dtwConstraint, dtwRadius, repositoryPath,  
		} 

	
	Logger log = Logger.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	private Map<optionsLabels, LabelControlEntry> jTextFields = null;

	private MapComboBoxModel<String, JavaMLSearchWindow> dtwWindowCbxModel;
	private MapComboBoxModel<String, JavaMLLocalConstraint> dtwConstraintCbxModel;
	
	/**
	 * This is the default constructor
	 */
	public RecognitionOptionPnl() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
//		this.setSize(300, 200);
		optionsLabels[] recognitionProcessingLabels = new optionsLabels[]{
				optionsLabels.dtwWindow, optionsLabels.dtwConstraint, 
				optionsLabels.dtwRadius, optionsLabels.repositoryPath
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
		builder.appendSeparator(getMessage(WindowOptionSeparators.recognition));
		for (optionsLabels fieldEntryLabel : recognitionProcessingLabels) {
			LabelControlEntry fieldEntry = getOptionComponents().get(fieldEntryLabel);
			builder.append(fieldEntry.getLabel(), fieldEntry.getControl());
		}
		builder.nextLine();
		
		add(builder.getPanel());
		
		reload();
	}
	

	
	public void reload() {
		onShowEvent();
	}
	
      
        
	//Override
	public void onShowEvent() {
		for (Entry<optionsLabels, LabelControlEntry> fieldEntry : getOptionComponents().entrySet()) {

			RecognitionConfig config = getInfo().getProject().getRecognitionConfig();

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
			case dtwRadius:
				textField.setValue(Float.valueOf((config.getRadius())));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;

			case dtwWindow:
				((JComboBox)fieldEntry.getValue().getControl()).setSelectedItem(
						getMessage(PREFIX_dtwWindow
						+config.getDtwWindow()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;
			case dtwConstraint:
				((JComboBox)fieldEntry.getValue().getControl()).setSelectedItem(
						getMessage(PREFIX_dtwConstraint
						+config.getLocalConstraint()));
				fieldEntry.getValue().setVisible(isAdvanced());
				break;

			case repositoryPath:
				File file = new File(config.getRepositoryPath());
				textField.setValue(file.getAbsolutePath());
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

			JComboBox windowingInput = new JComboBox();
			windowingInput.setModel(getDtwWindowCbxModel());
			addFieldList(windowingInput, optionsLabels.dtwWindow.name() );
			
			JComboBox constraintInput = new JComboBox();
			constraintInput.setModel(getDtwConstraintCbxModel());
			addFieldList(constraintInput, optionsLabels.dtwConstraint.name() );

                        
                        JFormattedTextField textField = new JFormattedTextField(
					getI18n().getDecimalFormat());
			addFieldList(textField, optionsLabels.dtwRadius.name() );
			
			
			textField = new JFormattedTextField();
			addFieldList(textField, optionsLabels.repositoryPath.name() );
			

			
		}
		return jTextFields;
	}

	
	public void save() {
		for (Entry<optionsLabels, LabelControlEntry> fieldEntry : getOptionComponents().entrySet()) {

			RecognitionConfig config = getInfo().getProject().getRecognitionConfig();
			JComponent cmp =  fieldEntry.getValue().getControl();
			JFormattedTextField field = null;
			if(cmp instanceof JFormattedTextField){
				field = (JFormattedTextField)cmp;
			}
			
			switch (fieldEntry.getKey()) {
			case dtwRadius:
				Number radius = (Number)(field.getValue());
				config.setRadius(radius.floatValue());
				break;
			case dtwWindow:
				JavaMLSearchWindow windowingEnum = getDtwWindowCbxModel().getSelectedObject();
				if(windowingEnum!=null){
					config.setDtwWindow(windowingEnum.name());
				}else{
					config.setDtwWindow(null);
				}
				break;
			case dtwConstraint:
				JavaMLLocalConstraint javaMLLocalConstraint = getDtwConstraintCbxModel().getSelectedObject();
				if(javaMLLocalConstraint!=null){
					config.setLocalConstraint(javaMLLocalConstraint.name());
				}else{
					config.setLocalConstraint(null);
				}
				break;
			case repositoryPath:
				File dirExist = new File(config.getRepositoryPath());
				File dirNew = new File(field.getValue().toString());
				if(dirNew.compareTo(dirExist.getAbsoluteFile())!=0){
					//hacking. this should be serice layer somewhere
					if(dirNew.exists()){
						if(dirNew.isDirectory()){
							config.setRepositoryPath(field.getValue().toString());
						}else{
							//file exist
							File createDir = new File(dirNew.getParent(), dirNew.getName()+"."+System.currentTimeMillis());
							createDir.mkdir();
							config.setRepositoryPath(createDir.getAbsolutePath());
						}
					}else{
						File createDir = new File(dirNew.getParent(), dirNew.getName());
						createDir.mkdir();
						config.setRepositoryPath(createDir.getAbsolutePath());
					}
				}
				
				break;
			default:
				throw new RuntimeException("Not impl: "  + fieldEntry.getKey().name());
			}
		}
	}
	/**
	 * dynamic show/hide for controls 
	 * @param optionsLabel
	 * @param value
	 * @return
	 */
	protected boolean setLabelControlVisible(optionsLabels optionsLabel, String value){
//		LabelControlEntry labelControlEntry = jTextFields.get(optionsLabel);
//		switch (optionsLabel) {
//		case thresholdLearningPeriod:
//			labelControlEntry.setVisible(ClassifierEnum.online.name().equals(value));
//			break;
//		case thresholdCoef:
//			labelControlEntry.setVisible(!ClassifierEnum.rules.name().equals(value));
//			break;
//		case segmentationMinSpace:
////			boolean isAuto = jTextFields.get(optionsLabels.segmentationServiceType).isVisible();
//			labelControlEntry.setVisible(!SegmentatorServiceEnum.basic.name().equals(value));
//			break;
//		case segmentationMinLength:
////			isAuto = jTextFields.get(optionsLabels.segmentationServiceType).isVisible();
//			labelControlEntry.setVisible(!SegmentatorServiceEnum.basic.name().equals(value));
//			break;
//		case segmentationExpandStart:
////			isAuto = jTextFields.get(optionsLabels.segmentationServiceType).isVisible();
//			labelControlEntry.setVisible( !SegmentatorServiceEnum.basic.name().equals(value));
//			break;
//		case segmentationExpandEnd:
////			isAuto = jTextFields.get(optionsLabels.segmentationServiceType).isVisible();
//			labelControlEntry.setVisible(!SegmentatorServiceEnum.basic.name().equals(value));
//			break;
//		case segmentationServiceType:
//			boolean val = Boolean.valueOf(value);
//			labelControlEntry.setVisible(val);
//			break;
//                case autoRegonition:
//			labelControlEntry.setVisible(Boolean.valueOf(value));
//			break;

//		default:
//			break;
//		}
		return true;
	}
	

	
	protected MapComboBoxModel<String, JavaMLSearchWindow> getDtwWindowCbxModel() {
		if (dtwWindowCbxModel == null) {
			dtwWindowCbxModel = new MapComboBoxModel<String, JavaMLSearchWindow>();
			for (JavaMLSearchWindow windowingTypeEnum : JavaMLSearchWindow.values()) {
				String label = getMessage(PREFIX_dtwWindow + windowingTypeEnum.name());
				dtwWindowCbxModel.add(new ModelEntry<String, JavaMLSearchWindow>(label, windowingTypeEnum));
			}
		}
		return dtwWindowCbxModel;
	}

	public MapComboBoxModel<String, JavaMLLocalConstraint> getDtwConstraintCbxModel() {
		if (dtwConstraintCbxModel == null) {
			dtwConstraintCbxModel = new MapComboBoxModel<String, JavaMLLocalConstraint>();
			for (JavaMLLocalConstraint constraintEnum : JavaMLLocalConstraint.values()) {
				String label = getMessage(PREFIX_dtwConstraint + constraintEnum.name());
				dtwConstraintCbxModel.add(new ModelEntry<String, JavaMLLocalConstraint>(label, constraintEnum));
			}
		}
		return dtwConstraintCbxModel;
	}
	
	
}

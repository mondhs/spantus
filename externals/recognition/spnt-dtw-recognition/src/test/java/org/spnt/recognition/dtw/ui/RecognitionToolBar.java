package org.spnt.recognition.dtw.ui;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory; 
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JToolBar;

public class RecognitionToolBar extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	enum LabelEnum{start, stop, train, admin};

	RecognitionUIActionListener recognitionUIActionListener;
	ToolbarActionListener toolbarActionListener;
	
	private JButton recordBtn = null;

	private JButton stopBtn = null;
	
	private JButton adminBtn = null;
	
	private JCheckBox train;
	
	private boolean learnMode = false;
	
	protected void initialize() {
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 3, 2);
		this.setLayout(layout);
		this.add(getRecordBtn());
		this.add(getStopBtn());
		this.add(getTrainCheckbox());
		this.add(getAdminBtn());
		
	}
	
	public JButton getRecordBtn() {
		if (recordBtn == null) {
//			ImageIcon icon = createIcon(ImageResourcesEnum.record.getCode());
			recordBtn = createButton(null, LabelEnum.start.name());
		}
		return recordBtn;
	}
	public JButton getStopBtn() {
		if (stopBtn == null) {
//			ImageIcon icon = createIcon(ImageResourcesEnum.stop.getCode());
			stopBtn = createButton(null, LabelEnum.stop.name());
		}
		return stopBtn;
	}
	
	public JButton getAdminBtn() {
		if (adminBtn == null) {
//			ImageIcon icon = createIcon(ImageResourcesEnum.stop.getCode());
			adminBtn = createButton(null, LabelEnum.admin.name());
		}
		return adminBtn;
	}
	
	public JCheckBox getTrainCheckbox() {
		if (train == null) {
//			ImageIcon icon = createIcon(ImageResourcesEnum.stop.getCode());
			train = new JCheckBox(getResource(LabelEnum.train.name())); 
			train.setSelected(learnMode);
			train.addActionListener(getToolbarActionListener());
		}
		return train;
	}
	
	protected JButton createButton(ImageIcon icon, String cmd){
		return createButton(icon, cmd, cmd);
	}
	
	protected JButton createButton(ImageIcon icon, String cmd, String name){
		JButton btn = new JButton(getResource(name),icon);
		btn.setActionCommand(cmd);
		btn.setBorder(BorderFactory.createCompoundBorder()); 
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setFocusable(false);
		btn.setActionCommand(cmd);
		btn.setToolTipText(getResource(name));
		btn.addActionListener(getToolbarActionListener());
		return btn;
	}
	public String getResource(String key) {
		return key;
	}

	
	public RecognitionUIActionListener getRecognitionUIActionListener() {
		return recognitionUIActionListener;
	}

	public void setRecognitionUIActionListener(
			RecognitionUIActionListener recognitionUIActionListener) {
		this.recognitionUIActionListener = recognitionUIActionListener;
	}

	public ToolbarActionListener getToolbarActionListener() {
		if(toolbarActionListener == null){
			toolbarActionListener =  new ToolbarActionListener();
		}
		return toolbarActionListener;
	}
	
	
	public class ToolbarActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			LabelEnum cmd = LabelEnum.valueOf(e.getActionCommand());
			switch (cmd) {
			case start:
				getRecognitionUIActionListener().start();
				break;
			case stop:
				getRecognitionUIActionListener().stop();
				break;
			case train:
				learnMode = ((JCheckBox)e.getSource()).isSelected();
				getRecognitionUIActionListener().changeLearningStatus(learnMode);
				break;
			case admin:
				getRecognitionUIActionListener().stop();
				
				JDialog adminDialog = new JDialog(
						(Frame)getParent().getParent().getParent().getParent());
				adminDialog.setContentPane(new AdminPanel());
				adminDialog.setSize(640,480);
				adminDialog.setModal(true);
				adminDialog.setVisible(true);
				
				break;
			default:
				break;
			}
			
		}
		
	}


	public boolean isLearnMode() {
		return learnMode;
	}

	public void setLearnMode(boolean learnMode) {
		this.learnMode = learnMode;
	}
}

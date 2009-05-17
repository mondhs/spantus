package org.spnt.recognition.dtw.ui;

import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JToolBar;

public class RecognitionToolBar extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	enum LabelEnum{start, stop, train};

	
	private JButton recordBtn = null;

	private JButton stopBtn = null;
	
	protected void initialize() {
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 3, 2);
		this.setLayout(layout);
		this.add(getRecordBtn());
		this.add(getStopBtn());
		this.add(getTrainCheckbox());
		
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
	
	JCheckBox train;
	public JCheckBox getTrainCheckbox() {
		if (train == null) {
//			ImageIcon icon = createIcon(ImageResourcesEnum.stop.getCode());
			train = new JCheckBox(getResource(LabelEnum.train.name())); 
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
//		btn.addActionListener(getToolbarActionListener());
		return btn;
	}
	public String getResource(String key) {
		return key;
	}
}

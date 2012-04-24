package org.spnt.applet.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.sound.sampled.Mixer;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.spnt.applet.ctx.SpantusAudioCtx;

import edu.mit.csail.sls.wami.applet.sound.AudioDevice;
import edu.mit.csail.sls.wami.applet.sound.SpeechDetector;

public class JSettings {
	private AudioDevice audioDevice;
	private SpantusAudioCtx ctx;
	private SpeechDetector detector;

	private JComboBox comboSource;
	private JComboBox comboTarget;
	private JFrame frame;
	private ArrayList<JTextField> paramFields;
	private Container contentPane;
	
	
	
	public JSettings(AudioDevice audioDevice, SpantusAudioCtx ctx,
			SpeechDetector detector, Container container) {
		super();
		this.audioDevice = audioDevice;
		this.ctx = ctx;
		this.detector = detector;
		this.contentPane = container;
	}

	/**
	 * shows a window where audio settings can be adjusted
	 */
	public void showSettings() {
		Mixer.Info[] sourceMixers = AudioDevice.getAvailableSourceMixers();
		Mixer.Info[] targetMixers = AudioDevice.getAvailableTargetMixers();

		Mixer.Info preferredSource = audioDevice.getPreferredSourceMixer();
		Mixer.Info preferredTarget = audioDevice.getPreferredTargetMixer();

		Vector<Object> vSource = new Vector<Object>(Arrays.asList(sourceMixers));
		Vector<Object> vTarget = new Vector<Object>(Arrays.asList(targetMixers));

		vSource.add(0, "Default");
		vTarget.add(0, "Default");
		comboSource = new JComboBox(vSource);
		comboTarget = new JComboBox(vTarget);
		if (preferredSource != null) {
			comboSource.setSelectedItem(preferredSource);
		}
		if (preferredTarget != null) {
			comboTarget.setSelectedItem(preferredTarget);
		}

		Box audioBox = Box.createVerticalBox();
		Box topBox = Box.createHorizontalBox();
		Box bottomBox = Box.createHorizontalBox();
		audioBox.add(topBox);
		audioBox.add(bottomBox);
		contentPane.add(audioBox);

		topBox.add(new JLabel("Audio Out"));
		topBox.add(comboSource);
		bottomBox.add(new JLabel("Audio In "));
		bottomBox.add(comboTarget);

		paramFields = new ArrayList<JTextField>();
		if (ctx.getUseSpeechDetector()) {
			// detector params
			for (String param : getParams()) {
				Box paramBox = Box.createHorizontalBox();
				final JTextField textField = new JTextField();
				final JLabel label = new JLabel(param);
				paramBox.add(label);
				paramBox.add(textField);
				paramFields.add(textField);
				textField.setText("" + detector.getParameter(param));
				textField.setEditable(true);
				audioBox.add(paramBox);
			}
		}

		frame = new JFrame("Settings");
		Box cp = Box.createVerticalBox();
		cp.add(audioBox);

		JButton okButton = newJButtonOK();
		JButton cancelButton = newJButtonCancel();

	
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		cp.add(buttonBox);

		frame.setContentPane(cp);
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}

	/**
	 * 
	 * @return
	 */
	private JButton newJButtonCancel() {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		return cancelButton;
	}

	/**
	 * 
	 * @return
	 */
	private String[] getParams() {
		final String[] params = ctx.getUseSpeechDetector() ? detector
				.getParameterNames() : new String[] {};
		return params;
	}

	/**
	 * 
	 * @return
	 */
	private JButton newJButtonOK() {
		JButton okButton = new JButton("OK");

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selected = comboSource.getSelectedItem();
				audioDevice
						.setPreferredSourceMixer((selected instanceof Mixer.Info) ? (Mixer.Info) selected
								: null);

				selected = comboTarget.getSelectedItem();
				audioDevice
						.setPreferredTargetMixer((selected instanceof Mixer.Info) ? (Mixer.Info) selected
								: null);

				if (ctx.getUseSpeechDetector()) {
					String[] params = getParams();
					for (int i = 0; i < params.length; i++) {
						String param = params[i];
						try {
							double value = Double.parseDouble(paramFields
									.get(i).getText());
							detector.setParameter(param, value);
						} catch (NumberFormatException eN) {
							eN.printStackTrace();
						}
					}
				}

				frame.dispose();
			}
		});
		return okButton;
	}
	
	public boolean isDisplayable(){
		return frame.isDisplayable();
	}
	
}

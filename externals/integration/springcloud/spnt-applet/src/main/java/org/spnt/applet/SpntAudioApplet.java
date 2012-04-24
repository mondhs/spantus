package org.spnt.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.spantus.logger.Logger;
import org.spnt.applet.ctx.SpantusAudioCtx;
import org.spnt.applet.ctx.SpantusAudioCtxService;
import org.spnt.applet.handler.PingHandler;
import org.spnt.applet.handler.PlayPollHandler;
import org.spnt.applet.handler.SpntAudioListenerHandler;
import org.spnt.applet.handler.SpntSpeechDetectorHandler;
import org.spnt.applet.ui.JSettings;
import org.spnt.applet.ui.SpntAppletButton;

import edu.mit.csail.sls.wami.applet.sound.AudioDevice;
import edu.mit.csail.sls.wami.applet.sound.AudioInputStreamSource;

public class SpntAudioApplet extends JApplet implements SpntAppletEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7646818688434041289L;

	private SpntAppletButton button;
	private static Logger LOG = Logger.getLogger(SpntAudioApplet.class);

	private JProgressBar levelMeter;
	private JSettings jSettings;
	private MouseListener mouseListener;

	volatile private SpantusAudioCtx ctx;

	private volatile boolean connected = false;
	private boolean initialized = false;

	private AudioDevice audioDevice = new AudioDevice();
	private PlayPollHandler playPollHandler;
	private PingHandler pingHandler;
	private SpntSpeechDetectorHandler speechDetectorHandler;
	private SpntAudioListenerHandler spntListenerHandler;
	private I18n i18n;

	@Override
	public void init() {
		LOG.debug("[init]Initializing WAMI Audio Applet 5");
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createGUI();
				}
			});
		} catch (Exception e) {
			LOG.debug("[init]Exception caught in applet init()");
			LOG.error(e);
		}
	}

	@Override
	public void start() {
		ctx.setIsPlaying(false);
		ctx.setIsRecording(false);
		ctx.setIsListening(false);
		ctx.setAudioFailure(false);
	}

	@Override
	public void destroy() {
		initialized = false;
		ctx.setDestroyed(true);
	}

	/**
	 * Visible to javascript: starts listening / recording
	 */
	public void startListening() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				startAudioListening();
			}
		});
	}

	/**
	 * Visible to javascript: stops listening / recording
	 */
	public void stopRecording() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				finish();
				ctx.setIsRecording(false);
				ctx.setIsListening(false);
			}
		});
	}

	/**
	 * Visible to javascript: stops playing
	 */
	public void stopPlaying() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (ctx.getIsPlaying()) {
					audioDevice.abort();
				}
			}
		});
	}

	/**
	 * Initializes the applet. Must be called from the swing thread
	 */
	private void createGUI() {

		if (initialized)
			return;
		ctx = SpantusAudioCtxService.getInstance().createSpantusAudioParams(
				this);
		playPollHandler = new PlayPollHandler(ctx, this);
		pingHandler = new PingHandler(this);
		spntListenerHandler = new SpntAudioListenerHandler(ctx, this);
		speechDetectorHandler = new SpntSpeechDetectorHandler(ctx, this);
		i18n = new I18n(ctx);
		LOG.debug("[createGUI]ctx: {0}", ctx);

		mouseListener = new MouseListener();

		button = new SpntAppletButton(i18n);
		button.setEnabled(false);
		button.addMouseListener(mouseListener);

		Container cp = getContentPane();
		cp.setBackground(Color.WHITE);

		JButton settings = new JButton(button.createImageIcon("gtk-preferences.png", "Settings"));
		settings.setOpaque(false);
		settings.setContentAreaFilled(false);
		settings.setBorderPainted(false);
		settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jSettings == null || !jSettings.isDisplayable()) {
					jSettings = new JSettings(audioDevice, ctx,
							speechDetectorHandler.getDetector(),
							getContentPane());
					jSettings.showSettings();
				}
			}
		});

		levelMeter = new JProgressBar(JProgressBar.HORIZONTAL, 0, 1024);
		levelMeter.setPreferredSize(new Dimension(
				levelMeter.getPreferredSize().width, settings
						.getPreferredSize().height));
		levelMeter.setStringPainted(false);
		levelMeter.setIndeterminate(false);

		cp.setLayout(new BorderLayout(3,3));

		LOG.debug("[createGUI]Hide Button: {0}", ctx.getHideButton());
		if (!ctx.getHideButton()) {
			cp.add(button, BorderLayout.CENTER);
		}

		Box box = Box.createHorizontalBox();
		cp.add(box, BorderLayout.SOUTH);
		box.add(settings);
		box.add(Box.createHorizontalStrut(5));
		box.add(levelMeter);

		audioDevice.addListener(spntListenerHandler);

		pingHandler.pingURL(ctx.getRecordUrl());
		playPollHandler.startPollingForAudio();
		showStatus("UI Started");
		initialized = true;
		audioDevice.playResource("start_tone.wav");
	}

	/**
	 * Starts "listening" if useSpeechDetector is true, otherwise it starts
	 * recording immediately
	 */
	void startAudioListening() {
		AudioInputStream audioIn;
		try {
			if (ctx.getPlayRecordTone()) {
				audioDevice.playResource("start_tone.wav");
			}

			// The following line is necessary to fix a weird bug on the Mac
			// whereby recording works once, but not a second time unless this
			// method gets called in between. I have no idea why. imcgraw

			AudioDevice.getAvailableTargetMixers();
			audioIn = audioDevice.getAudioInputStream(ctx.getRecordFormat());
			speechDetectorHandler.getDetector().listen(
					new AudioInputStreamSource(audioIn), 0,
					ctx.getUseSpeechDetector());
			LOG.debug("[startAudioListening]Detector is listening");
			showStatus("Start audio listening");
		} catch (LineUnavailableException e) {
			LOG.error(e);
			audioFailure();
		}
		ctx.setIsListening(true);
	}

	public void setConnectionStatus(boolean value) {
		connected = value;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				showStatus("Connection Status changed " + connected);
			}
		});
	}

	/**
	 * Must be called from the swing thread
	 */
	@Override
	public void showStatus(String msg) {
		LOG.debug(
				"[showStatus] msg: {4}; Conn: {0}, playing: {1}, listening {2}, recording {3}",
				connected, ctx.getIsPlaying(), ctx.getIsListening(),
				ctx.getIsRecording(), msg);
		if (!connected) {
			setListeningStatus(SpntAppletState.ErrorConnectionFailure);
		} else if (ctx.getAudioFailure()) {
			setListeningStatus(SpntAppletState.ErrorAudioFailure);
			button.setEnabled(false);
		} else if (ctx.getIsPlaying()) {
			if (ctx.getAllowStopPlaying()) {
				setListeningStatus(SpntAppletState.stopPlaying);
			} else {
				setListeningStatus(SpntAppletState.Playing);
			}
		} else if (ctx.getIsListening()) {
			if (ctx.getUseSpeechDetector()) {
				if (ctx.getIsRecording()) {
					setListeningStatus(SpntAppletState.RecordingClickToStop);
				} else {
					setListeningStatus(SpntAppletState.ListeningClickToStop);
				}
			} else {
				setListeningStatus(SpntAppletState.Recording);
			}
		} else {
			if (ctx.getUseSpeechDetector()) {
				setListeningStatus(SpntAppletState.ClickToTalk);
			} else {
				setListeningStatus(SpntAppletState.HoldToTalk);
			}
		}
	}

	/**
	 * Must be called from the swing thread
	 * 
	 * @param status
	 * @param color
	 */
	private void setListeningStatus(SpntAppletState state) {
		LOG.debug("[setListeningStatus] status {0};", state);
		button.updateState(state);
	}

	private class MouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			LOG.debug("[mousePressed]");
			if (!button.isEnabled()) {
				return;
			}
			if (!ctx.getIsPlaying()) {
				if (ctx.getIsListening() && ctx.getUseSpeechDetector()) {
					LOG.debug("[mousePressed] finish as is listening and uses detector");
					finish();
				} else if (!ctx.getIsRecording()) {
					LOG.debug("[mousePressed] startAudioListening as is not playing and not recording");
					startAudioListening();
				}
			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			LOG.debug("[mouseReleased]");
			if (!button.isEnabled()) {
				return;
			}
			if (!ctx.getIsPlaying() && !ctx.getUseSpeechDetector()) {
				LOG.debug("[mouseReleased] finish as is not playing and not uses detector");
				finish();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			LOG.debug("[mouseClicked]");
			if (!button.isEnabled()) {
				return;
			}
			if (ctx.getIsPlaying()) {
				if (ctx.getAllowStopPlaying()) {
					LOG.debug(
							"[mouseClicked] abort as is playing and allowed to stop {0}",
							ctx);
					audioDevice.abort();
				}
			} else if (ctx.getIsRecording()) {
				if (!ctx.getUseSpeechDetector()) {
					LOG.debug("[mouseClicked] finish as is not playing and is recording and  uses detector");
					finish();
				}
			}

		}
	}

	void audioFailure() {
		ctx.setAudioFailure(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				showStatus("audioFailure");
			}
		});
	}

	/**
	 * 
	 */
	public void play(AudioInputStream ais) {
		LOG.debug("[play] Stream");
		audioDevice.play(ais);
	}

	public void play(String fileName) {
		LOG.debug("[play] fileName {0}", fileName);
		audioDevice.playResource(fileName);
	}

	public void finish() {
		audioDevice.finish();
	}

	public void updateMeterChangedValue() {
		double peak = speechDetectorHandler.getDetector().readPeakLevel();
		levelMeter.setValue((int) (peak * 1024 + .5));
	}

	public void resetMeterChangedValue() {
		levelMeter.setValue(0);
	}

}

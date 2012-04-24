package org.spnt.applet.ui;

import java.awt.Color;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.spnt.applet.I18n;
import org.spnt.applet.SpntAppletState;

public class SpntAppletButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4553949268054877356L;
	private SpntAppletState state;
	private I18n i18n;

	public SpntAppletButton(I18n i18n) {
		super("INIT", new ImageIcon("error.png"));
		this.i18n =i18n;
		updateState(SpntAppletState.Initializing);
		setVerticalTextPosition(SwingConstants.BOTTOM);
		setHorizontalTextPosition(SwingConstants.CENTER);
	}
	/**
	 * 
	 * @param state
	 */
	public void updateState(SpntAppletState state) {
		this.state = state;
		setToolTipText(mapTooltip(state));
		setText(mapText(state));
		setBackground(mapColor(state));
		setEnabled(mapEnable(state));
		setIcon(createImageIcon(state));
		setDisabledIcon(createDisableImageIcon(state));
	}
	/**
	 * 
	 * @param aState
	 * @return
	 */
	private Icon createDisableImageIcon(SpntAppletState aState) {
		String path = mapDisableImage(aState);
		String description = aState.name();
		return createImageIcon(path, description);
	}
	/**
	 * 
	 * @param aState
	 * @return
	 */
	protected ImageIcon createImageIcon(SpntAppletState aState) {
		String path = mapImage(aState);
		String description = aState.name();
		return createImageIcon(path, description);
	}
	/**
	 * 
	 * @param aState
	 * @return
	 */
	private String mapText(SpntAppletState aState) {
		return i18n.getMessage(aState.name());
	}
	/**
	 * 
	 * @param aState
	 * @return
	 */
	private String mapTooltip(SpntAppletState aState) {
		return i18n.getTooltip(aState.name());
	}
	/**
	 * 
	 * @param aState
	 * @return
	 */
	private String mapDisableImage(SpntAppletState aState) {
		String image = "";
		switch (aState) {
		case Playing:
			image = "play.png";
			break;
		case ErrorAudioFailure:
		case ErrorConnectionFailure:
			image = "error.png";
			break;
		default:
			// throw new IllegalArgumentException("Not implemented");
		}
		return image;
	}
	/**
	 * 
	 * @param aState
	 * @return
	 */
	private String mapImage(SpntAppletState aState) {
		String image = "";
		switch (aState) {
		case Playing:
			image = "play.png";
			break;
		case stopPlaying:
			image = "play-stop.png";
			break;
		case ClickToTalk:
		case ListeningClickToStop:
		case Recording:
			image = "record.png";
			break;
		case RecordingClickToStop:
			image = "record-stop.png";
			break;
		case ErrorAudioFailure:
		case ErrorConnectionFailure:
			image = "error.png";
			break;
		default:
			// throw new IllegalArgumentException("Not implemented");
		}
		return image;
	}

	
	/**
	 * 
	 * @param path
	 * @param description
	 * @return
	 */
	public ImageIcon createImageIcon(String path, String description) {
		URL imgURL = getClass().getClassLoader().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}


	/**
	 * 
	 * @param aState
	 * @return
	 */
	private Boolean mapEnable(SpntAppletState aState) {
		Boolean enable = false;
		switch (aState.getStopable()) {
		case canStop:
			enable = Boolean.TRUE;
			break;
		case canNotStop:
			enable = Boolean.FALSE;
			break;
		default:
			throw new IllegalArgumentException("Not implemented");
		}
		return enable;
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	public Color mapColor(SpntAppletState aState) {
		Color mapped = Color.black;
		switch (aState.getSeverity()) {
		case Bloked:
			mapped = Color.RED;
			break;
		case Playing:
			mapped = Color.GREEN;
			break;
		case Waiting:
			mapped = Color.GREEN;
			break;
		case PlayingDoNotStop:
			mapped = Color.GREEN;
			break;
		case Recording:
			mapped = Color.CYAN;
			break;
		default:
			throw new IllegalArgumentException("Not implemented");
		}
		return mapped;
	}

	/**
	 * @return the state
	 */
	public SpntAppletState getState() {
		return state;
	}

	// /**
	// * @param state the state to set
	// */
	// public void setState(SpntAppletState state) {
	// setText(state.name());
	// this.state = state;
	// }
}

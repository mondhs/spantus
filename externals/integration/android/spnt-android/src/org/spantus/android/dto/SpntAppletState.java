package org.spantus.android.dto;

public enum SpntAppletState {
	Initializing(SpntAppletStateSeverity.Bloked, SpntAppletStateStopable.canNotStop), 
	stopPlaying(SpntAppletStateSeverity.Playing, SpntAppletStateStopable.canStop), 
	Playing(SpntAppletStateSeverity.PlayingDoNotStop,SpntAppletStateStopable.canNotStop), 
	RecordingClickToStop(SpntAppletStateSeverity.Recording, SpntAppletStateStopable.canStop), 
	ListeningClickToStop(SpntAppletStateSeverity.Recording, SpntAppletStateStopable.canStop), 
	Recording(SpntAppletStateSeverity.Recording, SpntAppletStateStopable.canStop), 
	ClickToTalk(SpntAppletStateSeverity.Waiting, SpntAppletStateStopable.canStop), 
	HoldToTalk(SpntAppletStateSeverity.Waiting, SpntAppletStateStopable.canStop), 
	ErrorConnectionFailure(SpntAppletStateSeverity.Bloked, SpntAppletStateStopable.canNotStop), 
	ErrorAudioFailure(SpntAppletStateSeverity.Bloked, SpntAppletStateStopable.canNotStop);
	
	private SpntAppletStateSeverity severity;
	private SpntAppletStateStopable stopable;

	private SpntAppletState(SpntAppletStateSeverity severity,SpntAppletStateStopable stopable) {
		this.severity = severity;
		this.stopable= stopable;
	}
	public SpntAppletStateSeverity getSeverity() {
		return severity;
	}
	/**
	 * @return the stopable
	 */
	public SpntAppletStateStopable getStopable() {
		return stopable;
	}
}

package org.spnt.applet;

/**
 * Event listener interface for audio being controlled
 * @author cyphers 
 */
public interface SpntAudoListener {
		/**
		 * Audio output has been initialized, and samples are being played.
		 * 
		 */
		void playingHasStarted();

		/**
		 * The last sample has been played and the audio has been shut down
		 */
		void playingHasEnded();

		/**
		 * Audio input has been initialized and samples are being received from
		 * the device
		 */
		void listeningHasStarted();

		/**
		 * Audio input has been disabled and samples are no longer being
		 * received from the device
		 */
		void listeningHasEnded();
}

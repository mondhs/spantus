/* -*- Java -*-
 *
 * Copyright (c) 2008
 * Spoken Language Systems Group
 * MIT Computer Science and Artificial Intelligence Laboratory
 * Massachusetts Institute of Technology
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.mit.csail.sls.wami.applet.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;

/**
 * Interface to something that can provide an audio input stream.
 * 
 */
public interface Recorder {

    /**
         * Listener for events on the recorder
         * 
         */
    public interface Listener {
	/**
         * Called when recording starts
         * 
         */
	void recordingHasStarted();

	/**
         * Called when recording finishes
         * 
         */
	void recordingHasEnded();
    }

    /**
         * Add a listener
         * 
         * @param listener
         *                The listener
         * 
         */
    public void addListener(Listener listener);

    /**
         * Remove a listener
         * 
         * @param listener
         *                The listener
         * 
         */
    public void removeListener(Listener listener);

    /**
         * Get an AudioInputStream for the specified format (or something
         * close).
         * 
         * @param desiredAudioFormat
         *                The audio format desired
         * 
         * @return An AudioInputStream.
         * 
         */
    public AudioInputStream getAudioInputStream(AudioFormat desiredAudioFormat)
	    throws LineUnavailableException;

    /**
         * Returns true if recording is in progress
         * 
         */
    public boolean isRecording();

    /**
         * Stops recording if it is in progress.
         * 
         */
    public void closeLine();

}

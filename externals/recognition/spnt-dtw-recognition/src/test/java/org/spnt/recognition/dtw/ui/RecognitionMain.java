package org.spnt.recognition.dtw.ui;

import javax.swing.JFrame;

public class RecognitionMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RecognitionAppFrame app = new RecognitionAppFrame();
		app.initialize();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
		app.toFront();
	}

}

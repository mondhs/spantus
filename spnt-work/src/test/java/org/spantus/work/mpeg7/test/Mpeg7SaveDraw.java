package org.spantus.work.mpeg7.test;

import java.net.MalformedURLException;

import javax.swing.JFrame;

import org.spantus.exception.ProcessingException;
import org.spantus.work.util.DrawSignalMpeg7;

public class Mpeg7SaveDraw {
	public void writeMpeg7() throws ProcessingException, MalformedURLException{
		DrawSignalMpeg7 dsm = new DrawSignalMpeg7("../data/text1.wav");
		dsm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dsm.setSize(140, 80);
		dsm.validate();
		dsm.setVisible(true);
		dsm.process();

	}

}

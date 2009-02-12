package org.spantus.work.util;

import javax.swing.JFrame;

import org.spantus.logger.Logger;

public class MultipleReaderSignalDraw {
	Logger log = Logger.getLogger(getClass());
	String path;
	
	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}

	public void process(){
		log.debug("process++++");
		DrawSignalCommon dsc = new DrawSignalCommon(getPath());
		dsc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dsc.setSize(640, 140);
		dsc.validate();
		dsc.setVisible(true);
		dsc.process();
		DrawSignalMpeg7 dsm7 = new DrawSignalMpeg7(getPath());
		dsm7.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dsm7.setSize(640, 140);
		dsm7.validate();
		dsm7.setVisible(true);
		dsm7.process();
		log.debug("process----");
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MultipleReaderSignalDraw draw = new MultipleReaderSignalDraw();
		draw.setPath(args[0]);
		draw.process();
	}

}

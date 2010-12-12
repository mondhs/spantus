/*
 	Copyright (c) 2010 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.work.ui;

import javax.swing.JFrame;

import org.spantus.work.ui.container.SpantusWorkFrame;

public class SpantusWorkMain {
	
	
//	public SpantusWorkMain() {
//		final SplashScreen splash = SplashScreen.getSplashScreen();
//		if (splash == null) {
//			System.out.println("SplashScreen.getSplashScreen() returned null");
//			return;
//		}
//		Graphics2D g = splash.createGraphics();
//		if (g == null) {
//			System.out.println("g is null");
//			return;
//		}
//		for (int i = 0; i < 100; i++) {
//			splash.update();
//			try {
//				Thread.sleep(90);
//			} catch (InterruptedException e) {
//			}
//		}
//		splash.close();
//	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		 String os = System.getProperty("os.name").toLowerCase();
		 if (os.indexOf("mac") != -1) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Spantus");

		 }
		
		SpantusWorkFrame spwork = new SpantusWorkFrame();
		spwork.initialize();
		spwork.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		spwork.setVisible(true);
		spwork.toFront();
	}

}

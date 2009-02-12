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
//		SpantusWorkMain main = new SpantusWorkMain();
		SpantusWorkFrame spwork = new SpantusWorkFrame();
		spwork.initialize();
		spwork.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		spwork.setVisible(true);
		spwork.toFront();
	}

}

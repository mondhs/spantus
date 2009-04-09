package org.spantus.work.ui.services;

import java.awt.Frame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.dto.EnviromentRepresentation;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class SpantusUIServiceImpl {
	/**
	 * 
	 * @param info
	 * @param frame
	 */
	public void setupEnv(SpantusWorkInfo info, Frame frame) {
		EnviromentRepresentation env = info.getEnv();
		if (env == null) {
			SpantusWorkSwingUtils.fullWindow(frame);
			info.setEnv(new EnviromentRepresentation());

			info.getEnv().setLaf(
					UIManager.getLookAndFeel().getClass().getName());
		} else {
			frame.setSize(env.getClientWindow().width,
					env.getClientWindow().height);
			frame.setLocation(env.getLocation());
			if (info.getEnv().getLaf() == null) {
				info.getEnv().setLaf(getDefaultLAF());
			}
			try {
				UIManager.setLookAndFeel(env.getLaf());
				SwingUtilities.updateComponentTreeUI(frame);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		if (info.getEnv().getGrid() == null) {
			info.getEnv().setGrid(Boolean.TRUE);
		}
		if (info.getEnv().getPopupNotifications() == null) {
			info.getEnv().setPopupNotifications(Boolean.TRUE);
		}
	}
	/**
	 * 
	 * @return
	 */
	public String getDefaultLAF() {
		String laf = null;
		// if MS Windows then select LAF Windows as default, if other OS then
		// pick up default
		if (System.getProperties().getProperty("os.name").startsWith("Windows")) {
			for (LookAndFeelInfo lafInfo : UIManager.getInstalledLookAndFeels()) {
				if (lafInfo.getName().equals("Windows")) {
					laf = lafInfo.getClassName();
					break;
				}
			}
			if (laf == null) {
				laf = UIManager.getLookAndFeel().getClass().getName();
			}
		} else {
			laf = UIManager.getLookAndFeel().getClass().getName();
		}
		return laf;

	}
}

package org.spantus.work.ui.services;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.spantus.chart.bean.VectorSeriesColorEnum;
import org.spantus.logger.Logger;
import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.dto.EnviromentRepresentation;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class SpantusUIServiceImpl {
	Logger log = Logger.getLogger(SpantusUIServiceImpl.class);
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
			if (isEmpty(env.getMainWindowDimension())) {
				env.setMainWindowDimension(SpantusWorkSwingUtils.currentWindowSize(
						.75, .75));
				env.setMainWindowState(JFrame.MAXIMIZED_BOTH);
			}
			if (env.getMainWindowState() == JFrame.MAXIMIZED_BOTH) {
				env.setMainWindowDimension(SpantusWorkSwingUtils.currentWindowSize(
						.75, .75));
			}
			frame.setSize(env.getMainWindowDimension().width,
					env.getMainWindowDimension().height);
			frame.setLocation(env.getLocation());
			frame.setExtendedState(env.getMainWindowState());

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
		if (info.getEnv().getAutoSegmentation() == null) {
			info.getEnv().setAutoSegmentation(Boolean.TRUE);
		}
		if (info.getEnv().getVectorChartColorTypes() == null) {
			info.getEnv().setVectorChartColorTypes(VectorSeriesColorEnum.blackWhite.name());
		}
		if (info.getEnv().getAdvancedMode() == null) {
			info.getEnv().setAdvancedMode(Boolean.FALSE);
		}
//		if (info.getEnv().getSpantusVersion() == null) {
			info.getEnv().setSpantusVersion(getVersion());
//		}

		Locale.setDefault(info.getLocale());

	}
	
	
	
	protected String getVersion(){
		String version = "N/A"; 	
		log.error("version not set. trying read from eclipse");
			try {
				Properties prop = new Properties();
				InputStream is = new FileInputStream(new File("./target/maven-archiver/pom.properties"));
				prop.load(is);
				version = prop.getProperty("version");
			} catch (IOException e) {
				log.debug("version for eclipse not found",e);
			}catch (NullPointerException e) {
				log.debug("version for eclipse not found",e);
			}
			if(version == null){
				log.error("version not set. trying read from jar");
				try {
					Properties prop = new Properties();
					InputStream is = this.getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.spantus/spnt-work-ui/pom.properties");
					prop.load(is);
					version = prop.getProperty("version");
				} catch (IOException e) {
					log.debug("version for jad not found",e);
				}catch (NullPointerException e) {
					log.debug("version for jar not found",e);
				}

			}
			if(version == null){
				log.error("version not set. trying read from properties");
//				version = getMessage("spantus.work.ui.version");
			}

		return version;
	}

	protected boolean isEmpty(Dimension d) {
		return d == null || d.width == 0 || d.height == 0;
	}

	protected boolean isEmpty(Point d) {
		return d == null || d.x == 0 || d.y == 0;
	}

	public void saveEnv(SpantusWorkInfo info, Frame frame) {
		info.getEnv().setMainWindowDimension(frame.getSize());
		info.getEnv().setLocation(frame.getLocation());
		info.getEnv().setMainWindowState(frame.getExtendedState());
		
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

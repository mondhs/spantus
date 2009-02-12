package org.spantus.chart.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.exception.ProcessingException;

public abstract class ChartUtils {
	/**
	 * 
	 * @param chart
	 * @param file
	 * @throws IOException
	 */
	public static void writeAsPNG(AbstractSwingChart chart, File file)
			throws IOException {
		if(null == chart || !chart.isDisplayable()){
			throw new ProcessingException("Not displayable", null);
		}
		BufferedImage buffer = new BufferedImage(chart.getWidth(), chart
				.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = buffer.createGraphics();
		chart.print(graphics);
		graphics.dispose();
		boolean writerAvailable = ImageIO.write(buffer, "PNG", file);
		if (!writerAvailable) {
			String message = "This Java runtime doesn't support PNG.";
			String title = "PNG export failed";
			JOptionPane.showMessageDialog(chart, message, title,
					JOptionPane.ERROR_MESSAGE);
		}
	}
}

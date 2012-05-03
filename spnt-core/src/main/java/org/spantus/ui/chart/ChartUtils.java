package org.spantus.ui.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.spantus.core.FrameVectorValues;
import org.spantus.exception.ProcessingException;

public final class ChartUtils {

	public enum ChartScale {
		linear, sqrt
	}

	private ChartUtils() {
	}

	/**
	 * 
	 * @param chart
	 * @param file
	 * @throws IOException
	 */
	public static void writeAsPNG(JPanel chart, File file) throws IOException {
		if (null == chart || !chart.isDisplayable()) {
			throw new ProcessingException("Not displayable", null);
		}
		BufferedImage buffer = new BufferedImage(chart.getWidth(),
				chart.getHeight(), BufferedImage.TYPE_INT_ARGB);
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

	public static BufferedImage createImage(FrameVectorValues vals,
			ChartScale pChartScale, ColorLookup pColorLookup) {
		int height = vals.get(0).size() == 0 ? 1 : vals.get(0).size();
		int width = vals.size();
		BufferedImage anImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Double minVal = vals.getMinValue();
		Double deltaVal = vals.getMaxValue() - minVal;
		Object pixelTmp = null;
		int x = 0, y = 0;
		for (List<Double> fv : vals) {
			y = 0;
			LinkedList<Double> lf = new LinkedList<Double>(fv);
			for (ListIterator<Double> iterator2 = lf.listIterator(fv.size()); iterator2
					.hasPrevious();) {
				Double val = (Double) iterator2.previous();
				int pixelCode = lookupColor(val, minVal,
						deltaVal, pChartScale, pColorLookup);
				pixelTmp = anImage.getColorModel().getDataElements(
						pixelCode, pixelTmp);
				anImage.getRaster().setDataElements(x, y++, pixelTmp);
			}
			x++;
		}
		return anImage;
	}

	/**
	 * 
	 * @param costMatrix
	 * @param stats
	 * @param pChartScale
	 * @param pColorLookup
	 * @return
	 */
	public static BufferedImage createImage(RealMatrix costMatrix,
			StatisticalSummary stats, ChartScale pChartScale,
			ColorLookup pColorLookup) {
		int rows = costMatrix.getRowDimension();
		int cols = costMatrix.getColumnDimension();
		BufferedImage anImage = new BufferedImage(cols,rows,
				BufferedImage.TYPE_INT_RGB);
		Double minVal = stats.getMin();
		Double deltaVal = stats.getMax() - minVal;
		Object pixelTmp = null;
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				Double val = costMatrix.getEntry(y, x);
				if (val.isInfinite()) {
					val = stats.getMax();
				}
				int pixelCode = lookupColor(val, minVal, deltaVal,
						pChartScale, pColorLookup);
				pixelTmp = anImage.getColorModel().getDataElements(
						pixelCode, pixelTmp);
				anImage.getRaster().setDataElements(x, y, pixelTmp);
			}
		}
		return anImage;
	}

	public static int lookupColor(Double floatValue, Double min, Double delta,
			ChartScale chartScale, ColorLookup colorLookup) {
		Double fColor = ((Double) (floatValue - min) / (delta));
		if (ChartScale.sqrt.equals(chartScale)) {
			fColor = Math.sqrt(fColor);
		}
		short s = (short) (256 * fColor);
		Color clr = colorLookup.lookup(s);
		return clr.getRGB();
	}

}

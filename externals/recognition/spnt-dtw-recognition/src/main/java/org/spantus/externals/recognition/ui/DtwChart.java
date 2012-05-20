package org.spantus.externals.recognition.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.spantus.core.beans.I18n;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.ui.chart.ChartUtils;
import org.spantus.ui.chart.ChartUtils.ChartScale;
import org.spantus.ui.chart.ColorLookup;
import org.spantus.ui.chart.VectorSeriesColorEnum;

public class DtwChart extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4977123531832394008L;
	private String key;
	private RecognitionResultDetails result;
	RealMatrix costMatrix;
	StatisticalSummary stats;
	private BufferedImage theImage;
	private I18n i18n;

	public DtwChart(String key, RecognitionResultDetails result, I18n i18n) {
		super();
		this.key = key;
		this.result = result;
		costMatrix = result.getCostMatrixMap().get(key);
		stats = result.getStatisticalSummaryMap().get(key);
		theImage = ChartUtils.createImage(costMatrix, stats, ChartScale.linear,
				new ColorLookup(VectorSeriesColorEnum.blackWhite));
		this.i18n = i18n;
		setBorder(new LineBorder(Color.GRAY, 1));
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		FontMetrics fm = g2d.getFontMetrics(g2d.getFont());

		List<Point> points = result.getPath().get(key);
		// Point firstPoint = list.get(0);
		Point lastPoint = points.get(points.size() - 1);
//		
		Dimension dimension = getSize();
		Graphics2D imageG2d = theImage.createGraphics();

		imageG2d.setColor(Color.red);
		drawPaths(imageG2d, points);

		int space = fm.getHeight()+20;
		g.drawImage(theImage, space, space, dimension.width - (2*space),
				dimension.height - (2*space), null);
		
//		g.drawImage(theImage, space, space, theImage.getWidth() - (2*space),
//				theImage.getHeight()  - (2*space), null);
		
		g2d.setColor(Color.red);
		String zero = "0";
		String targetLength = ""
				+ Math.round(result.getTargetLegths().get(key));
		String sampleLength = ""
				+ Math.round(result.getSampleLegths().get(key));

		String extractorName = i18n.getMessage(key) + " (ms)";

		g2d.drawString(zero, 3, fm.getHeight());
		g2d.drawString(extractorName, (getWidth()-fm.stringWidth(extractorName)) / 2, fm.getHeight());
		g2d.drawString(sampleLength, getWidth()-fm.stringWidth(targetLength)-3, fm.getHeight());
		g2d.drawString(targetLength, 0, getHeight()-3);
		String str = MessageFormat.format("[{0},{1}]", lastPoint.x , lastPoint.y);
//		int strWidth = fm.stringWidth(str);
		g2d.drawString(str, dimension.width - (2*space),dimension.height - (2*space));
	}

	/**
	 * 
	 * @param g
	 * @param points
	 */
	protected void drawPaths(Graphics2D g2d, List<Point> points) {
		int[] xArr = new int[points.size()];
		int[] yArr = new int[points.size()];
//		Point lastPoint = points.get(points.size() - 1);
//		FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
		int i = 0;
		for (Point p : points) {
			xArr[i] = p.x;
			yArr[i] = p.y;
			i++;
		}
		g2d.drawPolyline(xArr, yArr, xArr.length);
	}

	public I18n getI18n() {
		return i18n;
	}

	public void setI18n(I18n i18n) {
		this.i18n = i18n;
	}

}

package org.spantus.exp.segment.draw;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.spantus.logger.Logger;
import org.spantus.math.DTW;
import org.spantus.math.dtw.DtwInfo;

public class DrawDtw {
	
	Logger log = Logger.getLogger(getClass());
	DtwInfo info;
	Float min;
	Float max;
	
	public DrawDtw(DtwInfo info, Float min, Float max) {
		this.info = info;
		this.min = min;
		this.max = max;
	}
	
	public DrawDtw(DtwInfo info) {
		this.info = info;
		Float[] minMax = new Float[]{Float.MAX_VALUE, -Float.MAX_VALUE};
		for (List<Float> list : info.getDistanceMatrix()) {
			for (Float float1 : list) {
				minMax[0] = Float.isNaN(float1)?minMax[0]:Math.min(minMax[0], float1);
				minMax[1] = Float.isNaN(float1)?minMax[1]:Math.max(minMax[1], float1);
			}
		}
		this.min = minMax[0];
		this.max = minMax[1];
		log.debug("min: " + min + "; max: " + max);
	}
	
	public void showChart(){
		JFrame frame = new JFrame("Dtw chart");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JFreeChart chart = createXYZChart();
		int width = 600;
		int height = 600;
		ChartPanel chartPanel = new ChartPanel(chart, width, height, 16, 16,
				width * 10, height * 10, true, true, true, true, true, true);
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
	}
	
	protected JFreeChart createXYZChart() {
		NumberAxis xAxis = new NumberAxis("Sample");
		NumberAxis yAxis = new NumberAxis("target");
		List<List<Float>> data = info.getDistanceMatrix();
		XYZDataset xyzset = new XYZArrayDataset(data);
		XYPlot plot = new XYPlot(xyzset, xAxis, yAxis, null);
		XYBlockRenderer r = new XYBlockRenderer();
		PaintScale ps = new GrayPaintScale(min,max);
//		LookupPaintScale ps = new LookupPaintScale(0.0, 4.0, Color.WHITE);
//		ps.add(1, Color.red);
//		ps.add(2, Color.green);
//		ps.add(3, Color.gray);

		r.setPaintScale(ps);
		r.setBlockHeight(1.0f);
		r.setBlockWidth(1.0f);
		plot.setRenderer(r);
		JFreeChart chart = new JFreeChart("Chart Title",
				JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		NumberAxis scaleAxis = new NumberAxis("Scale");
		scaleAxis.setUpperBound(100);
		scaleAxis.setAxisLinePaint(Color.white);
		scaleAxis.setTickMarkPaint(Color.white);
		scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 12));
//		PaintScaleLegend legend = new PaintScaleLegend(ps, scaleAxis);
//		legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
//		legend.setPadding(new RectangleInsets(5, 5, 5, 5));
//		legend.setStripWidth(50);
//		legend.setPosition(RectangleEdge.RIGHT);
//		legend.setBackgroundPaint(Color.WHITE);
//		chart.addSubtitle(legend);
		chart.setBackgroundPaint(Color.white);
		return chart;
	}

	private static class XYZArrayDataset extends AbstractXYZDataset {
		private static final long serialVersionUID = 1L;
		List<List<Float>> data;
		int rowCount = 0;
		int columnCount = 0;

		XYZArrayDataset(List<List<Float>> data) {
			this.data = data;
			rowCount = data.size();
			columnCount = data.get(0).size();
		}

		public int getSeriesCount() {
			return 1;
		}

		public Comparable<String> getSeriesKey(int series) {
			return "serie";
		}

		public int getItemCount(int series) {
			return rowCount * columnCount;
		}

		public double getXValue(int series, int item) {
			return (int) (item / columnCount);
		}

		public double getYValue(int series, int item) {
			return item % columnCount;
		}

		public double getZValue(int series, int item) {
			int x = item % columnCount, y = (int) (item / columnCount);
			return data.get(y).get(x);
		}

		public Number getX(int series, int item) {
			int x = (int) (item / columnCount);;
			return x;
		}

		public Number getY(int series, int item) {
			int y = (int) (item % columnCount);
			return y;
		}

		public Number getZ(int series, int item) {
			int x = item % columnCount, y= (int) (item / columnCount);
			return data.get(y).get(x);
		}
	}

	public static void main(String[] args) {
		Float[][] sampleArr = new Float[][]{
				new Float[]{1f, 2f, 1f, 4f, 4f, 5f,},
				new Float[]{2f, 3f, 5f, 7f, 8f, 9f},
				new Float[]{1f, 1f, 2f, 3f, 4f, 5f, 5f}
		};
		List<Float> target = new ArrayList<Float>(Arrays.asList(sampleArr[1]));
		List<Float> sample1 = new ArrayList<Float>(Arrays.asList(sampleArr[0]));
		DtwInfo info = DTW.createDtwInfo (target, sample1);

		DrawDtw demo = new DrawDtw(info);
		demo.showChart();
	}
}
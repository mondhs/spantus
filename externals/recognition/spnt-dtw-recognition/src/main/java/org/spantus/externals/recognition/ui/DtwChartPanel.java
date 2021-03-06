/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
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
package org.spantus.externals.recognition.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.spantus.core.beans.I18n;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.RecognitionResultDetails;

/**
 * 
 * @author mondhs
 */
public class DtwChartPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RecognitionResultDetails recognitionResult;
	private String selctedFeatureId;
	private Map<String, DtwChart> charts = new LinkedHashMap<String, DtwChart>();
	private I18n i18n; 

	public DtwChartPanel(I18n i18n) {
		setPreferredSize(new Dimension(400, 100));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.i18n = i18n;
	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
		for (DtwChart iChart : charts.values()) {
			iChart.invalidate();
		}
	}
	
	
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setBackground(Color.white);
		g2d.fillRect(0, 0, getWidth(),getHeight());
		g2d.dispose();
	}

	public void repaintCharts(
			RecognitionResult recognitionResult,
			String selctedFeatureId) {
		this.removeAll();
		charts.clear();
		if (recognitionResult != null) {
			for (Entry<String, List<Point>> paths : recognitionResult.getDetails()
					.getPath().entrySet()) {
				if (selctedFeatureId != null
						&& !selctedFeatureId.equals(paths.getKey())) {
					continue;
				}
				DtwChart chart = new DtwChart(paths.getKey(),
						recognitionResult.getDetails(), i18n);
				chart.setAlignmentX(Component.CENTER_ALIGNMENT);
				charts.put(paths.getKey(), chart);
				this.add(Box.createRigidArea(new Dimension(5, 5)));
				this.add(chart);
				this.add(Box.createRigidArea(new Dimension(5, 5)));
			}
		}
		updateUI();
		repaint(30L);
	}

	public RecognitionResultDetails getRecognitionResult() {
		return recognitionResult;
	}

	public void setRecognitionResult(RecognitionResultDetails recognitionResult) {
		this.recognitionResult = recognitionResult;
		this.selctedFeatureId = null;
	}

	void setRecognitionResult(
			RecognitionResultDetails recognitionResultDetails,
			String selctedFeatureId) {
		this.selctedFeatureId = selctedFeatureId;
	}


	public String getSelctedFeatureId() {
		return selctedFeatureId;
	}

	public void setSelctedFeatureId(String selctedFeatureId) {
		this.selctedFeatureId = selctedFeatureId;
	}
}

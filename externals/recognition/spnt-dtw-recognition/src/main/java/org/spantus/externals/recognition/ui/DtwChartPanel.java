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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JPanel;

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

    public DtwChartPanel() {
        setPreferredSize(new Dimension(400, 100));
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2d;
        g2d = (Graphics2D) g.create();
        g2d.setBackground(Color.white);
        g2d.translate(0, getHeight());
        g2d.rotate(Math.toRadians(-90));
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, getHeight(), getWidth());
        g2d.setColor(Color.red);

        FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
        
        g2d.translate(20, fm.stringWidth("1000ms"));

        if(recognitionResult != null){
//            g2d.scale(1.9, 1.9);
            for (Entry<String, List<Point>> detail : recognitionResult.getPath().entrySet()) {
                if(!(selctedFeatureId == null || detail.getKey().equals(selctedFeatureId))){
                    continue;
                }
                List<Point> list = detail.getValue();
                Point firstPoint = list.get(0);
                Point lastPoint = list.get(list.size()-1);
                g2d.setColor(Color.red);
                drawPaths(g2d, detail.getValue());

                g2d.setColor(Color.gray);
                g2d.drawRect(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y);
                g2d.rotate(Math.toRadians(90));
                String targetLength = ""+Math.round(recognitionResult.getTargetLegths().get(detail.getKey()));
                String sampleLength = ""+Math.round(recognitionResult.getSampleLegths().get(detail.getKey()));
                
                String zero = "0 ms";
                g2d.drawString(zero, -fm.stringWidth(zero), 0 );
                g2d.drawString(detail.getKey().replace("_EXTRACTOR", ""), firstPoint.x+10, firstPoint.y -10);
                g2d.drawString(sampleLength, -fm.stringWidth(sampleLength), -lastPoint.x );
                g2d.drawString(targetLength, lastPoint.y, -firstPoint.x );
                g2d.rotate(Math.toRadians(-90));
                g2d.translate(lastPoint.x+40, .1);

            }
        }
         // done with g2d, dispose it
        g2d.dispose();
    }

    /**
     *
     * @param g
     * @param points
     */
    protected void drawPaths(Graphics2D g, List<Point> points) {
        int[] xArr = new int[points.size()];
        int[] yArr = new int[points.size()];
        int i = 0;
        for (Point p : points) {
            xArr[i] = p.x;
            yArr[i] = p.y;
            i++;
        }
        g.drawPolyline(xArr, yArr, xArr.length);
    }
    public RecognitionResultDetails getRecognitionResult() {
        return recognitionResult;
    }

    public void setRecognitionResult(RecognitionResultDetails recognitionResult) {
        this.recognitionResult = recognitionResult;
        this.selctedFeatureId = null;
    }

    void setRecognitionResult(RecognitionResultDetails recognitionResultDetails, String selctedFeatureId) {
        this.selctedFeatureId = selctedFeatureId;
    }
}

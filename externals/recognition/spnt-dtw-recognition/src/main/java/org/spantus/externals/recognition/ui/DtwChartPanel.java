/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.externals.recognition.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;

/**
 *
 * @author mondhs
 */
public class DtwChartPanel extends JPanel {

    private Map<String, List<Point>> pointMap;

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

         g2d.translate(10, 10);

        if(pointMap != null){
            g2d.scale(1.9, 1.9);
            for (Entry<String, List<Point>> detail : pointMap.entrySet()) {
                List<Point> list = detail.getValue();
                Point firstPoint = list.get(0);
                Point lastPoint = list.get(list.size()-1);
                g2d.setColor(Color.red);
                drawPaths(g2d, detail.getValue());

                g2d.setColor(Color.gray);
                g2d.drawRect(firstPoint.x, firstPoint.y, lastPoint.x, lastPoint.y);
                g2d.rotate(Math.toRadians(90));
                g2d.drawString(detail.getKey().replace("_EXTRACTOR", ""), firstPoint.x+20, firstPoint.y -20);
                g2d.rotate(Math.toRadians(-90));
                int height = getHeight();
                g2d.translate(lastPoint.x+10, .1);

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

    public Map<String, List<Point>> getPointMap() {
        return pointMap;
    }

    public void setRecognitionResultDetails(Map<String, List<Point>> pointMap) {
        this.pointMap = pointMap;
    }
}

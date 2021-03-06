/*
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://spantus.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 */
package org.spantus.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

import net.quies.math.plot.AxisInstance;
import net.quies.math.plot.CoordinateBoundary;
import net.quies.math.plot.GraphInstance;
import net.quies.math.plot.InteractiveGraph;
import net.quies.math.plot.YAxis;

import org.spantus.chart.bean.ChartInfo;
import org.spantus.utils.Assert;

/**
 * 
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * 
 *        Created 2008.04.11
 * 
 */
public class InteractiveChart extends InteractiveGraph {
	private SpantusChartToolbar spntToolbar;
	private List<ChartDescriptionResolver> resolvers;
	private final Color SELECTION_COLOR = UIManager.getColor("textHighlight");
        private BufferedImage backBuffer;
        private Dimension size;




	private Point currentMousePoint;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InteractiveChart() {
		super(new XAxisGrid(), new YAxis(), new Insets(0, 0, 65, 0));
	}

	
        @Override
	public SpantusChartToolbar getToolBar() {
		if (spntToolbar == null) {
			spntToolbar = new SpantusChartToolbar(this);
		}
		return spntToolbar;
	}
	/**
	 * 
	 */
	@Override
	public void paintChildren(Graphics g) {
		super.paintChildren(g);
		if(currentMousePoint!=null){
			g.setColor(SELECTION_COLOR.darker().darker());
			g.drawLine(currentMousePoint.x, getGraphTop(), currentMousePoint.x, getGraphHeight());
//			g.fillOval(currentMousePoint.x-2, currentMousePoint.y-2, 4, 4);
		}
	}
        
//        @Override
//        public void paint( Graphics g ){
//            super.paint(g);
//        }
        
        public void resetBackBuffer(){
            backBuffer = null;
        }
        /**
         * A back buffer, used to reduce flicker during painting
         */
        @Override
        public void paintComponent(Graphics g) {
//            Dimension currentSize = getSize();
//            if (size == null || backBuffer == null || currentSize.width != size.width || currentSize.height != size.height) {
//                size = null;
//            }
//        }
//        @Override
//        public void paint(Graphics g) {
//            super.paint(g);
            
            boolean repaint = false;
            // Get the size of our component
            Dimension currentSize = getSize();
            // See if we need to recompute the rectangles that host our cylon bars
            if (backBuffer == null ||size == null 
                    || currentSize.width != size.width || currentSize.height != size.height) {
                // Update our size
                size = currentSize;
                repaint = true; 
            }
            Graphics2D g2 = null;
            if (repaint) {
                // We have to create a new back buffer if this is the first time we're running, one does not exist
                // or we've resized...
                backBuffer = new BufferedImage(currentSize.width, currentSize.height, BufferedImage.TYPE_USHORT_565_RGB);
                g2 = (Graphics2D) backBuffer.getGraphics();
                try {
//                    if (isOpaque()) {
                        g2.setBackground(getBackground());
                        g2.clearRect(0, 0, size.width, size.height);
//                    }

                    GraphInstance instance = getRender();	// thread-safe
                    if (instance == null) {
                        return;
                    }

                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    instance.paint(g2);
                } finally {
                    //                g2.dispose();
                }
            }
            g.drawImage(backBuffer, 0, 0, size.width, size.height, null);
        }

	@Override
	public void mouseMoved(MouseEvent event) {
		super.mouseMoved(event);
		currentMousePoint = event.getPoint();
		repaint(30L);
	}
	
	
	/**
	 * calculate Tooltip text
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		GraphInstance render = getRender();
		if (render == null)
			return "";
		Point mousePosition = event.getPoint();
		StringBuffer buffer = new StringBuffer(80);
		buffer.append("<html><body>");
//		buffer.append('(');
		buffer.append("Time: ");
		buffer.append(MessageFormat.format("{0,number,#}",
				render.getXValue(mousePosition.x)));
		buffer.append("ms, \n ");
		buffer.append(resolveChart(mousePosition.x, mousePosition.y).replaceAll("\n", "<br\\>"));
//		buffer.append(')');
		buffer.append("</body></html>");
		return buffer.toString();
	}
	/**
	 * Resolve Chart
	 * @param y
	 * @return
	 */
	private String resolveChart(int x, int y) {
		GraphInstance render = getRender();
		List<String> descs = new ArrayList<String>();
		for (ChartDescriptionResolver resolver : getResolvers()) {
			ChartDescriptionInfo resolved = resolver
					.resolve(render.getXValue(x).longValue(),
							render.getYValue(y).floatValue());
			if (resolved != null) {
				descs.add("\n"+resolved.getName()+": "+resolved.getValue());
			}
		}
		if (descs.size() == 0) {
			return getYAxis().getFormat().format(render.getYValue(y));
		}
		return descs.size()==0?"":descs.get(0);
	}
	/**
	 * Getter 
	 * @return
	 */
	public List<ChartDescriptionResolver> getResolvers() {
		if (resolvers == null) {
			resolvers = new ArrayList<ChartDescriptionResolver>();
		}
		return resolvers;
	}
	/**
	 * add Chart Description Resolver
	 * 
	 * @param resolver
	 */
	public void addResolver(ChartDescriptionResolver resolver) {
		getResolvers().add(resolver);
	}

	public void mousePressed(MouseEvent event) {
		if(spntToolbar == null || spntToolbar.getChartInfo().isSelfZoomable()){
			super.mousePressed(event);
			return;
		}
		int modifiers = event.getModifiers();
		if ((modifiers & InputEvent.BUTTON1_MASK) != 0){
				setZoomSelection(new SpantusChartZoomSelection(this, event.getPoint()));
		}
	}
	/**
	 * 
	 */
	public void mouseReleased(MouseEvent event) {
		//if isnot set controlling toolbar then used default impl
		if(spntToolbar == null || spntToolbar.getChartInfo().isSelfZoomable()){
			super.mouseReleased(event);
			return;
		}
		SpantusChartZoomSelection spntZoomSelection = (SpantusChartZoomSelection)getZoomSelection();
		int modifiers = event.getModifiers();
		if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
			if (getZoomSelection() != null) {
				boolean selected = spntZoomSelection.apply();
				if (selected)
					getToolBar().setZoom(
							spntZoomSelection.getIntervalDescription());
				else
					getToolBar().setZoomPending(null);
				 notifyZoomListeners(spntZoomSelection.getZoomDomain());
			}
		}
	}
	
	public void changeSelection(int start, int length){
		Point p1 = new Point(start,10);
		Point p2 = new Point(start+length,10);
		SpantusChartZoomSelection selection = null;
		
		if(getZoomSelection() == null || 
				!(selection instanceof SpantusChartZoomSelection)){
			selection = new SpantusChartZoomSelection(this, p1);
			setZoomSelection(selection);
		}else{
			selection = (SpantusChartZoomSelection)getZoomSelection();
		}
//		if(getZoomSelection().getOrigin() == null){
		selection.setOrigin(p1);
//		}
//		if(getZoomSelection().getCurrent() == null){
		selection.setCurrent(p2);
//		}
		
//		selection.setMousePosition(p2);
		
		getToolBar().setZoomPending(null);
		getZoomSelection().apply();
		notifyZoomListeners(selection.getZoomDomain());
		repaint(1030L);
		
	}
	/**
	 * 
	 * @return
	 */
	protected int getGraphWidth(){
		Insets border = getInsets();
		Insets padding = getPadding();	// thread-safe
		int right = border.right + padding.right;
		int left = border.left + padding.left;
		int width = getWidth() - left - right;
		return width;
	}
	protected int getGraphTop(){
		Insets border = getInsets();
		Insets padding = getPadding();	// thread-safe
		int top = border.top + padding.top;
		return top;
	}
	
	protected int getGraphHeight(){
		Insets border = getInsets();
		Insets padding = getPadding();	// thread-safe
		int top = border.top + padding.top;
		int bottom = (border.bottom + padding.bottom)/3;
		int heigth = getHeight() - top -bottom;
		return heigth;
	}

	
	public AxisInstance getXAxisInstance(){
		int width = getWidth();
		width = width == 0 ? 1:width;
		CoordinateBoundary  boundary = getCoordinateBoundary();
		Assert.isTrue(getXAxis()!= null);
		Assert.isTrue(boundary != null);
		if(getXAxis() instanceof XAxisGrid){
			((XAxisGrid)getXAxis()).setGridOn(Boolean.TRUE.equals(getChartInfo().getGrid()));
		}
		
		AxisInstance xAxisInst = getXAxis().getInstance(boundary.getXMin(), boundary.getXMax(), 
				width);
//		BigDecimal xScalar = xAxis.getGraphichsScalar();
		return xAxisInst;
	}


	public ChartInfo getChartInfo() {
		return getToolBar().getChartInfo();
	}




}

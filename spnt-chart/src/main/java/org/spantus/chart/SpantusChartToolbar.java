/**
 * Part of program for analyze speech signal 
 * Copyright (c) 2008 Mindaugas Greibus (spantus@gmail.com)
 * http://code.google.com/p/spantus/
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

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import net.quies.math.plot.Graph;
import net.quies.math.plot.ToolBar;

import org.spantus.chart.bean.ChartInfo;
/**
 * 
 * 
 * @author Mindaugas Greibus
 *
 * @since 0.0.1
 * 
 * Created 2008.04.11
 *
 */
public class SpantusChartToolbar extends ToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -362869299631613281L;
	private JLabel playLabel;
	private Set<SignalSelectionListener> listeners = new HashSet<SignalSelectionListener>();
	private static final Image PLAY_IMAGE = getImageAsResource("/org/spantus/chart/icons/play.gif");
	private ChartInfo chartInfo;
	
	

	SpantusChartToolbar(Graph source) {
		super(source);
		
	}
	
	public Map<JLabel, Image> getToolbarComponents(){
		Map<JLabel, Image> comps = super.getToolbarComponents();
		getPrint().setVisible(getChartInfo().isPrintable());
		getExport().setVisible(getChartInfo().isExportable());
		comps.put(getPlayLabel(),PLAY_IMAGE); 
		return comps;
	
	}
	
	public JLabel getPlayLabel(){
		if(playLabel == null){
			playLabel = new JLabel();
			playLabel.setToolTipText("Play...");
			playLabel.setCursor(ACTION_CURSOR);
			playLabel.addMouseListener(new MouseAdapter() {
				
				public void mouseClicked(MouseEvent event) {
					notifySignalPlay();
				}
			});
			
		}
		playLabel.setVisible(getChartInfo().isPlayable());
		return playLabel;
	}

	public void setZoom(String intervalDescription) {
		super.setZoom(intervalDescription);
		if(!getChartInfo().isSelfZoomable()){
			getAbortZoom().setVisible(false);
		}
	}

	
	public static Image getImageAsResource(String location) {
		try {
			URL url = SpantusChartToolbar.class.getResource(location);
			if (url == null)
				throw new Error(location + " is missing");
			return ImageIO.read(url);
		} catch (IOException e) {
			throw new Error("IOException while retreiving " + location + ":\n\t" + e.getMessage());
		}
	}
	
	public void notifySignalPlay() {
		for (SignalSelectionListener listener : getSignalSelectionListeners()) {
			listener.play();
		}
	}

	public Set<SignalSelectionListener> getSignalSelectionListeners() {
		if (listeners == null) {
			listeners = new HashSet<SignalSelectionListener>();
		}
		return listeners;
	}

	/**
	 * 
	 * @param listener
	 */
	public void addSignalSelectionListener(SignalSelectionListener listener) {
		getSignalSelectionListeners().add(listener);
	}
	public ChartInfo getChartInfo() {
		if(chartInfo == null){
			chartInfo = new ChartInfo();
		}
		return chartInfo;
	}
	public void setCharInfo(ChartInfo info) {
		this.chartInfo = info;
	}
	
}

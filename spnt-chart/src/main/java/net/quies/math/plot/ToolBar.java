package net.quies.math.plot;

/*
Copyright (c) 2007 Pascal S. de Kloe. All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products derived
   from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
OF SUCH DAMAGE.
*/

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 @author Pascal S. de Kloe
 */
public class ToolBar extends JComponent implements ComponentListener {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public 
ToolBar(Graph source) {
	graph = source;
	getAbortZoom().setVisible(false);
	setDoubleBuffered(true);

	setLayout(new FlowLayout(FlowLayout.TRAILING));

	for (JComponent comp : getToolbarComponents().keySet()) {
		add(comp);
	}
	graph.addComponentListener(this);
}
	/**
	 * 
	 * @return
	 */
	public Map<JLabel, Image> getToolbarComponents(){
		Map<JLabel, Image> comps = new LinkedHashMap<JLabel, Image>();
		comps.put(getAbortZoom(),ABORT_ZOOM_IMAGE);
		comps.put(getInterval(), null);
		comps.put(getExport(), EXPORT_IMAGE);
		comps.put(getPrint(), PRINT_IMAGE);
	
		return comps;
	
	}
	/**
	 * 
	 * @return
	 */
	protected JLabel getInterval(){
		if(interval == null){
			interval = new JLabel();
			interval.setOpaque(true);
			int rgb = interval.getBackground().getRGB();
			rgb &= 0x00FFFFFF;	// strip alpha
			rgb |= 0xC0000000;	// make transparent
			interval.setBackground(new Color(rgb, true));

			interval.setToolTipText("Zoom interval");
		}
		return interval;

	}
	/**
	 * 
	 * @return
	 */
	protected JLabel getAbortZoom(){
		if(abortZoom == null){
			abortZoom = new JLabel();
			abortZoom.setToolTipText("Abort Zoom");
			abortZoom.setCursor(ACTION_CURSOR);
			abortZoom.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					int modifiers = event.getModifiers();
					if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
						currentInterval = null;
						getInterval().setText(null);
						getAbortZoom().setVisible(false);

						graph.setEnabled(false);
						graph.setCursor(InteractiveGraph.BUSSY_CURSOR);
						new Thread() {

							public void run() {
								try {
									graph.setDomain(new GraphDomain());
									graph.render();
									graph.repaint();
								} finally {
									graph.setEnabled(true);
									graph.setCursor(InteractiveGraph.DEFAULT_CURSOR);
								}
							}

						}.start();
					}
				}
			});

		}
		return abortZoom;

	}
	/**
	 * 
	 * @return
	 */
	protected JLabel getExport(){
		if(export == null){
			export = new JLabel();
			export.setToolTipText("Export...");
			export.setCursor(ACTION_CURSOR);
			export.addMouseListener(new ExportListener(graph));
		}
		return export;

	}
	/**
	 * 
	 * @return
	 */
	protected JLabel getPrint(){
		if(print == null){
			print = new JLabel();
			print.setToolTipText("Print...");
			print.setCursor(ACTION_CURSOR);
			print.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent event) {
					int modifiers = event.getModifiers();
					if ((modifiers & InputEvent.BUTTON1_MASK) != 0) {
						getPrint().setEnabled(false);
						new Thread() {

							public void run() {
								try {
									RenderUtils.print(graph);
								} finally {
									getPrint().setEnabled(true);
								}
							}

						}.start();
					}
				}

			});
		}
		return print;

	}

	


/**
 * Sets the transparency of the tool bar.
 * Use {@code null} to disable this feature.
 */
void
setAlphaComposite(AlphaComposite composite) {
	transparency = composite;
}


public void
paint(Graphics g) {
	if (transparency != null) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(transparency);
	}
	super.paint(g);
}


/**
 * Doesn't print this component.
 */
public void
print(Graphics g) {
}


public void
setZoomPending(String intervalDescription) {
	if (intervalDescription == null)
		getInterval().setText(currentInterval);
	else
		getInterval().setText(intervalDescription);
}


public void
setZoom(String intervalDescription) {
	currentInterval = intervalDescription;
	getInterval().setText(intervalDescription);
	getAbortZoom().setVisible(true);
}


public void
componentResized(ComponentEvent event) {
	setAppropriateIcons();
}


// Unused interface methods:
public void componentMoved(ComponentEvent event) { };
public void componentShown(ComponentEvent event) { };
public void componentHidden(ComponentEvent event) { };


protected void
setAppropriateIcons() {
	int size = (graph.getWidth() + graph.getHeight()) / 40 + 2;
	for (Entry<JLabel, Image> compEntity : getToolbarComponents().entrySet()) {
		if(compEntity.getValue() == null) continue;
		Image img = compEntity.getValue();
		compEntity.getKey().setIcon(
				new ImageIcon(
						img.getScaledInstance(size, size, Image.SCALE_SMOOTH)
					)
			);

	}

}


public Image
getImage(String location) {
	try {
		URL url = ToolBar.class.getResource(location);
		if (url == null)
			throw new Error(location + " is missing");
		return ImageIO.read(url);
	} catch (IOException e) {
		throw new Error("IOException while retreiving " + location + ":\n\t" + e.getMessage());
	}
}


public static final Cursor ACTION_CURSOR = new Cursor(Cursor.HAND_CURSOR);
public final Image ABORT_ZOOM_IMAGE = getImage("/net/quies/math/plot/icons/fit-width.gif");
public final Image EXPORT_IMAGE = getImage("/net/quies/math/plot/icons/export.gif");
public final Image PRINT_IMAGE = getImage("/net/quies/math/plot/icons/print.gif");

private final Graph graph;
private JLabel interval = null;
private JLabel abortZoom = null;
private JLabel export = null;
private JLabel print = null;

private AlphaComposite transparency = null;
private String currentInterval = null;

}

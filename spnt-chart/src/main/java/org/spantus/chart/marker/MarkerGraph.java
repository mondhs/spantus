package org.spantus.chart.marker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;

import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;

public class MarkerGraph extends JComponent {

	/**
	 * 
	 */
	Map<String, MarkerSetComponent> layers;

	MarkerSetHolder markerSetHolder;
	
	MarkerGraphCtx ctx;

	private static final long serialVersionUID = 1L;

	protected Logger log = Logger.getLogger(getClass());

	public boolean addMarker(float from, float length) {
		return false;
	}

	public void initialize() {
		Assert.isTrue(getMarkerSetHolder() != null, "Should not be null");
		setLayout(null);
		KeyListener listener = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
        		log.error(getName() + ":"+ keyChar);
            }
        };
        this.addKeyListener(listener);


		Map<String, MarkerSet> map = getMarkerSetHolder().getMarkerSets();
		for (Entry<String, MarkerSet> entry : map.entrySet()) {
			MarkerSetComponent comp = createMarkerSetComponent(entry.getValue());
			comp.setName(entry.getKey());
			comp.addMouseListener(getMouseListeners()[0]);
			getLayers().put(entry.getKey(), comp);
			add(comp);
		}

		// addMouseListener(this);
		// addMouseMotionListener(this);

//		KeyboardFocusManager.getCurrentKeyboardFocusManager()
//				.addPropertyChangeListener(new PropertyChangeListener() {
//					public void propertyChange(PropertyChangeEvent e) {
//						String prop = e.getPropertyName();
//						if (e.getNewValue() instanceof MarkerComponent) {
//							((MarkerComponent) e.getNewValue()).repaint();
//						}
//						if (e.getOldValue() instanceof MarkerComponent) {
//							((MarkerComponent) e.getOldValue()).repaint();
//						}
//						 log.debug(prop + ": " + e.getNewValue() + ";" +
//						 e.getOldValue());
//					}
//				});
	}

	public MarkerSetComponent createMarkerSetComponent(MarkerSet markerSet) {
		MarkerSetComponent component = new MarkerSetComponent();
		component.addKeyListener(getKeyListeners()[0]);
		component.setMarkerSet(markerSet);
		component.setCtx(getCtx());
		component.initialize();
		return component;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			if (isOpaque()) {
				g2.setBackground(getBackground());
				g2.clearRect(0, 0, getWidth(), getHeight());
			}
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} finally {
			g2.dispose();
		}
		resize(getSize());
	}

	public void resize(Dimension size){
		int yLayer = 0;
		int layersCount = getLayers().keySet().size();
		layersCount = layersCount == 0?1:layersCount;
		int heightLayer = (getSize().height - 2)
				/ layersCount;
		int widthLayer = getSize().width - 2;
		for (MarkerSetComponent cmp : getLayers().values()) {
			cmp.setCtx(getCtx());
			cmp.setLocation(0, yLayer);
			cmp.changeSize(new Dimension(widthLayer, heightLayer));
			yLayer += heightLayer;
			cmp.repaintIfDirty();
		}
	}

	
//	public void paintChildren(Graphics g) {
//
//
//		super.paintChildren(g);
//		// for (MarkerSetComponent cmp : getLayers().values()) {
//		// yLayer += heightLayer;
//		// Graphics g1 = g.create();
//		// cmp.paintComponent(g1);
//		// }
//
//	}

	public void mouseClicked(MouseEvent e) {
		log.debug("clicked: " + getName() + findComponentAt(e.getPoint()));
	}

	public void mouseEntered(MouseEvent e) {
		// log.debug("entered: " +getName() + e.getPoint());
	}

	public void mouseExited(MouseEvent e) {
		// log.debug("exited: " +getName() + e.getPoint());
	}

	public void mousePressed(MouseEvent e) {
		// log.debug("presed: " +getName() + e.getPoint());
	}

	public void mouseReleased(MouseEvent e) {
		// log.debug("release: " +getName() + e.getPoint());

	}

	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void setMarkerSetHolder(MarkerSetHolder markerSetHolder) {
		this.markerSetHolder = markerSetHolder;
	}

	public MarkerSetHolder getMarkerSetHolder() {
		return markerSetHolder;
	}

	public Map<String, MarkerSetComponent> getLayers() {
		if (layers == null) {
			layers = new LinkedHashMap<String, MarkerSetComponent>();
		}
		return layers;
	}
	
//	public Float calculateScale(){
//		Float widthTime = getMarkerSetHolder().getPeriodEnd().add(getMarkerSetHolder().getPeriodStart().negate()).floatValue();
//		Float scale = widthTime.floatValue()/Float.valueOf(getSize().width);
//		return scale * 1f;
//	}

	public MarkerGraphCtx getCtx() {
		if(ctx == null){
			ctx = new MarkerGraphCtx();
			ctx.setXScalar(BigDecimal.valueOf(1));
		}
		return ctx;
	}

	public void setCtx(MarkerGraphCtx ctx) {
		this.ctx = ctx;
	}

}

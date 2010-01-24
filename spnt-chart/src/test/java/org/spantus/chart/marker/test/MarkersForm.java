package org.spantus.chart.marker.test;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.spantus.chart.marker.MarkerComponent;
import org.spantus.chart.marker.MarkerGraph;
import org.spantus.chart.marker.MarkerSetComponent;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;

public class MarkersForm extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MarkerGraph markerGraph;
	protected Logger log = Logger.getLogger(getClass());

	public MarkersForm() {
		markerGraph = new MarkerGraph();
		markerGraph.getCtx().setXScalar(BigDecimal.valueOf(.009));
		getContentPane().add(markerGraph);
		addMouseListener(createMouseListener());

	}

	public void initialize() {
		markerGraph.setMarkerSetHolder(createMarkerSetHolder());
		KeyListener listener = new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				log.error(e.toString());
			}
		};
		this.addKeyListener(listener);
		
		markerGraph.addMouseListener(getMouseListeners()[0]);
		markerGraph.initialize();

	}

	public MarkerSetHolder createMarkerSetHolder() {
		MarkerSetHolder holder = new MarkerSetHolder();
		
		MarkerSet markerSet = new MarkerSet();
		Marker marker = new Marker();
		marker.setStart(0L);
		marker.setLength(900L);
		marker.setLabel("Test1");
		markerSet.getMarkers().add(marker);

		marker = new Marker();
		marker.setStart(1100L);
		marker.setLength(900L);
		marker.setLabel("Test11");
		markerSet.getMarkers().add(marker);

		marker = new Marker();
		marker.setStart(2100L);
		marker.setLength(900L);
		marker.setLabel("Test12");
		markerSet.getMarkers().add(marker);

		marker = new Marker();
		marker.setStart(3100L);
		marker.setLength(900L);
		marker.setLabel("Test13");
		markerSet.getMarkers().add(marker);

		
		holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), markerSet);

		markerSet = new MarkerSet();
		marker = new Marker();
		marker.setStart(100L);
		marker.setLength(600L);
		marker.setLabel("Test2");
		markerSet.getMarkers().add(marker);

		marker = new Marker();
		marker.setStart(1700L);
		marker.setLength(1900L);
		marker.setLabel("Test21");
		markerSet.getMarkers().add(marker);
		holder.getMarkerSets().put(MarkerSetHolderEnum.phone.name(), markerSet);

		return holder;
	}

//	@Override
//	public void paint(Graphics g) {
//		super.paint(g);
//		Dimension clientSize = new Dimension(getSize());
//		clientSize.setSize(clientSize.width - 10, clientSize.height - 30);
//		markerGraph.setSize(clientSize);
//		markerGraph.setPreferredSize(clientSize);
//
//	}

	public static void main(String[] args) {
		MarkersForm demo = new MarkersForm();
		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.initialize();
		demo.setSize(640, 100);
		demo.validate();
		demo.setVisible(true);
	}
	protected MouseListener createMouseListener(){
	    ActionListener actionListener = new ActionListener() {
	        public void actionPerformed(ActionEvent actionEvent) {
//	        	Object source = actionEvent.get();
	        	if("+".equals(actionEvent.getActionCommand())){
	    			Object source = actionEvent.getSource();
	    			JPopupMenu parent = (JPopupMenu)((JComponent)source).getParent();
	    			Component invoker = parent.getInvoker();
	    			if(invoker instanceof MarkerSetComponent){
	    				MarkerSetComponent _markerSetComponent = ((MarkerSetComponent)invoker);
	    				MouseListener ml =  getMouseListeners()[0];
	    				if(ml instanceof MarkerFormPopupMenuShower){
	    					Point p = ((MarkerFormPopupMenuShower)ml).currentPoint;
	    					float x = (float)p.getX()*_markerSetComponent.getCtx().getXScalar().floatValue();
	    					Marker marker = new Marker();
	    					marker.setLabel(p.toString());
	    					Float start = x*1000;
	    					marker.setStart(Long.valueOf(start.longValue()));
	    					marker.setLength(80L);
	    					_markerSetComponent.getMarkerSet().getMarkers().add(marker);
		    				_markerSetComponent.repaint();
	    				}	    				
	    			}
	    			log.debug("Add to " + invoker );
	        	}else if("-".equals(actionEvent.getActionCommand())){
	    			Object source = actionEvent.getSource();
	    			JPopupMenu parent = (JPopupMenu)((JComponent)source).getParent();
	    			Component invoker = parent.getInvoker();
	    			if(invoker instanceof MarkerSetComponent){
	    				MarkerSetComponent _markerSetComponent = ((MarkerSetComponent)invoker);
	    				MouseListener ml =  getMouseListeners()[0];
	    				if(ml instanceof MarkerFormPopupMenuShower){
		    				Marker _marker = ((MarkerFormPopupMenuShower)ml).currentMarker.getMarker();
		    				_markerSetComponent.getMarkerSet().getMarkers().remove(_marker);
		    				log.debug("mark as removed: " + _marker);
		    				_markerSetComponent.repaint();

	    				}
	    			}
	        		
	        	}
	          log.debug("Selected: "
	              + actionEvent.getActionCommand() + "; source: " );
	        }
	     };
		
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(popupMenuListener);
		MouseListener mouseListener = new MarkerFormPopupMenuShower(popupMenu);
		JMenuItem menuItem = new JMenuItem("+");
		menuItem.addActionListener(actionListener);
		popupMenu.add(menuItem);
		menuItem = new JMenuItem("-");
		menuItem.addActionListener(actionListener);
		popupMenu.add(menuItem);
		return mouseListener;

	}
	
	PopupMenuListener popupMenuListener = new PopupMenuListener() {
		public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
			System.out.println("Canceled");
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
			System.out.println("Becoming Invisible: ");
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
			System.out.println("Becoming Visible: ");
		}
	};

	class MarkerFormPopupMenuShower extends MouseAdapter {

		private JPopupMenu popup;
		
		MarkerComponent currentMarker;

		Point currentPoint;
		
		public MarkerFormPopupMenuShower(JPopupMenu popup) {
			this.popup = popup;
		}

		private void showIfPopupTrigger(MouseEvent mouseEvent) {
			if (popup.isPopupTrigger(mouseEvent)) {
				JComponent source = (JComponent)mouseEvent.getSource();
				if(source instanceof MarkerSetComponent){
					Component currentComponent = source.findComponentAt(mouseEvent.getPoint());
					if(currentComponent instanceof MarkerComponent){
						currentMarker = (MarkerComponent)currentComponent;
//						((MarkerSetComponent)source).setCurrentMarkerComponent((MarkerComponent)currentComponent);
					}
				}
				currentPoint = mouseEvent.getPoint();
				popup.show((JComponent)mouseEvent.getSource(), mouseEvent.getX(),
						mouseEvent.getY());
			}
		}

		public void mousePressed(MouseEvent mouseEvent) {
			showIfPopupTrigger(mouseEvent);
		}

		public void mouseReleased(MouseEvent mouseEvent) {
			showIfPopupTrigger(mouseEvent);
		}
	}

}

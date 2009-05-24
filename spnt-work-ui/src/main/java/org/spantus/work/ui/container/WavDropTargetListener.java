package org.spantus.work.ui.container;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * 
 * @author Mindaugas Greibus
 * b
 * @since 0.0.1
 *
 */
public class WavDropTargetListener implements DropTargetListener {
	
	private Logger log = Logger.getLogger(WavDropTargetListener.class);

	private SpantusWorkCommand handler;
	private SpantusWorkInfo info;
	
	/**
	 * 
	 * @param handler
	 * @param info
	 */
	public WavDropTargetListener(SpantusWorkCommand handler,
			SpantusWorkInfo info) {
		this.handler = handler;
		this.info = info;
	}
	/**
	 * 
	 */
	public void dragEnter(DropTargetDragEvent event) {
		 int a = event.getDropAction();
		 if ((a & DnDConstants.ACTION_LINK) != 0)
		 	event.rejectDrag();

		if (!isDragAcceptable(event)) {
			event.rejectDrag();
			return;
		}
	}
	/**
	 * 
	 */
	public void dragExit(DropTargetEvent event) {
	}
	/**
	 * 
	 */
	public void dragOver(DropTargetDragEvent event) { // you can provide
	}
	/**
	 * 
	 */
	public void dropActionChanged(DropTargetDragEvent event) {
		if (!isDragAcceptable(event)) {
			event.rejectDrag();
			return;
		}
	}
	/**
	 * 
	 */
	public void drop(DropTargetDropEvent event) {
		if (!isDropAcceptable(event)) {
			event.rejectDrop();
			return;
		}

		event.acceptDrop(DnDConstants.ACTION_COPY);

		URL file = getFile(event.getTransferable());
		if (file != null) {
			this.info.getProject().getCurrentSample().setCurrentFile(file);
			this.handler.execute(GlobalCommands.file.currentSampleChanged
						.name(), this.info);
		}
		event.dropComplete(true);
	}
	/**
	 * 
	 * @param event
	 * @return
	 */
	public boolean isDragAcceptable(DropTargetDragEvent event) {
		URL file = getFile(event.getTransferable());
		if (file != null) {
			return true;
		}
		return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
	}
	/**
	 * 
	 * @param transferable
	 * @return
	 */
	protected URL getFile(Transferable transferable) {
		List<URL> files = getFiles(transferable);
		
		if (files == null) return null;
		for (URL file : files) {
			if (isSupportedFile(file)) {
				return file;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param event
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<URL> getFiles(Transferable transferable) {
		List<URL> urls = new LinkedList<URL>();
		try {
			if (transferable
					.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				List<File> files = (List<File>) transferable
				.getTransferData(DataFlavor.javaFileListFlavor);
				for (File file : files) {
					urls.add(file.toURI().toURL());
				}
			}else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String urlString = (String) transferable
						.getTransferData(DataFlavor.stringFlavor);
				StringTokenizer tokens = new StringTokenizer(urlString);
				while (tokens.hasMoreTokens()) {
					String urlEntry = tokens.nextToken();
					URL url = new URL(urlEntry);
					urls.add(url);
				}
				
			}
		} catch (UnsupportedFlavorException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return urls;
	}
	/**
	 * 
	 * @param file
	 * @return
	 */
	protected boolean isSupportedFile(URL file) {
		return file != null && file.getFile().endsWith(".wav");
	}
	/**
	 * 
	 * @param event
	 * @return
	 */
	public boolean isDropAcceptable(DropTargetDropEvent event) {
		return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
	}

}
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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

		File file = getFile(event.getTransferable());
		if (file != null) {
			try {
				this.info.getProject().getCurrentSample().setCurrentFile(
						file.toURI().toURL());
				this.handler.execute(GlobalCommands.file.currentSampleChanged
						.name(), this.info);
			} catch (MalformedURLException e) {
				log.error(e);
			}

		}
		event.dropComplete(true);
	}
	/**
	 * 
	 * @param event
	 * @return
	 */
	public boolean isDragAcceptable(DropTargetDragEvent event) {
		File file = getFile(event.getTransferable());
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
	protected File getFile(Transferable transferable) {
		List<File> files = getFiles(transferable);
		
		if (files == null) return null;
		
		for (File file : files) {
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
	protected List<File> getFiles(Transferable transferable) {
		try {
			if (transferable
					.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return (List<File>) transferable
						.getTransferData(DataFlavor.javaFileListFlavor);
			}
			if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String urls;

				urls = (String) transferable
						.getTransferData(DataFlavor.stringFlavor);
				List<File> files = new LinkedList<File>();
				StringTokenizer tokens = new StringTokenizer(urls);
				while (tokens.hasMoreTokens()) {
					String urlString = tokens.nextToken();
					URL url = new URL(urlString);
					File file = new File(URLDecoder.decode(url.getFile(),
							"UTF-8"));
					files.add(file);
				}
				return files;
			}
		} catch (UnsupportedFlavorException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}

		return null;
	}
	/**
	 * 
	 * @param file
	 * @return
	 */
	protected boolean isSupportedFile(File file) {
		return file != null && file.getName().endsWith(".wav");
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
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
import java.util.List;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.SpantusWorkCommand;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class WavDropTargetListener implements DropTargetListener {
	private Logger log = Logger.getLogger(WavDropTargetListener.class);

	private SpantusWorkCommand handler;
	private SpantusWorkInfo info;
	
	public WavDropTargetListener(SpantusWorkCommand handler, SpantusWorkInfo info) {
		this.handler = handler;
		this.info = info;
	}

	public void dragEnter(DropTargetDragEvent event) {
//		int a = event.getDropAction();
//		if ((a & DnDConstants.ACTION_COPY) != 0)
//			event.rejectDrag();
//		if ((a & DnDConstants.ACTION_MOVE) != 0)
//			log.error("ACTION_MOVE\n");
//		if ((a & DnDConstants.ACTION_LINK) != 0)
//			event.rejectDrag();

		if (!isDragAcceptable(event)) {
			event.rejectDrag();
			return;
		}
	}

	public void dragExit(DropTargetEvent event) {
	}

	public void dragOver(DropTargetDragEvent event) { // you can provide
		// visual
		// feedback here
	}

	public void dropActionChanged(DropTargetDragEvent event) {
		if (!isDragAcceptable(event)) {
			event.rejectDrag();
			return;
		}
	}

	public void drop(DropTargetDropEvent event) {
		if (!isDropAcceptable(event)) {
			event.rejectDrop();
			return;
		}

		event.acceptDrop(DnDConstants.ACTION_COPY);

		File file = getFile(event); 
		if (file != null) {
			try {
				this.info.getProject().getCurrentSample().setCurrentFile(file.toURI().toURL());
				this.handler.execute(GlobalCommands.file.currentSampleChanged.name(), this.info);
			} catch (MalformedURLException e) {
				log.error(e);
			}
			
		}
		event.dropComplete(true);
	}

	
	public boolean isDragAcceptable(DropTargetDragEvent event) { 
		DataFlavor[] flavors = event.getTransferable().getTransferDataFlavors();
		if(flavors.length!=1 || !flavors[0].equals(DataFlavor.javaFileListFlavor)){
			return false;
		}
		File file = getFile(event, flavors[0]);
		if(file == null || !file.getName().endsWith(".wav")){
			return false;
		}
		
		return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
	}
	/**
	 * 
	 * @param event
	 * @param flavor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	File getFile(DropTargetDragEvent event, DataFlavor flavor){
		List<File> fileList;
		try {
			fileList = (List<File>) event.getTransferable().getTransferData(flavor);
			return fileList.iterator().next();
		} catch (UnsupportedFlavorException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}
	/**
	 * 
	 * @param event
	 * @param flavor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	File getFile(DropTargetDropEvent event){
		Transferable transferable = event.getTransferable();

		DataFlavor[] flavors = transferable.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++) {
			DataFlavor d = flavors[i];
			log.error("MIME type=" + d.getMimeType() + "\n");

			try {
				if (d.equals(DataFlavor.javaFileListFlavor)) {
					List<File> fileList = (List<File>) transferable
							.getTransferData(d);
					for (File file : fileList) {
						return file;
					}
				} else{ 
					throw new IllegalArgumentException("Wav is supported only");
				}
			} catch (Exception e) {
				log.error(e);
			}
		}
		return null;
	}
	
	
	
	public boolean isDropAcceptable(DropTargetDropEvent event) { 
		
		return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
	}

	
}
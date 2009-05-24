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
package org.spantus.work.ui.cmd;

import java.awt.Toolkit;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.services.WorkInfoManager;
import org.spantus.work.ui.services.WorkUIServiceFactory;

public class CurrentSampleChangedCmd extends AbsrtactCmd {

	SampleChangeListener lisetener;
	ProcessedFrameLinstener processedFrameLinstener;
	SpantusWorkCommand handler;
	WorkInfoManager workInfoManager;
	
	public CurrentSampleChangedCmd(SampleChangeListener lisetener, ProcessedFrameLinstener processedFrameLinstener, SpantusWorkCommand handler) {
		this.lisetener = lisetener;
		this.processedFrameLinstener = processedFrameLinstener;
		this.handler = handler;
	}

	
	public String execute(SpantusWorkInfo ctx) {
		
		if(ctx.getProject().getCurrentSample().getCurrentFile() != null){
			MarkerSetHolder holder = ctx.getProject().getCurrentSample().getMarkerSetHolder();
			MarkerSet markerSet = new MarkerSet();
			holder.getMarkerSets().clear();
			holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), markerSet);
//			jdk 1.6 only
//			ReadingThread thread = new ReadingThread(ctx);
//			thread.execute();

			Thread thread = new Thread(new ReadingThread(ctx));
			thread.start();
		}
		return null;
	}
	class ReadingThread extends Thread {
		private SpantusWorkInfo ctx;
		
		public ReadingThread(SpantusWorkInfo ctx) {
			this.ctx = ctx;
		}
		public void run() {
			IExtractorInputReader reader = WorkUIServiceFactory.constructReader(ctx, 
					processedFrameLinstener);
			if(reader.getExtractorRegister().size() == 0 && 
					reader.getExtractorRegister3D().size() == 0){
				handler.execute(GlobalCommands.tool.reloadResources.name(), ctx);
				return;
			}
			lisetener.changedReader(reader);
			if(Boolean.TRUE.equals(ctx.getEnv().getPopupNotifications())){
				Toolkit.getDefaultToolkit().beep();
			}
			if(Boolean.TRUE.equals(ctx.getEnv().getAutoSegmentation())){
				handler.execute(GlobalCommands.tool.autoSegmentation.name(), ctx);
			}
			handler.execute(GlobalCommands.tool.reloadResources.name(), ctx);
		}
	}

	public WorkInfoManager getWorkInfoManager() {
		if(workInfoManager == null){
			workInfoManager = WorkUIServiceFactory.createInfoManager();
		}
		return workInfoManager;
	}
	
//	class  ReadingThread extends SwingWorker<Void, Void>{
//		private SpantusWorkInfo ctx;
//		
//		public ReadingThread(SpantusWorkInfo ctx) {
//			this.ctx = ctx;
//		}
//
//		@Override
//		protected Void doInBackground() throws Exception {
//			lisetener.changedReader(WorkUIServiceFactory.constructReader(ctx, 
//					processedFrameLinstener));
//			return null;
//		}
//		
//		public void done() {
//			super.done();
//			try {
//				get();
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			} catch (ExecutionException e) {
//				throw new RuntimeException(e);
//			}
//	    	Toolkit.getDefaultToolkit().beep();
//	    }
		
//	}
	

}

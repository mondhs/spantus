package org.spantus.work.ui.cmd;

import java.awt.Toolkit;

import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.services.WorkUIServiceFactory;

public class CurrentSampleChangedCmd extends AbsrtactCmd {

	SampleChangeListener lisetener;
	ProcessedFrameLinstener processedFrameLinstener;
	
	public CurrentSampleChangedCmd(SampleChangeListener lisetener, ProcessedFrameLinstener processedFrameLinstener) {
		this.lisetener = lisetener;
		this.processedFrameLinstener = processedFrameLinstener;
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
		return GlobalCommands.tool.reloadResources.name();
	}
	class ReadingThread extends Thread {
		private SpantusWorkInfo ctx;
		
		public ReadingThread(SpantusWorkInfo ctx) {
			this.ctx = ctx;
		}
		public void run() {
			lisetener.changedReader(WorkUIServiceFactory.constructReader(ctx, 
					processedFrameLinstener));
			Toolkit.getDefaultToolkit().beep();
		}
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

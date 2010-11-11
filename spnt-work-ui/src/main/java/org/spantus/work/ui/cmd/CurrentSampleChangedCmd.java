/**
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
import java.util.Set;
import org.spantus.core.extractor.IExtractor;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.services.WorkInfoManager;
import org.spantus.work.ui.services.WorkUIServiceFactory;
import org.spantus.work.wav.AudioManagerFactory;

public class CurrentSampleChangedCmd extends AbsrtactCmd {

	protected Logger log = Logger.getLogger(getClass());
//	private SampleChangeListener lisetener;
//	private ProcessedFrameLinstener processedFrameLinstener;
	private WorkInfoManager workInfoManager;
	
	public CurrentSampleChangedCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}

	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.file.currentSampleChanged);
	}
	
	public String execute(SpantusWorkInfo ctx) {
		
		if(ctx.getProject().getSample().getCurrentFile() != null){
			MarkerSetHolder holder = ctx.getProject().getSample().getMarkerSetHolder();
			MarkerSet markerSet = new MarkerSet();
			holder.getMarkerSets().clear();
			holder.getMarkerSets().put(MarkerSetHolderEnum.word.name(), markerSet);
//			jdk 1.6 only
//			ReadingThread thread = new ReadingThread(ctx);
//			thread.execute();

			Thread thread = new ReadingThread(ctx);
			thread.start();
		}
		return null;
	}
	class ReadingThread extends Thread {
		private SpantusWorkInfo ctx;
		
		public ReadingThread(SpantusWorkInfo ctx) {
			this.ctx = ctx;
			setName("Signal Readning");
		}
		public void run() {
			IExtractorInputReader reader;
			try{
				//read changed sample
				reader = WorkUIServiceFactory.read(ctx, getExecutionFacade());
			}catch (ProcessingException e) {
				error(e.getLocalizedMessage(), ctx);
				return;
			}
			if(reader.getExtractorRegister().isEmpty() &&
					reader.getExtractorRegister3D().isEmpty()){
				getExecutionFacade().reload();
				return;
			}
//                        float length = AudioManagerFactory.createAudioManager().findLength(ctx.getProject().getSample().getCurrentFile());
//                        for (IExtractorVector vals : reader.getExtractorRegister3D()) {
//                            Assert.isTrue(Math.abs(length - vals.getOutputValues().getTime())<1,
//                                    "length does not match");
//                        }
//                        for (IExtractor vals : reader.getExtractorRegister()) {
//                            Assert.isTrue(Math.abs(length - vals.getOutputValues().getTime())<1,
//                                    "length does not match");
//                        }
			getExecutionFacade().changedReader(reader);
			if(Boolean.TRUE.equals(ctx.getEnv().getPopupNotifications())){
				Toolkit.getDefaultToolkit().beep();
			}
			if(Boolean.TRUE.equals(ctx.getEnv().getAutoSegmentation())){
				getExecutionFacade().fireEvent(GlobalCommands.tool.autoSegmentation);
			}
			getExecutionFacade().reload();
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

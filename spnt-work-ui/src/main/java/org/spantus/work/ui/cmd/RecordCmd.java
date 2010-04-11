/*
 	Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
 	Part of program for analyze speech signal 
 	http://spantus.sourceforge.net

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package org.spantus.work.ui.cmd;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.swing.JOptionPane;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioCapture;
import org.spantus.core.io.RecordWraperExtractorReader;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;
import org.spantus.work.reader.SupportableReaderEnum;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.services.WorkInfoManager;
import org.spantus.work.ui.services.WorkUIServiceFactory;
import org.spantus.work.ui.util.WorkUIExtractorConfigUtil;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 23, 2010
 *
 */
public class RecordCmd extends AbsrtactCmd {
	

	public static final String recordFinishedMessageHeader = "recordFinishedMessageHeader"; 
	public static final String recordFinishedMessageBody = "recordFinishedMessageBody";
	public static final String recordedSoundSavedMessageBody="recordedSoundSavedMessageBody";
	
	
	protected Logger log = Logger.getLogger(getClass());

	private AudioCapture capture;
//	private SpantusWorkCommand handler;
	

	private Timer timer;

	private SpantusWorkInfo ctx;
	
//	private boolean isRecordInitialyzed = false;
	private WorkInfoManager workInfoManager;
	
	private SampleChangeListener lisetener;

	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.sample.record);
	}
	
	public RecordCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
		this.lisetener = executionFacade;
	}
	
	public String execute(final SpantusWorkInfo ctx) {
		this.ctx = ctx;
		if( getCapture()!=null && getCapture().isRunning()){
//			log.error("is already recording ");
			return null;
		}
		
		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader().getWorkConfig();
		
		
		ctx.getProject().getSample().setCurrentFile(null);
		ctx.getProject().getSample().setFormat(null);
		ctx.getProject().getSample().setMarkerSetHolder(new MarkerSetHolder());
		setCapture(null);
		
		WraperExtractorReader wrapReader = createReader(config);
		
		RecordSegmentatorOnline recordSegmentator =
			createSegmentator(config, wrapReader);
		
		createExtractorForProject(wrapReader, recordSegmentator);

		getCapture().setReader(wrapReader);
		capture.setFormat(getFormat(config));
//		log.error("start capturing");
		ctx.setPlaying(true);
		getTimer().schedule(new UpdateCapture(recordSegmentator), 500L, 250L);
                getTimer().schedule(new UpdateMonitor(recordSegmentator), 0L, 5L);

		getWorkInfoManager().increaseExperimentId(ctx);
		return null;
	}
	
	protected void createExtractorForProject(WraperExtractorReader wrapReader,
			RecordSegmentatorOnline recordSegmentator) {

//		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader()
//				.getWorkConfig();
		IClassifier segmentator = null;
		
		if (ProjectTypeEnum.recordSegmentation.name().equals(ctx.getProject()
				.getType())) {
			for (String extr : ctx.getProject().getFeatureReader()
					.getExtractors()) {
				String[] extractor = extr.split(":");
				SupportableReaderEnum readerType = SupportableReaderEnum
						.valueOf(extractor[0]);
				switch (readerType) {
				case spantus:
					ExtractorEnum extractorType = ExtractorEnum
							.valueOf(extractor[1]);
					segmentator = ExtractorUtils.registerThreshold(wrapReader
							.getReader(), extractorType);
					if(segmentator != null){
						segmentator.addClassificationListener(recordSegmentator);
					}else {
						log.debug("segmentator for {0} not constructed", extractorType);
					}
//					segmentator.setCoef(config.getThresholdCoef());
//					segmentator.setLearningPeriod(config
//							.getThresholdLeaningPeriod().longValue());
					break;
				case mpeg7:
					break;
				default:
					throw new RuntimeException("not impl: " + readerType);
				}
			}
		} else {
			ExtractorEnum extractorType = ExtractorEnum.WAVFORM_EXTRACTOR;
			segmentator = ExtractorUtils.registerThreshold(wrapReader
					.getReader(), extractorType);
//			segmentator.addClassificationListener(recordSegmentator);

		}
	}

	public class UpdateMonitor extends TimerTask {

            private RecordSegmentatorOnline recordSegmentator;


            public UpdateMonitor(RecordSegmentatorOnline recordSegmentator) {
                    super();
			this.recordSegmentator = recordSegmentator;
            }


            @Override
            public void run() {

                if(recordSegmentator.getReader().getAudioBuffer().size()>0){
                        Double dbl = recordSegmentator.getReader().getLastValue().doubleValue();
                        Double sqr_value = dbl * dbl;
                        if(sqr_value != 0){
                            sqr_value = Math.log(sqr_value);
                        }

//                    if(recordSegmentator.getReader().getLastValue()!=null){
//                        log.error("sqr_value: " + sqr_value);
//                    }
                    lisetener.refreshValue(recordSegmentator.getReader().getLastValue());

                }

                if (!ctx.getPlaying()) {
                    this.cancel();
		}


            }

        }
	
	public class UpdateCapture extends TimerTask {
		
		private RecordSegmentatorOnline recordSegmentator;
		private boolean isRecordInitialyzed = false;
		
		public UpdateCapture(RecordSegmentatorOnline recordSegmentator) {
			super();
			this.recordSegmentator = recordSegmentator;
		}

		@Override
		public void run() {
			if(!getCapture().isAlive()){
				capture.start();
//				log.error("isNotRecordInitialyzed " + recordSegmentator.getReader().getAudioBuffer().size());
				return;
			}
			if(!isRecordInitialyzed && recordSegmentator.getReader().getAudioBuffer().size()>0){
				lisetener.changedReader(recordSegmentator.getReader().getReader());
				isRecordInitialyzed =true;
			}else{
				lisetener.refresh();
			}
			if (!ctx.getPlaying()) {
				stop();
				
			}
		}
		/**
		 * 
		 */
		private void stop(){
//			log.error("repaint");
			getCapture().finalize();
//			isRecordInitialyzed = false;
			URL wavFile = null;
			String fullSingalFullPath = getSignalName();
			if(StringUtils.hasText(recordSegmentator.getPath())){
				File dir = FileUtils.checkDirs(recordSegmentator.getPath());
				File file = new File(dir,getSignalName()+".wav");
				if(file.exists()){
					file = new File(dir,getSignalName()+"-"+System.currentTimeMillis()+".wav");
				}
				wavFile = recordSegmentator.saveFullSignal(file);
				fullSingalFullPath = wavFile.getPath();
			}else{
				fullSingalFullPath = "";
			}
			showMessage(recordSegmentator.getMarkSet(), fullSingalFullPath);
			this.cancel();
			if(wavFile != null){
				ctx.getProject().getSample().setCurrentFile(wavFile);
				getExecutionFacade().fireEvent(GlobalCommands.file.currentSampleChanged);
			}
			
		}
		
		
		/**
		 * 
		 * @return
		 */
		public String getSignalName(){
			String fullSingalName = ctx.getProject().getExperimentId();
			return fullSingalName;
			
		}

	}
	protected void showMessage(MarkerSet words, String path){
		String messageFormat = getMessage(recordFinishedMessageBody);
		if(ProjectTypeEnum.segmenation.name().equals(ctx.getProject().getType())){
			messageFormat = getMessage(recordedSoundSavedMessageBody);
		}
		
		String messageBody = MessageFormat.format(messageFormat, 
				words.getMarkers().size(),
				path
				);
		
		log.info(messageBody);
		
		if(Boolean.TRUE.equals(ctx.getEnv().getPopupNotifications())){
			JOptionPane.showMessageDialog(null,messageBody,
							getMessage(recordFinishedMessageHeader),
							JOptionPane.INFORMATION_MESSAGE);
		}
	}
	/**
	 * Construct record audio format
	 * @param config
	 * @return
	 */
	protected AudioFormat getFormat(WorkUIExtractorConfig config) {
		Float sampleRate = config.getRecordSampleRate();
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
		return af;
	}
	/**
	 * Create reader depends on content
	 * @param workConfig
	 * @return
	 */
	protected WraperExtractorReader createReader(WorkUIExtractorConfig workConfig){
		AudioFormat format = getFormat(workConfig);
		IExtractorInputReader reader = ExtractorsFactory.createReader(format);
		reader.setConfig(createReaderConfig(workConfig));
		WraperExtractorReader wraperExtractorReader = null;
		if(isRecordable(workConfig)){
			wraperExtractorReader =  new RecordWraperExtractorReader(reader);
		}else{
			wraperExtractorReader = new WraperExtractorReader(reader,1);
		}
		wraperExtractorReader.setFormat(getFormat(workConfig));
		return wraperExtractorReader;
	}
	
	protected RecordSegmentatorOnline createSegmentator(WorkUIExtractorConfig config, WraperExtractorReader reader){
		RecordSegmentatorOnline segmentator = new RecordSegmentatorOnline();
		segmentator.setReader((RecordWraperExtractorReader)reader);
		if(isRecordable(config)){
			segmentator.setPath(config.getAudioPathOutput());
		}else{
			segmentator.setPath(null);
		}
		segmentator.setParam(createParam(config));
		return segmentator;
	}

	protected OnlineDecisionSegmentatorParam createParam(WorkUIExtractorConfig config){
		OnlineDecisionSegmentatorParam param = new OnlineDecisionSegmentatorParam();
		param.setMinSpace(config.getSegmentationMinSpace().longValue());
		param.setMinLength(config.getSegmentationMinLength().longValue());
		param.setExpandStart(config.getSegmentationExpandStart().longValue());
		param.setExpandEnd(config.getSegmentationExpandEnd().longValue());
		return param;
	}
	
	protected IExtractorConfig createReaderConfig(WorkUIExtractorConfig workConfig){
		IExtractorConfig config = WorkUIExtractorConfigUtil.convert(workConfig,
				workConfig.getRecordSampleRate());
		return config;
	}
	
	protected boolean isRecordable(WorkUIExtractorConfig config){
		return true;//!StringUtils.hasText(config.getAudioPathOutput());
	}
	
	
	protected Timer getTimer() {
		if(timer == null){
			timer = new Timer("Spantus sound capture");
		}
		return timer;
	}
	
	public WorkInfoManager getWorkInfoManager() {
		if(workInfoManager == null){
			workInfoManager = WorkUIServiceFactory.createInfoManager();
		}
		return workInfoManager;
	}
	public AudioCapture getCapture() {
		if(capture == null){
			capture = new AudioCapture();
		}
		return capture;
	}

	public void setCapture(AudioCapture capture) {
		this.capture = capture;
	}
}

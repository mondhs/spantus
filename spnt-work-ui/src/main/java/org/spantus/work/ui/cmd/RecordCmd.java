package org.spantus.work.ui.cmd;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.swing.JOptionPane;

import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioCapture;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.segment.io.RecordWraperExtractorReader;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.segment.online.ThresholdSegmentatorOnline;
import org.spantus.utils.StringUtils;
import org.spantus.work.reader.SupportableReaderEnum;
import org.spantus.work.segment.OnlineSegmentationUtils;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.dto.SpantusWorkProjectInfo.ProjectTypeEnum;
import org.spantus.work.ui.services.WorkInfoManager;
import org.spantus.work.ui.services.WorkUIServiceFactory;
import org.spantus.work.ui.util.WorkUIExtractorConfigUtil;

public class RecordCmd extends AbsrtactCmd {
	
	public static final String recordFinishedMessageHeader = "recordFinishedMessageHeader"; 
	public static final String recordFinishedMessageBody = "recordFinishedMessageBody";
	
	
	protected Logger log = Logger.getLogger(getClass());

	private AudioCapture capture;
	private SpantusWorkCommand handler;
	

	private Timer timer;

	private SpantusWorkInfo ctx;
	
//	private boolean isRecordInitialyzed = false;
	private WorkInfoManager workInfoManager;
	
	private SampleChangeListener lisetener;

	public RecordCmd(SampleChangeListener lisetener, SpantusWorkCommand handler) {
		this.lisetener = lisetener;
		this.handler = handler;
	}
	
	public String execute(final SpantusWorkInfo ctx) {
		this.ctx = ctx;
		if( getCapture()!=null && getCapture().isRunning()){
//			log.error("is already recording ");
			return null;
		}
		
		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader().getWorkConfig();
		
		
		ctx.getProject().getCurrentSample().setCurrentFile(null);
		ctx.getProject().getCurrentSample().setFormat(null);
		ctx.getProject().getCurrentSample().setMarkerSetHolder(new MarkerSetHolder());
		setCapture(null);
		
		WraperExtractorReader wrapReader = createReader(config);
		
		RecordSegmentatorOnline recordSegmentator =
			createSegmentator(config, wrapReader);
		
		createExtractorForProject(wrapReader, recordSegmentator);

		getCapture().setReader(wrapReader);
		capture.setFormat(getFormat(config));
//		log.error("start capturing");
		ctx.setPlaying(true);
//		getTimer().schedule(new InitCapture(wrapReader.getReader()), 2000L);
		getTimer().schedule(new UpdateCapture(recordSegmentator), 500L, 250L);

		return null;
	}
	
	protected void createExtractorForProject(WraperExtractorReader wrapReader,
			RecordSegmentatorOnline recordSegmentator) {

		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader()
				.getWorkConfig();
		ThresholdSegmentatorOnline segmentator = null;
		
		if (ProjectTypeEnum.recordSegmentation.name().equals(ctx.getProject()
				.getCurrentType())) {
			for (String extr : ctx.getProject().getFeatureReader()
					.getExtractors()) {
				String[] extractor = extr.split(":");
				SupportableReaderEnum readerType = SupportableReaderEnum
						.valueOf(extractor[0]);
				switch (readerType) {
				case spantus:
					ExtractorEnum extractorType = ExtractorEnum
							.valueOf(extractor[1]);
					segmentator = OnlineSegmentationUtils.register(wrapReader
							.getReader(), extractorType);
					segmentator.setOnlineSegmentator(recordSegmentator);
					segmentator.setCoef(config.getThresholdCoef());
					segmentator.setLearningPeriod(config
							.getThresholdLeaningPeriod().longValue());
					break;
				case mpeg7:
					break;
				default:
					throw new RuntimeException("not impl: " + readerType);
				}
			}
		} else {
			ExtractorEnum extractorType = ExtractorEnum.WAVFORM_EXTRACTOR;
			segmentator = OnlineSegmentationUtils.register(wrapReader
					.getReader(), extractorType);
			segmentator.setOnlineSegmentator(recordSegmentator);
			segmentator.setCoef(config.getThresholdCoef());
			segmentator.setLearningPeriod(config.getThresholdLeaningPeriod()
					.longValue());

		}
	}


	
	public class UpdateCapture extends TimerTask {
		
		RecordSegmentatorOnline recordSegmentator;
		boolean isRecordInitialyzed = false;
		
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
			String fullSingalFullPath = recordSegmentator.getPath() + "/" + getSignalName();
			if(StringUtils.hasText(recordSegmentator.getPath())){
				wavFile = recordSegmentator.saveFullSignal(fullSingalFullPath);
			}else{
				fullSingalFullPath = "";
			}
			showMessage(recordSegmentator.getMarkSet(), fullSingalFullPath);
			this.cancel();
			if(wavFile != null){
				ctx.getProject().getCurrentSample().setCurrentFile(wavFile);
				handler.execute(GlobalCommands.file.currentSampleChanged.name(), ctx);
			}
			
		}
		/**
		 * 
		 * @return
		 */
		public String getSignalName(){
			String fullSingalName = ctx.getProject().getExperimentId();
			getWorkInfoManager().increaseExperimentId(ctx);
			return fullSingalName;
			
		}

	}
	protected void showMessage(MarkerSet words, String path){
		String messageFormat = getMessage(recordFinishedMessageBody);
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
	
	public AudioFormat getFormat(WorkUIExtractorConfig config) {
		Float sampleRate = config.getRecordSampleRate();
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}
	
	protected WraperExtractorReader createReader(WorkUIExtractorConfig workConfig){
		AudioFormat format = getFormat(workConfig);
		IExtractorInputReader reader = ExtractorsFactory.createReader(format);
		reader.setConfig(createReaderConfig(workConfig));
		WraperExtractorReader wraperExtractorReader = null;
		if(isRecordable(workConfig)){
			wraperExtractorReader =  new RecordWraperExtractorReader(reader);
		}else{
			wraperExtractorReader = new WraperExtractorReader(reader);
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

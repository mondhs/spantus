package org.spantus.work.ui.cmd;

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
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.segment.io.RecordWraperExtractorReader;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.segment.online.ThresholdSegmentatorOnline;
import org.spantus.work.reader.SupportableReaderEnum;
import org.spantus.work.segment.OnlineSegmentationUtils;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.util.WorkUIExtractorConfigUtil;

public class RecordCmd extends AbsrtactCmd {
	
	public static final String recordFinishedMessageHeader = "recordFinishedMessageHeader"; 
	public static final String recordFinishedMessageBody = "recordFinishedMessageBody";
	
	protected Logger log = Logger.getLogger(getClass());

	private AudioCapture capture;
	
	private Timer timer;

	private SpantusWorkInfo ctx;
	
	private boolean isRecordInitialyzed = false;
	
	SampleChangeListener lisetener;

	public RecordCmd(SampleChangeListener lisetener) {
		this.lisetener = lisetener;
	}
	
	public String execute(final SpantusWorkInfo ctx) {
		this.ctx = ctx;
		
		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader().getWorkConfig();
		
		
		ctx.getProject().getCurrentSample().setCurrentFile(null);
		ctx.getProject().getCurrentSample().setFormat(null);
		
		WraperExtractorReader wrapReader = createReader(config);
		
		RecordSegmentatorOnline recordSegmentator =
			createSegmentator(config, wrapReader);
		
		ThresholdSegmentatorOnline segmentator = null;
		
		for (String extr : ctx.getProject().getFeatureReader().getExtractors()) {
			String[] extractor = extr.split(":");
			SupportableReaderEnum readerType = SupportableReaderEnum.valueOf(extractor[0]);
			switch (readerType) {
			case spantus:
				ExtractorEnum extractorType = ExtractorEnum.valueOf(extractor[1]);
				segmentator  = OnlineSegmentationUtils.register(wrapReader.getReader(), extractorType);
				segmentator.setOnlineSegmentator(recordSegmentator);
				segmentator.setCoef(config.getThresholdCoef());
				segmentator.setLearningPeriod(config.getThresholdLeaningPeriod().longValue());
				break;
			case mpeg7:
				break;
			default:
				throw new RuntimeException("not impl: " + readerType);
			}
		}
		
		
//		segmentator  = OnlineSegmentationUtils.register(wrapReader.getReader(), ExtractorEnum.ENERGY_EXTRACTOR);
//		segmentator.setOnlineSegmentator(multipleSegmentator);
//		segmentator.setCoef(config.getThresholdCoef());
//		segmentator.setLearningPeriod(config.getThresholdLeaningPeriod().longValue());
//
//		segmentator  = OnlineSegmentationUtils.register(wrapReader.getReader(), ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR);
//		segmentator.setOnlineSegmentator(multipleSegmentator);
//		segmentator.setCoef(config.getThresholdCoef());
//		segmentator.setLearningPeriod(config.getThresholdLeaningPeriod().longValue());
//
//		segmentator  = OnlineSegmentationUtils.register(wrapReader.getReader(), ExtractorEnum.WAVFORM_EXTRACTOR);

		capture = new AudioCapture(wrapReader);
		capture.setFormat(getFormat(config));
		capture.start();
		
		
		ctx.setPlaying(true);
		getTimer().schedule(new InitCapture(wrapReader.getReader()), 2000L);
		getTimer().schedule(new UpdateCapture(recordSegmentator), 2000L, 1000L);

		return null;
	}

	public class InitCapture extends TimerTask {
		IExtractorInputReader reader;
		public InitCapture(IExtractorInputReader reader) {
			this.reader = reader;
		}
		@Override
		public void run() {
			lisetener.changedReader(reader);
			isRecordInitialyzed = true;
		}
	}
	
	public class UpdateCapture extends TimerTask {
		
		RecordSegmentatorOnline recordSegmentator;
		
		public UpdateCapture(RecordSegmentatorOnline recordSegmentator) {
			super();
			this.recordSegmentator = recordSegmentator;
		}

		@Override
		public void run() {
			if(!isRecordInitialyzed){
				return;
			}
			if (lisetener != null) {
				lisetener.refresh();
			}
			if (!ctx.getPlaying()) {
				log.debug("repaint");
				capture.finalize();
				this.cancel();
				String fullSingalName = "full"+System.currentTimeMillis();
				String fullSingalFullPath = recordSegmentator.getPath() + "/" + fullSingalName;
				if(recordSegmentator.getPath() != null && !"".equals(recordSegmentator.getPath())){
					recordSegmentator.saveFullSignal(fullSingalName);
				}else{
					fullSingalFullPath = "";
				}
				showMessage(recordSegmentator.getMarkSet(), fullSingalFullPath);
			}
		}

	}
	protected void showMessage(MarkerSet words, String path){
		String messageFormat = getMessage(recordFinishedMessageBody);
		String messageBody = MessageFormat.format(messageFormat, 
				words.getMarkers().size(),
				path
				);
		JOptionPane.showMessageDialog(null,messageBody,
							getMessage(recordFinishedMessageHeader),
							JOptionPane.INFORMATION_MESSAGE);
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
		return config.getAudioPathOutput()!=null && !"".equals(config.getAudioPathOutput());
	}
	
	
	protected Timer getTimer() {
		if(timer == null){
			timer = new Timer("Spantus sound capture");
		}
		return timer;
	}

}

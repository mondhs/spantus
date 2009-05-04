package org.spantus.work.ui;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.ExtractorWrapper;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioCapture;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.segment.online.DecistionSegmentatorOnline;
import org.spantus.segment.online.MultipleSegmentatorOnline;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.segment.online.ThresholdSegmentatorOnline;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.segment.OnlineSegmentationUtils;
import org.spantus.work.services.ConfigDao;
import org.spantus.work.services.ConfigPropertiesDao;

public class SegmentMonitorPlot extends AbstractSegmentPlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Timer timer = new Timer("Sound Monitor Plot");
	private Logger log = Logger.getLogger(getClass());
	private AudioCapture capture;
	public static final String FILE_NAME = "./config.properties";
	

	public SegmentMonitorPlot() {
		ConfigDao configDao = new ConfigPropertiesDao();

		IExtractorConfig config = configDao.read(new File(FILE_NAME));
		AudioFormat format = getFormat(config.getSampleRate());
		
		ExtractorParam param = config.getParameters().get(DefaultExtractorConfig.class.getName());
		Float threshold_coef = ExtractorParamUtils.<Float>getValue(param,
				ConfigPropertiesDao.key_threshold_coef);
		Long threshold_leaningPeriod = ExtractorParamUtils.<Long>getValue(param,
				ConfigPropertiesDao.key_threshold_leaningPeriod);

		
		reader = ExtractorsFactory
				.createReader(format); 

		reader.getConfig().setBufferSize(3000);
		
		DecistionSegmentatorOnline multipleSegmentator = 
			getSegmentatorRecordable(param);
//			getSegmentatorDefault(param);

		ThresholdSegmentatorOnline segmentator = null;
		
//		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.ENERGY_EXTRACTOR);
//		segmentator.setOnlineSegmentator(multipleSegmentator);
//		segmentator.setCoef(threshold_coef);
//		segmentator.setLearningPeriod(threshold_leaningPeriod);

		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR);
		segmentator.setOnlineSegmentator(multipleSegmentator);
		segmentator.setCoef(threshold_coef);
		segmentator.setLearningPeriod(threshold_leaningPeriod);

		

//		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.LOUDNESS_EXTRACTOR);
//		segmentator.setMultipleSegmentator(multipleSegmentator);
//		segmentator.setCoef(4f);

//		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.CROSSING_ZERO_EXTRACTOR);
//		segmentator.setMultipleSegmentator(multipleSegmentator);
//		segmentator.setCoef(6f);


//		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.WAVFORM_EXTRACTOR);
		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.LPC_EXTRACTOR);

		capture = new AudioCapture(wraperExtractorReader);
		capture.setFormat(format);
		capture.start();

//		initGraph(reader);
		timer.schedule(new TimerTask() {
			public void run() {
//				log.debug("repaint");
//				chart.setPreferredSize(getSize());
				repaint();
				if( chart == null ){
					if(reader.getExtractorRegister().iterator().next().getOutputValues().size()>0){
						initGraph(reader);
					}
				}
			}
		}, 1000L, 1000L);

	}
	
	private void initGraph(IExtractorInputReader reader) {
		chart = ChartFactory.createChart(reader);
//		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		getContentPane().add(chart);
	}


	public AudioFormat getFormat(float sampleRate) {
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}
	public AudioFormat getFormat() {
		return getFormat(8000);
	}
	
	public ThresholdSegmentatorOnline getSegmentator(IExtractor extractor, MultipleSegmentatorOnline multipe){
		ThresholdSegmentatorOnline segmentator = new ThresholdSegmentatorOnline();
		ExtractorWrapper wraper = new ExtractorWrapper(extractor);
		segmentator.setExtractor(wraper);
		wraper.getListeners().add(segmentator);
		segmentator.setOnlineSegmentator(multipe);
		return segmentator;
		
	}
	
	protected DecistionSegmentatorOnline getSegmentatorRecordable(ExtractorParam param){
		String path = ExtractorParamUtils.<String>getValue(param,
				ConfigPropertiesDao.key_format_pathOutput);
		RecordSegmentatorOnline segmentator = 
			(RecordSegmentatorOnline)getSegmentatorRecordable();
		segmentator.setPath(path);
		segmentator.setParam(createParam(param));
		return segmentator;
	}
	
	protected DecistionSegmentatorOnline getSegmentatorDefault(ExtractorParam param){
		DecistionSegmentatorOnline segmentator = getSegmentatorDefault();
		segmentator.setParam(createParam(param));
		return segmentator; 
	}
	
	protected OnlineDecisionSegmentatorParam createParam(ExtractorParam param) {
		OnlineDecisionSegmentatorParam onlineParam = createParam(); 
		onlineParam.setMinSpace(ExtractorParamUtils.<Long>getValue(param,
				ConfigPropertiesDao.key_segmentation_minSpace));
		onlineParam.setMinLength(ExtractorParamUtils.<Long>getValue(param,
				ConfigPropertiesDao.key_segmentation_minLength));
		onlineParam.setExpandStart(ExtractorParamUtils.<Long>getValue(param,
				ConfigPropertiesDao.key_segmentation_expandStart));
		onlineParam.setExpandEnd(ExtractorParamUtils.<Long>getValue(param,
				ConfigPropertiesDao.key_segmentation_expandEnd));
		return onlineParam;
	}
	
	public void repaint() {
		if (getChart() != null) {
			try{
				getChart().repaint();
			}catch(Exception e){
				log.error(e.getMessage());
			}
		}
		super.repaint();
	}

	public AbstractSwingChart getChart() {
		return chart;
	}

	public static void main(String[] args) {
		AbstractSegmentPlot monitorPlot = new SegmentMonitorPlot();
		monitorPlot.showChart();
	}


}

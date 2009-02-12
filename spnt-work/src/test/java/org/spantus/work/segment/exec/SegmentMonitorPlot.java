package org.spantus.work.segment.exec;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.core.extractor.ExtractorWrapper;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioCapture;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.online.MultipleSegmentatorOnline;
import org.spantus.segment.online.ThresholdSegmentatorOnline;
import org.spantus.work.segment.OnlineSegmentationUtils;
import org.spantus.work.test.SignalSelectionListenerMock;

public class SegmentMonitorPlot extends AbstractSegmentPlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Timer timer = new Timer("Sound Monitor Plot");
	Logger log = Logger.getLogger(getClass());
	AudioCapture capture;

	private SegmentMonitorPlot() {
		reader = ExtractorsFactory
				.createReader(getFormat());

		reader.getConfig().setBufferSize(3000);
		
		MultipleSegmentatorOnline multipleSegmentator = 
			getSegmentatorRecordable();
//			getSegmentatorDefault();
		
		ThresholdSegmentatorOnline segmentator = null;

		
		
		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.ENERGY_EXTRACTOR);
		segmentator.setOnlineSegmentator(multipleSegmentator);
		segmentator.setCoef(6f);
		segmentator.setLearningPeriod(5000f);

		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR);
		segmentator.setOnlineSegmentator(multipleSegmentator);
		segmentator.setCoef(6f);
		segmentator.setLearningPeriod(5000f);

		

//		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.LOUDNESS_EXTRACTOR);
//		segmentator.setMultipleSegmentator(multipleSegmentator);
//		segmentator.setCoef(4f);

//		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.CROSSING_ZERO_EXTRACTOR);
//		segmentator.setMultipleSegmentator(multipleSegmentator);
//		segmentator.setCoef(6f);


		segmentator  = OnlineSegmentationUtils.register(reader, ExtractorEnum.WAVFORM_EXTRACTOR);

		capture = new AudioCapture(wraperExtractorReader);
		capture.setFormat(getFormat());
		capture.start();

//		initGraph(reader);
		timer.schedule(new TimerTask() {
			public void run() {
				log.debug("repaint");
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
		chart.addSignalSelectionListener(new SignalSelectionListenerMock());
		getContentPane().add(chart);
	}



	public AudioFormat getFormat() {
		float sampleRate = 8000;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}
	
	public ThresholdSegmentatorOnline getSegmentator(IExtractor extractor, MultipleSegmentatorOnline multipe){
		ThresholdSegmentatorOnline segmentator = new ThresholdSegmentatorOnline();
		ExtractorWrapper wraper = new ExtractorWrapper(extractor);
		segmentator.setExtractor(wraper);
		wraper.getListeners().add(segmentator);
		segmentator.setOnlineSegmentator(multipe);
		return segmentator;
		
	}
	
	public void repaint() {
		if (getChart() != null) {
//			getChart().setSize(getSize());
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

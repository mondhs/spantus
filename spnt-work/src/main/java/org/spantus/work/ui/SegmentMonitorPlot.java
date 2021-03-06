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
package org.spantus.work.ui;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.DefaultExtractorConfig;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.io.AudioCapture;
import org.spantus.core.threshold.IClassifier;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.segment.io.RecordSegmentatorOnline;
import org.spantus.segment.online.DecisionSegmentatorOnline;
import org.spantus.segment.online.ISegmentatorListener;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;
import org.spantus.utils.ExtractorParamUtils;
import org.spantus.work.services.ConfigDao;
import org.spantus.work.services.ConfigPropertiesDao;

public class SegmentMonitorPlot extends AbstractSegmentPlot {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Timer timer = new Timer("Sound Monitor Plot");
//	private Logger log = Logger.getLogger(SegmentMonitorPlot.class);
	private DecisionSegmentatorOnline multipleSegmentator;
	private AudioCapture capture;
        private AudioFormat format;
//	private boolean recording;
	public static final String FILE_NAME = "./config.properties";

	public SegmentMonitorPlot() {
		
	}
        
        public void initialize() {
            ConfigDao configDao = new ConfigPropertiesDao();

            IExtractorConfig config = configDao.read(new File(FILE_NAME));
            format = getFormat(config.getSampleRate());

            ExtractorParam param = config.getParameters().get(DefaultExtractorConfig.class.getName());

            setReader(
                    ExtractorsFactory.createReader(format));

            getReader().getConfig().setBufferSize(3000);


            multipleSegmentator =
                    createSegmentatorRecordable(param);
    //			createSegmentatorDefault(param);

            registerExtractors(param, multipleSegmentator);
           
        }
	
	@Override
	public void startRecognition(){
		initialize();
		startRecord();
		super.startRecognition();
	}
	
        @Override
	public void stopRecognition(){
		stopRecord();
		super.stopRecognition();
	}
	
	public void stopRecord(){
		timer.cancel();
		capture.kill();
                capture = null;
	}
	
        public void startRecord() {
            capture = new AudioCapture(getWraperExtractorReader());
            capture.setFormat(format);
            capture.start();
            timer.schedule(new TimerTask() {

                public void run() {
                    if (getChart() == null) {
                        if (getReader().getExtractorRegister().iterator().next().getOutputValues().size() > 0) {
                            initGraphMinimum(getReader());
                        }
                    } else {
                        getChart().setDirty(Boolean.TRUE);
                        getChart().repaint();
                    }
                }
            }, 1000L, 1000L);
        }
	
	public void registerExtractors(ExtractorParam param, ISegmentatorListener multipleSegmentator){
		IClassifier segmentator = null;


		ExtractorParam paramEnergy = new ExtractorParam();
		segmentator =ExtractorUtils.registerThreshold(getReader(), ExtractorEnum.ENERGY_EXTRACTOR, paramEnergy); 
		segmentator.addClassificationListener(multipleSegmentator);
		segmentator  = ExtractorUtils.registerThreshold(getReader(), ExtractorEnum.MFCC_EXTRACTOR);

	}
	
	

	public AudioFormat getFormat(Double sampleRate) {
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate.floatValue(), sampleSizeInBits, channels, signed,
				bigEndian);
	}
	public AudioFormat getFormat() {
		return getFormat(8000D);
	}
	
	
	protected DecisionSegmentatorOnline createSegmentatorRecordable(ExtractorParam param){
		String path = ExtractorParamUtils.<String>getValue(param,
				ConfigPropertiesDao.key_format_pathOutput);
		
		RecordSegmentatorOnline segmentator = 
			(RecordSegmentatorOnline)createSegmentatorRecordable();
		segmentator.setPath(path);
		segmentator.setParam(createParam(param));
		return segmentator;
	}
	
	protected DecisionSegmentatorOnline createSegmentatorDefault(ExtractorParam param){
		DecisionSegmentatorOnline segmentator = createSegmentatorDefault();
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
	
	

	public static void main(String[] args) {
		AbstractSegmentPlot monitorPlot = new SegmentMonitorPlot();
		monitorPlot.showChartFrame();
		monitorPlot.startRecognition();
	}

	public DecisionSegmentatorOnline getMultipleSegmentator() {
		return multipleSegmentator;
	}


}

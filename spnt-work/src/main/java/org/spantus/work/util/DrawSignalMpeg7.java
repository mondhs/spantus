package org.spantus.work.util;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import org.spantus.chart.AbstractSwingChart;
import org.spantus.chart.ChartFactory;
import org.spantus.chart.util.ChartUtils;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IGeneralExtractor;
import org.spantus.exception.ProcessingException;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;
import org.spantus.mpeg7.config.Mpeg7ConfigUtil;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.mpeg7.io.Mpeg7Factory;
import org.spantus.utils.FileUtils;

public class DrawSignalMpeg7 extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7833736854853194647L;
	
	static Map<String, String> resolveMap = new HashMap<String, String>();
	static {
//		resolveMap.put("BUFFERED_AUTOCORRELATION_EXTRACTOR", "autocorr");
	}
	/**
	 * 
	 */
	AbstractSwingChart chart;
	IExtractorInputReader reader;
	List<IGeneralExtractor> extr = new ArrayList<IGeneralExtractor>();
	String path;
	String name;
	String extention;
	String sufix = "_"; 
	
	public AbstractSwingChart getChart() {
		return chart;
	}

	public void setChart(AbstractSwingChart chart) {
		this.chart = chart;
	}

	/**
	 * Demo
	 */
	
	
	public DrawSignalMpeg7(String name) {
		File audioFile = new File(name);
		this.path =  FileUtils.getPath(audioFile);
		this.name =  FileUtils.getOnlyFileName(audioFile);
		try {
			reader = readSignal(audioFile);
		} catch (UnsupportedAudioFileException e) {
			throw new ProcessingException(e);
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		extr.addAll(reader.getExtractorRegister());
		extr.addAll(reader.getExtractorRegister3D());
	}

	public IExtractorInputReader getReader(IExtractorInputReader reader, List<IGeneralExtractor> extr, int index){
		if(index == 0){
			return null;
		}
		reader.getExtractorRegister().clear();
		reader.getExtractorRegister3D().clear();
		IGeneralExtractor gextr = extr.get(index);
		sufix = gextr.getName();
		if(gextr instanceof IExtractor){
			reader.getExtractorRegister().add((IExtractor)gextr);
		}else if(gextr instanceof IExtractorVector){
			reader.getExtractorRegister3D().add((IExtractorVector)gextr);
		}
		return reader;
	}
	public void process(){
		process(getExtractorsSize()-1);
	}
	
	private void process(int index) {
		if(index < 0){
			this.dispose();
			return;
		}
		IExtractorInputReader _reader = getReader(reader, extr, index);
		if(_reader == null){
			process(index-1);
			return;
		}
		setChart(ChartFactory.createChart(_reader));
		getContentPane().add(getChart());
		getChart().addComponentListener(new FrameShowedAdapter( this.path + name + "_" + resolveSufix(sufix), index ));

	}
	
	public int getExtractorsSize(){
		return extr.size();
	}
	
	public IExtractorInputReader readSignal(File audioFile)
			throws UnsupportedAudioFileException, IOException {
		IExtractorConfig config = Mpeg7ConfigUtil.createConfig(Mpeg7ExtractorEnum.values());
		Mpeg7ExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		reader.setConfig(config);
		Mpeg7Factory.createAudioReader().readAudio(audioFile.toURI().toURL(),
				reader);
		if (reader == null) {
			throw new RuntimeException();
		}
		return reader;
	}
	public class FrameShowedAdapter extends ComponentAdapter {
		String name;
		int index;
		public FrameShowedAdapter(String name, int index) {
			this.name = name;
			this.index = index;
		}
		
		public void componentResized(ComponentEvent e) {
			try {
				ChartUtils.writeAsPNG(getChart(), new File(
						name + ".png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			getContentPane().remove(getChart());
			process(index-1);
		}
	}
	
	
	public String resolveSufix(String sufix) {
		return sufix.replace("Type", "");
	}

	

	public static void main(String[] args) {
//		"./target/test-classes/text1.wav"
		if(args.length != 1) throw new IllegalArgumentException("it is needed provide path to wav file");
		DrawSignalMpeg7 demo = new DrawSignalMpeg7(args[0]);
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.setSize(640, 140);
		demo.validate();
		demo.setVisible(true);
		demo.process();

	}
}

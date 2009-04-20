package org.spantus.work.ui.cmd.file;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JFileChooser;

import org.spantus.chart.util.ChartUtils;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;
import org.spantus.work.SpantusBundle;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.cmd.UIFileFilter;
import org.spantus.work.ui.container.chart.SampleChart;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ExportCmd extends AbsrtactCmd {
	
	private Logger log = Logger.getLogger(getClass());
	
	public static final String[] IMAGE_FILES = {"png"};
	public static final String[] MARKER_FILES = {"mspnt.xml"};
	public static final String[] SAMPLE_FILES = {"sspnt.xml"};
	public static final String[] MPEG7_FILES = {"mpeg7.xml"};
	public static final String[] BUNDLE_FILES = {"spnt.zip"};
	
	
	private SampleChart chart;
	
	private Component parent;
	
	private JFileChooser chooser;
	
	public JFileChooser getChooser() {
		if(chooser == null){
			chooser = new JFileChooser();
			chooser.setFileFilter(new UIFileFilter(IMAGE_FILES, ExportType.image.name()));
			chooser.addChoosableFileFilter(new UIFileFilter(MARKER_FILES, ExportType.markers.name()));
			chooser.addChoosableFileFilter(new UIFileFilter(SAMPLE_FILES, ExportType.sample.name()));
			chooser.addChoosableFileFilter(new UIFileFilter(BUNDLE_FILES, ExportType.bundle.name()));
			chooser.setAcceptAllFileFilterUsed(false);
		}
		return chooser;
	}

	enum ExportType{image, markers, sample, mpeg7, bundle}; 
	
	public ExportCmd(Frame frame, SampleChart chart) {
		this.chart = chart;
		this.parent = frame;
	}
	
	
	
	public String execute(SpantusWorkInfo ctx) {
		exportFile(ctx);
		return null;
	}

	protected boolean exportFile(SpantusWorkInfo ctx) {
//		File file = null;
//		try {
//			file = new File(ctx.getProject().getCurrentSample().getCurrentFile().toURI());
//			String fileName = file.getName();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		JFileChooser fileChooser = getChooser();
		fileChooser.getSelectedFile();
		
		
		int returnValue = fileChooser.showSaveDialog(parent);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			fileChooser.getSelectedFile().getName();
			File selectedFile = fileChooser.getSelectedFile();
			selectedFile = addExtention(selectedFile, fileChooser.getFileFilter());
			ExportType type = ExportType.valueOf(((UIFileFilter)fileChooser.getFileFilter()).getType());
			switch (type) {
			case image:
				return writePNG(selectedFile);
			case markers:
				return writeMarker(ctx.getProject().getCurrentSample().getMarkerSetHolder(),selectedFile);
			case sample:
				return writeSample(chart.getReader(), selectedFile);
			case bundle:
				return writeBundle(chart.getReader(), ctx.getProject().getCurrentSample().getMarkerSetHolder(), selectedFile);
			default:
				throw new RuntimeException("not impl: " + type.toString());
			}
		}
		return false;
	}

	protected boolean writeMarker(MarkerSetHolder holder, File file){
		WorkServiceFactory.createMarkerDao().write(holder, file);
		return true;
	}
	
	protected boolean writeSample(IExtractorInputReader reader, File file){
		WorkServiceFactory.createReaderDao().write(reader, file);
		return true;
	}

	protected boolean writeBundle(IExtractorInputReader reader, MarkerSetHolder holder, File file){
		SpantusBundle bundle = new SpantusBundle();
		bundle.setReader(reader);
		bundle.setHolder(holder);
		WorkServiceFactory.createBundleDao().write(bundle, file);
		return true;
	}


	
	protected boolean writePNG(File file){
		try {
			ChartUtils.writeAsPNG(chart.getChart(), file);
			log.debug("exported image successfuly: " + file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public File addExtention(File selectedFile, javax.swing.filechooser.FileFilter UIFileFilter){
		String fileName = selectedFile.getName();
		File rtnFile = selectedFile;
		String[] extentions = ((UIFileFilter)UIFileFilter).getExtension();
		if(!fileName.endsWith("."+extentions[0])){
			rtnFile = new File(selectedFile.getParent(), fileName+"."+extentions[0]);
		}
		return rtnFile;
	}
	
}

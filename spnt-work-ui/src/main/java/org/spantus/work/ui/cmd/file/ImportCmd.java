package org.spantus.work.ui.cmd.file;

import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JFileChooser;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exception.ProcessingException;
import org.spantus.logger.Logger;
import org.spantus.mpeg7.extractors.Mpeg7ExtractorInputReader;
import org.spantus.mpeg7.io.Mpeg7Factory;
import org.spantus.work.SpantusBundle;
import org.spantus.work.services.WorkServiceFactory;
import org.spantus.work.ui.cmd.AbsrtactCmd;
import org.spantus.work.ui.cmd.GlobalCommands;
import org.spantus.work.ui.cmd.UIFileFilter;
import org.spantus.work.ui.cmd.file.ExportCmd.ExportType;
import org.spantus.work.ui.container.chart.SampleChart;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class ImportCmd extends AbsrtactCmd {
	
	private Logger log = Logger.getLogger(getClass());
	
	private JFileChooser fileChooser;
	private Component parent;
	private SampleChart chart;
	private File defaulDir;
	
	
	public ImportCmd(Component parent, SampleChart chart) {
		this.parent = parent;
		this.chart = chart;
	}
	
	
	
	public String execute(SpantusWorkInfo ctx) {
		defaulDir = ctx.getProject().getWorkingDir();
		
		int returnValue = getFileChooser().showOpenDialog(parent);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = getFileChooser().getSelectedFile();
			ExportCmd.ExportType type = ExportCmd.ExportType.valueOf(((UIFileFilter)getFileChooser().getFileFilter()).getType());
			switch (type) {
			case markers:
				ctx.getProject().getCurrentSample().setMarkerSetHolder(readMarker(selectedFile));
				log.debug("Imported markers successfuly:" + selectedFile);
				return GlobalCommands.sample.reloadSampleChart.name();
			case sample:
				chart.setReader(readExtractorReader(selectedFile));
				log.debug("Imported sample successfuly:" + selectedFile);
				return GlobalCommands.sample.reloadSampleChart.name();
			case mpeg7:
				chart.setReader(readMpeg7(selectedFile));
				log.debug("Imported mpeg7 successfuly:" + selectedFile);
				return GlobalCommands.sample.reloadSampleChart.name();
			case bundle:
				readBundle(ctx, selectedFile);
				log.debug("Imported bundle successfuly:" + selectedFile);
				return GlobalCommands.sample.reloadSampleChart.name();
			default:
				throw new ProcessingException("not impl: " + type.toString());
			}
		}
		return null;
	}
	
	protected JFileChooser getFileChooser(){
		if(fileChooser == null){
			fileChooser = new JFileChooser();
			fileChooser.addChoosableFileFilter(new UIFileFilter(ExportCmd.MARKER_FILES, ExportCmd.ExportType.markers.name()));
			fileChooser.addChoosableFileFilter(new UIFileFilter(ExportCmd.SAMPLE_FILES, ExportType.sample.name()));
			fileChooser.addChoosableFileFilter(new UIFileFilter(ExportCmd.MPEG7_FILES, ExportType.mpeg7.name()));
			fileChooser.addChoosableFileFilter(new UIFileFilter(ExportCmd.BUNDLE_FILES, ExportType.bundle.name()));

			fileChooser.setAcceptAllFileFilterUsed(false);
			if(defaulDir != null){
				fileChooser.setCurrentDirectory(defaulDir);
			}
		}
		return fileChooser;
	}

	protected MarkerSetHolder readMarker(File file){
		MarkerSetHolder holder = WorkServiceFactory.createMarkerDao().read(file);
		return holder;
	}
	
	protected IExtractorInputReader readExtractorReader(File file){
		IExtractorInputReader reader = WorkServiceFactory.createReaderDao().read(file);
		return reader;
	}
	protected IExtractorInputReader readMpeg7(File file){
		Mpeg7ExtractorInputReader reader = new Mpeg7ExtractorInputReader();
		try {
			Mpeg7Factory.createAudioReader().readSignal(file.toURI().toURL(), reader);
		} catch (ProcessingException e) {
			throw e;
		} catch (MalformedURLException e) {
			throw new ProcessingException(e);
		}
		return reader;
	}

	
	protected void readBundle(SpantusWorkInfo ctx, File file){
		SpantusBundle bundle = WorkServiceFactory.createBundleDao().read(file);
		chart.setReader(bundle.getReader());
		ctx.getProject().getCurrentSample().setMarkerSetHolder(bundle.getHolder());
	}

	
	
}

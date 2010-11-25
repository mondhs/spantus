package org.spantus.exp.segment.exec;

import java.io.File;

import org.spantus.core.beans.SampleInfo;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.exp.segment.beans.ProcessReaderInfo;
import org.spantus.exp.segment.services.ExpServiceFactory;
import org.spantus.exp.segment.services.MakerComparison;
import org.spantus.exp.segment.services.ProcessReader;
import org.spantus.work.services.MarkerDao;
import org.spantus.work.services.ReaderDao;
import org.spantus.work.services.WorkServiceFactory;

public class SaveProcessedThresholdReader {

	ReaderDao readerDao = null;
	MarkerDao markerDao = null;
	ProcessReader processReader = null;
	MakerComparison makerComparison = null;

	
	
	public SaveProcessedThresholdReader() {
		readerDao = WorkServiceFactory.createReaderDao();
		markerDao = WorkServiceFactory.createMarkerDao();
		processReader = ExpServiceFactory.createProcessReader();
		makerComparison = ExpServiceFactory.createMakerComparison();
	}
	public void process(String inputReaderFileName, String fileResultName){
		File inputReaderFile = new File(inputReaderFileName);
		IExtractorInputReader reader = readerDao.read(inputReaderFile);
		ProcessReaderInfo processReaderInfo = new ProcessReaderInfo();
		processReaderInfo.setThresholdCoef(1.2);
		SampleInfo info = processReader.processReader(reader, processReaderInfo);
		readerDao.write(info.getReader(), getAppendedFile(inputReaderFile, fileResultName, "sspnt.xml"));
		markerDao.write(info.getMarkerSetHolder(), 
				getAppendedFile(inputReaderFile, fileResultName, "mspnt.xml"));
	}
	protected File getAppendedFile(File file, String name, String sufix){
		String parentFileName = file.getParent();
		return new File(parentFileName+"/"+name+"."+sufix);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SaveProcessedThresholdReader ptr = new SaveProcessedThresholdReader();
		ptr.process(
				"../../../data/t_1_2.wav.sspnt.xml",
				"processed_t_1_2"
		);
		
	}

}

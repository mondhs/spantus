package org.spnt.recognition.dtw.exec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.corpus.CorpusMatchListener;
import org.spantus.logger.Logger;

public class WritableCorpusMatchListener implements CorpusMatchListener {

	public enum ControledLabels{sviesa, isjunk};
	
	protected Logger log = Logger.getLogger(getClass());
	BufferedWriter out;
	
	public void matched(RecognitionResult result) {
		if(result == null) return;
		ControledLabels controledLabel = null;
		try{
			controledLabel = ControledLabels.valueOf(result.getInfo().getName());
		}catch (Exception e) {
			log.error(e);
			return;
		}
		switch (controledLabel) {
		case sviesa:
			write("H");
			break;
		case isjunk:
			write("L");
			break;

		default:
			log.error("not implemented: " + controledLabel);
			break;
		}
	}
	public void write(String str){
		try {
			getOutput().write(str);
			getOutput().flush();
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	protected BufferedWriter getOutput(){
		if(out == null){
			try {
				out = new BufferedWriter(new FileWriter("/dev/ttyUSB0"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out;
	}

}

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
package org.spnt.recognition.dtw.ui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.corpus.CorpusMatchListener;
import org.spantus.logger.Logger;

public class WritableCorpusMatchListener implements CorpusMatchListener {

	public enum ControledLabels{sviesa1,sviesa2,sviesa, isjunk, tamsa1, tamsa2};
	
	protected Logger log = Logger.getLogger(getClass());
	BufferedWriter out;
	
	public void matched(RecognitionResult result) {
		if(result == null) return;
		ControledLabels controledLabel = null;
		try{
			controledLabel = ControledLabels.valueOf(result.getInfo().getName());
		}catch (Exception e) {
			log.error("command not found for: " + controledLabel);
			return;
		}
		switch (controledLabel) {
		case sviesa1:
		case sviesa2:	
		case sviesa:
			write("M");
			break;
		case tamsa1:
		case tamsa2:
		case isjunk:
			write("I");
			break;

		default:
			log.error("not implemented: " + controledLabel);
			break;
		}
	}
	public void write(String str){
		try {
			BufferedWriter currentOutput = getOutput();
			if(currentOutput != null){
				currentOutput.write(str);
				currentOutput.flush();
			}
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	protected BufferedWriter getOutput(){
		if(out == null){
			try {
				out = new BufferedWriter(new FileWriter("/dev/ttyUSB0"));
			} catch (IOException e) {
				log.error("Not possible open "+"/dev/ttyUSB0");
			}
		}
		return out;
	}

}

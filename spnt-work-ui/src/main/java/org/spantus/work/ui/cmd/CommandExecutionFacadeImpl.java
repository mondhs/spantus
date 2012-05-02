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
package org.spantus.work.ui.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.logger.Logger;
import org.spantus.ui.SwingUtils;
import org.spantus.ui.chart.ChartUtils;
import org.spantus.utils.Assert;
import org.spantus.work.ui.container.SpantusWorkFrame;
/**
 * 
 * Action invocation facade
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 23, 2010
 *
 */
public class CommandExecutionFacadeImpl implements CommandExecutionFacade {

	private SpantusWorkFrame frame;
	Logger log = Logger.getLogger(CommandExecutionFacadeImpl.class);

	Map<String, SpantusWorkCommand> cmds;
	
	public CommandExecutionFacadeImpl(SpantusWorkFrame frame) {
		super();
		this.frame = frame;
	}
	
	public void fireEvent(Enum<?> enumCmdName){
		fireEvent(enumCmdName, null);
	}
	
	public void fireEvent(String cmdName){
		Assert.isTrue(cmdName!=null, "Command not found");
		fireEvent(cmdName, null);
	}

	public void fireEvent(Enum<?> enumCmdName, Object object) {
		fireEvent(enumCmdName.name(), object);
		
	}

	public void fireEvent(String cmdName, Object object) {
		Assert.isTrue(cmdName!=null);
		SpantusWorkUIEvent event = new SpantusWorkUIEvent(this,frame.getInfo(), 
				cmdName, object);
		log.debug("[fireEvent] cmdName: {0}", cmdName);
		try{
			frame.getEventMulticaster().multicastEvent(event);
		}catch (Throwable e) {
			SwingUtils.showError(frame, e,"");
		}
	}

	
	
	public void changedReader(IExtractorInputReader reader) {
		frame.getSampleRepresentationPanel().changedReader(reader);
	}

	public void refresh() {
		frame.getSampleRepresentationPanel().refresh();
	}
	public void refreshValue(Double value){
		if(value!=null){
                        Double sqr_value = value.doubleValue() * value;
                        if(sqr_value != 0){
                            sqr_value = Math.log(sqr_value);
                        }
			frame.getRecordMonitor().setValue(sqr_value.intValue());
		}
        }


	public void started(Long total) {
		frame.getSampleRepresentationPanel().started(total);

	}

	public void processed(Long current, Long total) {
		frame.getSampleRepresentationPanel().processed(current, total);

	}

	public void ended() {
		frame.getSampleRepresentationPanel().ended();

	}

	public void registerProcessedFrameLinstener(
			ProcessedFrameLinstener linstener) {
		frame.getSampleRepresentationPanel().registerProcessedFrameLinstener(linstener);

	}

	public IExtractorInputReader getReader() {
		return frame.getSampleRepresentationPanel().getSampleChart().getReader();
	}

	public void setReader(IExtractorInputReader reader) {
		frame.getSampleRepresentationPanel().getSampleChart().setReader(reader);
	}

	public void writeChartAsPNG(File file) throws IOException {
		 ChartUtils.writeAsPNG(frame.getSampleRepresentationPanel().getSampleChart().getChart(), file);
	}

	public void changedZoom(Double from, Double length) {
		frame.getSampleRepresentationPanel().getSampleChart().getChart().changedZoom(from, length);
		frame.getSampleRepresentationPanel().getSampleChart().getChart().setSize(frame.getSize());
//		frame.getSampleRepresentationPanel().getSampleChart().getChart().repaint(30L);
	}

	public void updateContent() {
		frame.getSampleRepresentationPanel().getSampleChart().updateContent();
	}

        public void updateMarkers() {
            frame.getSampleRepresentationPanel().getSampleChart().updateMarker();
	}


	public void initialize() {
		frame.getSampleRepresentationPanel().getSampleChart().initialize();

	}

	public void reload() {
		frame.reload();
	}

	public void newProject() {
		frame.newProject();
	}

	public void showError(Throwable throwable, String message) {
		SwingUtils.showError(frame, throwable, message);
	}


}

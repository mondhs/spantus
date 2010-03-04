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

import java.util.HashMap;
import java.util.Map;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.file.CurrentProjectChangedCmd;
import org.spantus.work.ui.cmd.file.ExportCmd;
import org.spantus.work.ui.cmd.file.ImportCmd;
import org.spantus.work.ui.cmd.file.NewProjectCmd;
import org.spantus.work.ui.cmd.file.OpenProjectCmd;
import org.spantus.work.ui.cmd.file.SaveProjectCmd;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 23, 2010
 *
 */
public class CommandBuilder {

	Logger log = Logger.getLogger(getClass());

	

	
	public static Map<String, SpantusWorkCommand> create(CommandExecutionFacade executionFacade) {
		Map<String, SpantusWorkCommand> cmds =  new HashMap<String, SpantusWorkCommand>();;
		
		cmds.put(GlobalCommands.file.exit.name(),
				new ExitCmd(executionFacade));
		
		createSampleCmd(cmds,  executionFacade );
		createFileCmd(cmds, executionFacade );
		createMiscCmd(cmds, executionFacade);
		return cmds;
	}

//	public Map<String, SpantusWorkCommand> getCmds() {
//		if (cmds == null) {
//			cmds =
//		}
//		return cmds;
//	}

//	public void execute(SpantusWorkUIEvent event) {
//		log.debug("cmd: " + event.getCmd());
//		if (event.getCmd() == null) {
//			return ;
//		}
//		
////		SpantusWorkUIEvent newEvent = getCmds().get(event.getCmd()).execute(event);
////		if (newEvent == null) {
////			return null;
////		} else {
////			return execute(newEvent);
////		}
//	}

	private static void createSampleCmd(Map<String, SpantusWorkCommand> cmds, CommandExecutionFacade executionFacade) {

		cmds.put(GlobalCommands.sample.record.name(),
				new RecordCmd(executionFacade));
		cmds.put(GlobalCommands.sample.play.name(), new PlayCmd(executionFacade));

		cmds.put(GlobalCommands.sample.stop.name(), new StopCmd(executionFacade));

		cmds.put(GlobalCommands.sample.zoomin.name(),
				new ZoomInCmd(executionFacade));
		cmds.put(GlobalCommands.sample.zoomout.name(),
				new ZoomOutCmd(executionFacade));

		cmds.put(GlobalCommands.sample.reloadSampleChart.name(),
				new ReloadSampleChartCmd(executionFacade));

	}
	/**
	 * 
	 * @param handler
	 * @param frame
	 * @param reloadableComponent
	 * @param sampleChart
	 */
	private static void createFileCmd(Map<String, SpantusWorkCommand> cmds, CommandExecutionFacade executionFacade) {
		
		cmds.put(GlobalCommands.file.open.name(), new OpenCmd(executionFacade));
		cmds.put(GlobalCommands.file.newProject.name(),
				new NewProjectCmd(executionFacade));
		cmds.put(GlobalCommands.file.openProject.name(),
				new OpenProjectCmd(executionFacade));
		cmds.put(GlobalCommands.file.saveProject.name(),
				new SaveProjectCmd(executionFacade));

		cmds.put(GlobalCommands.file.currentProjectChanged.name(),
				new CurrentProjectChangedCmd(executionFacade));
		cmds.put(GlobalCommands.file.exportFile.name(),
				new ExportCmd(executionFacade));
		cmds.put(GlobalCommands.file.importFile.name(),
				new ImportCmd(executionFacade));
	}
	/**
	 * 
	 * @param handler
	 * @param frame
	 * @param lisetener
	 * @param processedFrameLinstener
	 * @param reloadableComponent
	 */
	private static void createMiscCmd(Map<String, SpantusWorkCommand> cmds,
			CommandExecutionFacade executionFacade) {
		
		cmds.put(GlobalCommands.help.about.name(),
				new AboutCmd(executionFacade));
		
		cmds.put(GlobalCommands.help.signalInfo.name(),
				new SignalInfoCmd(executionFacade));

		
		cmds.put(GlobalCommands.help.userGuide.name(),
				new ShowDocumentationCmd(executionFacade));

		CurrentSampleChangedCmd currentSampleChanged = new CurrentSampleChangedCmd(
				executionFacade);
		cmds.put(GlobalCommands.file.currentSampleChanged.name(),
				currentSampleChanged);

		cmds.put(GlobalCommands.tool.option.name(),
				new OptionCmd(executionFacade));

		cmds.put(GlobalCommands.tool.reloadResources.name(),
				new ReloadResourcesCmd(executionFacade));

		cmds.put(GlobalCommands.tool.saveSegments.name(),
				new SaveSegmentCmd(executionFacade));

		cmds.put(
				GlobalCommands.tool.autoSegmentation.name(),
				new AutoSegmentationCmd(executionFacade));

	}

}
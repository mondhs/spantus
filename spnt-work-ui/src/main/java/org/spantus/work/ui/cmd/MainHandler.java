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

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.file.CurrentProjectChangedCmd;
import org.spantus.work.ui.cmd.file.ExportCmd;
import org.spantus.work.ui.cmd.file.ImportCmd;
import org.spantus.work.ui.cmd.file.NewProjectCmd;
import org.spantus.work.ui.cmd.file.OpenProjectCmd;
import org.spantus.work.ui.cmd.file.SaveProjectCmd;
import org.spantus.work.ui.container.SpantusWorkFrame;
import org.spantus.work.ui.dto.SpantusWorkInfo;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 23, 2010
 *
 */
public class MainHandler implements SpantusWorkCommand {

	Logger log = Logger.getLogger(getClass());

	private Map<String, SpantusWorkCommand> cmds;

	public void initialize(SpantusWorkFrame frame) {
		this.getCmds().put(GlobalCommands.file.exit.name(),
				new ExitCmd(frame));
		CommandExecutionFacade executionFacade = new CommandExecutionFacade(frame);
		createSampleCmd(this,  executionFacade );
		createFileCmd(this, frame, executionFacade );
		createMiscCmd(this, frame, executionFacade);
	}

	public Map<String, SpantusWorkCommand> getCmds() {
		if (cmds == null) {
			cmds = new HashMap<String, SpantusWorkCommand>();
		}
		return cmds;
	}

	public String execute(String cmdName, SpantusWorkInfo info) {
		log.debug("cmd: " + cmdName);
		if (cmdName == null) {
			return null;
		}
		String newCmdName = getCmds().get(cmdName).execute(cmdName, info);
		if (newCmdName == null) {
			return null;
		} else {
			return execute(newCmdName, info);
		}
	}
	public String execute(Enum<?> enumCmdName, SpantusWorkInfo info) {
		return execute(enumCmdName.name(), info);
	}

	private void createSampleCmd(MainHandler handler, CommandExecutionFacade executionFacade) {

		handler.getCmds().put(GlobalCommands.sample.record.name(),
				new RecordCmd(executionFacade, handler));
		handler.getCmds().put(GlobalCommands.sample.play.name(), new PlayCmd());

		handler.getCmds().put(GlobalCommands.sample.stop.name(), new StopCmd());

		handler.getCmds().put(GlobalCommands.sample.zoomin.name(),
				new ZoomInCmd(executionFacade));
		handler.getCmds().put(GlobalCommands.sample.zoomout.name(),
				new ZoomOutCmd(executionFacade));

		handler.getCmds().put(GlobalCommands.sample.reloadSampleChart.name(),
				new ReloadSampleChartCmd(executionFacade));

	}
	/**
	 * 
	 * @param handler
	 * @param frame
	 * @param reloadableComponent
	 * @param sampleChart
	 */
	private void createFileCmd(MainHandler handler, Frame frame, CommandExecutionFacade executionFacade) {
		
		handler.getCmds().put(GlobalCommands.file.open.name(), new OpenCmd());
		handler.getCmds().put(GlobalCommands.file.newProject.name(),
				new NewProjectCmd(frame));
		handler.getCmds().put(GlobalCommands.file.openProject.name(),
				new OpenProjectCmd(frame));
		handler.getCmds().put(GlobalCommands.file.saveProject.name(),
				new SaveProjectCmd(frame));

		handler.getCmds().put(GlobalCommands.file.currentProjectChanged.name(),
				new CurrentProjectChangedCmd(frame));
		handler.getCmds().put(GlobalCommands.file.exportFile.name(),
				new ExportCmd(frame, executionFacade));
		handler.getCmds().put(GlobalCommands.file.importFile.name(),
				new ImportCmd(frame, executionFacade));
	}
	/**
	 * 
	 * @param handler
	 * @param frame
	 * @param lisetener
	 * @param processedFrameLinstener
	 * @param reloadableComponent
	 */
	private void createMiscCmd(MainHandler handler, Frame frame,
			CommandExecutionFacade executionFacade) {
		
		handler.getCmds().put(GlobalCommands.help.about.name(),
				new AboutCmd(frame));
		
		handler.getCmds().put(GlobalCommands.help.signalInfo.name(),
				new SignalInfoCmd(frame));

		
		handler.getCmds().put(GlobalCommands.help.userGuide.name(),
				new ShowDocumentationCmd(frame));

		CurrentSampleChangedCmd currentSampleChanged = new CurrentSampleChangedCmd(
				executionFacade, handler);
		handler.getCmds().put(GlobalCommands.file.currentSampleChanged.name(),
				currentSampleChanged);

		handler.getCmds().put(GlobalCommands.tool.option.name(),
				new OptionCmd(frame));

		handler.getCmds().put(GlobalCommands.tool.reloadResources.name(),
				new ReloadResourcesCmd(executionFacade));

		handler.getCmds().put(GlobalCommands.tool.saveSegments.name(),
				new SaveSegmentCmd());

		handler.getCmds().put(
				GlobalCommands.tool.autoSegmentation.name(),
				new AutoSegmentationCmd(executionFacade));

	}

}
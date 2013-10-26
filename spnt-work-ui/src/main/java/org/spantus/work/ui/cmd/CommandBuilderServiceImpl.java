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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class CommandBuilderServiceImpl implements CommandBuilderService {

	Logger log = Logger.getLogger(getClass());

	
	public Map<String, Set<SpantusWorkCommand>> createSystem(CommandExecutionFacade executionFacade) {
		Map<String, Set<SpantusWorkCommand>> cmds =  new HashMap<String, Set<SpantusWorkCommand>>();
		safePut(cmds, new AboutCmd(executionFacade));
		safePut(cmds, new ShowDocumentationCmd(executionFacade));
		safePut(cmds, new ExitCmd(executionFacade));
		createFileCmd(cmds, executionFacade );
		return cmds;

	}
	
	/* (non-Javadoc)
	 * @see org.spantus.work.ui.cmd.CommandBuilderService#create(org.spantus.work.ui.cmd.CommandExecutionFacade)
	 */
	public Map<String, Set<SpantusWorkCommand>> create(CommandExecutionFacade executionFacade) {
		Map<String, Set<SpantusWorkCommand>> cmds =  new HashMap<String, Set<SpantusWorkCommand>>();
		
		createSampleCmd(cmds,  executionFacade );
		createMiscCmd(cmds, executionFacade);
		return cmds;
	}
	
	public static void safePut(Map<String, Set<SpantusWorkCommand>> cmds, SpantusWorkCommand cmd){
		for (String key : cmd.getExpectedActions()) {
			if(cmds.get(key)==null){
				cmds.put(key, new HashSet<SpantusWorkCommand>());
			}
			cmds.get(key).add(cmd);
		}
		
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

	private static void createSampleCmd(Map<String, Set<SpantusWorkCommand>> cmds, CommandExecutionFacade executionFacade) {
		safePut(cmds, new RecordCmd(executionFacade));
		safePut(cmds, new PlayCmd(executionFacade));
		safePut(cmds, new CalculateSnrCmd(executionFacade));
		safePut(cmds, new CalculateStatisticsCmd(executionFacade));
		safePut(cmds, new StopCmd(executionFacade));
		safePut(cmds, new ZoomInCmd(executionFacade));
		safePut(cmds, new ZoomOutCmd(executionFacade));
		safePut(cmds, new ReloadSampleChartCmd(executionFacade));
                safePut(cmds, new ReloadMarkersCmd(executionFacade));

	}
	/**
	 * 
	 * @param handler
	 * @param frame
	 * @param reloadableComponent
	 * @param sampleChart
	 */
	private static void createFileCmd(Map<String, Set<SpantusWorkCommand>> cmds, CommandExecutionFacade executionFacade) {
		
		safePut(cmds, new OpenCmd(executionFacade));
		safePut(cmds, new NewProjectCmd(executionFacade));
		safePut(cmds, new OpenProjectCmd(executionFacade));
		safePut(cmds, new SaveProjectCmd(executionFacade));
		safePut(cmds, new CurrentProjectChangedCmd(executionFacade));
		safePut(cmds, new ExportCmd(executionFacade));
		safePut(cmds, new ImportCmd(executionFacade));
	}
	/**
	 * 
	 * @param handler
	 * @param frame
	 * @param lisetener
	 * @param processedFrameLinstener
	 * @param reloadableComponent
	 */
	private static void createMiscCmd(Map<String, Set<SpantusWorkCommand>> cmds,
			CommandExecutionFacade executionFacade) {
		
		safePut(cmds, new SignalInfoCmd(executionFacade));
		safePut(cmds, new CurrentSampleChangedCmd(executionFacade));
		safePut(cmds, new OptionCmd(executionFacade));
		safePut(cmds, new ReloadResourcesCmd(executionFacade));
		safePut(cmds, new SaveSegmentCmd(executionFacade));
		safePut(cmds, new AutoSegmentationCmd(executionFacade));
		safePut(cmds, new SphinxRecognitionCmd(executionFacade));
		safePut(cmds, new AppendNoiseCmd(executionFacade));
		safePut(cmds, new LearnCmd(executionFacade));
                safePut(cmds, new RecognizeCmd(executionFacade));

		
	}

}
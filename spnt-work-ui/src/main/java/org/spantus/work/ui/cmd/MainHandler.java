package org.spantus.work.ui.cmd;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.logger.Logger;
import org.spantus.work.ui.cmd.file.CurrentProjectChangedCmd;
import org.spantus.work.ui.cmd.file.ExportCmd;
import org.spantus.work.ui.cmd.file.ImportCmd;
import org.spantus.work.ui.cmd.file.NewProjectCmd;
import org.spantus.work.ui.cmd.file.OpenProjectCmd;
import org.spantus.work.ui.cmd.file.SaveProjectCmd;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.container.SpantusWorkFrame;
import org.spantus.work.ui.container.chart.SampleChart;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class MainHandler implements SpantusWorkCommand {

	Logger log = Logger.getLogger(getClass());

	private Map<String, SpantusWorkCommand> cmds;

	public void initialize(SpantusWorkFrame frame) {
		this.getCmds().put(GlobalCommands.file.exit.name(),
				new ExitCmd(frame));
		createSampleCmd(this, frame.getSampleRepresentationPanel()
				.getSampleChart(), frame.getSampleRepresentationPanel());
		createFileCmd(this, frame, frame, frame.getSampleRepresentationPanel()
				.getSampleChart());
		createMiscCmd(this, frame, frame.getSampleRepresentationPanel(),
				frame.getSampleRepresentationPanel(), frame, frame.getSampleRepresentationPanel()
				.getSampleChart());
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

	private void createSampleCmd(MainHandler handler, SampleChart sampleChart,
			SampleChangeListener lisetener) {

		handler.getCmds().put(GlobalCommands.sample.record.name(),
				new RecordCmd(lisetener, handler));
		handler.getCmds().put(GlobalCommands.sample.play.name(), new PlayCmd());

		handler.getCmds().put(GlobalCommands.sample.stop.name(), new StopCmd());

		handler.getCmds().put(GlobalCommands.sample.zoomin.name(),
				new ZoomInCmd(sampleChart));
		handler.getCmds().put(GlobalCommands.sample.zoomout.name(),
				new ZoomOutCmd(sampleChart));

		handler.getCmds().put(GlobalCommands.sample.reloadSampleChart.name(),
				new ReloadSampleChartCmd(sampleChart));

	}
	/**
	 * 
	 * @param handler
	 * @param frame
	 * @param reloadableComponent
	 * @param sampleChart
	 */
	private void createFileCmd(MainHandler handler, Frame frame,
			ReloadableComponent reloadableComponent, SampleChart sampleChart) {
		
		handler.getCmds().put(GlobalCommands.file.open.name(), new OpenCmd());
		handler.getCmds().put(GlobalCommands.file.newProject.name(),
				new NewProjectCmd(frame));
		handler.getCmds().put(GlobalCommands.file.openProject.name(),
				new OpenProjectCmd(frame));
		handler.getCmds().put(GlobalCommands.file.saveProject.name(),
				new SaveProjectCmd(frame));

		handler.getCmds().put(GlobalCommands.file.currentProjectChanged.name(),
				new CurrentProjectChangedCmd(reloadableComponent));
		handler.getCmds().put(GlobalCommands.file.exportFile.name(),
				new ExportCmd(frame, sampleChart));
		handler.getCmds().put(GlobalCommands.file.importFile.name(),
				new ImportCmd(frame, sampleChart));
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
			SampleChangeListener lisetener,
			ProcessedFrameLinstener processedFrameLinstener,
			ReloadableComponent reloadableComponent,
			SampleChart sampleChart) {
		handler.getCmds().put(GlobalCommands.help.about.name(),
				new AboutCmd(frame));

		handler.getCmds().put(GlobalCommands.help.userGuide.name(),
				new ShowDocumentationCmd(frame));

		CurrentSampleChangedCmd currentSampleChanged = new CurrentSampleChangedCmd(
				lisetener, processedFrameLinstener, handler);
		handler.getCmds().put(GlobalCommands.file.currentSampleChanged.name(),
				currentSampleChanged);

		handler.getCmds().put(GlobalCommands.tool.option.name(),
				new OptionCmd(frame));

		handler.getCmds().put(GlobalCommands.tool.reloadResources.name(),
				new ReloadResourcesCmd(reloadableComponent));

		handler.getCmds().put(GlobalCommands.tool.saveSegments.name(),
				new SaveSegmentCmd());

		handler.getCmds().put(
				GlobalCommands.tool.autoSegmentation.name(),
				new AutoSegmentationCmd(sampleChart));

	}

}
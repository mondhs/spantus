package org.spantus.work.ui.cmd;

import java.io.File;
import java.io.IOException;

import org.spantus.chart.util.ChartUtils;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.work.ui.container.ReloadableComponent;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.container.SpantusWorkFrame;

public class CommandExecutionFacade implements SampleChangeListener,
		ReloadableComponent {

	private SpantusWorkFrame frame;

	public CommandExecutionFacade(SpantusWorkFrame frame) {
		super();
		this.frame = frame;
	}

	public void changedReader(IExtractorInputReader reader) {
		frame.getSampleRepresentationPanel().changedReader(reader);
	}

	public void refresh() {
		frame.getSampleRepresentationPanel().refresh();

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

	public void changedZoom(Float from, Float length) {
		frame.getSampleRepresentationPanel().getSampleChart().getChart().changedZoom(from, length);
	}

	public void updateContent() {
		frame.getSampleRepresentationPanel().getSampleChart().updateContent();
	}

	public void initialize() {
		frame.getSampleRepresentationPanel().getSampleChart().initialize();

	}

	public void reload() {
		frame.reload();
	}
}

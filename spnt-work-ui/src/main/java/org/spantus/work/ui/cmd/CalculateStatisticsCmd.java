package org.spantus.work.ui.cmd;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.marker.Marker;
import org.spantus.work.services.calc.CalculateSnr;
import org.spantus.work.services.calc.CalculateSnr.segmentStatics;
import org.spantus.work.services.calc.impl.CalculateSnrImpl;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class CalculateStatisticsCmd extends AbsrtactCmd {

	private CalculateSnr calculateSnr;

	public CalculateStatisticsCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}

	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.sample.calculateStatistics.name());
	}

	public String execute(SpantusWorkInfo ctx) {
		
		Marker marker = ((Marker) getCurrentEvent().getValue());
		Set<IExtractor> extacts = getReader().getExtractorRegister();
		StringBuilder sb = new StringBuilder();
		for (IExtractor iExtractor : extacts) {
			Map<segmentStatics, Double> stats = getCalculateSnr().calculateStatistics(iExtractor, marker.getStart(), marker.getLength());
			String msg = MessageFormat.format("{0}: [mean: {1}; min: {2}; max: {3}]", iExtractor.getName(), stats.get(segmentStatics.mean),
					stats.get(segmentStatics.min), stats.get(segmentStatics.max));
			sb.append(msg).append("\n");
		}
		JOptionPane.showMessageDialog(null, sb.toString(),
				"Statistics",
				JOptionPane.INFORMATION_MESSAGE);
//		Long current = null;
//		for (Marker marker : segments.getMarkers()) {
//			if(current == null){
//				current = marker.getEnd();
//			}
//		}
		
		return null;
	}

	

	private CalculateSnr getCalculateSnr() {
		if(calculateSnr == null){
			calculateSnr = new CalculateSnrImpl();
		}
		return calculateSnr;
	}


}

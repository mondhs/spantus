package org.spantus.work.ui.cmd;

import java.util.Set;

import javax.swing.JOptionPane;

import org.spantus.core.extractor.IExtractor;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.work.services.calc.CalculateSnr;
import org.spantus.work.services.calc.impl.CalculateSnrImpl;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class CalculateSnrCmd extends AbsrtactCmd {

	private CalculateSnr calculateSnr;

	public CalculateSnrCmd(CommandExecutionFacade executionFacade) {
		super(executionFacade);
	}

	public Set<String> getExpectedActions() {
		return createExpectedActions(GlobalCommands.sample.calculateSNR.name());
	}

	public String execute(SpantusWorkInfo ctx) {
		MarkerSet segments = extractPhonemes(ctx.getProject().getSample().getMarkerSetHolder());
		Set<IExtractor> extacts = getReader().getExtractorRegister();
		StringBuilder sb = new StringBuilder();
		for (IExtractor iExtractor : extacts) {
			Double snr = calculateSNR(iExtractor,segments);
			sb.append(iExtractor.getName()).append("=").append(snr).append("\n");
		}
		JOptionPane.showMessageDialog(null, sb.toString(),
				"SNR",
				JOptionPane.INFORMATION_MESSAGE);
//		Long current = null;
//		for (Marker marker : segments.getMarkers()) {
//			if(current == null){
//				current = marker.getEnd();
//			}
//		}
		
		return null;
	}

	private Double calculateSNR(IExtractor iExtractor, MarkerSet segments) {
		return getCalculateSnr().calculate(iExtractor, segments);
	}

	private CalculateSnr getCalculateSnr() {
		if(calculateSnr == null){
			calculateSnr = new CalculateSnrImpl();
		}
		return calculateSnr;
	}

	private MarkerSet extractPhonemes(MarkerSetHolder markerSetHolder) {
		MarkerSet markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.phone.name());
		if(markerSet == null){
			markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.syllable.name());
		}
		if(markerSet == null){
			markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.word.name());
		}
		if(markerSet == null){
			markerSet = markerSetHolder.getMarkerSets().get(MarkerSetHolderEnum.sentence.name());
		}
		return markerSet;
	}

}

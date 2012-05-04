/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.work.ui.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;

import org.spantus.core.IValues;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.io.ProcessedFrameLinstener;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLLocalConstraint;
import org.spantus.math.dtw.DtwServiceJavaMLImpl.JavaMLSearchWindow;
import org.spantus.math.services.MathServicesFactory;
import org.spantus.utils.StringUtils;
import org.spantus.work.ui.dto.RecognitionConfig;

/**
 * 
 * @author mondhs
 */
public class MatchingServiceImpl {
	private static MatchingServiceImpl matchingService;

	public static MatchingServiceImpl getInstance() {
		if (matchingService == null) {
			matchingService = new MatchingServiceImpl();
		}
		return matchingService;
	}

	private CorpusServiceBaseImpl corpusService;
	private CorpusRepositoryFileImpl corpusRepo;

	/**
	 * Update configuration
	 * 
	 * @param ctx
	 */
	public void update(RecognitionConfig recognitionConfig, ProcessedFrameLinstener listener) {
		String corpusPath = recognitionConfig.getRepositoryPath();
		String searchWindowStr = recognitionConfig.getDtwWindow();
		String localConstraintStr = recognitionConfig.getLocalConstraint();
		JavaMLSearchWindow searchWindow = JavaMLSearchWindow.FullWindow;
		if (StringUtils.hasText(searchWindowStr)) {
		searchWindow = JavaMLSearchWindow
				.valueOf(searchWindowStr);
		}
		JavaMLLocalConstraint localConstraint = JavaMLLocalConstraint.Default;
		if (StringUtils.hasText(localConstraintStr)) {
			localConstraint = JavaMLLocalConstraint.valueOf(localConstraintStr);
		}
		Float radius = recognitionConfig.getRadius();

		File corpusDir = new File(corpusPath);
		if (!corpusDir.equals(getCorpusRepository().getRepoDir())) {
			getCorpusRepository().setRepositoryPath(corpusPath);
			getCorpusRepository().flush();
		}

		corpusService = new CorpusServiceBaseImpl();
		corpusService.setDtwService(MathServicesFactory.createDtwService(
				radius, searchWindow, localConstraint));
		corpusService.setCorpus(getCorpusRepository());
		corpusService.setIncludeFeatures(new HashSet<String>());
		corpusService.getIncludeFeatures().add(
				ExtractorEnum.MFCC_EXTRACTOR.name());
		corpusService.getIncludeFeatures().add(
				ExtractorEnum.PLP_EXTRACTOR.name());
		corpusService.getIncludeFeatures().add(
				ExtractorEnum.LPC_EXTRACTOR.name());
		
		corpusService.getListeners().add(listener);
		// corpusService.getIncludeFeatures().add(ExtractorEnum.FFT_EXTRACTOR.name());
		// corpusServiceimpl.getIncludeFeatures().add(ExtrasSctorEnum.SPECTRAL_FLUX_EXTRACTOR.name());

		getCorpusRepository().flush();
	}

	/**
	 * 
	 * @return
	 */
	public CorpusServiceBaseImpl getCorpusService() {
		return corpusService;
	}

	public void setCorpusService(CorpusServiceBaseImpl corpusService) {
		this.corpusService = corpusService;
	}

	public CorpusRepositoryFileImpl getCorpusRepository() {
		if (corpusRepo == null) {
			CorpusRepositoryFileImpl corpusFileRepo = new CorpusRepositoryFileImpl();
			corpusRepo = corpusFileRepo;
		}
		return corpusRepo;
	}

	public void learn(String label, Map<String, IValues> fvv,
			AudioInputStream ais) {
		SignalSegment corpusEntry = getCorpusService().create(label, fvv);
		getCorpusService().learn(corpusEntry, ais);
	}

	public List<RecognitionResult> findMultipleMatch(
			Map<String, IValues> fvv) {
		return getCorpusService().findMultipleMatchFull(fvv);
	}

	public RecognitionResult match(Map<String, IValues> fvv) {
		RecognitionResult result = getCorpusService().match(fvv);
//		Double mfccScore = result.getScores().get("MFCC_EXTRACTOR");
//		Double plpScore = result.getScores().get("PLP_EXTRACTOR");
//		if (mfccScore != null && mfccScore > 250) {
//			return null;
//		}
//		if (plpScore != null && plpScore > 15) {
//			return null;
//		}
		return result;
	}
	

}

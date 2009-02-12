package org.spnt.recognition.dtw;

import java.util.HashMap;
import java.util.Map;

import org.spantus.core.FrameVectorValues;
import org.spantus.logger.Logger;
import org.spantus.math.dtw.DtwService;
import org.spantus.math.services.MathServicesFactory;
import org.spnt.recognition.RecognitionService;

public class RecognitionServiceDtwImpl implements RecognitionService {

	private Logger log = Logger.getLogger(getClass()); 
	
	private DtwService dtwService;

	private RecognitionModelRepository sampleRepository;
	
	public RecognitionServiceDtwImpl() {
		dtwService = MathServicesFactory.createDtwService();
	}

	public String match(FrameVectorValues test) {
		Map<String, DtwRecognitionResult> results = new HashMap<String, DtwRecognitionResult>();
		String match = process(test, results );
		return match;
	}
	
	protected String process(FrameVectorValues test, Map<String, DtwRecognitionResult> results){
		Float min = Float.MAX_VALUE;
		String match = null;
		for (RecognitionModelEntry info : sampleRepository.findAllEntries()) {
			DtwRecognitionResult res = calculateRecognitionResult(test, info);
			results.put(info.getName(), calculateRecognitionResult(test, info));
			if(min > res.getDistance()){
				min = res.getDistance();
				match = info.getName();
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Match: ").append(match).append("\n");
		for (DtwRecognitionResult result : results.values()) {
			sb.append("result: ").append(result.getInfo().getName())
			.append("(").append(result.getDistance()).append(")\n");
		}
		log.debug(sb.toString());
		
		
		return match;
	}
	
	protected DtwRecognitionResult calculateRecognitionResult(FrameVectorValues test,
			RecognitionModelEntry entry) {
		DtwRecognitionResult result = new DtwRecognitionResult();
		result.setInfo(entry);
		result.setDistance(dtwService.calculateDistance(DTWUtils.createDtwInfo(
				entry.getVals(), test)));
		return result;
	}

	public void setSampleRepository(RecognitionModelRepository sampleRepository) {
		this.sampleRepository = sampleRepository;
	}
}

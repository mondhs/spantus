package org.spantus.server.services.recognition.controler;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.core.beans.RecognitionResult;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.service.CorpusService;
import org.spantus.server.dto.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RecognitionHandlingController {
	
	private static final String CORPORA_JSON_VIEW_KEY = "CORPORA_JSON_VIEW_KEY";
	private static Logger LOG = LoggerFactory.getLogger(RecognitionHandlingController.class);
	
	@Autowired
	CorpusService corpusService;
	@RequestMapping(method = RequestMethod.GET, value = "/recognition/recognize" )
	public ModelAndView ping() throws IOException {
		return newMAV(ResultStatus.success("Use PUT method instead GET"));
	}



	@RequestMapping(method = RequestMethod.PUT, value = "/recognition/recognize" )
	public ModelAndView recognize(@RequestBody final SignalSegment segment) throws IOException {
		LOG.error("[recognize]input: " + segment.getMarker().getLabel());
		List<RecognitionResult> entryList = corpusService.findMultipleMatchFull(segment.findAllFeatures());
//		if(LOG.isDebugEnabled()){
			for (RecognitionResult recognitionResult : entryList) {
				recognitionResult.getInfo().setFeatureFrameValuesMap(null);
				recognitionResult.getInfo().setFeatureFrameVectorValuesMap(null);
				recognitionResult.getDetails().setPath(null);
				LOG.error("Result: " + recognitionResult.getInfo().getMarker().getLabel());
			}
//		}
		return newMAV(entryList);
	}
	
	private ModelAndView newMAV(ResultStatus status) {
		ModelAndView mav = new ModelAndView(CORPORA_JSON_VIEW_KEY);
		mav.addObject(status);
		return mav;
	}
	
	private ModelAndView newMAV(List<RecognitionResult> entryList) {
		ModelAndView mav = new ModelAndView(CORPORA_JSON_VIEW_KEY);
		mav.addObject(entryList);
		return mav;
	}
	
}

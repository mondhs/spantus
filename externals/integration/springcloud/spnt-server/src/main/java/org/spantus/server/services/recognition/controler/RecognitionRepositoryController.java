package org.spantus.server.services.recognition.controler;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.server.dto.ResultStatus;
import org.spantus.server.dto.SignalSegmentEntry;
import org.spantus.server.dto.SignalSegmentList;
import org.spantus.server.services.SignalSegmentEntryDao;
import org.spantus.server.services.recognition.repository.SignalSegmentEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RecognitionRepositoryController {
	private static final String CORPORA_JSON_VIEW_KEY = "CORPORA_JSON_VIEW_KEY";

	private static Logger LOG = LoggerFactory.getLogger(RecognitionRepositoryController.class);
	
	@Autowired
	SignalSegmentEntryRepository signalSegmentEntryRepository;
	
	@Autowired
	SignalSegmentEntryDao signalSegmentEntryDao;
	
	@RequestMapping(method = RequestMethod.GET, value = "/recognition/repo/test" )
	public ModelAndView getSegment() throws IOException {
		SignalSegment segment = new SignalSegment();
		segment.setMarker(new Marker());
		segment.getMarker().setLabel("__label__");
		segment.getMarker().setStart(10L);
		segment.getMarker().setLength(100L);
		segment.setFeatureFrameValuesMap(new HashMap<String, FrameValuesHolder>());
		segment.setFeatureFrameVectorValuesMap(new HashMap<String, FrameVectorValuesHolder>());
		FrameValues frameValues = new FrameValues();
		frameValues.setSampleRate(45.714285714285715);
		frameValues.setSampleRate(839D);
		frameValues.add(1D);frameValues.add(2D);frameValues.add(3D);
		FrameVectorValues frameVectorValues = new FrameVectorValues();
		frameVectorValues.add(frameValues);
		frameVectorValues.add(frameValues);
		frameVectorValues.add(frameValues);
		frameVectorValues.setSampleRate(839D);
		
		segment.getFeatureFrameValuesMap().put("__FrameValues1__", new FrameValuesHolder(frameValues));
		segment.getFeatureFrameValuesMap().put("__FrameValues2__", new FrameValuesHolder(frameValues));
		segment.getFeatureFrameVectorValuesMap().put("__FrameVectorValues1__", new FrameVectorValuesHolder(frameVectorValues));
		segment.getFeatureFrameVectorValuesMap().put("__FrameVectorValues2__", new FrameVectorValuesHolder(frameVectorValues));
		
		return newMAV(segment);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/recognition/repo" )
	public ModelAndView findAll() throws IOException {
		Page<SignalSegmentEntry> elements = signalSegmentEntryRepository.findAll(new PageRequest(0, 10));
		LOG.error(format("[findAll] request: page {0} of {1}, elements in repo {2}", elements.getNumber(), elements.getTotalPages(), elements.getTotalElements()));
//		Iterable<SignalSegmentEntry> elements = signalSegmentEntryRepository.findAll();
		SignalSegmentList segmentList = new SignalSegmentList(new ArrayList<SignalSegmentEntry>());
		for (SignalSegmentEntry signalSegmentEntry : elements) {
			LOG.error("[findAll] request: {}", signalSegmentEntry.get_id());
			segmentList.getSignalSegment().add(signalSegmentEntry);
		}
		LOG.error("[findAll] request: {}", segmentList.getSignalSegment().size());
		return newMAV(segmentList);
	}
	

	@RequestMapping(method = RequestMethod.GET, value = "/recognition/repo/{id}" )
	public ModelAndView findById(@PathVariable String id) throws IOException {
		SignalSegmentEntry segment = signalSegmentEntryRepository.findOne(new ObjectId(id));
		LOG.debug("[findById] request: {}", segment.getObjectId());
		return newMAV(segment);
	}

	@RequestMapping(method = RequestMethod.PUT,  value = "/recognition/repo")
	public ModelAndView insert(@RequestBody final SignalSegment segment) throws IOException {
		SignalSegmentEntry entry = new SignalSegmentEntry();
		entry.setSignalSegment(segment);
		entry = signalSegmentEntryRepository.save(entry);
		String msg = format("[insert] id: {0}; Label: {1}", entry.get_id(), segment.getMarker().getLabel());
		return newMAV(ResultStatus.success(msg));
	}

	@RequestMapping(method = RequestMethod.PUT,  value = "/recognition/repo/recognizeble/{id}/{recognizable}")
	public ModelAndView updateRecognizable(@PathVariable final String id, @PathVariable final Boolean recognizable) throws IOException {
		signalSegmentEntryDao.updateFirstRecognizable(id, recognizable );
		return newMAV(ResultStatus.success(format("[insert] request: [{0}] {1}", id, recognizable)));
	}

	
	
	@RequestMapping(method = RequestMethod.POST,  value = "/recognition/repo/{id}")
	public ModelAndView save(@PathVariable String id, @RequestBody final SignalSegment segment) throws IOException {
		SignalSegmentEntry entry = signalSegmentEntryRepository.findOne(new ObjectId(id));
		entry.setSignalSegment(segment);
		entry = signalSegmentEntryRepository.save(entry);
		return newMAV(ResultStatus.success(format("updated {0}", id)));
	}
	

	
	@RequestMapping(method = RequestMethod.DELETE,  value = "/recognition/repo/{id}")
	public ModelAndView remove(@PathVariable String id) throws IOException {
		signalSegmentEntryRepository.delete(new ObjectId(id));
		LOG.debug("[remove] request: {}", id);
		return newMAV(ResultStatus.success());
	}
	

	private ModelAndView newMAV(ResultStatus resultStatus) {
		ModelAndView mav = new ModelAndView(CORPORA_JSON_VIEW_KEY);
		mav.addObject(resultStatus);
		return mav;
	}

	private ModelAndView newMAV(SignalSegment obj) {
		ModelAndView mav = new ModelAndView(CORPORA_JSON_VIEW_KEY);
		mav.addObject(obj);
		return mav;
	}
	private ModelAndView newMAV(SignalSegmentEntry obj) {
		ModelAndView mav = new ModelAndView(CORPORA_JSON_VIEW_KEY);
		mav.addObject(obj);
		return mav;
	}
	private ModelAndView newMAV(SignalSegmentList segment) {
		ModelAndView mav = new ModelAndView(CORPORA_JSON_VIEW_KEY);
		mav.addObject(segment);
		return mav;
	}
}

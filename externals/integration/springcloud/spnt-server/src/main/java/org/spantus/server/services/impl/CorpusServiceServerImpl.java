package org.spantus.server.services.impl;

import org.spantus.core.beans.SignalSegment;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.server.dto.SignalSegmentEntry;
import org.spantus.server.services.recognition.repository.SignalSegmentEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;


@Component(value = "corpusService")
public class CorpusServiceServerImpl extends CorpusServiceBaseImpl {
//	private static final Logger LOG = LoggerFactory
//			.getLogger(CorpusServiceServerImpl.class);
	
	@Autowired
	SignalSegmentEntryRepository signalSegmentEntryRepository;




	protected Iterable<SignalSegment> findAll() {
		Function<SignalSegmentEntry, SignalSegment> transformFunction = new Function<SignalSegmentEntry, SignalSegment>() {
			@Override
			public SignalSegment apply(SignalSegmentEntry input) {
				return input.getSignalSegment();
			}

		};

		return Iterables.transform(
				signalSegmentEntryRepository.findByRecognizable(true),
				transformFunction);
	}


}

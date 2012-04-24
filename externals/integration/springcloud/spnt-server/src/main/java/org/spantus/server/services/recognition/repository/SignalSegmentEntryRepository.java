package org.spantus.server.services.recognition.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.spantus.server.dto.SignalSegmentEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

@org.springframework.stereotype.Repository
public interface SignalSegmentEntryRepository extends
		CrudRepository<SignalSegmentEntry, ObjectId>{
	
	Page<SignalSegmentEntry> findAll(Pageable pageable);
	
	List<SignalSegmentEntry> findByRecognizable(Boolean recognizable);

	Page<SignalSegmentEntry> findByRecognizable(Boolean recognizable, Pageable pageable);

	List<SignalSegmentEntry> findBySignalSegment_Name(String name);

}

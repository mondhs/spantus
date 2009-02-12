package org.spnt.recognition.dtw.test;

import java.util.ArrayList;
import java.util.List;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spnt.recognition.dtw.RecognitionModelEntry;
import org.spnt.recognition.dtw.RecognitionModelRepository;

public class SampleRepositoryTestImpl implements RecognitionModelRepository {

	static Float[][] samplesArr = new Float[][]{
			new Float[]{1f, 2f, 1f, 4f, 4f, 5f},
			new Float[]{2f, 3f, 5f, 7f, 8f, 9f},
			new Float[]{1f, 1f, 2f, 3f, 4f, 5f, 5f},
	};
	
	List<RecognitionModelEntry> entries;
	
	public List<RecognitionModelEntry> findAllEntries() {
		if(entries == null){
			entries = new ArrayList<RecognitionModelEntry>();
			int i = 0;
			for (Float[] vector : samplesArr) {
				RecognitionModelEntry entry = new RecognitionModelEntry();
				entry.setName("test" + (i++));
				entry.setVals(getVals(vector));
				entries.add(entry);
			}
		}
		return entries;
	}

	public FrameVectorValues getVals(Float[] vector){
		FrameVectorValues calculatedVals = new FrameVectorValues();
		for (Float f : vector) {
			FrameValues vals = new FrameValues();
			vals.add(f+.1f);
			vals.add(f);
			vals.add(f-.1f);
			calculatedVals.add(vals);
		}
		return  calculatedVals;
		
	}

	public void save(RecognitionModelEntry entry) {
		throw new RuntimeException("Not implemented");
	}
	
}

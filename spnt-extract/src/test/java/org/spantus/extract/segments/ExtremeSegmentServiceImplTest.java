package org.spantus.extract.segments;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.extractor.segments.ExtremeSegmentServiceImpl;
import org.spantus.extractor.segments.offline.ExtremeEntry;
import org.spantus.extractor.segments.offline.ExtremeEntry.FeatureStates;
import org.spantus.extractor.segments.offline.ExtremeSegment;

public class ExtremeSegmentServiceImplTest {
	
	ExtremeSegmentServiceImpl extremeSegmentService;
	
	@Before
	public void onSetup() {
		extremeSegmentService = new ExtremeSegmentServiceImpl();
	}
	
	@Test
	public void testAngle() throws UnsupportedAudioFileException,
			IOException {
		//given
		ExtremeSegment current = new ExtremeSegment();
		ExtremeEntry startEntry = new ExtremeEntry(1, 1D, FeatureStates.min);
		ExtremeEntry peadkEntry = new ExtremeEntry(5, 5D, FeatureStates.max);
		ExtremeEntry endEntry = new ExtremeEntry(10,10D, FeatureStates.min);
		current.setStartEntry(startEntry);
		current.setPeakEntry(peadkEntry);
		current.setEndEntry(endEntry);
		
		//when
		Double angle = extremeSegmentService.angle(current);
		//then
		Assert.assertEquals("Angle",180D, angle);
	
	}
}

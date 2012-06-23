package org.spantus.server.services.impl.test;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spantus.core.beans.SignalSegment;
import org.spantus.server.dto.SignalSegmentEntry;
import org.spantus.server.services.impl.SignalSegmentEntryMongoDao;
import org.spnt.servlet.dao.test.AbstractEmbededIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SignalSegmentEntryMongoDaoIntegrationTest extends
AbstractEmbededIntegrationTest {

	// private static final String COLLECTION = "SignalSegments";
	private ArrayList<SignalSegmentEntry> signalSegmentEntries;
	private SignalSegmentEntry firstEntry;
	private SignalSegmentEntry secondEntry;

	@Autowired
	SignalSegmentEntryMongoDao SignalSegmentEntryDao;

	@Override
	@Before
	public void setUp() throws IOException{
		super.setUp();
		signalSegmentEntries = new ArrayList<SignalSegmentEntry>();
		firstEntry = new SignalSegmentEntry(new SignalSegment());
		signalSegmentEntries.add(firstEntry);
		getOperations().save(firstEntry);
		secondEntry = new SignalSegmentEntry(new SignalSegment());
		signalSegmentEntries.add(secondEntry);
		getOperations().save(secondEntry);
	}
	
	
	@After
	public void onTeardown(){
		getOperations().dropCollection(SignalSegmentEntry.class);
		super.teadDown();
	}

	@Test
	public void updateFirstRecognizeble() throws Exception {
		// given
		// when
		SignalSegmentEntryDao.updateFirstRecognizable(firstEntry.get_id(), true);
		SignalSegmentEntry entry = getOperations().findOne(query(where("_id").is(firstEntry.getObjectId())), SignalSegmentEntry.class);
				
		// then
		Assert.assertEquals("Recognizable", Boolean.TRUE, entry.getRecognizable());

	}

//	@Test
//	public void insert() throws Exception {
//		// given
//		SignalSegment testSegment = new SignalSegment();
//		testSegment.setMarker(new Marker());
//		testSegment.getMarker().setLabel("__label__");
//		testSegment.getMarker().setStart(10L);
//		testSegment.getMarker().setLength(100L);
//		testSegment.setFeatureFrameValuesMap(new HashMap<String, FrameValuesHolder>());
//		testSegment.setFeatureFrameVectorValuesMap(new HashMap<String, FrameVectorValuesHolder>());
//		FrameValues frameValues = new FrameValues();
//		frameValues.setSampleRate(45.714285714285715);
//		frameValues.setSampleRate(839D);
//		frameValues.add(1D);
//		frameValues.add(2D);
//		frameValues.add(3D);
//		FrameVectorValues frameVectorValues = new FrameVectorValues();
//		frameVectorValues.add(frameValues);
//		frameVectorValues.add(frameValues);
//		frameVectorValues.add(frameValues);
//		frameVectorValues.setSampleRate(839D);
//
//		testSegment.getFeatureFrameValuesMap().put("__FrameValues1__",
//				new FrameValuesHolder(frameValues));
//		testSegment.getFeatureFrameValuesMap().put("__FrameValues2__",
//				new FrameValuesHolder(frameValues));
//		testSegment.getFeatureFrameVectorValuesMap().put("__FrameVectorValues1__",
//				new FrameVectorValuesHolder(frameVectorValues));
//		testSegment.getFeatureFrameVectorValuesMap().put("__FrameVectorValues2__",
//				new FrameVectorValuesHolder(frameVectorValues));
//		// when
//		SignalSegmentEntry signalSegmentEntry = spntRecognitionRepositoryImpl
//				.insert(testSegment);
//		// then
//		Assert.assertNotNull("Saved", signalSegmentEntry.getObjectId());
//	}

//	@Test
//	public void save() throws Exception {
//		// given
//		Long timeLng = System.currentTimeMillis();
//		String time = timeLng.toString();
//		SignalSegmentEntry signalSegment = newSignalSegment(time);
//
//		// when
//		spntRecognitionRepositoryImpl.save(signalSegment.getSignalSegment(),
//				firstEntry.getObjectId().toString());
//		Query query = new BasicQuery(String.format(
//				"{'_id' : { '$oid' : '%s' }}", firstEntry.getObjectId()
//						.toString()));
//		List<SignalSegmentEntry> entries = getOperations().find(query,
//				SignalSegmentEntry.class);
//		// then
//		Assert.assertEquals("only one entry", 1, entries.size());
//		Assert.assertEquals("Updated", time, entries.get(0).getSignalSegment()
//				.getMarker().getLabel());
//	}
//
//	@Test
//	public void remove() throws Exception {
//		// given
//
//		// when
//		spntRecognitionRepositoryImpl.remove(firstEntry.getObjectId()
//				.toString());
//		List<SignalSegmentEntry> entries = getOperations().findAll(
//				SignalSegmentEntry.class);
//		// then
//		Assert.assertEquals("one deleted one left", 1, entries.size());
//	}
//
//	private SignalSegmentEntry newSignalSegment(String time) {
//		SignalSegment signalSegment = new SignalSegment();
//		signalSegment.setMarker(new Marker());
//		signalSegment.getMarker().setLabel(time);
//		return new SignalSegmentEntry(signalSegment);
//	}

}

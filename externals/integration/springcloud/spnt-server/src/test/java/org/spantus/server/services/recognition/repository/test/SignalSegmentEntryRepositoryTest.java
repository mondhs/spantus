package org.spantus.server.services.recognition.repository.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spantus.core.beans.SignalSegment;
import org.spantus.server.dto.SignalSegmentEntry;
import org.spantus.server.services.recognition.repository.SignalSegmentEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SignalSegmentEntryRepositoryTest {

	@Autowired
	SignalSegmentEntryRepository repository;
	
	@Before
	public void onSetup(){
		for (int i = 0; i < 30; i++) {
			SignalSegmentEntry entry = new SignalSegmentEntry();
			entry.setSignalSegment(new SignalSegment());
			entry.setRecognizable(true);
			repository.save(entry);
		}
	}
	
	@After
	public void onTeardown(){
		repository.deleteAll();
	}

	@Test
	public void findByTarget() {
		Page<SignalSegmentEntry> entries = repository.findByRecognizable(true,
				new PageRequest(1, 3));
		assertThat(entries.getTotalElements(), is(30L));
		assertThat(entries.getNumber(), is(1));
	}

}

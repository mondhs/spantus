package org.spantus.android.service.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.spantus.android.audio.RecordServiceReader;
import org.spantus.android.dto.ExtractorReaderCtx;
import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.android.service.AndroidExtractorsFactory;
import org.spantus.android.service.RecognizeService;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.io.BaseWraperExtractorReader;

public class ExtractRecogniseITest {

//	private static final Logger LOG = Logger
//			.getLogger(ExtractRecogniseITest.class);

	private RecordServiceReader recordService;
	private RecognizeService recognizeService;
	File wavFile = null;

	@Before
	public void onSetup() throws MalformedURLException {
		wavFile = new File("./test/du1.wav");
		recordService = new RecordServiceReader();
		SpantusAudioCtx ctx =new SpantusAudioCtx();
		URL url = new URL("http://spantus.cloudfoundry.com/api/recognition/recognize");
		ctx.setRecognizedUrl(url);
		recognizeService=new RecognizeService(ctx);
	}

	@Test
	public void test_extract() throws Exception {
		// given
		List<RecognitionResultDetails> lastResult = null;
		List<String> labels = new ArrayList<String>();
		
		List<SignalSegment> list = extractSegments();
	
		// when
		for (SignalSegment signalSegment : list) {
//			recordService.getSegmentDao().write(signalSegment, new FileOutputStream(new File("./target/"+signalSegment.getMarker().getLabel()+".json")));
			lastResult = recognizeService.recognize(signalSegment);
			String label = lastResult.get(0).getInfo().getMarker().getLabel();
			labels.add(label);
		}
		
		
		// then
		Assert.assertEquals("Segments", 1, list.size());
		Assert.assertEquals("Labels", 1, labels.size());
		Assert.assertEquals("recognize", "du", labels.get(0));
	}

	private List<SignalSegment> extractSegments() throws MalformedURLException,
			IOException {
		ExtractorReaderCtx readerCtx = AndroidExtractorsFactory
				.createDefaultReader();
		BaseWraperExtractorReader wrappedReader = new BaseWraperExtractorReader(
				readerCtx.getReader(), 1);
		wrappedReader.setSampleSizeInBits(16);
		
		recordService.readFile(wavFile.toURL(), recordService, wrappedReader);
		
		List<SignalSegment> list = recordService.extractSegments(readerCtx);
		
		return list;
	}



}

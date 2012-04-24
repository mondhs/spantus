package org.spantus.android.service.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.spantus.android.audio.RecordServiceReader;
import org.spantus.android.dto.ExtractorReaderCtx;
import org.spantus.android.dto.SpantusAudioCtx;
import org.spantus.android.service.AndroidExtractorConfigUtil;
import org.spantus.android.service.AndroidExtractorsFactory;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.extractor.ExtractorParam;
import org.spantus.core.extractor.IExtractorConfig;
import org.spantus.core.io.AudioReaderFactory;
import org.spantus.extractor.impl.ExtractorEnum;

public class RecordServiceReaderTest {
	
	private static final Logger LOG =Logger.getLogger(RecordServiceReaderTest.class);

	
	RecordServiceReader recordServiceReader;
	
	@Before
	public void onSetup() {
		recordServiceReader = new RecordServiceReader();
	}

	@Test
	public void test_createReader() {
		//given
		SpantusAudioCtx ctx = new SpantusAudioCtx();
		//when
		ExtractorReaderCtx readerCtx = recordServiceReader.createReader(ctx);
		//then
		Assert.assertEquals("Extractor", 1, readerCtx.getReader().getExtractorRegister().size());
		Assert.assertEquals("Vector Extractor", 1, readerCtx.getReader().getExtractorRegister3D().size());
	}
	
	@Test
	public void test_sendFullSignal() throws MalformedURLException, FileNotFoundException {
		//given
		SpantusAudioCtx ctx = new SpantusAudioCtx();
		ctx.setWorkingDir(new File("./target/"));
		ExtractorReaderCtx readerCtx = createReader(ctx);
		URL inputUrl = new File("../../trunk/data/text1.8000.wav").toURI().toURL();
		AudioReaderFactory.createAudioReader().readSignal(inputUrl, readerCtx.getReader());
		
		//when
		URL sentUrl = recordServiceReader.sendFullSignal(readerCtx.getReader(), ctx);
		LOG.error("sent: "+ sentUrl);
		List<SignalSegment> segments = recordServiceReader.extractSegments(readerCtx);
		for (SignalSegment segment : segments) {
			File file = recordServiceReader.getAudioCtxService().recreateFile(ctx,
					segment.getMarker().getLabel() + "spnt.json");
			recordServiceReader.getSegmentDao().write(segment, new FileOutputStream(file));
			LOG.error("Segment : "+ file);
		}
		//then
		Assert.assertEquals("segments", 4, segments.size());
		
	}
	
	private ExtractorReaderCtx createReader(SpantusAudioCtx ctx) {
		IExtractorConfig extractorConfig = AndroidExtractorConfigUtil.defaultConfig(8000D);
		Map<String, ExtractorParam> params = new HashMap<String, ExtractorParam>();
		ExtractorReaderCtx readerCtx = AndroidExtractorsFactory.createReader(extractorConfig, params,
				ExtractorEnum.ENERGY_EXTRACTOR,
				ExtractorEnum.MFCC_EXTRACTOR
				);
		return readerCtx;
	}

}

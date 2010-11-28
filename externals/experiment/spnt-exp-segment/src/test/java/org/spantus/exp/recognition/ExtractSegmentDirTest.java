/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.recognition;

import java.io.File;
import java.io.FilenameFilter;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.externals.recognition.services.CorpusServiceBaseImpl;
import org.spantus.externals.recognition.services.impl.CorpusEntryExtractorFileImpl;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.segment.online.OnlineDecisionSegmentatorParam;

/**
 *
 * @author mondhs
 */
public class ExtractSegmentDirTest {

    public final static String DIR_WAV = "/home/mondhs/src/garsynai/skaiciai/learn";
    public final static String DIR_LEARN = "./target/learn-corpus/";

    private File parent;
    private CorpusEntryExtractorFileImpl extractor;
    private  CorpusServiceBaseImpl corpusService;
    private CorpusRepositoryFileImpl corpusRepository;
    private static final Logger log = Logger.getLogger(ExtractSegmentDirTest.class);

    @Before
    public void onSetup() {
        parent = new File(DIR_WAV);
        extractor = new CorpusEntryExtractorFileImpl();
        corpusService = new CorpusServiceBaseImpl();
        corpusRepository = new CorpusRepositoryFileImpl();
        corpusRepository.setRepositoryPath(DIR_LEARN);
        corpusService.setCorpus(corpusRepository);
        extractor.setCorpusService(corpusService);
        
        OnlineDecisionSegmentatorParam segmentionParam = new OnlineDecisionSegmentatorParam();
        segmentionParam.setMinLength(91L);
        segmentionParam.setMinSpace(261L);
        segmentionParam.setExpandStart(360L);
        segmentionParam.setExpandEnd(360L);
        
        ExtractorEnum[] extractors = new ExtractorEnum[]{
                        ExtractorEnum.MFCC_EXTRACTOR,
                        ExtractorEnum.LPC_EXTRACTOR,
                        ExtractorEnum.FFT_EXTRACTOR,
                        ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR,
                        ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR};
        
        extractor.setSegmentionParam(segmentionParam);
        extractor.setExtractors(extractors);
    }

    @Test
    public void testExtract() {
        int sum = 0; 
        for (File filePath : parent.listFiles(new WavFileNameFilter())) {
           int entries = extractor.extractAndSave(filePath.getAbsoluteFile());
           log.debug("accept: {0}:{1}",filePath, entries);
           sum += entries;
        }
        Assert.assertEquals(30, sum);
    }
    
    public class WavFileNameFilter implements FilenameFilter{

        public boolean accept(File file, String fileName) {
            return fileName.endsWith(".wav");
        }
        
    }
}

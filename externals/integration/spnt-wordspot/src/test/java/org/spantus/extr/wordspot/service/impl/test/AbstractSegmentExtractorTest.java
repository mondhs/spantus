package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;
import junit.framework.Assert;
import org.junit.Before;
import org.spantus.extr.wordspot.domain.SegmentExtractorServiceConfig;
import org.spantus.extr.wordspot.service.impl.SegmentExtractorServiceImpl;

public abstract class AbstractSegmentExtractorTest {

    private SegmentExtractorServiceImpl segmentExtractorService;
    private File repositoryPathRoot = new File("../../../data");
    private File wavFile = new File(repositoryPathRoot, "text1.8000.wav");
    private File repositoryPath = new File(repositoryPathRoot,"corpus/syllable");



    @Before
    public void setUp() throws Exception {
        Assert.assertTrue("repositoryPath exists", getRepositoryPath().exists());
        Assert.assertTrue("repositoryPath is directory", getRepositoryPath().isDirectory());
        Assert.assertTrue("wavFile exists", getWavFile().exists());
        segmentExtractorService = new SegmentExtractorServiceImpl();
        segmentExtractorService.getConfig().setRepositoryPath(getRepositoryPath().getAbsolutePath());
        changeOtherParams(segmentExtractorService.getConfig());
        segmentExtractorService.updateParams();
    }

    /**
     *
     * @param config
     */
    protected void changeOtherParams(SegmentExtractorServiceConfig config) {
        // for other things in child classes
    }

    public File getWavFile() {
        return wavFile;
    }

    public SegmentExtractorServiceImpl getSegmentExtractorService() {
        return segmentExtractorService;
    }

    public void setSegmentExtractorService(
            SegmentExtractorServiceImpl segmentExtractorService) {
        this.segmentExtractorService = segmentExtractorService;
    }

    public File getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(File repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public void setWavFile(File wavFile) {
        this.wavFile = wavFile;
    }
    public File getRepositoryPathRoot() {
        return repositoryPathRoot;
    }

    public void setRepositoryPathRoot(File repositoryPathRoot) {
        this.repositoryPathRoot = repositoryPathRoot;
    }
}

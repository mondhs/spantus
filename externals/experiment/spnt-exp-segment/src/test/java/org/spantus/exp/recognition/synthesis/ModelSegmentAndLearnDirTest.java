package org.spantus.exp.recognition.synthesis;

import java.io.File;

import org.junit.Test;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.ExpConfig;
import org.spantus.exp.recognition.AbstractSegmentDirTest;
import org.spantus.exp.recognition.SpantusNameFilter;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

public class ModelSegmentAndLearnDirTest  extends AbstractSegmentDirTest{
	
	private static final Logger log = Logger
			.getLogger(ModelSegmentAndLearnDirTest.class);
	
	@Override
	public ExpConfig createExpConfig() {
		ExpConfig config = super.createExpConfig();
		config.setDirLearn(config.getTrainDirAsFile().getAbsolutePath());
		return config;
	}
	
    @Test
    public void testExtract() {
        clearCorpus();
        SpantusNameFilter filter = new SpantusNameFilter();
        
        int sum = 0;
        for (File markerFile : getExpConfig().getTrainDirAsFile().listFiles(filter)) {
            log.debug("reading: {0}", markerFile);
            String markersPath = FileUtils.replaceExtention(markerFile, ".wav");
            File wavFile = new File(getExpConfig().getTrainDirAsFile(), markersPath); 
            if(!wavFile.exists()){
            	continue;
            }
            MarkerSetHolder markerSetHolder = WorkServiceFactory.createMarkerDao().read(
            		markerFile);
            
            MarkerSet markerSet = findSegementedMarkers(markerSetHolder);
            int count = markerSet.getMarkers().size();

            getExtractor().extractAndLearn(
            		wavFile.getAbsoluteFile(), markerSet, null);
            
            log.debug("accept: {0}:{1}", wavFile, markerSet);
            sum += count;
        }
//        Assert.assertEquals(70, sum);
//        verifyMatches();


    }
}

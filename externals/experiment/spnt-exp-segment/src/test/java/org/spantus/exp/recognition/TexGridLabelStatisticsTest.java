package org.spantus.exp.recognition;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.junit.Test;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.google.common.io.Files;

public class TexGridLabelStatisticsTest extends AbstractSegmentDirTest {
	private static final Logger log = Logger
			.getLogger(TexGridLabelStatisticsTest.class);
	/**
	 * get extension for marker file
	 * @return
	 */
	public String getExtension(){
		return ".TextGrid";
	}

	@Test
	public void testCalculateStatistics() {
		File wavDir = new File(DIR_LEARN_WAV, "WAV/AK1/");
		File markerDir = new File(DIR_LEARN_WAV, "GRID/AK1/");
		TreeMultimap<String, String> multimap = TreeMultimap.create();
		
		for (File filePath : wavDir.listFiles(new WavFileNameFilter())) {
			String markersPath = FileUtils.stripExtention(filePath);
			File markerFile = new File(markerDir, markersPath+getExtension());
			log.debug("reading: {0}", markerFile);
			MarkerSetHolder markerSetHolder = WorkServiceFactory
					.createMarkerDao().read(markerFile);
			if(markerSetHolder == null){
				log.error("[testCalculateStatistics]File not exists" + markerFile);
				continue;
			}
			MarkerSet markerSet = getSegementedMarkers(markerSetHolder);
			
			int i=0;
			for (Marker marker : markerSet.getMarkers()) {
				String label = marker.getLabel().trim().replaceAll("[\\.\\d-]", ""); 
				multimap.put(label
						, Joiner.on("-").join(label , markersPath,""+(i++)));
			}
		}
		
		log.error("[testCalculateStatistics] label->markers: \n" + multimap.toString());
		
		SortedMap<String, Integer> counted = Maps.newTreeMap();

		counted.putAll(
				Maps.transformValues(multimap.asMap(), new Function<Collection<String>, Integer>() {
			public Integer apply(Collection<String> input) {
				return input.size();
			}
		}));

		
//		log.error("[testCalculateStatistics] label->count: \n" + Joiner.on("\n").join(counted.entrySet()));

		try {
			Files.write(Joiner.on("\n").join(counted.entrySet()), new File("./target/test.csv"), Charsets.ISO_8859_1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		TreeMultimap<Integer, String> sortedMultimap = TreeMultimap.create(Ordering.natural().reverse(),Ordering.natural());
		for (Entry<String, Integer> iEntry : counted.entrySet()) {
//			sortedMultimap.put(iEntry.getValue(),iEntry.getKey() );
			for (String marker : multimap.get(iEntry.getKey())) {
				sortedMultimap.put(iEntry.getValue(),marker );
			}
			
		}

		log.error("[testCalculateStatistics]" +
				Joiner.on("\n").join(sortedMultimap.asMap().entrySet())
				);

	}
}

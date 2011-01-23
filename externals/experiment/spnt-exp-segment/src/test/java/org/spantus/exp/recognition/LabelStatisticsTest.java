package org.spantus.exp.recognition;

import java.io.File;
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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class LabelStatisticsTest extends AbstractSegmentDirTest {
	private static final Logger log = Logger
			.getLogger(LabelStatisticsTest.class);

	@Test
	public void testCalculateStatistics() {
		File wavDir = new File(DIR_LEARN_WAV, "WAV/AK1/");
		File markerDir = new File(DIR_LEARN_WAV, "GRID/AK1/");
		TreeMultimap<String, String> multimap = TreeMultimap.create();
		
		for (File filePath : wavDir.listFiles(new WavFileNameFilter())) {
			String markersPath = FileUtils.stripExtention(filePath);
			File markerFile = new File(markerDir, markersPath+".TextGrid");
			log.debug("reading: {0}", markerFile);
			MarkerSetHolder markerSetHolder = WorkServiceFactory
					.createMarkerDao().read(markerFile);
			MarkerSet markerSet = getSegementedMarkers(markerSetHolder);
			
			int i=0;
			for (Marker marker : markerSet.getMarkers()) {
				multimap.put(marker.getLabel().trim(), Joiner.on("-").join(marker.getLabel().trim() , markersPath,""+(i++)));
			}
		}
		
		log.error(multimap.toString());
		
		SortedMap<String, Integer> counted = Maps.newTreeMap();
		counted.putAll(
				Maps.transformValues(multimap.asMap(), new Function<Collection<String>, Integer>() {
			public Integer apply(Collection<String> input) {
				return input.size();
			}
		}));
		TreeMultimap<Integer, String> sortedMultimap = TreeMultimap.create(Ordering.natural().reverse(),Ordering.natural());
		for (Entry<String, Integer> iEntry : counted.entrySet()) {
//			sortedMultimap.put(iEntry.getValue(),iEntry.getKey() );
			for (String marker : multimap.get(iEntry.getKey())) {
				sortedMultimap.put(iEntry.getValue(),marker );
			}
			
		}

		log.error(
				Joiner.on("\n").join(sortedMultimap.asMap().entrySet())
				);

	}
}

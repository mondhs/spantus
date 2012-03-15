package org.spantus.exp.recognition.statistics;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.junit.Test;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.exp.recognition.AbstractSegmentDirTest;
import org.spantus.exp.recognition.filefilter.WavFileNameFilter;
import org.spantus.logger.Logger;
import org.spantus.utils.FileUtils;
import org.spantus.work.services.WorkServiceFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
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
	
	public String tranformLabel(String label){
		return label.trim().replaceAll("[\\.\\d-\\^\\:]", "");
	}

	@Test
	public void testCalculateStatistics() {
		File wavDir = new File(getExpConfig().getDirLearn(), "WAV/AK1/");
		File markerDir = new File(getExpConfig().getDirLearn(), "GRID/AK1/");
		TreeMultimap<String, String> multimap = TreeMultimap.create();
		HashMultimap<String, Marker> markerMultimap = HashMultimap.create();
		
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
			MarkerSet markerSet = findSegementedMarkers(markerSetHolder);
			
			int i=0;
			for (Marker marker : markerSet.getMarkers()) {
				processMarker(markersPath, marker, markerMultimap, multimap, i++);
			}
		}
		
		log.error("[testCalculateStatistics] label->markers: \n" + multimap.toString());
		
		SortedMap<String, LabelStatistics> counted = Maps.newTreeMap();

		counted.putAll(
				Maps.transformValues(markerMultimap.asMap(), new Function<Collection<Marker>, LabelStatistics>() {
			public LabelStatistics apply(Collection<Marker> input) {
				LabelStatistics labelStatistics = new LabelStatistics();
				labelStatistics.setLabel(
						tranformLabel(input.iterator().next().getLabel()));
				Long sum = 0L;
				for (Marker marker : input) {
					sum += marker.getLength();
				}
				labelStatistics.setLength(sum/input.size());
				labelStatistics.setCount(input.size());
				return labelStatistics;
			}
		}));

		
//		log.error("[testCalculateStatistics] label->count: \n" + Joiner.on("\n").join(counted.entrySet()));

		try {
			Files.write(LabelStatistics.getHeader()+"\n"+Joiner.on("\n").join(counted.values()), 
			new File("./target/test"+getClass().getSimpleName()+".csv"), Charsets.ISO_8859_1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		TreeMultimap<Integer, String> sortedMultimap = TreeMultimap.create(Ordering.natural().reverse(),Ordering.natural());
		for (Entry<String, LabelStatistics> iEntry : counted.entrySet()) {
//			sortedMultimap.put(iEntry.getValue(),iEntry.getKey() );
			for (String marker : multimap.get(iEntry.getKey())) {
				sortedMultimap.put(iEntry.getValue().getCount(),marker );
			}
			
		}

		log.error("[testCalculateStatistics]" +
				Joiner.on("\n").join(sortedMultimap.asMap().entrySet())
				);

	}

	private void processMarker(String markersPath, Marker marker, 
			HashMultimap<String, Marker>  markerMultimap, 
			TreeMultimap<String, String> multimap, int i) {
		String label = marker.getLabel().trim().replaceAll("[\\.\\d-\\^\\:]", ""); 
		markerMultimap.put(label, marker);
		multimap.put(label
				, Joiner.on("-").join(label , markersPath,""+i));
		
	}
}

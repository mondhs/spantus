package org.spantus.exp.recognition.statistics;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.exp.recognition.AbstractSegmentDirTest;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.logger.Logger;
import org.spantus.math.VectorUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultimap;
import com.google.common.io.Files;

public class CorpusSegmentStatisticsTest extends AbstractSegmentDirTest {
	private static final Logger log = Logger
			.getLogger(CorpusSegmentStatisticsTest.class);

	@Test
	public void testCalculateStatistics() {
		File wavDir = new File(getExpConfig().getDirLearn(), "WAV/AK1/");
		File markerDir = new File(getExpConfig().getDirLearn(), "GRID/AK1/");
		TreeMultimap<String, String> multimap = TreeMultimap.create();
		HashMultimap<String, Marker> markerMultimap = HashMultimap.create();
		// Map<String, List<CorpusEntryStat>> map = Maps.newLinkedHashMap();
		ListMultimap<String, CorpusEntryStat> map = ArrayListMultimap.create();

		for (SignalSegment entry : getCorpusRepository().findAllEntries()) {

			for (Entry<String, IValues> featureData : entry.findAllFeatures().entrySet()) {
				Double avg = avg(featureData.getValue());
				Double std = std(featureData.getValue(), avg);
				CorpusEntryStat stat = new CorpusEntryStat(
						fix(entry.getName()), avg, std);
				map.put(featureData.getKey(), stat);
			}

		}

		// writeToFile(map,ExtractorEnum.SPECTRAL_FLUX_EXTRACTOR.name());
		// writeToFile(map,ExtractorEnum.SIGNAL_ENTROPY_EXTRACTOR.name());
		// writeToFile(map,ExtractorEnum.LOUDNESS_EXTRACTOR.name());

		writeToFile(map, ExtractorEnum.PLP_EXTRACTOR.name());
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private String fix(String name) {
		String label = name.trim().replaceAll("[-123\\.]", "");
		return label;
	}

	/**
	 * 
	 * @param map
	 * @param featureName
	 */
	private void writeToFile(ListMultimap<String, CorpusEntryStat> map,
			String featureName) {
		try {
			Files.write(LabelStatistics.getHeader() + "\n"
					+ Joiner.on("\n").join(map.get(featureName)), new File(
					"./target/test" + getClass().getSimpleName() + "_"
							+ featureName + ".csv"), Charsets.ISO_8859_1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	private Double std(IValues values, Double avg) {
		if (values.getDimention() == 1) {
			FrameValues fv = (FrameValues) values;
			return VectorUtils.std(fv, avg);

		}
		List<Double> stds = Lists.newArrayList();
		FrameVectorValues fvv = (FrameVectorValues) values;
		for (List<Double> fv : fvv) {
			Double previousAbs = null;
			Double flux = 0D;
			for (Double current : fv) {
				if(previousAbs == null){
					previousAbs = Double.valueOf(Math.abs(current));
					continue;
				}
				//x=(|X[k]|-|X[k-1]|)
				Double x = Math.abs(current) - previousAbs;
				//H(x)=(x+|x|)/2
				flux += (x + Math.abs(x))/2;
				previousAbs = Math.abs(current);
			}
			//Normalization
			flux/=fv.size();
			stds.add(flux);
		}

		return VectorUtils.std(stds,avg);
	}

	/**
	 * 
	 * @param values
	 * @return
	 */
	private Double avg(IValues values) {
		if (values.getDimention() == 1) {
			FrameValues fv = (FrameValues) values;
			return VectorUtils.avg(fv);
		}
		List<Double> avgs = Lists.newArrayList();
		FrameVectorValues fvv = (FrameVectorValues) values;
		for (List<Double> fv : fvv) {
			Double previousAbs = null;
			Double flux = 0D;
			for (Double current : fv) {
				if(previousAbs == null){
					previousAbs = Double.valueOf(Math.abs(current));
					continue;
				}
				//x=(|X[k]|-|X[k-1]|)
				Double x = Math.abs(current) - previousAbs;
				//H(x)=(x+|x|)/2
				flux += (x + Math.abs(x))/2;
				previousAbs = Math.abs(current);
			}
			//Normalization
			flux/=fv.size();
			avgs.add(flux);
		}

		return VectorUtils.avg(avgs);
	}

	public class CorpusEntryStat {
		public String name;
		public Double avg;
		public Double std;

		public CorpusEntryStat(String name, Double avg, Double std) {
			super();
			this.name = name;
			this.avg = avg;
			this.std = std;
		}

		@Override
		public String toString() {
			return MessageFormat.format(
					"{0};{1,number,#.###};{2,number,#.###}", name, avg, std);
		}
	}

}

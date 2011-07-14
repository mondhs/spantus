package org.spantus.exp.recognition.statistics;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.junit.Test;
import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;
import org.spantus.exp.recognition.AbstractSegmentDirTest;
import org.spantus.externals.recognition.bean.CorpusEntry;
import org.spantus.externals.recognition.bean.FeatureData;
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
		File wavDir = new File(DIR_LEARN_WAV, "WAV/AK1/");
		File markerDir = new File(DIR_LEARN_WAV, "GRID/AK1/");
		TreeMultimap<String, String> multimap = TreeMultimap.create();
		HashMultimap<String, Marker> markerMultimap = HashMultimap.create();
		// Map<String, List<CorpusEntryStat>> map = Maps.newLinkedHashMap();
		ListMultimap<String, CorpusEntryStat> map = ArrayListMultimap.create();

		for (CorpusEntry entry : getCorpusRepository().findAllEntries()) {

			for (FeatureData featureData : entry.getFeatureMap().values()) {
				Float avg = avg(featureData.getValues());
				Float std = std(featureData.getValues(), avg);
				CorpusEntryStat stat = new CorpusEntryStat(
						fix(entry.getName()), avg, std);
				map.put(featureData.getName(), stat);
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
	private Float std(IValues values, Float avg) {
		if (values.getDimention() == 1) {
			FrameValues fv = (FrameValues) values;
			return VectorUtils.std(fv, avg);

		}
		List<Float> stds = Lists.newArrayList();
		FrameVectorValues fvv = (FrameVectorValues) values;
		for (List<Float> fv : fvv) {
			Float previousAbs = null;
			float flux = 0;
			for (Float current : fv) {
				if(previousAbs == null){
					previousAbs = Float.valueOf(Math.abs(current));
					continue;
				}
				//x=(|X[k]|-|X[k-1]|)
				float x = Math.abs(current) - previousAbs;
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
	private Float avg(IValues values) {
		if (values.getDimention() == 1) {
			FrameValues fv = (FrameValues) values;
			return VectorUtils.avg(fv);
		}
		List<Float> avgs = Lists.newArrayList();
		FrameVectorValues fvv = (FrameVectorValues) values;
		for (List<Float> fv : fvv) {
			Float previousAbs = null;
			float flux = 0;
			for (Float current : fv) {
				if(previousAbs == null){
					previousAbs = Float.valueOf(Math.abs(current));
					continue;
				}
				//x=(|X[k]|-|X[k-1]|)
				float x = Math.abs(current) - previousAbs;
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
		public Float avg;
		public Float std;

		public CorpusEntryStat(String name, Float avg, Float std) {
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

// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 

package org.spantus.exp.recognition.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.math.NumberUtils;
import org.spantus.utils.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class CorpusSegmentStatsDataset extends AbstractXYDataset implements XYDataset,
		DomainInfo, RangeInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double domainMin;
	private Double domainMax;
	private Double rangeMin;
	private Double rangeMax;
	private Range domainRange;
	private Range range;
	private ArrayListMultimap<String, Entry> map = ArrayListMultimap.create();
	private BiMap<Integer, String> keys = HashBiMap.create();


	public CorpusSegmentStatsDataset() {
		this.domainMin = Double.MAX_VALUE;
		this.domainMax = -Double.MAX_VALUE;
		this.rangeMin = Double.MAX_VALUE;
		this.rangeMax =-Double.MAX_VALUE;
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(
					new File(
							"./target/testCorpusSegmentStatisticsTest_"+ ExtractorEnum.PLP_EXTRACTOR.name()+".csv"));

			Pattern pattern = Pattern.compile("(.*);([\\d\\\\.]*);(.*)");

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Matcher match = pattern.matcher(line);
				if (match.find()) {
					String name = match.group(1);
					Double avg = new Double(match.group(2));
					Double std = new Double(match.group(3));
					Entry entry = new Entry(name, avg, std);
					if(keys.inverse().get(name) == null){
						keys.put(keys.size(),name);
					}
					map.put(name, entry);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		for (Iterator<java.util.Map.Entry<Integer, String>> iterator = keys.entrySet().iterator(); iterator.hasNext();) {
			java.util.Map.Entry<Integer, String> key = (java.util.Map.Entry<Integer, String>) iterator.next();
			if(map.get(key.getValue()).size()<10 || !StringUtils.hasText(key.getValue())){
				iterator.remove();
				map.removeAll(key.getValue());
				System.out.println("Remove: " + key.getValue());
			}else{
				for (Entry entry : map.get(key.getValue())) {
					domainMin = NumberUtils.min(domainMin, entry.avg);
					domainMax =NumberUtils.max(domainMax, entry.avg);
					rangeMin = NumberUtils.min(rangeMin, entry.std);
					rangeMax =NumberUtils.max(rangeMax, entry.std);
				}
			}
		}
		int i = 0;
		for (String keyName : map.keySet()) {
			keys.forcePut(i++, keyName);
		}



		this.domainRange = new Range(domainMin, domainMax);
		this.range = new Range(rangeMin, rangeMax);
	}

	public Number getX(int i, int j) {
		return map.get(keys.get(i)).get(j).avg;
	}

	public Number getY(int i, int j) {
		return map.get(keys.get(i)).get(j).std;
	}

	public int getSeriesCount() {
		return keys.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable getSeriesKey(int i) {
		String key = keys.get(i) ;
		if(key != null){
			return key;
		}
		return "<NOT_FOUND>";
	}

	public int getItemCount(int i) {
		return map.get(keys.get(i)).size();
	}

	public double getDomainLowerBound() {
		return domainMin.doubleValue();
	}

	public double getDomainLowerBound(boolean flag) {
		return domainMin.doubleValue();
	}

	public double getDomainUpperBound() {
		return domainMax.doubleValue();
	}

	public double getDomainUpperBound(boolean flag) {
		return domainMax.doubleValue();
	}

	public Range getDomainBounds() {
		return domainRange;
	}

	public Range getDomainBounds(boolean flag) {
		return domainRange;
	}

	public Range getDomainRange() {
		return domainRange;
	}

	public double getRangeLowerBound() {
		return rangeMin.doubleValue();
	}

	public double getRangeLowerBound(boolean flag) {
		return rangeMin.doubleValue();
	}

	public double getRangeUpperBound() {
		return rangeMax.doubleValue();
	}

	public double getRangeUpperBound(boolean flag) {
		return rangeMax.doubleValue();
	}

	public Range getRangeBounds(boolean flag) {
		return range;
	}

	public Range getValueRange() {
		return range;
	}

	public Number getMinimumDomainValue() {
		return domainMin;
	}

	public Number getMaximumDomainValue() {
		return domainMax;
	}

	public Number getMinimumRangeValue() {
		return domainMin;
	}

	public Number getMaximumRangeValue() {
		return domainMax;
	}
	
	public class Entry{
		public String name;
		public Double avg;
		public Double std;
		public Entry(String name, Double avg, Double std) {
			super();
			this.name = name;
			this.avg = avg;
			this.std = std;
		}
		@Override
		public String toString() {
			return MessageFormat.format("{0};{1,number,#.###};{2,number,#.###}", name,avg, std);
		}
	}
}

package org.spantus.core.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.preemphasis.Preemphasis;
import org.spantus.core.extractor.preemphasis.PreemphasisFactory;
import org.spantus.math.windowing.HammingWindowing;

public class BaseWraperExtractorReader {
	int sampleSizeInBits;
	List<List<Byte>> shortBuffers;
	private IExtractorInputReader reader;
	private Preemphasis preemphasisFilter;
	private Long sample;
	private Double lastValue;
	private boolean smooth = false;
	private Integer smoothingSize = null;
	private HammingWindowing hammingWindowing;
	private boolean signed;
	private boolean bigEndian;
	
	public BaseWraperExtractorReader(IExtractorInputReader reader, int size) {
		this.reader = reader;
		shortBuffers = new ArrayList<List<Byte>>(size);
		for (int i = 0; i < size; i++) {
			List<Byte> shortBuffer = new ArrayList<Byte>(3);
			shortBuffers.add(shortBuffer);
		}
		preemphasisFilter = PreemphasisFactory.createPreemphasis(reader
				.getConfig().getPreemphasis());
		sample = 0L;
	}
	
	public void put(byte value) {
		switch (sampleSizeInBits) {
		case 8:
			put(AudioUtil.read8(value));
			break;
		case 16:
			List<Byte> shortBuffer = shortBuffers.get(0);
			shortBuffer.add(value);
			if (shortBuffer.size() == 2) {
				Double d = AudioUtil.read16(shortBuffer.get(0),
						shortBuffer.get(1), isSigned(), isBigEndian());
				put(d);
				shortBuffer.clear();
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(sampleSizeInBits
					+ " bits/sample not supported");
		}
	}
	
	/**
	 * put byte list
	 * 
	 * @param value
	 */
	public void put(List<Byte> value) {
		Double sum = null;
		switch (getSampleSizeInBits()) {
		case 8:
			sum = 0D;
			for (Byte byte1 : value) {
				sum += AudioUtil.read8(byte1);
			}
			break;
		case 16:
			Iterator<Byte> valIterator = value.iterator();
			Iterator<List<Byte>> buffIterator = shortBuffers.iterator();
			while (valIterator.hasNext()) {
				Byte ival = valIterator.next();
				buffIterator.next().add(ival);
			}
			if (shortBuffers.get(0).size() == 2) {
				for (List<Byte> shortBuffer : shortBuffers) {
					if (shortBuffer.size() == 2) {
						sum = sum == null ? 0D : sum;
						sum += AudioUtil.read16(shortBuffer.get(0),
								shortBuffer.get(1), isSigned(), isBigEndian());
					}
					shortBuffer.clear();
				}
			}
			break;
		default:
			throw new java.lang.IllegalArgumentException(
					sampleSizeInBits + " bits/sample not supported");
		}
		// add value tooo the lis
		put(sum);
	}

	public void put(Double val) {
		if (val != null) {
			if (smooth == true && smoothingSize != null) {
				val *= getHammingWindowing().calculate(smoothingSize,
						sample.intValue());
			}
			reader.put(sample++, preemphasis(val));
		}
	}
	
	/**
	 * Calculate post process data
	 * 
	 * @param currentValue
	 * @return
	 */
	protected Double preemphasis(Double currentValue) {
		Double processedValue = preemphasisFilter.process(currentValue);
		setLastValue(processedValue);
		return processedValue;
	}
	
	public void pushValues() {
		reader.pushValues(sample);
	}
	
	public boolean isSmooth() {
		return smooth;
	}

	public void setSmooth(boolean smooth) {
		this.smooth = smooth;
	}

	public Integer getSmoothingSize() {
		return smoothingSize;
	}

	public void setSmoothingSize(Integer smoothingSize) {
		this.smoothingSize = smoothingSize;
	}

	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public void setSampleSizeInBits(int sampleSizeInBits) {
		this.sampleSizeInBits = sampleSizeInBits;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public boolean isBigEndian() {
		return bigEndian;
	}

	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}
	public HammingWindowing getHammingWindowing() {
		if (hammingWindowing == null) {
			hammingWindowing = new HammingWindowing();
		}
		return hammingWindowing;
	}
	protected void setLastValue(Double lastValue) {
		this.lastValue = lastValue;
	}

	public Double getLastValue() {
		return lastValue;
	}
	public IExtractorInputReader getReader() {
		return reader;
	}

	public Long getSample() {
		return sample;
	}
}

package org.spantus.work.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.spantus.core.IValues;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.dao.SignalSegmentDao;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.dao.MarkerDao;
import org.spantus.core.extractor.dao.ReaderDao;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.service.ExtractorInputReaderService;
import org.spantus.work.SpantusBundle;
import org.spantus.work.services.BundleDao;
import org.spantus.work.services.WorkServiceFactory;

public class BundleZipDaoImpl implements BundleDao {

	private MarkerDao markerDao;
	private ReaderDao readerDao;
	private SignalSegmentDao signalSegmentDao;
	private ExtractorInputReaderService extractorInputReaderService;

	public static final String BUNDLE_FILE_SAMPLE = "sample.sspnt.xml";
	public static final String BUNDLE_FILE_MARKER = "markers.mspnt.xml";
	public static final String SIGNAL_SEGMENT_MARKER = ".segment.json";

	public SpantusBundle read(File zipFile) {
		SpantusBundle bundle = new SpantusBundle();
		ZipFile input;
		IExtractorInputReader reader;
		MarkerSetHolder holder;
		try {
			input = new ZipFile(zipFile);
			InputStream inputStream = input.getInputStream(input
					.getEntry(BUNDLE_FILE_SAMPLE));
			reader = WorkServiceFactory.createReaderDao().read(inputStream);
			inputStream = input.getInputStream(input
					.getEntry(BUNDLE_FILE_MARKER));
			holder = WorkServiceFactory.createMarkerDao().read(inputStream);
		} catch (ZipException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		bundle.setHolder(holder);
		bundle.setReader(reader);
		return bundle;
	}

	public void write(SpantusBundle bundle, File zipFile) {
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipFile));
			
			int id = 0;
			for (Entry<String, MarkerSet> markerSetEntry : bundle.getHolder()) {
				for (Marker markerEntry : markerSetEntry.getValue()) {
					String idStr = markerSetEntry.getKey()+"-"+id++;
					out.putNextEntry(new ZipEntry(idStr+"-"+SIGNAL_SEGMENT_MARKER));
					SignalSegment segment = new SignalSegment();
					segment.setId(idStr);
					segment.setMarker(markerEntry);
					Map<String, IValues> features = extractorInputReaderService.findAllVectorValuesForMarker(bundle.getReader(),markerEntry);
					segment.putAll(features);
					signalSegmentDao.write(segment, out);
				}
			}
			out.putNextEntry(new ZipEntry(BUNDLE_FILE_SAMPLE));
			readerDao.write(bundle.getReader(), out);
			out.putNextEntry(new ZipEntry(BUNDLE_FILE_MARKER));
			markerDao.write(bundle.getHolder(), out);
			
			out.close();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setMarkerDao(MarkerDao markerDao) {
		this.markerDao = markerDao;
	}

	public void setReaderDao(ReaderDao readerDao) {
		this.readerDao = readerDao;
	}

	public void setSignalSegmentDao(SignalSegmentDao signalSegmentDao) {
		this.signalSegmentDao = signalSegmentDao;
	}

	public void setExtractorInputReaderService(
			ExtractorInputReaderService extractorInputReaderService) {
		this.extractorInputReaderService = extractorInputReaderService;
	}

}

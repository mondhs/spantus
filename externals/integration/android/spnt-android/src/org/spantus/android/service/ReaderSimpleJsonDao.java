package org.spantus.android.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spantus.core.extractor.IExtractor;
import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.extractor.IExtractorVector;
import org.spantus.core.extractor.dao.ReaderDao;
import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.segment.ISegmentator;
import org.spantus.logger.Logger;

public class ReaderSimpleJsonDao implements ReaderDao {

	public static final String EXTRACTORS = "extractors";
	public static final String DIMENTION = "dimention";
	public static final String VALUES = "values";
	public static final String TIME = "time";
	public static final String SAMPLE_RATE = "sampleRate";
	public static final String NAME = "name";
	public static final Logger LOG = Logger
			.getLogger(ReaderSimpleJsonDao.class);

	@Override
	public void write(IExtractorInputReader reader, File file) {
		try {
			FileOutputStream outputFile = new FileOutputStream(file, false);
			saveDataToFile(reader, outputFile);
		} catch (Exception e) {
			LOG.error(e);
		}

	}

	@Override
	public void write(IExtractorInputReader reader, OutputStream outputStream) {
		try {
			saveDataToFile(reader, outputStream);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Override
	public IExtractorInputReader read(File file) {
		throw new IllegalArgumentException("Not impl");
	}

	@Override
	public IExtractorInputReader read(InputStream inputStream) {
		throw new IllegalArgumentException("Not impl");
	}

	public JSONObject transfor(IExtractorInputReader reader) throws Exception {
		JSONObject root = new JSONObject();
		JSONArray extractors = new JSONArray();
		for (IExtractor iExtractor : reader.getExtractorRegister()) {
			extractors.put(createExtractor(iExtractor));
		}
		for (IExtractorVector iExtractor : reader.getExtractorRegister3D()) {
			extractors.put(createExtractor(iExtractor));
		}
		root.put(EXTRACTORS, extractors);
		return root;
	}

	/**
	 * 
	 * @param iExtractor
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createExtractor(IExtractorVector iExtractor)
			throws JSONException {
		JSONObject rExtractor = new JSONObject();
		rExtractor.put(NAME, iExtractor.getName());
		rExtractor
				.put(SAMPLE_RATE, iExtractor.getOutputValues().getSampleRate());
		rExtractor.put(TIME, iExtractor.getOutputValues().getTime());
		rExtractor.put(DIMENTION, iExtractor.getOutputValues().getDimention());
		JSONArray vectors = new JSONArray();
		for (List<Double> iVector : iExtractor.getOutputValues()) {
			JSONArray vector = new JSONArray();
			for (Double value : iVector) {
				vector.put(value);
			}
			vectors.put(vector);
		}
		rExtractor.put(VALUES, vectors);
		return null;
	}

	/**
	 * 
	 * @param iExtractor
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createExtractor(IExtractor iExtractor)
			throws JSONException {
		JSONObject rExtractor = new JSONObject();
		rExtractor.put(NAME, iExtractor.getName());
		rExtractor
				.put(SAMPLE_RATE, iExtractor.getOutputValues().getSampleRate());
		rExtractor.put(TIME, iExtractor.getOutputValues().getTime());
		rExtractor.put(DIMENTION, iExtractor.getOutputValues().getDimention());
		//marker set
		if (iExtractor instanceof ISegmentator) {
			ISegmentator segmentator = (ISegmentator) iExtractor;
			MarkerSet markerSet = segmentator.getMarkSet();
			rExtractor.put("markerSet", createMarkerSet(markerSet));
		}
		
		JSONArray values = new JSONArray();
		for (Double value : iExtractor.getOutputValues()) {
			values.put(value);
		}
		rExtractor.put(VALUES, values);
		return rExtractor;
	}
	/**
	 * 
	 * @param markerSet
	 * @return
	 * @throws JSONException
	 */
	private JSONObject createMarkerSet(MarkerSet markerSet)
			throws JSONException {
		JSONObject rMarkerSet = new JSONObject();
		rMarkerSet.put("markerSetType", markerSet.getMarkerSetType());
		JSONArray markers = new JSONArray();
		for (Marker marker : markerSet.getMarkers()) {
			markers.put(createMarker(marker));
		}
		rMarkerSet.put("markers", markers);
		return rMarkerSet;
	}

	private JSONObject createMarker(Marker marker)throws JSONException {
		JSONObject rMarker = new JSONObject();
		rMarker.put("label", marker.getLabel());
		rMarker.put("start", marker.getStart());
		rMarker.put("length", marker.getLength());
		return rMarker;
	}

	private void saveDataToFile(IExtractorInputReader reader,
			OutputStream outputStream) throws Exception {
		JSONObject root = transfor(reader);
		outputStream.write(root.toString(3).getBytes(Charset.forName("UTF-8")));
		outputStream.flush();
		outputStream.close();

	}

}

package org.spantus.work.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.spantus.core.beans.FrameValuesHolder;
import org.spantus.core.beans.FrameVectorValuesHolder;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.dao.SignalSegmentDao;
import org.spantus.core.marker.Marker;
import org.spantus.logger.Logger;

public class SignalSegmentSimpleJsonDao implements SignalSegmentDao {

	public static final Logger LOG = Logger
			.getLogger(SignalSegmentSimpleJsonDao.class);

	/* (non-Javadoc)
	 * @see org.spantus.android.service.SignalSegmentDao#write(org.spantus.core.beans.SignalSegment, java.io.File)
	 */
	@Override
	public void write(SignalSegment segment, File file) {
		try {
			FileOutputStream outputFile = new FileOutputStream(file, false);
			saveDataToFile(segment, outputFile);
			outputFile.flush();
			outputFile.close();
		} catch (Exception e) {
			LOG.error(e);
		}

	}

	/* (non-Javadoc)
	 * @see org.spantus.android.service.SignalSegmentDao#write(org.spantus.core.beans.SignalSegment, java.io.OutputStream)
	 */
	@Override
	public void write(SignalSegment segment, OutputStream outputStream) {
		try {
			saveDataToFile(segment, outputStream);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	private void saveDataToFile(SignalSegment segment, OutputStream outputStream)
			throws Exception {
		JSONObject root = transfor(segment);
		outputStream.write(root.toString().getBytes(Charset.forName("UTF-8")));
	}

	private JSONObject transfor(SignalSegment segment) throws Exception {
		JSONObject root = new JSONObject();
		root.put("marker", tranform(segment.getMarker()));
		JSONObject map = new JSONObject();
		for (Entry<String, FrameValuesHolder> entry : segment
				.getFeatureFrameValuesMap().entrySet()) {
			JSONArray values = newJSONArray(entry.getValue());
			JSONObject frameValues = new JSONObject();
			frameValues.put("values", values);
			frameValues.put("sampleRate", entry.getValue().getSampleRate());
			map.put(entry.getKey(), frameValues);
		}
		root.put("featureFrameValuesMap", map);
		map = new JSONObject();
		for (Entry<String, FrameVectorValuesHolder> entry : segment
				.getFeatureFrameVectorValuesMap().entrySet()) {
			JSONArray values = newJSONArray(entry.getValue());
			JSONObject frameVectorValues = new JSONObject();
			frameVectorValues.put("values", values);
			frameVectorValues.put("sampleRate", entry.getValue().getSampleRate());
			map.put(entry.getKey(), frameVectorValues);
		}
		root.put("featureFrameVectorValuesMap", map);
		return root;
	}

	private Object tranform(Marker marker) throws JSONException {
		JSONObject root = new JSONObject();
		root.put("label", marker.getLabel());
		root.put("start", marker.getStart());
		root.put("length", marker.getLength());
		return root;
	}

	private JSONArray newJSONArray(FrameValuesHolder values) {
		JSONArray jsonValues = new JSONArray();
		for (Double value : values.getValues()) {
			jsonValues.add(value);
		}
		return jsonValues;
	}

	private JSONArray newJSONArray(FrameVectorValuesHolder values) {
		JSONArray jsonValues = new JSONArray();
		for (List<Double> iVector :  values.getValues()) {
			JSONArray vector = new JSONArray();
			for (Double value : iVector) {
				vector.add(value);
			}
			jsonValues.add(vector);
		}
		return jsonValues;
	}
}

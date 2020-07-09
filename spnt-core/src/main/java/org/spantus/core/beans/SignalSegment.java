package org.spantus.core.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

// import javax.xml.bind.annotation.XmlRootElement;

import org.spantus.core.FrameValues;
import org.spantus.core.FrameVectorValues;
import org.spantus.core.IValues;
import org.spantus.core.marker.Marker;
import org.spantus.exception.ProcessingException;

// @XmlRootElement(name = "SignalSegment")
public class SignalSegment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8552336631645110494L;
	private Marker marker;
	private Map<String, FrameValuesHolder> featureFrameValuesMap = new HashMap<String, FrameValuesHolder>();
	private Map<String, FrameVectorValuesHolder> featureFrameVectorValuesMap = new HashMap<String, FrameVectorValuesHolder>();
	private String name;
	private String id;

        public SignalSegment() {
        }

        public SignalSegment(Marker marker) {
            this.marker = marker;
        }
        
        
        
        

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public Map<String, FrameValuesHolder> getFeatureFrameValuesMap() {
		return featureFrameValuesMap;
	}

	public void setFeatureFrameValuesMap(
			Map<String, FrameValuesHolder> featureFrameValuesMap) {
		this.featureFrameValuesMap = featureFrameValuesMap;
	}

	public Map<String, FrameVectorValuesHolder> getFeatureFrameVectorValuesMap() {
		return featureFrameVectorValuesMap;
	}

	public void setFeatureFrameVectorValuesMap(
			Map<String, FrameVectorValuesHolder> featureFrameVectorValuesMap) {
		this.featureFrameVectorValuesMap = featureFrameVectorValuesMap;
	}

	public void putAll(SignalSegment segment) {
		this.getFeatureFrameValuesMap().putAll(
				segment.getFeatureFrameValuesMap());
		this.getFeatureFrameVectorValuesMap().putAll(
				segment.getFeatureFrameVectorValuesMap());
	}

	public void putAll(Map<String, IValues> features) {
		for (Map.Entry<String, IValues> entry1 : features.entrySet()) {
			if (entry1.getValue() instanceof FrameVectorValues) {
				FrameVectorValuesHolder fd = new FrameVectorValuesHolder();
				fd.setValues((FrameVectorValues) entry1.getValue());
				this.getFeatureFrameVectorValuesMap().put(entry1.getKey(), fd);
			} else if (entry1.getValue() instanceof FrameValues) {
				FrameValuesHolder fd = new FrameValuesHolder();
				fd.setValues((FrameValues) entry1.getValue());
				this.getFeatureFrameValuesMap().put(entry1.getKey(), fd);
			} else {
				throw new ProcessingException("Not impl");
			}

		}
	}

	public Map<String, IValues> findAllFeatures() {
		Map<String, IValues> features = new HashMap<String, IValues>();
		if (featureFrameVectorValuesMap != null) {
			for (Entry<String, FrameVectorValuesHolder> holder : featureFrameVectorValuesMap
					.entrySet()) {
				features.put(holder.getKey(), holder.getValue().getValues());
			}
		}
		if (featureFrameValuesMap != null) {
			for (Entry<String, FrameValuesHolder> holder : featureFrameValuesMap
					.entrySet()) {
				features.put(holder.getKey(), holder.getValue().getValues());
			}
		}
		return features;
	}

	public IValueHolder<?> findValueHolder(String key) {
		IValueHolder<?> holder = getFeatureFrameValuesMap().get(key);
		if (holder == null) {
			holder = getFeatureFrameVectorValuesMap().get(key);
		}
		return holder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "SignalSegment [marker=" + marker + ", id=" + id + "]";
	}

}

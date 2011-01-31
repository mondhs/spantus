package org.spantus.externals.recognition.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spantus.core.marker.Marker;
import org.spantus.core.marker.MarkerSet;
import org.spantus.core.marker.MarkerSetHolder;
import org.spantus.core.marker.MarkerSetHolder.MarkerSetHolderEnum;
import org.spantus.logger.Logger;
import org.spantus.utils.Assert;
import org.spantus.utils.FileUtils;
import org.spantus.utils.StringUtils;
import org.spantus.work.services.MarkerDao;
import org.spantus.work.services.WorkServiceFactory;

public class CorpusEntryExtractorTextGridMapImpl extends
		CorpusEntryExtractorFileImpl {
	
	private File markerDir;
	private MarkerDao markerDao;
	private Logger log = Logger.getLogger(CorpusEntryExtractorTextGridMapImpl.class);
	

	@Override
	public String createLabel(File filePath, Marker marker, int result) {
		String markersPath = FileUtils.stripExtention(filePath);
		markersPath += ".TextGrid";
//		MarkerSetHolder markerSetHolder = getMarkerDao().read();
		String text = createLabelFromTextGrid(new File(markerDir, markersPath), marker);
		if(!StringUtils.hasText(text)){
			return super.createLabel(filePath, marker, result);
		}
		return text;
	}
	/**
	 * 
	 * @param markerPath
	 * @param marker
	 * @return
	 */
	public String createLabelFromTextGrid(File markerPath, Marker marker) {
		MarkerSetHolder markerSetHolder = getMarkerDao().read(markerPath);
		Assert.isTrue(markerSetHolder != null, "Not initialized");
		Assert.isTrue(markerSetHolder.getMarkerSets() != null, "Not initialized");
		MarkerSet markerSet = getSegementedMarkers(markerSetHolder);
		Collection<Marker>  markers = findMappedMarkers(markerSet, marker);
		StringBuilder buf = new StringBuilder();
		for (Marker iMarker : markers) {
			String lbl =iMarker.getLabel().trim();
			lbl = lbl.replace("...", "-");
			lbl = lbl.replace(":", "1");
			lbl = lbl.replace("'", "2");
			lbl = lbl.replace("^", "3");
			buf.append(lbl);
		}
		String bufStr = buf.toString();
		log.debug("[createLabel]{0}: {1}", bufStr, marker);
		return bufStr.toString();
	}
	
	/**
	 * 
	 * @param markerSet
	 * @param matchMarker
	 * @return
	 */
	public Collection<Marker> findMappedMarkers(MarkerSet markerSet, Marker matchMarker){
		List<Marker> rtnMarkers = new ArrayList<Marker>();
		for (Marker iMarker : markerSet.getMarkers()) {
			//i ...xxx...
			//t ..xxx....
			if(iMarker.getStart().compareTo(matchMarker.getStart())<0 
					&& iMarker.getEnd().compareTo(matchMarker.getStart())>0
					&& iMarker.getEnd().compareTo(matchMarker.getEnd())<0){
//				log.debug("[findMappedMarkers]<>< {0}:{1}", matchMarker, iMarker );
				rtnMarkers.add(iMarker);
			}else
			//i ...xxx...
			//t ..xxx....
			if(iMarker.getStart().compareTo(matchMarker.getStart())>0 
					&& iMarker.getEnd().compareTo(matchMarker.getEnd())<0){
//				log.debug("[findMappedMarkers]>< {0}:{1}", matchMarker, iMarker );
				rtnMarkers.add(iMarker);
			}else if(iMarker.getStart().compareTo(matchMarker.getStart())>0 
						&& iMarker.getStart().compareTo(matchMarker.getEnd())<0
						&& iMarker.getEnd().compareTo(matchMarker.getEnd())>0){
//					log.debug("[findMappedMarkers]<<> {0}:{1}", matchMarker, iMarker );
					rtnMarkers.add(iMarker);
			}else{
//				log.debug("[findMappedMarkers]!! {0}:{1}", matchMarker, iMarker );
			}

		}
		return rtnMarkers;
	}
	
	protected MarkerSet getSegementedMarkers(MarkerSetHolder markerSetHolder) {
		MarkerSet segments = markerSetHolder.getMarkerSets().get(
				MarkerSetHolderEnum.word.name());
		if (segments == null) {
			segments = markerSetHolder.getMarkerSets().get(
					MarkerSetHolderEnum.phone.name());
		}

		return segments;
	}

	
	public File getMarkerDir() {
		return markerDir;
	}

	public void setMarkerDir(File file) {
		this.markerDir = file;
	}

	public MarkerDao getMarkerDao() {
		if(markerDao == null){
			markerDao = WorkServiceFactory.createMarkerDao();
		}
		return markerDao;
	}

	public void setMarkerDao(MarkerDao markerDao) {
		this.markerDao = markerDao;
	}
}

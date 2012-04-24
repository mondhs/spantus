package org.spantus.android.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spantus.core.beans.RecognitionResultDetails;
import org.spantus.core.beans.SignalSegment;
import org.spantus.core.marker.Marker;
import org.spantus.exception.ProcessingException;
import org.spantus.extractor.impl.ExtractorEnum;

public class RecognitionResultDetailsJsonDao {
	ExtractorEnum[] extractors = new ExtractorEnum[]{ExtractorEnum.MFCC_EXTRACTOR, ExtractorEnum.LPC_EXTRACTOR, ExtractorEnum.PLP_EXTRACTOR};
	
	public List<RecognitionResultDetails> read(File file) throws FileNotFoundException, JSONException {
		FileInputStream fstream = new FileInputStream(file);
		return read(fstream);
	}

	public List<RecognitionResultDetails> read(InputStream inputStream) throws JSONException {
		 List<RecognitionResultDetails> rtn = new  ArrayList<RecognitionResultDetails>();
		String jsonStr = readInput(inputStream);
		JSONObject jsonObject = new JSONObject(jsonStr);
		JSONArray arrJson = jsonObject.getJSONArray("recognitionResultDetailsList");
		for (int i = 0; i < arrJson.length(); i++) {
			RecognitionResultDetails iDetail = new RecognitionResultDetails();
			JSONObject detail = arrJson.getJSONObject(i);
			JSONObject jInfo = detail.getJSONObject("info");
			iDetail.setInfo(new SignalSegment());
			JSONObject jMarker = jInfo.getJSONObject("marker");
			iDetail.getInfo().setMarker(new Marker());
			iDetail.getInfo().getMarker().setLabel(jMarker.getString("label"));
			
			JSONObject jScores = detail.getJSONObject("scores");
			iDetail.setScores(new HashMap<String, Double>());
			for (ExtractorEnum iEnum : extractors) {
				if(!jScores.has(iEnum.name())){
					continue;
				}
				double value = jScores.getDouble(iEnum.name());
				iDetail.getScores().put(iEnum.name(), value);
			}
			iDetail.setDistance(detail.getDouble("distance"));
			rtn.add(iDetail);
		}
		Collections.sort(rtn, new Comparator<RecognitionResultDetails>() {
			@Override
			public int compare(RecognitionResultDetails lhs,
					RecognitionResultDetails rhs) {
				Double extractorL = lhs.getScores().get(ExtractorEnum.MFCC_EXTRACTOR.name());
				Double extractorR = rhs.getScores().get(ExtractorEnum.MFCC_EXTRACTOR.name());
				if(extractorL == null && extractorR==null){
					return 0;
				}if(extractorL == null){
					return -1;
				}if(extractorR == null){
					return 1;
				}

				return extractorL.compareTo(extractorR);
			}
		} );
		return rtn;
	}
	
	
	private String readInput( InputStream inputStream ) {
	    StringBuilder buffer = new StringBuilder();
	    try {
	        InputStreamReader isr = new InputStreamReader(inputStream, "UTF8");
	        Reader in = new BufferedReader(isr);
	        int ch;
	        while ((ch = in.read()) > -1) {
	            buffer.append((char)ch);
	        }
	        in.close();
	        return buffer.toString();
	    } 
	    catch (IOException e) {
	      throw new ProcessingException(e);
	    }
	}
	
}

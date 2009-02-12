/*
  Copyright (c) 2002-2003, Holger Crysandt
 
  This file is part of the MPEG7AudioEnc project.
 */

package de.crysandt.audio.mpeg7audio;

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.sound.sampled.AudioInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.crysandt.audio.AudioInFloat;
import de.crysandt.audio.AudioInFloatSampled;
import de.crysandt.audio.mpeg7audio.mci.CreationInformation;
import de.crysandt.audio.mpeg7audio.mci.MediaHelper;
import de.crysandt.audio.mpeg7audio.mci.MediaInformation;
import de.crysandt.audio.mpeg7audio.msgs.Msg;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioFundamentalFrequency;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioHarmonicity;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioPower;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioSignature;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioSpectrumBasisProjection;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioSpectrumCentroid;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioSpectrumDistribution;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioSpectrumEnvelope;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioSpectrumFlatness;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioSpectrumSpread;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioTempoType;
import de.crysandt.audio.mpeg7audio.msgs.MsgAudioWaveform;
import de.crysandt.audio.mpeg7audio.msgs.MsgBackgroundNoiseLevel;
import de.crysandt.audio.mpeg7audio.msgs.MsgBandWidth;
import de.crysandt.audio.mpeg7audio.msgs.MsgClick;
import de.crysandt.audio.mpeg7audio.msgs.MsgDcOffset;
import de.crysandt.audio.mpeg7audio.msgs.MsgDigitalClip;
import de.crysandt.audio.mpeg7audio.msgs.MsgDigitalZero;
import de.crysandt.audio.mpeg7audio.msgs.MsgHarmonicSpectralCentroid;
import de.crysandt.audio.mpeg7audio.msgs.MsgHarmonicSpectralDeviation;
import de.crysandt.audio.mpeg7audio.msgs.MsgHarmonicSpectralSpread;
import de.crysandt.audio.mpeg7audio.msgs.MsgHarmonicSpectralVariation;
import de.crysandt.audio.mpeg7audio.msgs.MsgListener;
import de.crysandt.audio.mpeg7audio.msgs.MsgLogAttackTime;
import de.crysandt.audio.mpeg7audio.msgs.MsgResizer;
import de.crysandt.audio.mpeg7audio.msgs.MsgSampleHold;
import de.crysandt.audio.mpeg7audio.msgs.MsgSilence;
import de.crysandt.audio.mpeg7audio.msgs.MsgSoundModel;
import de.crysandt.audio.mpeg7audio.msgs.MsgSpectralCentroid;
import de.crysandt.audio.mpeg7audio.msgs.MsgTemporalCentroid;
import de.crysandt.hmm.GaussianDistribution;
import de.crysandt.xml.Namespace;
@SuppressWarnings("unchecked")
public class MP7DocumentBuilder
	implements MsgListener
{
	private static final String NEWLINE = System.getProperty("line.separator");
	private static final String SPACE = " ";
	private static final String NEXT_BLOCK = "Next Block";
	

/*	private static final float FORMAT_LIMIT_MAX = 1e4f;
	private static final float FORMAT_LIMIT_MIN = 1e-2f;

	private static final DecimalFormat df;
	private static final DecimalFormat df_exp;

*/	
	static {
/*
		DecimalFormatSymbols locale_us = new DecimalFormatSymbols(Locale.US);
		df     = new DecimalFormat("0.#########", locale_us);
		df_exp = new DecimalFormat("0.#######E0", locale_us);
		df_exp.setDecimalFormatSymbols(locale_us);
*/		
	}

	private final TreeMap schema_location = new TreeMap();
   // private boolean checked=false;
	private int duration = 0;

	// start with ArrayList (optimal for add()); sort messages later
	private List listAP   = new ArrayList();
	private List listASBP = new ArrayList();
	private List listASC  = new ArrayList();
	private List listASD  = new ArrayList();
	private List listASE  = new ArrayList();
	private List listASF  = new ArrayList();
	private List listASS  = new ArrayList();
	private List listAW   = new ArrayList();
	private List listAH   = new ArrayList();
	private List listAFF  = new ArrayList();
	private List listDC   = new ArrayList();// DigitalClip
	private List listDZ   = new ArrayList();// DigitalZero
	private List listSH   = new ArrayList();// SampleHold
	private List listCK   = new ArrayList();// Click
	private List listBNL  = new ArrayList();// BackgroundNoiseLevel
	private List listDCO  = new ArrayList();// DcOffset
	private List listBW   = new ArrayList();// BandWidth

	private MsgHarmonicSpectralCentroid  msgHSC = null; // HarmonicSpectralCentroid
	private MsgHarmonicSpectralDeviation msgHSD = null; // HarmonicSpectralDeviation
	private MsgHarmonicSpectralSpread    msgHSS = null; // HarmonicSpectralSpread
	private MsgHarmonicSpectralVariation msgHSV = null; // HarmonicSpectralVariation
	
	private MsgLogAttackTime    msgLAT = null; // LogAttackTime
	private MsgSpectralCentroid msgSC  = null; // SpectralCentroid
	private MsgTemporalCentroid msgTC  = null; // TemporalCentroid

	private List listAS   = new ArrayList();
	private List listSI   = new ArrayList();
	private List listBPM  = new ArrayList();

	private MsgSoundModel msg_sound_model = null;
	
	private MediaInformation		media_information = null;
	private CreationInformation		creation_information = null;

	public void setMediaInformation(MediaInformation mi)
	{
		media_information = mi;
	}

	public void setCreationInformation(CreationInformation ci)
	{
		creation_information = ci;
	}
	
	protected Element createFrame(Document doc) {
		Element mpeg7 = doc.createElementNS(Namespace.MPEG7, "Mpeg7");
		
		// add some namespaces
		mpeg7.setAttributeNS(Namespace.XMLNS, "xmlns", Namespace.MPEG7);
		mpeg7.setAttributeNS(Namespace.XMLNS, "xmlns:mpeg7", Namespace.MPEG7);
		mpeg7.setAttributeNS(Namespace.XMLNS, "xmlns:xsi", Namespace.XSI);
		doc.appendChild(mpeg7);
		
		Element description = doc.createElementNS(Namespace.MPEG7, "Description");		
		description.setAttributeNS(
				Namespace.XSI, "xsi:type", "ContentEntityType");
		mpeg7.appendChild(description);
		
		Element mm_content = doc.createElementNS(
				Namespace.MPEG7,"MultimediaContent");
		mm_content.setAttributeNS(Namespace.XSI, "xsi:type", "AudioType");
		description.appendChild(mm_content);
		
		Element audio_segment = doc.createElementNS(Namespace.MPEG7, "Audio");
		audio_segment.setAttributeNS(
				Namespace.XSI, "xsi:type",	"AudioSegmentType");
		mm_content.appendChild(audio_segment);
		
		return audio_segment;
	}
	
	public Document getDocument()
		throws ParserConfigurationException 
	{
		DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
		
		doc_factory.setNamespaceAware(true);
		//		doc_factory.setValidating(true);
		//		doc_factory.setExpandEntityReferences(true);
		//		doc_factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		
		DocumentBuilder doc_builder = doc_factory.newDocumentBuilder();
		
		Document doc = doc_builder.newDocument();
		
		Element audio_segment = createFrame(doc);
		
		// add everything except descriptors and descrition schemes
		addMediaInformation(doc, audio_segment);
		addCreationInformation(doc, audio_segment);
		
		// add low level descriptors and description schemes
		addAFF(doc, audio_segment);
		addAH(doc, audio_segment);
		addAP(doc, audio_segment);
		
		addASBP(doc, audio_segment);
		addASC(doc, audio_segment);
		addASD(doc, audio_segment);
		addASE(doc, audio_segment);
		addASF(doc, audio_segment);
		addASS(doc, audio_segment);
		addAW(doc, audio_segment);
		
		addLAT(doc, audio_segment);
		addSC(doc, audio_segment);
		addTC(doc, audio_segment);
		
		addHSC(doc, audio_segment);
		addHSD(doc, audio_segment);
		addHSS(doc, audio_segment);
		addHSV(doc, audio_segment);
		
		addASQ(doc, audio_segment);//AudioSignalQuality
		
		// add description schemes
		addAS(doc, audio_segment);
		addSoundModel(doc);
		
		if (!schema_location.isEmpty()) {
			StringBuffer buffer = new StringBuffer();
			for (Iterator i = schema_location.keySet().iterator(); i.hasNext(); ) {
				Object key = i.next();
				Object value = schema_location.get(key);
				buffer.append(key.toString()).append(SPACE);
				buffer.append(value.toString()).append(SPACE);
			}
			
			((Element) doc.getFirstChild()).setAttributeNS(
					Namespace.XSI, "xsi:schemaLocation", buffer.toString());
		}
		
		doc.normalize();
		
		return doc;
	}
	
	public void addSchemaLocation(String schema, String location) {
		schema_location.put(schema, location);		
	}
	
	public void receivedMsg(Msg msg) {
		if (msg instanceof MsgResizer)
			setDuration((MsgResizer) msg);
		else if (msg instanceof MsgDigitalClip)
		    listDC.add(msg);
		else if (msg instanceof MsgDigitalZero)
		    listDZ.add(msg);
		else if (msg instanceof MsgSampleHold)
		    listSH.add(msg);
		else if (msg instanceof MsgClick)
		    listCK.add(msg);
		else if (msg instanceof MsgBackgroundNoiseLevel)
		    listBNL.add(msg);
		else if (msg instanceof MsgDcOffset)
		    listDCO.add(msg);
		else if (msg instanceof MsgBandWidth)
			listBW.add(msg);
		else if (msg instanceof MsgAudioSignature)
			listAS.add(msg);
		else if (msg instanceof MsgAudioSpectrumBasisProjection)
			listASBP.add(msg);
		else if (msg instanceof MsgAudioSpectrumCentroid)
			listASC.add(msg);
		else if (msg instanceof MsgAudioSpectrumSpread)
			listASS.add(msg);
		else if (msg instanceof MsgAudioPower)
			listAP.add(msg);
		else if (msg instanceof MsgAudioSpectrumDistribution)
			listASD.add(msg);
		else if (msg instanceof MsgAudioSpectrumEnvelope)
			listASE.add(msg);
		else if (msg instanceof MsgAudioSpectrumFlatness)
			listASF.add(msg);
		else if (msg instanceof MsgAudioWaveform)
			listAW.add(msg);
		else if (msg instanceof MsgAudioFundamentalFrequency)
			listAFF.add(msg);
		else if (msg instanceof MsgAudioHarmonicity)
			listAH.add(msg);
		else if (msg instanceof MsgSilence)
			listSI.add(msg);
		else if (msg instanceof MsgAudioTempoType)
			listBPM.add(msg);
		else if (msg instanceof MsgHarmonicSpectralCentroid) {
			assert msgHSC == null;
			msgHSC = (MsgHarmonicSpectralCentroid) msg;
		} else if (msg instanceof MsgHarmonicSpectralDeviation) {  
			assert msgHSD == null;
			msgHSD = (MsgHarmonicSpectralDeviation) msg;
		} else if (msg instanceof MsgHarmonicSpectralSpread) {
			assert msgHSS == null;
			msgHSS = (MsgHarmonicSpectralSpread) msg;
		} else if (msg instanceof MsgHarmonicSpectralVariation) {
			assert msgHSV == null;
			msgHSV = (MsgHarmonicSpectralVariation) msg;
		} else if (msg instanceof MsgSoundModel) {
			assert msg_sound_model==null;
			msg_sound_model = (MsgSoundModel) msg;
		}
/*		
		else
			assert false;
*/			
	}
	
	private void addMediaInformation(Document doc, Element audio_segment) {
		if (media_information == null)
			return;
		
		audio_segment.appendChild(media_information.toXML(doc, "MediaInformation"));
	}
	
	private void addCreationInformation(Document doc, Element audio_segment) {
		if (creation_information == null)
			return;
		
		audio_segment.appendChild(creation_information.toXML(doc, "CreationInformation"));
	}
	
	private void setDuration(MsgResizer msg) {
		duration = Math.max(duration, msg.time + msg.duration);
	}
	
	
	private void addASQ(Document doc,Element audio_segment) {
	    
	    if(listDC.isEmpty() &&
	       listDZ.isEmpty() &&
	       listSH.isEmpty() &&
	       listCK.isEmpty() &&
	       listBNL.isEmpty()&&
	       listDCO.isEmpty()&&
	       listBW.isEmpty()) 
	        return;
	
	    
	   Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type", "AudioSignalQuality");
		audio_segment.appendChild(audio_descriptor);
		
		//BackgroundNoiseLevel
		if(!listBNL.isEmpty()){
		   Collections.sort(listBNL);
	       MsgBackgroundNoiseLevel msg = (MsgBackgroundNoiseLevel) listBNL.get(0);
	    
	       StringBuffer numberch = new StringBuffer();
	       StringBuffer buffer_bnl = new StringBuffer();
	       DecimalFormat df = new DecimalFormat("0.####");
	       
		   for (Iterator i = listBNL.iterator(); i.hasNext(); ){
			   msg = (MsgBackgroundNoiseLevel) i.next();
			   numberch.append(msg.channel);
			   if(msg.bnl==100) buffer_bnl.append("-Infinity");
			   else buffer_bnl.append(df.format(msg.bnl));
		   }
		   
	       Element bnl = doc.createElementNS(Namespace.MPEG7, "BackgroundNoiseLevel");
		   audio_descriptor.appendChild(bnl);
		   bnl.setAttributeNS(Namespace.XSI,"channels",numberch.toString());
		
		   Element vector=doc.createElementNS(Namespace.MPEG7,"Vector");
		   bnl.appendChild(vector);
		   vector.appendChild(doc.createTextNode(buffer_bnl.toString()));
		 }
		
        //DCOffset
		if(!listDCO.isEmpty()){
		   Collections.sort(listDCO);
	       MsgDcOffset msg = (MsgDcOffset) listDCO.get(0);
	    
	       StringBuffer numberch = new StringBuffer();
	       StringBuffer buffer_dco = new StringBuffer();
	       DecimalFormat df = new DecimalFormat("0.####");
		   for (Iterator i = listDCO.iterator(); i.hasNext(); ){
			   msg = (MsgDcOffset) i.next();
			   numberch.append(msg.channel);
			   buffer_dco.append(df.format(msg.dco));
		   }
		   
	       Element dco = doc.createElementNS(Namespace.MPEG7,"DcOffset");
		   audio_descriptor.appendChild(dco);
		   dco.setAttributeNS(Namespace.XSI, "channels",numberch.toString());
		
		   Element vector=doc.createElementNS(Namespace.MPEG7,"Vector");
		   dco.appendChild(vector);
		   vector.appendChild(doc.createTextNode(buffer_dco.toString()));
		 }
		
        //BandWidth
		if(!listBW.isEmpty()){
		   Collections.sort(listBW);
	       MsgBandWidth msg = (MsgBandWidth) listBW.get(0);
	    
	       StringBuffer numberch = new StringBuffer();
	       StringBuffer buffer_bw = new StringBuffer();
	       DecimalFormat df = new DecimalFormat("0.####");
		   for (Iterator i = listBW.iterator(); i.hasNext(); ){
			   msg = (MsgBandWidth) i.next();
			   numberch.append(msg.channel);
			   buffer_bw.append(df.format(msg.bw));
			
		   }
	       Element bw = doc.createElementNS(Namespace.MPEG7, "BandWidth");
		   audio_descriptor.appendChild(bw);
		   bw.setAttribute("channels",numberch.toString());
		
		   Element vector=doc.createElementNS(Namespace.MPEG7,"Vector");
		   bw.appendChild(vector);
		   vector.appendChild(doc.createTextNode(buffer_bw.toString()));
		 }
		
		//ErrorEvent
		if(!(listDC.isEmpty()) || !(listDZ.isEmpty()) || !(listCK.isEmpty()) || !(listSH.isEmpty())){

			   Element ev_list=doc.createElementNS(Namespace.MPEG7,"ErrorEventList");
			   audio_descriptor.appendChild(ev_list);
			   
               //Click
			   if(!listCK.isEmpty()){
			       Collections.sort(listCK);
			       Iterator i=listCK.iterator();
			       
			       while(i.hasNext()){
			           MsgClick msg = (MsgClick) i.next();
			           int how_many_clicks=msg.getClicksnumber();
			           
			           StringBuffer numberch = new StringBuffer();
			           StringBuffer mtu=new StringBuffer();
			           StringBuffer mtb=new StringBuffer();
			          
			           mtu.append("PT1N"+(int)msg.SAMPLE_RATE+"F");
		               mtb.append("../../MediaLocator[1]");
		               numberch.append(msg.channel);
		               
			           
			           for(int w=0;w<how_many_clicks;w++){
			               
			               StringBuffer cp=new StringBuffer();
			               cp.append(msg.getClickposition(w));
			               
			           
			               Element ev=doc.createElementNS(Namespace.MPEG7,"ErrorEvent");
				           ev_list.appendChild(ev);
				   
				           Element er_class=doc.createElementNS(Namespace.MPEG7,"ErrorClass");
				           ev.appendChild(er_class);
				           er_class.setAttributeNS(Namespace.XSI,"href","urn:mpeg:mpeg7:cs:ErrorClassCS:click");
				   
				           Element name=doc.createElementNS(Namespace.MPEG7,"Name");
				           er_class.appendChild(name);
				           name.appendChild(doc.createTextNode("Click"));
				   
				           Element channel_no=doc.createElementNS(Namespace.MPEG7,"ChannelNo");
				           ev.appendChild(channel_no);
				           channel_no.appendChild(doc.createTextNode(numberch.toString()));
				   
				           Element time=doc.createElementNS(Namespace.MPEG7,"TimeStamp");
				           ev.appendChild(time);
				   
				           Element time_point=doc.createElementNS(Namespace.MPEG7,"MediaRelIncrTimePoint");
				           time.appendChild(time_point);
				           time_point.setAttributeNS(Namespace.XSI,"mediaTimeUnit",mtu.toString());
				           time_point.setAttributeNS(Namespace.XSI,"mediaTimeBase",mtb.toString());
				           time_point.appendChild(doc.createTextNode(cp.toString()));
				   
				           Element rel=doc.createElementNS(Namespace.MPEG7,"Relevance");
				           ev.appendChild(rel);
				           rel.appendChild(doc.createTextNode("0"));
				   
				           Element det=doc.createElementNS(Namespace.MPEG7,"DetectionProcess");
				           ev.appendChild(det);
				           det.appendChild(doc.createTextNode("automatic"));
				   
				           Element status=doc.createElementNS(Namespace.MPEG7,"Status");
				           ev.appendChild(status);
				           status.appendChild(doc.createTextNode("checked"));
				   
				           Element comm=doc.createElementNS(Namespace.MPEG7,"Comment");
				           ev.appendChild(comm);
				   
				           Element free=doc.createElementNS(Namespace.MPEG7,"FreeTextAnnotation");
				           comm.appendChild(free);
				           free.appendChild(doc.createTextNode("any comment"));
			           }   
			       }
				   
			   }
			   
			   //DigitalClip
			   if(!listDC.isEmpty()){
			       Collections.sort(listDC);
			       Iterator i=listDC.iterator();
			       
			       while(i.hasNext()){
			           MsgDigitalClip msg = (MsgDigitalClip) i.next();
			           int how_many_clips=msg.getClipsnumber();
			           
			           StringBuffer numberch = new StringBuffer();
			           StringBuffer mtu=new StringBuffer();
			           StringBuffer mtb=new StringBuffer();
			          
			           mtu.append("PT1N"+(int)msg.SAMPLE_RATE+"F");
		               mtb.append("../../MediaLocator[1]");
		               numberch.append(msg.channel);
		               
			           
			           for(int k=0;k<how_many_clips;k++){
			               
			               StringBuffer cp=new StringBuffer();
			               cp.append(msg.getClipposition(k));
			               
			               StringBuffer cd=new StringBuffer();
			               cd.append(msg.getCliplength(k));
			               
			               Element ev=doc.createElementNS(Namespace.MPEG7,"ErrorEvent");
				           ev_list.appendChild(ev);
				   
				           Element er_class=doc.createElementNS(Namespace.MPEG7,"ErrorClass");
				           ev.appendChild(er_class);
				           er_class.setAttributeNS(Namespace.XSI,"href","urn:mpeg:mpeg7:cs:ErrorClassCS:digitalclip");
				   
				           Element name=doc.createElementNS(Namespace.MPEG7,"Name");
				           er_class.appendChild(name);
				           name.appendChild(doc.createTextNode("DigitalClip"));
				   
				           Element channel_no=doc.createElementNS(Namespace.MPEG7,"ChannelNo");
				           ev.appendChild(channel_no);
				           channel_no.appendChild(doc.createTextNode(numberch.toString()));
				   
				           Element time=doc.createElementNS(Namespace.MPEG7,"TimeStamp");
				           ev.appendChild(time);
				   
				           Element time_point=doc.createElementNS(Namespace.MPEG7,"MediaRelIncrTimePoint");
				           time.appendChild(time_point);
				           time_point.setAttributeNS(Namespace.XSI,"mediaTimeUnit",mtu.toString());
				           time_point.setAttributeNS(Namespace.XSI,"mediaTimeBase",mtb.toString());
				           time_point.appendChild(doc.createTextNode(cp.toString()));
				           
				           Element time_duration=doc.createElementNS(Namespace.MPEG7,"MediaIncrDuration");
				           time.appendChild(time_duration);
				           time_duration.setAttributeNS(Namespace.XSI,"mediaTimeUnit",mtu.toString());
				           time_duration.appendChild(doc.createTextNode(cd.toString()));
				   
				           Element rel=doc.createElementNS(Namespace.MPEG7,"Relevance");
				           ev.appendChild(rel);
				           rel.appendChild(doc.createTextNode("0"));
				   
				           Element det=doc.createElementNS(Namespace.MPEG7,"DetectionProcess");
				           ev.appendChild(det);
				           det.appendChild(doc.createTextNode("automatic"));
				   
				           Element status=doc.createElementNS(Namespace.MPEG7,"Status");
				           ev.appendChild(status);
				           status.appendChild(doc.createTextNode("checked"));
				   
				           Element comm=doc.createElementNS(Namespace.MPEG7,"Comment");
				           ev.appendChild(comm);
				   
				           Element free=doc.createElementNS(Namespace.MPEG7,"FreeTextAnnotation");
				           comm.appendChild(free);
				           free.appendChild(doc.createTextNode("any comment"));
			           }   
			       }
				   
			   }
               //DigitalZero
			   if(!listDZ.isEmpty()){
			       Collections.sort(listDZ);
			       Iterator i=listDZ.iterator();
			       
			       while(i.hasNext()){
			           MsgDigitalZero msg = (MsgDigitalZero) i.next();
			           int how_many_zeros=msg.getZerosnumber();
			           
			           StringBuffer numberch = new StringBuffer();
			           StringBuffer mtu=new StringBuffer();
			           StringBuffer mtb=new StringBuffer();
			          
			           mtu.append("PT1N"+(int)msg.SAMPLE_RATE+"F");
		               mtb.append("../../MediaLocator[1]");
		               numberch.append(msg.channel);
		               
			           
			           for(int k=0;k<how_many_zeros;k++){
			               
			               StringBuffer zp=new StringBuffer();
			               zp.append(msg.getZeroposition(k));
			               
			               StringBuffer zd=new StringBuffer();
			               zd.append(msg.getZerolength(k));
			               
			               Element ev=doc.createElementNS(Namespace.MPEG7,"ErrorEvent");
				           ev_list.appendChild(ev);
				   
				           Element er_class=doc.createElementNS(Namespace.MPEG7,"ErrorClass");
				           ev.appendChild(er_class);
				           er_class.setAttributeNS(Namespace.XSI,"href","urn:mpeg:mpeg7:cs:ErrorClassCS:digitalzero");
				   
				           Element name=doc.createElementNS(Namespace.MPEG7,"Name");
				           er_class.appendChild(name);
				           name.appendChild(doc.createTextNode("DigitalZero"));
				   
				           Element channel_no=doc.createElementNS(Namespace.MPEG7,"ChannelNo");
				           ev.appendChild(channel_no);
				           channel_no.appendChild(doc.createTextNode(numberch.toString()));
				   
				           Element time=doc.createElementNS(Namespace.MPEG7,"TimeStamp");
				           ev.appendChild(time);
				   
				           Element time_point=doc.createElementNS(Namespace.MPEG7,"MediaRelIncrTimePoint");
				           time.appendChild(time_point);
				           time_point.setAttributeNS(Namespace.XSI,"mediaTimeUnit",mtu.toString());
				           time_point.setAttributeNS(Namespace.XSI,"mediaTimeBase",mtb.toString());
				           time_point.appendChild(doc.createTextNode(zp.toString()));
				           
				           Element time_duration=doc.createElementNS(Namespace.MPEG7,"MediaIncrDuration");
				           time.appendChild(time_duration);
				           time_duration.setAttributeNS(Namespace.XSI,"mediaTimeUnit",mtu.toString());
				           time_duration.appendChild(doc.createTextNode(zd.toString()));
				   
				           Element rel=doc.createElementNS(Namespace.MPEG7,"Relevance");
				           ev.appendChild(rel);
				           rel.appendChild(doc.createTextNode("0"));
				   
				           Element det=doc.createElementNS(Namespace.MPEG7,"DetectionProcess");
				           ev.appendChild(det);
				           det.appendChild(doc.createTextNode("automatic"));
				   
				           Element status=doc.createElementNS(Namespace.MPEG7,"Status");
				           ev.appendChild(status);
				           status.appendChild(doc.createTextNode("checked"));
				   
				           Element comm=doc.createElementNS(Namespace.MPEG7,"Comment");
				           ev.appendChild(comm);
				   
				           Element free=doc.createElementNS(Namespace.MPEG7,"FreeTextAnnotation");
				           comm.appendChild(free);
				           free.appendChild(doc.createTextNode("any comment"));
			           }   
			       }
				   
			   }
			   //SampleHold
			   if(!listSH.isEmpty()){
			       Collections.sort(listSH);
			       Iterator i=listSH.iterator();
			       
			       while(i.hasNext()){
			           MsgSampleHold msg = (MsgSampleHold) i.next();
			           int how_many_s_holds=msg.getShnumber();
			           
			           StringBuffer numberch = new StringBuffer();
			           StringBuffer mtu=new StringBuffer();
			           StringBuffer mtb=new StringBuffer();
			          
			           mtu.append("PT1N"+(int)msg.SAMPLE_RATE+"F");
		               mtb.append("../../MediaLocator[1]");
		               numberch.append(msg.channel);
		               
			           
			           for(int k=0;k<how_many_s_holds;k++){
			               
			               StringBuffer shp=new StringBuffer();
			               shp.append(msg.getshposition(k));
			               
			               StringBuffer shd=new StringBuffer();
			               shd.append(msg.getshlength(k));
			               
			               Element ev=doc.createElementNS(Namespace.MPEG7,"ErrorEvent");
				           ev_list.appendChild(ev);
				   
				           Element er_class=doc.createElementNS(Namespace.MPEG7,"ErrorClass");
				           ev.appendChild(er_class);
				           er_class.setAttributeNS(Namespace.XSI,"href","urn:mpeg:mpeg7:cs:ErrorClassCS:samplehold");
				   
				           Element name=doc.createElementNS(Namespace.MPEG7,"Name");
				           er_class.appendChild(name);
				           name.appendChild(doc.createTextNode("SampleHold"));
				   
				           Element channel_no=doc.createElementNS(Namespace.MPEG7,"ChannelNo");
				           ev.appendChild(channel_no);
				           channel_no.appendChild(doc.createTextNode(numberch.toString()));
				   
				           Element time=doc.createElementNS(Namespace.MPEG7,"TimeStamp");
				           ev.appendChild(time);
				   
				           Element time_point=doc.createElementNS(Namespace.MPEG7,"MediaRelIncrTimePoint");
				           time.appendChild(time_point);
				           time_point.setAttributeNS(Namespace.XSI,"mediaTimeUnit",mtu.toString());
				           time_point.setAttributeNS(Namespace.XSI,"mediaTimeBase",mtb.toString());
				           time_point.appendChild(doc.createTextNode(shp.toString()));
				           
				           Element time_duration=doc.createElementNS(Namespace.MPEG7,"MediaIncrDuration");
				           time.appendChild(time_duration);
				           time_duration.setAttributeNS(Namespace.XSI,"mediaTimeUnit",mtu.toString());
				           time_duration.appendChild(doc.createTextNode(shd.toString()));
				   
				           Element rel=doc.createElementNS(Namespace.MPEG7,"Relevance");
				           ev.appendChild(rel);
				           rel.appendChild(doc.createTextNode("0"));
				   
				           Element det=doc.createElementNS(Namespace.MPEG7,"DetectionProcess");
				           ev.appendChild(det);
				           det.appendChild(doc.createTextNode("automatic"));
				   
				           Element status=doc.createElementNS(Namespace.MPEG7,"Status");
				           ev.appendChild(status);
				           status.appendChild(doc.createTextNode("checked"));
				   
				           Element comm=doc.createElementNS(Namespace.MPEG7,"Comment");
				           ev.appendChild(comm);
				   
				           Element free=doc.createElementNS(Namespace.MPEG7,"FreeTextAnnotation");
				           comm.appendChild(free);
				           free.appendChild(doc.createTextNode("any comment"));
			           }   
			       }
				   
			   }
			   
		}
		
	}

	private void addAFF(Document doc, Element audio_segment) {
		if (this.listAFF.isEmpty())
			return;
		
		Collections.sort(listAFF); 
		MsgAudioFundamentalFrequency msg = 
			(MsgAudioFundamentalFrequency) listAFF.get(0);
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type", "AudioFundamentalFrequencyType");
		audio_descriptor.setAttribute("loLimit", "" + msg.lolimit);
		audio_descriptor.setAttribute("hiLimit", "" + msg.hilimit);
		audio_segment.appendChild(audio_descriptor);
		
		Element sos_aff = getSeriesOfScalar(doc, msg.hopsize, listAFF.size());
		audio_descriptor.appendChild(sos_aff);
		
		Element raw_aff = doc.createElementNS(Namespace.MPEG7, "Raw");
		sos_aff.appendChild(raw_aff);
		
		StringBuffer buffer_aff = new StringBuffer();
		
		for (Iterator i = listAFF.iterator(); i.hasNext(); ){
			msg = (MsgAudioFundamentalFrequency) i.next();			
			buffer_aff.append(msg.fundfreq).append(SPACE);
		}

		raw_aff.appendChild(doc.createTextNode(buffer_aff.toString()));
	}
	
	private void addAH(Document doc, Element audio_segment) {
		if (listAH.isEmpty())
			return;
		
		Collections.sort(listAH);
		MsgAudioHarmonicity msg = (MsgAudioHarmonicity) listAH.get(0);
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type", "AudioHarmonicityType");
		audio_segment.appendChild(audio_descriptor);
		
		Element hr = doc.createElementNS(
				Namespace.MPEG7, "HarmonicRatio");
		audio_descriptor.appendChild(hr);
		
		Element ul = doc.createElementNS(
				Namespace.MPEG7, "UpperLimitOfHarmonicity");
		audio_descriptor.appendChild(ul);
		
		Element sos_hr = getSeriesOfScalar(doc, msg.hopsize, listAH.size());
		hr.appendChild(sos_hr);
		Element raw_hr = doc.createElementNS(Namespace.MPEG7, "Raw");
		sos_hr.appendChild(raw_hr);
		
		Element sos_ul = getSeriesOfScalar(doc, msg.hopsize, listAH.size());
		ul.appendChild(sos_ul);
		Element raw_ul = doc.createElementNS(Namespace.MPEG7, "Raw");
		sos_ul.appendChild(raw_ul);

		StringBuffer buffer_hr = new StringBuffer();
		StringBuffer buffer_ul = new StringBuffer();
		for (Iterator i = listAH.iterator(); i.hasNext(); ){
			msg = (MsgAudioHarmonicity) i.next();
			
			buffer_hr.append(msg.harmonicratio).append(SPACE);
			buffer_ul.append(msg.upperlimit).append(SPACE);
		}
		
		raw_hr.appendChild(doc.createTextNode(buffer_hr.toString()));
		raw_ul.appendChild(doc.createTextNode(buffer_ul.toString()));
	}
	
	private void addAP(Document doc, Element audio_segment) {
		if (listAP.isEmpty())
			return;
		
		Collections.sort(listAP);		
		MsgAudioPower msg = (MsgAudioPower) listAP.get(0);
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type", "AudioPowerType");
		if (msg.db_scale) {
			audio_descriptor.setAttribute("dbScale", "true");
			schema_location.put(
					Namespace.MPEG7,
					"http://www.ient.rwth-aachen.de" + 
					"/team/crysandt/mpeg7mds/mpeg7patched.xml");
		}
		audio_segment.appendChild(audio_descriptor);
		
		Element sos = getSeriesOfScalar(doc, msg.hopsize, listAP.size());
		audio_descriptor.appendChild(sos);
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator i = listAP.iterator(); i.hasNext(); ) {
			buffer.append(format(((MsgAudioPower) i.next()).power));
			buffer.append(SPACE);
		}
		
		Element raw = doc.createElementNS(Namespace.MPEG7, "Raw");
		sos.appendChild(raw);
		raw.appendChild(doc.createTextNode(buffer.toString().trim()));
	}
	
	private void addASBP(Document doc, Element audio_segment) {
		if (listASBP.isEmpty())
			return; 
		
		Collections.sort(listASBP);
		MsgAudioSpectrumBasisProjection msg =
			(MsgAudioSpectrumBasisProjection) listASBP.get(0);
		
		// create tree for basis
		Element audio_descriptor_basis = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor_basis.setAttribute("loEdge", "" + msg.lo_edge);
		audio_descriptor_basis.setAttribute("hiEdge", "" + msg.hi_edge);
		audio_descriptor_basis.setAttribute("octaveResolution", 
				(msg.resolution >= 1.0f ? 
						"" + Math.round(msg.resolution) : 
						"1/" + Math.round(msg.resolution)));
		audio_descriptor_basis.setAttributeNS(
				Namespace.XSI, "xsi:type", "AudioSpectrumBasisType");
		audio_segment.appendChild(audio_descriptor_basis);

		float[][] basis = msg.getBasis();
		int basis_rows = basis.length;
		int basis_cols = basis[0].length;
		Element sov_basis = getSeriesOfVector(
				doc, msg.hopsize, basis_rows, basis_cols);
		audio_descriptor_basis.appendChild(sov_basis);
		
		Element raw_basis = doc.createElementNS(Namespace.MPEG7, "Raw");
		raw_basis.setAttributeNS(
				Namespace.MPEG7, 
				"mpeg7:dim", 
				listASBP.size() + " " + basis_rows + " " + basis_cols);
		sov_basis.appendChild(raw_basis);
		
		raw_basis.appendChild(doc.createTextNode(
				append(new StringBuffer(), basis).toString()));
		
		// create tree for projection
		Element audio_descriptor_projection = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor_projection.setAttributeNS
			(Namespace.XSI, "xsi:type", "AudioSpectrumProjectionType");
		audio_segment.appendChild(audio_descriptor_projection);
		
		float[][] projection = msg.getProjection();
		int projection_rows = projection.length;
		int projection_cols = projection[0].length;
		
		Element sov_projection = getSeriesOfVector(
				doc, msg.hopsize, projection_rows, projection_cols);
		audio_descriptor_projection.appendChild(sov_projection);
		
		Element raw_projection = doc.createElementNS(Namespace.MPEG7, "Raw");
		raw_projection.setAttributeNS(
				Namespace.MPEG7, 
				"mpeg7:dim", 
				listASBP.size() + " " + projection_rows + " " + projection_cols);
		sov_projection.appendChild(raw_projection);
		
		raw_projection.appendChild(doc.createTextNode(
				append(new StringBuffer(), projection).toString()));
		
		if (listASBP.size()>1) {
			Iterator i = listASBP.iterator();
			i.next(); 		// drop first msg (fields already added)
			
			while (i.hasNext()){
				msg = (MsgAudioSpectrumBasisProjection) i.next();
				
				raw_basis.appendChild(doc.createComment(NEXT_BLOCK));
				raw_basis.appendChild(doc.createTextNode(append(
								new StringBuffer(NEWLINE), 
								msg.getBasis()).toString()));

				raw_projection.appendChild(doc.createComment(NEXT_BLOCK));
				raw_projection.appendChild(doc.createTextNode(append(
								new StringBuffer(NEWLINE), 
								msg.getProjection()).toString()));
			}
		}
	}
	
	private void addASC(Document doc, Element audio_segment) {
		if (listASC.isEmpty())
			return;

		Collections.sort(listASC);
		MsgAudioSpectrumCentroid msg = (MsgAudioSpectrumCentroid) listASC.get(0);
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type",	"AudioSpectrumCentroidType");
		audio_segment.appendChild(audio_descriptor);
		
		Element sos = getSeriesOfScalar(doc, msg.hopsize, listASC.size());
		audio_descriptor.appendChild(sos);
		
		Element raw = doc.createElementNS(Namespace.MPEG7, "Raw");
		sos.appendChild(raw);
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator i = listASC.iterator(); i.hasNext(); ) {
			buffer.append("" +
					format(((MsgAudioSpectrumCentroid) i.next()).centroid));
			buffer.append(SPACE);
		}
		raw.appendChild(doc.createTextNode(buffer.toString()));
	}
	
	private void addASS(Document doc, Element audio_segment) {
		if (listASS.isEmpty())
			return;
		
		Collections.sort(listASS);
		MsgAudioSpectrumSpread msg = (MsgAudioSpectrumSpread) listASS.get(0);

		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type",	"AudioSpectrumSpreadType");
		audio_segment.appendChild(audio_descriptor);
		
		Element sos = getSeriesOfScalar(doc, msg.hopsize, listASS.size());
		audio_descriptor.appendChild(sos);
		
		Element raw = doc.createElementNS(Namespace.MPEG7, "Raw");
		sos.appendChild(raw);
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator i = listASS.iterator(); i.hasNext(); ) {
			buffer.append("" +
					format(((MsgAudioSpectrumSpread) i.next()).spread));
			buffer.append(SPACE);
		}
		raw.appendChild(doc.createTextNode(buffer.toString()));
	}
	
	private void addASD(Document doc, Element audio_segment) {
		if (listASD.isEmpty())
			return;
		
		// add new namespace with location
		doc.getDocumentElement().setAttributeNS(
				Namespace.XMLNS, "xmlns:mpeg7hc", Namespace.MPEG7HC);
		schema_location.put(
				Namespace.MPEG7HC,
				"http://www.ient.rwth-aachen.de/team/crysandt/mpeg7mds/mpeg7hc.xsd");
		
		Collections.sort(listASD);
		MsgAudioSpectrumDistribution msg = 
			(MsgAudioSpectrumDistribution) listASD.get(0);
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttribute("loEdge", "" + msg.lo_edge);
		audio_descriptor.setAttribute("hiEdge", "" + msg.hi_edge);
		audio_descriptor.setAttribute("octaveResolution",
				getOctaveResolution(msg.resolution));
		audio_descriptor.setAttributeNS(
				Namespace.XSI, 
				"xsi:type",
				"mpeg7hc:AudioSpectrumDistributionType");
		audio_segment.appendChild(audio_descriptor);
		
		int rows = listASD.size();
		int cols = msg.getDistributionLength();
		
		Element sov = getSeriesOfVector(doc, msg.hopsize, rows, cols);
		audio_descriptor.appendChild(sov);
		
		Element raw = doc.createElementNS(Namespace.MPEG7, "Raw");
		raw.setAttributeNS(Namespace.MPEG7, "mpeg7:dim", rows + " " + cols);
		sov.appendChild(raw);
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator i = listASD.iterator(); i.hasNext(); ) {
			float[] distribution = ((MsgAudioSpectrumDistribution) i.next()).getDistribution();
			
			assert distribution.length == cols;
			
			append(buffer, distribution);
			buffer.append(NEWLINE);
		}
		
		raw.appendChild(doc.createTextNode(buffer.toString()));
	}
	
	private void addASE(Document doc, Element audio_segment) {
		if (listASE.isEmpty())
			return;
		
		Collections.sort(listASE);
		MsgAudioSpectrumEnvelope msg = (MsgAudioSpectrumEnvelope) listASE.get(0);
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		
		audio_descriptor.setAttribute("loEdge", "" + msg.lo_edge);
		audio_descriptor.setAttribute("hiEdge", "" + msg.hi_edge);
		audio_descriptor.setAttribute(
				"octaveResolution", getOctaveResolution(msg.resolution));
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type",	"AudioSpectrumEnvelopeType");
		
		if (msg.db_scale) {
			audio_descriptor.setAttribute("dbScale", "true");
		}
		
		switch (msg.normalize) {
			case AudioSpectrumEnvelope.NORMALIZE_OFF:
				break;
			case AudioSpectrumEnvelope.NORMALIZE_POWER:
				audio_descriptor.setAttribute("normalize", "power");
				break;
			case AudioSpectrumEnvelope.NORMALIZE_NORM2:
				audio_descriptor.setAttribute("normalize", "norm2");
				break;
			default:
				assert false;
		}

		if (msg.db_scale || (msg.normalize!=AudioSpectrumEnvelope.NORMALIZE_OFF)) {
			schema_location.put(
					Namespace.MPEG7,
					"http://www.ient.rwth-aachen.de" + 
					"/team/crysandt/mpeg7mds/mpeg7patched.xml");
		}	
		
		audio_segment.appendChild(audio_descriptor);
		
		int rows = listASE.size();
		int cols = msg.getEnvelopeLength();
		Element sov = getSeriesOfVector(doc, msg.hopsize, rows, cols);
		audio_descriptor.appendChild(sov);
		
		Element raw = doc.createElementNS(Namespace.MPEG7, "Raw");
		raw.setAttributeNS(Namespace.MPEG7, "mpeg7:dim", rows + " " + cols);
		sov.appendChild(raw);
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator i = listASE.iterator(); i.hasNext(); ) {
			float[] envelope = ((MsgAudioSpectrumEnvelope) i.next()).getEnvelope();
			assert envelope.length == cols;
			for (int c = 0; c < cols; ++c) {
//				buffer.append(format(envelope[c]));
				buffer.append(envelope[c]);
				buffer.append(SPACE);
			}
			buffer.append(NEWLINE);
		}
		
		raw.appendChild(doc.createTextNode(buffer.toString()));
	}
	
	private void addASF(Document doc, Element audio_segment) {
		if (listASF.isEmpty())
			return;
		
		Collections.sort(listASF);
		MsgAudioSpectrumFlatness msg = (MsgAudioSpectrumFlatness) listASF.get(0);
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttribute("loEdge", "" + msg.lo_edge);
		audio_descriptor.setAttribute("hiEdge", "" + msg.hi_edge);
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type",	"AudioSpectrumFlatnessType");
		audio_segment.appendChild(audio_descriptor);
		
		final int rows = listASF.size();
		final int cols = msg.getFlatnessLength();
		
		Element sov = getSeriesOfVector(doc, msg.hopsize, rows, cols);
		audio_descriptor.appendChild(sov);
		
		Element raw = doc.createElementNS(Namespace.MPEG7, "Raw");
		raw.setAttributeNS(Namespace.MPEG7, "mpeg7:dim", rows + " " + cols);
		sov.appendChild(raw);
		
		StringBuffer buffer = new StringBuffer();
		for (Iterator i = listASF.iterator(); i.hasNext(); ) {
			float[] flatness = ((MsgAudioSpectrumFlatness) i.next()).
			getFlatness();
			assert flatness.length == cols;
			for (int c = 0; c < cols; ++c) {
//				buffer.append(format(flatness[c]));
				buffer.append(flatness[c]);
				buffer.append(SPACE);
			}
			buffer.append(NEWLINE);
		}
		
		raw.appendChild(doc.createTextNode(buffer.toString()));
	}
	
	private void addAW(Document doc, Element audio_segment) {
		if (listAW.isEmpty())
			return;
		
		Collections.sort(listAW);
		MsgAudioWaveform msg = (MsgAudioWaveform) listAW.get(0);
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type",	"AudioWaveformType");
		audio_segment.appendChild(audio_descriptor);
		
		Element sos = getSeriesOfScalar(doc, msg.hopsize, listAW.size());
		audio_descriptor.appendChild(sos);
		
		StringBuffer buffer_min = new StringBuffer();
		StringBuffer buffer_max = new StringBuffer();
		for (Iterator i = listAW.iterator(); i.hasNext(); ) {
			msg = (MsgAudioWaveform) i.next();
			
			buffer_min.append(format(msg.min));
			buffer_min.append(SPACE);
			
			buffer_max.append(format(msg.max));
			buffer_max.append(SPACE);
		}
		
		Element min = doc.createElementNS(Namespace.MPEG7, "Min");
		min.appendChild(doc.createTextNode(buffer_min.toString().trim()));
		sos.appendChild(min);
		
		Element max = doc.createElementNS(Namespace.MPEG7, "Max");
		max.appendChild(doc.createTextNode(buffer_max.toString().trim()));
		sos.appendChild(max);
	}
	
	private void addHSC(Document doc, Element audio_segment) {
		if (msgHSC==null)
			return;
		
		addHS(doc, audio_segment, "HarmonicSpectralCentroidType", msgHSC.hsc);
	}
	
	private void addHSD(Document doc, Element audio_segment) {
		if (msgHSD==null)
			return;
		
		addHS(doc, audio_segment, "HarmonicSpectralDeviationType", msgHSD.hsd);
	}
	
	private void addHSS(Document doc, Element audio_segment) {
		if (msgHSS==null)
			return;
		
		addHS(doc, audio_segment, "HarmonicSpectralSpreadType", msgHSS.hss);
	}
	
	private void addHSV(Document doc, Element audio_segment) {
		if (msgHSV==null)
			return;
		
		addHS(doc, audio_segment, "HarmonicSpectralVariationType", msgHSV.hsv);
	}
	
	private void addHS(
			Document doc, 
			Element audio_segment, 
			String type, 
			float value) 
	{
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor"); 
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type", type);
		audio_segment.appendChild(audio_descriptor);
		
		Element scalar = doc.createElementNS(
				Namespace.MPEG7, "Scalar");
		audio_descriptor.appendChild(scalar);
		
		scalar.appendChild(doc.createTextNode(""+ value));
	}
	
	private void addAS(Document doc, Element audio_segment) {
		if (listAS.isEmpty())
			return;
		
		Collections.sort(listAS);
		MsgAudioSignature msg = (MsgAudioSignature) listAS.get(0);
		
		Element audio_ds = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptionScheme");
		audio_ds.setAttributeNS(
				Namespace.XSI, "xsi:type", "AudioSignatureType");
		audio_segment.appendChild(audio_ds);
		
		Element flatness = doc.createElementNS(
				Namespace.MPEG7, "Flatness");
		flatness.setAttribute("loEdge", "" + msg.lo_edge);
		flatness.setAttribute("hiEdge", "" + msg.hi_edge);
		audio_ds.appendChild(flatness);
		
		Element sov = getSeriesOfVector(
				doc,msg.hopsize, listAS.size(), msg.getLength());
		flatness.appendChild(sov);
		
		Element scaling = doc.createElementNS(
				Namespace.MPEG7, "Scaling");
		scaling.setAttribute("numOfElements", "" + listAS.size());
		scaling.setAttribute("ratio", "" + msg.decimation);
		sov.appendChild(scaling);
		
		Element mean = doc.createElementNS(Namespace.MPEG7, "Mean");
		Element var = doc.createElementNS(Namespace.MPEG7, "Variance");

		String dim = listAS.size() + " " + msg.getLength();
		mean.setAttributeNS(Namespace.MPEG7, "mpeg7:dim", dim);
		var.setAttributeNS(Namespace.MPEG7, "mpeg7:dim", dim);
		
		sov.appendChild(mean);
		sov.appendChild(var);
		
		StringBuffer buffer_mean = new StringBuffer();
		StringBuffer buffer_var  = new StringBuffer();
		
		for (Iterator i=listAS.iterator(); i.hasNext(); ) {
			msg = (MsgAudioSignature) i.next();
			float[] m = msg.getFlatnessMean();
			float[] v = msg.getFlatnessVariance();
			
			for (int n=0; n<m.length; ++n) {
				buffer_mean.append(format(m[n])).append(SPACE);
				buffer_var.append(format(v[n])).append(SPACE);
			}
			buffer_mean.append(NEWLINE);
			buffer_var.append(NEWLINE);
		}
		
		mean.appendChild(doc.createTextNode(buffer_mean.toString()));
		var.appendChild(doc.createTextNode(buffer_var.toString()));
	}
	
	private void addSoundModel(Document doc) {
		if (this.msg_sound_model==null)
			return;
		
		Element mpeg7 = doc.getDocumentElement();
		assert mpeg7.getNodeName().equals("Mpeg7");
		
		Element description = doc.createElementNS(Namespace.MPEG7, "Description");
		description.setAttributeNS(
				Namespace.XSI, "xsi:type", "ModelDescriptionType");
		mpeg7.appendChild(description);

		de.crysandt.hmm.HMM hmm = msg_sound_model.hmm;
		
		Element sound_model = doc.createElementNS(Namespace.MPEG7, "Model");
		sound_model.setAttributeNS(Namespace.XSI, "xsi:type", "SoundModelType");
		sound_model.setAttribute("numOfStates", "" + hmm.N);
		description.appendChild(sound_model);
		
		float[] init = hmm.getInit(); 
		if (init!=null) {
			Element initial = doc.createElementNS(Namespace.MPEG7, "Initial");
			sound_model.appendChild(initial);
			
			initial.setAttributeNS(
					Namespace.MPEG7, "mpeg7:dim", "1 " + init.length);
			StringBuffer buffer = new StringBuffer();
			for (int i=0; i<init.length; ++i)
				buffer.append(init[i]).append(SPACE);	
			initial.appendChild(doc.createTextNode(buffer.toString()));
		}
		
		float[][] trans = hmm.getTransitions();

		Element transitions = doc.createElementNS(Namespace.MPEG7, "Transitions");
		transitions.setAttributeNS(
				Namespace.MPEG7, "mpeg7:dim", trans.length + " " +trans.length);
		sound_model.appendChild(transitions);
		
		StringBuffer buffer_trans = new StringBuffer();
		for (int i=0; i<trans.length; ++i) {
			float[] row = trans[i];
			for (int j=0; j<row.length; ++j)
				buffer_trans.append(row[j]).append(SPACE);
			buffer_trans.append(NEWLINE);			
		}
		transitions.appendChild(doc.createTextNode(buffer_trans.toString()));
		
		for (int i=0, i_max = init.length; i<i_max; ++i) {
			Element state = doc.createElementNS(Namespace.MPEG7, "State");
			sound_model.appendChild(state);
			
			Element label = doc.createElementNS(Namespace.MPEG7, "Label");
			state.appendChild(label);
			
			String state_i = "State" + i;
			Element term = doc.createElementNS(Namespace.MPEG7, "Term");
			term.setAttribute("termID", state_i);
			label.appendChild(term);
			
			Element name = doc.createElementNS(Namespace.MPEG7, "Name");
			name.appendChild(doc.createTextNode(state_i));
			term.appendChild(name);
		}

		Element descriptor_model = doc.createElementNS(
				Namespace.MPEG7, "DescriptorModel");
		sound_model.appendChild(descriptor_model);
		
		Element descriptor = doc.createElementNS(Namespace.MPEG7, "Descriptor");
		descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type" , "AudioSpectrumProjectionType");
		descriptor_model.appendChild(descriptor);
		
		Element sov = doc.createElementNS(Namespace.MPEG7, "SeriesOfVector");
		sov.setAttribute("totalNumOfSamples", "1");
		descriptor.appendChild(sov);
		
		Element field = doc.createElementNS(Namespace.MPEG7, "Field");
		field.appendChild(doc.createTextNode("SeriesOfVector"));
		descriptor_model.appendChild(field);
		
		for (int i=0, i_max=hmm.N; i<i_max; ++i)  {
			Element od = doc.createElementNS(
					Namespace.MPEG7, "ObservationDistribution");
			od.setAttributeNS(
					Namespace.XSI, "xsi:type", "GaussianDistributionType");
			sound_model.appendChild(od);
			
			GaussianDistribution gd = hmm.getDist(i);
			
			// center
			float[] gd_mean = gd.getCenter();
			Element mean = doc.createElementNS(Namespace.MPEG7, "Mean");
			mean.setAttributeNS(Namespace.MPEG7, "mpeg7:dim", "1 " + gd_mean.length);
			od.appendChild(mean);
			
			StringBuffer buffer_mean = new StringBuffer();
			for (int n=0, size=gd.getLength(); n<size; ++n)
				buffer_mean.append(gd_mean[n]).append(SPACE);
			mean.appendChild(doc.createTextNode(buffer_mean.toString()));
			
			// inverser covariance matrix
			float[][] gd_cov_inv = gd.getCovarianceInverse();
			Element cov_inv = doc.createElementNS(
					Namespace.MPEG7, "CovarianceInverse");
			cov_inv.setAttributeNS(
					Namespace.MPEG7, 
					"mpeg7:dim", 
					gd_cov_inv.length + " " + gd_cov_inv.length);		
			od.appendChild(cov_inv);
			
			StringBuffer buffer_cov_inv = new StringBuffer();
			for (int n=0; n<gd_cov_inv.length; ++n) {
				float[] row = gd_cov_inv[n];
				for(int m=0; m<row.length; ++m)
					buffer_cov_inv.append(row[m]).append(SPACE);
				buffer_cov_inv.append(NEWLINE);
			}				
			cov_inv.appendChild(doc.createTextNode(buffer_cov_inv.toString()));
		}
		
		// label
		Element sound_class_label = doc.createElementNS(
				Namespace.MPEG7, "SoundClassLabel");
		sound_model.appendChild(sound_class_label);
		
		Element name = doc.createElementNS(Namespace.MPEG7, "Name");
		name.appendChild(doc.createTextNode(msg_sound_model.label));
		sound_class_label.appendChild(name);
		
		// spectrum basis
		Element spectrum_basis = doc.createElementNS(
				Namespace.MPEG7, "SpectrumBasis");
		spectrum_basis.setAttribute("loEdge", "" + msg_sound_model.lo_edge);
		spectrum_basis.setAttribute("hiEdge", "" + msg_sound_model.hi_edge);
		String resolution = msg_sound_model.resolution >= 1.0f ?
				"" + (int) msg_sound_model.resolution : 
				"1/"  + (int) (1.0f/msg_sound_model.resolution);
		spectrum_basis.setAttribute("octaveResolution", resolution);
		sound_model.appendChild(spectrum_basis);
		
		sov = getSeriesOfVector(doc, msg_sound_model.hopsize, 1, 1);
		spectrum_basis.appendChild(sov);
		
		float[][] basis = msg_sound_model.audio_spectrum_basis; 

		Element raw = doc.createElementNS(Namespace.MPEG7, "Raw");
		int rows = basis.length;
		int cols = basis[0].length;
		raw.setAttributeNS(Namespace.MPEG7, "mpeg7:dim", rows + " " + cols);
		sov.appendChild(raw);
		
		StringBuffer buffer_raw = new StringBuffer();
		for (int m=0; m<basis.length; ++m) {
			float[] row = basis[m];
			for (int n=0; n<row.length; ++n)
				buffer_raw.append(row[n]).append(SPACE);
			buffer_raw.append(NEWLINE);
		}
		
		raw.appendChild(doc.createTextNode(buffer_raw.toString()));		
	}
	
	private void addLAT(Document doc, Element audio_segment) {
		if (msgLAT==null)
			return;
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type", "LogAttackTimeType");
		audio_segment.appendChild(audio_descriptor);
		
		Element scalar = doc.createElementNS(
				Namespace.MPEG7, "Scalar");
		audio_descriptor.appendChild(scalar);
		
		scalar.appendChild(doc.createTextNode(format(msgLAT.lat)));
	}
	
	private void addSC(Document doc, Element audio_segment) {
		if (msgSC==null)
			return;
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type", "SpectralCentroidType");
		audio_segment.appendChild(audio_descriptor);
		
		Element scalar = doc.createElementNS(Namespace.MPEG7, "Scalar");
		audio_descriptor.appendChild(scalar);
		
		scalar.appendChild(doc.createTextNode(format(msgSC.spectralCentroid)));
	}
	
	private void addTC(Document doc, Element audio_segment) {
		if (msgTC==null)
			return;
		
		Element audio_descriptor = doc.createElementNS(
				Namespace.MPEG7, "AudioDescriptor");
		audio_descriptor.setAttributeNS(
				Namespace.XSI, "xsi:type", "TemporalCentroidType");
		audio_segment.appendChild(audio_descriptor);
		
		Element scalar = doc.createElementNS(Namespace.MPEG7, "Scalar");
		audio_descriptor.appendChild(scalar);
		
		scalar.appendChild(doc.createTextNode(format(msgTC.temporalCentroid)));
	}
	
	public static Element getSeriesOfScalar(
			Document doc,
			int hop_size,
			int num_of_samples) {
		Element sos = doc.createElementNS(Namespace.MPEG7, "SeriesOfScalar");
		
		sos.setAttribute("hopSize", getMediaDuration(hop_size));
		sos.setAttribute("totalNumOfSamples", "" + num_of_samples);
		return sos;
	}
	
	public static Element getSeriesOfVector(
			Document doc,
			int hop_size,
			int rows,
			int cols) 
	{
		Element sov = doc.createElementNS(Namespace.MPEG7, "SeriesOfVector");
		
		sov.setAttribute("hopSize", getMediaDuration(hop_size));
		sov.setAttribute("totalNumOfSamples", "" + (rows * cols));
		sov.setAttribute("vectorSize", "" + cols);
		
		return sov;
	}
	
	/**
	 * Creates a MediaDuration from a duration given in milliseconds
	 *
	 * @param time int time in milliseconds
	 * @return String MPEG-7 compliant MediaDuration
	 */
	private static String getMediaDuration(int time) {
		assert time > 0;
		
		int msec = time % 1000;
		int sec = (time /= 1000) % 60;
		int min = (time /= 60) % 60;
		
		StringBuffer duration = new StringBuffer("PT");
		
		if (min > 0) {
			duration.append(min);
			duration.append("M");
		}
		
		if ((sec > 0) || (min > 0)) {
			duration.append(sec);
			duration.append("S");
		}
		
		if (msec > 0) {
			duration.append(msec);
			duration.append("N1000F");
		}
		
		return duration.toString();
	}
	
	private static String getOctaveResolution(float resolution) {
		if (resolution >= 1)
			return "" + Math.round(resolution);
		
		return "1/" + Math.round(1.0f / resolution);
	}
	
	private static StringBuffer append(StringBuffer buffer, float[] vector) {
		if ((vector!=null) && (vector.length>0)) {
			buffer.append(vector[0]);			
			for (int i=1; i<vector.length; ++i)
				buffer.append(SPACE).append(vector[i]);
		}
		
		return buffer;
	}
	
	private static StringBuffer append(StringBuffer buffer, float[][] matrix) {
		if ((matrix!=null) && (matrix.length>0)) {
			for (int i=0; i<matrix.length; ++i) {
				append(buffer, matrix[i]);
				buffer.append(NEWLINE);
			}
		}
		
		return buffer;
	}
	
	/**
	 * @deprecated
	 * @param x float value to be converted
	 * @return Returns String of float value
	 */
	private static String format(float x) {
		return ""+x;
		/*
		if (x == 0.0f)
			return "0";
		
		float x_abs = Math.abs(x);
		if ((x_abs > FORMAT_LIMIT_MAX) || (x_abs < FORMAT_LIMIT_MIN))
			return df_exp.format(x);
		else
			return df.format(x);
			*/
	}
	
	public static Document encode(AudioInputStream ais, Config config) 
		throws ParserConfigurationException
	{
		AudioInFloatSampled audioin = new AudioInFloatSampled(ais);
		MP7DocumentBuilder mp7out = new MP7DocumentBuilder();
		Encoder encoder = new Encoder(audioin.getSampleRate(), mp7out, config); 
		
		// copy audio signal from source to encoder
		float[] audio;
		while ((audio = audioin.get()) != null) {
			if (!audioin.isMono())
				audio = AudioInFloat.getMono(audio);
			encoder.put(audio);
		}
		encoder.flush();

		return mp7out.getDocument();		
	}
	
	public static void main(String[] args) {
		try {
			// open audio source
			File file = new File(args[0]);
			AudioInFloatSampled audioin = new AudioInFloatSampled(file);
			
			// create MPEG-7 DocumentBuilder
			MP7DocumentBuilder mp7out = new MP7DocumentBuilder();
			mp7out.addSchemaLocation(
					Namespace.MPEG7,
					"http://www.ient.rwth-aachen.de/team/crysandt/mpeg7mds/mpeg7ver1.xsd");
			
			MediaInformation mi = MediaHelper.createMediaInformation();
			MediaHelper.setMediaLocation(mi, file.toURI());
			mp7out.setMediaInformation(mi);
			
			// create encoder
			Encoder encoder = null;
			if (args.length == 2) {
				// read config from second file
				Config config = ConfigXML.parse(new FileReader(args[1]));
				encoder = new Encoder(audioin.getSampleRate(), mp7out, config); 
			} else {
				encoder = new Encoder(audioin.getSampleRate(), mp7out);
			}
			
			// add 0:00, 0:01, ... output
			encoder.addTimeElapsedListener(new Ticker(System.err));
			
			// copy audio signal from source to encoder
			float[] audio;
			while ((audio = audioin.get()) != null) {
				if (!audioin.isMono())
					audio = AudioInFloat.getMono(audio);
				encoder.put(audio);
			}
			encoder.flush();

			// get MPEG-7 description
			Document mp7 = mp7out.getDocument();
			
			// initialize output format
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			// write MPEG-7 description to file
			transformer.transform(
					new DOMSource(mp7), 
					new StreamResult(System.out));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit( -1);
		}		
	}
}

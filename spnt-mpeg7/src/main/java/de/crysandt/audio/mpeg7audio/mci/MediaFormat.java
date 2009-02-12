/*
 * Created on Jun 30, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.*;

import de.crysandt.xml.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class MediaFormat implements DescriptorI
{
/*	<!-- ##################################### -->
	<!--  Definition of MediaFormat D (8.2.4)  -->
	<!-- ##################################### -->
	<complexType name="MediaFormatType">
		<complexContent>
			<extension base="mpeg7v1:DType">
				<sequence>
					<element name="Content" type="mpeg7v1:ControlledTermUseType"/>
					<element name="Medium" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
					<element name="FileFormat" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
					<element name="FileSize" type="nonNegativeInteger" minOccurs="0"/>
					<element name="System" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
					<element name="Bandwidth" type="float" minOccurs="0"/>
					<element name="BitRate" minOccurs="0">
						<complexType>
							<simpleContent>
								<extension base="nonNegativeInteger">
									<attribute name="variable" type="boolean" use="optional" default="false"/>
									<attribute name="minimum" type="nonNegativeInteger" use="optional"/>
									<attribute name="average" type="nonNegativeInteger" use="optional"/>
									<attribute name="maximum" type="nonNegativeInteger" use="optional"/>
								</extension>
							</simpleContent>
						</complexType>
					</element>
					<element name="TargetChannelBitRate" type="nonNegativeInteger" minOccurs="0"/>
					<element name="ScalableCoding" minOccurs="0">
						<simpleType>
							<union>
								<simpleType>
									<restriction base="NMTOKEN">
										<enumeration value="spatial"/>
										<enumeration value="temporal"/>
										<enumeration value="snr"/>
										<enumeration value="fgs"/>
									</restriction>
								</simpleType>
								<simpleType>
									<restriction base="mpeg7v1:termReferenceType"/>
								</simpleType>
							</union>
						</simpleType>
					</element>
					<element name="VisualCoding" minOccurs="0">
						<complexType>
							<sequence>
								<element name="Format" minOccurs="0">
									<complexType>
										<complexContent>
											<extension base="mpeg7v1:ControlledTermUseType">
												<attribute name="colorDomain" use="optional" default="color">
													<simpleType>
														<union>
															<simpleType>
																<restriction base="NMTOKEN">
																	<enumeration value="binary"/>
																	<enumeration value="color"/>
																	<enumeration value="graylevel"/>
																	<enumeration value="colorized"/>
																</restriction>
															</simpleType>
															<simpleType>
																<restriction base="mpeg7v1:termReferenceType"/>
															</simpleType>
														</union>
													</simpleType>
												</attribute>
											</extension>
										</complexContent>
									</complexType>
								</element>
								<element name="Pixel" minOccurs="0">
									<complexType>
										<attribute name="resolution" type="nonNegativeInteger" use="optional"/>
										<attribute name="aspectRatio" type="mpeg7v1:nonNegativeReal" use="optional"/>
										<attribute name="bitsPer" type="nonNegativeInteger" use="optional"/>
									</complexType>
								</element>
								<element name="Frame" minOccurs="0">
									<complexType>
										<attribute name="height" type="nonNegativeInteger" use="optional"/>
										<attribute name="width" type="nonNegativeInteger" use="optional"/>
										<attribute name="aspectRatio" type="mpeg7v1:nonNegativeReal" use="optional"/>
										<attribute name="rate" type="mpeg7v1:nonNegativeReal" use="optional"/>
										<attribute name="structure" use="optional">
											<simpleType>
												<restriction base="NMTOKEN">
													<enumeration value="progressive"/>
													<enumeration value="interlaced"/>
												</restriction>
											</simpleType>
										</attribute>
									</complexType>
								</element>
								<element name="ColorSampling" type="mpeg7v1:ColorSamplingType" minOccurs="0"/>
							</sequence>
						</complexType>
					</element>
					<element name="AudioCoding" minOccurs="0">
						<complexType>
							<sequence>
								<element name="Format" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
								<element name="AudioChannels" minOccurs="0">
									<complexType>
										<simpleContent>
											<extension base="nonNegativeInteger">
												<attribute name="front" type="nonNegativeInteger" use="optional"/>
												<attribute name="side" type="nonNegativeInteger" use="optional"/>
												<attribute name="rear" type="nonNegativeInteger" use="optional"/>
												<attribute name="lfe" type="nonNegativeInteger" use="optional"/>
												<attribute name="track" type="nonNegativeInteger" use="optional"/>
											</extension>
										</simpleContent>
									</complexType>
								</element>
								<element name="Sample" minOccurs="0">
									<complexType>
										<attribute name="rate" type="mpeg7v1:nonNegativeReal" use="optional"/>
										<attribute name="bitsPer" type="nonNegativeInteger" use="optional"/>
									</complexType>
								</element>
								<element name="Emphasis" minOccurs="0">
									<simpleType>
										<union>
											<simpleType>
												<restriction base="NMTOKEN">
													<enumeration value="none"/>
													<enumeration value="50over15Microseconds"/>
													<enumeration value="ccittJ17"/>
												</restriction>
											</simpleType>
											<simpleType>
												<restriction base="mpeg7v1:termReferenceType"/>
											</simpleType>
										</union>
									</simpleType>
								</element>
								<element name="Presentation" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
							</sequence>
						</complexType>
					</element>
					<element name="SceneCodingFormat" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
					<element name="GraphicsCodingFormat" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
					<element name="OtherCodingFormat" type="mpeg7v1:ControlledTermUseType" minOccurs="0"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

private MFContent			content;					// cardinality: 1
private ControlledTermUse	medium;						// cardinality: 0 - 1
private ControlledTermUse	file_format;				// cardinality: 0 - 1
private Long				file_size;					// cardinality: 0 - 1
private ControlledTermUse	system;						// cardinality: 0 - 1
private Float				bandwidth;					// cardinality: 0 - 1
private Integer				bit_rate;					// cardinality: 0 - 1
private Boolean				bit_rate_variable;			// use: optional, default: false
private Integer				bit_rate_minimum;			// use: optional
private Integer				bit_rate_average;			// use: optional
private Integer				bit_rate_maximum;			// use: optional
private Integer				target_channel_bit_rate;	// cardinality: 0 - 1
// not complete
private MFAudioCoding		audio_coding;				// cardinality: 0 - 1
private ControlledTermUse	scene_coding_format;		// cardinality: 0 - 1
private ControlledTermUse	graphics_coding_format;		// cardinality: 0 - 1
private ControlledTermUse	other_coding_format;		// cardinality: 0 - 1


public MediaFormat( MFContent content )
{
	this.content = content;
	medium = null;
	file_format = null;
	file_size = null;
	system = null;
	bandwidth = null;
	bit_rate = null;
	bit_rate_variable = null;
	bit_rate_minimum = null;
	bit_rate_average = null;
	bit_rate_maximum = null;
	target_channel_bit_rate = null;
	audio_coding = null;
	scene_coding_format = null;
	graphics_coding_format = null;
	other_coding_format = null;
}

public Element toXML( Document doc, String name )
{
Element		format_ele;
Element		file_size_ele;
Element		bandwidth_ele;
Element		rate_ele;
Element		tc_rate_ele;

	format_ele = doc.createElementNS(Namespace.MPEG7, name);
	format_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaFormatType");

	format_ele.appendChild(content.toXML(doc, "Content"));
	
	if ( medium != null )
		format_ele.appendChild(medium.toXML(doc, "Medium"));
		
	if ( file_format != null )
		format_ele.appendChild(file_format.toXML(doc, "FileFormat"));

	if ( file_size != null )
	{
		file_size_ele = doc.createElement("FileSize");
		Utils.setContent(doc, file_size_ele, file_size.toString());
		format_ele.appendChild(file_size_ele);
	}
	
	if ( system != null )
		format_ele.appendChild(system.toXML(doc, "System"));
	
	if ( bandwidth != null )
	{
		bandwidth_ele = doc.createElement("Bandwidth");
		Utils.setContent(doc, bandwidth_ele, bandwidth.toString());
		format_ele.appendChild(bandwidth_ele);
	}
	
	if (	( bit_rate != null ) ||
			( bit_rate_variable != null ) ||
			( bit_rate_minimum != null ) ||
			( bit_rate_average != null ) ||
			( bit_rate_maximum != null ) )
	{
		rate_ele = doc.createElement("BitRate");
		format_ele.appendChild(rate_ele);
		
		if ( bit_rate_variable != null )
			rate_ele.setAttribute("variable", bit_rate_variable.toString());

		if ( bit_rate_minimum != null )
			rate_ele.setAttribute("minimum", bit_rate_minimum.toString());

		if ( bit_rate_average != null )
			rate_ele.setAttribute("average", bit_rate_average.toString());

		if ( bit_rate_maximum != null )
			rate_ele.setAttribute("maximum", bit_rate_maximum.toString());

		if ( bit_rate != null )
			Utils.setContent(doc, rate_ele, bit_rate.toString());
	}

	if ( target_channel_bit_rate != null )
	{
		tc_rate_ele = doc.createElement("TargetChannelBitRate");
		Utils.setContent(doc, tc_rate_ele, target_channel_bit_rate.toString());
		format_ele.appendChild(tc_rate_ele);
	}
	
	if ( audio_coding != null )
		format_ele.appendChild(audio_coding.toXML(doc, "AudioCoding"));

	if ( scene_coding_format != null )
		format_ele.appendChild(scene_coding_format.toXML(doc, "SceneCodingFormat"));
		
	if ( graphics_coding_format != null )
		format_ele.appendChild(graphics_coding_format.toXML(doc, "GraphicsCodingFormat"));
		
	if ( other_coding_format != null )
		format_ele.appendChild(other_coding_format.toXML(doc, "OtherCodingFormat"));

	return(format_ele);
}

public MFAudioCoding getAudioCoding( )
{
	return(audio_coding);
}

public float getBandwidth( )
{
	return(bandwidth.floatValue());
}

public int getBitRate( )
{
	return(bit_rate.intValue());
}

public int getBitRateAverage( )
{
	return(bit_rate_average.intValue());
}

public int getBitRateMaximum( )
{
	return(bit_rate_maximum.intValue());
}

public int getBitRateMinimum( )
{
	return(bit_rate_minimum.intValue());
}

public boolean getBitRateVariable( )
{
	return(bit_rate_variable.booleanValue());
}

public MFContent getContent( )
{
	return(content);
}

public ControlledTermUse getFileFormat( )
{
	return(file_format);
}

public long getFileSize( )
{
	return(file_size.longValue());
}

public ControlledTermUse getGraphicsCodingFormat( )
{
	return(graphics_coding_format);
}

public ControlledTermUse getMedium( )
{
	return(medium);
}

public ControlledTermUse getOtherCodingFormat( )
{
	return(other_coding_format);
}

public ControlledTermUse getSceneCodingFormat( )
{
	return(scene_coding_format);
}

public ControlledTermUse getSystem( )
{
	return(system);
}

public int getTargetChannelBitRate( )
{
	return(target_channel_bit_rate.intValue());
}

public void setAudioCoding( MFAudioCoding coding )
{
	this.audio_coding = coding;
}

public void setBandwidth( float bandwidth )
{
	this.bandwidth = new Float(bandwidth);
}

public void setBitRate( int rate )
{
	this.bit_rate = new Integer(rate);
}

public void setBitRateAverage( int rate )
{
	this.bit_rate_average = new Integer(rate);
}

public void setBitRateMaximum( int rate )
{
	this.bit_rate_maximum = new Integer(rate);
}

public void setBitRateMinimum( int rate )
{
	this.bit_rate_minimum = new Integer(rate);
}

public void setBitRateVariable( boolean variable )
{
	this.bit_rate_variable = new Boolean(variable);
}

public void setContent( MFContent content )
{
	this.content = content;
}

public void setFileFormat( ControlledTermUse format )
{
	this.file_format = format;
}

public void setFileSize( long size )
{
	this.file_size = new Long(size);
}

public void setGraphicsCodingFormat( ControlledTermUse format )
{
	this.graphics_coding_format = format;
}

public void setMedium( ControlledTermUse medium )
{
	this.medium = medium;
}

public void setOtherCodingFormat( ControlledTermUse format )
{
	this.other_coding_format = format;
}

public void setSceneCodingFormat( ControlledTermUse format )
{
	this.scene_coding_format = format;
}

public void setSystem( ControlledTermUse system )
{
	this.system = system;
}

public void setTargetChannelBitRate( int rate )
{
	this.target_channel_bit_rate = new Integer(rate);
}

}


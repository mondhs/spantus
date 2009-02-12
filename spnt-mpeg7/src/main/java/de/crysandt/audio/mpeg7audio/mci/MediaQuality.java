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
public class MediaQuality implements DescriptorI
{
/*	<!-- ###################################### -->
	<!--  Definition of MediaQuality D (8.2.6)  -->
	<!-- ###################################### -->
	<!-- Definition of MediaQuality D -->
	<complexType name="MediaQualityType">
		<complexContent>
			<extension base="mpeg7v1:DType">
				<sequence>
					<element name="QualityRating" maxOccurs="unbounded">
						<complexType>
							<complexContent>
								<extension base="mpeg7v1:RatingType">
									<attribute name="type" use="required">
										<simpleType>
											<restriction base="NMTOKEN">
												<enumeration value="subjective"/>
												<enumeration value="objective"/>
											</restriction>
										</simpleType>
									</attribute>
								</extension>
							</complexContent>
						</complexType>
					</element>
					<element name="RatingSource" type="mpeg7v1:AgentType" minOccurs="0"/>
					<element name="RatingInformationLocator" type="mpeg7v1:ReferenceType" minOccurs="0" maxOccurs="unbounded"/>
					<element name="PerceptibleDefects" minOccurs="0">
						<complexType>
							<sequence>
								<element name="VisualDefects" type="mpeg7v1:ControlledTermUseType" minOccurs="0" maxOccurs="unbounded"/>
								<element name="AudioDefects" type="mpeg7v1:ControlledTermUseType" minOccurs="0" maxOccurs="unbounded"/>
							</sequence>
						</complexType>
					</element>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

public MediaQuality( )
{
}

public Element toXML( Document doc, String name )
{
Element		qual_ele;

	qual_ele = doc.createElementNS(Namespace.MPEG7, name);
	qual_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaQualityType");

	return(qual_ele);
}

}

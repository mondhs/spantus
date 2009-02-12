/*
 * Created on Jul 20, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import de.crysandt.util.*;
import de.crysandt.xml.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class CreationInformation implements DescriptionSchemeI
{
/*	<!-- ############################################## -->
	<!--  Definition of CreationInformation DS (9.2.1)  -->
	<!-- ############################################## -->
	<!-- Definition of CreationInformation DS  -->
	<complexType name="CreationInformationType">
		<complexContent>
			<extension base="mpeg7v1:DSType">
				<sequence>
					<element name="Creation" type="mpeg7v1:CreationType"/>
					<element name="Classification" type="mpeg7v1:ClassificationType" minOccurs="0"/>
					<element name="RelatedMaterial" type="mpeg7v1:RelatedMaterialType" minOccurs="0" maxOccurs="unbounded"/>
				</sequence>
			</extension>
		</complexContent>
	</complexType> */

private Creation					creation;			// cardinality: 1
private Classification				classification;		// cardinality: 0 - 1
private VectorTyped					related_materials;	// cardinality: 0 - n

public CreationInformation( Creation creation )
{
	this.creation = creation;
	this.classification = null;
	this.related_materials = new VectorTyped(RelatedMaterial.class);
}

public Element toXML( Document doc, String name )
{
Element		info_ele;
int			i;

	info_ele = doc.createElementNS(Namespace.MPEG7, name);
	info_ele.setAttributeNS(Namespace.XSI, "xsi:type", "CreationInformationType");

	info_ele.appendChild(creation.toXML(doc, "Creation"));
	
	if ( classification != null )
		info_ele.appendChild(classification.toXML(doc, "Classification"));

	for ( i = 0; i < related_materials.size(); ++i )
		info_ele.appendChild(((RelatedMaterial)related_materials.get(i)).toXML(doc, "RelatedMaterial"));

	return(info_ele);
}

public Creation getCreation( )
{
	return(creation);
}

public void setCreation( Creation creation )
{
	this.creation = creation;
}

public Classification getClassification( )
{
	return(classification);
}

public void setClassification( Classification classification )
{
	this.classification = classification;
}

/**
 * @return "VectorTyped" with elements of type "RelatedMaterial"
 */
public VectorTyped getRelatedMaterials( )
{
	return(related_materials);
}

/**
 * Inserted for testing purposes. 
 */
public static void main( String[] args )
{
CreationInformation		creation_info;

DocumentBuilderFactory	doc_factory;
DocumentBuilder			doc_builder;
Document				doc;
TransformerFactory		trans_factory;
Transformer				trans;

	try
	{
		creation_info = CreationHelper.createCreationInformation();
		
		CreationHelper.setAlbumTitle(creation_info, "The Dark Side Of The Moon");
		CreationHelper.setSongTitle(creation_info, "Time");
		CreationHelper.setArtistGroup(creation_info, "Pink Floyd");
		CreationHelper.addArtistGroupMember(creation_info,"David", "Gilmour");
		CreationHelper.addArtistGroupMember(creation_info,"Nick", "Mason");
		CreationHelper.addArtistGroupMember(creation_info,"Richard", "Wright");
		CreationHelper.addArtistGroupMember(creation_info,"Roger", "Waters");
		CreationHelper.setCreationTimePoint(creation_info, "1972");
		CreationHelper.setGenre(creation_info, ClGenre.CLASSIC_ROCK);

		doc_factory = DocumentBuilderFactory.newInstance();
		doc_factory.setNamespaceAware(true);
		doc_builder = doc_factory.newDocumentBuilder();
		doc = doc_builder.newDocument();
		doc.appendChild(creation_info.toXML(doc, "CreationInformation"));

		trans_factory = TransformerFactory.newInstance();
		trans = trans_factory.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.transform(
			new DOMSource(doc),
			new StreamResult(new File("testCI.mp7")));
	}
	catch( Exception exc )
	{
		Debug.printStackTrace(System.err, exc);
	}

	return;
}

}

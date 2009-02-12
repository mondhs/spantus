/*
 * Created on Jun 30, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import java.awt.*;

import org.w3c.dom.*;

import de.crysandt.xml.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 *
 */
public class MediaTranscodingHints implements DescriptorI
{
/*	<!-- ################################################ -->
	<!--  Definition of Media TranscodingHints D (8.2.5)  -->
	<!-- ################################################ -->
	<!-- Definition of MediaTranscodingHints D -->
	<complexType name="MediaTranscodingHintsType">
		<complexContent>
			<extension base="mpeg7v1:DType">
				<sequence>
					<element name="MotionHint" minOccurs="0">
						<complexType>
							<sequence>
								<element name="MotionRange" minOccurs="0">
									<complexType>
										<attribute name="xLeft" type="nonNegativeInteger" use="required"/>
										<attribute name="xRight" type="nonNegativeInteger" use="required"/>
										<attribute name="yDown" type="nonNegativeInteger" use="required"/>
										<attribute name="yUp" type="nonNegativeInteger" use="required"/>
									</complexType>
								</element>
							</sequence>
							<attribute name="uncompensability" type="mpeg7v1:zeroToOneType" use="optional"/>
							<attribute name="intensity" type="mpeg7v1:zeroToOneType" use="optional"/>
						</complexType>
					</element>
					<element name="ShapeHint" minOccurs="0">
						<complexType>
							<attribute name="shapeChange" type="float" use="optional"/>
							<attribute name="numOfNonTranspBlocks" type="mpeg7v1:nonNegativeReal" use="optional"/>
						</complexType>
					</element>
					<element name="CodingHints" minOccurs="0">
						<complexType>
							<attribute name="avgQuantScale" type="mpeg7v1:nonNegativeReal" use="optional"/>
							<attribute name="intraFrameDistance" type="nonNegativeInteger" use="optional"/>
							<attribute name="anchorFrameDistance" type="positiveInteger" use="optional"/>
						</complexType>
					</element>
				</sequence>
				<attribute name="difficulty" type="mpeg7v1:zeroToOneType" use="optional"/>
				<attribute name="importance" type="mpeg7v1:zeroToOneType" use="optional"/>
				<attribute name="spatialResolutionHint" type="mpeg7v1:zeroToOneType" use="optional"/>
			</extension>
		</complexContent>
	</complexType> */

private Rectangle		motion_hint_range;						// cardinality: 0 - 1
private Float			motion_hint_uncompensability;			// use: optional
private Float			motion_hint_intensity;					// use: optional
private Float			shape_hint_shape_change;				// use: optional
private Float			shape_hint_num_of_non_transp_blocks;	// use: optional
private Float			coding_hint_avg_quant_scale;			// use: optional
private Integer			coding_hint_intra_frame_distance;		// use: optional
private Integer			coding_hint_anchor_frame_distance;		// use: optional
private Float			difficulty;								// use: optional
private Float			importance;								// use: optional
private Float			spatial_resolution_hint;				// use: optional

public MediaTranscodingHints( )
{
	motion_hint_range = null;
	motion_hint_uncompensability = null;
	motion_hint_intensity = null;
	shape_hint_shape_change = null;
	shape_hint_num_of_non_transp_blocks = null;
	coding_hint_avg_quant_scale = null;
	coding_hint_intra_frame_distance = null;
	coding_hint_anchor_frame_distance = null;
	difficulty = null;
	importance = null;
	spatial_resolution_hint = null;
}

public Element toXML( Document doc, String name )
{
Element		hints_ele;
Element		motion_hint_ele;
Element		motion_hint_range_ele;
Element		shape_hint_ele;
Element		coding_hint_ele;

	hints_ele = doc.createElementNS(Namespace.MPEG7, name);
	hints_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaTranscodingHintsType");
	
	if (	( motion_hint_range != null ) ||
			( motion_hint_uncompensability != null ) ||
			( motion_hint_intensity != null )	)
	{
		motion_hint_ele = doc.createElementNS(Namespace.MPEG7, "MotionHint");
		hints_ele.appendChild(motion_hint_ele);
		if ( motion_hint_range != null )
		{
			motion_hint_range_ele = doc.createElementNS(Namespace.MPEG7, "MotionRange");
			motion_hint_ele.appendChild(motion_hint_range_ele);
			motion_hint_range_ele.setAttribute("xLeft", Integer.toString(motion_hint_range.x));
			motion_hint_range_ele.setAttribute("xRight", Integer.toString(motion_hint_range.x + motion_hint_range.width));
			motion_hint_range_ele.setAttribute("yDown", Integer.toString(motion_hint_range.y));
			motion_hint_range_ele.setAttribute("yUp", Integer.toString(motion_hint_range.y + motion_hint_range.height));
		}
		if ( motion_hint_uncompensability != null )
			motion_hint_ele.setAttribute("uncompensability", motion_hint_uncompensability.toString());
		if ( motion_hint_intensity != null )
			motion_hint_ele.setAttribute("intensity", motion_hint_intensity.toString());
	}

	if (	( shape_hint_shape_change != null ) ||
			( shape_hint_num_of_non_transp_blocks != null ) )
	{
		shape_hint_ele = doc.createElementNS(Namespace.MPEG7, "ShapeHint");
		hints_ele.appendChild(shape_hint_ele);
		if ( shape_hint_shape_change != null )
			shape_hint_ele.setAttribute("shapeChange", shape_hint_shape_change.toString());
		if ( shape_hint_num_of_non_transp_blocks != null )
			shape_hint_ele.setAttribute("numOfNonTranspBlocks", shape_hint_num_of_non_transp_blocks.toString());
	}

	if (	( coding_hint_avg_quant_scale != null ) ||
			( coding_hint_intra_frame_distance != null ) ||
			( coding_hint_anchor_frame_distance != null ) )
	{
		coding_hint_ele = doc.createElementNS(Namespace.MPEG7, "CodingHints");
		hints_ele.appendChild(coding_hint_ele);
		if ( coding_hint_avg_quant_scale != null )
			coding_hint_ele.setAttribute("avgQuantScale", coding_hint_avg_quant_scale.toString());
		if ( coding_hint_intra_frame_distance != null )
			coding_hint_ele.setAttribute("intraFrameDistance", coding_hint_intra_frame_distance.toString());
		if ( coding_hint_anchor_frame_distance != null )
			coding_hint_ele.setAttribute("anchorFrameDistance", coding_hint_anchor_frame_distance.toString());
	}

	if ( difficulty != null )
		hints_ele.setAttribute("difficulty", difficulty.toString());
	if ( importance != null )
		hints_ele.setAttribute("importance", importance.toString());
	if ( spatial_resolution_hint != null )
		hints_ele.setAttribute("spatialResolutionHint", spatial_resolution_hint.toString());
	
	return(hints_ele);
}

public int getCodingHintAnchorFrameDistance( )
{
	return(coding_hint_anchor_frame_distance.intValue());
}

public float getCodingHintAvgQuantScale( )
{
	return(coding_hint_avg_quant_scale.floatValue());
}

public int getCodingHintIntraFrameDistance( )
{
	return(coding_hint_intra_frame_distance.intValue());
}

public float getDifficulty( )
{
	return(difficulty.floatValue());
}

public float getImportance( )
{
	return(importance.floatValue());
}

public float getMotionHintIntensity( )
{
	return(motion_hint_intensity.floatValue());
}

public Rectangle getMotionHintRange( )
{
	return(motion_hint_range);
}

public float getMotionHintUncompensability( )
{
	return(motion_hint_uncompensability.floatValue());
}

public float getShapeHintNumOfNonTranspBlocks( )
{
	return(shape_hint_num_of_non_transp_blocks.floatValue());
}

public float getShapeHintShapeChange( )
{
	return(shape_hint_shape_change.floatValue());
}

public float getSpatialResolutionHint( )
{
	return(spatial_resolution_hint.floatValue());
}

public void setCodingHintAnchorFrameDistance( int distance )
{
	this.coding_hint_anchor_frame_distance = new Integer(distance);
}

public void setCodingHintAvgQuantScale( float scale )
{
	this.coding_hint_avg_quant_scale = new Float(scale);
}

public void setCodingHintIntraFrameDistance( int distance )
{
	this.coding_hint_intra_frame_distance = new Integer(distance);
}

public void setDifficulty( float difficulty )
{
	this.difficulty = new Float(difficulty);
}

public void setImportance( float importance )
{
	this.importance = new Float(importance);
}

public void setMotionHintIntensity( float intensity )
{
	this.motion_hint_intensity = new Float(intensity);
}

public void setMotionHintRange( Rectangle rectangle )
{
	this.motion_hint_range = rectangle;
}

public void setMotionHintUncompensability( float uncompensability )
{
	this.motion_hint_uncompensability = new Float(uncompensability);
}

public void setShapeHintNumOfNonTranspBlocks( float blocks )
{
	this.shape_hint_num_of_non_transp_blocks = new Float(blocks);
}

public void setShapeHintShapeChange( float change )
{
	this.shape_hint_shape_change = new Float(change);
}

public void setSpatialResolutionHint( float hint )
{
	this.spatial_resolution_hint = new Float(hint);
}

}

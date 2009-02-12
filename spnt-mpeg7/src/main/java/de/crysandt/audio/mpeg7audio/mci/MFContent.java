/*
 * Created on Oct 29, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class MFContent extends ControlledTermUse
{
/*	<element name="Content" type="mpeg7v1:ControlledTermUseType"/> */

public static final int			UNSPECIFIED = 0;
public static final int			AUDIO = 1;
public static final int			IMAGE = 2;
public static final int			SCENE_DEFINITION = 3;
public static final int			VIDEO = 4;
public static final int			AUDIOVISUAL = 5;

public static final String[]	CONTENT_TYPE_NAMES = {
		"unspecified",		"audio",
		"image",			"scene definition",
		"video",			"audiovisual"			};

private int			content_type;

public MFContent( int content_type )
{
	super("MPEG7ContentCS");
	setContentType(content_type);
}

public MFContent( )
{
	this( UNSPECIFIED );
}

public void setContentType( int content_type )
{
	if ( (content_type >= 0) && (content_type <= 5) )
	{
		this.content_type = content_type;
		getNames().clear();
		getNames().add(CONTENT_TYPE_NAMES[content_type]);
	}
}

public int getContentType( )
{
	return(this.content_type);
}

}

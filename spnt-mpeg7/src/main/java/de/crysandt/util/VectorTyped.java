/*
 * Created on Jun 30, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
*/
package de.crysandt.util;

import java.util.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
@SuppressWarnings("unchecked")
public class VectorTyped
{

ArrayList	vector;
Class		type;

public VectorTyped( Class type )
{
	this.type = type;
	vector = new ArrayList( );
}

private boolean check( Object obj )
{
Class		obj_class;

	obj_class = obj.getClass();
	while ((obj_class != null) && (!obj_class.equals(type)))
		obj_class = obj_class.getSuperclass();
		
	if ( !obj_class.equals(type) )
	{
		System.err.println("Type mismatch.");
		return(false);
	}
	else
		return(true);
}

public void add( Object obj )
{
	if ( check(obj) )
		vector.add(obj);
}

public void clear( )
{
	vector.clear();
}

public Object get( int index )
{
	return(vector.get(index));
}

public int size( )
{
	return(vector.size());
}

public boolean isEmpty( )
{
	return(vector.isEmpty());
}

}

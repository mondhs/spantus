/*
 * Created on Oct 22, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.*;

import de.crysandt.xml.*;
import de.crysandt.util.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class MediaAgent
{
/*	<!-- Definition of MediaAgent datatype  -->
	<complexType name="MediaAgentType">
		<sequence>
			<element name="Role" type="mpeg7v1:ControlledTermUseType"/>
			<choice>
				<element name="Agent" type="mpeg7v1:AgentType"/>
				<element name="AgentRef" type="mpeg7v1:ReferenceType"/>
			</choice>
		</sequence>
	</complexType>*/

private VectorTyped				roles;
private VectorTyped				agents;

public MediaAgent( )
{
	this.roles = new VectorTyped(ControlledTermUse.class);
	this.agents = new VectorTyped(Agent.class);
}

public Element toXML( Document doc, String name )
{
Element		media_agent_ele;
int			size;
int			i;

	media_agent_ele = doc.createElementNS(Namespace.MPEG7, name);
	media_agent_ele.setAttributeNS(Namespace.XSI, "xsi:type", "MediaAgentType");

	if (roles.size() > agents.size())
		size = roles.size();
	else
		size = agents.size();
	for (i = 0; i < size; ++i)
	{
		if (roles.size() > i)
			media_agent_ele.appendChild(((ControlledTermUse)roles.get(i)).toXML(doc, "Role"));
		if (agents.size() > i)
			media_agent_ele.appendChild(((Agent)agents.get(i)).toXML(doc, "Agent"));
	}

	return(media_agent_ele);
}

/**
 * @return "VectorTyped" with elements of type "ControlledTermUse"
 */
public VectorTyped getRoles( )
{
	return(this.roles);
}

/**
 * @return "VectorTyped" with elements of type "Agent"
 */
public VectorTyped getAgents( )
{
	return(this.agents);
}

}

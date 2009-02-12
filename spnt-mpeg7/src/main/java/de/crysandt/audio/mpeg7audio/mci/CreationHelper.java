/*
 * Created on Oct 22, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import de.crysandt.util.*;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class CreationHelper
{
/* public helper functions */

public static CreationInformation createCreationInformation( )
{
	return(new CreationInformation(new Creation()));
}

/**
 * Sets the song title.<br>
 * Equivalent to id3tag song title.<br>
 * XPath: ./Creation/Title[@type='songTitle']
 * @param creation_info		CreationInformation node
 * @param song_title		song title
 */
public static void setSongTitle(
	CreationInformation creation_info,
	String song_title )
{
	getSongTitle(creation_info).setTitle(song_title);
}

/**
 * Sets the album title.<br>
 * Equivalent to id3tag album title.<br>
 * XPath: ./Creation/Title[@type='albumTitle']
 * @param creation_info		CreationInformation node
 * @param album_title		album title
 */
public static void setAlbumTitle(
	CreationInformation creation_info,
	String album_title )
{
	getAlbumTitle(creation_info).setTitle(album_title);
}

/**
 * Sets the name of the artist, if it is a person.<br>
 * Equivalent to id3tag artist.<br>
 * XPath: ./Creation/Creator/Agent[@xsi:type='PersonType']/Name
 * @param creation_info		CreationInformation node
 * @param given_name		given name - XPath: ./GivenName
 * @param family_name		family name (optional, can be "null") - XPath: ./FamilyName
 */
public static void setArtistPerson(
	CreationInformation creation_info,
	String given_name,
	String family_name )
{
PersonName		person_name;

	person_name = getArtistPersonName(creation_info);
	person_name.setGivenName(given_name);
	person_name.setFamilyName(family_name);
}

/**
 * Sets the name of the artist, if it is a group.<br>
 * Equivalent to id3tag artist.<br>
 * XPath: ./Creation/Creator/Agent[@xsi:type='PersonGroupType']/Name
 * @param creation_info		CreationInformation node
 * @param group_name		group name
 */
public static void setArtistGroup(
	CreationInformation creation_info,
	String group_name )
{
PersonGroup		person_group;

	person_group = getArtistGroup(creation_info);
	person_group.getNames().clear();
	person_group.getNames().add(group_name);
}

/**
 * Clears all members of the artist group.<br>
 * XPath: ./Creation/Creator/Agent[@xsi:type='PersonGroupType']/Member
 * @param creation_info		CreationInformation node
 */
public static void clearArtistGroupMembers(
	CreationInformation creation_info )
{
PersonGroup		person_group;

	person_group = getArtistGroup(creation_info);
	person_group.getMembers().clear();
}

/**
 * Adds a member to the artist group.<br>
 * XPath: ./Creation/Creator/Agent[xsi:type='PersonGroupType']/Member
 * @param creation_info		CreationInformation node
 * @param given_name		given name - XPath: ./Name/GivenName
 * @param family_name		family name (optional, cann be "null") - XPath: ./Name/FamilyName
 */
public static void addArtistGroupMember(
	CreationInformation creation_info,
	String given_name,
	String family_name )
{
PersonGroup		person_group;

	person_group = getArtistGroup(creation_info);
	person_group.getMembers().add(new Person(new PersonName(given_name, family_name)));
}

/**
 * Sets the music genre.<br>  
 * Equivalent to id3tag genre.<br>
 * XPath: ./Classification/Genre@href
 * @param creation_info		CreationInformation node
 * @param genre				genre (see class "ClGenre" for a list of available genres)
 */
public static void setGenre(
	CreationInformation creation_info,
	int genre )
{
	getGenre(creation_info).setGenre(genre);
}

/**
 * Sets the date of creation.
 * Can be used to represent id3tag year.<br>
 * XPath: ./Creation/CreationCoordinates/Date/TimePoint
 * @param creation_info		CreationInformation node
 * @param time				creation date
 */
public static void setCreationTimePoint(
	CreationInformation creation_info,
	String time )
{
	getCreationTimePoint(creation_info).setTimePoint(time);
}

/* private helper functions which create the structure */

private static Creation getCreation(
	CreationInformation creation_info )
{
	if (creation_info.getCreation() == null)
		creation_info.setCreation(new Creation());
	return((Creation)creation_info.getCreation());
}

private static Title getSongTitle(
	CreationInformation creation_info )
{
	return(getTitle(creation_info, "songTitle"));
}

private static Title getAlbumTitle(
	CreationInformation creation_info )
{
	return(getTitle(creation_info, "albumTitle"));
}

private static Title getTitle(
	CreationInformation creation_info,
	String title_type )
{
VectorTyped		titles;
Title			title;
int				i;

	titles = getCreation(creation_info).getTitles();
	i = 0;
	while ((i < titles.size()) && (!((Title)titles.get(i)).getType().equals(title_type)))
		++i;

	if (i == titles.size())
	{
		title = new Title("", title_type);
		titles.add(title);
	}

	return((Title)titles.get(i));
}

private static Creator getCreator(
	CreationInformation creation_info )
{
Creation	creation;

	creation = getCreation(creation_info);
	if (creation.getCreators().size() == 0)
		creation.getCreators().add(new Creator());
	return((Creator)creation.getCreators().get(0));
}

private static Person getArtistPerson(
	CreationInformation creation_info )
{
Creator		creator;
Person		person;

	creator = getCreator(creation_info);
	if (creator.getAgents().size() == 0)
	{
		person = new Person(new PersonName(""));
		creator.getAgents().add(person);
	}
	else
		if (!creator.getAgents().get(0).getClass().equals(Person.class))
		{
			creator.getAgents().clear();
			person = new Person(new PersonName(""));
			creator.getAgents().add(person);
		}
	if (creator.getRoles().size() == 0)
		creator.getRoles().add(new ControlledTermUse("urn:mpeg:mpeg7:RoleCS:2001:PERFORMER"));
	return((Person)creator.getAgents().get(0));
}

private static PersonName getArtistPersonName(
	CreationInformation creation_info )
{
Person		person;

	person = getArtistPerson(creation_info);
	if (person.getNames().size() == 0)
		person.getNames().add(new PersonName(""));
	return((PersonName)person.getNames().get(0));
}

private static PersonGroup getArtistGroup(
	CreationInformation creation_info )
{
Creator		creator;
PersonGroup	person_group;

	creator = getCreator(creation_info);
	if (creator.getAgents().size() == 0)
	{
		person_group = new PersonGroup("");
		creator.getAgents().add(person_group);
	}
	else
		if (!creator.getAgents().get(0).getClass().equals(PersonGroup.class))
		{
			creator.getAgents().clear();
			person_group = new PersonGroup("");
			creator.getAgents().add(person_group);
		}
	if (creator.getRoles().size() == 0)
		creator.getRoles().add(new ControlledTermUse("urn:mpeg:mpeg7:RoleCS:2001:PERFORMER"));
	return((PersonGroup)creator.getAgents().get(0));
}

private static Classification getClassification(
	CreationInformation creation_info )
{
	if (creation_info.getClassification() == null)
		creation_info.setClassification(new Classification());
	return(creation_info.getClassification());
}

private static ClGenre getGenre(
	CreationInformation creation_info )
{
Classification		classification;

	classification = getClassification(creation_info);
	if (classification.getGenres().size() == 0)
		classification.getGenres().add(new ClGenre(ClGenre.OTHER));
	return((ClGenre)classification.getGenres().get(0));
}

private static CrCreationCoordinate getCreationCoordinate(
	CreationInformation creation_info )
{
Creation			creation;

	creation = getCreation(creation_info);
	if (creation.getCoordinates().size() == 0)
		creation.getCoordinates().add(new CrCreationCoordinate());
	return((CrCreationCoordinate)creation.getCoordinates().get(0));
}

private static Time getCreationTime(
	CreationInformation creation_info )
{
CrCreationCoordinate	coord;

	coord = getCreationCoordinate(creation_info);
	if (coord.getDates().size() == 0)
		coord.getDates().add(new Time(new TimePoint("")));
	return((Time)coord.getDates().get(0));
}

private static TimePoint getCreationTimePoint(
	CreationInformation creation_info )
{
Time			time;

	time = getCreationTime(creation_info);
	if (time.getTimePoint() == null)
		time.setTimePoint(new TimePoint(""));
	return(time.getTimePoint());
}

}

/*
 * Created on Oct 27, 2004
 *
 * This file is part of the MPEG7AudioEnc project.
 */
package de.crysandt.audio.mpeg7audio.mci;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.crysandt.xml.Namespace;

/**
 * @author Michael.Lambertz@rwth-aachen.de
 */
public class ClGenre extends ControlledTermUse
{
/*	<element name="Genre" minOccurs="0" maxOccurs="unbounded">
		<complexType>
			<complexContent>
				<extension base="mpeg7v1:ControlledTermUseType">
					<attribute name="type" use="optional" default="main">
						<simpleType>
							<restriction base="NMTOKEN">
								<enumeration value="main"/>
								<enumeration value="secondary"/>
							</restriction>
						</simpleType>
					</attribute>
				</extension>
			</complexContent>
		</complexType>
	</element> */

/* according to "http://www.id3.org/id3v2.4.0-frames.txt" - Appendix A: */
public static final int			BLUES = 0;
public static final int			CLASSIC_ROCK = 1;
public static final int			COUNTRY = 2;
public static final int			DANCE = 3;
public static final int			DISCO = 4;
public static final int			FUNK = 5;
public static final int			GRUNGE = 6;
public static final int			HIP_HOP = 7;
public static final int			JAZZ = 8;
public static final int			METAL = 9;
public static final int			NEW_AGE = 10;
public static final int			OLDIES = 11;
public static final int			OTHER = 12;
public static final int			POP = 13;
public static final int			RNB = 14;
public static final int			RAP = 15;
public static final int			REGGAE = 16;
public static final int			ROCK = 17;
public static final int			TECHNO = 18;
public static final int			INDUSTRIAL = 19;
public static final int			ALTERNATIVE = 20;
public static final int			SKA = 21;
public static final int			DEATH_METAL = 22;
public static final int			PRANKS = 23;
public static final int			SOUNDTRACK = 24;
public static final int			EURO_TECHNO = 25;
public static final int			AMBIENT = 26;
public static final int			TRIP_HOP = 27;
public static final int			VOCAL = 28;
public static final int			JAZZ_N_FUNK = 29;
public static final int			FUSION = 30;
public static final int			TRANCE = 31;
public static final int			CLASSICAL = 32;
public static final int			INSTRUMENTAL = 33;
public static final int			ACID = 34;
public static final int			HOUSE = 35;
public static final int			GAME = 36;
public static final int			SOUND_CLIP = 37;
public static final int			GOSPEL = 38;
public static final int			NOISE = 39;
public static final int			ALTERNATIVE_ROCK = 40;
public static final int			BASS = 41;
public static final int			SOUL = 42;
public static final int			PUNK = 43;
public static final int			SPACE = 44;
public static final int			MEDITATIVE = 45;
public static final int			INSTRUMENTAL_POP = 46;
public static final int			INSTRUMENTAL_ROCK = 47;
public static final int			ETHNIC = 48;
public static final int			GOTHIC = 49;
public static final int			DARKWAVE = 50;
public static final int			TECHNO_INDUSTRIAL = 51;
public static final int			ELECTRONIC = 52;
public static final int			POP_FOLK = 53;
public static final int			EURODANCE = 54;
public static final int			DREAM = 55;
public static final int			SOUTHERN_ROCK = 56;
public static final int			COMEDY = 57;
public static final int			CULT = 58;
public static final int			GANGSTA = 59;
public static final int			TOP_40 = 60;
public static final int			CHRISTIAN_RAP = 61;
public static final int			POP_FUNK = 62;
public static final int			JUNGLE = 63;
public static final int			NATIVE_AMERICAN = 64;
public static final int			CABARET = 65;
public static final int			NEW_WAVE = 66;
public static final int			PSYCHADELIC = 67;
public static final int			RAVE = 68;
public static final int			SHOWTUNES = 69;
public static final int			TRAILER = 70;
public static final int			LO_FI = 71;
public static final int			TRIBAL = 72;
public static final int			ACID_PUNK = 73;
public static final int			ACID_JAZZ = 74;
public static final int			POLKA = 75;
public static final int			RETRO = 76;
public static final int			MUSICAL = 77;
public static final int			ROCK_N_ROLL = 78;
public static final int			HARD_ROCK = 79;

public static final String[]	GENRE_NAMES = {
	"Blues",			"Classic Rock",		"Country",			"Dance",
	"Disco",			"Funk",				"Grunge",			"Hip-Hop",
	"Jazz",				"Metal",			"New Age",			"Oldies",
	"Other",			"Pop",				"R&B",				"Rap",
	"Reggae",			"Rock",				"Techno",			"Industrial",
	"Alternative",		"Ska",				"Death Metal",		"Pranks",
	"Soundtrack",		"Euro-Techno",		"Ambient",			"Trip-Hop",
	"Vocal",			"Jazz+Funk",		"Fusion",			"Trance",
	"Classical",		"Instrumental",		"Acid",				"House",
	"Game",				"Sound Clip",		"Gospel",			"Noise",
	"AlternRock",		"Bass",				"Soul",				"Punk",
	"Space",			"Meditative",		"Instrumental Pop",	"Instrumental Rock",
	"Ethnic",			"Gothic",			"Darkwave",			"Techno-Industrial",
	"Electronic",		"Pop-Folk",			"Eurodance",		"Dream",
	"Southern Rock",	"Comedy",			"Cult",				"Gangsta",
	"Top 40",			"Christian Rap",	"Pop/Funk",			"Jungle",
	"Native American",	"Cabaret",			"New Wave",			"Psychadelic",
	"Rave",				"Showtunes",		"Trailer",			"Lo-Fi",
	"Tribal",			"Acid Punk",		"Acid Jazz",		"Polka",
	"Retro",			"Musical",			"Rock & Roll",		"Hard Rock"				};

private int						genre;
private String					type;

public ClGenre( int genre )
{
	super("urn:id3:cs:ID3genreCS:v1:" + Integer.toString(genre));
	setGenre(genre);
	setType(null);
}

public ClGenre( int genre, String type )
{
	super("urn:id3:cs:ID3genreCS:v1:" + Integer.toString(genre));
	setGenre(genre);
	setType(type);
}

public Element toXML( Document doc, String name )
{
Element				genre_ele;

	genre_ele = super.toXML(doc, name);
	genre_ele.removeAttributeNS(Namespace.XSI, "type");

	if (type != null)
		genre_ele.setAttribute("type", type);
	
	return(genre_ele);
}

public int getGenre( )
{
	return(this.genre);
}

public void setGenre( int genre )
{
	if ( (genre >= 0) && (genre <= 79) )
	{
		this.genre = genre;
		setHref("urn:id3:cs:ID3genreCS:v1:" + Integer.toString(genre));
		getNames().clear();
		getNames().add(GENRE_NAMES[genre]);
	}
}

public String getType( )
{
	return(this.type);
}

public void setType( String type )
{
	if ( (type == null) || (type.equals("main")) || (type.equals("secondary")) )
		this.type = type;
}

}

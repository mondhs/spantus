/*
  Copyright (c) 2002-2004, Holger Crysandt

  This file is part of MPEG7AudioEnc.
*/

package de.crysandt.xml;

/**
 * Small collection of namespaces often used with xml (especially with MPEG-7).
 *
 * @author  <a href="mailto:crysandt@ient.rwth-aachen.de">Holger Crysandt</a>
 * @version 1.0
 */
public interface Namespace {
    /** http://www.w3.org/2001/XMLSchema */
  public static final String XS = "http://www.w3.org/2001/XMLSchema";

    /** http://www.w3.org/2001/XMLSchema-instance */
  public static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";

    /** http://www.w3.org/XML/1998/namespace */
  public static final String XML = "http://www.w3.org/XML/1998/namespace";

  /** http://www.w3.org/2000/xmlns/ */
  public static final String XMLNS = "http://www.w3.org/2000/xmlns/";

  /** urn:mpeg:mpeg7:schema:2001 */
  public static final String MPEG7 = "urn:mpeg:mpeg7:schema:2001";

  /** http://mpeg7audioenc.sf.net/mpeg7audioenc.xsd */
  public static final String MPEG7AE = "http://mpeg7audioenc.sf.net/mpeg7audioenc.xsd";
  
  /** de:crysandt:mpeg7 */
  public static final String MPEG7HC = "de:crysandt:mpeg7";

  /** http://www.xmldb.org/xupdate */
  public static final String XUPDATE = "http://www.xmldb.org/xupdate";
}

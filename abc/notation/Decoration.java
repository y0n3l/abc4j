// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
package abc.notation;

/** This class defines decorations. */
public class Decoration implements MusicElement
{

  public static final byte UNKNOWN = 0;

  /** The  type. Ex:  */
  public static final byte STACCATO = 1;
  public static final char[] STACCATO_CHARS = {'\uF02E'};

  /** The  type. Ex:  */
  public static final byte ROLL = 2;
  // this character is for a left bracket - must rotate 90CW for display
  public static final char[] ROLL_CHARS = {'\uF028'};

  /** The  type. Ex:  */
  public static final byte UPBOW = 3;
  public static final char[] UPBOW_CHARS = {'\uF076'};

  /** The  type. Ex:  */
  public static final byte DOWNBOW = 4;
  public static final char[] DOWNBOW_CHARS = {'\uF0B3'};
  /** The  type. Ex:  */
  public static final byte TRILL = 5;
  public static final char[] TRILL_CHARS = {'\uF0D9'};

  /** The  type. Ex:  */
  public static final byte FERMATA = 6;
  public static final char[] FERMATA_CHARS = {'\uF055'};

  /** The  type. Ex:  */
  public static final byte ACCENT = 7;
  public static final char[] ACCENT_CHARS = {'\uF04B'};

  /** The  type. Ex:  */
  public static final byte LOWERMORDENT = 8;
  public static char[] LOWERMORDENT_CHARS = {'\uF06D'};

  /** The  type. Ex:  */
  public static final byte UPPERMORDENT = 9;
  public static char[] UPPERMORDENT_CHARS = {'\uF06D'};

  /** The  type. Ex:  */
  public static final byte SEGNO = 10;
  public static char[] SEGNO_CHARS = {'\uF025'};

  /** The  type. Ex:  */
  public static final byte CODA = 11;
  public static char[] CODA_CHARS = {'\uF0DE'};

  /** The type of this Decoration. */
  private byte m_type = UNKNOWN;


  /** Default constructor.
   * Constructs a simple bar line. */
  public Decoration()
  {}

  /** Creates a new bar line with the corresponding type.
   * @param type The type of bar line to be created : <TT>SIMPLE</TT>,
   * <TT>REPEAT_OPEN</TT> or <TT>REPEAT_CLOSE</TT>. */
  public Decoration(byte type)
  { m_type = type; }

  /** Returns the type of this bar line.
   * @return The type of this bar line. */
  public byte getType()
  { return m_type; }



  /** Checks if the decoration's type is the same as the give type.
   * @return true if decoration is of type
   */
  public boolean isType (byte type) {
      return ((m_type == type) ? true : false);
  }


  /** Checks if this decoration is a dynamic.
   * mezzo forte, crescendo, etc.
   * @return true if decoration is dynamic
   */
  public boolean isDynamic (byte decoration) {
      boolean is = false;
/* TJM : todo: implement!! */
      switch (decoration) {
		  default : break;
	  }
      return (is);
	}


  /** Converts the specified string to a Decoration type.
   * @param str The string to be converted as a decoration.
   * Possible values are:
   * <pre>
   * .		staccato
   * ~		general gracing (abc v1.6 and older)
   * ~		irish roll (abc v2.0)
   * u		upbow
   * v		downbow
   * T       trill
   * H       fermata
   * L       accent or emphasis
   * M       lowermordent
   * P       uppermordent
   * S       segno
   * O       coda
   * </pre>
   * @return The decoration type corresponding to the given string.
   * <TT>null</TT> is returned if no type matches the string. */
  public static byte convertToType(String str)
  {
      byte type = UNKNOWN;

      if (str.equals(".")) type = STACCATO;
      else if (str.equals("~")) type = ROLL;
      else if (str.equals("u")) type = UPBOW;
      else if (str.equals("v")) type = DOWNBOW;
      else if (str.equals("T")) type = TRILL;
      else if (str.equals("H")) type = FERMATA;
      else if (str.equals("L")) type = ACCENT;
      else if (str.equals("M")) type = LOWERMORDENT;
      else if (str.equals("P")) type = UPPERMORDENT;
      else if (str.equals("S")) type = SEGNO;
      else if (str.equals("O")) type = CODA;

/* TJM */
// add 'roll' 'trill' 'slide'

      return type;

    }

  /** Returns the characters of this object.
   * @return A character array representation of this object. */
  public char [] getChars()
  {

	if (m_type == STACCATO) return (STACCATO_CHARS);
    else if (m_type == UPBOW) return (UPBOW_CHARS);
    else if (m_type == DOWNBOW) return (DOWNBOW_CHARS);
    else if (m_type == ROLL) return (ROLL_CHARS);
    else if (m_type == TRILL) return (TRILL_CHARS);
    else if (m_type == FERMATA) return (FERMATA_CHARS);
    else if (m_type == ACCENT) return (ACCENT_CHARS);
    else if (m_type == LOWERMORDENT) return (LOWERMORDENT_CHARS);
    else if (m_type == UPPERMORDENT) return (UPPERMORDENT_CHARS);
    else if (m_type == SEGNO) return (SEGNO_CHARS);
    else if (m_type == CODA) return (CODA_CHARS);

    return "".toCharArray();
  }

  /** Returns a string representation of this object.
   * @return A string representation of this object. */
  public String toString()
  {

	if (m_type == STACCATO) return (".");
    else if (m_type == ROLL) return ("~");
    else if (m_type == UPBOW) return ("u");
    else if (m_type == DOWNBOW) return ("v");
    else if (m_type == TRILL) return ("T");
    else if (m_type == FERMATA) return ("H");
    else if (m_type == ACCENT) return ("L");
    else if (m_type == LOWERMORDENT) return ("M");
    else if (m_type == UPPERMORDENT) return ("P");
    else if (m_type == SEGNO) return ("S");
    else if (m_type == CODA) return ("O");

    return "";
  }
}

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

/** Constants for accidental types. */
public abstract class AccidentalType
{
  /** The <TT>FLAT</TT> accidental type : b. */
  public static final byte FLAT	= -1;
  /** The <TT>NATURAL</TT> accidental type. */
  public static final byte NATURAL = 0;
  /** The <TT>SHARP</TT> accidental type : # */
  public static final byte SHARP = 1;
  /** The <TT>NONE</TT> accidental type. */
  public static final byte NONE = 10;
  /** The <TT>DOUBLE SHARP</TT> accidental type : x */
  public static final byte DOUBLE_SHARP = 2;
  /** The <TT>DOUBLE FLAT</TT> ccidental type : bb */
  public static final byte DOUBLE_FLAT = -2;
  
  //TODO half-sharp, half-flat
  

  /** Convert a string to an accidental.
   * Understand ABC ^ and _, chord names #, b and unicode char */
  public static byte convertToAccidentalType(String accidental) throws IllegalArgumentException
  {
	  //TODO double and half flat and sharp
    if (accidental==null) return NONE;
    else if (accidental.equals("^")
    		|| accidental.equals("#")
    		|| accidental.equals("\u266F")) return SHARP;
    else if (accidental.equals("_")
    		|| accidental.equals("b")
    		|| accidental.equals("\u266D")) return FLAT;
    else if (accidental.equals("=")
    		|| accidental.equals("\u266E")) return NATURAL;
    else if (accidental.equals("^^")
    		|| accidental.equals("##")
    		|| accidental.equals("\u266F\u266F")) return DOUBLE_SHARP;
    else if (accidental.equals("__")
    		|| accidental.equals("bb")
    		|| accidental.equals("\u266D\u266D")) return DOUBLE_FLAT;
    else if (accidental.equals("^/")) return SHARP; //half sharp
    else if (accidental.equals("_/")) return FLAT; //half flat
    else throw new IllegalArgumentException(accidental + " is not a valid accidental");
  }
}
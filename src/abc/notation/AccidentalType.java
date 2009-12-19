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
public interface AccidentalType
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
}
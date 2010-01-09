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

/**
 * This class contains constants for clef
 */
public class Clef implements MusicElement {
	
	/** The bass clef (F) */
	public static final Clef F = new Clef("F", 0);
	/** The standard G clef */
	public static final Clef G = new Clef("G", 0);
	/** The standard C clef */
	public static final Clef C = new Clef("C", 0);
	/** the drum clef || */
	public static final Clef DRUM = new Clef("Drum", 0);
	/** G clef one octave up */
	public static final Clef G_8va = new Clef("G", 12);
	/** G clef two octaves up */
	public static final Clef G_15va = new Clef("G", 24);
	/** G clef one octave down */
	public static final Clef G_8vb = new Clef("G", -12);
	/** G clef two octaves up */
	public static final Clef G_15vb = new Clef("G", -24);
	/** F clef one octave up */
	public static final Clef F_8va = new Clef("F", 12);
	/** F clef two octaves up */
	public static final Clef F_15va = new Clef("F", 24);
	/** F clef one octave down */
	public static final Clef F_8vb = new Clef("F", -12);
	/** F clef two octaves down */
	public static final Clef F_15vb = new Clef("F", -24);
	/** C clef one octave up */
	public static final Clef C_8va = new Clef("C", 12);
	/** C clef two octaves up */
	public static final Clef C_15va = new Clef("C", 24);
	/** C clef one octave down */
	public static final Clef C_8vb = new Clef("C", -12);
	/** C clef two octaves down */
	public static final Clef C_15vb = new Clef("C", -24);
	
	//TODO G first line, F third line, various C...

	/** Ottava 8va +1 octave */
	public static final Clef ottava_8va = new Clef(null, 12);
	/** Ottava 16ma +2 octave */
	public static final Clef ottava_15ma = new Clef(null, 24);
	/** Ottava 8vb -1 octave */
	public static final Clef ottava_8vb = new Clef(null, -12);
	/** Ottava 16mb -2 octave */
	public static final Clef ottava_15mb = new Clef(null, -24);
	
	private String clefName = null;
	
	private int transposition = 0;
	
	/** Creates a clef with the specified parameters.
	 * @param name The name of the clef
	 * @param transpo Transposition in semitone, +12 for *va, -12 for *vb...
	 */
	private Clef(String name, int transpo) {
		clefName = name;
		transposition = transpo;
	}
	
	public boolean equals(Clef otherClef) {
		return ((clefName!=null && clefName.equals(otherClef.getName()))
			|| ((clefName==null) && (otherClef.clefName == null)))
			&& (transposition == otherClef.transposition);
	}
	
	/** @depecrated use {@link #equals(Clef)} */
	public boolean equals(String otherClefName) {
		return clefName.equals(otherClefName);
	}

	public String getName() {
		return clefName;
	}
	
	/** Returns the transposition in semitone, +12 for *va, -12 for *vb... */
	public int getTransposition() {
		return transposition;
	}

	public String toString() {
		return getName();
	}
}

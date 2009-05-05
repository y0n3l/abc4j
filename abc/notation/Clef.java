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
	public static final Clef F = new Clef("F");
	/** The standard G clef */
	public static final Clef G = new Clef("G");
	
	//TODO G and F +/- 8, 15 (G_8vb, G_8va...), C...
	
	private String clefName = null;
	
	/** Creates a clef with the specified parameters.
	 * @param name The name of the clef
	 */
	private Clef(String name) {
		clefName = name;
	}
	
	public boolean equals(Clef otherClef) {
		return equals(otherClef.getName());
	}
	
	public boolean equals(String otherClefName) {
		return clefName.equals(otherClefName);
	}

	public String getName() {
		return clefName;
	}

	public String toString() {
		return getName();
	}
}

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

import java.io.Serializable;

/**
 * A music element "reference" is a set of 3 coordinates [part label ; index in
 * Music ; index in MultiNote]<br>
 * <br>
 * <ul>
 * <li>the part label, <TT>' '</TT> for default part
 * <li>the "horizontal" index set when added in {@link abc.notation.Tune.Music}
 * elements collection, <TT>-1</TT> if not set
 * <li>the "vertical" index for each note in a {@link abc.notation.MultiNote},
 * <TT>-1</TT> if not set.
 * </ul>
 */
public class MusicElementReference implements Serializable, Cloneable {

	private static final long serialVersionUID = 2819815880924977717L;

	private char part = ' ';

	private int x = -1;

	private int y = -1;

	protected MusicElementReference() {
		this(' ', -1, -1);
	}
	
	public MusicElementReference(char p, int _x, int _y) {
		part = p;
		x = _x;
		y = _y;
	}

	public char getPart() {
		return part;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	protected void setPart(char p) {
		this.part = p;
	}

	protected void setX(int _x) {
		this.x = _x;
	}

	protected void setY(int _y) {
		this.y = _y;
	}

	public Object clone() {
		return new MusicElementReference(part, x, y);
	}
	
	public boolean equals(Object o) {
		if (o instanceof MusicElementReference) {
			MusicElementReference msr = (MusicElementReference) o;
			return msr.getPart()==getPart()
				&& msr.getX()==getX()
				&& msr.getY()==getY();
		} else {
			return super.equals(o);
		}
	}
	
	public String toString() {
		return "["+part+";"+x+";"+y+"]";
	}
}

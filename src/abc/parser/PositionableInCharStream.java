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
package abc.parser;

import java.io.Serializable;

/**
 * Abstract class that should be extends by any object that can have a position
 * in a char stream when parsing.
 */
public abstract class PositionableInCharStream implements Cloneable,
		Serializable {

	private CharStreamPosition _charStreamPosition = null;
	
	protected PositionableInCharStream() {
		//voided
	}
	
	protected PositionableInCharStream(CharStreamPosition pos) {
		setCharStreamPosition(pos);
	}

	public Object clone() throws CloneNotSupportedException {
		Object o = super.clone();
		if (_charStreamPosition != null) {
			((PositionableInCharStream) o)._charStreamPosition
				= (CharStreamPosition) _charStreamPosition.clone();
		}
		return o;
	}
	
	/**
	 * Returns the char stream position if this object was built by a parser,
	 * else returns <code>null</code>
	 * 
	 * @deprecated use {@link #getCharStreamPosition()} to
	 * avoid conflicts with probable "getPosition()" methods in
	 * objects which extends PositionableInCharStream.
	 */
	public CharStreamPosition getPosition() {
		return getCharStreamPosition();
	}

	/**
	 * Returns the char stream position if this object was built by a parser,
	 * else returns <code>null</code>
	 */
	public CharStreamPosition getCharStreamPosition() {
		return _charStreamPosition;
	}

	/**
	 * At parsing time, set the char stream position.
	 * @param csp
	 */
	protected void setCharStreamPosition(CharStreamPosition csp) {
		_charStreamPosition = csp;
	}

}

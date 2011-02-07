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

import org.parboiled.buffers.DefaultInputBuffer;

/**
 * Extends {@link org.parboiled.buffers.DefaultInputBuffer}
 * to add {@link #getIndex(Position)} method, to catch the
 * exact position in *source* ABC string. The parser builds
 * an input buffer but adds some {@link org.parboiled.support.Chars}
 * to mark del/ins errors.
 */
public class AbcInputBuffer extends DefaultInputBuffer {

	protected AbcInputBuffer(char[] buffer) {
        super(buffer);
    }

	protected int getIndex(Position position) {
		buildNewlines();
		if ((position.line <= 0) || (position.line > newlines.length + 1))
			throw new IllegalArgumentException("Line "+ position.line + " out of bounds");
		int linestart = (position.line > 1)
				? (newlines[position.line - 2] + 1)
				: 0;
		return linestart + position.column - 1;
	}
	
}

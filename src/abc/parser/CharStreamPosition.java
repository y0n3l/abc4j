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

/** This class defines positions in a stream of characters. */
public class CharStreamPosition implements Cloneable, Serializable {

	private static final long serialVersionUID = 9202215234974140708L;

	/**
	 * The index of first char from the beginning of the stream
	 * (starting at 0).
	 */
	private int m_startIndex = 0;
	
	/**
	 * The index of last char from the beginning of the stream
	 * (starting at 0).
	 */
	private int m_endIndex = 0;

	/** The column of the character. (first char has column 1). */
	private int m_column = 1;

	/** The line of the character. (first char has line 1). */
	private int m_line = 1;

	/**
	 * Creates a new position at the specified place.
	 * 
	 * @param line
	 *            This position's line.
	 * @param column
	 *            This position's column.
	 * @param startIndex
	 *            This position's beginning offset.
	 * @param startIndex
	 *            This position's end offset.
	 */
	public CharStreamPosition(int line, int column,
			int startIndex, int endIndex) {
		m_line = line;
		m_column = column;
		m_startIndex = startIndex;
		m_endIndex = endIndex;
	}

	/**
	 * Returns a new position at the same place as this one.
	 * 
	 * @return A new position at the same place as this one.
	 */
	public Object clone() {
		return new CharStreamPosition(m_line, m_column, m_startIndex, m_endIndex);
	}

	/** Returns the first character offset.
	 * Identical to {@link #getStartIndex()} */
	public int getCharactersOffset() {
		return getStartIndex();
	}
	
	/**
	 * Returns the first character offset.
	 */
	public int getStartIndex() {
		return m_startIndex;
	}

	/**
	 * Returns the last character offset.
	 */
	public int getEndIndex() {
		return m_endIndex;
	}

	/**
	 * Returns the column of this position.
	 * 
	 * @return The column of this position.
	 */
	public int getColumn() {
		return m_column;
	}
	
	/**
	 * Returns the length (endIndex - startIndex)
	 */
	public int getLength() {
		return m_endIndex - m_startIndex;
	}

	/**
	 * Returns the line of this position.
	 * 
	 * @return The line of this position.
	 */
	public int getLine() {
		return m_line;
	}

	/**
	 * Returns a string representation this object.
	 * 
	 * @return A string representation this object.
	 */
	public String toString() {
		return "@(L:" + m_line + ", C:" + m_column + "; "
			+ m_startIndex + "->" + m_endIndex + ")";
	}
}
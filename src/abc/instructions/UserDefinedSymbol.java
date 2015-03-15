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
package abc.instructions;

import abc.notation.MusicElement;
import abc.notation.SymbolElement;

/**
 * A user-defined symbol is an association between a symbol
 * (~, letter) and a {@link abc.notation.SymbolElement}
 * (decoration, dynamic...)
 * 
 * There are two kinds of user symbol : printable and playable
 */
public abstract class UserDefinedSymbol extends MusicElement implements Cloneable {

	private static final long serialVersionUID = 487311425430335556L;

	private char m_symbol;
	
	private SymbolElement m_element;
	
	protected UserDefinedSymbol(char symbol, SymbolElement element) {
		m_symbol = symbol;
		m_element = element;
	}

	public SymbolElement getElement() {
		return m_element;
	}

	public char getSymbol() {
		return m_symbol;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

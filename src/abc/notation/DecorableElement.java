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
 * A decorable element is an element than can receive decorations and dynamics:
 * <ul>
 * <li>note and multinote
 * <li>bar line
 * <li>spacer
 * </ul>
 */
public abstract class DecorableElement implements MusicElement {

	protected Decoration[] m_decorations = null;

	protected Dynamic m_dynamic = null;

	/**
	 * Returns the decorations for this note.
	 * 
	 * @return The decorations for this note. <TT>null</TT> if this note has
	 *         no decoration.
	 * @see #hasDecorations()
	 */
	public Decoration[] getDecorations() {
		return m_decorations;
	}

	/**
	 * Returns the dynamic for this note.
	 * 
	 * @return Dynamic for this note, <TT>null</TT> if not set
	 */
	public Dynamic getDynamic() {
		return m_dynamic;
	}

	/**
	 * Test if the note has the specified type of decoration
	 * 
	 * @param decorationType
	 *            {@link Decoration#DOWNBOW}, {@link Decoration#STACCATO}...
	 * @return
	 */
	public boolean hasDecoration(byte decorationType) {
		if (hasDecorations()) {
			for (int i = 0; i < m_decorations.length; i++) {
				if (m_decorations[i].isType(decorationType))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns <TT>true</TT> if this note has decorations, <TT>false</TT>
	 * otherwise.
	 * 
	 * @return <TT>true</TT> if this note has decorations, <TT>false</TT>
	 *         otherwise.
	 */
	public boolean hasDecorations() {
		if (m_decorations != null && m_decorations.length > 0) {
			for (int i = 0; i < m_decorations.length; i++) {
				if (m_decorations[i] != null)
					return true;
			}
		}
		return false;
	}

	public boolean hasDynamic() {
		return m_dynamic != null;
	}

	public void setDecorations(Decoration[] dec) {
		m_decorations = dec;
	}

	public void setDynamic(Dynamic dyn) {
		m_dynamic = dyn;
	}

}

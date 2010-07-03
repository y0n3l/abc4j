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
public abstract class DecorableElement extends MusicElement implements
		Cloneable {

	private static final long serialVersionUID = 6909509549064348544L;

	/**
	 * The chord name.
	 */
	protected Chord m_chord = null;

	protected Decoration[] m_decorations = null;

	protected Dynamic m_dynamic = null;

	public Object clone() throws CloneNotSupportedException {
		Object o = super.clone();
		if (m_decorations != null)
			((DecorableElement) o).m_decorations = (Decoration[]) m_decorations
					.clone();
		if (m_dynamic != null)
			((DecorableElement) o).m_dynamic = (Dynamic) m_dynamic.clone();
		return o;
	}

	/**
	 * Returns the Chord object instead of its name
	 * 
	 * @return a {@link Chord} object, or <TT>null</TT>
	 */
	public Chord getChord() {
		return m_chord;
	}

	/**
	 * Returns the name of the chord.
	 * 
	 * @return The name of the chord, <TT>null</TT> if no chord has been set.
	 * @see #getChord()
	 */
	public String getChordName() {
		return m_chord != null ? m_chord.getText() : null;
	}

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

	/**
	 * Sets the chord instead of only the chord name
	 * 
	 * @param chord
	 *            e.g. new Chord("Gm6")
	 */
	public void setChord(Chord chord) {
		m_chord = chord;
	}

	/**
	 * Sets the name of the chord.
	 * 
	 * @param chordName
	 *            The name of the chord, ex: Gm6.
	 */
	public void setChordName(String chordName) {
		m_chord = new Chord(chordName);
	}

	public void setDecorations(Decoration[] dec) {
		m_decorations = dec;
	}

	public void setDynamic(Dynamic dyn) {
		m_dynamic = dyn;
	}

}

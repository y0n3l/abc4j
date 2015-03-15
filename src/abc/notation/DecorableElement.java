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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * A decorable element is an element than can receive one chord name, one
 * dynamic, multiple decorations and multiple annotations:
 * 
 * <ul>
 * <li>note and multinote
 * <li>barline
 * <li>spacer
 * </ul>
 */
public abstract class DecorableElement extends MusicElement implements
		Cloneable {

	private static final long serialVersionUID = 6909509549064348544L;

	protected Vector<Annotation> m_annotations = null;

	/**
	 * The chord name.
	 */
	private Chord m_chord = null;

	private ArrayList<Decoration> m_decorations = new ArrayList<Decoration>(0);

	private Dynamic m_dynamic = null;

	public void addAnnotation(Annotation ann) {
		if (m_annotations == null) {
			m_annotations = new Vector<Annotation>(2, 2);
		}
		m_annotations.add(ann);
	}
	
	/**
	 * Add a decoration
	 * 
	 * @param deco
	 */
	public void addDecoration(Decoration deco) {
		if (deco != null)
			getDecorations().add(deco);
	}

	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException {
		DecorableElement o = (DecorableElement) super.clone();
		if (m_annotations != null)
			o.m_annotations = (Vector<Annotation>) m_annotations
					.clone();
		for (Decoration deco : getDecorations()) {
			o.addDecoration((Decoration) deco.clone());
		}
		if (m_dynamic != null)
			o.m_dynamic = (Dynamic) m_dynamic.clone();
		return o;
	}

	/**
	 * Returns the annotations for this element.
	 * 
	 * @return The annotationss for this element. <TT>null</TT> if it has not.
	 * @see #hasAnnotations()
	 */
	public Vector<Annotation> getAnnotations() {
		return m_annotations;
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
	 * Returns the decorations for this element.
	 * 
	 * @return The decorations for this element. <TT>null</TT> if it has not.
	 * @see #hasDecorations()
	 */
	public ArrayList<Decoration> getDecorations() {
		return m_decorations;
	}

	/**
	 * Returns the dynamic for this element.
	 * 
	 * @return Dynamic for this element, <TT>null</TT> if not set
	 */
	public Dynamic getDynamic() {
		return m_dynamic;
	}

	/**
	 * Returns <TT>true</TT> if this element has annotations, <TT>false</TT>
	 * otherwise.
	 * 
	 * @return <TT>true</TT> if this element has annotations, <TT>false</TT>
	 *         otherwise.
	 */
	public boolean hasAnnotations() {
		return ((m_annotations != null) && (m_annotations.size() > 0));
	}

	/**
	 * Test if the element has the specified type of decoration
	 * 
	 * @param decorationType
	 *            {@link Decoration#DOWNBOW}, {@link Decoration#STACCATO}...
	 */
	public boolean hasDecoration(byte decorationType) {
		if (hasDecorations()) {
			for (Decoration deco : getDecorations()) {
				if (deco.isType(decorationType))
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns <TT>true</TT> if this element has decorations, <TT>false</TT>
	 * otherwise.
	 * 
	 * @return <TT>true</TT> if this element has decorations, <TT>false</TT>
	 *         otherwise.
	 */
	public boolean hasDecorations() {
		return getDecorations().size() > 0;
	}

	public boolean hasDynamic() {
		return m_dynamic != null;
	}

	/**
	 * Removes annotation(s) having the given identifier
	 * @param annotIdentifier
	 */
	protected void removeAnnotation(String annotIdentifier) {
		if (m_annotations != null) {
			Iterator<Annotation> it = m_annotations.iterator();
			while (it.hasNext()) {
				Annotation ann = (Annotation) it.next();
				if (ann.getIdentifier().equals(annotIdentifier))
					it.remove();
			}
		}
	}

	public void setAnnotations(Vector<Annotation> ann) {
		m_annotations = ann;
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

	@Deprecated
	public void setDecorations(Decoration[] dec) {
		for (int i = 0; i < dec.length; i++) {
			addDecoration(dec[i]);
		}
	}

	public void setDynamic(Dynamic dyn) {
		m_dynamic = dyn;
	}

}

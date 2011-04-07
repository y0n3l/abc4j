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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import abc.parser.CharStreamPosition;
import abc.parser.PositionableInCharStream;

/**
 * A Music is a collection of {@link abc.notation.Voice}s
 * containing {@link MusicElement music elements} (notes,
 * bars...), for a {@link abc.notation.Part} of a
 * {@link abc.notation.Tune} (part "A", "B",... or at least
 * the default part).
 */
public class Music implements Cloneable, Serializable {

	private static final long serialVersionUID = 5411161761359626571L;

	protected transient NoteAbstract lastNote = null;

	private TreeMap m_bars = new TreeMap();

	private short m_firstBarNumber = 1;
	
	private Vector m_voices = new Vector(2, 1);
	
	private String m_partLabel = " ";

	/** Collection of Instruction object (Xcommand, user defined symbols),
	 * transmitted from the Tune object in {@link Tune#newMusic()} */
	private ArrayList m_instructions = null;

	public Music() {
		this((short) 1);
	}
	
	public Music(short firstBarNo) {
		super();
		m_firstBarNumber = firstBarNo;
		m_bars.put(new Short((short) m_firstBarNumber), new Bar(
				(short) m_firstBarNumber, 0));
	}
	
	protected void setPartLabel(String c) {
		m_partLabel = c;
		Iterator it = m_voices.iterator();
		while (it.hasNext()) {
			Voice v = (Voice) it.next();
			v.setPartLabel(m_partLabel);
		}
	}
	
	protected void setGlobalInstructions(ArrayList al) {
		m_instructions = al;
	}
	
	public ArrayList getGlobalInstructions() {
		return m_instructions;
	}

	/**
	 * Returns a Collection of {@link Voice}.
	 */
	public Collection getVoices() {
		return m_voices;
	}
	
	/**
	 * Concatene the given music object to current one
	 * @param music
	 */
	public void append(Music music) {
		//if appending a music from a part
		//add a PartLabel to all voices
		if (!music.m_partLabel.equals(" ")) {
			Iterator it = m_voices.iterator();
			while (it.hasNext()) {
				((Voice)it.next()).addElement(new PartLabel(music.m_partLabel));
			}
		}
		Iterator it = music.getVoices().iterator();
		while (it.hasNext()) {
			Voice v = (Voice) it.next();
			if (!voiceExists(v.getVoiceName())) {
				//create new voice (in getVoice)
				//and copy v informations
				Voice vCopy = getVoice(v.getVoiceName());
				vCopy.setTablature(v.getTablature());
				vCopy.setVolume(v.getVolume());
				vCopy.setInstrument(v.getInstrument());
				//? vCopy.setFirstBarNumber(v.getFirstBar().getBarNumber());
			}
			getVoice(v.getVoiceName()).addAll(v);
		}
	}
	
	public Voice getFirstVoice() {
		if (m_voices.size() > 0) {
			return (Voice) m_voices.firstElement();
		} else {
			return getVoice("1");
		}
	}
	
	/**
	 * Returns the asked voice, create it if needed.
	 * @param voiceName
	 */
	public Voice getVoice(String voiceName) {
		Iterator it = m_voices.iterator();
		while (it.hasNext()) {
			Voice v = (Voice) it.next();
			if (v.getVoiceName().equals(voiceName))
				return v;
		}
		Voice v = new Voice(voiceName, m_firstBarNumber);
		v.setPartLabel(m_partLabel);
		m_voices.add(v);
		return v;
	}

	/** For compatibility, return iterator on first voice
	 * @deprecated use {@link #getVoice(String)}.iterator() 
	 */
	public Iterator iterator() {
		return getFirstVoice().iterator();
	}
	
	/** @deprecated use {@link #getVoice(String)} */
	public void addElement(MusicElement element) {
		getFirstVoice().addElement(element);
	}

	/**
	 * Add an element to the specified voice.
	 * 
	 * This is just a shorten way to call
	 * <code>myMusic.getVoice(voiceNumber).addElement(element)</code>
	 * @param voiceName
	 * @param element
	 */
	public void addElement(String voiceName, MusicElement element) {
		getVoice(voiceName).addElement(element);
	}
	
	/**
	 * Maybe could be used...? add an element to all voices
	 * be sure to add it first in the voice, are last when
	 * all elements have been added...
	 * @param element
	 */
	public void addToAllVoices(MusicElement element) {
		Iterator it = m_voices.iterator();
		while (it.hasNext()) {
			Voice v = (Voice) it.next();
			v.addElement(element);
		}
	}

	/**
	 * Return true if the bar is empty or contains only barline and spacer(s).
	 * False if barline contain other kind of music element
	 * 
	 * @param bar
	 */
	public boolean barIsEmptyForAllVoices(Bar bar) {
		Iterator it = m_voices.iterator();
		while (it.hasNext()) {
			Voice v = (Voice) it.next();
			if (!v.barIsEmpty(bar))
				return false;
		}
		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		Object o = super.clone();
		((Music) o).m_bars = (TreeMap) m_bars.clone();
		((Music) o).m_voices = (Vector) m_voices.clone();
		((Music) o).m_instructions = (ArrayList) m_instructions.clone();
		return o;
	}

	/**
	 * Returns the score element location at the specified offset.
	 * 
	 * @param offset
	 *            An offset in a char stream.
	 * @return The score element location at the specified offset.
	 * @deprecated use {@link #getElementAtStreamPosition(int)}
	 * FIXME this is specific to ABC format... should be elsewhere
	 * than in Music object
	 */
	public MusicElement getElementAt(int offset) {
		return getElementAtStreamPosition(offset);
	}
	/**
	 * Returns the score element location at the specified offset.
	 * 
	 * @param offset
	 *            An offset in a char stream.
	 * @return The score element location at the specified offset.
	 * FIXME this is specific to ABC format... should be elsewhere
	 * than in Music object
	 */
	public MusicElement getElementAtStreamPosition(int offset) {
		Iterator it = m_voices.iterator();
		CharStreamPosition pos;
		while (it.hasNext()) {
			Voice v = (Voice) it.next();
			int size = v.size();
			MusicElement current = null;
			for (int i = 0; i < size; i++) {
				current = (MusicElement) v.elementAt(i);
				if (current instanceof PositionableInCharStream) {
					pos = ((PositionableInCharStream) current)
						.getCharStreamPosition();
					if (pos != null) {
						if ((pos.getStartIndex() <= offset)
								&& (pos.getEndIndex() > offset))
							return current;
					}
				}
			}
		}
		return null;
	}
	
	private boolean voiceExists(String voiceName) {
		for (Iterator it = m_voices.iterator(); it.hasNext();) {
			Voice v = (Voice) it.next();
			if (v.getVoiceName().equals(voiceName))
				return true;
		}
		return false;
	}

	/**
	 * Returns an element for the given reference, <TT>null</TT> if not found
	 * 
	 * @param ref
	 * @return
	 */
	public MusicElement getElementByReference(MusicElementReference ref) {
		if (voiceExists(ref.getVoice())) {
			Iterator it = getVoice(ref.getVoice()).iterator();
			while (it.hasNext()) {
				MusicElement element = (MusicElement) it.next();
				if (element.getReference().equals(ref))
					return element;
			}
		}
		return null;
	}

	/**
	 * Returns the highest note between two music elements. <TT>MultiNote</TT>
	 * instances are ignored.
	 * 
	 * @param elmtBegin
	 *            The music element where to start (included) the search of the
	 *            highest note.
	 * @param elmtEnd
	 *            The music element where to end (included) the search of the
	 *            highest note.
	 * @return The highest note or multinote between two music elements. <TT>null</TT>
	 *         if no note has been found between the two music elements.
	 * @throws IllegalArgumentException
	 *             Thrown if one of the music elements hasn't been found in the
	 *             music or if the <TT>elmtEnd</TT> param is located before
	 *             the <TT>elmntBegin</TT> param in the music.
	 * @deprecated use {@link #getVoice(String)}.{@link Voice#getHighestNoteBewteen(MusicElement, MusicElement) getHighest...}
	 */
	public NoteAbstract getHighestNoteBewteen(MusicElement elmtBegin,
			MusicElement elmtEnd) throws IllegalArgumentException {
		return getFirstVoice().getHighestNoteBewteen(elmtBegin, elmtEnd);
	}

	/**
	 * Returns the key signature of this tune.
	 * 
	 * @return The key signature of this tune.
	 */
	public KeySignature getKey() {
		return getFirstVoice().getKey();
	}

	/**
	 * Returns the last note that has been added to this score.
	 * 
	 * @return The last note that has been added to this score. <TT>null</TT>
	 *         if no note in this score.
	 * @deprecated use {@link #getVoice(String)}.getLastNote()
	 */
	public NoteAbstract getLastNote() {
		return getFirstVoice().getLastNote();
	}

	/**
	 * @param elmtBegin
	 *            (included)
	 * @param elmtEnd
	 *            (included)
	 * @return The lowest note or multinote between the two given score elements
	 *         if found. <TT>null</TT> if no note has been found between the
	 *         two music elements.
	 * @throws IllegalArgumentException
	 * @deprecated use {@link #getVoice(String)}.{@link Voice#getLowestNoteBewteen(MusicElement, MusicElement) getLowest...}
	 */
	public NoteAbstract getLowestNoteBewteen(MusicElement elmtBegin,
			MusicElement elmtEnd) throws IllegalArgumentException {
		return getFirstVoice().getLowestNoteBewteen(elmtBegin, elmtEnd);
	}

	/**
	 * Returns a collection of Note between begin and end included
	 * 
	 * @param elmtBegin
	 * @param elmtEnd
	 * @return a Collection of NoteAbstract (Note or MultiNote)
	 * @throws IllegalArgumentException
	 * @deprecated use {@link #getVoice(String)}.{@link Voice#getNotesBetween(MusicElement, MusicElement) getNotesBetween...}
	 */
	public Collection getNotesBetween(MusicElement elmtBegin,
			MusicElement elmtEnd) throws IllegalArgumentException {
		return getFirstVoice().getNotesBetween(elmtBegin, elmtEnd);
	}

	/**
	 * @return The shortest note in the tune.
	 */
	public Note getShortestNoteInAllVoices() throws IllegalArgumentException {
		Note shortestNote = null;
		Iterator it = m_voices.iterator();
		while (it.hasNext()) {
			Voice v = (Voice) it.next();
			Note shortInVoice = v.getShortestNote();
			if (shortInVoice != null) {
				if (shortestNote == null)
					shortestNote = shortInVoice;
				else if (shortInVoice.isShorterThan(shortestNote))
					shortestNote = shortInVoice;
			}
		}
		return shortestNote;
	}

	/**
	 * Returns <TT>true</TT> if this tune music has chord names, <TT>false</TT>
	 * otherwise.
	 * @deprecated use {@link #getVoice(String)}.hasChordNames()
	 */
	public boolean hasChordNames() {
		return getFirstVoice().hasChordNames();
	}

	private boolean hasObject(Class musicElementClass) {
		Iterator it = m_voices.iterator();
		while (it.hasNext()) {
			Voice v = (Voice) it.next();
			if (v.hasObject(musicElementClass))
				return true;
		}
		return false;
	}

	/**
	 * Returns <TT>true</TT> if this tune music has part label(s), <TT>false</TT>
	 * otherwise.
	 */
	public boolean hasPartLabel() {
		return hasObject(PartLabel.class);
	}

	/**
	 * Returns <TT>true</TT> if this tune music has tempo, <TT>false</TT>
	 * otherwise.
	 */
	public boolean hasTempo() {
		return hasObject(Tempo.class);
	}

	/** @deprecated use {@link #getVoice(String)}.indexOf() */
	public int indexOf(MusicElement elmnt) {
		return getFirstVoice().indexOf(elmnt);
	}

}

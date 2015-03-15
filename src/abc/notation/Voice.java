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
import java.util.Collection;
import java.util.TreeMap;
import java.util.Vector;

/**
 * A Voice is a vector of {@link MusicElement music elements} (notes, bars...).
 * 
 * One or several voices make the {@link abc.notation.Music}. They are
 * generally printed on separated staves, but two voices can be merged in one
 * staff.
 * 
 * Voice has a sound (instrument, volume) and some related properties.
 * 
 * Voice can also have lyrics, a {@link abc.notation.Tablature}...
 */
public class Voice extends Vector<MusicElement> implements Cloneable, Serializable {

	private static final long serialVersionUID = 8131014387452835226L;

	protected transient NoteAbstract lastNote = null;
	
	private transient PositionableInTime m_lastPosInTime = null;
	
	private transient short m_ReferenceNoteLength = Note.EIGHTH;
	
	/**
	 * For each note that will be added after setting this, the reference
	 * note length is transmitted
	 * 
	 * @param s
	 */
	public void setReferenceNoteLength(short s) {
		m_ReferenceNoteLength = s;
	}
	
	private TreeMap<Short, Bar> m_bars = new TreeMap<Short, Bar>();

	private short m_currentBar = 1;

	private short m_firstBarNumber = 1;

	private byte m_instrument = 0;

	private String m_name = "";

	private String m_partLabel = " ";

	private byte m_stemPolicy = StemPolicy.STEMS_AUTO;
	
	private String m_subname = "";
	
	private Tablature m_tablature = null;
	
	private String m_voiceId = "1";
	
	private byte m_volume = 64;
	
	public Voice(String voiceName) {
		this(voiceName, (short) 1);
	}
	
	public Voice(String voiceId, short firstBarNo) {
		super();
		m_voiceId = voiceId;
		setFirstBarNumber(firstBarNo);
		m_bars.put(new Short((short) m_firstBarNumber), new Bar(
				(short) m_firstBarNumber, 0));
	}
	
	public void addElement(MusicElement element) {
		addElement0(element);
	}
	
	private synchronized void addElement0(MusicElement me) {
  		//CD EF | CD/E/ F2 | (3CDE F2
  		if (me == null)
			System.err.println(toString() + " addElement0 null");
		else {
			if (me instanceof NoteAbstract) {
				lastNote = (NoteAbstract) me;
			} else if (me instanceof BarLine) {
				m_currentBar++;
				// BarLine barLine = (BarLine) me;
				// barLine.removeAnnotation("BAR_NUMBER");
				// barLine.addAnnotation(new Annotation("^"+currentBar,
				// "BAR_NUMBER"));
				m_bars.put(new Short(m_currentBar), new Bar(m_currentBar,
						size()));
			}
			short x = (short) size();
			MusicElementReference ref = me.getReference();
			ref.setPart(m_partLabel);
			ref.setVoice(m_voiceId);
			ref.setX(x);
			if (me instanceof MultiNote) {
				Note[] notes = ((MultiNote) me).toArray();
				if (notes != null) {
					for (int i = 0; i < notes.length; i++) {
						notes[i].getReference().setPart(m_partLabel);
						notes[i].getReference().setVoice(m_voiceId);
						notes[i].getReference().setX(x);
						// setY is defined in MultiNote constructor
					}
				}
			}
			if (me instanceof PositionableInTime) {
				PositionableInTime pit = (PositionableInTime) me;
				pit.setPreviousElement(m_lastPosInTime);
				pit.setReferenceNoteLength(m_ReferenceNoteLength);
				m_lastPosInTime = pit;
			}
			super.addElement(me);
		}
	}
	
	/**
	 * Return true if the bar is empty or contains only barline and spacer(s).
	 * False if barline contain other kind of music element
	 * 
	 * @param bar
	 */
	public boolean barIsEmpty(Bar bar) {
		for (MusicElement me : getBarContent(bar)) {
			if (!(me instanceof BarLine) && !(me instanceof Spacer))
				return false;
		}
		return true;
	}
	
	public Object clone() {
		return super.clone();
	}

	public Collection<MusicElement> getBarContent(Bar bar) {
		int from = bar.getPosInMusic();
		int to = size() - 1;
		Bar next = getNextBar(bar.getBarNumber());
		if (next != null) {
			to = next.getPosInMusic() - 1;/* exclude barline */
		}
		Collection<MusicElement> ret = new Vector<MusicElement>(to - from + 1);
		for (int i = from; i <= to; i++) {
			ret.add((MusicElement) elementData[i]);
		}
		return ret;
	}

	public Bar getFirstBar() {
		return (Bar) m_bars.get(new Short(m_firstBarNumber));
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
	 *             voice or if the <TT>elmtEnd</TT> param is located before
	 *             the <TT>elmntBegin</TT> param in the music.
	 */
	public NoteAbstract getHighestNoteBewteen(MusicElement elmtBegin,
			MusicElement elmtEnd) throws IllegalArgumentException {
		NoteAbstract highestNote = null;
		int highestNoteHeight = Note.REST;
		if (elmtBegin instanceof NoteAbstract) {
			if (!((elmtBegin instanceof Note) && ((Note) elmtBegin).isRest())) {
				highestNote = (NoteAbstract) elmtBegin;
				highestNoteHeight = (highestNote instanceof MultiNote) ? ((MultiNote) elmtBegin)
						.getHighestNote().getMidiLikeHeight()
						: ((Note) highestNote).getMidiLikeHeight();
			}
		}
		int idxBegin = indexOf(elmtBegin);
		int idxEnd = indexOf(elmtEnd);
		if (idxBegin == -1)
			throw new IllegalArgumentException("Note " + elmtBegin
					+ " hasn't been found in voice "+getVoiceId());
		if (idxEnd == -1)
			throw new IllegalArgumentException("Note " + elmtEnd
					+ " hasn't been found in voice "+getVoiceId());
		if (idxBegin > idxEnd)
			throw new IllegalArgumentException("Note " + elmtBegin
					+ " is located after " + elmtEnd + " in voice "+getVoiceId());
		MusicElement currentScoreEl;
		int currentNoteHeight;
		for (int i = idxBegin + 1; i <= idxEnd; i++) {
			currentScoreEl = (MusicElement) elementAt(i);
			if (currentScoreEl instanceof NoteAbstract) {
				currentNoteHeight = (currentScoreEl instanceof MultiNote) ? ((MultiNote) currentScoreEl)
						.getHighestNote().getMidiLikeHeight()
						: ((Note) currentScoreEl).getMidiLikeHeight();
				if ((currentNoteHeight != Note.REST)
						&& ((highestNoteHeight == Note.REST) || (currentNoteHeight > highestNoteHeight))) {
					highestNoteHeight = currentNoteHeight;
					highestNote = (NoteAbstract) currentScoreEl;
				}
			}
		}
		return highestNote;
	}

	/** Get MIDI program (instrument) */
	public byte getInstrument() {
		return m_instrument;
	}

	/**
	 * Returns the key signature of this tune.
	 * 
	 * @return The key signature of this tune.
	 */
	public KeySignature getKey() {
		for (int i = 0, j = size(); i < j; i++) {
			if (elementAt(i) instanceof KeySignature)
				return (KeySignature) elementAt(i);
		}
		return new KeySignature(Note.C, KeySignature.MAJOR);
	}

	public Bar getLastBar() {
		return (Bar) m_bars.get(m_bars.lastKey());
	}

	/**
	 * Returns the last note that has been added to this score.
	 * 
	 * @return The last note that has been added to this score. <TT>null</TT>
	 *         if no note in this score.
	 */
	public NoteAbstract getLastNote() {
		if (lastNote == null) {
			for (int i = super.size() - 1; i >= 0; i--) {
				if (super.elementAt(i) instanceof NoteAbstract) {
					lastNote = (NoteAbstract) super.elementAt(i);
					break;
				}
			}
		}
		return lastNote;
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
	 *             Thrown if one of the music elements hasn't been found in the
	 *             voice or if the <TT>elmtEnd</TT> param is located before
	 *             the <TT>elmntBegin</TT> param in the music.
	 */
	public NoteAbstract getLowestNoteBewteen(MusicElement elmtBegin,
			MusicElement elmtEnd) throws IllegalArgumentException {
		NoteAbstract lowestNote = null;
		int lowestNoteHeight = Note.REST;
		if (elmtBegin instanceof NoteAbstract) {
			if (!((elmtBegin instanceof Note) && ((Note) elmtBegin).isRest())) {
				lowestNote = (NoteAbstract) elmtBegin;
				lowestNoteHeight = (lowestNote instanceof MultiNote) ? ((MultiNote) elmtBegin)
						.getLowestNote().getMidiLikeHeight()
						: ((Note) lowestNote).getMidiLikeHeight();
			}
		}
		int idxBegin = indexOf(elmtBegin);
		int idxEnd = indexOf(elmtEnd);
		if (idxBegin == -1)
			throw new IllegalArgumentException("Note " + elmtBegin
					+ " hasn't been found in voice "+getVoiceId());
		if (idxEnd == -1)
			throw new IllegalArgumentException("Note " + elmtEnd
					+ " hasn't been found in voice "+getVoiceId());
		if (idxBegin > idxEnd)
			throw new IllegalArgumentException("Note " + elmtBegin
					+ " is located after " + elmtEnd + " in voice "+getVoiceId());
		MusicElement currentScoreEl;
		int currentNoteHeight;
		for (int i = idxBegin + 1; i <= idxEnd; i++) {
			currentScoreEl = (MusicElement) elementAt(i);
			if (currentScoreEl instanceof NoteAbstract) {
				currentNoteHeight = (currentScoreEl instanceof MultiNote) ? ((MultiNote) currentScoreEl)
						.getLowestNote().getMidiLikeHeight()
						: ((Note) currentScoreEl).getMidiLikeHeight();
				if ((currentNoteHeight != Note.REST)
						&& ((lowestNoteHeight == Note.REST) || (currentNoteHeight < lowestNoteHeight))) {
					lowestNoteHeight = currentNoteHeight;
					lowestNote = (NoteAbstract) currentScoreEl;
				}
			}
		}
		return lowestNote;

	}

	/**
	 * Returns the name which is printed on the left of first staff
	 */
	public String getName() {
		if (m_name.equals(""))
			return m_voiceId;
		else
			return m_name;
	}

	public Bar getNextBar() {
		return getNextBar(m_currentBar);
	}

	private Bar getNextBar(short barNum) {
		short nextBar = barNum;
		if (nextBar < m_firstBarNumber)
			nextBar = (short) (m_firstBarNumber - 1);
		Short s = new Short(nextBar++);
		if (m_bars.keySet().contains(s))
			return (Bar) m_bars.get(s);
		else {
			return null;
		}
	}

	/**
	 * Returns a collection of Note between begin and end included
	 * 
	 * @param elmtBegin
	 * @param elmtEnd
	 * @return a Collection of NoteAbstract (Note or MultiNote)
	 * @throws IllegalArgumentException
	 *             Thrown if one of the music elements hasn't been found in the
	 *             voice or if the <TT>elmtEnd</TT> param is located before
	 *             the <TT>elmntBegin</TT> param in the music.
	 */
	public Collection<NoteAbstract> getNotesBetween(MusicElement elmtBegin,
			MusicElement elmtEnd) throws IllegalArgumentException {
		int idxBegin = indexOf(elmtBegin);
		int idxEnd = this.indexOf(elmtEnd);
		if (idxBegin == -1)
			throw new IllegalArgumentException("Note " + elmtBegin
					+ " hasn't been found in voice "+getVoiceId());
		if (idxEnd == -1)
			throw new IllegalArgumentException("Note " + elmtEnd
					+ " hasn't been found in voice "+getVoiceId());
		if (idxBegin > idxEnd)
			throw new IllegalArgumentException("Note " + elmtBegin
					+ " is located after " + elmtEnd + " in voice "+getVoiceId());
		Collection<NoteAbstract> ret = new Vector<NoteAbstract>();
		MusicElement currentScoreEl;
		for (int i = idxBegin; i <= idxEnd; i++) {
			currentScoreEl = (MusicElement) elementAt(i);
			if (currentScoreEl instanceof NoteAbstract)
				ret.add((NoteAbstract) currentScoreEl);
		}
		return ret;
		
	}

	public Bar getPreviousBar() {
		Short s = new Short((short) (m_currentBar - 1));
		if ((m_currentBar > m_firstBarNumber) && m_bars.keySet().contains(s))
			return (Bar) m_bars.get(s);
		else {
			return null;
		}
	}

	/**
	 * @return The shortest note in the voice.
	 */
	public Note getShortestNote() throws IllegalArgumentException {
		Note shortestNote = null;
		// init
		for (MusicElement currentScoreEl : this) {
			if (currentScoreEl instanceof Note) {
				if (shortestNote == null)
					shortestNote = (Note) currentScoreEl;
				else if (((Note) currentScoreEl).isShorterThan(shortestNote))
					shortestNote = (Note) currentScoreEl;
			} else if (currentScoreEl instanceof MultiNote) {
				Note shortestInChrod = ((MultiNote) currentScoreEl)
						.getShortestNote();
				if (shortestNote == null)
					shortestNote = shortestInChrod;
				else if (shortestInChrod.isShorterThan(shortestNote))
					shortestNote = shortestInChrod;
			}
		}
		return shortestNote;

	}

	/**
	 * Returns stem policy, one of {@link StemPolicy} constants.
	 */
	public byte getStemPolicy() {
		return m_stemPolicy;
	}

	/**
	 * Returns the shorted name to print on all staves excepted
	 * the first where the whole name is printed
	 */
	public String getSubname() {
		return m_subname;
	}

	/**
	 * Returns the tablature definition attached to this voice,
	 * <code>null</code> if no tablature.
	 */
	public Tablature getTablature() {
		return m_tablature;
	}

	/** Return voice identifier V:1 returns "1", V:RH returns "RH" */
	public String getVoiceId() {
		return m_voiceId;
	}

	/** %%MIDI volume(?) xx */
	public byte getVolume() {
		return m_volume;
	}

	/**
	 * Returns <TT>true</TT> if this voice has chord names, <TT>false</TT>
	 * otherwise.
	 */
	public boolean hasChordNames() {
		for (MusicElement currentScoreEl : this) {
			if (currentScoreEl instanceof NoteAbstract) {
				if (((NoteAbstract) currentScoreEl).getChordName() != null)
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns <TT>true</TT> if this voice contains one of the requested
	 * decoration
	 */
	public boolean hasDecoration(byte[] decorations) {
		for (MusicElement currentScoreEl : this) {
			if (currentScoreEl instanceof DecorableElement) {
				for (Decoration d : ((DecorableElement) currentScoreEl)
						.getDecorations()) {
					for (int i = 0; i < decorations.length; i++) {
						if (d.isType(decorations[i]))
							return true;
					}
				}
			}
		}
		return false;
	}

	public boolean hasNextBar() {
		return getNextBar() != null;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean hasObject(Class musicElementClass) {
		for (MusicElement currentScoreEl : this) {
			if ((currentScoreEl != null)
					&& currentScoreEl.getClass().equals(musicElementClass)) {
				return true;
			}
		}
		return false;
	}

	public int indexOf(MusicElement elmnt) {
		if (elmnt != null) {
			Object elmntIt = null;
			boolean isLooking4Note = elmnt instanceof Note;
			for (int i = 0; i < size(); i++) {
				elmntIt = elementAt(i);
				if (elmntIt != null) {
					if (elementAt(i) instanceof MultiNote && isLooking4Note) {
						if (((MultiNote) elmntIt).contains((Note) elmnt))
							return i;
					} else if (elementAt(i).equals(elmnt))
						return i;
				}
			}
		}
		return -1;
	}

	public void moveToBar(Bar bar) {
		m_currentBar = bar.getBarNumber();
	}

	public void setFirstBarNumber(short s) {
		m_firstBarNumber = s;
	}

	/** %%MIDI program xx */
	public void setInstrument(byte instrument) {
		this.m_instrument = instrument;
	}

	/**
	 * Defines the name which is printed on the left of first staff
	 */
	public void setName(String name) {
		m_name = name;
	}

	protected void setPartLabel(String c) {
		m_partLabel = c;
	}

	/**
	 * Sets the stem policy
	 * 
	 * @param policy
	 *            one of {@link StemPolicy} constants.
	 */
	public void setStemPolicy(byte policy) {
		m_stemPolicy = StemPolicy.ensureCorrectPolicy(policy);
	}

	/**
	 * Defines the shorted name to print on all staves excepted
	 * the first where the whole name is printed
	 */
	public void setSubname(String subname) {
		m_subname = subname;
	}

	/**
	 * Attaches a tablature definition to this voice, <code>null</code> to
	 * remove tablature.
	 */
	public void setTablature(Tablature tab) {
		m_tablature = tab;
	}

	/** %%MIDI volume(?) xx */
	public void setVolume(byte volume) {
		this.m_volume = volume;
	}

	public String toString() {
		return "V:" + getVoiceId();
	}

	// TODO hasLyrics...

}

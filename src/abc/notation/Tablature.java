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
import java.util.HashMap;
import java.util.Map;

/**
 * A tablature is another way to print the notes on a usual 5-line staff. Most
 * frequent tablature is for guitar. This class only manage fretted string
 * instrument tablature like guitar, banjo, lute.
 * 
 * A tablature is attached to a Voice and generally printed under it.
 */
public class Tablature implements Cloneable, Serializable {

	private static final long serialVersionUID = 4879361297166279532L;

	public final static Tablature BANJO_BLUEGRASS = new Tablature(new Note[] {
			new Note(Note.g), new Note(Note.D), new Note(Note.G),
			new Note(Note.B), new Note(Note.d), }, 21);

	public final static Tablature BASS = new Tablature(new Note[] {
			new Note(Note.E, -2), new Note(Note.A, -2), new Note(Note.D, -1),
			new Note(Note.G, -1), }, 20);

	/** Greek bouzouki used in popular music */
	public final static Tablature BOUZOUKI_GREEK_TETRACHORDO = new Tablature(
			new Note[] { new Note(Note.C), new Note(Note.F), new Note(Note.A),
					new Note(Note.d), }, 27);

	/** Greek bouzouki used in rebetika music */
	public final static Tablature BOUZOUKI_GREEK_TRICHORDO = new Tablature(
			new Note[] { new Note(Note.D), new Note(Note.A), new Note(Note.d), },
			27);

	public final static Tablature BOUZOUKI_IRISH_GDAD = new Tablature(
			new Note[] { new Note(Note.G, -1), new Note(Note.D),
					new Note(Note.A), new Note(Note.d), }, 21);

	/** Bulgarian tambura */
	public final static Tablature TAMBURA_BULGARIAN = new Tablature(new Note[] {
			new Note(Note.D), new Note(Note.G), new Note(Note.B),
			new Note(Note.e), }, 21);

	/**
	 * Latin american traditionally made with the shell of the back of an
	 * armadillo
	 */
	public final static Tablature CHARANGO = new Tablature(new Note[] {
			new Note(Note.G), new Note(Note.c), new Note(Note.E),
			new Note(Note.A), new Note(Note.e), }, 15);

	public final static Tablature GUITAR = new Tablature(new Note[] {
			new Note(Note.E, -1), new Note(Note.A, -1), new Note(Note.D),
			new Note(Note.G), new Note(Note.B), new Note(Note.e), }, 21);

	/** Guitar Irish tuning */
	public final static Tablature GUITAR_DADGAD = new Tablature(new Note[] {
			new Note(Note.D, -1), new Note(Note.A, -1), new Note(Note.D),
			new Note(Note.G), new Note(Note.A), new Note(Note.D, 1), }, 21);

	public final static Tablature MANDOLIN = new Tablature(new Note[] {
			new Note(Note.G, -1), new Note(Note.D), new Note(Note.A),
			new Note(Note.e), }, 21);

	private int m_numberOfFret = 0;

	private Note[] m_strings = null;
	
	private Map<MusicElementReference, int[]> computedFingerings = null;

	public Tablature(Note[] strings, int numberOfFret) {
		m_strings = strings;
		m_numberOfFret = numberOfFret;
	}
	
	public Note[] getStrings() {
		return m_strings;
	}
	
	public int getNumberOfString() {
		return m_strings.length;
	}
	
	/**
	 * Returns the Note for the request string
	 * @param stringNumber Starts at 1 for lowest string line
	 * @return a Note object, or null if string number out of
	 * defined strings
	 */
	public Note getStringNote(int stringNumber) {
		if (stringNumber > 0 && stringNumber <= m_strings.length) {
			return m_strings[stringNumber-1];
		}
		else return null;
	}
	
	/**
	 * Returns the position and fingering of note n.
	 * This should be called after {@link #computedFingerings}
	 * which compute bests fingerings.
	 * The result is a array of 2 int, the first is the string
	 * number, and the second is the fret.
	 * 
	 * @param n A Note
	 * @return int[2] or null if not computed (rest, end of tie...)
	 */
	public int[] getFingeringForNote(Note n) {
		return (int[]) computedFingerings.get(n.getReference());
	}
	
	/**
	 * Here is the brain of tablature fingerings computation.
	 * @param musicElements Collection of MusicElement in which notes
	 * will be used for computation.
	 */
	public void computeFingerings(Collection<MusicElement> musicElements) {
		if (computedFingerings != null)
			computedFingerings.clear();
		//collect all notes
		Collection<NoteAbstract> notes = new ArrayList<NoteAbstract>(musicElements.size());
		for (MusicElement element : musicElements) {
			if (element instanceof NoteAbstract) {
				NoteAbstract[] graces = ((NoteAbstract) element).getGracingNotes();
				if (graces != null) {
					for (int i = 0; i < graces.length; i++) {
						notes.add(graces[i]);
					}
				}
				notes.add((NoteAbstract) element);
			}
		}
		computedFingerings = new HashMap<MusicElementReference, int[]>(notes.size());
		
		//TODO iterate notes collection, compute fingering
		NoteAbstract lastNote = null;
		for (NoteAbstract currentNote : notes) {
			if (currentNote instanceof MultiNote) {
				//we have a chord
				for (Note note : ((MultiNote) currentNote).getNotesAsVector()) {
					if (note.isRest() || note.isEndingTie())
						continue;
					//just a random thing to put numbers on the tab :-)
					int string = 1+(int)(Math.random()*(m_strings.length-1));
					int fret = (int)(Math.random()*m_numberOfFret);
					computedFingerings.put(note.getReference(),
							new int[]{string, fret});
				}
			} else if (currentNote instanceof Note) {
				Note note = (Note) currentNote;
				if (!note.isEndingTie()) {
					int[] pos = null;
					if (note.isRest()) {
						//reinit penalty, player may have time
						//to move on the neck
					}
					else if ((lastNote != null)
						&& (lastNote instanceof Note)
						&& (note.getMidiLikeHeight()
							== ((Note)lastNote).getMidiLikeHeight())
						&& ((pos = getFingeringForNote((Note)lastNote)) != null)) {
						//If the current note is the same than the last note
						//keep the same position
						computedFingerings.put(note.getReference(),
							new int[] {pos[0], pos[1]});
					} else {
						//just a random thing to put numbers on the tab :-)
						int string = 1+(int)Math.round(Math.random()*(m_strings.length-1));
						int fret = (int)(Math.random()*m_numberOfFret);
						computedFingerings.put(note.getReference(),
							new int[]{string, fret});
					}
				}
			}
			lastNote = currentNote;
		}
		//use penalty parameters, use private field if needed:
		//- maximal gap between index finger and little finger
		//	e.g. guitar: 3 frets near headstock/nut, 5 near body
		//		 charango: 5 near headstock/nut, 7 near body
		//- pick another string (small penalty)
		//- move finger to another fret (greater penalty)
		//- void string in a chord (increasing with number of strings
		//	(no penalty for 3 or 4 strings, huge penalty for 7+ strings...)
		//
		//if a note is impossible to render on tab, set fret number to -1
		//(note lower than lowest string, more notes in MultiNote than strings...)
		//capodastre position = the minimal fret value
		
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}

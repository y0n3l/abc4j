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

import java.util.Vector;

/**
 * This class defines a (single) Note : height, rhythm, part of tuplet, rest
 * etc... There can be some tricky representation of a duration for a note. For
 * instance :<BR/> <B>Tuplets</B><BR/> <IMG src="../../images/tuplets.gif"/><BR/>
 * The first tuplet describes 3 quarter notes in the time of 2. So in that case,
 * the representation of each note of tuplet as a <TT>Note</TT> object is :
 * <UL>
 * <LI><TT>getStrictDuration()</TT> returns <TT>Note.QUARTER</TT></LI>
 * <LI><TT>isPartOfTuplet()</TT> returns <TT>true</TT></LI>
 * <LI><TT>getTuplet()</TT> returns a <TT>Tuplet</TT> instance that
 * enacapsulates the three instances of notes that are part of the tuplet.</LI>
 * </UL>
 * The same applies to the second tuplet except that the strict duration of the
 * notes composing the tuplet is <TT>Note.EIGHTH</TT>. Same for the third
 * tuplet with a strict duration equals to <TT>Note.SIXTEENTH</TT>. <B>Dots</B><BR/>
 * <IMG src="../../images/multiDots.jpg"/><BR/> When a note is dotted, its
 * strict duration remains unchanged. The only difference between a non-dotted
 * note and a dotted one can be retrieved from the method <TT>countDots()</TT>
 * that returns the number of dots for a given <TT>Note</TT> instance. So in
 * the example above :
 * <UL>
 * <LI>For the first example</LI>
 * <UL>
 * <LI><TT>getStrictDuration()</TT> returns <TT>Note.WHOLE</TT></LI>
 * <LI><TT>countDots()</TT> returns <TT>2</TT></LI>
 * </UL>
 * <LI>For the second example</LI>
 * <UL>
 * <LI><TT>getStrictDuration()</TT> returns <TT>Note.HALF</TT></LI>
 * <LI><TT>countDots()</TT> returns <TT>2</TT></LI>
 * </UL>
 * <LI>For the third example</LI>
 * <UL>
 * <LI><TT>getStrictDuration()</TT> returns <TT>Note.HALF</TT></LI>
 * <LI><TT>countDots()</TT> returns <TT>3</TT></LI>
 * </UL>
 * </UL>
 */
public class Note extends NoteAbstract implements Cloneable {

	// max short possible = 290, else transform to int = 560 (9:2 64th)
	protected static final short LENGTH_RESOLUTION = 12;

	/** The <TT>a</TT> note height type. */
	public static final byte a = 21;

	/** The <TT>A</TT> note height type : A440 */
	public static final byte A = 9;

	/** The <TT>b</TT> note height type. */
	public static final byte b = 23;

	/** The <TT>B</TT> note height type. */
	public static final byte B = 11;

	/** The <TT>BREVE</TT> (double whole) length type. */
	public static final short BREVE = LENGTH_RESOLUTION * 128;

	/** The <TT>c</TT> note height type. */
	public static final byte c = 12;

	/** The <TT>C</TT> note height type. */
	public static final byte C = 0;

	/** The <TT>d</TT> note height type. */
	public static final byte d = 14;

	/** The <TT>D</TT> note height type. */
	public static final byte D = 2;

	/** The <TT>DOTTED_EIGHTH</TT> length type. */
	public static final short DOTTED_EIGHTH = LENGTH_RESOLUTION * 12;

	/** The <TT>DOTTED_HALF</TT> length type. */
	public static final short DOTTED_HALF = LENGTH_RESOLUTION * 48;

	/** The <TT>DOTTED_QUARTER</TT> length type. */
	public static final short DOTTED_QUARTER = LENGTH_RESOLUTION * 24;

	/** The <TT>DOTTED_SIXTEENTH</TT> length type. */
	public static final short DOTTED_SIXTEENTH = LENGTH_RESOLUTION * 6;

	/** The <TT>DOTTED_SIXTY_FOURTH</TT> length type. */
	public static final short DOTTED_SIXTY_FOURTH = (short) (LENGTH_RESOLUTION * 1.5);

	/** The <TT>DOTTED_THIRTY_SECOND</TT> length type. */
	public static final short DOTTED_THIRTY_SECOND = LENGTH_RESOLUTION * 3;

	/** The <TT>DOTTED_WHOLE</TT> length type. */
	public static final short DOTTED_WHOLE = LENGTH_RESOLUTION * 96;

	/** The <TT>e</TT> note height type. */
	public static final byte e = 16;

	/** The <TT>E</TT> note height type. */
	public static final byte E = 4;

	/** The <TT>EIGHTH</TT> length type. <IMG src="../../images/eighth.jpg"/> */
	public static final short EIGHTH = LENGTH_RESOLUTION * 8;

	/** The <TT>f</TT> note height type. */
	public static final byte f = 17;

	/** The <TT>F</TT> note height type. */
	public static final byte F = 5;

	/** The <TT>g</TT> note height type. */
	public static final byte g = 19;

	/** The <TT>G</TT> note height type. */
	public static final byte G = 7;

	/** The <TT>HALF</TT> length type. <IMG src="../../images/half.jpg"/> */
	public static final short HALF = LENGTH_RESOLUTION * 32;

	/** The <TT>HUNDRED TWENTY-EIGHTH</TT> rare length. */
	public static final short HUNDRED_TWENTY_EIGHTH = LENGTH_RESOLUTION / 2;

	/** The <TT>LONG</TT> (double breve) length type. */
	public static final short LONG = LENGTH_RESOLUTION * 256;

	/** The <TT>QUARTER</TT> length type. <IMG src="../../images/quarter.jpg"/> */
	public static final short QUARTER = LENGTH_RESOLUTION * 16;

	/** The <TT>REST</TT> height type. */
	public static final byte REST = -128;

	private static final long serialVersionUID = -5606744266433398412L;

	/**
	 * The <TT>SIXTEENTH</TT> length type. <IMG
	 * src="../../images/sixteenth.jpg"/>
	 */
	public static final short SIXTEENTH = LENGTH_RESOLUTION * 4;

	/** The <TT>SIXTY_FOURTH</TT> length type. */
	public static final short SIXTY_FOURTH = LENGTH_RESOLUTION;

	/**
	 * The <TT>THIRTY_SECOND</TT> length type. <IMG
	 * src="../../images/thirtySecond.jpg"/>
	 */
	public static final short THIRTY_SECOND = LENGTH_RESOLUTION * 2; // triple
																		// croche

	/** The <TT>WHOLE</TT> length type. <IMG src="../../images/whole.jpg"/> */
	public static final short WHOLE = LENGTH_RESOLUTION * 64;

	public static short convertToNoteLengthStrict(Fraction fraction)
			throws IllegalArgumentException {
		return convertToNoteLengthStrict(fraction.getNumerator(), fraction
				.getDenominator());
	}

	public static short convertToNoteLengthStrict(int num, int denom)
			throws IllegalArgumentException {
		if (num == 1 && denom == 1)
			return Note.WHOLE;
		else if (num == 1 && denom == 2)
			return Note.HALF;
		else if (num == 3 && denom == 2)
			return Note.DOTTED_WHOLE;
		else if (num == 1 && denom == 4)
			return Note.QUARTER;
		else if (num == 3 && denom == 4)
			return Note.DOTTED_HALF;
		else if (num == 1 && denom == 8)
			return Note.EIGHTH;
		else if (num == 3 && denom == 8)
			return Note.DOTTED_QUARTER;
		else if (num == 1 && denom == 16)
			return Note.SIXTEENTH;
		else if (num == 3 && denom == 16)
			return Note.DOTTED_EIGHTH;
		else if (num == 1 && denom == 32)
			return Note.THIRTY_SECOND;
		else if (num == 3 && denom == 32)
			return Note.DOTTED_SIXTEENTH;
		else if (num == 1 && denom == 64)
			return Note.SIXTY_FOURTH;
		else if (num == 1 && denom == 64)
			return Note.DOTTED_THIRTY_SECOND;
		else
			throw new IllegalArgumentException(num + "/" + denom
					+ " doesn't correspond to any strict note length");
	}

	public static byte convertToNoteType(String note) {
		if (note.equals("A"))
			return Note.A;
		else if (note.equals("B"))
			return Note.B;
		else if (note.equals("C"))
			return Note.C;
		else if (note.equals("D"))
			return Note.D;
		else if (note.equals("E"))
			return Note.E;
		else if (note.equals("F"))
			return Note.F;
		else if (note.equals("G"))
			return Note.G;
		else if (note.equals("a"))
			return Note.a;
		else if (note.equals("b"))
			return Note.b;
		else if (note.equals("c"))
			return Note.c;
		else if (note.equals("d"))
			return Note.d;
		else if (note.equals("e"))
			return Note.e;
		else if (note.equals("f"))
			return Note.f;
		else if (note.equals("g"))
			return Note.g;
		else if (note.equals("z"))
			return Note.REST;
		// abc v2.0 invisible rest
		else if (note.equals("x"))
			return Note.REST;
		else
			return -1;
	}

	/**
	 * If possible, returns the enharmonic note with the given accidental type.
	 * If impossible, returns the note (cloned).
	 * 
	 * e.g.: enharmonics of _E (E flat) are ^D (D sharp) and __F (F double
	 * flat).
	 * 
	 * <B>Be careful!</B> note without accidental ({@link Accidental#NONE})
	 * is considered as NATURAL. In fact it depends on the key! If height =
	 * Note.E and accidental = NONE, it could be E flat if key is Bb major, C
	 * minor...!
	 * 
	 * @param note
	 * @param accidentalTypes
	 *            array of accidental types, by order of preference
	 * @see #createEnharmonic(Note, KeySignature)
	 */
	static public Note createEnharmonic(Note note, Accidental[] accidentalTypes) {
		Note ret;
		try {
			ret = (Note) note.clone();
		} catch (CloneNotSupportedException never) {
			never.printStackTrace();
			ret = null;
		}
		if (ret.isRest() || (accidentalTypes[0].equals(ret.getAccidental())))
			return ret;
		Vector<Note> nearNotes = new Vector<Note>(9);
		for (int i = -4; i <= 4; i++) {
			if (i == 0)
				continue;
			try {
				nearNotes.add(transpose(ret, i));
			} catch (NoteHeightException ignoreIt) {
			}
		}
		try {
			nearNotes.add(transpose(ret, 0));
		} catch (NoteHeightException ignoreIt) {
		}
		for (int i = 0; i < accidentalTypes.length; i++) {
			for (Note nearNote : nearNotes) {
				nearNote.setAccidental(accidentalTypes[i]);
				if (nearNote.getMidiLikeMicrotonalHeight() == note
						.getMidiLikeMicrotonalHeight())
					return nearNote;
			}
		}
		return ret;
	}

	/**
	 * If possible, returns the enharmonic note with the given accidental type.
	 * If impossible, returns the note (cloned).
	 * 
	 * e.g.: enharmonics of _E (E flat) are ^D (D sharp) and __F (F double
	 * flat).
	 * 
	 * @param note
	 * @param key
	 */
	static public Note createEnharmonic(Note note, KeySignature key) {
		Note ret;
		try {
			ret = (Note) note.clone();
		} catch (CloneNotSupportedException never) {
			never.printStackTrace();
			ret = null;
		}
		if (note.getAccidental().isInTheKey())
			return ret; // is in the key
		Accidental accValue = note.getAccidental();
		if (accValue.isDoubleFlat() || accValue.isDoubleSharp())
			ret = createEnharmonic(note, new Accidental[] { Accidental.NATURAL,
					Accidental.SHARP });
		if (ret.getAccidental().equals(
				key.getAccidentalFor(ret.getStrictHeight()))) {
			// no need to change
			ret.setAccidental(Accidental.NONE);
		}
		// 1st degree with different accidental than key accidental
		else if (ret.getStrictHeight() == key.getDegree(Degree.TONIC)) {
			if (key.getAccidental().isSharp())
				ret = createEnharmonic(ret, new Accidental[] {
						Accidental.DOUBLE_SHARP, Accidental.SHARP,
						Accidental.NATURAL });
			else if (key.getAccidental().isFlat())
				ret = createEnharmonic(ret, new Accidental[] {
						Accidental.NATURAL, Accidental.FLAT });
			else {// NATURAL
				Accidental acc = key.hasOnlySharps() ? Accidental.SHARP
						: Accidental.FLAT;
				ret = createEnharmonic(ret, new Accidental[] {
						Accidental.NATURAL, acc });
			}
		}
		// 7th degree (leading note) is always sharp, never key note flat
		else if (ret.getStrictHeight() == key.getDegree(Degree.LEADING_TONE)) {
			if (key.getAccidental().isFlat())
				ret = createEnharmonic(ret, new Accidental[] {
						Accidental.NATURAL, Accidental.FLAT });
			else if (key.getAccidental().isSharp())
				ret = createEnharmonic(ret, new Accidental[] {
						Accidental.DOUBLE_SHARP, Accidental.SHARP,
						Accidental.NATURAL });
			else
				ret = createEnharmonic(ret, new Accidental[] {
						Accidental.SHARP, Accidental.NATURAL });
		}
		// 2nd degree (supertonic) flat is never 1st sharp
		// else if (((ret.getStrictHeight() == key.getDegree(1))
		// && (ret.getAccidental().isSharp()))
		// || ((ret.getStrictHeight() == key.getDegree(2))
		// && (ret.getAccidental().isFlat()))) {
		// ret = createEnharmonic(note,
		// new Accidental[] {Accidental.NATURAL, Accidental.FLAT});
		// }
		else {
			// 1st prefered accidental is given by the key
			// 2nd is flat or sharp depends on key dominant accidental
			Accidental dominant = Accidental.NATURAL;
			if (key.hasOnlyFlats())
				dominant = Accidental.FLAT;
			else if (key.hasOnlySharps())
				dominant = Accidental.SHARP;
			// other degrees
			for (int i = Degree.SUPERTONIC; i <= Degree.SUBMEDIANT; i++) {
				if (ret.getStrictHeight() == key.getDegree(i))
					ret = createEnharmonic(note, new Accidental[] {
							key.getAccidentalFor(ret.getStrictHeight()),
							Accidental.NATURAL, dominant });
			}
		}

		// second time, for cases where new note has been computed
		if (ret.getAccidental().equals(
				key.getAccidentalFor(ret.getStrictHeight()))) {
			// set accidental to NONE
			ret.setAccidental(Accidental.NONE);
		}
		return ret;
	}

	/**
	 * Create a Note from a given midi like height see
	 * {@link #getMidiLikeHeight()}. Accidental is always NATURAL or SHARP
	 * 
	 * e.g.:
	 * <ul>
	 * <li>midiHeight = 0 returns C
	 * <li>midiHeight = 13 returns ^c never _d
	 * <li>midiHeight = -1 returns B,
	 * </ul>
	 * 
	 * @param midiHeight
	 * @throws NoteHeightException
	 */
	static public Note createFromMidiLikeHeight(int midiHeight)
			throws NoteHeightException {
		if (midiHeight == REST)
			return new Note(REST);
		if ((midiHeight < Byte.MIN_VALUE) || (midiHeight > Byte.MAX_VALUE))
			throw new NoteHeightException(midiHeight);
		// +132: we are sure we have a positive value
		int strictHeight = (midiHeight + 132) % 12;
		Accidental accident = Accidental.NATURAL;
		if ((strictHeight != C) && (strictHeight != D) && (strictHeight != E)
				&& (strictHeight != F) && (strictHeight != G)
				&& (strictHeight != A) && (strictHeight != B)) {
			accident = Accidental.SHARP;
			strictHeight -= 1;
			midiHeight -= 1;
		}
		byte octave = getOctaveTransposition((byte) midiHeight);
		return new Note((byte) strictHeight, accident, octave);
	}

	/**
	 * Create a Note from a given midi like height see
	 * {@link #getMidiLikeHeight()} and a preferred accidental type, to choose
	 * the proper enharmonic.
	 * 
	 * e.g.: a midi height of 15 is d# or eb above A440Hz. The key signature
	 * will determinate if alteration is sharp (E major, B major) or flat (Bb
	 * major, C minor).
	 * 
	 * <B>Be careful!</B> note without accidental ({@link Accidental#NONE})
	 * is considered as NATURAL. In fact it depends on the key! If height =
	 * Note.E and accidental = NONE, it could be E flat if key is Bb major, C
	 * minor...!
	 * 
	 * @param midiHeight
	 * @param key
	 */
	static public Note createFromMidiLikeHeight(int midiHeight,
			Accidental preferredAccidental) {
		return createEnharmonic(createFromMidiLikeHeight(midiHeight),
				new Accidental[] { preferredAccidental });
	}

	/**
	 * Create a Note from a given midi like height see
	 * {@link #getMidiLikeHeight()} and a KeySignature, to choose the proper
	 * enharmonic.
	 * 
	 * e.g.: a midi height of 15 is d# or eb above A440Hz. The key signature
	 * will determinate if alteration is sharp (E major, B major) or flat (Bb
	 * major, C minor).
	 * 
	 * If the key has no accidental, sharp is preferred. If the note is just
	 * under the key note, sharp is preferred (e.g. f# instead of gb for G
	 * minor).
	 * 
	 * @param midiHeight
	 * @param key
	 */
	static public Note createFromMidiLikeHeight(int midiHeight, KeySignature key) {
		Accidental acc = key.isFlatDominant() ? Accidental.FLAT
				: Accidental.SHARP;
		return createFromMidiLikeHeight(midiHeight, acc);
	}

	public static Note getHighestNote(Note[] notes) {
		Note highestNote = notes[0];
		for (int i = 1; i < notes.length; i++) {
			if (notes[i].getHeight() > highestNote.getHeight()) {
				highestNote = notes[i];
				// System.out.println("highest note is" + i);
			}
		}
		return highestNote;
	}

	public static int getHighestNoteIndex(NoteAbstract[] notes) {
		NoteAbstract highestNote = notes[0];
		int highestNoteHeight = (highestNote instanceof MultiNote) ? ((MultiNote) highestNote)
				.getHighestNote().getHeight()
				: ((Note) highestNote).getHeight();
		int index = 0;
		for (int i = 1; i < notes.length; i++) {
			int currentNoteHeight = (notes[i] instanceof MultiNote) ? ((MultiNote) notes[i])
					.getHighestNote().getHeight()
					: ((Note) notes[i]).getHeight();
			if (currentNoteHeight > highestNoteHeight) {
				highestNote = notes[i];
				highestNoteHeight = currentNoteHeight;
				index = i;
				// System.out.println("highest note is" + i);
			}
		}
		return index;
	}

	public static Note getLowestNote(Note[] notes) {
		Note lowestNote = notes[0];
		for (int i = 1; i < notes.length; i++) {
			if (notes[i].getHeight() < lowestNote.getHeight()) {
				lowestNote = notes[i];
				// System.out.println("highest note is" + i);
			}
		}
		return lowestNote;
	}

	public static int getLowestNoteIndex(NoteAbstract[] notes) {
		NoteAbstract lowestNote = notes[0];
		int lowestNoteHeight = (lowestNote instanceof MultiNote) ? ((MultiNote) lowestNote)
				.getLowestNote().getHeight()
				: ((Note) lowestNote).getHeight();
		int index = 0;
		for (int i = 1; i < notes.length; i++) {
			int currentNoteHeight = (notes[i] instanceof MultiNote) ? ((MultiNote) notes[i])
					.getLowestNote().getHeight()
					: ((Note) notes[i]).getHeight();
			if (currentNoteHeight < lowestNoteHeight) {
				lowestNote = notes[i];
				lowestNoteHeight = currentNoteHeight;
				index = i;
				// System.out.println("lowest note is" + i);
			}
		}
		return index;
	}

	/**
	 * Returns the octave transposition for the specified height relative to its
	 * strict height. For instance, the octave transposition of <TT>Note.c</TT>
	 * is <TT>1</TT> because it is one octave higher than its strict height
	 * <TT>Note.C</TT>.
	 * 
	 * @param height
	 *            A height as a byte that respect the scale defined by constants
	 *            such as C D E F G A B c d e ....
	 * @return The number of octave(s), to reach the given height from the stric
	 *         height. A positive value is returned if the height is higher than
	 *         the strict height, negative otherwise.
	 */
	public static byte getOctaveTransposition(byte height) {
		if (height == REST)
			return 0;
		return (byte) ((height - getStrictHeight(height)) / 12);
	}

	public static short getStrictDuration(short duration) {
		if (isStrictDuration(duration))
			return duration;
		short[] strictDurs = new short[] { LONG, BREVE, WHOLE, HALF, QUARTER,
				EIGHTH, SIXTEENTH, THIRTY_SECOND, SIXTY_FOURTH };
		for (int i = 1; i < strictDurs.length; i++) {
			if (strictDurs[i] < duration)
				return strictDurs[i];
		}
		return SIXTY_FOURTH;
	}

	/**
	 * Returns this note absolute height. This height doesn't take in account
	 * octave transposition.
	 * 
	 * @param height
	 *            A height of a note as a byte that respect the scale defined by
	 *            constants such as C D E F G A B c d e ....
	 * @return The height of this note on the first octave. Possible values are
	 *         <TT>C</TT>, <TT>D</TT>, <TT>E</TT>, <TT>F</TT>, <TT>G</TT>,
	 *         <TT>A</TT>(440Hz) <TT>B</TT> or <TT>REST</TT> only.
	 * @see #getHeight()
	 */
	public static byte getStrictHeight(byte height) {
		if (height == REST)
			return REST;
		// The +132 is needed to move the height of the note to a positive range
		byte sh = (byte) ((height + 132) % 12);
		if (!(sh == Note.C || sh == Note.D || sh == Note.E || sh == Note.F
				|| sh == Note.G || sh == Note.A || sh == Note.B))
			throw new IllegalArgumentException(
					"The height "
							+ height
							+ " cannot be strictly mapped because of sharp or flat (sh="
							+ sh + ")");
		else
			return sh;
	}

	/**
	 * Returns <TT>true</TT> if the duration of the note is one of the
	 * following : {@link #LONG}, {@link #BREVE}, {@link #WHOLE},
	 * {@link #HALF}, {@link #QUARTER}, {@link #EIGHTH}, {@link #SIXTEENTH},
	 * {@link #THIRTY_SECOND}, {@link #SIXTY_FOURTH}.
	 * 
	 * @param noteDuration
	 *            The note duration to be checked
	 */
	public static boolean isStrictDuration(short noteDuration) {
		return ((noteDuration == LONG) || (noteDuration == BREVE)
				|| (noteDuration == WHOLE) || (noteDuration == HALF)
				|| (noteDuration == QUARTER) || (noteDuration == EIGHTH)
				|| (noteDuration == SIXTEENTH)
				|| (noteDuration == THIRTY_SECOND)
				|| (noteDuration == SIXTY_FOURTH) || (noteDuration == HUNDRED_TWENTY_EIGHTH));
	}

	/** <TT>true</TT> if this note is tied, <TT>false</TT> otherwise. */

	/**
	 * Transpose a note by the given number of semi-tones. The returned note is
	 * a clone which only height is changed. If any, the accidental is always
	 * sharp, never flat.
	 * 
	 * To transpose and specify accidental, use
	 * {@link #transpose(Note, int, KeySignature)} which apply
	 * {@link #createEnharmonic(Note, KeySignature)} after transposing.
	 * 
	 * @param note
	 * @param semitones
	 * @return
	 * @throws NoteHeightException
	 *             if transposition is going to low or to high.
	 */
	static public Note transpose(Note note, int semitones)
			throws NoteHeightException {
		return transpose(note, semitones, 0);
	}

	/**
	 * Transpose a note by the given number of semi-tones and micro-tones. The
	 * returned note is a clone which only height is changed. If any, the
	 * accidental is always sharp, never flat.
	 * 
	 * To transpose and specify accidental, use
	 * {@link #transpose(Note, int, KeySignature)} which apply
	 * {@link #createEnharmonic(Note, KeySignature)} after transposing, or
	 * {@link #transpose(Note, Interval)} with optional KeySignature parameter
	 * {@link #transpose(Note, Interval, KeySignature)}.
	 * 
	 * @param note
	 * @param semitones
	 * @param microtones
	 * @return
	 * @throws NoteHeightException
	 *             if transposition is going to low or to high.
	 */
	static public Note transpose(Note note, int semitones, float microtones)
			throws NoteHeightException {
		Note ret;
		try {
			ret = (Note) note.clone();
		} catch (CloneNotSupportedException never) {
			never.printStackTrace();
			ret = null;
		}
		// if semitones == 0, E## will be transposed as F#
		// ensure that we will return NATURAL or SHARP accidental
		if (ret.isRest()/* || (semitones == 0) */)
			return ret;
		float microtonalOffset = microtones
				+ ret.getAccidental().getMicrotonalOffset();
		int newMidiHeight = ret.getMidiLikeHeight() + semitones;
		if ((newMidiHeight < Byte.MIN_VALUE)
				|| (newMidiHeight > Byte.MAX_VALUE))
			throw new NoteHeightException(newMidiHeight);
		int addSemitones = (int) (microtonalOffset < 0 ? Math
				.ceil(microtonalOffset) : Math.floor(microtonalOffset));
		Note newHeight = createFromMidiLikeHeight(newMidiHeight + addSemitones);
		ret.setHeight(newHeight.getHeight());
		ret.setAccidental(new Accidental(newHeight.getAccidental().getValue()
				+ (microtonalOffset - addSemitones)));
		ret.setOctaveTransposition(newHeight.getOctaveTransposition());
		return ret;
	}

	/**
	 * Transpose a note by the given number of semi-tones. The returned note is
	 * a clone which only height is changed. The accidental is deduced from the
	 * key signature.
	 * 
	 * @param note
	 * @param semitones
	 * @param keySig
	 * @return
	 * @throws NoteHeightException
	 *             if transposition is going to low or to high.
	 */
	static public Note transpose(Note note, int semitones, KeySignature keySig)
			throws NoteHeightException {
		return createEnharmonic(transpose(note, semitones), keySig);
	}

	/**
	 * Transpose the given note by the given interval, which make a serious
	 * difference compared to semitone transposition. <br>
	 * C - 2 semitones = A# or Bb whereas C - a second = Bb, not A#
	 * 
	 * @param note
	 * @param interval
	 * @return
	 * @throws NoteHeightException
	 */
	static public Note transpose(Note note, Interval interval)
			throws NoteHeightException {
		return interval.calculateSecondNote(note);
	}

	/**
	 * Transpose the given note by the given interval, which make a serious
	 * difference compared to semitone transposition. <br>
	 * C - 2 semitones = A# or Bb whereas C - a second = Bb, not A#
	 * 
	 * @param note
	 * @param interval
	 * @param destinationKey
	 *            if specified, remove unnecessary accidental to result note
	 * @return
	 * @throws NoteHeightException
	 */
	static public Note transpose(Note note, Interval interval,
			KeySignature destinationKey) throws NoteHeightException {
		return interval.calculateSecondNote(note, destinationKey);
	}

	/** Accidental for this note. */
	private Accidental m_accidental = new Accidental();// Accidental.NONE;

	/**
	 * The full whole duration that takes into account the dots. (why not the
	 * tuplet stuff ? :/ )
	 */
	private short m_duration = -1;

	/** Is the rest invisible (not printed on score) */
	private boolean m_invisibleRest = false;

	private byte m_octaveTransposition = 0;

	/**
	 * The strict duration (that does not take into account the dots, the tuplet
	 * or whatever : this is the pure note type definition.
	 */
	private short m_strictDuration = EIGHTH;

	/**
	 * The height of the note AS A CONSTANT such as C D E F G A B <B>only</B> !!
	 * This strict height must be used with the octave transposition (if
	 * defined) to know the real height of this note. Accidentals are not taken
	 * into account in this value.
	 */
	private byte m_strictHeight = REST;

	/**
	 * Creates an abc note with the specified height. Accidental will inherit
	 * its default value <TT>Accidental.NONE</TT>.
	 * 
	 * @param heightValue
	 *            The heigth of this note as a byte that respect the scale
	 *            defined by constants such as C D E F G A B c d e ..... The
	 *            heigth is <TT>REST</TT> if this note is a rest.
	 * @see #setHeight(byte)
	 */
	public Note(byte heightValue) {
		super();
		setHeight(heightValue);
	}

	/**
	 * Creates an abc note with the specified heigth and accidental.
	 * 
	 * @param heightValue
	 *            The heigth of this note as a byte that respect the scale
	 *            defined by constants such as C D E F G A B c d e ..... The
	 *            heigth is <TT>REST</TT> if this note is a rest.
	 * @param accidental
	 */
	public Note(byte heightValue, Accidental accidental) {
		super();
		setHeight(heightValue);
		setAccidental(accidental);
	}

	/**
	 * Creates an abc note with the specified heigth, accidental and octave
	 * transposition.
	 * 
	 * @param heightValue
	 *            The heigth of this note as a byte that respect the scale
	 *            defined by constants such as C D E F G A B c d e ..... The
	 *            heigth is <TT>REST</TT> if this note is a rest.
	 * @param accidental
	 * @param octaveTranspositionValue
	 *            The octave transposition for this note : 1, 2 or 3 means "1, 2
	 *            or 3 octave(s) higher than the reference octave" and -1, -2 or
	 *            -3 means "1, 2 or 3 octave(s) less than the reference octave".
	 */
	public Note(byte heightValue, Accidental accidental,
			byte octaveTranspositionValue) {
		this(heightValue, accidental);
		setOctaveTransposition((byte) (m_octaveTransposition + octaveTranspositionValue));
	}

	/**
	 * Creates an abc note with the specified heigth and accidental.
	 * 
	 * @param heightValue
	 *            The heigth of this note as a byte that respect the scale
	 *            defined by constants such as C D E F G A B c d e ..... The
	 *            heigth is <TT>REST</TT> if this note is a rest.
	 * @param accidentalValue
	 *            Accidental for this note. Possible values are <TT>Accidental.NATURAL</TT>,
	 *            <TT>Accidental.SHARP</TT> (#), <TT>Accidental.FLAT</TT>
	 *            (b) or <TT>Accidental.NONE</TT>.
	 * @see #setAccidental(byte)
	 * @see #setHeight(byte)
	 * @deprecated use {@link #Note(byte, Accidental)}
	 */
	public Note(byte heightValue, float accidentalValue) {
		this(heightValue, new Accidental(accidentalValue));
		System.err
				.println("Note constructor using float accidentalValue is deprecated, use Accidental constant");
	}

	/**
	 * Creates an abc note with the specified heigth, accidental and octave
	 * transposition.
	 * 
	 * @param heightValue
	 *            The heigth of this note as a byte that respect the scale
	 *            defined by constants such as C D E F G A B c d e ..... The
	 *            heigth is <TT>REST</TT> if this note is a rest.
	 * @param accidentalValue
	 *            Accidental for this note. Possible values are <TT>Accidental.NATURAL</TT>,
	 *            <TT>Accidental.SHARP</TT> (#), <TT>Accidental.FLAT</TT>
	 *            (b) or <TT>Accidental.NONE</TT>.
	 * @param octaveTranspositionValue
	 *            The octave transposition for this note : 1, 2 or 3 means "1, 2
	 *            or 3 octave(s) higher than the reference octave" and -1, -2 or
	 *            -3 means "1, 2 or 3 octave(s) less than the reference octave".
	 * @see #setAccidental(byte)
	 * @see #setOctaveTransposition(byte)
	 * @see #setHeight(byte)
	 * @deprecated
	 */
	public Note(byte heightValue, float accidentalValue,
			byte octaveTranspositionValue) {
		this(heightValue, accidentalValue);
		setOctaveTransposition((byte) (m_octaveTransposition + octaveTranspositionValue));
	}

	/**
	 * Creates an abc note with the specified heigth and octave transposition.
	 * 
	 * @param heightValue
	 *            The heigth of this note as a byte that respect the scale
	 *            defined by constants such as C D E F G A B c d e ..... The
	 *            heigth is <TT>REST</TT> if this note is a rest.
	 * @param octaveTranspositionValue
	 *            The octave transposition for this note : 1, 2 or 3 means "1, 2
	 *            or 3 octave(s) higher than the reference octave" and -1, -2 or
	 *            -3 means "1, 2 or 3 octave(s) less than the reference octave".
	 */
	public Note(byte heightValue, int octaveTranspositionValue) {
		this(heightValue, Accidental.NONE);
		setOctaveTransposition((byte) (m_octaveTransposition + octaveTranspositionValue));
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Compute a duration that takes strict duration as a reference plus the
	 * duration of the optional dots and the tuplet if any.
	 */
	private short computeDuration(short strictDuration, int dotsNumber) {
		short duration = strictDuration;
		if (isPartOfTuplet()) {
			// if the note is part of tuplet then, for now,
			// ignore the strict duration of the note. => more simple
			// but may need to ne improved.
			Tuplet tuplet = getTuplet();
			int notesNb = tuplet.getTupletNumber();
			float totalTupletDuration = tuplet.getTotalDuration();
			float tupletDefaultLength = tuplet.getDefaultNoteLength();
			double rapport = duration / tupletDefaultLength;
			// The correction for the note duration because that's a tuplet.
			duration = (short) ((totalTupletDuration / notesNb) * rapport);
		} else {
			int dotsNb = countDots();
			for (int i = 1; i <= dotsNb; i++) {
				short dottedDuration = (short) (strictDuration / (i + 1)); // at
																			// the
																			// first
																			// dot
																			// (i=1),
																			// we
																			// should
																			// divide
																			// by 2
				duration = (short) (duration + dottedDuration);
			}
		}
		return duration;
	}

	/**
	 * Returns accidental for this note if any.
	 * 
	 * @return Accidental for this note if any. Possible values are <TT>Accidental.NATURAL</TT>,
	 *         <TT>Accidental.FLAT</TT>, <TT>Accidental.SHARP</TT> or <TT>Accidental.NONE</TT>.
	 * @see #setAccidental(byte)
	 */
	public Accidental getAccidental() {
		return m_accidental;
	}

	/**
	 * Returns accidental for this note. If the note has no accidental, returns
	 * the accidental at key for this note (if key is not null)
	 * 
	 * @return If key is not null, accidental is allways defined (never set to
	 *         NONE, at least to NATURAL)
	 */
	public Accidental getAccidental(KeySignature key) {
		if ((key != null) && (m_accidental.isInTheKey())) {
			return key.getAccidentalFor(getStrictHeight());
		}
		return m_accidental;
	}

	/**
	 * Returns the duration of this note. The duration returned here takes into
	 * account if the note is dotted, part of a tuplet and so on ... (as opposed
	 * to <TT>getStrictDuration()</TT> that only refers to the "pure" note)
	 * 
	 * @return The duration of this note as a value adjusted to the scale of
	 *         constants such as <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT>
	 *         etc etc ...
	 * @see #setLength(short)
	 * @see #getStrictDuration()
	 */
	public short getDuration() {
		// != -1 : The duration has been set in an absolute manner (not
		// recommanded, but
		// can be usefull
		if (m_duration == -1) {
			// Compute the absolute duration from the strict duration and dots
			// Store it and return it.
			m_duration = computeDuration(m_strictDuration, countDots());
		}
		return m_duration;
	}

	/**
	 * Returns the length of all grace notes associated with this note.
	 * 
	 * @return The sum of strict durations of associated grace notes.
	 * @see #getStrictDuration()
	 */
	public int getGracingNotesLength() {
		int totalLength = 0;
		if (hasGracingNotes()) {
			NoteAbstract[] notes = getGracingNotes();
			for (int i = 0; i < notes.length; i++) {
				if (notes[i] instanceof Note)
					totalLength += ((Note) notes[i]).getStrictDuration();
				else
					totalLength += ((MultiNote) notes[i]).getLongestNote()
							.getStrictDuration();
			}
		}
		return totalLength;
	}

	/**
	 * Returns this note height. This height takes into account octave
	 * transposition. This height is not the height of the note itself (like how
	 * it would be played using midi for instance) but the height of its
	 * representation on a score. For instance 2 notes written C and C# would
	 * have the same value returned by getHeight(). They would only differ with
	 * their accidental value returned by {@link #getAccidental()}.
	 * 
	 * @return The heigth of this note as a byte that respect the scale defined
	 *         by constants such as C D E F G A B c d e ....
	 * @see #getStrictHeight()
	 * @see #setHeight(byte)
	 */
	public byte getHeight() {
		return (byte) (m_strictHeight + m_octaveTransposition * 12);
	}

	/**
	 * Returns this note absolute height. This height <DEL>doesn't take in
	 * account</DEL> <B>takes into account</B> octave transposition.
	 * 
	 * @return This note height.
	 * @deprecated use getHeight() instead. Sorry for the typo....
	 * @see #getHeight()
	 */
	public byte getHeigth() {
		return getHeight();
	}

	/**
	 * Returns a "midi-like" height.
	 * <ul>
	 * <li>C = 0
	 * <li>^c = 13
	 * <li>B, = -1
	 * </ul>
	 * 
	 * <B>Be careful!</B> note without accidental ({@link Accidental#NONE})
	 * is considered as NATURAL. In fact it depends on the key! If height =
	 * Note.E and accidental = NONE, it could be E flat if key is Bb major, C
	 * minor...!
	 * 
	 * @see #getMidiLikeHeight(KeySignature)
	 */
	public int getMidiLikeHeight() {
		return getMidiLikeHeight(null);
	}

	/**
	 * Returns a "midi-like" height.
	 * <ul>
	 * <li>C = 0
	 * <li>^c = 13
	 * <li>B, = -1
	 * </ul>
	 * 
	 * @param key
	 *            To determinate the midi-like height when note accidental is
	 *            not defined
	 */
	public int getMidiLikeHeight(KeySignature key) {
		return getHeight() + getAccidental(key).getNearestOccidentalValue();
	}

	/**
	 * Returns a "midi-like" height with float value taking into account the
	 * microtonal accidental.
	 * <ul>
	 * <li>C = 0
	 * <li>^c = 13
	 * <li>B, = -1
	 * </ul>
	 * 
	 * <B>Be careful!</B> note without accidental ({@link Accidental#NONE})
	 * is considered as NATURAL. In fact it depends on the key! If height =
	 * Note.E and accidental = NONE, it could be E flat if key is Bb major, C
	 * minor...!
	 * 
	 * @see #getMidiLikeMicrotonalHeight(KeySignature)
	 */
	public float getMidiLikeMicrotonalHeight() {
		return getMidiLikeMicrotonalHeight(null);
	}

	/**
	 * Returns a "midi-like" height with float value taking into account the
	 * microtonal accidental.
	 * <ul>
	 * <li>C = 0
	 * <li>^c = 13
	 * <li>B, = -1
	 * </ul>
	 * 
	 * @param key
	 *            To determinate the midi-like height when note accidental is
	 *            not defined
	 */
	public float getMidiLikeMicrotonalHeight(KeySignature key) {
		return getHeight() + getAccidental(key).getValue();
	}

	/**
	 * Returns the textual name of the note, e.g. <TT>"C"</TT>, <TT>"Eb"</TT>.
	 * Returns empty string <TT>""</TT> if this note is a rest
	 */
	public String getName() {
		try {
			String acc = m_accidental.toString();
			switch (getStrictHeight()) {
			case REST:
				return "";
			case C:
				return "C" + acc;
			case D:
				return "D" + acc;
			case E:
				return "E" + acc;
			case F:
				return "F" + acc;
			case G:
				return "G" + acc;
			case A:
				return "A" + acc;
			case B:
				return "B" + acc;
			}
			return "";// should not happen
		} catch (IllegalArgumentException shouldNeverHappen) {
			return "";
		}
	}

	/**
	 * Returns the octave transposition for this note.
	 * 
	 * @return The octave transposition for this note. Default is 0.
	 * @see #setOctaveTransposition(byte)
	 */
	public byte getOctaveTransposition() {
		return m_octaveTransposition;
	}

	/**
	 * Returns the strict duration of this note.
	 * 
	 * @return The strict duration of this note. The dot, tuplet whatever... are
	 *         taken NOT into account for the duration returned by this
	 *         function. The possible returned values are : <TT>Note.WHOLE</TT>,
	 *         <TT>Note.HALF</TT>, <TT>Note.QUARTER</TT>, <TT>Note.EIGHTH</TT>,
	 *         <TT>Note.SIXTEENTH</TT>, <TT>Note.THIRTY_SECOND</TT>, <TT>Note.SIXTY_FOURTH</TT>
	 *         or -1 if this note duration is not expressed using (strict
	 *         duration + dots + tuplet) but with an exotic duration (that can
	 *         be retrieved using getDuration() in that case).
	 * @see #getDuration()
	 */
	public short getStrictDuration() {
		return m_strictDuration;
	}

	/**
	 * Returns this note absolute height. This height doesn't take in account
	 * octave transposition.
	 * 
	 * @return The height of this note on the first octave. Possible values are
	 *         <TT>C</TT>, <TT>D</TT>, <TT>E</TT>, <TT>F</TT>, <TT>G</TT>,
	 *         <TT>A</TT>(440Hz), <TT>B</TT> or <TT>REST</TT> only.
	 * @see #getHeight()
	 * @see #setHeight(byte)
	 */
	public byte getStrictHeight() {
		return getStrictHeight(m_strictHeight);
	}

	/**
	 * @return false if {@link Accidental#isInTheKey()}
	 */
	public boolean hasAccidental() {
		return !m_accidental.isInTheKey();
	}

	/**
	 * Returns <TT>true</TT> if the given note is higher than this one.
	 * 
	 * <B>Be careful!</B> without key signature parameter, impossible to know
	 * the real accidental of a note which have no accidental (NONE); it could
	 * be SHARP at the key! Use {@link #isHigherThan(Note, KeySignature)}
	 * 
	 * @param aNote
	 *            A note instance.
	 * @return <TT>true</TT> if the given note is strictly higher than this
	 *         one, <TT>false</TT> otherwise.
	 * @see #isLowerThan(Note)
	 */
	public boolean isHigherThan(Note aNote) {
		return isHigherThan(aNote, null);
	}

	/**
	 * Returns <TT>true</TT> if the given note is higher than this one.
	 * 
	 * @param aNote
	 *            A note instance.
	 * @param key
	 *            Helps to determinate the real accidental of a note which have
	 *            NONE. Give an accurate result. <TT>null</TT> accepted.
	 * @return <TT>true</TT> if the given note is strictly higher than this
	 *         one, <TT>false</TT> otherwise.
	 * @see #isLowerThan(Note, KeySignature)
	 */
	public boolean isHigherThan(Note aNote, KeySignature key) {
		return getMidiLikeMicrotonalHeight(key) > aNote
				.getMidiLikeMicrotonalHeight(key);
	}

	public boolean isLongerThan(Note aNote) {
		return getDuration() > aNote.getDuration();
	}

	/**
	 * Returns <TT>true</TT> if the given note is lower than this one.
	 * 
	 * <B>Be careful!</B> without key signature parameter, impossible to know
	 * the real accidental of a note which have no accidental (NONE); it could
	 * be SHARP at the key! Use {@link #isHigherThan(Note, KeySignature)}
	 * 
	 * @param aNote
	 *            A note instance.
	 * @return <TT>true</TT> if the given note is strictly higher than this
	 *         one, <TT>false</TT> otherwise.
	 * @see #isLowerThan(Note)
	 */
	public boolean isLowerThan(Note aNote) {
		return isLowerThan(aNote, null);
	}

	/**
	 * Returns <TT>true</TT> if the given note is higher than this one.
	 * 
	 * @param aNote
	 *            A note instance.
	 * @param key
	 *            Helps to determinate the real accidental of a note which have
	 *            NONE. Give an accurate result. <TT>null</TT> accepted.
	 * @return <TT>true</TT> if the given note is strictly higher than this
	 *         one, <TT>false</TT> otherwise.
	 * @see #isHigherThan(Note, KeySignature)
	 */
	public boolean isLowerThan(Note aNote, KeySignature key) {
		return getMidiLikeMicrotonalHeight(key) < aNote
				.getMidiLikeMicrotonalHeight(key);
	}

	/**
	 * A convenient method that returns <TT>true</TT> if this note is a rest.
	 * A note is a rest if its height returned by {@link #getHeight()} or
	 * {@link #getStrictHeight()} is equals to <TT>Note.REST</TT>.
	 * 
	 * @return <TT>true</TT> if this note is a rest, <TT>false</TT>
	 *         otherwise.
	 */
	public boolean isRest() {
		return (m_strictHeight == REST);
	}

	/**
	 * Returns true if this note is not printed on score if it's a rest.
	 */
	public boolean isRestInvisible() {
		return m_invisibleRest;
	}

	public boolean isShorterThan(Note aNote) {
		return getDuration() < aNote.getDuration();
	}

	/** Sets the accidental for this note. */
	public Note setAccidental(Accidental accidental) {
		if (accidental == null)
			m_accidental = Accidental.NONE;
		else
			m_accidental = accidental;
		return this;
	}

	/**
	 * Sets the accidental for this note.
	 * 
	 * @param accidentalValue
	 *            Accidental for this note. Possible values are <TT>Accidental.NATURAL</TT>,
	 *            <TT>Accidental.SHARP</TT> (#), <TT>Accidental.FLAT</TT>
	 *            (b) or <TT>Accidental.NONE</TT>.
	 * @deprecated see {@link #setAccidental(Accidental)}
	 */
	public void setAccidental(byte accidentalValue) {
		setAccidental(new Accidental(accidentalValue));
	}

	public Note setAccidental(float accidentalValue) {
		setAccidental(new Accidental(accidentalValue));
		return this;
	}

	/**
	 * Sets the number of dots for this note.
	 * 
	 * @param dotsNb
	 *            The number of dots for this note.
	 * @see #countDots()
	 */
	public void setDotted(byte dotsNb) {
		super.setDotted(dotsNb);
		// Re init the whole duration => will be computed later on request only.
		m_duration = -1;
	}

	/**
	 * Sets the length of this note. However, it is recommended to represent the
	 * note duration using methods such as setStrictDuration(short
	 * strictDuration), setDotted(byte dotted) etc etc as explained at the
	 * beginning of this class description.
	 * 
	 * @param duration
	 *            The length of this note as a value adjusted to the scale of
	 *            constants such as <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT>
	 *            etc etc ...
	 * @see #getDuration()
	 */
	public void setDuration(short duration) {
		m_duration = duration;
		System.err.println("[warning]duration of " + this
				+ " set in an absolute manner with " + duration
				+ "(not recommanded but supported)");
		// Thread.dumpStack();
	}

	/**
	 * Sets the height of this note. Accidentals are not taken into account in
	 * this value, Ex: using this method you will be able to specify that your
	 * note is a C but not a C#. To express the sharp, you'll have to use the
	 * {@link #setAccidental(byte)} method.
	 * 
	 * @param heightValue
	 *            The height of this note as a byte that respect the scale
	 *            defined by constants such as C D E F G A B c d e ..... The
	 *            height is <TT>REST</TT> if this note is a rest.
	 * @see #getHeight()
	 * @see #setAccidental(byte)
	 */
	public Note setHeight(byte heightValue) throws IllegalArgumentException {
		// checks if this height does not describe a sharp.
		m_strictHeight = getStrictHeight(heightValue);
		if (m_strictHeight < 0 && m_strictHeight != REST)
			throw new IllegalArgumentException("negative : " + m_strictHeight);
		m_octaveTransposition = getOctaveTransposition(heightValue);
		// if (isRest())
		// setIsLastOfGroup(false);
		// System.out.println(heightValue + " decomposed into " + strictHeight +
		// ", "+ octaveTransposition);
		return this;
	}

	/**
	 * Sets the height of this note.
	 * 
	 * @param heigthValue
	 *            The height of this note. The height is <TT>REST</TT> if this
	 *            note is a rest.
	 * @deprecated use setHeight(byte heigthValue) instead. sorry for the
	 *             typo...
	 * @see #setHeight(byte)
	 */
	public void setHeigth(byte heigthValue) {
		setHeight(heigthValue);
	}

	/** Sets if the rest should be invisible */
	public Note setInvisibleRest(boolean b) {
		m_invisibleRest = b;
		return this;
	}

	/**
	 * Sets the length of this note.
	 * 
	 * @deprecated use setDuration(short duration) instead.
	 * @param length
	 *            The length of this note as a value adjusted to the scale of
	 *            constants such as <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT>
	 *            etc etc ...
	 * @see #setDuration(short)
	 */
	public void setLength(short length) {
		m_duration = length;
	}

	/**
	 * Sets the octave transposition for this note.
	 * 
	 * @param octaveTranspositionValue
	 *            The octave transposition for this note : 1, 2 or 3 means "1, 2
	 *            or 3 octave(s) higher than the reference octave" and -1, -2 or
	 *            -3 means "1, 2 or 3 octave(s) less than the reference octave".
	 */
	public Note setOctaveTransposition(byte octaveTranspositionValue) {
		// byte strictHeight = getStrictHeight();
		m_octaveTransposition = octaveTranspositionValue;
		// strictHeight = (byte)(strictHeight + octaveTransposition * 12);
		return this;
	}

	/**
	 * Sets the strict duration of this note.
	 * 
	 * @param strictDuration
	 *            This note strict duration. Possible values are ONLY
	 *            {@link #LONG}, {@link #BREVE}, {@link #WHOLE},
	 *            {@link #HALF}, {@link #QUARTER}, {@link #EIGHTH},
	 *            {@link #SIXTEENTH}, {@link #THIRTY_SECOND},
	 *            {@link #SIXTY_FOURTH}.
	 * @exception IllegalArgumentException
	 *                Thrown if the given duration does not match the excepted
	 *                ones.
	 */
	public Note setStrictDuration(short strictDuration)
			throws IllegalArgumentException {
		if (isStrictDuration(strictDuration)) {
			m_strictDuration = strictDuration;
			// if (m_strictDuration==Note.WHOLE || m_strictDuration==Note.HALF
			// || m_strictDuration==Note.QUARTER)
			// setIsLastOfGroup(true);
		} else
			throw new IllegalArgumentException(
					"The note duration "
							+ strictDuration
							+ " is not equals to "
							+ "Note.LONG, Note.BREVE, "
							+ "Note.WHOLE, Note.HALF, Note.QUARTER, Note.EIGHTH, Note.SIXTEENTH, "
							+ "Note.THIRTY_SECOND or Note.SIXTY_FOURTH");
		// Re init the whole duration => will be computed later on request only.
		m_duration = -1;
		return this;
	}

	/**
	 * Returns the heigth of this note on the first octave.
	 * 
	 * @return the heigth of this note on the first octave. Possible values are
	 *         <TT>C</TT>, <TT>D</TT>, <TT>E</TT>, <TT>F</TT>, <TT>G</TT>,
	 *         <TT>A</TT>(404) or <TT>B</TT>.
	 * @deprecated use getStrictHeight() instead
	 * @see #getStrictHeight()
	 */
	public byte toRootOctaveHeigth() {
		return getStrictHeight();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		switch (m_strictHeight) {
		case REST:
			sb.append("z");
			break;
		case C:
			sb.append("C");
			break;
		case D:
			sb.append("D");
			break;
		case E:
			sb.append("E");
			break;
		case F:
			sb.append("F");
			break;
		case G:
			sb.append("G");
			break;
		case A:
			sb.append("A");
			break;
		case B:
			sb.append("B");
			break;
		}
		sb.append(m_accidental.toString());
		if (m_octaveTransposition >= 1) {
			for (int i = 1; i <= m_octaveTransposition; i++)
				sb.append("'");
		} else if (m_octaveTransposition <= -1) {
			for (int i = -1; i >= m_octaveTransposition; i--)
				sb.append(",");
		}
		if (isTied())
			sb.append("-");
		return sb.toString();
	}
	
}

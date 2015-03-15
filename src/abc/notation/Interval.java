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

//Interesting links:
//http://en.wikipedia.org/wiki/Interval_(music)
//http://en.wikipedia.org/wiki/Quarter_tone#Interval_size_in_equal_temperament
//http://en.wikipedia.org/wiki/Neutral_interval
//http://www.tierceron.com/diss/intro/intro.pdf

/**
 * In music theory, the term interval describes the relationship
 * between the pitches of two notes.
 */
public class Interval implements Cloneable, Serializable {

	/**
	 * An interval may be inverted, by raising the lower pitch an octave, or
	 * lowering the upper pitch an octave (though it is less usual to speak of
	 * inverting unisons or octaves).
	 * 
	 * For example, the fourth between a lower C and a higher F may be inverted
	 * to make a fifth, with a lower F and a higher C.
	 * 
	 * The inversion of a major interval is a minor interval (and vice versa);
	 * the inversion of a perfect interval is also perfect; the inversion of an
	 * augmented interval is a diminished interval (and vice versa); and the
	 * inversion of a double augmented interval is a double diminished interval
	 * (and vice versa).
	 * 
	 * Since compound intervals are larger than an octave, to be inverted one
	 * note must be moved two octaves or both notes must be moved an octave,
	 * with the result being that, "the inversion of any compound interval is
	 * always the same as [the inversion] of the simple interval from which it
	 * is compounded.
	 * 
	 * @param i
	 * @return a new Interval object.
	 */
	public static Interval invert(Interval i) {
		byte iLabel = (byte) (OCTAVE - (i.getLabel()%OCTAVE));
		byte iQuality = (byte) -i.getQuality();
		byte iOrder = (byte) -i.getDirection();
		Interval ret = new Interval(iLabel, iQuality, iOrder);
		if (i.m_microtonalOffset != 0.0f)
			ret.m_microtonalOffset = -i.m_microtonalOffset;
		return ret;
	}
	
	static private String label2string(byte label) {
		String ret = "";
		switch(label) {
		case UNISON: ret += "unison"; break;
		case SECOND: ret += "second"; break;
		case THIRD: ret += "third"; break;
		case FOURTH: ret += "fourth"; break;
		case FIFTH: ret += "fifth"; break;
		case SIXTH: ret += "sixth"; break;
		case SEVENTH: ret += "seventh"; break;
		case OCTAVE: ret += "octave"; break;
		case NINTH: ret += "ninth"; break;
		case TENTH: ret += "tenth"; break;
		case ELEVENTH: ret += "eleventh"; break;
		case TWELFTH: ret += "twelfth"; break;
		case THIRTEENTH: ret += "thirteenth"; break;
		default:
			int octave = (int) Math.floor(label / OCTAVE);
			ret += octave+" octave";
			if (octave > 1)
				ret += "s";
			byte newLabel = (byte) (label % OCTAVE);
			if (newLabel > UNISON)
				ret += " and a "+label2string(newLabel);
		}
		return ret;
	}
	/**
	 * Reverse the order of an interval. if i.getOrder() is upward,
	 * return a downward interval, and vice-versa.
	 * 
	 * A fifth stay a fifth.
	 * @param i
	 * @return a new Interval object.
	 */
	public static Interval reverseOrder(Interval i) {
		return new Interval(i.getLabel(), i.getQuality(), (byte) -i.getDirection());
	}
	private static final long serialVersionUID = -2402929528152000306L;
	//
	//labels
	//
	public static final byte UNISON = 0;
	public static final byte SECOND = 1;
	public static final byte THIRD = 2;
	public static final byte FOURTH = 3;
	public static final byte FIFTH = 4;
	public static final byte SIXTH = 5;
	public static final byte SEVENTH = 6;
	public static final byte OCTAVE = 7;
	public static final byte NINTH = OCTAVE + SECOND;
	public static final byte TENTH = OCTAVE + THIRD;

	public static final byte ELEVENTH = OCTAVE + FOURTH;
	public static final byte TWELFTH = OCTAVE + FIFTH;
	public static final byte THIRTEENTH = OCTAVE + SIXTH;
	//Intervals larger than a thirteenth seldom need to be spoken
	//of, most often being referred to by their compound names,
	//for example "two octaves plus a fifth" rather than "a 19th".
	//quality
	/** Quadruple diminished quality, quite improbable but
	 * mathematically possible */
	public static final byte QUADRUPLE_DIMINISHED = -50;
	/** Half-way between triple diminished and quadruple diminshed,
	 * for microtonal 24-TET scale */
	public static final byte SUPER_TRIPLE_DIMINISHED = -45;
	/** Triple diminished quality, quite improbable but
	 * mathematically possible */
	public static final byte TRIPLE_DIMINISHED = -40;
	/** Half-way between double diminished and triple diminshed,
	 * for microtonal 24-TET scale */
	public static final byte SUPER_DOUBLE_DIMINISHED = -35;
	/** Double diminished quality */
	public static final byte DOUBLE_DIMINISHED = -30;
	/** Half-way between diminished and double diminshed, for microtonal 24-TET scale */
	public static final byte SUPER_DIMINISHED = -25;
	/** Diminished quality */
	public static final byte DIMINISHED = -20;
	/** Half-way between minor and diminished, for microtonal 24-TET scale */
	public static final byte SUBMINOR = -15;
	/** Minor quality, apply to second, third, sixth and seventh */
	public static final byte MINOR = -10;
	/** Half-way between major and minor, for microtonal 24-TET scale */
	public static final byte NEUTRAL = -5;
	/** Half-way between diminished and perfect, for fourth and fifth in
	 * microtonal 24-TET scale */
	public static final byte SUB = -5;
	/** Perfect quality, note that only unison, fourth,
	 * fifth and octave can be perfect */
	public static final byte PERFECT = 0;
	/** Half-way between perfect and augmented, for fourth and fifth in
	 * microtonal 24-TET scale */
	public static final byte SUPER = 5;
	/** Major quality, apply to second, third, sixth and seventh */
	public static final byte MAJOR = 10;
	/** Half-way between major and augmented, for microtonal 24-TET scale */
	public static final byte SUPER_MAJOR = 15;
	/** Augmented quality */
	public static final byte AUGMENTED = 20;
	/** Half-way between augmented and double augmented, for microtonal 24-TET scale */
	public static final byte SUPER_AUGMENTED = 25;
	/** Double augmented quality, e.g. Eb - B# */
	public static final byte DOUBLE_AUGMENTED = 30;
	/** Half-way between double augmented and triple augmented, for microtonal 24-TET scale */
	public static final byte SUPER_DOUBLE_AUGMENTED = 35;
	
	/** Triple augmented quality, quite improbable but
	 * mathematically possible. e.g. Eb - B##, or Ebb - B# */
	public static final byte TRIPLE_AUGMENTED = 40;
	/** Half-way between triple augmented and quadruple augmented, for microtonal 24-TET scale */
	public static final byte SUPER_TRIPLE_AUGMENTED = 45;

	//
	//and now some constants that may be useful...
	//
	
	/** Quadruple augmented quality, quite improbable but
	 * mathematically possible. e.g. Ebb - B## */
	public static final byte QUADRUPLE_AUGMENTED = 50;
	//
	//order (or direction)
	//
	/** upward direction (order): the 2nd note is higher than the first */
	public static final byte UPWARD = 1;
	/** downward direction (order): the 2nd note is lower than the first */
	public static final byte DOWNWARD = -1;
	/** Diminished unison, e.g. C-Cb */
	public static final Interval DIMINISHED_UNISON = new Interval(UNISON, DIMINISHED);
	/** Perfect unison, two notes at equal height */
	public static final Interval PERFECT_UNISON = new Interval(UNISON, PERFECT);
	/** Augmented unison, e.g. C-C#, similar to {@link #DIMINISHED_SECOND} */
	public static final Interval AUGMENTED_UNISON = new Interval(UNISON, AUGMENTED);
	/** Diminished second, e.g. C-Dbb, similar to {@link #PERFECT_UNISON} */
	public static final Interval DIMINISHED_SECOND = new Interval(SECOND, DIMINISHED); //equals unison
	/** Minor second, e.g. C-Db, similar to {@link #AUGMENTED_UNISON} */
	public static final Interval MINOR_SECOND = new Interval(SECOND, MINOR);
	/** Major second, e.g. C-D */
	public static final Interval MAJOR_SECOND = new Interval(SECOND, MAJOR);
	/** Augmented second, e.g. C-D#, similar to {@link #MINOR_THIRD} */
	public static final Interval AUGMENTED_SECOND = new Interval(SECOND, AUGMENTED);
	/** Diminished second, e.g. C-Ebb, similar to {@link #MAJOR_SECOND} */
	public static final Interval DIMINISHED_THIRD = new Interval(THIRD, DIMINISHED);
	/** Minor third, e.g. C-Eb */
	public static final Interval MINOR_THIRD = new Interval(THIRD, MINOR);
	/** Major third, e.g. C-E */
	public static final Interval MAJOR_THIRD = new Interval(THIRD, MAJOR);
	/** Augmented third, e.g. C-E#, similar to {@link #PERFECT_FOURTH} */
	public static final Interval AUGMENTED_THIRD = new Interval(THIRD, AUGMENTED);
	/** Diminished fourth, e.g. C-Fb, similar to {@link #MAJOR_THIRD} */
	public static final Interval DIMINISHED_FOURTH = new Interval(FOURTH, DIMINISHED);
	/** Perfect fourth, e.g. C-F */
	public static final Interval PERFECT_FOURTH = new Interval(FOURTH, PERFECT);
	/** Augmented fourth, e.g. C-F#, similar to {@link #DIMINISHED_FIFTH} */
	public static final Interval AUGMENTED_FOURTH = new Interval(FOURTH, AUGMENTED);
	/** Diminished fifth, e.g. C-Gb, similar to {@link #AUGMENTED_FOURTH} */
	public static final Interval DIMINISHED_FIFTH = new Interval(FIFTH, DIMINISHED);
	/** Perfect fifth, e.g. C-G */
	public static final Interval PERFECT_FIFTH = new Interval(FIFTH, PERFECT);
	/** Augmented fifth, e.g. C-G#, similar to {@link #MINOR_SIXTH} */
	public static final Interval AUGMENTED_FIFTH = new Interval(FIFTH, AUGMENTED);
	/** Diminished sixth, e.g. C-Abb, similar to {@link #PERFECT_FIFTH} */
	public static final Interval DIMINISHED_SIXTH = new Interval(SIXTH, DIMINISHED);
	/** Minor sixth, e.g. C-Ab, similar to {@link #AUGMENTED_FIFTH} */
	public static final Interval MINOR_SIXTH = new Interval(SIXTH, MINOR);
	/** Major sixth, e.g. C-A */
	public static final Interval MAJOR_SIXTH = new Interval(SIXTH, MAJOR);
	/** Augmented sixth, e.g. C-A#, similar to {@link #MINOR_SEVENTH} */
	public static final Interval AUGMENTED_SIXTH = new Interval(SIXTH, AUGMENTED);
	/** Diminished seventh, e.g. C-Bbb, similar to {@link #MAJOR_SIXTH} */
	public static final Interval DIMINISHED_SEVENTH = new Interval(SEVENTH, DIMINISHED);
	/** Minor seventh, e.g. C-Bb, similar to {@link #AUGMENTED_SIXTH} */
	public static final Interval MINOR_SEVENTH = new Interval(SEVENTH, MINOR);
	/** Major seventh, e.g. C-B */
	public static final Interval MAJOR_SEVENTH = new Interval(SEVENTH, MAJOR);
	/** Augmented seventh, e.g. C-B#, similar to {@link #PERFECT_OCTAVE} */
	public static final Interval AUGMENTED_SEVENTH = new Interval(SEVENTH, AUGMENTED);
	
	/** Diminished octave, e.g. C-cb */
	public static final Interval DIMINISHED_OCTAVE = new Interval(OCTAVE, DIMINISHED);
	
	/** Perfect octave, e.g. C-c */
	public static final Interval PERFECT_OCTAVE = new Interval(OCTAVE, PERFECT);

	/** Augmented octave, e.g. C-c# */
	public static final Interval AUGMENTED_OCTAVE = new Interval(OCTAVE, AUGMENTED);
	private byte m_direction = UPWARD;
	/** height or degree offset */
	private byte m_label = UNISON;
	private float m_microtonalOffset = 0.0f;

	private byte m_quality = PERFECT;
	
	/**
	 * Creates an upward interval
	 * 
	 * @param label
	 *            the strict height offset, or degree offset 0 for unison, 1 for
	 *            second, 2 for third... 8 for octave
	 * @param quality
	 *            one of {@link #PERFECT}, {@link #MINOR}, {@link #MAJOR},
	 *            {@link #DIMINISHED}, {@link #AUGMENTED}
	 * @throws IllegalArgumentException
	 *             If this interval can't be perfect and qualification is
	 *             PERFECT, or this interval can't be major/minor and
	 *             qualification is MAJOR or MINOR
	 */
	public Interval(byte label, byte quality)
		throws IllegalArgumentException {
		this(label, quality, UPWARD);
	}
	
	/**
	 * Creates an interval
	 * @param label
	 *            the strict height offset, or degree offset 0 for unison, 1 for
	 *            second, 2 for third... 8 for octave
	 * @param quality
	 *            one of {@link #PERFECT}, {@link #MINOR}, {@link #MAJOR},
	 *            {@link #DIMINISHED}, {@link #AUGMENTED}
	 * @param order
	 *            one of {@link #UPWARD} or {@link #DOWNWARD}
	 * @throws IllegalArgumentException
	 *             If this interval can't be perfect and qualification is
	 *             PERFECT, or this interval can't be major/minor and
	 *             qualification is MAJOR or MINOR
	 */
	public Interval(byte label, byte quality, byte order)
		throws IllegalArgumentException {
		setLabel(label);
		setQuality(quality);
		setDirection(order);
	}
	
	/**
	 * Create an interval calculated between two Note.
	 * 
	 * The parameter key, if specified, helps to determinate the
	 * accidentals of the notes if n1.getAccidental() equals
	 * Accidental.NONE (e.g. in D major, F with accidental NONE
	 * is understood as F sharp).
	 * 
	 * If n1 is lower than n2, interval order is upward,
	 * if n1 is higher than n2, interval order is downward.
	 * @param n1
	 * @param n2
	 * @param key can be <TT>null</TT>
	 */
	public Interval(Note n1, Note n2, KeySignature key) {
		Note low, high;
		if (n2.getHeight() < n1.getHeight()) {
		//if (n2.isLowerThan(n1, key)) {
			low = n2; high = n1;
			setDirection(DOWNWARD);
		} else {
			low = n1; high = n2;
			setDirection(UPWARD);
		}
		int deltaHeight = 0, deltaOctave = 0, deltaQuartertones = 0;
		m_microtonalOffset = high.getAccidental(key).getMicrotonalOffset()
			- low.getAccidental(key).getMicrotonalOffset();
		deltaQuartertones = (int) (2 * Math.abs(high.getMidiLikeHeight(key) - low.getMidiLikeHeight(key))
			+ (2 * m_microtonalOffset));
		
		//delta strict height
		byte[] notes = new byte[] { Note.C, Note.D, Note.E, Note.F, Note.G,
				Note.A, Note.B };
		int height1 = 0, height2 = 0;
		for (int i = 0; i < notes.length; i++) {
			if (notes[i] == low.getStrictHeight()) height1 = i;
			if (notes[i] == high.getStrictHeight()) height2 = i;
		}
		if (height2 < height1) height2 += 7;
		deltaHeight = height2 - height1;
		
		//delta octave
		deltaOctave = (int) Math.floor(deltaQuartertones / 24);
		if (height1 == height2)
			deltaOctave = high.getOctaveTransposition() - low.getOctaveTransposition();
		setLabel((byte) (deltaHeight + deltaOctave*OCTAVE));
		
		//delta between the wanted interval and major/perfect interval
		int delta = deltaQuartertones - getQuartertones();
		boolean apf = allowPerfectQuality();
		switch (delta) {
		case -8: //quadruple_dim or triple_dim (very rare!)
			setQuality(apf ? QUADRUPLE_DIMINISHED : TRIPLE_DIMINISHED);
			break;
		case -7:
			setQuality(apf ? SUPER_TRIPLE_DIMINISHED : SUPER_DOUBLE_DIMINISHED);
			break;
		case -6: //triple_dim or double_dim (rare)
			setQuality(apf ? TRIPLE_DIMINISHED : DOUBLE_DIMINISHED);
			break;
		case -5:
			setQuality(apf ? SUPER_DOUBLE_DIMINISHED : SUPER_DIMINISHED);
			break;
		case -4: //double_dim or diminished
			setQuality(apf ? DOUBLE_DIMINISHED : DIMINISHED);
			break;
		case -3:
			setQuality(apf ? SUPER_DIMINISHED : SUBMINOR);
			break;
		case -2: //diminished or minor
			setQuality(apf ? DIMINISHED : MINOR);
			break;
		case -1:
			setQuality(apf ? SUB : NEUTRAL);
			break;
		case 0:
			setQuality(apf ? PERFECT : MAJOR);
			break;
		case 1:
			setQuality(apf ? SUPER : SUPER_MAJOR);
			break;
		case 2: //augmented
			setQuality(AUGMENTED);
			break;
		case 3:
			setQuality(SUPER_AUGMENTED);
			break;
		case 4: //double augmented
			setQuality(DOUBLE_AUGMENTED);
			break;
		case 5:
			setQuality(SUPER_DOUBLE_AUGMENTED);
			break;
		case 6: //triple augmented
			setQuality(TRIPLE_AUGMENTED);
			break;
		case 7:
			setQuality(SUPER_TRIPLE_AUGMENTED);
			break;
		case 8: //quadruple augmented
			setQuality(QUADRUPLE_AUGMENTED);
			break;
		default:
			System.err.println("Could not compute the qualification of the interval between "
					+low+" and "+high+" : label="+m_label+", delta="+delta);
		}
	}
	
	/** Only unison, fourth and fifth (+ <I>n</I>*octaves) can be perfect */
	private boolean allowPerfectQuality() {
		int i = m_label % OCTAVE;
		return (i == UNISON) || (i == FOURTH) || (i == FIFTH);
	}
	
	/**
	 * Compute the second note of this interval based on first
	 * note n1.
	 * @param n1
	 * @return a clone of n1 which pitch is changed
	 */
	public Note calculateSecondNote(Note n1) {
		return calculateSecondNote(n1, null);
	}
	
	/**
	 * Compute the second note of this interval based on first
	 * note n1.
	 * 
	 * This version is more accurate, if n1 has no accidental,
	 * the key signature tells which accidental it has.
	 * The returned note will have no accidental if its accidental
	 * is in the key signature.
	 * @param n1
	 * @param key can be <TT>null</TT>
	 * @return a clone of n1 which pitch is changed
	 */
	//TODO throw NoteHeightException if 2nd note is to low/high
	public Note calculateSecondNote(Note n1, KeySignature key) {
		if (n1.isRest()) {
			try {
				return (Note) n1.clone();
			} catch (CloneNotSupportedException never) {
				never.printStackTrace();
			}
		}
		byte[] notes = new byte[] { Note.C, Note.D, Note.E, Note.F, Note.G,
				Note.A, Note.B };
		int height1 = n1.getStrictHeight();
		int index = 0;
		for (int i = 0; i < notes.length; i++) {
			if (notes[i] == height1) {
				index = i;
				break;
			}
		}
		int steps = getLabel();
		int octaveTransp = n1.getOctaveTransposition();
		while (steps > 0) {
			int oldIndex = index;
			index = (7 + index + m_direction) % OCTAVE;
			if ((oldIndex == 6) && (index == 0)) {
				//upward one octave
				octaveTransp += 1;
			} else if ((oldIndex == 0) && (index == 6)) {
				//downward one octave
				octaveTransp -= 1;
			}
			steps--;
		}
		Note n2;
		try {
			n2 = (Note) n1.clone();
		} catch (CloneNotSupportedException never) {
			never.printStackTrace();
			n2 = null;
		}
		n2.setHeight(notes[index]);
		n2.setOctaveTransposition((byte) octaveTransp);
		n2.setAccidental(Accidental.NATURAL);
		
		//and now compute accidentals!
		int deltaS = n1.getMidiLikeHeight(key)
				+ getDirection() * (getSemitones())
				- n2.getMidiLikeHeight(key);
		float deltaQ = n1.getMidiLikeMicrotonalHeight(key)
				+ (getDirection() * 0.5f * getQuartertones())
				- n2.getMidiLikeMicrotonalHeight(key);
		if (deltaS == 0 && deltaQ == 0.0) {
			//do nothing
		} else {
			n2 = Note.transpose(n2, 0, deltaQ);
			boolean found = false;
			int[] passes;
			if ((isUpward() && this.m_quality >= 0)
					|| (isDownward() && this.m_quality < 0))
				passes = new int[] {0,1,-1,2,-2};
			else
				passes = new int[] {0,-1,1,-2,2};
			for (int i = 0; i < passes.length; i++) {
				for (Accidental acc : Accidental.getAll()) {
					Note n3 = Note.createEnharmonic(n2, new Accidental[] {acc});
					if (n3.getStrictHeight() == notes[(notes.length + index + passes[i]) % notes.length]) {
						n2 = n3;
						found = true;//We got a not with proper distance and accidental
						break;
					}
				}
				if (found)
					break;
			}
			if (!found && key != null) {
				n2 = Note.createEnharmonic(n2, key);
			}
//			Accidental[] acc = calculateSecondNoteAccidental(n1, key);
//			n2 = Note.createEnharmonic(n2, acc);
		}
		if (key != null) {
			if (n2.getAccidental().equals(key.getAccidentalFor(n2.getStrictHeight())))
				n2.setAccidental(Accidental.NONE);
		}
		return n2;
	}
	
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Two Interval are equals if they have the same label,
	 * quality and direction
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Interval))
			return super.equals(o);
		else {
			Interval interval = (Interval) o;
			return (interval.getLabel() == getLabel())
				&& (interval.getQuality() == getQuality())
				&& (interval.getDirection() == getDirection())
				&& (interval.getMicrotonalOffset() == getMicrotonalOffset());
		}
	}

	public float get100thOfSemitone() {
		return (getSemitones() + getMicrotonalOffset()) * 100.0f;
	}

	/**
	 * Get the interval value in 100th of semitone.
	 * @return 200 for major second, 700 for perfect fifth, 1200 for perfect octave
	 */
	public int getCents() {
		return getQuartertones() * 50;
	}

	/**
	 * Returns the direction (or order) of the interval
	 * @return one of {@link #UPWARD} or {@link #DOWNWARD}
	 */
	public byte getDirection() {
		return m_direction;
	}
	
	/**
	 * Returns the label (unison, second, third...)
	 * 
	 * You can test the label as following :
	 * <pre><code>if (myInterval.getLabel() <= Interval.FIFTH) {
	 * ...here is an unisson, second, third, fourth or fifth
	 * } else {
	 * ...here is a greater interval
	 * }</code></pre>
	 * This doesn't take account quality
	 * 
	 * @return one of {@link #UNISON}, {@link #SECOND}, {@link #THIRD},
	 *         {@link #FOURTH}, {@link #FIFTH}, {@link #SIXTH},
	 *         {@link #SEVENTH} + <I>n</i>*{@link #OCTAVE}
	 */
	public byte getLabel() {
		return m_label;
	}
	
	public float getMicrotonalOffset() {
		return m_microtonalOffset;
	}

	/**
	 * If interval is compound, returns the number of octave
	 */
	public int getOctaveNumber() {
		if (isCompound() || (getLabel() == OCTAVE)) {
			return (int) Math.floor(getLabel() / OCTAVE);
		}
		return 0;
	}

	/**
	 * Returns the quality of the interval (minor, major, perfect...)
	 * @return one of {@link #PERFECT}, {@link #MINOR}, {@link #MAJOR},
	 *         {@link #DIMINISHED}, {@link #AUGMENTED},
	 *         {@link #DOUBLE_DIMINISHED}, {@link #DOUBLE_AUGMENTED}...
	 */
	public byte getQuality() {
		return m_quality;
	}

	public int getQuartertones() {
		int qt = 0;
		switch (getLabel() % OCTAVE) {
		case UNISON: qt = 0; break; //unison
		case SECOND: qt = 4; break; //major 2nd
		case THIRD: qt = 8; break; //major 3rd
		case FOURTH: qt = 10; break; //perfect 4th
		case FIFTH: qt = 14; break; //perfect 5th
		case SIXTH: qt = 18; break; //major 6th
		case SEVENTH: qt = 22; break; //major 7th
		}
		qt += 24 * getOctaveNumber();
		boolean apf = allowPerfectQuality();
		switch (getQuality()) {
		case QUADRUPLE_DIMINISHED:
			return qt - (apf ? 8 : 10);
		case SUPER_TRIPLE_DIMINISHED:
			return qt - (apf ? 7 : 9);
		case TRIPLE_DIMINISHED:
			return qt - (apf ? 6 : 8);
		case SUPER_DOUBLE_DIMINISHED:
			return qt - (apf ? 5 : 7);
		case DOUBLE_DIMINISHED:
			return qt - (apf ? 4 : 6);
		case SUPER_DIMINISHED:
			return qt - (apf ? 3 : 5);
		case DIMINISHED:
			return qt - (apf ? 2 : 4);
		case SUBMINOR:
			return qt - (apf ? 1 : 3);
		case MINOR:
			return qt - 2;
		case NEUTRAL: //= case SUB:
			return qt - 1;
		case PERFECT:
		case MAJOR:
			return qt;
		case SUPER:
		case SUPER_MAJOR:
			return qt + 1;
		case AUGMENTED:
			return qt + 2;
		case SUPER_AUGMENTED:
			return qt + 3;
		case DOUBLE_AUGMENTED:
			return qt + 4;
		case SUPER_DOUBLE_AUGMENTED:
			return qt + 5;
		case TRIPLE_AUGMENTED:
			return qt + 6;
		case SUPER_TRIPLE_AUGMENTED:
			return qt + 7;
		case QUADRUPLE_AUGMENTED:
			return qt + 8;
		default:
			return qt;
		}
	}

	/**
	 * Return the number of semitones between 2 notes spaced by
	 * this Interval
	 */
	public int getSemitones() {
		int semitones = 0;
		switch (getLabel() % OCTAVE) {
		case UNISON: semitones = 0; break; //unison
		case SECOND: semitones = 2; break; //major 2nd
		case THIRD: semitones = 4; break; //major 3rd
		case FOURTH: semitones = 5; break; //perfect 4th
		case FIFTH: semitones = 7; break; //perfect 5th
		case SIXTH: semitones = 9; break; //major 6th
		case SEVENTH: semitones = 11; break; //major 7th
		}
		semitones += 12 * getOctaveNumber();
		boolean apf = allowPerfectQuality();
		switch (getQuality()) {
		case QUADRUPLE_DIMINISHED:
		case SUPER_TRIPLE_DIMINISHED:
			return semitones - (apf ? 4 : 5);
		case TRIPLE_DIMINISHED:
		case SUPER_DOUBLE_DIMINISHED:
			return semitones - (apf ? 3 : 4);
		case DOUBLE_DIMINISHED:
		case SUPER_DIMINISHED:
			return semitones - (apf ? 2 : 3);
		case DIMINISHED:
		case SUBMINOR:
			return semitones - (apf ? 1 : 2);
		case MINOR:
		case NEUTRAL: //= case SUB:
			return semitones - 1;
		case PERFECT:
		case MAJOR:
			return semitones; //do not change pitch for major and perfect
		case SUPER:
		case AUGMENTED:
		case SUPER_MAJOR:
			return semitones + 1;
		case SUPER_AUGMENTED:
		case DOUBLE_AUGMENTED:
			return semitones + 2;
		case SUPER_DOUBLE_AUGMENTED:
		case TRIPLE_AUGMENTED:
			return semitones + 3;
		case SUPER_TRIPLE_AUGMENTED:
		case QUADRUPLE_AUGMENTED:
			return semitones + 4;
		default:
			return semitones;
		}
	}
	
	public boolean isAugmented() {
		return (m_quality == AUGMENTED)
			|| (m_quality == DOUBLE_AUGMENTED)
			|| (m_quality == TRIPLE_AUGMENTED)
			|| (m_quality == QUADRUPLE_AUGMENTED);
	}
	
	/**
	 * Intervals larger than an octave are called compound intervals; for
	 * example, a tenth is known as a compound third. The quality of the
	 * compound interval is determined by the quality of the interval on which
	 * it is based. For example, a perfect eleventh is the same as a compound
	 * perfect fourth.
	 * 
	 * Intervals larger than a thirteenth seldom need to be spoken of, most
	 * often being referred to by their compound names, for example "two octaves
	 * plus a fifth" rather than "a 19th".
	 */
	public boolean isCompound() {
		return getLabel() > OCTAVE;
	}
	
	public boolean isDiminished() {
		return (m_quality == DIMINISHED)
			|| (m_quality == DOUBLE_DIMINISHED)
			|| (m_quality == TRIPLE_DIMINISHED)
			|| (m_quality == QUADRUPLE_DIMINISHED);
	}
	public boolean isDownward() {
		return getDirection() == DOWNWARD;
	}
	
	/**
	 * Returns <TT>true</TT> if interval is greater than this one
	 * 
	 * @param interval
	 */
	public boolean isGreaterThan(Interval interval) {
		return getCents() > interval.getCents();
	}
	
	public boolean isPerfectUnisonOrOctave() {
		return (getQuality() == PERFECT) && ((getLabel() % OCTAVE) == UNISON);
	}
	
	/**
	 * Two Interval are similar if they have the same semitone offset
	 * 
	 * e.g. augmented fifth (C-G#) = diminished sixth (C-Ab)
	 */
	public boolean isSimilarTo(Interval interval) {
		return getCents() == interval.getCents();
	}
	
	/**
	 * Returns <TT>true</TT> if interval is smaller than this one
	 * 
	 * @param interval
	 */
	public boolean isSmallerThan(Interval interval) {
		return getCents() < interval.getCents();
	}

	/** Super augmented = a quarter tone more than augmented (or double, triple) */
	public boolean isSuperAugmented() {
		return (m_quality == SUPER_AUGMENTED)
				|| (m_quality == SUPER_DOUBLE_AUGMENTED)
				|| (m_quality == SUPER_TRIPLE_AUGMENTED);
	}
	
	/**
	 * Super diminished = a quarter tone more than diminished (or double,
	 * triple)
	 */
	public boolean isSuperDiminished() {
		return (m_quality == SUPER_DIMINISHED)
				|| (m_quality == SUPER_DOUBLE_DIMINISHED)
				|| (m_quality == SUPER_TRIPLE_DIMINISHED);
	}
	
	public boolean isUpward() {
		return getDirection() == UPWARD;
	}
	
	/**
	 * Sets the direction (or order) of the interval
	 * @param d
	 *            one of {@link #UPWARD} or {@link #DOWNWARD}
	 */
	private void setDirection(byte d) {
		this.m_direction = d;
	}

	/**
	 * Sets the label
	 * @param i
	 *            one of {@link #UNISON}, {@link #SECOND}, {@link #THIRD},
	 *            {@link #FOURTH}, {@link #FIFTH}, {@link #SIXTH},
	 *            {@link #SEVENTH} + <I>n</i>*{@link #OCTAVE}
	 */
	private void setLabel(byte i) {
		m_label = i;
	}
	
	/**
	 * @param i
	 *            one of {@link #PERFECT}, {@link #MINOR}, {@link #MAJOR},
	 *            {@link #DIMINISHED}, {@link #AUGMENTED},
	 *            {@link #DOUBLE_DIMINISHED}, {@link #DOUBLE_AUGMENTED}
	 */
	private void setQuality(byte i) throws IllegalArgumentException {
		boolean apf = allowPerfectQuality();
		if (apf && ((i == MINOR) || (i == MAJOR) || (i == SUBMINOR)
		// (i == NEUTRAL) exclude because = SUB
				|| (i == SUPER_MAJOR)))
			throw new IllegalArgumentException(
					"Unison, fourth, fifth and octave can't be major or minor");
		else if (!apf && (i == PERFECT))
			throw new IllegalArgumentException(
					"Only unison, fourth, fifth and octave can be perfect");
		else if (!apf && (i < TRIPLE_DIMINISHED))
			throw new IllegalArgumentException(
					"Only unison, fourth, fifth and octave can be quadruple diminished or super triple diminished");
		this.m_quality = i;
	}
	
	public String toString() {
		String ret = "Interval: ";
		switch (getQuality()) {
		case QUADRUPLE_AUGMENTED:
			ret += "quadruple augmented";
			break;
		case SUPER_TRIPLE_AUGMENTED:
			ret += "super triple augmented";
			break;
		case TRIPLE_AUGMENTED:
			ret += "triple augmented";
			break;
		case SUPER_DOUBLE_AUGMENTED:
			ret += "super double augmented";
			break;
		case DOUBLE_AUGMENTED:
			ret += "double augmented";
			break;
		case SUPER_AUGMENTED:
			ret += "super augmented";
			break;
		case AUGMENTED:
			ret += "augmented";
			break;
		case SUPER_MAJOR:
			ret += "super major";
			break;
		case MAJOR:
			ret += "major";
			break;
		case SUPER:
			ret += "super";
			break;
		case PERFECT:
			ret += "perfect";
			break;
		case NEUTRAL: // = case SUB for perfectable label
			ret += allowPerfectQuality() ? "sub" : "neutral";
			break;
		case MINOR:
			ret += "minor";
			break;
		case SUBMINOR:
			ret += "subminor";
			break;
		case DIMINISHED:
			ret += "diminished";
			break;
		case SUPER_DIMINISHED:
			ret += "super diminished";
			break;
		case DOUBLE_DIMINISHED:
			ret += "double diminished";
			break;
		case SUPER_DOUBLE_DIMINISHED:
			ret += "super duoble diminished";
			break;
		case TRIPLE_DIMINISHED:
			ret += "triple diminished";
			break;
		case SUPER_TRIPLE_DIMINISHED:
			ret += "super triple diminished";
			break;
		case QUADRUPLE_DIMINISHED:
			ret += "quadruple diminished";
			break;
		default:
			ret += "<UNKNOWN QUALITY>";
			break;
		}
		
		// label
		ret += " " + label2string(getLabel());
		// direction
		ret += isUpward() ? " upward" : " downward";
		return ret;
	}
}

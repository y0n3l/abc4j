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

/** Accidentals */
public class Accidental implements Cloneable, Serializable {

	private static final long serialVersionUID = -50238873169452062L;

	/** The <TT>DOUBLE FLAT</TT> ccidental type : bb */
	private static final float _DOUBLE_FLAT = -2.0f;

	/** The <TT>DOUBLE SHARP</TT> accidental type : x */
	private static final float _DOUBLE_SHARP = 2.0f;

	/** The <TT>FLAT</TT> accidental type : b. */
	private static final float _FLAT = -1.0f;

	/** The <TT>NATURAL</TT> accidental type. */
	private static final float _NATURAL = 0.0f;

	/** The <TT>NONE</TT> accidental type. */
	private static final float _NONE = 10.0f;

	/** The <TT>SHARP</TT> accidental type : # */
	private static final float _SHARP = 1.0f;

	public static final Accidental DOUBLE_FLAT = new Accidental(_DOUBLE_FLAT);

	public static final Accidental DOUBLE_SHARP = new Accidental(_DOUBLE_SHARP);

	public static final Accidental FLAT = new Accidental(_FLAT); // -1f

	public static final Accidental FLAT_AND_A_HALF = new Accidental(-1.5f);

	public static final Accidental HALF_FLAT = new Accidental(-0.5f);

	public static final Accidental HALF_SHARP = new Accidental(0.5f);

	public static final Accidental NATURAL = new Accidental(_NATURAL);

	public static final Accidental NONE = new Accidental();

	public static final Accidental SHARP = new Accidental(_SHARP);

	public static final Accidental SHARP_AND_A_HALF = new Accidental(1.5f);

	public static final char UNICODE_SHARP = '\u266F', UNICODE_FLAT = '\u266D',
			UNICODE_NATURAL = '\u266E';
	public static final String UNICODE_DOUBLE_SHARP = "\uD834\uDD2A", //U+1D12A
			UNICODE_DOUBLE_FLAT = "\uD834\uDD2B", //U+1D12B
			UNICODE_HALF_FLAT = "\uD834\uDD2C", //U+1D12C
			UNICODE_FLAT_AND_A_HALF = "\uD834\uDD2D", //U+1D12D
			UNICODE_HALF_SHARP = "\uD834\uDD31", //U+1D131
			UNICODE_SHARP_AND_A_HALF = "\uD834\uDD30"//U+1D130
				;

	/**
	 * Convert a string to an accidental. Understand ABC ^ and _, chord names #,
	 * b and unicode char
	 */
	public static Accidental convertToAccidental(String accidental)
			throws IllegalArgumentException {
		if (accidental == null)
			return NONE;
		else if (accidental.equals("^") || accidental.equals("#")
				|| accidental.equals(UNICODE_SHARP + ""))
			return SHARP;
		else if (accidental.equals("_") || accidental.equals("b")
				|| accidental.equals(UNICODE_FLAT + ""))
			return FLAT;
		else if (accidental.equals("=") || accidental.equals(UNICODE_NATURAL + ""))
			return NATURAL;
		else if (accidental.equals("^^") || accidental.equals("##")
				|| accidental.equals(UNICODE_SHARP + "" + UNICODE_SHARP))
			return DOUBLE_SHARP;
		else if (accidental.equals("__") || accidental.equals("bb")
				|| accidental.equals(UNICODE_FLAT + "" + UNICODE_FLAT))
			return DOUBLE_FLAT;
		else if (accidental.equals("^/"))
			return HALF_SHARP;
		else if (accidental.equals("_/"))
			return HALF_FLAT;
		else if (accidental.equals("^3/") || accidental.equals("^3/2"))
			return SHARP_AND_A_HALF;
		else if (accidental.equals("_3/") || accidental.equals("_3/2"))
			return FLAT_AND_A_HALF;
		else
			throw new IllegalArgumentException(accidental
					+ " is not a valid accidental");
	}
	
	public static Collection<Accidental> getAll() {
		ArrayList<Accidental> ret = new ArrayList<Accidental>();
		ret.add(DOUBLE_FLAT);
		ret.add(FLAT_AND_A_HALF);
		ret.add(FLAT);
		ret.add(HALF_FLAT);
		ret.add(NATURAL);
		ret.add(HALF_SHARP);
		ret.add(SHARP);
		ret.add(SHARP_AND_A_HALF);
		ret.add(DOUBLE_SHARP);
		return ret;
	}

	private Fraction m_fraction = null;

	private float m_value = _NONE;

	public Accidental() {
	}

	public Accidental(float value) {
		setValue(value);
	}

	public Accidental(Fraction fraction) {
		m_fraction = fraction;
		setValue(fraction.floatValue());
	}

	public boolean equals(Object o) {
		if (o instanceof Accidental) {
			return ((Accidental) o).getValue() == getValue();
		} else
			return super.equals(o);
	}

	/**
	 * Returns the microtonal offset between this (microtonal) accidental and
	 * the nearest occidental semitone.
	 * 
	 * e.g. returns -0.5 for flat-and-half (-1.5), this is the offset from -1
	 * (flat). Returns 0.5 for half-flat (-0.5), this is the offset from -1
	 * (flat).
	 */
	public float getMicrotonalOffset() {
		if (isMicrotonal())
			return m_value - getNearestOccidentalValue();
		else
			return 0;
	}

	/**
	 * Return the nearest non microtonal semitone
	 * 
	 * e.g. flat (-1) for a half-flat or flat-and-half
	 */
	public byte getNearestOccidentalValue() {
		if (m_value == _NONE || m_value == _NATURAL)
			return 0;
		if ((m_value == _DOUBLE_FLAT) || (m_value == _DOUBLE_SHARP))
			return (byte) m_value;
		if (m_value <= _FLAT)
			return -1;// flat
		if (m_value >= _SHARP)
			return 1;// sharp
		return 0;
	}

	public float getValue() {
		if (m_value == _NONE)
			return 0;
		else
			return m_value;
	}

	/** Value is not equals to {@link #_NONE} */
	public boolean isDefined() {
		return m_value != _NONE;
	}

	public boolean isDoubleFlat() {
		return m_value == _DOUBLE_FLAT;
	}

	public boolean isDoubleSharp() {
		return m_value == _DOUBLE_SHARP;
	}

	public boolean isFlat() {
		return m_value == _FLAT;
	}

	/** Value is not defined, use the key accidental */
	public boolean isInTheKey() {
		return m_value == _NONE;
	}

	public boolean isMicrotonal() {
		return isDefined() && !isDoubleFlat() && !isFlat() && !isNatural()
				&& !isSharp() && !isDoubleSharp();
	}

	public boolean isMicrotonalFlat() {
		return isMicrotonal() && m_value < 0;
	}
	
	public boolean isMicrotonalLessThanFlat() {
		return isMicrotonal() && m_value > _FLAT && m_value < 0;
	}

	public boolean isMicrotonalMoreThanFlat() {
		return isMicrotonal() && m_value < _FLAT;
	}
	
	public boolean isMicrotonalSharp() {
		return isMicrotonal() && m_value > 0;
	}

	public boolean isMicrotonalLessThanSharp() {
		return isMicrotonal() && m_value > 0 && m_value < _SHARP;
	}
	
	public boolean isMicrotonalMoreThanSharp() {
		return isMicrotonal() && m_value > _SHARP;
	}
	
	public boolean isNatural() {
		return m_value == _NATURAL;
	}

	/** Value is not defined, use the key accidental */
	public boolean isNotDefined() {
		return isInTheKey();
	}

	public boolean isSharp() {
		return m_value == _SHARP;
	}

	private void setValue(float value) {
		if (((value >= _DOUBLE_FLAT) && (value <= _DOUBLE_SHARP))
				|| (value == _NONE))
			m_value = value;
		else
			m_value = _NONE;
	}
	
	public String toString() {
		if (m_value == _NONE)
			return "";
		if (m_value == _NATURAL)
			return "=";
		if (m_value == _FLAT)
			return "b";
		if (m_value == _SHARP)
			return "#";
		if (m_value == _DOUBLE_FLAT)
			return "bb";
		if (m_value == _DOUBLE_SHARP)
			return "##";
		if (isMicrotonal()) {
			String ret = "";
			if (m_fraction != null)
				ret = m_fraction.toString();
			else
				ret = Float.toString(Math.abs(m_value));
			return ret + (m_value < 0 ? "b" : "#");
		}
		return "";
	}
	
	//TODO: complete, but some fraction symbols not supported or don't exist
	public String toUnicodeString() {
		if (m_value == _NONE)
			return "";
		if (m_value == _NATURAL)
			return String.valueOf(UNICODE_NATURAL);
		if (m_value == _FLAT)
			return String.valueOf(UNICODE_FLAT);
		if (m_value == _SHARP)
			return String.valueOf(UNICODE_SHARP);
		if (m_value == _DOUBLE_FLAT)
			return UNICODE_DOUBLE_FLAT;
		if (m_value == _DOUBLE_SHARP)
			return UNICODE_DOUBLE_SHARP;
		if (isMicrotonalLessThanFlat())
			return UNICODE_HALF_FLAT;
		if (isMicrotonalMoreThanFlat())
			return UNICODE_FLAT_AND_A_HALF;
		if (isMicrotonalLessThanSharp())
			return UNICODE_HALF_SHARP;
		if (isMicrotonalMoreThanSharp())
			return UNICODE_SHARP_AND_A_HALF;
		if (isMicrotonal()) {
			String ret = "";
			if (m_fraction != null)
				ret = m_fraction.toString();
			else
				ret = Float.toString(Math.abs(m_value));
			return ret + (m_value < 0 ? String.valueOf(UNICODE_FLAT) : String.valueOf(UNICODE_SHARP));
		}
		return "";
	}

}
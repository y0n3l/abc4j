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
 * 
 */
public abstract class PositionableInTime extends DecorableElement {

	private static final long serialVersionUID = -8983228958670756390L;

	private short m_referenceNoteLength = Note.EIGHTH;

	private PositionableInTime previousElement = null;

	/**
	 * Returns duration of note/rest
	 */
	public abstract short getDuration();

	/**
	 * Returns duration of the note in bars, only bar lines, measure repeat and
	 * rest jump to next or 2nd next bar.
	 */
	public abstract int getDurationInBars();

	public int getPositionInBars() {
		if (previousElement != null) {
			return previousElement.getPositionInBars()
					+ previousElement.getDurationInBars();
		} else if (this instanceof BarLine) {
			return 0; // Voice starts with a bar line before first bar
		} else {
			int b = getDurationInBars();
			if (b > 0) {
				return b; // Voice starts with a multiple measures rest
			} else {
				return 1; // Voice starts with a note
			}
		}
	}

	public double getPositionInBeats() {
		int i = getPositionInUnits();
		if (i == 0)
			return 0.0;
		double ret = ((i - 1) / (double) m_referenceNoteLength) + 1;
		ret = ((int) (ret * 10000)) / 10000.0;
		return ret;
	}

	/**
	 * Return the position unit ({@value Note#LENGTH_RESOLUTION subdivisions of
	 * a beat)
	 */
	public int getPositionInUnits() {
		if (previousElement != null) {
			if (previousElement instanceof BarLine)
				return 1; // Beats start at 1
			else {
				return previousElement.getPositionInUnits()
						+ previousElement.getDuration();
			}
		} else if (this instanceof BarLine) {
			return 0;
			// Voice starts with a bar line before first bar
		} else {
			return 1; // Voice starts with a note or whatever
		}
	}

	/**
	 * Return the position unit ({@value Note#LENGTH_RESOLUTION subdivisions of
	 * a beat) from the beginning of the voice/part
	 */
	public int getPositionInUnitsFromBeginning() {
		if (previousElement != null) {
			return previousElement.getPositionInUnitsFromBeginning()
					+ previousElement.getDuration();
		} else if (this instanceof BarLine) {
			return 0; // Voice starts with a bar line before first bar
		} else {
			return 1; // Voice starts with a note or whatever
		}
	}

	protected void setPreviousElement(PositionableInTime pit) {
		this.previousElement = pit;
	}

	protected void setReferenceNoteLength(short ref) {
		m_referenceNoteLength = ref;
	}

}

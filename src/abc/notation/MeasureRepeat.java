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
 * A (multi) measure repeat is a symbol which tells the musician to replay the
 * last one or last two measures.
 */
public class MeasureRepeat extends PositionableInTime implements Cloneable {

	private static final long serialVersionUID = 804979956263621851L;

	private int m_numberOfMeasure = 1;

	/**
	 * @param numberOfMeasure
	 *            Number of measure to repeat, 1 or 2
	 */
	public MeasureRepeat(int numberOfMeasure) {
		m_numberOfMeasure = Math.max(1, Math.min(2, numberOfMeasure));
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/** Returns the number of measure to repeat, 1 or 2 */
	public int getNumberOfMeasure() {
		return m_numberOfMeasure;
	}

	public int getDurationInBars() {
		return getNumberOfMeasure();
	}

	public short getDuration() {
		return 0;
	}

}

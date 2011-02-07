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
 * A (multi) measure rest is a rest covering a whole measure (bar) and which may
 * be repeated.
 * <p>
 * It inserts a long rest in the audio output, it prints a long rectangle with
 * number of repeat above of it.
 */
public class MeasureRest extends MusicElement implements Cloneable {

	private static final long serialVersionUID = 1571575580427511657L;

	private int m_numberOfRepeats = 1;

	public MeasureRest(int numberOfRepeats) {
		m_numberOfRepeats = Math.max(1, numberOfRepeats);
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public int getNumberOfRepeats() {
		return m_numberOfRepeats;
	}

}

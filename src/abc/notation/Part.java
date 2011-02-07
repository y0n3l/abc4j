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

/** <TT>Part</TT> objects are used to define parts in tunes.
 * A part contains a {@link Music},
 * which can contains multiple voices. */
public class Part implements Cloneable, Serializable {

	private static final long serialVersionUID = 7633083530672682502L;

	private String m_label;

	// private Tune m_tune = null;
	private Music m_music = null;

	Part(Tune tune, String labelValue) {
		// m_tune = tune;
		m_music = new Music();
		setLabel(labelValue);
	}
	Part(Tune tune, char labelValue) {
		this(tune, labelValue+"");
	}

	/**
	 * Sets the label which first char identifies this part.
	 * 
	 * @param labelValue
	 *            The label that identifies this part.
	 * @throws IllegalArgumentException if label is 0-length
	 */
	public void setLabel(String labelValue)
			throws IllegalArgumentException {
		if ((labelValue == null) || (labelValue.length() == 0))
			throw new IllegalArgumentException("Part's label can't be null or empty!");
		m_label = labelValue;
		m_music.setPartLabel(m_label);
	}
	
	/**
	 * Sets the label that identifies this part.
	 * 
	 * @param labelValue
	 *            The label that identifies this part.
	 */
	public void setLabel(char labelValue) {
		setLabel(labelValue + "");
	}

	/**
	 * Returns the label that of this part.
	 */
	public String getLabel() {
		return m_label;
	}
	
	/**
	 * Returns the identifies of this part, which is the first
	 * char of its label
	 */
	public char getIdentifier() {
		return m_label.charAt(0);
	}

	/**
	 * Returns the music to this part.
	 * 
	 * @return The music associated to this part.
	 */
	public Music getMusic() {
		return m_music;
	}

	void setMusic(Music score) {
		m_music = score;
	}

	public Object clone() throws CloneNotSupportedException {
		Object o = super.clone();
		((Part) o).m_music = (Music) m_music.clone();
		return o;
	}
}

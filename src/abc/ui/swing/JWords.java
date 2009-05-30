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
package abc.ui.swing;

import java.awt.Font;
import java.awt.Graphics2D;

import abc.notation.MusicElement;
import abc.notation.Words;

class JWords extends JText {

	protected Words m_words = null;

	/** Constructor
	 * @param mtrx The score metrics needed
	 */
	protected JWords(ScoreMetrics mtrx, Words words) {
		super(mtrx);
		m_words = words;
	}

	/** Returns the height of this score element.
	 * @return The height of this score element. */
	public double getHeight() {
		m_height = (double)m_metrics.getLyricsHeight();
		return (m_height);
	}

	/** Returns the width of this score element.
	 * @return The width of this score element. */
	public double getWidth() {
		m_width = (double)m_metrics.getLyricsWidth(m_words.getContent());
		return (m_width);
	}

	/** Returns the tune's music element represented by this graphical score element.
	 * @return The tune's music element represented by this graphical score element. <TT>null</TT>
	 * if this graphical score element is not related to any music element.
	 * @see MusicElement  */
	public MusicElement getMusicElement() {
		return (m_words);
	}

	/** Callback invoked when the base has changed for this object. */
	protected void onBaseChanged() {
		// do something here?

	}

	/** Renders this Score element to the given graphic context.
	 * @param g2 */
	public double render(Graphics2D g2) {
		Font previousFont = g2.getFont();
		g2.setFont(m_metrics.getLyricsFont());
		g2.drawString(m_words.getContent(), (int)getBase().getX(), (int)getBase().getY());
		g2.setFont(previousFont);
		return m_width;
	}
}


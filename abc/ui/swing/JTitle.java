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
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.MusicElement;
import abc.notation.Words;


/** This class defines a score rendition element. Rendition scores elements
 * are graphical representations of tune score elements objects retrieved
 * from a tune object.
 * A <TT>JScoreElement</TT> can itself contain <TT>JScoreElement</TT> instances.
 * You should figure out the graphical score representation as a tree of
 * <TT>JScoreElement</TT>. (Composite)
 * @see Tune#getMusic()
 */
class JTitle extends JText {

	protected String m_text = null;

	/** Constructor
	 * @param mtrx The score metrics needed
	 */
	protected JTitle(ScoreMetrics mtrx, String text) {
		super(mtrx);
		m_text = text;
	}

	/** Returns the height of this score element.
	 * @return The height of this score element. */
	public double getHeight() {
		m_height = (double)m_metrics.getTitleHeight();
		return (m_height);
	}

	/** Returns the width of this score element.
	 * @return The width of this score element. */
	public double getWidth() {
		m_width = (double)m_metrics.getTitleWidth(m_text);
		return (m_width);
	}

	/** Renders this Score element to the given graphic context.
	 * @param g2 */
	public double render(Graphics2D g2) {
		Font previousFont = g2.getFont();
		g2.setFont(m_metrics.getTitleFont());
		g2.drawString(m_text, (int)m_base.getX(), (int)m_base.getY());
		g2.setFont(previousFont);
		return m_width;
	}

}

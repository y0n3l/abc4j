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

import java.awt.Graphics2D;

import abc.notation.MusicElement;


/** TODO doc
 */
abstract class JText extends JScoreElementAbstract {
	
	private String m_text = null;

	/** Constructor
	 * @param mtrx The score metrics needed
	 */
	protected JText(ScoreMetrics mtrx, String text) {
		super(mtrx);
		this.m_text = text;
	}

	/** Returns the height of this score element.
	 * @return The height of this score element. */
	public abstract double getHeight();

	/** Returns the width of this score element.
	 * @return The width of this score element. */
	public abstract double getWidth();

	/** Returns the tune's music element represented by this graphical score element.
	 * @return The tune's music element represented by this graphical score element. <TT>null</TT>
	 * if this graphical score element is not related to any music element.
	 * @see MusicElement  */
	public MusicElement getMusicElement() {
		return null;
	}

	public String getText() {
		return m_text;
	}

	/** Callback invoked when the base has changed for this object. */
	protected void onBaseChanged() {
		// does nothing
	}

	/** Renders this Score element to the given graphic context.
	 * @param g2 */
	public abstract double render(Graphics2D g2);

}

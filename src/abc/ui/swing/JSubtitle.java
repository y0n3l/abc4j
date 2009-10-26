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

/** TODO doc */
class JSubtitle extends JText {

	/** Constructor
	 * @param mtrx The score metrics needed
	 */
	protected JSubtitle(ScoreMetrics mtrx, String text) {
		super(mtrx, text);
	}

	/** Returns the height of this score element.
	 * @return The height of this score element. */
	public double getHeight() {
		return (double)getMetrics().getTextFontHeight(ScoreMetrics.FONT_SUBTITLE);
	}

	/** Returns the width of this score element.
	 * @return The width of this score element. */
	public double getWidth() {
		return (double)getMetrics().getTextFontWidth(ScoreMetrics.FONT_SUBTITLE, getText());
	}

	/** Renders this Score element to the given graphic context.
	 * @param g2 */
	public double render(Graphics2D g2) {
		Font previousFont = g2.getFont();
		g2.setFont(getMetrics().getTextFont(ScoreMetrics.FONT_SUBTITLE));
		g2.drawString(getText(), (int)getBase().getX(), (int)getBase().getY());
		g2.setFont(previousFont);
		return getWidth();
	}

}

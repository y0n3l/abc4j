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

import abc.ui.scoretemplates.HorizontalAlign;
import abc.ui.scoretemplates.ScoreElements;
import abc.ui.scoretemplates.VerticalAlign;

/** TODO doc */
public class JAnnotation extends JText {

	private byte hAlign = HorizontalAlign.CENTER;
	private byte vAlign = VerticalAlign.TOP;
	
	/** Constructor
	 * @param mtrx The score metrics needed
	 */
	protected JAnnotation(ScoreMetrics mtrx, String text) {
		super(mtrx, text, ScoreElements.TEXT_ANNOTATIONS);
		setTextPosition();
	}
	
	private void setTextPosition() {
		String s = getText();
		String newText = s.substring(1);
		if (newText.length() == 0)
			newText = s;
		char c = s.charAt(0);
		switch(c) {
		case '<':
			vAlign = VerticalAlign.MIDDLE;
			hAlign = HorizontalAlign.LEFT;
			setText(newText);
			break;
		case '>':
			vAlign = VerticalAlign.MIDDLE;
			hAlign = HorizontalAlign.RIGHT;
			setText(newText);
			break;
		case '_':
			vAlign = VerticalAlign.UNDER_STAFF;
			hAlign = HorizontalAlign.CENTER;
			setText(newText);
			break;
		case '^':
		case '@':
			setText(newText);
		default:
			vAlign = VerticalAlign.ABOVE_STAFF;
			hAlign = HorizontalAlign.CENTER;
			break;
		}
	}

	public byte getHorizontalAlignment() {
		return hAlign;
	}

	public byte getVerticalAlignment() {
		return vAlign;
	}

}

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

/** TODO doc */
class JSubtitle extends JText {

	/** Constructor
	 * @param mtrx The score metrics needed
	 * @param text
	 */
	protected JSubtitle(ScoreMetrics mtrx, String text) {
		this(mtrx, text, ALIGN_LEFT);
	}
	
	/** Constructor
	 * @param mtrx The score metrics needed
	 * @param text
	 * @param textAlign
	 */
	protected JSubtitle(ScoreMetrics mtrx, String text, short textAlign) {
		super(mtrx, text, ScoreMetrics.FONT_SUBTITLE, textAlign);
	}

}

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.Note;

class JGraceNotePartOfGroup extends JNotePartOfGroup {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	protected int NOTATION_CONTEXT = ScoreMetrics.GRACENOTE_GLYPH;

	public JGraceNotePartOfGroup(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
	}

	/** Always "true" for auto stemming
	*/
	public void setStemUp(boolean isUp) {
		if (getAutoStem()) {
			super.setStemUp(true);
		} else {
			super.setStemUp(isUp);
		}
	}

	protected void valuateNoteChars() {
		// beamed notes are always 1/8th notes or less
		// so just display a stemless note - stems and beams are drawn
		// programmatically
		noteChars = ScoreMetrics.NOTE;
	}

	// correct for font glyph positioning
	public double getCorrectedGlyphOffest(Note note) {
		double positionOffset = super.getCorrectedGlyphOffest(note);
		return positionOffset -= 1; // move up 1px
	}



	protected void onBaseChanged() {
		super.onBaseChanged();

		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);
/* TJM */ // bug ... 1st time called this is always null. why ?
if (glyphDimension == null) return;

		/* TJM */
		// bug corection below:
		// for some (unknown) reason, beaming moves one of the note positions
		// down 1 note position. This line corrects for it.
		int noteY = (int) ( notePosition.getY() - (m_metrics.getStaffLineHeight()/2) );
		int noteX = (int) notePosition.getX();
		displayPosition.setLocation(noteX, noteY);

		BasicStroke stemStroke = m_metrics.getNotesLinkStroke();
		/*if (isStemUp)
			stemX = (int)(noteX + m_metrics.getNoteWidth() - stemStroke.getLineWidth()/10);
		else
			stemX = (int)(noteX);*/

		noteX = (int)displayPosition.getX();

		int stemYBegin = (int)(displayPosition.getY() - glyphDimension.getHeight()/6);

		stemUpBeginPosition = new Point2D.Double(noteX + m_metrics.getGraceNoteWidth() - stemStroke.getLineWidth()/10,
			stemYBegin);
		stemDownBeginPosition = new Point2D.Double(noteX,stemYBegin);

		//reinit stem position
		setStemUp(isStemUp);

	}

	public double render(Graphics2D context){

		Font previousFont = context.getFont();

		try {
			context.setFont(m_metrics.getGracingsFont());
			super.render(context);
		} finally {
			context.setFont(previousFont);
		}

		return m_width;
	}
}

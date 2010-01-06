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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.notation.Note;

class JGraceNotePartOfGroup extends JNotePartOfGroup {

	public JGraceNotePartOfGroup(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
		super.setAutoStem(false);
		super.setStemUp(true);
	}


	// no-op: gracenotes should always be stemmed up
	public void autoStem(boolean auto) {
	}

	// no-op: gracenotes should always be stemmed up
	public void setStemUp(boolean isUp) {
	}

	protected void valuateNoteChars() {
		// beamed notes are always 1/8th notes or less
		// so just display a stemless note - stems and beams are drawn
		// programmatically
		noteChars = ScoreMetrics.NOTE;
	}
	
	/**
	 * used to request glyph-specific metrics
	 * in a genric way that enables positioning, sizing, rendering
	 * to be done generically
	 * <p>subclasses should override this method.
	 * @return {@link ScoreMetrics#NOTATION_CONTEXT_GRACENOTE}
	 */
	protected int getNotationContext() {
		return ScoreMetrics.NOTATION_CONTEXT_GRACENOTE;
	}

	// correct for font glyph positioning
	public double getCorrectedGlyphOffest(Note note) {
		double positionOffset = getOffset(note);
		return positionOffset -= 1; // move up 1px
	}



	protected void onBaseChanged() {
		super.onBaseChanged();

		Dimension glyphDimension = getMetrics().getGlyphDimension(getNotationContext());
		/* TJM */ // bug ... 1st time called this is always null. why ?
        if (glyphDimension == null) return;

		int noteY = (int) notePosition.getY();
		int noteX = (int) notePosition.getX();
		displayPosition.setLocation(noteX, noteY);

		BasicStroke stemStroke = getMetrics().getNotesLinkStroke();

		noteX = (int)displayPosition.getX();

		int stemYBegin = (int)(displayPosition.getY() - glyphDimension.getHeight()/2);

		stemUpBeginPosition = new Point2D.Double(noteX + getMetrics().getGraceNoteWidth(), stemYBegin);
		stemDownBeginPosition = new Point2D.Double(noteX,stemYBegin);

	}

	public double render(Graphics2D context){

		Font previousFont = context.getFont();

		try {
			context.setFont(getMetrics().getGracingsFont());
			super.render(context);
		} finally {
			context.setFont(previousFont);
		}
		
		//renderDebugBoundingBox(context);

		return getWidth();
	}
}

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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import abc.notation.Note;

class JGraceNote extends JNote {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	protected int NOTATION_CONTEXT = ScoreMetrics.GRACENOTE_GLYPH;

	protected boolean renderSlash = true;
	protected Point2D slashStart = null;
	protected Point2D slashEnd = null;

	public JGraceNote(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
		slashStart = new Point2D.Double(0,0);
		slashEnd = new Point2D.Double(0,0);
	}

	/** Always "true" for auto stemming
	*/
	public void setStemUp(boolean isUp) {
		if (isAutoStem()) {
			super.setStemUp(true);
		} else {
			super.setStemUp(isUp);
		}
	}

	public void setRenderSlash(boolean render) {
		renderSlash = render;
	}


	// correct for font glyph positioning
	public double getCorrectedGlyphOffest(Note note) {
		double positionOffset = //
			super.getCorrectedOffset(note);
		return positionOffset -= 1; // move up 1px
	}

	protected void onBaseChanged() {
		super.onBaseChanged();
		Dimension d = m_metrics.getGlyphDimension(NOTATION_CONTEXT);

		if (d == null) return;

		m_width = d.getWidth();

		// calculate slash position
		int startX = 0;
		int endX = 0;
		int startY = 0;
		int endY = 0;
		if (isStemUp()) {
			startX = (int) (displayPosition.getX() + m_width*0.5);
			endX = (int) (displayPosition.getX() + m_width*2.5);
			startY = (int) (displayPosition.getY() - m_width*2.5);
			endY = (int) (displayPosition.getY() - m_width*3.5);
		} else {
			startX = (int) (displayPosition.getX() - m_width*0.5);
			endX = (int) (displayPosition.getX() - m_width*2.5);
			startY = (int) (displayPosition.getY() + m_width*2.5);
			endY = (int) (displayPosition.getY() + m_width*3.5);
		}
		slashStart.setLocation(startX, startY);
		slashEnd.setLocation(endX, endY);

	}

	public double render(Graphics2D context){
		Font previousFont = context.getFont();
		try {
			context.setFont(m_metrics.getGracingsFont());
			super.render(context);

// visual testing of base/offset/note positions
/*
Color previousColor = context.getColor();
context.setColor(Color.RED);
double nh = m_metrics.getStaffLineHeight();
double offset = getOffset(note);
context.drawRect((int)(m_base.getX()), (int)(m_base.getY()),1, 1);
context.setColor(Color.GREEN);
context.drawRect((int)(m_base.getX()), (int)(m_base.getY()+offset),1, 1);
context.setColor(previousColor);
*/

		} finally {
			context.setFont(previousFont);
		}

		if (renderSlash) {
			Stroke dfs = context.getStroke();
			context.setStroke(m_metrics.getStemStroke());
			context.drawLine((int)slashStart.getX(), (int)slashStart.getY(), (int)slashEnd.getX(), (int)slashEnd.getY());
			context.setStroke(dfs);
		}

		return m_width;
	}
}

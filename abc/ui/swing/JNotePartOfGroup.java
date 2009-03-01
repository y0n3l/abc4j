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

/* TJM */
/**
CHANGELOG
Glyph type and Dimension

*/

package abc.ui.swing;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.Note;

class JNotePartOfGroup extends JNote implements JGroupableNote {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	protected int NOTATION_CONTEXT = ScoreMetrics.NOTE_GLYPH;

	/*protected int stemX = -1;
	protected int stemYBegin = -1;  */
	protected int stemYEnd = -1;

	public JNotePartOfGroup(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
		//onBaseChanged();
	}

	protected void valuateNoteChars() {
		// beamed notes are always 1/8th notes or less
		// so just display a stemless note - stems and beams are drawn
		// programmatically
		noteChars = ScoreMetrics.NOTE;
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

		int stemYBegin = -1;
		if (isStemUp) {
			stemYBegin = (int)(displayPosition.getY() - glyphDimension.getHeight()/6);
			// if stemYEnd hasn't been set give it a default
			if (stemYEnd < 0) stemYEnd = (int)(displayPosition.getY() - m_metrics.getStemLength(NOTATION_CONTEXT));
		} else {
			stemYBegin = (int)(displayPosition.getY() + glyphDimension.getHeight()/6);
			// if stemYEnd hasn't been set give it a default
			if (stemYEnd < 0) stemYEnd = (int)(displayPosition.getY() + m_metrics.getStemLength(NOTATION_CONTEXT));
		}

/* TODO : change m_metrics.getNoteWidth to use call like: getCharDimensions(NOTATION_CONTEXT) */
		stemUpBeginPosition = new Point2D.Double(noteX + m_metrics.getNoteWidth() - stemStroke.getLineWidth()/10,
			stemYBegin);
		stemDownBeginPosition = new Point2D.Double(noteX,stemYBegin);

		//reinit stem position
		setStemUp(isStemUp);

	}

	public void setStemYEnd(int value) {
		stemYEnd = value;
	}

	public int getStemYEnd() {
		return stemYEnd;
	}

	/*public Point2D getStemBegin() {
		return new Point2D.Double(stemX, stemYBegin);
	}*/


	public Rectangle2D getBoundingBox() {
		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);
		Rectangle2D bb = new Rectangle2D.Double((int)(m_base.getX()), (int)(stemYEnd),
				m_width, stemBeginPosition.getY()-stemYEnd+glyphDimension.getHeight()/2);
		return bb;
	}

	public Point2D getEndOfStemPosition() {
		if(stemYEnd!=-1)
			return new Point2D.Double(stemBeginPosition.getX(), stemYEnd);
		else
			throw new IllegalStateException();
	}

	public double render(Graphics2D context){
		super.render(context);

		Stroke defaultS = context.getStroke();
		context.setStroke(m_metrics.getStemStroke());

		// draws stem
			context.drawLine((int)stemBeginPosition.getX(), (int)stemBeginPosition.getY(),
					(int)stemBeginPosition.getX(), stemYEnd);
		context.setStroke(defaultS);

		// visual debug
		/*
		java.awt.Color previousColor = context.getColor();
		context.setColor(java.awt.Color.RED);
					context.drawLine((int)stemBeginPosition.getX(), (int)stemBeginPosition.getY(),
							(int)stemBeginPosition.getX(), stemYEnd);
		context.setColor(java.awt.Color.GREEN);
		context.drawLine((int)m_base.getX(), (int)m_base.getY(),
				(int)m_base.getX()+1, (int)m_base.getY()+1);
		context.setColor(previousColor);
		*/
		return m_width;
	}
}

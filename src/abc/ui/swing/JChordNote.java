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
import java.awt.geom.Point2D;

import abc.notation.Note;

class JChordNote extends JNotePartOfGroup {

	public JChordNote(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
		//onBaseChanged();
	}
	
	protected void valuateNoteChars() {
		if (note.getStrictDuration()==Note.HALF || note.getStrictDuration()==Note.WHOLE) {
			noteChars = ScoreMetrics.NOTE_LONGER;
		}
		else
			noteChars = ScoreMetrics.NOTE;
	}
	
	public double render(Graphics2D context){
		//super.render(context);
		context.drawChars(noteChars, 0, 1, (int)displayPosition.getX(), (int)displayPosition.getY());
		renderExtendedStaffLines(context, m_metrics, m_base);
		renderAccidentals(context);
		renderDots(context);
		/*
		Color previousColor = context.getColor();
		context.setColor(Color.RED);
		context.drawLine((int)getStemX(), (int)getStemYBegin(), 
				(int)getStemX(), (int)getStemYBegin());
		context.setColor(previousColor);
		/*Color previousColor = context.getColor();
		context.setColor(Color.RED);
		context.drawLine((int)getStemBegin().getX(), (int)getStemBegin().getY(), 
				(int)getStemBegin().getX()+10, (int)getStemBegin().getY());
		context.setColor(previousColor);*/
		
		return m_width;
	}
}

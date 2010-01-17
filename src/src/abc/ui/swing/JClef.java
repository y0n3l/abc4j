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

import abc.notation.Clef;
import abc.notation.MusicElement;

/** This class is in charge of rendering a staff clef. */
class JClef extends JScoreElementAbstract {
	
	//protected BarLine m_barLine = null;
	
	public JClef(Point2D base, ScoreMetrics c) {
		super(c);
		setBase(base);
	}
	
	public double getWidth() {
		//TODO m_metrics.getClefWidth+noteWidth/4
		return 3*getMetrics().getNoteWidth();
	}
	
	protected void onBaseChanged() {
	}
	
	public MusicElement getMusicElement() {
		return Clef.G;
	}
	
	public double render(Graphics2D context){
		super.render(context);
		/*char[] chars2 = {ScoreMetrics.STAFF_SIX_LINES};
		context.drawChars(chars2, 0, chars2.length, 
				(int)m_base.getX(), (int)(m_base.getY()));*/
		char[] chars = {getMusicalFont().getClef(Clef.G)};
		context.drawChars(chars, 0, chars.length, 
				(int)(getBase().getX()+getMetrics().getNoteWidth()/4),
				(int)(getBase().getY()-getMetrics().getNoteHeight()));
		return getWidth();
	}
}

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

import abc.notation.MusicElement;

/** This class is in charge of rendering a staff clef. */
class JClef extends JScoreElementAbstract {
	
	public static final char G_CLEF = '\uF026';
	
	//protected BarLine m_barLine = null;
	
	public JClef(Point2D base, ScoreMetrics c) {
		super (c);
		m_width = 3*m_metrics.getNoteWidth();
		setBase(base);
	}
	
	protected void onBaseChanged() {
	}
	
	public MusicElement getMusicElement() {
		return null;
	}
	
	public double render(Graphics2D context){
		super.render(context);
		/*char[] chars2 = {ScoreMetrics.STAFF_SIX_LINES};
		context.drawChars(chars2, 0, chars2.length, 
				(int)m_base.getX(), (int)(m_base.getY()));*/
		char[] chars = {G_CLEF};
		context.drawChars(chars, 0, chars.length, 
				(int)m_base.getX(), (int)(m_base.getY()-m_metrics.getNoteHeigth()));
		return m_width;
	}
}

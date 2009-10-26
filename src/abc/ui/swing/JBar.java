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

//import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.notation.BarLine;
import abc.notation.MusicElement;

/** This class renders a simple bar line. */
class JBar extends JScoreElementAbstract{
	/** The encapsulated abc notation bar element */
	protected BarLine m_barLine = null;

	private int m_barWidth = -1;
	//dots in case of repeat only.
	private int m_barDotsSpacing = -1;
	private int m_dotsRadius = -1;
	private int m_topDotY = -1;
	private int m_bottomDotY = -1;
	
	public JBar(BarLine barLine, Point2D base, ScoreMetrics mtrx) {
		super(mtrx);
		m_barLine = barLine; 
		m_dotsRadius = (int)(mtrx.getNoteWidth()*0.3);
		m_barWidth = (int)(mtrx.getNoteWidth()*0.5);
		m_barDotsSpacing = (int)(mtrx.getNoteWidth()*0.2);
		setBase(base);
	}
	
	public MusicElement getMusicElement() {
		return m_barLine;
	}
	
	private int getHeight() {
		return (int)getMetrics().getStaffCharBounds().getHeight();
	}
	
	public double getWidth() {
		switch (m_barLine.getType()) {
		case BarLine.SIMPLE :
			return 0;
		case BarLine.REPEAT_OPEN : 
			return m_barWidth+2*m_barDotsSpacing+m_dotsRadius;
		case BarLine.REPEAT_CLOSE : 
			return m_barWidth+2*m_barDotsSpacing+m_dotsRadius;
		}
		return 0;
	}
	
	protected void onBaseChanged() {
		int height = getHeight();
		m_topDotY = (int)(getBase().getY()-height*0.61);
		m_bottomDotY = (int)(getBase().getY()-height*0.4);
	}
	
	public double render(Graphics2D context){
		super.render(context);
		int height = getHeight();
		//System.out.println("space between = " + m_barDotsSpacing);
		Point2D m_base = getBase();
		switch (m_barLine.getType()) {
			case BarLine.REPEAT_OPEN : 
				context.fillRect((int)m_base.getX(), (int)m_base.getY()-height, m_barWidth, height);
				context.drawLine((int)(m_base.getX()+m_barWidth + m_barDotsSpacing), (int)(m_base.getY()-height),
												(int)(m_base.getX()+m_barWidth + m_barDotsSpacing), (int)(m_base.getY()));
				context.fillOval((int)(m_base.getX()+m_barWidth + 2*m_barDotsSpacing), m_topDotY, m_dotsRadius, m_dotsRadius);
				context.fillOval((int)(m_base.getX()+m_barWidth + 2*m_barDotsSpacing), m_bottomDotY, m_dotsRadius, m_dotsRadius);
				break;
			case BarLine.REPEAT_CLOSE : 
				context.fillOval((int)(m_base.getX()), (int)(m_base.getY()-height*0.61), 
													m_dotsRadius, m_dotsRadius);
				context.fillOval((int)(m_base.getX()), (int)(m_base.getY()-height*0.4), 
													m_dotsRadius, m_dotsRadius);
				context.drawLine((int)(m_base.getX()+m_dotsRadius+m_barDotsSpacing), (int)(m_base.getY()-height),
												(int)(m_base.getX()+m_dotsRadius+m_barDotsSpacing), (int)(m_base.getY()));
				context.fillRect((int)(m_base.getX()+m_dotsRadius+2*m_barDotsSpacing), (int)m_base.getY()-height, 
												m_barWidth, height);
				break;
			case BarLine.SIMPLE : context.drawChars(ScoreMetrics.BAR_LINE, 0, 1, 
					(int)(m_base.getX()+getMetrics().getNoteWidth()/3), (int)(m_base.getY()));
				break;
		}
		return getWidth();
	}
	
	
}

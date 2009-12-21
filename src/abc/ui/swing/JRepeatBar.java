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

import abc.notation.RepeatBarLine;

/** This class is in charge of rendering a repeat bar. */
class JRepeatBar extends JBar{

	//TODO move this to ScoreMetrics
	public static final char[][] DIGITS = {
			{'\uF0C1', '\uF02E'}, 
			{'\uF0AA', '\uF02E'},
			{'\uF0A3', '\uF02E'},
			{'\uF0A2', '\uF02E'},
			{'\uF0B0', '\uF02E'},
			{'\uF0A4', '\uF02E'},
			{'\uF0A6', '\uF02E'},
			{'\uF0A5', '\uF02E'},
			{'\uF0BB', '\uF02E'}};

	public JRepeatBar(RepeatBarLine barLine, Point2D base, ScoreMetrics c) {
		super(barLine,base, c); 
	}
	
	public double render(Graphics2D context){
		ScoreMetrics metrics = getMetrics();
		double x = super.getWidth();// metrics.getNoteWidth();
		double staffHeight = metrics.getStaffCharBounds().getHeight();
		double staffWidth = metrics.getStaffCharBounds().getWidth();
		char[] ch = DIGITS[((RepeatBarLine)m_barLine).getRepeatNumber()-1];
		Point2D m_base = getBase();
		context.drawChars(ch, 0, ch.length, 
				(int)(m_base.getX()+x+1),
				(int)(m_base.getY()-staffHeight*1.3));
		context.drawLine(
				(int)(m_base.getX()+x-1), 
				(int)(m_base.getY()-staffHeight*1.1), 
				(int)(m_base.getX()+x-1), 
				(int)(m_base.getY()-staffHeight*1.7));
		context.drawLine(
				(int)(m_base.getX()+x-1), 
				(int)(m_base.getY()-staffHeight*1.7), 
				(int)(m_base.getX()+x-1+staffWidth), 
				(int)(m_base.getY()-staffHeight*1.7));
		return super.render(context);
		
	}
}

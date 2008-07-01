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
import abc.notation.TimeSignature;

/** This class is in charge of rendering a time signature. */
class JTimeSignature extends JScoreElementAbstract {
	
	public static final char[][] DIGITS = {
			{'\uF030'}, //0
			{'\uF031'}, //1
			{'\uF032'}, //2
			{'\uF033'}, //3
			{'\uF034'}, //4
			{'\uF035'}, //5
			{'\uF036'}, //6
			{'\uF037'}, //7
			{'\uF038'}, //8
			{'\uF039'}, //9
			{'\uF031','\uF030'}, //10
			{'\uF031','\uF031'}, //11
			{'\uF031','\uF032'}, //12
			{'\uF031','\uF033'}  //13
			};
	
	
	protected TimeSignature m_ts = null;
	
	protected char[] m_numChars = null;
	protected char[] m_denomChars = null;
	protected int m_topNumY = -1;
	protected int m_bottomNumY = -1;

	public JTimeSignature(TimeSignature ts, Point2D base, ScoreMetrics c) {
		super(c);
		m_ts = ts;
		m_numChars = DIGITS[m_ts.getNumerator()];
		m_denomChars = DIGITS[m_ts.getDenominator()];
		if (m_ts.getNumerator()>=10 || m_ts.getDenominator()>=10)
			m_width = 2*m_metrics.getNoteWidth();
		else
			m_width = m_metrics.getNoteWidth();
		setBase(base);
	}
	
	public MusicElement getMusicElement() {
		return m_ts;
	}
	
	protected void onBaseChanged() {
		//FIXME what if the signature numbers are not supported ? => arrayOutOfBounds ! :/
		m_topNumY = (int)(m_base.getY()-m_metrics.getNoteHeigth()*3.0);
		m_bottomNumY = (int)(m_base.getY()-m_metrics.getNoteHeigth()*0.9);
	}
	
	public double render(Graphics2D context){
		super.render(context);
		context.drawChars(m_numChars, 0, m_numChars.length, (int)m_base.getX(), m_topNumY);
		context.drawChars(m_denomChars, 0, m_denomChars.length, (int)m_base.getX(), m_bottomNumY);
		return m_width;
	}
}


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
	
	protected TimeSignature m_ts = null;
	
	protected char[] m_numChars = null;
	protected char[] m_denomChars = null;
	protected int m_topNumY = -1;
	protected int m_bottomNumY = -1;

	public JTimeSignature(TimeSignature ts, Point2D base, ScoreMetrics c) {
		super(c);
		m_ts = ts;
		//Transform number into char (via String)
		m_numChars = String.valueOf(m_ts.getNumerator()).toCharArray();
		m_denomChars = String.valueOf(m_ts.getDenominator()).toCharArray();
		//m_numChars = DIGITS[m_ts.getNumerator()];
		//m_denomChars = DIGITS[m_ts.getDenominator()];
		//transform each char representing a number into it's font digit
		for (int i = 0; i < m_numChars.length; i++) {
			m_numChars[i] = c.getTimeSignatureDigitChar(Integer.valueOf(""+m_numChars[i]).intValue());
		}
		for (int i = 0; i < m_denomChars.length; i++) {
			m_denomChars[i] = c.getTimeSignatureDigitChar(Integer.valueOf(""+m_denomChars[i]).intValue());
		}
		//compute width, no limit, we can have 16/128 if we want ;)
		m_width = Math.max(m_numChars.length, m_denomChars.length)*m_metrics.getTimeSignatureNumberWidth();
		setBase(base);
	}
	
	public MusicElement getMusicElement() {
		return m_ts;
	}
	
	protected void onBaseChanged() {
		//FIXME what if the signature numbers are not supported ? => arrayOutOfBounds ! :/
		m_topNumY = (int)(getBase().getY()-m_metrics.getNoteHeight()*3.0);
		m_bottomNumY = (int)(getBase().getY()-m_metrics.getNoteHeight()*0.9);
	}
	
	public double render(Graphics2D context){
		super.render(context);
		context.drawChars(m_numChars, 0, m_numChars.length,
				(int)(getBase().getX()+m_width/2-m_numChars.length*m_metrics.getTimeSignatureNumberWidth()/2),
				m_topNumY);
		context.drawChars(m_denomChars, 0, m_denomChars.length,
				(int)(getBase().getX()+m_width/2-m_denomChars.length*m_metrics.getTimeSignatureNumberWidth()/2),
				m_bottomNumY);
		return m_width;
	}
}


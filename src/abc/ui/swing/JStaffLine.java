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
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import abc.notation.MusicElement;

class JStaffLine extends JScoreElementAbstract {

	protected Vector m_staffElements = null;

	protected Vector m_lyrics = null;
	//protected Vector m_beginningSlurElements = null;

	public JStaffLine(Point2D base, ScoreMetrics c) {
		super (c);
		m_staffElements = new Vector();
		m_lyrics = new Vector();
		//m_beginningSlurElements = new Vector();
	}

	public Point2D getBase() {
		return ((JScoreElementAbstract)m_staffElements.elementAt(0)).getBase();
	}

	public MusicElement getMusicElement() {
		return null;
	}

	protected void onBaseChanged() {

	}

	public void addElement(JScoreElementAbstract r) {
		m_staffElements.addElement(r);
		r.setStaffLine(this);
	}

	// FIXME: lyrics should be appended score
	//        inline lyrics should be parsed and appended to staff lines
	public void addLyrics(JWords r) {
		m_lyrics.addElement(r);
	}

	public boolean hasLyrics() {
		if (m_lyrics.size() > 0) return true;
		else return false;
	}

	public int getLyricsLineCount() {
		return (m_lyrics.size());
	}

	public JWords[] getLyrics() {
		return ( (JWords[]) m_lyrics.toArray(new JWords[1]) );
	}

	public JScoreElementAbstract[] toArray() {
		JScoreElementAbstract[] r = new JScoreElementAbstract[m_staffElements.size()];
		m_staffElements.toArray(r);
		return r;
	}
	
	/**
	 * Returns the last added element, <TT>null</TT> if
	 * the staff line is empty
	 */
	public JScoreElement getLastElement() {
		if (m_staffElements.size() > 0)
			return (JScoreElement) m_staffElements.lastElement();
		else
			return null;
	}

	public JScoreElement getScoreElementAt(Point point) {
		JScoreElement scoreEl = null;
		for (int i=0; i<m_staffElements.size(); i++) {
			scoreEl = ((JScoreElement)m_staffElements.elementAt(i)).getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}

	public double render(Graphics2D g) {
		//super.render(g);
		/*Color previousColor = g.getColor();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)(getBase().getX()), (int)(getBase().getY()-m_metrics.getStaffCharBounds().getHeight()),
				(int)getWidth(), (int)(m_metrics.getStaffCharBounds().getHeight()));
		g.setColor(previousColor);*/
		JScoreElementAbstract[] elmts = toArray();
		for (int j=0; j<elmts.length; j++) {
			elmts[j].render((Graphics2D)g);
			/*if (elmts[j] instanceof SNote) {
				Note note = ((SNote)elmts[j]).getNote();
				if (note.isBeginingSlur())
					m_beginningSlurElements.addElement(note);

			}*/
		}
		int staffCharNb = (int)(getWidth()/getMetrics().getStaffCharBounds().getWidth());
		//System.out.println("char staff nb : " + staffCharNb);
		char[] staffS = new char[staffCharNb+2];
		for (int j=0; j<staffS.length; j++)
			staffS[j] = ScoreMetrics.STAFF_SIX_LINES;
		//1 avoid a small gap (antialiased but really here)
		//I can't explain what, but it fixes the gap :)
		g.drawChars(staffS, 0, staffS.length, 1, (int)getBase().getY());

		// render lyrics, annotations, etc.
		Iterator iter = m_lyrics.iterator();
		JWords lyrics = null;
		while (iter.hasNext()) {
			lyrics = (JWords)iter.next();
			lyrics.render(g);
		}

		return getWidth();
	}

	public void scaleToWidth(double newWidth) {
		for (int i=0; i<m_staffElements.size(); i++){
			JScoreElementAbstract elmt = (JScoreElementAbstract)m_staffElements.elementAt(i);
			if ( (!(elmt instanceof JClef)) && (!(elmt instanceof JKeySignature))
					&& (!(elmt instanceof JTimeSignature))
					)  {
				double newXpos =(elmt.getBase().getX()*newWidth/getWidth());
				Point2D base = elmt.getBase();
				base.setLocation(newXpos, base.getY());
				if (elmt instanceof JGroupOfNotes)
					((JGroupOfNotes)elmt).setInternalSpacingRatio(newWidth/getWidth());
				elmt.setBase(base);
			}
		}
		//System.out.println("StaffLine, required Width : " + newWidth + " | result width=" + getWidth());
	}

	public double getWidth() {
		JScoreElementAbstract lastElmt = (JScoreElementAbstract)m_staffElements.lastElement();
		return lastElmt.getBase().getX()+lastElmt.getWidth();
	}

}

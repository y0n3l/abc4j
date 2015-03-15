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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Vector;

import abc.notation.MusicElement;
import abc.notation.Tablature;
import abc.ui.scoretemplates.ScoreAttribute;
import abc.ui.scoretemplates.ScoreElements;

class JStaffLine extends JScoreElementAbstract {

	protected Vector<JScoreElementAbstract> m_staffElements = null;
	
	private Engraver m_engraver;
	
	private double topY = -1;

	protected Vector<JWords> m_lyrics = null;
	
	private String m_voiceId = null;
	
	private JTablature m_tablature = null;
	//protected Vector m_beginningSlurElements = null;

	public JStaffLine(Point2D base, ScoreMetrics c, Engraver e,
			String voiceId) {
		super(base, c);
		m_engraver = e;
		m_staffElements = new Vector<JScoreElementAbstract>();
		m_lyrics = new Vector<JWords>();
		m_voiceId = voiceId;
		//m_beginningSlurElements = new Vector();
	}
	
	protected String getVoiceId() {
		return m_voiceId;
	}
	
	/**
	 * Sets the top Y of the staff line, including the space
	 * above the staff, e.g. for chord line
	 */
	protected void setTopY(double d) {
		topY = d;
	}
	
	/**
	 * Returns the top Y of the staff line, including the space
	 * above the staff, e.g. for chord line
	 */
	protected double getTopY() {
		return topY;
	}
	
	/** Return the y coordinate of the 5th (from low to high) line */
	protected double get5thLineY() {
		return getBase().getY() - getMetrics().getStaffCharBounds().getHeight();
	}
	
	/** Return the y coordinate of the 1st (from low to high) line */
	protected double get1stLineY() {
		return getBase().getY();
	}
	
	/** Returns height between 1st and 5th line */
	protected double getHeight() {
		return getMetrics().getStaffCharBounds().getHeight();
	}

	public Point2D getBase() {
		if (m_staffElements.size() > 0)
			return ((JScoreElementAbstract)m_staffElements.elementAt(0)).getBase();
		else
			return super.getBase();
	}

	public MusicElement getMusicElement() {
		return null;
	}

	/** voided */
	protected void onBaseChanged() {
	}

	public void addElement(JScoreElementAbstract r) {
		m_staffElements.addElement(r);
		r.setStaffLine(this);
	}
	
	/**
	 * attaches a tablature to this staff line
	 * @param tablature null to remove tablature
	 */
	protected void setTablature(Tablature tablature) {
		if (tablature != null)
			m_tablature = new JTablature(tablature, getBase(), getMetrics());
		else
			m_tablature = null;
	}
	
	/** Returns the JTablature associated to this staff, or
	 * null if none. */
	protected JTablature getTablature() {
		return m_tablature;
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
	
	/**
	 * Returns true if this staff line has note(s).
	 * Returns false if it only contains clef, key, time sig., tempo, barlines...
	 * @return
	 */
	public boolean hasNotes() {
		for (JScoreElement element : m_staffElements) {
			if ((element instanceof JNoteElementAbstract)
					|| (element instanceof JGroupOfNotes))
				return true;
		}
		return false;
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
	
	/**
	 * Returns the number of elements in the staff elements array
	 */
	protected int countElement() {
		return m_staffElements.size();
	}
	
	/**
	 * Returns the Vector of elements
	 */
	protected Vector<JScoreElementAbstract> getStaffElements() {
		return m_staffElements;
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
	
	/** Return the bottom most (Y) point, first staff line +
	 * lyrics line + tablature + ... */
	protected double getBottomY() {
		//TODO add space for lyrics, low notes...
		double ret = get1stLineY();
		if (m_tablature != null) {
			ret += getTemplate().getAttributeSize(ScoreAttribute.TABLATURE_SPACING);
			ret += m_tablature.getHeight();
		}
		return ret;
	}

	public double render(Graphics2D g) {
		//super.render(g);
		
		Color previousColor = g.getColor();
		setColor(g, ScoreElements.STAFF_LINES);
		double width = getWidth();
		int charX = (int) getBase().getX();
		int charY = (int) getBase().getY();
		char[] charGlyph = new char[] { getMusicalFont().getStaffFiveLines() };
		//-1 to "overwrite" 1px to avoid small gap
		int charWidth = (int)(getMetrics().getStaffCharBounds().getWidth() - 1);
		while (charX+charWidth < width) {
			g.drawChars(charGlyph, 0, 1, charX, charY);
			charX += charWidth;
		}
		//write the last
		charX = (int) (width - charWidth - 1);
		g.drawChars(charGlyph, 0, 1, charX, charY);
		g.setColor(previousColor);
		/*Color previousColor = g.getColor();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)(getBase().getX()), (int)(getBase().getY()-m_metrics.getStaffCharBounds().getHeight()),
				(int)getWidth(), (int)(m_metrics.getStaffCharBounds().getHeight()));
		g.setColor(previousColor);*/

		//Set base for lyrics
		//Set base for tablature
		if (m_tablature != null) {
			m_tablature.setBase(new Point2D.Double(
					getBase().getX(),
					get1stLineY() +
					getTemplate().getAttributeSize(
							ScoreAttribute.TABLATURE_SPACING)));
			m_tablature.setWidth(width);
			m_tablature.render(g);
		}
		
		JScoreElementAbstract[] elmts = toArray();
		for (int j=0; j<elmts.length; j++) {
			//Render all elements of this line (notes, barlines...)
			//this will render lyrics, tablature numbers
			elmts[j].render(g);
		}
		
		// TODO render lyrics, etc.
		for (JWords lyrics : m_lyrics) {
			lyrics.render(g);
		}
		
		//renderDebugBoundingBox(g);

		return getWidth();
	}

	public void scaleToWidth(double newWidth) {
		//while this is true, don't move clef, key, time, bar and part
		//turns false at first other element (e.g.) note encountered
		//so if there's a key, time... change in the middle
		//of the staff, it'll be scaled.
		boolean dontMoveFirstElements = true;
		double getWidth = getWidth();
		if (getWidth == newWidth)
			return;
		//System.out.println("scaleToWidth("+newWidth+")");
		for (int i=0; i<m_staffElements.size(); i++){
			JScoreElementAbstract elmt = (JScoreElementAbstract)m_staffElements.elementAt(i);
			//System.out.println(elmt);
			if (dontMoveFirstElements) {
				if ((elmt instanceof JClef)
					|| (elmt instanceof JKeySignature)
					|| (elmt instanceof JTimeSignature)
					|| (elmt instanceof JBar)
					|| (elmt instanceof JTempo)
					|| (elmt instanceof JPartLabel)) {
					continue;
				}
			}
			//we are here because we encountered another element
			//since now, scale all
			dontMoveFirstElements = false;
			double newXpos =(elmt.getBase().getX()*newWidth/getWidth);
			if ((i == (m_staffElements.size()-1))
					&& (elmt instanceof JBar)) {
				newXpos = newWidth - elmt.getWidth();
			}
			Point2D base = elmt.getBase();
			//System.out.println(" - oldX = "+(int)base.getX()+" - newX="+(int)newXpos);
			base.setLocation(newXpos, base.getY());
			if (elmt instanceof JGroupOfNotes)
				((JGroupOfNotes)elmt).setInternalSpacingRatio(newWidth/getWidth);
			elmt.setBase(base);
		}
		//System.out.println("StaffLine, required Width : " + newWidth + " | result width=" + getWidth());
	}

	public double getWidth() {
		JScoreElementAbstract lastElmt = (JScoreElementAbstract)m_staffElements.lastElement();
		double width = lastElmt.getBase().getX()+lastElmt.getWidth();
		if (!(lastElmt instanceof JBar))
			width += getMetrics().getNotesSpacing()
					+ m_engraver.getNoteSpacing(lastElmt);
		return width;
	}

}

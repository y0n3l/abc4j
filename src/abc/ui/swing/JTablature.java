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
import java.awt.geom.Point2D;

import abc.notation.MusicElement;
import abc.notation.Note;
import abc.notation.Tablature;
import abc.ui.scoretemplates.ScoreElements;
import abc.ui.scoretemplates.TextJustification;
import abc.ui.scoretemplates.TextVerticalAlign;

/**
 * A JTablature represents a {@link abc.notation.Tablature}
 * under a {@link abc.ui.swing.JStaffLine}.
 */
class JTablature extends JScoreElementAbstract {

	private class JTablatureNumber extends JText {
		protected JTablatureNumber(ScoreMetrics mtrx, String text) {
			super(mtrx, text, ScoreElements.TEXT_TABLATURE);
		}
	}
	
	private Tablature m_tablature = null;
	
	private JTablatureNumber number = null;
	
	private double width = 0;
	
	private int oneLineHeight = 0;
	
	public JTablature(Tablature tab, Point2D base, ScoreMetrics c) {
		super(c);
		m_tablature = tab;
		number = new JTablatureNumber(c, "0");
		oneLineHeight = (int)(number.getOneLineHeight() * .75);
	}
	
	public double getHeight() {
		return m_tablature.getNumberOfString()
			* oneLineHeight;
	}
	
	public double getWidth() {
		return width;
	}
	//used by JStaffLine.render()
	protected void setWidth(double w) {
		width = w;
	}

	public MusicElement getMusicElement() {
		return null;
	}

	protected void onBaseChanged() {
	}
	
	/** Returns the Y position for text for request string */
	protected double getTextY(int stringNo) {
		return getBase().getY()
			+ (oneLineHeight * (m_tablature.getNumberOfString()+1-stringNo));
	}
	
	/** Returns the Y position for text for request string */
	protected double getLineY(int stringNo) {
		return getTextY(stringNo)
			//don't know why / 2 don't give good result :-/
			- (int)(oneLineHeight / 1.5);
	}
	
	/**
	 * Returns the Y of the first (bottom) line
	 */
	protected double getBottomLineY() {
		return getLineY(1);
	}

	/**
	 * Returns the Y of the last (top) line
	 */
	protected double getTopLineY() {
		return getLineY(m_tablature.getNumberOfString());
	}
	
	protected void renderNote(Graphics2D g, JNote jnote) {
		Note note = (Note) jnote.getMusicElement();
		int[] pos = m_tablature.getFingeringForNote(note);
		if (pos != null) {
			number.setText(pos[1]+"");
			number.setTextVerticalAlign(TextVerticalAlign.BOTTOM);
			number.setTextJustification(TextJustification.LEFT);
			number.setBase(new Point2D.Double(
				jnote.getNotePosition().getX(),
				getTextY(pos[0])));
			//remove the line (draw with background color)
			Color previousColor = g.getColor();
			g.setColor(Color.white);//g.getBackground());
			int y = (int)getLineY(pos[0]);
			int x = (int)(jnote.getNotePosition().getX() - 2);
			g.drawLine(x, y, (int)(x+number.getWidth()+4), y);
			g.setColor(previousColor);
			number.render(g);
		}
	}
	
	/** Renders the strings' lines, avoiding the numbers
	 * previously rendered */
	public double render(Graphics2D g) {
		Color previousColor = g.getColor();
		setColor(g, ScoreElements.TABLATURE_LINES);
		number.setText("MM");
		double mWidth = number.getWidth();
		double numberOfStrings = m_tablature.getNumberOfString();
		for (int i = 1; i <= numberOfStrings; i++) {
			//String name
			double y = getTextY(i);
			number.setText(m_tablature.getStringNote(i).getName());
			number.setBase(new Point2D.Double(getBase().getX()+mWidth/2, y));
			number.setTextVerticalAlign(TextVerticalAlign.BOTTOM);
			number.setTextJustification(TextJustification.CENTER);
			number.render(g);
			// line
			y = getLineY(i);
			g.drawLine((int)(getBase().getX()+mWidth),
					(int)y,
					(int)(getBase().getX()+width),
					(int)y);
		}
		g.setColor(previousColor);
		return getWidth();
	}

}

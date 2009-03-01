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
import java.awt.geom.Rectangle2D;

import abc.notation.MusicElement;
import abc.notation.Tune;

/** This class defines a score rendition element. Rendition scores elements
 * are graphical representations of tune score elements objects retrieved 
 * from a tune object.
 * A <TT>JScoreElement</TT> can itself contain <TT>JScoreElement</TT> instances.
 * You should figure out the graphical score representation as a tree of 
 * <TT>JScoreElement</TT>. (Composite) 
 * @see Tune#getMusic()
 */ 
abstract class JScoreTextAbstract implements JScoreElement {
	/** The metrics to be used to calculate this rendition element */ 
	protected ScoreMetrics m_metrics = null;
	/** The reference point (bottom, left) that should be used when rendering
	 * this element. */
	protected Point2D m_base = null;
	/** The width of this rendition element */
	protected double m_width = -1;
	/** Teh bounding box (more or less humm....) that encapsulates
	 * this graphical score element. */
	protected Rectangle2D m_boundingBox = null;
	
	//protected Color m_color = null;
	/** The staff line that contains this score element. */ 
	protected JStaffLine staffLine = null; 
	
	/** Constructor 
	 * @param mtrx The score metrics needed 
	 */
	protected JScoreTextAbstract(ScoreMetrics mtrx) {
		m_metrics = mtrx;
	}
	
	/** Returns the width of this score element.
	 * @return The width of this score element. */
	public double getWidth() {
		return m_width;
	}
	
	/** Returns the staff line containing this score element. 
	 * @return The staff line containing this score element. */
	public JStaffLine getStaffLine() {
		return staffLine;
	}
	
	public void setStaffLine(JStaffLine staffLine) {
		this.staffLine = staffLine;
	}
	
	/** Returns the tune's music element represented by this graphical score element.
	 * @return The tune's music element represented by this graphical score element. <TT>null</TT>
	 * if this graphical score element is not related to any music element. 
	 * @see MusicElement  */ 
	public abstract MusicElement getMusicElement();
	
	/** Returns the bounding box for this score element. 
	 * @return the bounding box for this score element. */
	public Rectangle2D getBoundingBox() {
		Rectangle2D bb = new Rectangle2D.Double(m_base.getX(), m_base.getY()-50, m_width, 50);
		return bb;
	}
	
	/** Returns the score element whose bouding box contains the
	 * given location.
	 * @param location A location.
	 * @return The score element whose bouding box contains the
	 * given location. <TT>this</TT> can be returned or one of the 
	 * sub <TT>JScoreElement</TT> contained in this one. <TT>null</TT>
	 * is returned if no matching element has been found. */ 
	public JScoreElement getScoreElementAt(Point location) {
		if (getBoundingBox().contains(location))
			return this;
		else
			return null;
	}
	
	/** Return the base of this element.
	 * @return The base of this element. The base is the point that is used 
	 * as a reference to draw the element at this location. 
	 * @see #setBase(Point2D) */
	public Point2D getBase() {
		return m_base;
	}
	
	/** Sets the base of this element.
	 * @param base The new bas that should be used to draw this element. 
	 * @see #getBase() */
	public void setBase(Point2D base) {
		m_base = (Point2D)base.clone();
		onBaseChanged();
	}
	
	/** Sets the color used for the rendition of this score element.
	 * @param color The color used for the rendition of this score element. */
	/*public void setColor(Color color) {
		m_color = color;
	}*/
	
	/** Callback invoked when the base has changed for this object. */
	protected abstract void onBaseChanged();
	
	/** Renders this Score element to the given graphic context. 
	 * @param g2 */
	public double render(Graphics2D g2) {
		/*Color previousColor = g.getColor();
		g.setColor(Color.RED);
		m_boundingBox = getBoundingBox();
		g.drawRect((int)(m_boundingBox.getX()), (int)(m_boundingBox.getY()), 
				(int)(m_boundingBox.getWidth()), (int)(m_boundingBox.getHeight()));
		g.setColor(previousColor);*/
		return m_width;
	}
}

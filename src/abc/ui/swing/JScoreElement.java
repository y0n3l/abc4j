package abc.ui.swing;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.MusicElement;

public interface JScoreElement {
	/** Returns the width of this score element.
	 * @return The width of this score element. */
	public double getWidth();
	
	/** Returns the staff line containing this score element. 
	 * @return The staff line containing this score element. */
	//public JStaffLine getStaffLine();
	
	/** Returns the tune's music element represented by this graphical score element.
	 * @return The tune's music element represented by this graphical score element. <TT>null</TT>
	 * if this graphical score element is not related to any music element. 
	 * @see MusicElement  */ 
	public abstract MusicElement getMusicElement();
	
	/** Returns the bounding box for this score element. 
	 * @return the bounding box for this score element. */
	public Rectangle2D getBoundingBox();
	
	/** Returns the score element whose bouding box contains the
	 * given location.
	 * @param location A location.
	 * @return The score element whose bouding box contains the
	 * given location. <TT>this</TT> can be returned or one of the 
	 * sub <TT>JScoreElement</TT> contained in this one. <TT>null</TT>
	 * is returned if no matching element has been found. */ 
	public JScoreElement getScoreElementAt(Point location);
	
	/** Return the base of this element.
	 * @return The base of this element. The base is the point that is used 
	 * as a reference to draw the element at this location. 
	 * @see #setBase(Point2D) */
	public Point2D getBase();
	
}

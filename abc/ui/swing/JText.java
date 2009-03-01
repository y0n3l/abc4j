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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.MusicElement;
import abc.notation.Words;


/** This class defines a score rendition element. Rendition scores elements
 * are graphical representations of tune score elements objects retrieved
 * from a tune object.
 * A <TT>JScoreElement</TT> can itself contain <TT>JScoreElement</TT> instances.
 * You should figure out the graphical score representation as a tree of
 * <TT>JScoreElement</TT>. (Composite)
 * @see Tune#getMusic()
 */
abstract class JText extends JScoreElementAbstract {

	/** The height of this rendition element */
	protected double m_height = -1;

	/** Constructor
	 * @param mtrx The score metrics needed
	 */
	protected JText(ScoreMetrics mtrx) {
		super(mtrx);
	}

	/** Returns the height of this score element.
	 * @return The height of this score element. */
	public abstract double getHeight();

	/** Returns the width of this score element.
	 * @return The width of this score element. */
	public abstract double getWidth();

	/** Returns the tune's music element represented by this graphical score element.
	 * @return The tune's music element represented by this graphical score element. <TT>null</TT>
	 * if this graphical score element is not related to any music element.
	 * @see MusicElement  */
	public MusicElement getMusicElement() {
		return null;
	}


	/** Callback invoked when the base has changed for this object. */
	protected void onBaseChanged() {
		// does nothing
	}

	/** Renders this Score element to the given graphic context.
	 * @param g2 */
	public abstract double render(Graphics2D g2);

}

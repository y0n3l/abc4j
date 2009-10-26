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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.Decoration;
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
class JDecoration extends JScoreElementAbstract {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	protected int NOTATION_CONTEXT = ScoreMetrics.NOTE_GLYPH;

	Decoration m_decoration = null;

	/** The width of this rendition element */
	protected double m_width = -1;

	/** The height of this rendition element */
	protected double m_height = -1;

	/** Constructor
	 * @param decoration The decoration
	 * @param mtrx The score metrics needed
	 */
	protected JDecoration(Decoration decoration, ScoreMetrics mtrx) {
		this(decoration, null, mtrx);
	}

	/** Constructor
	 * @param decoration The decoration
	 * @param base The base
	 * @param mtrx The score metrics needed
	 */
	protected JDecoration(Decoration decoration, Point2D base, ScoreMetrics mtrx) {
		super(mtrx);
		m_decoration = decoration;
		if (base != null) setBase(base);
		m_height = getMetrics().getDecorationHeight();
		m_width = getMetrics().getDecorationWidth(m_decoration.getChars());
	}

	/** Returns the height of this score element.
	 * @return The height of this score element. */
	public double getHeight() {
		return m_height;
	}

	/** Returns the width of this score element.
	 * @return The width of this score element. */
	public double getWidth() {
		return m_width;
	}

	/** Returns the tune's music element represented by this graphical score element.
	 * @return The tune's music element represented by this graphical score element. <TT>null</TT>
	 * if this graphical score element is not related to any music element.
	 * @see MusicElement  */
	public MusicElement getMusicElement() {
		return m_decoration;
	}


	/** Callback invoked when the base has changed for this object. */
	protected void onBaseChanged() {
		// does nothing
	}

	/** Renders this Score element to the given graphic context.
	 * @param g2 */
	public double render(Graphics2D g2) {
		if (m_decoration.getType() == Decoration.ROLL) {
			// note.hasGeneralGracing() is ignored as
			// ABC says the '~' typically denotes a roll
			//
			// where's the roll character in SONOROA??
			// use 90CW rotated left bracket instead .....
			Shape rotatedDecoration = getMetrics().getRotatedDecoration(g2, m_decoration.getChars(), Math.PI/2);
			if (rotatedDecoration != null) {
				AffineTransform t = null;
				try {
					t = g2.getTransform();
					g2.translate((int)getBase().getX(), (int)getBase().getY());
					g2.fill(rotatedDecoration);
				} finally {
					g2.setTransform(t);
				}
			}
		} else {
			g2.drawChars(m_decoration.getChars(), 0, 1,
					(int)getBase().getX(), (int)getBase().getY());
		}

		return m_width;
	}

}

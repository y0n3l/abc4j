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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Vector;

import abc.notation.Decoration;
import abc.notation.MusicElement;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.Tune;

/** This class defines a note rendition element.
 */
abstract class JNoteElementAbstract extends JScoreElementAbstract {

	protected static final double SPACE_RATIO_FOR_GRACENOTES = 0.3;

	protected Note note = null;


	// instance of JGraceNotes or JGroupOfGraceNotes
	protected JScoreElementAbstract m_jGracenotes = null;

	// vector of JDecoration instances for this note.
	protected Vector m_jDecorations = null;

	private JSlurOrTie jSlurDefinition = null;

	//TODO redundant with slur...AnchorOutOfStem ?
	protected Dimension slurStemOffset = null;

	protected Point2D slurUnderAnchor = null, slurAboveAnchor = null;
	protected Point2D slurUnderAnchorOutOfStem = null, slurAboveAnchorOutOfStem = null;

	/** <TT>true</TT> if the stem is up for this chord, <TT>false</TT> otherwise. */
	// private attribute so all classes are forced to accessor methods
	//  this ensures autoStemming is done correctly
	protected boolean stemUp = true;

	/** Stem direction will be determined automatically
	 * based on note value. Notes B or higher will be stemed down,
	 * Notes A or lower will be stemed up. True by default.
	 */
	// private attribute so all classes are forced to accessor methods
	//  this ensures autoStemming is done correctly
	protected boolean autoStem = true;

	/** Callback invoked when the base has changed for this object. */
	protected abstract void onBaseChanged();



	public JNoteElementAbstract(NoteAbstract noteValue, Point2D base, ScoreMetrics metrics) {
		super(base, metrics);

		// add JGraceNotes
		if (noteValue.hasGracingNotes()) {
			Note [] graceNotes = noteValue.getGracingNotes();
			if (graceNotes.length == 1) {
				m_jGracenotes = new JGraceNote(graceNotes[0], base, m_metrics);
				base.setLocation(base.getX()+m_jGracenotes.getWidth(),base.getY());
			} else if (graceNotes.length>1) {
				// FIXME: gracenote groups should use a proper engraver!!
				m_jGracenotes = new JGroupOfGraceNotes(m_metrics, base, graceNotes, null);
//				base.setLocation(base.getX()+m_jGracenotes.getWidth(),base.getY());
			}
		}

		// add JDecorations
		if (noteValue.hasDecorations()) {
			Decoration[] decorations = noteValue.getDecorations();
			for (int i=0; i<decorations.length;i++) {
				addDecoration(new JDecoration(decorations[i], m_metrics));
			}
		}
	}

	public boolean isAutoStem() {
		return autoStem;
	}

	public void setAutoStem(boolean auto) {
		autoStem = auto;
	}

	public void setStemUp(boolean isUp) {
		stemUp = isUp;
		/*
		valuate note char
		*/
	}

	public boolean isStemUp() {
	  boolean isup = stemUp;
	  if (autoStem) {
		if (note.getHeight()<Note.B) {
		  isup = true;
		} else {
		  isup = false;
		}
	  }
	  return isup;
	}



	/** Add decoration if it hasn't been added previously */
	/* FIXME: move into a common base class */
	private void addDecoration (JDecoration decoration) {
		if (m_jDecorations == null) {
			m_jDecorations = new Vector();
		}
		if (!m_jDecorations.contains(decoration)) {
			m_jDecorations.add(decoration);
		}
	}

	/**
	 * @return Returns the jSlurDefinition.
	 */
	public JSlurOrTie getJSlurDefinition() {
		if (jSlurDefinition == null)
			jSlurDefinition = new JSlurOrTie(null, m_metrics);
		return jSlurDefinition;
	}

	/**
	 * @param slurDefinition The jSlurDefinition to set.
	 */
	public void setJSlurDefinition(JSlurOrTie slurDefinition) {
		jSlurDefinition = slurDefinition;
	}

	public Point2D getSlurAboveAnchor() {
		return slurAboveAnchor;
	}

	public Point2D getSlurUnderAnchor() {
		return slurUnderAnchor;
	}

	public Point2D getSlurAboveAnchorOutOfStem() {
		return slurAboveAnchorOutOfStem;
	}

	public Point2D getSlurUnderAnchorOutOfStem() {
		return slurUnderAnchorOutOfStem;
	}


	/* Render each indiviual gracenote associated with this note. */
	protected void renderGraceNotes(Graphics2D context) {
		if (m_jGracenotes != null)
			m_jGracenotes.render(context);
	}

	protected void renderDecorations(Graphics2D context){
		if (m_jDecorations != null && m_jDecorations.size() > 0) {
			Iterator iter = m_jDecorations.iterator();
			JDecoration decoration = null;
			while (iter.hasNext()) {
				decoration = (JDecoration)iter.next();
				decoration.render(context);
 			}
 		}
	}


}

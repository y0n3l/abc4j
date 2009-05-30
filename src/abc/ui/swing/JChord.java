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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

import abc.notation.Decoration;
import abc.notation.MultiNote;
import abc.notation.MusicElement;
import abc.notation.Note;

/** This class is in charge of rendering a chord. */
class JChord extends JNoteElementAbstract {
	/** the multi this JChord is the graphic representation of. */
	protected MultiNote multiNote = null;
	/** All the notes composing the chord. */
	protected Note[] m_notes = null;
	/** All the notes rendition elements that are part of the group. */
	protected JNote[] m_sNoteInstances = null;

	protected JNote anchor = null;

	/** When multi notes are made of notes with different durations, such chord
	 * is decomposed into chords with same strict duration for normalization,
	 * and to ease the rendition : the rendition is made on set of notes with the same
	 * strict duration.
	 * Such normalized chords are ordered with ascend strict durations in this array. */
	protected JChord[] m_normalizedChords = null;

	// the width of the chord group without grace notes
	private double c_width = 0;


	public JChord(MultiNote multiNote, ScoreMetrics metrics, Point2D base){
		super(multiNote, base, metrics);
		this.multiNote = multiNote;

		m_notes = multiNote.toArray();
		//create JNotePartOfGroup instances. Those instances stay the same when the base is changed.
		//The width of the chord is the width of the largest note.
		if (multiNote.hasUniqueStrictDuration()) {
			m_sNoteInstances = new JNote[m_notes.length];
			for (int i=0; i<m_notes.length; i++) {
				m_sNoteInstances[i] = new JChordNote(m_notes[i], base /*new Point2D.Double()*/, m_metrics);
				if(m_sNoteInstances[i].getWidth()>c_width)
					c_width += m_sNoteInstances[i].getWidth();
			}
			//inits stem direction
			if (multiNote.getHighestNote().getHeight()<Note.B) {
				setStemUp(true);
			} else 	if (multiNote.getLowestNote().getHeight()>=Note.B) {
				setStemUp(false);
			} else {
				byte highest = multiNote.getHighestNote().getHeight();
				byte lowest = multiNote.getLowestNote().getHeight();
				if ( (highest - Note.B) >= (Note.B - lowest) ) {
					setStemUp(false);
				} else if ( (highest - Note.B) < (Note.B - lowest) ) {
					setStemUp(true);
				} else {
					setStemUp(false);
				}
			}

		}
		else {
			m_sNoteInstances = new JNote[1];
			m_sNoteInstances[0] = new JNote(multiNote.getHighestNote(), new Point2D.Double(), m_metrics);
			c_width = m_sNoteInstances[0].getWidth();
			MultiNote[] h = multiNote.normalize();
			short[] durations = multiNote.getStrictDurations();
			if (durations.length>2)
				System.err.println("abc4j - warning : chords with more than 2 differents strict duration aren't supported : only 2 smaller durations are taken into account");
			m_normalizedChords = new JChord[2];
			MultiNote fastest = h[0];
			JChord jChord = createNormalizedChord(fastest, metrics, base);
			m_normalizedChords[0] = jChord;
			m_normalizedChords[0].setStemUp(true);
			MultiNote slowest = h[1];
			jChord = createNormalizedChord(slowest, metrics, base);
			m_normalizedChords[1] = jChord;
			m_normalizedChords[1].setStemUp(false);
		}

		if (m_jGracenotes != null)
			m_width = c_width + m_jGracenotes.getWidth();
		else
			m_width = c_width;

		setBase(base);
	}

	/** The anchor of the chord is the one that present the rhytm (the highest one
	 * in case of stem up, the lowest one in case of stem down)
	 * @param note
	 * @param base
	 * @param metrics
	 * @return
	 */
	protected JNote createAnchorNote(Note note, Point2D base, ScoreMetrics metrics) {
		JNote jNote = new JNote(note, new Point2D.Double(), m_metrics);
		//note.set
		return jNote;
	}

	/** Invoked when a multi note is decomposed into multi notes with same strict
	 * duration. */
	protected JChord createNormalizedChord(MultiNote mNote, ScoreMetrics mtrx, Point2D base) {
		return new JChord(mNote, mtrx, base);
	}

	public MusicElement getMusicElement() {
		return multiNote;
	}

	public JNote[] getScoreElements() {
		return m_sNoteInstances;
	}

	/** Sets the staff line this chord belongs to. */
	public void setStaffLine(JStaffLine staffLine) {
		for (int i=0; i<m_sNoteInstances.length; i++)
			m_sNoteInstances[i].setStaffLine(staffLine);
		super.setStaffLine(staffLine);
	}

	/*JNotePartOfGroup[] getRenditionElements() {
		return m_sNoteInstances;
	}*/

	/** Sets the base of this chord. */
	public void setBase(Point2D base) {
		if (m_normalizedChords!=null)
			for (int i=0; i<m_normalizedChords.length; i++)
				m_normalizedChords[i].setBase(base);
		if (m_jGracenotes != null) {
			m_jGracenotes.setBase(base);
		}
		super.setBase(base);
	}

	/** Invoked when this chord base has changed. */
	protected void onBaseChanged() {
		double graceNotesWidth = 0;
		// setBase for grace notes
		if (m_jGracenotes != null) {
			m_jGracenotes.onBaseChanged();
			graceNotesWidth = m_jGracenotes.getWidth() + (getWidth()*0.3);
		}

		// TODO: setBase for decorations

		if (m_normalizedChords==null) {
			double biggestStemX = -1;
			for (int i=0; i<m_sNoteInstances.length; i++) {
				m_sNoteInstances[i].setBase(getBase());
				if (m_sNoteInstances[i].getStemBeginPosition().getX()>biggestStemX)
					biggestStemX = m_sNoteInstances[i].getStemBeginPosition().getX();
				//if(m_sNoteInstances[i].getWidth()>width)
				//	width = m_sNoteInstances[i].getWidth();
			}
			//realign all stems
			for (int i=0; i<m_sNoteInstances.length; i++) {
				Point2D stemBegin = m_sNoteInstances[i].getStemBeginPosition();
				Point2D newStemBegin = new Point2D.Double (biggestStemX, stemBegin.getY());
				m_sNoteInstances[i].setStemBeginPosition(newStemBegin);
			}
		}
		else
		{
			for (int i=0; i<m_normalizedChords.length; i++) {
				m_normalizedChords[i].onBaseChanged();
			}
		}

		for (int i=0; i<m_sNoteInstances.length; i++) {
//				m_sNoteInstances[i].??
		}

		m_width = c_width + graceNotesWidth;
	}

	public double render(Graphics2D context){
		//super.render(context);
		//Stroke defaultStroke = context.getStroke();
		//JNote lowestElement = m_sNoteInstances[0];
		//JNote highestElement = m_sNoteInstances[m_sNoteInstances.length-1];
		if (m_normalizedChords==null){
			for (int i=0; i<m_sNoteInstances.length; i++) {
				JNote n = m_sNoteInstances[i];
				n.render(context);
			}
		}
		else
			for (int i=0; i<m_normalizedChords.length; i++)
				m_normalizedChords[i].render(context);

		renderGraceNotes(context);
//		renderDecorations(context);


		return m_width;
	}


	public void setStemUp(boolean isUp) {
		super.setStemUp(isUp);
		if (isUp) {
			m_sNoteInstances[0] = new JChordNote(m_notes[0], m_sNoteInstances[0].getBase(), m_metrics);
			JNote highestJNote = m_sNoteInstances[m_sNoteInstances.length-1];
			Note highestNote = (Note)highestJNote.getMusicElement();
			//When the stem is up, the anchor is the highest note.
			anchor = createAnchorNote(highestNote, highestJNote.getBase(), m_metrics);
			m_sNoteInstances[m_sNoteInstances.length-1] = anchor;
			for (int i=0; i<m_sNoteInstances.length; i++)
				m_sNoteInstances[i].setStemUp(true);
		}
		else {
			//Replace the existing highest note
			m_sNoteInstances[m_sNoteInstances.length-1] = new JChordNote(m_notes[m_notes.length-1],
					m_sNoteInstances[m_sNoteInstances.length-1].getBase(), m_metrics);
			JNote lowestJNote = m_sNoteInstances[0];
			Note lowestNote = (Note)lowestJNote.getMusicElement();
			// Replace the existing lowest note
			//When the stem is down, the anchor is the lowest note.
			anchor = createAnchorNote(lowestNote, lowestJNote.getBase(), m_metrics);
			m_sNoteInstances[0] = anchor;
			//m_sNoteInstances[0].setStemUp(false);
			//Apply the stem direction to the rest of the notes composing the chord.
			for (int i=0; i<m_sNoteInstances.length; i++)
				m_sNoteInstances[i].setStemUp(false);
		}
	}

	public JScoreElement getScoreElementAt(Point point) {
		JScoreElement scoreEl = null;
		if(m_normalizedChords!=null)
			for (int i=0; i<m_normalizedChords.length && scoreEl==null; i++) {
				scoreEl = m_normalizedChords[i].getScoreElementAt(point);
			}
		else
		for (int i=0; i<m_sNoteInstances.length; i++) {
			scoreEl = m_sNoteInstances[i].getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}



}

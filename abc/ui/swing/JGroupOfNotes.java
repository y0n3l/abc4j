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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.MultiNote;
import abc.notation.MusicElement;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.SlurDefinition;

/** This class is in charge of rendering a group of notes whose stems should be linked. */
class JGroupOfNotes extends JScoreElementAbstract {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	protected int NOTATION_CONTEXT = ScoreMetrics.NOTE_GLYPH;

	/** All the notes that are part of the group. */
	protected Note[] m_notes = null;
	/** notes that are grouped */
	//protected Note[] anchorNotes = null;
	/** All the notes rendition elements that are part of the group. chords and / or notes*/
	protected JGroupableNote[] m_jNotes = null;
	/** The Y coordinate where the line linking all the notes is put. */
	private int m_stemYend = -1;

	private int nUpletSize = -1;

	private boolean isStemUp = true;

	private Engraver m_engraver = null;

	public JGroupOfNotes(ScoreMetrics metrics, Point2D base, NoteAbstract[] notes, Engraver engrav){
		super(metrics);
		if (notes.length<=1)
			throw new IllegalArgumentException(notes + " is not a group of notes, length = " + notes.length);
		m_engraver = engrav;
		m_notes = new Note[notes.length];
		//create JNotePartOfGroup instances. Those instance should stay the same
		//when the base is changed.
		m_jNotes = new JGroupableNote[m_notes.length];
		for (int i=0; i<notes.length; i++)
			if (notes[i] instanceof Note) {
				m_notes[i] = (Note)notes[i];
				m_jNotes[i] = new JNotePartOfGroup((Note)m_notes[i], base/*new Point2D.Double()*/, m_metrics);
				//anchorNotes[i] = (Note)notes[i];
			}
			else {
				//This is a multiNote
				m_jNotes[i] = new JChordPartOfGroup((MultiNote)notes[i], m_metrics, new Point2D.Double());
				m_notes[i] = (Note)((JChordPartOfGroup)m_jNotes[i]).getReferenceNoteForGroup().getMusicElement();

			}
		//m_jNotes[i]=n;
		if (notes[0].getTuplet()!=null) {
			nUpletSize = notes[0].getTuplet().getNotesAsVector().size();
			SlurDefinition slurDef = new SlurDefinition();
			slurDef.setStart(notes[0]);
			slurDef.setEnd(notes[notes.length-1]);
			JSlurOrTie jSlurDef = new JSlurOrTie(slurDef, m_metrics);
			jSlurDef.setPosition(JSlurOrTie.POSITION_ABOVE);
			jSlurDef.setOutOfStems(true);
			jSlurDef.setTuplet(true);
			m_notes[0].setSlurDefinition(slurDef);
			((JNoteElementAbstract) m_jNotes[0]).setJSlurDefinition(jSlurDef);
		}
		setBase(base);
	}

	public MusicElement getMusicElement() {
		return null;
	}

	public void setStaffLine(JStaffLine staffLine) {
		//If a group of notes if displayed on a staff line, all notes
		//composing the group are then part of this staff line as well.
		for (int i=0; i<m_jNotes.length; i++)
			((JScoreElementAbstract)m_jNotes[i]).setStaffLine(staffLine);
		super.setStaffLine(staffLine);
	}

	Note[] getMusicElements() {
		return m_notes;
	}

	JScoreElementAbstract[] getRenditionElements() {
		JScoreElementAbstract[] array = new JScoreElementAbstract[m_jNotes.length];
		System.arraycopy(m_jNotes, 0, array, 0, m_jNotes.length);
		return array;
	}

	public void setStemUp(boolean isUp) {
		isStemUp = isUp;
		for (int i=0; i<m_jNotes.length; i++) {
			((JNoteElementAbstract)m_jNotes[i]).setStemUp(isUp);
		}
	}

	protected void onBaseChanged() {
		Point2D currentBase =(Point2D)getBase().clone();
		int highIndex = 0;
		int lowIndex = 0;

		highIndex = Note.getHighestNoteIndex(m_notes);
		lowIndex = Note.getLowestNoteIndex(m_notes);

		JGroupableNote highestNote = m_jNotes[highIndex];//(m_notes); Notestsn = new JNotePartOfGroup(highestNote, getBase(), m_metrics);
		JGroupableNote lowestNote = m_jNotes[lowIndex];//(m_notes); Notestsn = new JNotePartOfGroup(lowestNote, getBase(), m_metrics);

		// assume every note in group has same auto stemming policy
		// can be indivudual beamed note or chord multinote

		boolean autoStemming = ((JNoteElementAbstract)highestNote).isAutoStem();

		if (autoStemming) {

			byte h = m_notes[highIndex].getHeight();
			byte l = m_notes[lowIndex].getHeight();

			if (h <= Note.B) {
				setStemUp(true);
			} else if (l > Note.B) {
				setStemUp(false);
			} else {
				if ( (h - Note.B) < (Note.B - l) ) {
					// lowest note is further away from center line than highest note
					setStemUp(true);
				} else {
					setStemUp(false);
				}
			}

		}

		//FIXME compute average distance between note head and middle Note.c
//		int cptUp = 0, cptDown = 0;
//		for (int i = 0; i < m_jNotes.length; i++) {
//			if (((JNote) m_jNotes[i]).isStemUp())
//				cptUp++;
//			else
//				cptDown++;
//		}
//		//System.out.println("up="+cptUp+",down="+cptDown);
//		isStemUp = cptUp>=cptDown;
//		for (int i = 0; i < m_jNotes.length; i++) {
//			((JNote) m_jNotes[i]).setStemUp(isStemUp);
//			((JNote) m_jNotes[i]).onBaseChanged();
//		}

		Point2D updatedBase = null;
		if (isStemUp) {
			//update the highest note to calculate when the stem Y end should be after the base change.
			updatedBase = highestNote.getBase();
			updatedBase.setLocation(currentBase);
			((JScoreElementAbstract)highestNote).setBase(updatedBase);
			//based on this, calculate the new stem Y end.
			m_stemYend = (int)(highestNote.getStemBeginPosition().getY()-m_metrics.getStemLength(NOTATION_CONTEXT));
		} else {
			//update the lowest note to calculate when the stem Y end should be after the base change.
			updatedBase = lowestNote.getBase();
			updatedBase.setLocation(currentBase);
			((JScoreElementAbstract)lowestNote).setBase(updatedBase);
			//based on this, calculate the new stem Y end.
			m_stemYend = (int)(lowestNote.getStemBeginPosition().getY()+m_metrics.getStemLength(NOTATION_CONTEXT));
		}

		JGroupableNote firstNote = m_jNotes[0];
		JGroupableNote lastNote = m_jNotes[m_jNotes.length-1];
		double engraverSpacing = 0;
		// apply the new stem y end to the rest of the group of notes.
		for (int i=0; i<m_jNotes.length; i++) {
			updatedBase = m_jNotes[i].getBase();
			updatedBase.setLocation(currentBase);
			((JScoreElementAbstract)m_jNotes[i]).setBase(updatedBase);
			m_jNotes[i].setStemYEnd(m_stemYend);
			engraverSpacing = 0;

			// gracenote group have fixed spacing, so only use engraver spacing if engraver is not null
			// FIXME: fix this so gracenote groups use a proper engraver
			if (m_engraver != null) engraverSpacing = m_engraver.getNoteSpacing(m_jNotes[i]);
			currentBase.setLocation(currentBase.getX()
							+ m_jNotes[i].getWidth()
							+ m_metrics.getNotesSpacing()
							+ engraverSpacing,
						getBase().getY());
				//}
		}
		if (lastNote==null)
			lastNote=firstNote;
		double firstNoteAccidentalWidth = (firstNote.getWidth()-m_metrics.getNoteWidth());

		if (isStemUp) {
			m_width = (int)(lastNote.getStemBeginPosition().getX()-(firstNote).getBase().getX() + firstNoteAccidentalWidth);
		}
		else {
			m_width = (int)(lastNote.getStemBeginPosition().getX()+m_metrics.getNoteWidth()
						-firstNote.getBase().getX() + firstNoteAccidentalWidth);
		}
	}

	public double render(Graphics2D context){
		//super.render(context);
		Stroke defaultStroke = context.getStroke();
		int factor = isStemUp?1:-1; //invert direction for noteLinkY
		for (int i=0; i<m_jNotes.length; i++) {
			JGroupableNote n = m_jNotes[i];
			((JScoreElementAbstract)n).render(context);
			BasicStroke notesLinkStroke = m_metrics.getNotesLinkStroke();
			context.setStroke(notesLinkStroke);
			short[] longerRhythms = null;
			short noteStrictDuration = m_notes[i].getStrictDuration();
			switch (noteStrictDuration) {
				case Note.EIGHTH: longerRhythms = new short[] { Note.EIGHTH }; break;
				case Note.SIXTEENTH: longerRhythms = new short[] { Note.EIGHTH, Note.SIXTEENTH }; break;
				case Note.THIRTY_SECOND: longerRhythms = new short[] { Note.EIGHTH, Note.SIXTEENTH, Note.THIRTY_SECOND }; break;
				case Note.SIXTY_FOURTH: longerRhythms = new short[] { Note.EIGHTH, Note.SIXTEENTH, Note.THIRTY_SECOND, Note.SIXTY_FOURTH }; break;
			}
			for (int j=0; j<longerRhythms.length; j++) {
				//decide where the end of the rhythm is.
				int noteLinkY = -1;
				if (longerRhythms[j]==Note.EIGHTH)
					noteLinkY = (int)(m_stemYend+factor*notesLinkStroke.getLineWidth()*0.5);
				else if (longerRhythms[j]==Note.SIXTEENTH)
					noteLinkY = (int)(m_stemYend+factor*notesLinkStroke.getLineWidth()*2);
				else if (longerRhythms[j]==Note.THIRTY_SECOND)
					noteLinkY = (int)(m_stemYend+factor*notesLinkStroke.getLineWidth()*3.5);
				else if (longerRhythms[j]==Note.SIXTY_FOURTH)
					noteLinkY = (int)(m_stemYend+factor*notesLinkStroke.getLineWidth()*5);
				//small graphical bug if stem down
				if (!isStemUp)
					noteLinkY += 1;//notesLinkStroke.getLineWidth();

				int noteLinkEnd = -1;
				// is there any note after ?
				boolean nextNoteIsShorterOrEquals = false;
				boolean previousNoteIsShorterOrEquals = false;
				boolean hasNext = i<m_jNotes.length-1;
				boolean hasPrevious = i>0;
				if (hasNext)
					nextNoteIsShorterOrEquals = m_notes[i+1].getStrictDuration()<=longerRhythms[j];
				if (hasPrevious)
					previousNoteIsShorterOrEquals = m_notes[i-1].getStrictDuration()<=longerRhythms[j];
				if (hasPrevious) {
					if (previousNoteIsShorterOrEquals)
						//the end is the stem of the previous note.
						noteLinkEnd = (int)((JGroupableNote)m_jNotes[i-1]).getStemBeginPosition().getX();//getE (int)(stemX-2*context.getNoteWidth());
					else
						if (!(hasNext && nextNoteIsShorterOrEquals))
							noteLinkEnd = (int)(m_jNotes[i].getStemBeginPosition().getX()-m_metrics.getNoteWidth()/1.2);
				}
				else
					if (!nextNoteIsShorterOrEquals)
						noteLinkEnd = (int)(m_jNotes[i].getStemBeginPosition().getX()+m_metrics.getNoteWidth()/1.2);
				if (noteLinkEnd!=-1)
					context.drawLine((int)m_jNotes[i].getStemBeginPosition().getX(), noteLinkY, noteLinkEnd, noteLinkY);
			}
//			restore defaut stroke.
			context.setStroke(defaultStroke);
		}

		if (nUpletSize!=-1) {
			char[] chars = {m_metrics.getTupletDigitChar(nUpletSize)};
			//TODO replace with commented line but needs to be improved because of the get display position.
			//context.drawChars(chars, 0, 1, (int)(((JNote)m_jNotes[0]).getDisplayPosition().getX()+m_width/2), (int)(m_stemYend - m_metrics.getNoteHeigth()/4));
			Point2D ctrlP = null;
			if (isStemUp) {
				ctrlP = new Point2D.Double(
						m_jNotes[0].getBase().getX()+m_width/2,
						m_stemYend - m_metrics.getTupletNumberYOffset());
				context.drawChars(chars, 0, 1, (int)ctrlP.getX(), (int)ctrlP.getY());
			} else {
				//if group is ascending or descending
				//tuplet number Y = middle note minY-numberYOffset
				//straight line between start & end, if it doesn't
				//intersect notes
				Line2D line = new Line2D.Double(
						((JNote) m_jNotes[0]).getSlurAboveAnchor(),
						((JNote) m_jNotes[m_jNotes.length-1]).getSlurAboveAnchor()
					);
				boolean intersects = false;
				for (int i = 0; i < m_jNotes.length; i++) {
					if (line.intersects(m_jNotes[i].getBoundingBox())) {
						intersects = true;
						break;
					}
				}
				if (intersects) { //control point above bounds of the groupe
					ctrlP = new Point2D.Double(
						m_jNotes[0].getStemBeginPosition().getX()+(m_width-m_metrics.getNoteWidth())/2,
						getBoundingBox().getMinY() - m_metrics.getTupletNumberYOffset());
				} else {
					ctrlP = new Point2D.Double(
						m_jNotes[0].getStemBeginPosition().getX()+(m_width-m_metrics.getNoteWidth())/2,
						line.getBounds2D().getCenterY() - m_metrics.getTupletNumberYOffset());
				}
				context.drawChars(chars, 0, 1, (int)ctrlP.getX(), (int)ctrlP.getY());
			}
			ctrlP.setLocation(
				ctrlP.getX()+m_metrics.getTimeSignatureNumberWidth()/2,
				ctrlP.getY()-m_metrics.getTimeSignatureNumberHeight()-m_metrics.getTupletNumberYOffset());
			((JNoteElementAbstract) m_jNotes[0]).getJSlurDefinition()
				.setTupletControlPoint(ctrlP);
		}

		/* * /java.awt.Color previousColor = context.getColor();
		context.setColor(java.awt.Color.RED);
		context.draw(getBoundingBox());
		context.setColor(previousColor);/* */

		return m_width;
	}

	public Rectangle2D getBoundingBox() {
		Rectangle2D bb = new Rectangle2D.Double(getBase().getX(), getBase().getY(), 0, 0);
		for (int i = 0; i < m_jNotes.length; i++) {
			bb.add(((JNotePartOfGroup) m_jNotes[i]).getBoundingBox());
		}
		return bb;
	}

	public JScoreElement getScoreElementAt(Point point) {
		JScoreElement scoreEl = null;
		for (int i=0; i<m_jNotes.length; i++) {
			scoreEl = ((JScoreElement)m_jNotes[i]).getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}
}

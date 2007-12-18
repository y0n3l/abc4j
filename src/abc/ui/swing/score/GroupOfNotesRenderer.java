package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import abc.notation.Note;
import abc.notation.ScoreElementInterface;
import abc.ui.swing.JScoreElement;

/** This class is in charge of rendering a group of notes whose stems should be linked. */
public class GroupOfNotesRenderer extends JScoreElement {
	
	public static final char[] DIGITS = {
		'\uF0C1', 
		'\uF0AA',
		'\uF0A3',
		'\uF0A2',
		'\uF0B0',
		'\uF0A4',
		'\uF0A6',
		'\uF0A5',
		'\uF0BB'};
	/** All the notes that are part of the group. */
	protected Note[] m_notes = null;
	/** All the notes rendition elements that are part of the group. */
	protected JNotePartOfGroup[] m_sNoteInstances = null;
	/** The Y coordinate where the line linking all the notes is put. */
	protected int m_stemYend = -1;
	
	public GroupOfNotesRenderer(ScoreMetrics metrics, Point2D base, Note[] notes){
		super(metrics);
		if (notes.length<=1)
			throw new IllegalArgumentException(m_notes + "is not a group of notes, length = " + m_notes.length);
		m_notes = notes;
		//create JNotePartOfGroup instances. Those instance should stay the same
		//when the base is changed.
		m_sNoteInstances = new JNotePartOfGroup[m_notes.length];
		for (int i=0; i<notes.length; i++)
			m_sNoteInstances[i] = new JNotePartOfGroup(m_notes[i], new Point2D.Double(), m_metrics);
		//m_sNoteInstances[i]=n;
		setBase(base);
	}
	
	public ScoreElementInterface getScoreElement() {
		return null;
	}
	
	public void setStaffLine(StaffLine staffLine) {
		//If a group of notes if displayed on a staff line, all notes
		//composing the group are then part of this staff line as well. 
		for (int i=0; i<m_sNoteInstances.length; i++) 
			m_sNoteInstances[i].setStaffLine(staffLine);
		super.setStaffLine(staffLine);
	}
	
	Note[] getScoreElements() {
		return m_notes; 
	}
	
	JNotePartOfGroup[] getRenditionElements() {
		return m_sNoteInstances;
	}
	
	protected void onBaseChanged() {
		//m_sNoteInstances = new JNotePartOfGroup[m_notes.length];
		Point2D currentBase =(Point2D)m_base.clone();
		Note highestNote = Note.getHighestNote(m_notes);
		JNotePartOfGroup sn = new JNotePartOfGroup(highestNote, m_base, m_metrics);
		m_stemYend = sn.getStemYBegin()-m_metrics.getStemLength();
		JNotePartOfGroup firstNote = null;
		JNotePartOfGroup lastNote = null;
		for (int i=0; i<m_notes.length; i++) {
			short noteStrictDuration = m_notes[i].getStrictDuration();
			if (noteStrictDuration==Note.THIRTY_SECOND || noteStrictDuration==Note.SIXTEENTH || noteStrictDuration==Note.EIGHTH
					|| noteStrictDuration==Note.QUARTER){
				//JNotePartOfGroup n = new JNotePartOfGroup(m_notes[i], currentBase, m_metrics);
				//m_sNoteInstances[i]=n;
				Point2D updatedBase = m_sNoteInstances[i].getBase();
				updatedBase.setLocation(currentBase);
				m_sNoteInstances[i].setBase(updatedBase);
				if (i==0)
					firstNote = m_sNoteInstances[i];
				else
					if (i==m_notes.length-1)
						lastNote = m_sNoteInstances[i];
				m_sNoteInstances[i].setStemYEnd(m_stemYend);
				//int stemX = n.getStemX();
				//double width = n.render(context);
				m_width+=m_metrics.getNotesSpacing();
				currentBase.setLocation(currentBase.getX() + m_sNoteInstances[i].getWidth() + m_metrics.getNotesSpacing(), m_base.getY());
			}
		}
		if (lastNote==null)
			lastNote=firstNote;
		double firstNoteAccidentalWidth = (firstNote.getWidth()-m_metrics.getNoteWidth());
		m_width = (int)(lastNote.getStemX()-firstNote.getDisplayPosition().getX() + firstNoteAccidentalWidth);
	}
	
	public double render(Graphics2D context){
		//super.render(context);
		Stroke defaultStroke = context.getStroke();
		for (int i=0; i<m_sNoteInstances.length; i++) {
			JNotePartOfGroup n = m_sNoteInstances[i];
			n.render(context);
			BasicStroke notesLinkStroke = m_metrics.getNotesLinkStroke();
			context.setStroke(notesLinkStroke);
			short[] longerRhythms = null;
			short noteStrictDuration =  m_sNoteInstances[i].getNote().getStrictDuration();
			switch (noteStrictDuration) {
				case Note.EIGHTH : longerRhythms = new short[1]; longerRhythms[0] = Note.EIGHTH; break;
				case Note.SIXTEENTH : longerRhythms = new short[2]; longerRhythms[0] = Note.EIGHTH; longerRhythms[1] = Note.SIXTEENTH; break;
				case Note.THIRTY_SECOND: longerRhythms = new short[3]; longerRhythms[0] = Note.EIGHTH; longerRhythms[1] = Note.SIXTEENTH; longerRhythms[2] = Note.THIRTY_SECOND; break;
			}
			for (int j=0; j<longerRhythms.length; j++) {
				//decide where the end of the rhythm is.
				int noteLinkY = -1;
				if (longerRhythms[j]==Note.EIGHTH)
					noteLinkY = (int)(m_stemYend+notesLinkStroke.getLineWidth()/2.5);
				else
					if (longerRhythms[j]==Note.SIXTEENTH)
						noteLinkY = (int)(m_stemYend+notesLinkStroke.getLineWidth()*2);
					else
						if (longerRhythms[j]==Note.THIRTY_SECOND)
							noteLinkY = (int)(m_stemYend+notesLinkStroke.getLineWidth()*3.5);
				
				int noteLinkEnd = -1;
				// is there any note after ?
				boolean nextNoteIsShorterOrEquals = false;
				boolean previousNoteIsShorterOrEquals = false;
				boolean hasNext = i<m_notes.length-1; 
				boolean hasPrevious = i>0;
				if (hasNext)
					nextNoteIsShorterOrEquals = m_notes[i+1].getStrictDuration()<=longerRhythms[j];
				if (hasPrevious)
					previousNoteIsShorterOrEquals = m_notes[i-1].getStrictDuration()<=longerRhythms[j];
				if (hasPrevious) {
					if (previousNoteIsShorterOrEquals)
						//the end is the stem of the previous note.
						noteLinkEnd = ((JNotePartOfGroup)m_sNoteInstances[i-1]).getStemX();//getE (int)(stemX-2*context.getNoteWidth()); 
					else
						if (!(hasNext && nextNoteIsShorterOrEquals))
							noteLinkEnd = (int)(m_sNoteInstances[i].getStemX()-m_metrics.getNoteWidth()/2);
				}
				else
					if (!nextNoteIsShorterOrEquals)
						noteLinkEnd = (int)(m_sNoteInstances[i].getStemX()+m_metrics.getNoteWidth()/2);
				if (noteLinkEnd!=-1)
					context.drawLine(m_sNoteInstances[i].getStemX(), noteLinkY, noteLinkEnd, noteLinkY);
			}
//			restore defaut stroke.
			context.setStroke(defaultStroke);
		}
		
		if (m_sNoteInstances[0].getNote().getTuplet()!=null) {
			int nb = m_notes[0].getTuplet().getNotesAsVector().size();
			char[] chars = {DIGITS[nb-1]};
			context.drawChars(chars, 0, 1, (int)(m_sNoteInstances[0].getDisplayPosition().getX()+m_width/2), (int)(m_stemYend - m_metrics.getNoteHeigth()/4));
		}
		return m_width;
	}
	
	public JScoreElement getScoreElementAt(Point point) {
		JScoreElement scoreEl = null;
		for (int i=0; i<m_sNoteInstances.length; i++) {
			scoreEl = m_sNoteInstances[i].getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}
	
	/*public static double getOffset(Note note) {
		double positionOffset = 0;
		byte noteHeight = note.getStrictHeight();
		switch (noteHeight) {
			case Note.C : positionOffset = -1.5; break;
			case Note.D : positionOffset = -1;break;
			case Note.E : positionOffset = -0.5;break;
			case Note.F : positionOffset = 0;break;
			case Note.G : positionOffset = 0.5;break;
			case Note.A : positionOffset = 1;break;
			case Note.B : positionOffset = 1.5;break;
		}
		positionOffset = positionOffset + note.getOctaveTransposition()*3.5;
		//System.out.println("offset for " + note +"," + note.getOctaveTransposition() + " : " + positionOffset);
		return positionOffset;
	}*/
}

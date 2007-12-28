package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.ScoreElementInterface;
import abc.ui.swing.JScoreElement;
import abc.ui.swing.JScoreElementAbstract;

/** This class is in charge of rendering a group of notes whose stems should be linked. */
public class JGroupOfNotes extends JScoreElementAbstract {
	
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
	/** notes that are grouped */
	//protected Note[] anchorNotes = null;
	/** All the notes rendition elements that are part of the group. chords and / or notes*/
	protected JGroupableNote[] m_jNotes = null;
	/** The Y coordinate where the line linking all the notes is put. */
	protected int m_stemYend = -1;
	
	protected int nUpletSize = -1; 
	
	public JGroupOfNotes(ScoreMetrics metrics, Point2D base, NoteAbstract[] notes){
		super(metrics);
		if (notes.length<=1)
			throw new IllegalArgumentException(m_notes + "is not a group of notes, length = " + m_notes.length);
		m_notes = new Note[notes.length];
		//create JNotePartOfGroup instances. Those instance should stay the same
		//when the base is changed.
		m_jNotes = new JGroupableNote[m_notes.length];
		for (int i=0; i<notes.length; i++)
			if (notes[i] instanceof Note) {
				m_notes[i] = (Note)notes[i];
				m_jNotes[i] = new JNotePartOfGroup((Note)m_notes[i], new Point2D.Double(), m_metrics);
				//anchorNotes[i] = (Note)notes[i];
			}
			else {
				//This is a multiNote
				m_jNotes[i] = new JChordPartOfGroup((MultiNote)notes[i], m_metrics, new Point2D.Double());
				m_notes[i] = (Note)((JChordPartOfGroup)m_jNotes[i]).getReferenceNoteForGroup().getMusicElement();
				
			}
		//m_jNotes[i]=n;
		if (notes[0].getTuplet()!=null)
			nUpletSize = notes[0].getTuplet().getNotesAsVector().size();
		setBase(base);
	}
	
	public ScoreElementInterface getMusicElement() {
		return null;
	}
	
	public void setStaffLine(StaffLine staffLine) {
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
	
	protected void onBaseChanged() {
		//m_jNotes = new JNotePartOfGroup[m_notes.length];
		Point2D currentBase =(Point2D)m_base.clone();
		Note highestNote = Note.getHighestNote(m_notes);
		JNotePartOfGroup sn = new JNotePartOfGroup(highestNote, m_base, m_metrics);
		m_stemYend = sn.getStemYBegin()-m_metrics.getStemLength();
		JGroupableNote firstNote = null;
		JGroupableNote lastNote = null;
		for (int i=0; i<m_jNotes.length; i++) {
			//short noteStrictDuration = m_notes[i].getStrictDuration();
			//if (noteStrictDuration==Note.THIRTY_SECOND || noteStrictDuration==Note.SIXTEENTH || noteStrictDuration==Note.EIGHTH
			//		|| noteStrictDuration==Note.QUARTER){
				//JNotePartOfGroup n = new JNotePartOfGroup(m_notes[i], currentBase, m_metrics);
				//m_jNotes[i]=n;
				Point2D updatedBase = m_jNotes[i].getBase();
				updatedBase.setLocation(currentBase);
				((JScoreElementAbstract)m_jNotes[i]).setBase(updatedBase);
				if (i==0)
					firstNote = m_jNotes[i];
				else
					if (i==m_jNotes.length-1)
						lastNote = m_jNotes[i];
				m_jNotes[i].setStemYEnd(m_stemYend);
				//int stemX = n.getStemX();
				//double width = n.render(context);
				m_width+=m_metrics.getNotesSpacing();
				currentBase.setLocation(currentBase.getX() + m_jNotes[i].getWidth() + m_metrics.getNotesSpacing(), m_base.getY());
			//}
		}
		if (lastNote==null)
			lastNote=firstNote;
		double firstNoteAccidentalWidth = (firstNote.getWidth()-m_metrics.getNoteWidth());
		//TODO replace with commented line but needs to be improved because of the get display position.
		//m_width = (int)(lastNote.getStemX()-((JNote)firstNote).getDisplayPosition().getX() + firstNoteAccidentalWidth);
		m_width = (int)(lastNote.getStemX()-(firstNote).getBase().getX() + firstNoteAccidentalWidth);
	}
	
	public double render(Graphics2D context){
		//super.render(context);
		Stroke defaultStroke = context.getStroke();
		for (int i=0; i<m_jNotes.length; i++) {
			JGroupableNote n = m_jNotes[i];
			n.render(context);
			BasicStroke notesLinkStroke = m_metrics.getNotesLinkStroke();
			context.setStroke(notesLinkStroke);
			short[] longerRhythms = null;
			short noteStrictDuration =  m_notes[i].getStrictDuration();
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
				boolean hasNext = i<m_jNotes.length-1; 
				boolean hasPrevious = i>0;
				if (hasNext)
					nextNoteIsShorterOrEquals = m_notes[i+1].getStrictDuration()<=longerRhythms[j];
				if (hasPrevious)
					previousNoteIsShorterOrEquals = m_notes[i-1].getStrictDuration()<=longerRhythms[j];
				if (hasPrevious) {
					if (previousNoteIsShorterOrEquals)
						//the end is the stem of the previous note.
						noteLinkEnd = ((JGroupableNote)m_jNotes[i-1]).getStemX();//getE (int)(stemX-2*context.getNoteWidth()); 
					else
						if (!(hasNext && nextNoteIsShorterOrEquals))
							noteLinkEnd = (int)(m_jNotes[i].getStemX()-m_metrics.getNoteWidth()/2);
				}
				else
					if (!nextNoteIsShorterOrEquals)
						noteLinkEnd = (int)(m_jNotes[i].getStemX()+m_metrics.getNoteWidth()/2);
				if (noteLinkEnd!=-1)
					context.drawLine(m_jNotes[i].getStemX(), noteLinkY, noteLinkEnd, noteLinkY);
			}
//			restore defaut stroke.
			context.setStroke(defaultStroke);
		}
		
		if (nUpletSize!=-1) {
			char[] chars = {DIGITS[nUpletSize-1]};
			//TODO replace with commented line but needs to be improved because of the get display position.
			//context.drawChars(chars, 0, 1, (int)(((JNote)m_jNotes[0]).getDisplayPosition().getX()+m_width/2), (int)(m_stemYend - m_metrics.getNoteHeigth()/4));
			context.drawChars(chars, 0, 1, (int)(m_jNotes[0].getBase().getX()+m_width/2), (int)(m_stemYend - m_metrics.getNoteHeigth()/4));
		}
		return m_width;
	}
	
	public JScoreElementAbstract getScoreElementAt(Point point) {
		JScoreElementAbstract scoreEl = null;
		for (int i=0; i<m_jNotes.length; i++) {
			scoreEl = ((JScoreElementAbstract)m_jNotes[i]).getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}
}

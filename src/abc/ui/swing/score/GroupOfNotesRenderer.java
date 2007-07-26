package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Vector;

import abc.notation.Note;

public class GroupOfNotesRenderer extends SRenderer {
	
	protected Note[] m_notes = null;
	
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
	
	public GroupOfNotesRenderer(ScoreMetrics metrics, Point2D base, Note[] notes){
		super(base, metrics);
		m_notes = notes;
	}
	
	public double render(Graphics2D context){
		if (m_notes.length<=1)
			throw new IllegalArgumentException(m_notes + "is not a group of notes, length = " + m_notes.length);
		double cursorPosOffset = 0;
		m_base=(Point2D)m_base.clone();
		Note highestNote = Note.getHighestNote(m_notes);
		BasicStroke notesLinkStroke = m_metrics.getNotesLinkStroke();
		//BasicStroke stemStroke = context.getStemStroke();
		SNotePartOfGroup sn = new SNotePartOfGroup(highestNote, m_base, m_metrics);
		int stemYend = sn.getStemYBegin()-m_metrics.getStemLength();
		Stroke defaultStroke = context.getStroke();
		Vector sNoteInstances = new Vector();
		SNotePartOfGroup firstNote = null;
		SNotePartOfGroup lastNote = null;
		for (int i=0; i<m_notes.length; i++) {
			short noteStrictDuration = m_notes[i].getStrictDuration();
			if (noteStrictDuration==Note.THIRTY_SECOND || noteStrictDuration==Note.SIXTEENTH || noteStrictDuration==Note.EIGHTH
					|| noteStrictDuration==Note.QUARTER){
				SNotePartOfGroup n = new SNotePartOfGroup(m_notes[i], m_base, m_metrics);
				sNoteInstances.addElement(n);
				if (i==0)
					firstNote = n;
				else
					if (i==m_notes.length-1)
						lastNote = n;
				n.setStemYEnd(stemYend);
				int stemX = n.getStemX();
				int width = n.render(context);
				width+=m_metrics.getNotesSpacing();
				m_base.setLocation(m_base.getX()+width, m_base.getY());
				//context.getGraphics().drawLine(stemX, stemYbegin, stemX, stemYend);
				//===== draw rhythm info
				context.setStroke(notesLinkStroke);
				short[] longerRhythms = null;
				switch (noteStrictDuration) {
					case Note.EIGHTH : longerRhythms = new short[1]; longerRhythms[0] = Note.EIGHTH; break;
					case Note.SIXTEENTH : longerRhythms = new short[2]; longerRhythms[0] = Note.EIGHTH; longerRhythms[1] = Note.SIXTEENTH; break;
					case Note.THIRTY_SECOND: longerRhythms = new short[3]; longerRhythms[0] = Note.EIGHTH; longerRhythms[1] = Note.SIXTEENTH; longerRhythms[2] = Note.THIRTY_SECOND; break;
				}
				for (int j=0; j<longerRhythms.length; j++) {
					//decide where the end of the rhythm is.
					int noteLinkY = -1;
					if (longerRhythms[j]==Note.EIGHTH)
						noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()/2.5);
					else
						if (longerRhythms[j]==Note.SIXTEENTH)
							noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()*2);
						else
							if (longerRhythms[j]==Note.THIRTY_SECOND)
								noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()*3.5);
					
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
							noteLinkEnd = ((SNotePartOfGroup)sNoteInstances.elementAt((i-1))).getStemX();//getE (int)(stemX-2*context.getNoteWidth()); 
						else
							if (!(hasNext && nextNoteIsShorterOrEquals))
								noteLinkEnd = (int)(stemX-m_metrics.getNoteWidth()/2);
					}
					else
						if (!nextNoteIsShorterOrEquals)
							noteLinkEnd = (int)(stemX+m_metrics.getNoteWidth()/2);
					if (noteLinkEnd!=-1)
						context.drawLine(stemX, noteLinkY, noteLinkEnd, noteLinkY);
				}
				//restore defaut stroke.
				context.setStroke(defaultStroke);
				cursorPosOffset+=width;
			}
		}
		if (lastNote==null)
			lastNote=firstNote;
		int width = (int)(lastNote.getStemX()-firstNote.getNotePosition().getX());
		if (m_notes[0].getTuplet()!=null) {
			int nb = m_notes[0].getTuplet().getNotesAsVector().size();
			char[] chars = {DIGITS[nb-1]};
			context.drawChars(chars, 0, 1, (int)(firstNote.getNotePosition().getX()+width/2), (int)(stemYend - m_metrics.getNoteHeigth()/4));
		}
		return width;
	}
	
	public static double getOffset(Note note) {
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
	}
}

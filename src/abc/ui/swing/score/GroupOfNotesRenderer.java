package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

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
	
	protected SNotePartOfGroup[] m_sNoteInstances = null;
	
	protected int m_stemYend = -1;
	
	public GroupOfNotesRenderer(ScoreMetrics metrics, Point2D base, Note[] notes){
		super(base, metrics);
		m_notes = notes;
		m_sNoteInstances = new SNotePartOfGroup[notes.length];
		if (m_notes.length<=1)
			throw new IllegalArgumentException(m_notes + "is not a group of notes, length = " + m_notes.length);
		//double cursorPosOffset = 0;
		Point2D currentBase =(Point2D)m_base.clone();
		Note highestNote = Note.getHighestNote(m_notes);
		//BasicStroke notesLinkStroke = m_metrics.getNotesLinkStroke();
		//BasicStroke stemStroke = context.getStemStroke();
		SNotePartOfGroup sn = new SNotePartOfGroup(highestNote, m_base, m_metrics);
		m_stemYend = sn.getStemYBegin()-m_metrics.getStemLength();
		SNotePartOfGroup firstNote = null;
		SNotePartOfGroup lastNote = null;
		for (int i=0; i<m_notes.length; i++) {
			short noteStrictDuration = m_notes[i].getStrictDuration();
			if (noteStrictDuration==Note.THIRTY_SECOND || noteStrictDuration==Note.SIXTEENTH || noteStrictDuration==Note.EIGHTH
					|| noteStrictDuration==Note.QUARTER){
				SNotePartOfGroup n = new SNotePartOfGroup(m_notes[i], currentBase, m_metrics);
				m_sNoteInstances[i]=n;
				if (i==0)
					firstNote = n;
				else
					if (i==m_notes.length-1)
						lastNote = n;
				n.setStemYEnd(m_stemYend);
				//int stemX = n.getStemX();
				//double width = n.render(context);
				m_width+=m_metrics.getNotesSpacing();
				currentBase.setLocation(currentBase.getX() + n.getWidth() + metrics.getNotesSpacing(), m_base.getY());
			}
		}
		if (lastNote==null)
			lastNote=firstNote;
		m_width = (int)(lastNote.getStemX()-firstNote.getNotePosition().getX());
	}
	
	public double render(Graphics2D context){
		for (int i=0; i<m_sNoteInstances.length; i++) {
			SNotePartOfGroup n = m_sNoteInstances[i];
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
						noteLinkEnd = ((SNotePartOfGroup)m_sNoteInstances[i-1]).getStemX();//getE (int)(stemX-2*context.getNoteWidth()); 
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
		}
		if (m_sNoteInstances[0].getNote().getTuplet()!=null) {
			int nb = m_notes[0].getTuplet().getNotesAsVector().size();
			char[] chars = {DIGITS[nb-1]};
			context.drawChars(chars, 0, 1, (int)(m_sNoteInstances[0].getNotePosition().getX()+m_width/2), (int)(m_stemYend - m_metrics.getNoteHeigth()/4));
		}
		/*		width+=m_metrics.getNotesSpacing();
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
		}*/
		return m_width;
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

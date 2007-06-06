package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Vector;

import abc.notation.Note;

public class GroupOfNotesRenderer {
	
	public static double render(ScoreRenditionContext context, Point2D base, Note[] notes){
		if (notes.length<=1)
			throw new IllegalArgumentException(notes + "is not a group of notes, length = " + notes.length);
		double cursorPosOffset = 0;
		Note highestNote = Note.getHighestNote(notes);
		BasicStroke notesLinkStroke = context.getNotesLinkStroke();
		//BasicStroke stemStroke = context.getStemStroke();
		SNotePartOfGroup sn = new SNotePartOfGroup(highestNote, base, context);
		int stemYend = sn.getStemYBegin()-context.getStemLength();
		Stroke defaultStroke = context.getGraphics().getStroke();
		Vector sNoteInstances = new Vector();
		SNotePartOfGroup firstNote = null;
		SNotePartOfGroup lastNote = null;
		for (int i=0; i<notes.length; i++) {
			short noteStrictDuration = notes[i].getStrictDuration();
			if (noteStrictDuration==Note.THIRTY_SECOND || noteStrictDuration==Note.SIXTEENTH || noteStrictDuration==Note.EIGHTH
					|| noteStrictDuration==Note.QUARTER){
				SNotePartOfGroup n = new SNotePartOfGroup(notes[i], base, context);
				sNoteInstances.addElement(n);
				if (i==0)
					firstNote = n;
				else
					if (i==notes.length-1)
						lastNote = n;
				n.setStemYEnd(stemYend);
				int stemX = n.getStemX();
				int width = n.render(context, base);
				width+=context.getNotesSpacing();
				base.setLocation(base.getX()+width, base.getY());
				//context.getGraphics().drawLine(stemX, stemYbegin, stemX, stemYend);
				//===== draw rhythm info
				context.getGraphics().setStroke(notesLinkStroke);
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
					boolean hasNext = i<notes.length-1; 
					boolean hasPrevious = i>0;
					if (hasNext)
						nextNoteIsShorterOrEquals =  notes[i+1].getStrictDuration()<=longerRhythms[j];
					if (hasPrevious)
						previousNoteIsShorterOrEquals =  notes[i-1].getStrictDuration()<=longerRhythms[j];
					if (hasPrevious) {
						if (previousNoteIsShorterOrEquals)
							//the end is the stem of the previous note.
							noteLinkEnd = ((SNotePartOfGroup)sNoteInstances.elementAt((i-1))).getStemX();//getE (int)(stemX-2*context.getNoteWidth()); 
						else
							if (!(hasNext && nextNoteIsShorterOrEquals))
								noteLinkEnd = (int)(stemX-context.getNoteWidth()/2);
					}
					else
						if (!nextNoteIsShorterOrEquals)
							noteLinkEnd = (int)(stemX+context.getNoteWidth()/2);
					if (noteLinkEnd!=-1)
						context.getGraphics().drawLine(stemX, noteLinkY, noteLinkEnd, noteLinkY);
				}
				//restore defaut stroke.
				context.getGraphics().setStroke(defaultStroke);
				/*width = (int)(SingleNoteRenderer.getAccidentalRenditionWidth(context, base, notes[i])
					+ context.getNoteWidth() + SingleNoteRenderer.getOffsetAfterNoteRendition(context)) ;
				//update cursor pos*/
				cursorPosOffset+=width;
			}
		}
		if (lastNote==null)
			lastNote=firstNote;
		int width = (int)(lastNote.getStemX()-firstNote.getNotePosition().getX());
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

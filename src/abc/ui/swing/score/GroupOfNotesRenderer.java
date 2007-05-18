package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import abc.notation.Note;

public class GroupOfNotesRenderer {
	
	public static double render(ScoreRenditionContext context, Point2D base, Note[] notes){
		context.getGraphics().setColor(Color.BLUE);
		context.getGraphics().drawLine((int)base.getX(), (int)base.getY(), (int)base.getX(), (int)(base.getY()-50));
		context.getGraphics().setColor(Color.BLACK);
		System.out.println("rendering group of notes : " + notes.length);
		double cursorPosOffset = 0;
		Note highestNote = getHighestNote(notes);
		BasicStroke notesLinkStroke = new BasicStroke((float)(context.getNoteWidth()/3), 0, 0);
		int stemYend = (int)(SingleNoteRenderer.getNoteY(context, base, highestNote) - context.getNoteHeigth()*3);
		BasicStroke stemStroke = new BasicStroke((float)(context.getNoteWidth()/12));
	
		for (int i=0; i<notes.length; i++) {
			short noteStrictDuration = notes[i].getStrictDuration();
			if (noteStrictDuration==Note.THIRTY_SECOND || noteStrictDuration==Note.SIXTEENTH || noteStrictDuration==Note.EIGHTH
					|| noteStrictDuration==Note.QUARTER){
				//	===== draw note
				int noteX = (int)(SingleNoteRenderer.getNoteX(context, base, notes[i])+cursorPosOffset);
				int noteY = (int)(SingleNoteRenderer.getNoteY(context, base, notes[i]) - context.getNoteHeigth()/2);
				switch (notes[i].getStrictDuration()) {
					case Note.THIRTY_SECOND : noteY+=context.getNoteHeigth()*0.5;break;
					case Note.SIXTEENTH : noteY+=context.getNoteHeigth()*0.5;break;
					case Note.WHOLE: break;
				}
				SingleNoteRenderer.renderAccidentals(context, base, notes[i]);
				SingleNoteRenderer.renderDots(context, base, notes[i]);
				context.getGraphics().drawChars(ScoreRenditionContext.NOTE, 0, 1,noteX, noteY);
				// ===== draw stem
				int stemX = (int)(noteX + context.getNoteWidth() - stemStroke.getLineWidth()/2);
				int stemYbegin = (int)(noteY - context.getNoteHeigth()/6);
				Stroke defaultStroke = context.getGraphics().getStroke();
				context.getGraphics().setStroke(stemStroke);
				context.getGraphics().drawLine(stemX, stemYbegin, stemX, stemYend);
				//===== draw rhythm info
				context.getGraphics().setStroke(notesLinkStroke);
				short[] slowerRhythms = null;
				switch (noteStrictDuration) {
					case Note.EIGHTH : slowerRhythms = new short[1]; slowerRhythms[0] = Note.EIGHTH; break;
					case Note.SIXTEENTH : slowerRhythms = new short[2]; slowerRhythms[0] = Note.EIGHTH; slowerRhythms[1] = Note.SIXTEENTH; break;
					case Note.THIRTY_SECOND: slowerRhythms = new short[3]; slowerRhythms[0] = Note.EIGHTH; slowerRhythms[1] = Note.SIXTEENTH; slowerRhythms[2] = Note.THIRTY_SECOND; break;
				}
				for (int j=0; j<slowerRhythms.length; j++) {
					//decide where the end of the rhythm is.
					int noteLinkY = -1;
					if (slowerRhythms[j]==Note.EIGHTH)
						noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()/2);
					else
						if (slowerRhythms[j]==Note.SIXTEENTH)
							noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()*2);
						else
							if (slowerRhythms[j]==Note.THIRTY_SECOND)
								noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()*3.5);
					
					int noteLinkEnd = -1;
					if (i==0)
						noteLinkEnd = (int)(stemX+context.getNoteWidth()/2);
					else
						if (notes[i-1].getStrictDuration()<=slowerRhythms[j])
							//the end is the stem of the previous note.
							noteLinkEnd = (int)(stemX-2*context.getNoteWidth()); 
						else
							noteLinkEnd = (int)(stemX-context.getNoteWidth()/2);
							
					/*switch (noteStrictDuration) {
						case Note.THIRTY_SECOND : context.getGraphics().drawLine(
								(int)stemX, (int)(stemYend+notesLinkStroke.getLineWidth()*3.5), 
								(int)(stemX-context.getNoteWidth()), (int)(stemYend+notesLinkStroke.getLineWidth()*3.5));
						case Note.SIXTEENTH : context.getGraphics().drawLine(
								(int)stemX, (int)(stemYend+notesLinkStroke.getLineWidth()*2), 
								(int)(stemX-context.getNoteWidth()), (int)(stemYend+notesLinkStroke.getLineWidth()*2));
					}*/
					context.getGraphics().drawLine(
							(int)stemX, noteLinkY, 
							noteLinkEnd, noteLinkY);
				}
				//restore defaut stroke.
				context.getGraphics().setStroke(defaultStroke);
				int width = (int)(SingleNoteRenderer.getAccidentalRenditionWidth(context, base, notes[i])
					+ context.getNoteWidth() + SingleNoteRenderer.getOffsetAfterNoteRendition(context)) ;
				//update cursor pos
				cursorPosOffset+=width;
			}
		}
		context.getGraphics().setStroke(notesLinkStroke);
		//context.getGraphics().drawLine((int)startNoteX, (int)(stemYend+notesLinkStroke.getLineWidth()/2), (int)endNoteX, (int)(stemYend+notesLinkStroke.getLineWidth()/2));
		//int noteX = (int)(SingleNoteRenderer.getNoteX(context, base, highestNote)+cursorPosOffset);
		//int noteY = (int)(SingleNoteRenderer.getNoteY(context, base, highestNote) - context.getNoteHeigth()*4);
		/*switch (highestNote.getStrictDuration()) {
			case Note.THIRTY_SECOND : noteY+=context.getNoteHeigth()*0.5;break;
			case Note.SIXTEENTH : noteY+=context.getNoteHeigth()*0.5;break;
			case Note.WHOLE: break;
		}
		context.getGraphics().drawChars(ScoreRenditionContext.NOTE, 0, 1,noteX, noteY);*/
		//context.getGraphics().drawLine((int)(base.getX()+cursorPosOffset), (int)base.getY(), (int)(base.getX()+cursorPosOffset), (int)(base.getY()-50));
		context.getGraphics().setColor(Color.BLACK);
		return cursorPosOffset;
	}
	
	public static Note getHighestNote(Note[] notes) {
		Note highestNote = notes[0];
		for (int i=0; i<notes.length; i++) {
			if(getOffset(notes[i])>getOffset(highestNote)) {
				highestNote =notes[i];
				//System.out.println("highest note is" + i);
			}
		}
		return highestNote;
	}
	
	public static Note getLowestNote(Note[] notes) {
		Note lowestNote = notes[0];
		for (int i=0; i<notes.length; i++) {
			if(getOffset(notes[i])<getOffset(lowestNote)) {
				lowestNote =notes[i];
				//System.out.println("highest note is" + i);
			}
		}
		return lowestNote;
	}
	
	public static double getOffset(Note note) {
		double positionOffset = 0;
		byte noteHeight = note.getHeight();
		switch (noteHeight) {
			case Note.C : positionOffset = -1.5; break;
			case Note.D : positionOffset = -1;break;
			case Note.E : positionOffset = -0.5;break;
			case Note.F : positionOffset = 0;break;
			case Note.G : positionOffset = 0.5;break;
			case Note.A : positionOffset = 1;break;
			case Note.B : positionOffset = 1.5;break;
			case Note.c : positionOffset = 2;break;
			case Note.d : positionOffset = 2.5;break;
			case Note.e : positionOffset = 3;break;
			case Note.f : positionOffset = 3.5;break;
			case Note.g : positionOffset = 4;break;
			case Note.a : positionOffset = 4.5;break;
			case Note.b : positionOffset = 5;break;
		}
		return positionOffset;
	}
}

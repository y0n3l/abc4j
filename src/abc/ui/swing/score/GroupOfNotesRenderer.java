package abc.ui.swing.score;

import java.awt.Color;
import java.awt.geom.Point2D;

import abc.notation.Note;

public class GroupOfNotesRenderer {
	
	public static double render(ScoreRenditionContext context, Point2D base, Note[] notes){
		context.getGraphics().setColor(Color.BLUE);
		context.getGraphics().drawLine((int)base.getX(), (int)base.getY(), (int)base.getX(), (int)(base.getY()-50));
		context.getGraphics().setColor(Color.BLACK);
		System.out.println("rendering group of notes : " + notes.length);
		double stemUpLength = 0;
		double stemDownLength = 0;
		double cursorPosOffset = 0;
		for (int i=0; i<notes.length; i++) {
			short noteStrictDuration = notes[i].getStrictDuration();
			if (noteStrictDuration==Note.THIRTY_SECOND || noteStrictDuration==Note.SIXTEENTH || noteStrictDuration==Note.EIGHTH
					|| noteStrictDuration==Note.QUARTER){
				System.out.println("displaying note " + i);
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
				/*context.getGraphics().drawChars(ScoreRenditionContext.QUARTER_NOTE, 0, 1, 
						(int)(noteX+cursorPosOffset), noteY);*/
				int width = (int)(SingleNoteRenderer.getAccidentalRenditionWidth(context, base, notes[i])
					+ context.getNoteWidth() + SingleNoteRenderer.getOffsetAfterNoteRendition(context)) ;
				//System.out.println("cursor offset before "  + cursorPosOffset);
				cursorPosOffset+=width;
				//System.out.println("cursor offset "  + cursorPosOffset);
			}
		}
		context.getGraphics().setColor(Color.RED);
		context.getGraphics().drawLine((int)(base.getX()+cursorPosOffset), (int)base.getY(), (int)(base.getX()+cursorPosOffset), (int)(base.getY()-50));
		context.getGraphics().setColor(Color.BLACK);
		return cursorPosOffset;
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

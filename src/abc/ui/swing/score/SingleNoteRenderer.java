package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.Note;

public class SingleNoteRenderer {

	public static void render(ScoreRenditionContext context, Point2D base, Note note){
		double heightSpecificOffset = 0;
		char[] chars = new char[1];
		short noteDuration = note.getDuration();
		switch (noteDuration) {
			//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
			//Note.THIRTY_SECOND:chars[0] = ScoreRenditionContext.
			case Note.THIRTY_SECOND : chars[0] = ScoreRenditionContext.THIRTY_SECOND_NOTE; break;
			case Note.SIXTEENTH : chars[0] = ScoreRenditionContext.SIXTEENTH_NOTE; break;
			case Note.EIGHTH : chars[0] = ScoreRenditionContext.EIGHTH_NOTE; heightSpecificOffset=-context.getNoteHeigth()/2; break;
			case Note.QUARTER: chars[0] = ScoreRenditionContext.QUARTER_NOTE; heightSpecificOffset=-context.getNoteHeigth()/2; break;
			case Note.HALF: chars[0] = ScoreRenditionContext.HALF_NOTE; break;
			case Note.WHOLE: chars[0] = ScoreRenditionContext.WHOLE_NOTE; break;
		}
		byte noteHeight = note.getHeight();
		double positionOffset = 0;
		//CDEFGABcdefgabc'
		switch (noteHeight) {
			case Note.C : positionOffset = -1; break;
			case Note.D : positionOffset = -0.5;break;
			case Note.E : positionOffset = 0;break;
			case Note.F : positionOffset = 0.5;break;
			case Note.G : positionOffset = 1;break;
			case Note.A : positionOffset = 1.5;break;
			case Note.B : positionOffset = 2;break;
			case Note.c : positionOffset = 2.5;break;
			case Note.d : positionOffset = 3;break;
			case Note.e : positionOffset = 3.5;break;
			case Note.f : positionOffset = 4;break;
			case Note.g : positionOffset = 4.5;break;
			case Note.a : positionOffset = 5;break;
			case Note.b : positionOffset = 5.5;break;
		}
		System.out.println("delta to E for " + note + " : " + positionOffset);
		positionOffset = positionOffset*context.getNoteHeigth() + heightSpecificOffset;
		context.getGraphics().drawChars(chars, 0, chars.length, 
				(int)base.getX(), 
				(int)(base.getY()-positionOffset));
	}
}

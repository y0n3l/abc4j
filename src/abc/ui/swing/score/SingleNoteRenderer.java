package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.AccidentalType;
import abc.notation.Note;

public class SingleNoteRenderer {

	public static double render(ScoreRenditionContext context, Point2D base, Note note){
		double totalWidth= 0;
		//double rhythmSpecificOffset = 0;
		char[] chars = null;
		//byte noteHeight = note.getHeight();
		//double positionOffset = 0;
		//The basic display of the quarter note gives a F
		//CDEFGABcdefgabc'
		/*switch (noteHeight) {
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
		}*/
		//System.out.println("delta to E for " + note + " : " + positionOffset);
		//positionOffset = positionOffset*context.getNoteHeigth();
		int noteY = (int)(base.getY()-getOffset(note)*context.getNoteHeigth());
		int strokeY = (int)(noteY-context.getNoteHeigth()/2.6);
		int dotY = (int)(noteY-context.getNoteHeigth()/6);
		if (note.getAccidental()!=AccidentalType.NONE) {
			if (note.getAccidental()==AccidentalType.FLAT)
				chars = ScoreRenditionContext.FLAT;
			else
				if (note.getAccidental()==AccidentalType.SHARP)
					chars = ScoreRenditionContext.SHARP; 
				else
					if (note.getAccidental()==AccidentalType.NATURAL)
						chars = ScoreRenditionContext.NATURAL;
			context.getGraphics().drawChars(chars, 0, chars.length, 
				(int)(base.getX()), 
				(int)(noteY-context.getNoteHeigth()/2));
			totalWidth+=context.getNoteWidth();
		}
		short noteDuration = note.getStrictDuration();
		switch (noteDuration) {
			//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
			case Note.THIRTY_SECOND : chars = ScoreRenditionContext.THIRTY_SECOND_NOTE; break;
			case Note.SIXTEENTH : chars = ScoreRenditionContext.SIXTEENTH_NOTE; break;
			case Note.EIGHTH : chars = ScoreRenditionContext.EIGHTH_NOTE; break;
			case Note.QUARTER: chars = ScoreRenditionContext.QUARTER_NOTE; break;
			case Note.HALF: chars = ScoreRenditionContext.HALF_NOTE; break;
			case Note.WHOLE: chars = ScoreRenditionContext.WHOLE_NOTE; break;
		}
		int noteX = (int)(base.getX()+((note.getAccidental()!=AccidentalType.NONE)?context.getNoteWidth():0));
		//int noteY = (int)(base.getY()-positionOffset+rhythmSpecificOffset);
		//int noteY = (int)(base.getY()-getOffset(note)*context.getNoteHeigth());
		context.getGraphics().drawChars(chars, 0, chars.length, noteX, noteY);
		if (note.getHeight()==Note.C || note.getHeight()==Note.a)
			context.getGraphics().drawChars(context.STROKE, 0, 1, (int)(noteX-context.getNoteWidth()/4), strokeY);
		totalWidth+=context.getNoteWidth();
		if (note.countDots()!=0){
			context.getGraphics().drawChars(ScoreRenditionContext.DOT, 0, chars.length, 
				(int)(base.getX()
					+context.getNoteWidth()+((note.getAccidental()!=AccidentalType.NONE)?context.getNoteWidth():0)), 
					dotY);
		}
		return totalWidth;
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
		short noteDuration = note.getStrictDuration();
		switch (noteDuration) {
			case Note.THIRTY_SECOND : positionOffset=positionOffset+0.5; break;
			case Note.SIXTEENTH : positionOffset=positionOffset+0.5; break;
			case Note.WHOLE: positionOffset=positionOffset+0.5; break;
		}
		return positionOffset;
	}
}

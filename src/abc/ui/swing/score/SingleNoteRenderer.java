package abc.ui.swing.score;

import java.awt.Color;
import java.awt.geom.Point2D;

import abc.notation.AccidentalType;
import abc.notation.Note;

public class SingleNoteRenderer {

	public static double render(ScoreRenditionContext context, Point2D base, Note note){
		context.getGraphics().setColor(Color.BLUE);
		context.getGraphics().drawLine((int)base.getX(), (int)base.getY(), (int)base.getX(), (int)(base.getY()-50));
		context.getGraphics().setColor(Color.BLACK);
		double totalWidth= 0;
		char[] chars = null;
		//System.out.println("delta to E for " + note + " : " + positionOffset);
		int noteY = getNoteY(context, base, note);//(int)(base.getY()-getOffset(note)*context.getNoteHeigth());
		int strokeY = (int)(noteY-context.getNoteHeigth()/2.6);
		if (note.getAccidental()!=AccidentalType.NONE) {
			totalWidth+=renderAccidentals(context, base, note);
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
		int noteX = getNoteX(context, base, note);
		context.getGraphics().drawChars(chars, 0, chars.length, noteX, noteY);
		if (note.getHeight()==Note.C || note.getHeight()==Note.a)
			context.getGraphics().drawChars(ScoreRenditionContext.STROKE, 0, 1, (int)(noteX-context.getNoteWidth()/4), strokeY);
		totalWidth+=context.getNoteWidth();
		if (note.countDots()!=0){
			renderDots(context, base, note);
		}
		//context.getGraphics().setColor(Color.RED);
		//context.getGraphics().drawLine((int)(base.getX()+totalWidth), (int)base.getY(), (int)(base.getX()+totalWidth), (int)(base.getY()-50));
		//context.getGraphics().setColor(Color.BLACK);
		return totalWidth;
	}
	
	public static double renderAccidentals(ScoreRenditionContext context, Point2D base, Note note){
		char[] chars=null;
		int noteY = getNoteY(context, base, note);
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
		}
		return getAccidentalRenditionWidth(context, base, note);
	}
	
	public static void renderDots(ScoreRenditionContext context, Point2D base, Note note){
		int noteY = getNoteY(context, base, note);
		int dotY = (int)(noteY-context.getNoteHeigth()/6);
		if (note.countDots()!=0){
			context.getGraphics().drawChars(ScoreRenditionContext.DOT, 0, 1, 
				(int)(base.getX()
					+context.getNoteWidth()+((note.getAccidental()!=AccidentalType.NONE)?context.getNoteWidth():0)), 
					dotY);
		}
	}
	
	public static int getNoteY(ScoreRenditionContext context, Point2D base, Note note){
		return (int)(base.getY()-getOffset(note)*context.getNoteHeigth());
	}
	
	public static int getNoteX(ScoreRenditionContext context, Point2D base, Note note){
		return (int)(base.getX()+((note.getAccidental()!=AccidentalType.NONE)?context.getNoteWidth():0));
	}
	
	/*public static int getNoteRenditionWidth(ScoreRenditionContext context, Point2D base, Note note){
		return (int)(base.getY()-getOffset(note)*context.getNoteHeigth());
	}*/
	
	public static double getAccidentalRenditionWidth(ScoreRenditionContext context, Point2D base, Note note) {
		if (note.getAccidental()!=AccidentalType.NONE)
			return context.getNoteWidth();
		else
			return 0;
	}
	
	public static double getOffsetAfterNoteRendition(ScoreRenditionContext context) {
		return context.getNoteWidth();
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

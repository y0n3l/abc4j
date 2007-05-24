package abc.ui.swing.score;

import java.awt.Color;
import java.awt.geom.Point2D;

import abc.notation.AccidentalType;
import abc.notation.KeySignature;
import abc.notation.Note;

public class SKeySignature {
	KeySignature key = null;
	Point2D FPosition = null; 
	Point2D CPosition = null;
	Point2D GPosition = null;
	Point2D DPosition = null;
	Point2D APosition = null;
	Point2D EPosition = null;
	Point2D BPosition = null;
	
	
	public SKeySignature(KeySignature keyV, Point2D base, ScoreRenditionContext c) {
		key = keyV;
		double FPositionX = base.getX();
		double FPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.f, AccidentalType.NONE))*c.getNoteHeigth();
		FPosition = new Point2D.Double(FPositionX, FPositionY);
		double GPositionX = base.getX()+c.getSharpBounds().getWidth();
		double GPositionY = base.getY() - SNotePartOfGroup.getOffset(new Note(Note.g, AccidentalType.NONE))*c.getNoteHeigth();
		GPosition = new Point2D.Double(GPositionX, GPositionY);
		
	}
	
	public int render(ScoreRenditionContext context, Point2D base){
		context.getGraphics().drawChars(ScoreRenditionContext.SHARP, 0, 1, (int)FPosition.getX(), (int)FPosition.getY());
		context.getGraphics().drawChars(ScoreRenditionContext.SHARP, 0, 1, (int)GPosition.getX(), (int)GPosition.getY());
		
		return (int)context.getSharpBounds().getWidth();
	}
	
	
}

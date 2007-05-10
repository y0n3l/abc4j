package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.KeySignature;

public class ClefRenderer {
	
	public static void render(ScoreRenditionContext context, Point2D base, KeySignature key){
		char[] chars = {ScoreRenditionContext.G_CLEF};
		context.getGraphics().drawChars(chars, 0, chars.length, 
				(int)base.getX(), (int)(base.getY()-context.getNoteHeigth()));
		char[] chars2 = {ScoreRenditionContext.STAFF_SIX_LINES};
		context.getGraphics().drawChars(chars2, 0, chars2.length, 
				(int)base.getX(), (int)(base.getY()));
	}
}

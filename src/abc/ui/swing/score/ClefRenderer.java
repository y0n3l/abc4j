package abc.ui.swing.score;

import java.awt.Color;
import java.awt.geom.Point2D;

public class ClefRenderer {
	
	public static double render(ScoreRenditionContext context, Point2D base){
		char[] chars2 = {ScoreRenditionContext.STAFF_SIX_LINES};
		context.getGraphics().drawChars(chars2, 0, chars2.length, 
				(int)base.getX(), (int)(base.getY()));
		char[] chars = {ScoreRenditionContext.G_CLEF};
		context.getGraphics().drawChars(chars, 0, chars.length, 
				(int)base.getX(), (int)(base.getY()-context.getNoteHeigth()));
		double width = context.getFont().getStringBounds(chars, 0, 1, context.getGraphics().getFontRenderContext()).getWidth();
		/*context.getGraphics().setColor(Color.GREEN);
		context.getGraphics().drawLine((int)base.getX(), (int)base.getY(), (int)(base.getX()+width), (int)base.getY());
		context.getGraphics().setColor(Color.BLACK);*/

		return 3*context.getNoteWidth();
	}
}

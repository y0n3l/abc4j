package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.BarLine;

public class BarRenderer {
	
	public static double render(ScoreRenditionContext context, Point2D base, BarLine bar){
		context.getGraphics().drawChars(ScoreRenditionContext.BAR_LINE, 0, 1, 
				(int)(base.getX()+context.getNoteWidth()/3), (int)(base.getY()));
		return context.getNoteWidth();
	}
}

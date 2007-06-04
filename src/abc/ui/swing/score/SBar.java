package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.BarLine;

public class SBar {
	
	protected BarLine m_barLine =null;
	
	public SBar(BarLine barLine, Point2D base, ScoreRenditionContext c) {
		m_barLine = barLine; 
	}
	
	public int render(ScoreRenditionContext context, Point2D base){
		int width = 0;
		int height = (int)context.getStaffCharBounds().getHeight();
		int barWidth = (int)(context.getNoteWidth()*0.5);
		int spaceBetween = (int)(context.getNoteWidth()*0.2);
		//System.out.println("space between = " + spaceBetween);
		switch (m_barLine.getType()) {
			case BarLine.REPEAT_OPEN : 
				int circleD = (int)(context.getNoteWidth()*0.3);
				context.getGraphics().fillRect((int)base.getX(), (int)base.getY()-height, 
												barWidth, height);
				context.getGraphics().drawLine((int)(base.getX()+barWidth + spaceBetween), (int)(base.getY()-height),
												(int)(base.getX()+barWidth + spaceBetween), (int)(base.getY()));
				context.getGraphics().fillOval((int)(base.getX()+barWidth + 2*spaceBetween), (int)(base.getY()-height*0.61), 
						circleD, circleD);
				context.getGraphics().fillOval((int)(base.getX()+barWidth + 2*spaceBetween), (int)(base.getY()-height*0.4), 
						circleD, circleD);
				width = barWidth+2*spaceBetween+circleD;
				break;
			case BarLine.REPEAT_CLOSE : 
				circleD = (int)(context.getNoteWidth()*0.3);
				context.getGraphics().fillOval((int)(base.getX()), (int)(base.getY()-height*0.61), 
													circleD, circleD);
				context.getGraphics().fillOval((int)(base.getX()), (int)(base.getY()-height*0.4), 
													circleD, circleD);
				context.getGraphics().drawLine((int)(base.getX()+circleD+spaceBetween), (int)(base.getY()-height),
												(int)(base.getX()+circleD+spaceBetween), (int)(base.getY()));
				context.getGraphics().fillRect((int)(base.getX()+circleD+2*spaceBetween), (int)base.getY()-height, 
												barWidth, height);
				width = barWidth+2*spaceBetween+circleD;
				break;
			case BarLine.SIMPLE : context.getGraphics().drawChars(ScoreRenditionContext.BAR_LINE, 0, 1, 
					(int)(base.getX()+context.getNoteWidth()/3), (int)(base.getY()));
				width = 0;
				break;
		}
		return width;
	}
	
	
}

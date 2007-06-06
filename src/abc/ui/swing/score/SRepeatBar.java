package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.RepeatBarLine;

public class SRepeatBar extends SBar{

	public SRepeatBar(RepeatBarLine barLine, Point2D base, ScoreRenditionContext c) {
		super(barLine,base, c); 
	}
	
	public int render(ScoreRenditionContext context, Point2D base){
		char[] ch = STimeSignature.getChar(((RepeatBarLine)m_barLine).getRepeatNumber());
		context.getGraphics().drawChars(ch, 0, 1, 
				(int)(base.getX()+context.getNoteWidth()), 
				(int)(base.getY()-context.getStaffCharBounds().getHeight()*1.3));
		context.getGraphics().drawLine(
				(int)(base.getX()+context.getNoteWidth()/2), 
				(int)(base.getY()-context.getStaffCharBounds().getHeight()*1.1), 
				(int)(base.getX()+context.getNoteWidth()/2), 
				(int)(base.getY()-context.getStaffCharBounds().getHeight()*1.7));
		context.getGraphics().drawLine(
				(int)(base.getX()+context.getNoteWidth()/2), 
				(int)(base.getY()-context.getStaffCharBounds().getHeight()*1.7), 
				(int)(base.getX()+context.getNoteWidth()/2+context.getStaffCharWidth()), 
				(int)(base.getY()-context.getStaffCharBounds().getHeight()*1.7));
		return super.render(context, base);
		
	}
}

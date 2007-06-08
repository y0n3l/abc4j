package abc.ui.swing.score;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import abc.notation.BarLine;

public class SBar extends SRenderer{
	
	protected BarLine m_barLine = null;
	
	public SBar(BarLine barLine, Point2D base, ScoreMetrics c) {
		super (base, c);
		m_barLine = barLine; 
	}
	
	public int render(Graphics context){
		int width = 0;
		int height = (int)m_metrics.getStaffCharBounds().getHeight();
		int barWidth = (int)(m_metrics.getNoteWidth()*0.5);
		int spaceBetween = (int)(m_metrics.getNoteWidth()*0.2);
		//System.out.println("space between = " + spaceBetween);
		switch (m_barLine.getType()) {
			case BarLine.REPEAT_OPEN : 
				int circleD = (int)(m_metrics.getNoteWidth()*0.3);
				context.fillRect((int)m_base.getX(), (int)m_base.getY()-height, 
												barWidth, height);
				context.drawLine((int)(m_base.getX()+barWidth + spaceBetween), (int)(m_base.getY()-height),
												(int)(m_base.getX()+barWidth + spaceBetween), (int)(m_base.getY()));
				context.fillOval((int)(m_base.getX()+barWidth + 2*spaceBetween), (int)(m_base.getY()-height*0.61), 
						circleD, circleD);
				context.fillOval((int)(m_base.getX()+barWidth + 2*spaceBetween), (int)(m_base.getY()-height*0.4), 
						circleD, circleD);
				width = barWidth+2*spaceBetween+circleD;
				break;
			case BarLine.REPEAT_CLOSE : 
				circleD = (int)(m_metrics.getNoteWidth()*0.3);
				context.fillOval((int)(m_base.getX()), (int)(m_base.getY()-height*0.61), 
													circleD, circleD);
				context.fillOval((int)(m_base.getX()), (int)(m_base.getY()-height*0.4), 
													circleD, circleD);
				context.drawLine((int)(m_base.getX()+circleD+spaceBetween), (int)(m_base.getY()-height),
												(int)(m_base.getX()+circleD+spaceBetween), (int)(m_base.getY()));
				context.fillRect((int)(m_base.getX()+circleD+2*spaceBetween), (int)m_base.getY()-height, 
												barWidth, height);
				width = barWidth+2*spaceBetween+circleD;
				break;
			case BarLine.SIMPLE : context.drawChars(ScoreMetrics.BAR_LINE, 0, 1, 
					(int)(m_base.getX()+m_metrics.getNoteWidth()/3), (int)(m_base.getY()));
				width = 0;
				break;
		}
		return width;
	}
	
	
}

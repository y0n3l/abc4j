package abc.	ui.swing.score;

//import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.notation.BarLine;

public class SBar extends SRenderer{
	/** The encapsulated abc notation bar element */
	protected BarLine m_barLine = null;

	protected int m_barWidth = -1;
	//dots in case of repeat only.
	protected int m_barDotsSpacing = -1;
	protected int m_dotsRadius = -1;
	protected int m_topDotY = -1;
	protected int m_bottomDotY = -1;
	
	public SBar(BarLine barLine, Point2D base, ScoreMetrics c) {
		super (base, c);
		m_barLine = barLine; 
		onBaseChanged();
	}
	
	protected void onBaseChanged() {
		m_dotsRadius = (int)(m_metrics.getNoteWidth()*0.3);
		m_barWidth = (int)(m_metrics.getNoteWidth()*0.5);
		m_barDotsSpacing = (int)(m_metrics.getNoteWidth()*0.2);
		int height = (int)m_metrics.getStaffCharBounds().getHeight();
		m_topDotY = (int)(m_base.getY()-height*0.61);
		m_bottomDotY = (int)(m_base.getY()-height*0.4);
		switch (m_barLine.getType()) {
			case BarLine.SIMPLE :  
				m_width = 0;
				break;
			case BarLine.REPEAT_OPEN : 
				m_width = m_barWidth+2*m_barDotsSpacing+m_dotsRadius;
				break;
			case BarLine.REPEAT_CLOSE : 
				m_width = m_barWidth+2*m_barDotsSpacing+m_dotsRadius;
				break;
		}
	}
	
	public double render(Graphics2D context){
		super.render(context);
		int height = (int)m_metrics.getStaffCharBounds().getHeight();
		//System.out.println("space between = " + m_barDotsSpacing);
		switch (m_barLine.getType()) {
			case BarLine.REPEAT_OPEN : 
				context.fillRect((int)m_base.getX(), (int)m_base.getY()-height, m_barWidth, height);
				context.drawLine((int)(m_base.getX()+m_barWidth + m_barDotsSpacing), (int)(m_base.getY()-height),
												(int)(m_base.getX()+m_barWidth + m_barDotsSpacing), (int)(m_base.getY()));
				context.fillOval((int)(m_base.getX()+m_barWidth + 2*m_barDotsSpacing), m_topDotY, m_dotsRadius, m_dotsRadius);
				context.fillOval((int)(m_base.getX()+m_barWidth + 2*m_barDotsSpacing), m_bottomDotY, m_dotsRadius, m_dotsRadius);
				break;
			case BarLine.REPEAT_CLOSE : 
				context.fillOval((int)(m_base.getX()), (int)(m_base.getY()-height*0.61), 
													m_dotsRadius, m_dotsRadius);
				context.fillOval((int)(m_base.getX()), (int)(m_base.getY()-height*0.4), 
													m_dotsRadius, m_dotsRadius);
				context.drawLine((int)(m_base.getX()+m_dotsRadius+m_barDotsSpacing), (int)(m_base.getY()-height),
												(int)(m_base.getX()+m_dotsRadius+m_barDotsSpacing), (int)(m_base.getY()));
				context.fillRect((int)(m_base.getX()+m_dotsRadius+2*m_barDotsSpacing), (int)m_base.getY()-height, 
												m_barWidth, height);
				break;
			case BarLine.SIMPLE : context.drawChars(ScoreMetrics.BAR_LINE, 0, 1, 
					(int)(m_base.getX()+m_metrics.getNoteWidth()/3), (int)(m_base.getY()));
				break;
		}
		return m_width;
	}
	
	
}

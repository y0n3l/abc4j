package abc.ui.swing.score;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.notation.RepeatBarLine;

public class SRepeatBar extends SBar{
	
	public static final char[][] DIGITS = {
			{'\uF0C1', '\uF02E'}, 
			{'\uF0AA', '\uF02E'},
			{'\uF0A3', '\uF02E'},
			{'\uF0A2', '\uF02E'},
			{'\uF0B0', '\uF02E'},
			{'\uF0A4', '\uF02E'},
			{'\uF0A6', '\uF02E'},
			{'\uF0A5', '\uF02E'},
			{'\uF0BB', '\uF02E'}};

	public SRepeatBar(RepeatBarLine barLine, Point2D base, ScoreMetrics c) {
		super(barLine,base, c); 
	}
	
	public double render(Graphics2D context){
		char[] ch = DIGITS[((RepeatBarLine)m_barLine).getRepeatNumber()-1];
		context.drawChars(ch, 0, ch.length, 
				(int)(m_base.getX()+m_metrics.getNoteWidth()), 
				(int)(m_base.getY()-m_metrics.getStaffCharBounds().getHeight()*1.3));
		context.drawLine(
				(int)(m_base.getX()+m_metrics.getNoteWidth()/2), 
				(int)(m_base.getY()-m_metrics.getStaffCharBounds().getHeight()*1.1), 
				(int)(m_base.getX()+m_metrics.getNoteWidth()/2), 
				(int)(m_base.getY()-m_metrics.getStaffCharBounds().getHeight()*1.7));
		context.drawLine(
				(int)(m_base.getX()+m_metrics.getNoteWidth()/2), 
				(int)(m_base.getY()-m_metrics.getStaffCharBounds().getHeight()*1.7), 
				(int)(m_base.getX()+m_metrics.getNoteWidth()/2+m_metrics.getStaffCharBounds().getWidth()), 
				(int)(m_base.getY()-m_metrics.getStaffCharBounds().getHeight()*1.7));
		return super.render(context);
		
	}
}

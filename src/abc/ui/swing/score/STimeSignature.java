package abc.ui.swing.score;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import abc.notation.TimeSignature;

public class STimeSignature extends SRenderer {
	
	public static final char[][] DIGITS = {
			{'\uF031'},
			{'\uF032'},
			{'\uF033'},
			{'\uF034'},
			{'\uF035'},
			{'\uF036'},
			{'\uF037'},
			{'\uF038'},
			{'\uF039'}};
	
	
	private TimeSignature m_ts = null;
	
	public STimeSignature(TimeSignature ts, Point2D base, ScoreMetrics c) {
		super(base, c);
		m_ts = ts;
	}
	
	public double render(Graphics context){
		//System.out.println("base for time : " + base);
		try {
			char[] numChars = DIGITS[m_ts.getNumerator()-1];
			char[] denomChars = DIGITS[m_ts.getDenominator()-1];
			context.drawChars(numChars, 0, 1, 
				(int)m_base.getX(), (int)(m_base.getY()-m_metrics.getNoteHeigth()*3.1));
			context.drawChars(denomChars, 0, 1, 
				(int)m_base.getX(), (int)(m_base.getY()-m_metrics.getNoteHeigth()*0.9));
			return m_metrics.getNoteWidth();
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return 0;
		}
	}
}

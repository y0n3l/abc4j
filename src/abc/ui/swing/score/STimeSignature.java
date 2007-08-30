package abc.ui.swing.score;

import java.awt.Graphics2D;
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
	
	
	protected TimeSignature m_ts = null;
	
	protected char[] m_numChars = null;
	protected char[] m_denomChars = null;
	protected int m_topNumY = -1;
	protected int m_bottomNumY = -1;

	public STimeSignature(TimeSignature ts, Point2D base, ScoreMetrics c) {
		super(base, c);
		m_ts = ts;
		m_numChars = DIGITS[m_ts.getNumerator()-1];
		m_denomChars = DIGITS[m_ts.getDenominator()-1];
		m_topNumY = (int)(m_base.getY()-m_metrics.getNoteHeigth()*3.1);
		m_bottomNumY = (int)(m_base.getY()-m_metrics.getNoteHeigth()*0.9);
		m_width = m_metrics.getNoteWidth();
	}
	
	public double render(Graphics2D context){
		super.render(context);
		context.drawChars(m_numChars, 0, 1, (int)m_base.getX(), m_topNumY);
		context.drawChars(m_denomChars, 0, 1, (int)m_base.getX(), m_bottomNumY);
		return m_width;
	}
}


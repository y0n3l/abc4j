package abc.ui.swing;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.notation.MusicElement;
import abc.notation.TimeSignature;

/** This class is in charge of rendering a time signature. */
class JTimeSignature extends JScoreElementAbstract {
	
	public static final char[][] DIGITS = {
			{'\uF030'}, //0
			{'\uF031'}, //1
			{'\uF032'}, //2
			{'\uF033'}, //3
			{'\uF034'}, //4
			{'\uF035'}, //5
			{'\uF036'}, //6
			{'\uF037'}, //7
			{'\uF038'}, //8
			{'\uF039'}, //9
			{'\uF031','\uF030'}, //10
			{'\uF031','\uF031'}, //11
			{'\uF031','\uF032'}, //12
			{'\uF031','\uF033'}  //13
			};
	
	
	protected TimeSignature m_ts = null;
	
	protected char[] m_numChars = null;
	protected char[] m_denomChars = null;
	protected int m_topNumY = -1;
	protected int m_bottomNumY = -1;

	public JTimeSignature(TimeSignature ts, Point2D base, ScoreMetrics c) {
		super(c);
		m_ts = ts;
		m_numChars = DIGITS[m_ts.getNumerator()];
		m_denomChars = DIGITS[m_ts.getDenominator()];
		if (m_ts.getNumerator()>=10 || m_ts.getDenominator()>=10)
			m_width = 2*m_metrics.getNoteWidth();
		else
			m_width = m_metrics.getNoteWidth();
		setBase(base);
	}
	
	public MusicElement getMusicElement() {
		return m_ts;
	}
	
	protected void onBaseChanged() {
		//FIXME what if the signature numbers are not supported ? => arrayOutOfBounds ! :/
		m_topNumY = (int)(m_base.getY()-m_metrics.getNoteHeigth()*3.0);
		m_bottomNumY = (int)(m_base.getY()-m_metrics.getNoteHeigth()*0.9);
	}
	
	public double render(Graphics2D context){
		super.render(context);
		context.drawChars(m_numChars, 0, m_numChars.length, (int)m_base.getX(), m_topNumY);
		context.drawChars(m_denomChars, 0, m_denomChars.length, (int)m_base.getX(), m_bottomNumY);
		return m_width;
	}
}


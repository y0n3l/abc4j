package abc.ui.swing;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.notation.MusicElement;

/** This class is in charge of rendering a staff clef. */
class JClef extends JScoreElementAbstract {
	
	public static final char G_CLEF = '\uF026';
	
	//protected BarLine m_barLine = null;
	
	public JClef(Point2D base, ScoreMetrics c) {
		super (c);
		m_width = 3*m_metrics.getNoteWidth();
		setBase(base);
	}
	
	protected void onBaseChanged() {
	}
	
	public MusicElement getMusicElement() {
		return null;
	}
	
	public double render(Graphics2D context){
		super.render(context);
		/*char[] chars2 = {ScoreMetrics.STAFF_SIX_LINES};
		context.drawChars(chars2, 0, chars2.length, 
				(int)m_base.getX(), (int)(m_base.getY()));*/
		char[] chars = {G_CLEF};
		context.drawChars(chars, 0, chars.length, 
				(int)m_base.getX(), (int)(m_base.getY()-m_metrics.getNoteHeigth()));
		return m_width;
	}
}

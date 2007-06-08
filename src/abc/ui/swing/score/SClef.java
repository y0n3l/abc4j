package abc.ui.swing.score;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import abc.notation.BarLine;

public class SClef extends SRenderer {
	
	public static final char G_CLEF = '\uF026';
	
	protected BarLine m_barLine = null;
	
	public SClef(Point2D base, ScoreMetrics c) {
		super (base, c);
	}
	
	public int render(Graphics context){
		char[] chars2 = {ScoreMetrics.STAFF_SIX_LINES};
		context.drawChars(chars2, 0, chars2.length, 
				(int)m_base.getX(), (int)(m_base.getY()));
		char[] chars = {G_CLEF};
		context.drawChars(chars, 0, chars.length, 
				(int)m_base.getX(), (int)(m_base.getY()-m_metrics.getNoteHeigth()));
		//double width = context.getFont().getStringBounds(chars, 0, 1, context.getFontRenderContext()).getWidth();
		/*context.getGraphics().setColor(Color.GREEN);
		context.getGraphics().drawLine((int)base.getX(), (int)base.getY(), (int)(base.getX()+width), (int)base.getY());
		context.getGraphics().setColor(Color.BLACK);*/

		return (int)(3*m_metrics.getNoteWidth());
	}
}

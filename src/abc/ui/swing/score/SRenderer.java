package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/** This class defines a score rendition element.
 * Any rendition element built for a tune score rendition should extend this 
 * base class. */ 
public abstract class SRenderer {
	/** The metrics to be used to calculate this rendition element */ 
	protected ScoreMetrics m_metrics = null;
	/** The reference point (bottom, left) that should be used when rendering
	 * this element. */
	protected Point2D m_base = null;
	/** The width of this rendition element */
	protected double m_width = -1;
	
	public SRenderer(Point2D base, ScoreMetrics c) {
		m_metrics = c;
		m_base = base;
	}
	
	public double getWidth() {
		return m_width;
	}
	
	public double render(Graphics2D g) {
		Color previousColor = g.getColor();
		g.setColor(Color.RED);
		g.drawRect((int)(m_base.getX()), (int)(m_base.getY()-m_metrics.getStaffCharBounds().getHeight()), 
				(int)m_width, (int)(m_metrics.getStaffCharBounds().getHeight()));
		g.setColor(previousColor);
		return m_width;
	}
}

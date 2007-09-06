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
		this (c);
		m_base = (Point2D)base.clone();
	}
	
	protected SRenderer(ScoreMetrics c) {
		m_metrics = c;
	}
	
	public double getWidth() {
		return m_width;
	}
	
	public Point2D getBase() {
		return m_base;
	}
	
	public void setBase(Point2D base) {
		m_base = base;
		onBaseChanged();
	}
	
	protected abstract void onBaseChanged();
	
	public double render(Graphics2D g) {
		/*Color previousColor = g.getColor();
		g.setColor(Color.RED);
		g.drawRect((int)(getBase().getX()), (int)(getBase().getY()-m_metrics.getStaffCharBounds().getHeight()), 
				(int)getWidth(), (int)(m_metrics.getStaffCharBounds().getHeight()));
		g.setColor(previousColor);*/
		return m_width;
	}
}

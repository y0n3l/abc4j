package abc.ui.swing.score;

import java.awt.geom.Point2D;

public class SRenderer {
	
	protected ScoreMetrics m_metrics = null;
	
	protected Point2D m_base = null;
	
	public SRenderer(Point2D base, ScoreMetrics c) {
		m_metrics = c;
		m_base = base;
	}
}

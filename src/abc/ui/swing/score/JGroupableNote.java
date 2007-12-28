package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.ui.swing.JScoreElement;

public interface JGroupableNote extends JScoreElement {
	public int getStemX() ;

	public int getStemYBegin();
	
	public void setStemYEnd(int value);
	
	public int getStemYEnd();
	
	public Point2D getStemBegin();
	
}

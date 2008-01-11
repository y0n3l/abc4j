package abc.ui.swing;

import java.awt.geom.Point2D;


interface JGroupableNote extends JScoreElement {
	/*public int getStemX() ;

	public int getStemYBegin();*/
	
	public void setStemYEnd(int value);
	
	public int getStemYEnd();
	
	public Point2D getStemBegin();
	
}

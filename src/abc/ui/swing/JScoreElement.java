package abc.ui.swing;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.ui.swing.score.StaffLine;

public interface JScoreElement {
	public Point2D getBase();
	
	public double getWidth();
	
	public double render(Graphics2D g2);
	
	public StaffLine getStaffLine();
	
	
}

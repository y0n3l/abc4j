package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.rmi.ServerRuntimeException;
import java.util.Vector;

public class StaffLine extends SRenderer {

	protected Vector m_staffElements = null;
	
	public StaffLine(Point2D base, ScoreMetrics c) {
		super (c);
		m_staffElements = new Vector();
	}
	
	public Point2D getBase() {
		return ((SRenderer)m_staffElements.elementAt(0)).getBase();
	}
	
	protected void onBaseChanged() {
		
	}
	
	public void addElement(SRenderer r) {
		m_staffElements.addElement(r);
		computeWidth();
	}
	
	public SRenderer[] toArray() {
		SRenderer[] r = new SRenderer[m_staffElements.size()];
		m_staffElements.toArray(r);
		return r;
	}
	
	public double render(Graphics2D g) {
		//super.render(g);
		/*Color previousColor = g.getColor();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)(getBase().getX()), (int)(getBase().getY()-m_metrics.getStaffCharBounds().getHeight()), 
				(int)getWidth(), (int)(m_metrics.getStaffCharBounds().getHeight()));
		g.setColor(previousColor);*/
		
		SRenderer[] elmts = toArray();
		for (int j=0; j<elmts.length; j++) {
			elmts[j].render((Graphics2D)g);
		}
		int staffCharNb = (int)(getWidth()/m_metrics.getStaffCharWidth());
		//System.out.println("char staff nb : " + staffCharNb);
		char[] staffS = new char[staffCharNb+2];
		for (int j=0; j<staffS.length; j++)
			staffS[j] = ScoreMetrics.STAFF_SIX_LINES;
		g.drawChars(staffS, 0, staffS.length, 0, (int)getBase().getY());
		return m_width;
	}
	
	public void scaleToWidth(double newWidth) {
		for (int i=0; i<m_staffElements.size(); i++){
			SRenderer elmt = (SRenderer)m_staffElements.elementAt(i);
			if ( (!(elmt instanceof SClef)) && (!(elmt instanceof SKeySignature))
					&& (!(elmt instanceof STimeSignature))
					)  {
				double newXpos =(elmt.getBase().getX()*newWidth/getWidth());
				Point2D base = elmt.getBase();
				base.setLocation(newXpos, base.getY());
				elmt.setBase(base);
			}
		}
		computeWidth();
		//System.out.println("StaffLine, required Width : " + newWidth + " | result width=" + m_width);
	}
	
	private void computeWidth() {
		SRenderer lastElmt = (SRenderer)m_staffElements.lastElement();
		m_width = lastElmt.getBase().getX()+lastElmt.getWidth();
	}

}

package abc.ui.swing.score;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Vector;

import abc.notation.Note;
import abc.notation.ScoreElementInterface;
import abc.ui.swing.JScoreElement;

public class StaffLine extends JScoreElement {

	protected Vector m_staffElements = null;
	
	//protected Vector m_beginningSlurElements = null;
	
	public StaffLine(Point2D base, ScoreMetrics c) {
		super (c);
		m_staffElements = new Vector();
		//m_beginningSlurElements = new Vector();
	}
	
	public Point2D getBase() {
		return ((JScoreElement)m_staffElements.elementAt(0)).getBase();
	}
	
	public ScoreElementInterface getScoreElement() {
		return null;
	}
	
	protected void onBaseChanged() {
		
	}
	
	public void addElement(JScoreElement r) {
		m_staffElements.addElement(r);
		computeWidth();
	}
	
	/*public Vector getBeginningSlurElements() {
		return m_beginningSlurElements;
	}*/
	
	public JScoreElement[] toArray() {
		JScoreElement[] r = new JScoreElement[m_staffElements.size()];
		m_staffElements.toArray(r);
		return r;
	}
	
	public JScoreElement getScoreElementAt(Point point) {
		JScoreElement scoreEl = null;
		for (int i=0; i<m_staffElements.size(); i++) {
			scoreEl = ((JScoreElement)m_staffElements.elementAt(i)).getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}
	
	public double render(Graphics2D g) {
		//super.render(g);
		/*Color previousColor = g.getColor();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)(getBase().getX()), (int)(getBase().getY()-m_metrics.getStaffCharBounds().getHeight()), 
				(int)getWidth(), (int)(m_metrics.getStaffCharBounds().getHeight()));
		g.setColor(previousColor);*/
		JScoreElement[] elmts = toArray();
		for (int j=0; j<elmts.length; j++) {
			elmts[j].render((Graphics2D)g);
			/*if (elmts[j] instanceof SNote) {
				Note note = ((SNote)elmts[j]).getNote();
				if (note.isBeginingSlur())
					m_beginningSlurElements.addElement(note);
					
			}*/
		}
		int staffCharNb = (int)(getWidth()/m_metrics.getStaffCharBounds().getWidth());
		//System.out.println("char staff nb : " + staffCharNb);
		char[] staffS = new char[staffCharNb+2];
		for (int j=0; j<staffS.length; j++)
			staffS[j] = ScoreMetrics.STAFF_SIX_LINES;
		g.drawChars(staffS, 0, staffS.length, 0, (int)getBase().getY());
		return m_width;
	}
	
	public void scaleToWidth(double newWidth) {
		for (int i=0; i<m_staffElements.size(); i++){
			JScoreElement elmt = (JScoreElement)m_staffElements.elementAt(i);
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
		JScoreElement lastElmt = (JScoreElement)m_staffElements.lastElement();
		m_width = lastElmt.getBase().getX()+lastElmt.getWidth();
	}

}

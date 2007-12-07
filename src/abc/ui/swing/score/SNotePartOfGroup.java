package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.Note;

public class SNotePartOfGroup extends SNote {
	protected int stemX = -1;
	protected int stemYBegin = -1;  
	protected int stemYEnd = -1;
	
	public SNotePartOfGroup(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
		//correct what differs from SNote...
		//The displayed character is not the same.
		noteChars = ScoreMetrics.NOTE; 
		onBaseChanged();
	}
	
	protected void onBaseChanged() {
		super.onBaseChanged();
		//correct what differs from SNote...
		//The displayed character is not the same.
		//noteChars = ScoreMetrics.NOTE; 
		//The Y offset needs to be updated. 
		int noteY = (int)(m_base.getY()-getOffset(note)*m_metrics.getNoteHeigth());
		//apply the new Y offset to the note location
		displayPosition.setLocation(displayPosition.getX(), noteY);
		double noteX = displayPosition.getX();
		BasicStroke stemStroke = m_metrics.getNotesLinkStroke();
		stemX = (int)(noteX + m_metrics.getNoteWidth() - stemStroke.getLineWidth()/10);
		stemYBegin = (int)(noteY - m_metrics.getNoteHeigth()/6);
		notePosition = new Point2D.Double(displayPosition.getX(), displayPosition.getY()+m_metrics.getNoteHeigth()*0.5);
		onNotePositionChanged();
	}
	
	public int getStemX(){
		return stemX;
	}
	
	public int getStemYBegin(){
		return stemYBegin;
	}
	
	public void setStemYEnd(int value) {
		stemYEnd = value;
	}
	
	public int getStemYEnd() {
		return stemYEnd;
	}
	
	public Rectangle2D getBoundingBox() {
		Rectangle2D bb = new Rectangle2D.Double((int)(m_base.getX()), (int)(stemYEnd), 
				m_width, stemYBegin-stemYEnd+m_metrics.getNoteHeigth()/2);
		return bb;
	}
	
	public Point2D getEndOfStemPosition() {
		if(stemYEnd!=-1)
			return new Point2D.Double(stemX, stemYEnd);
		else
			throw new IllegalStateException();
	}
	
	public static double getOffset(Note note) {
		double positionOffset = 0;
		byte noteHeight = note.getStrictHeight();
		switch (noteHeight) {
			case Note.C : positionOffset = -1; break;
			case Note.D : positionOffset = -0.5;break;
			case Note.E : positionOffset = 0;break;
			case Note.F : positionOffset = 0.5;break;
			case Note.G : positionOffset = 1;break;
			case Note.A : positionOffset = 1.5;break;
			case Note.B : positionOffset = 2;break;
		}
		positionOffset = positionOffset + note.getOctaveTransposition()*3.5;
		//System.out.println("offset for " + note +"," + note.getOctaveTransposition() + " : " + positionOffset);
		return positionOffset;
	}
	
	public double render(Graphics2D context){
		super.render(context);
		context.drawChars(noteChars, 0, 1, (int)displayPosition.getX(), (int)displayPosition.getY());
		Stroke defaultS = context.getStroke();
		context.setStroke(m_metrics.getStemStroke());
		context.drawLine((int)stemX, stemYBegin,stemX, stemYEnd);
		context.setStroke(defaultS);
		/*Color previousColor = context.getColor();
		context.setColor(Color.RED);
		context.drawLine((int)getNotePosition().getX(), (int)getNotePosition().getY(), 
				(int)getNotePosition().getX(), (int)getNotePosition().getY());
		context.setColor(Color.GREEN);
		context.drawLine((int)m_base.getX(), (int)m_base.getY(), 
				(int)m_base.getX(), (int)m_base.getY());
		context.setColor(previousColor);*/
		
		return m_width;
	}
}

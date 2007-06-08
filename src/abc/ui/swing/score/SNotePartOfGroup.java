package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import abc.notation.Note;

public class SNotePartOfGroup extends SNote {
	protected int stemX = -1;
	protected int stemYBegin = -1;  
	protected int stemYEnd = -1;
	
	public SNotePartOfGroup(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
		//correct what differs from SNote...
		noteChars = ScoreMetrics.NOTE;
		int noteY = (int)(base.getY()-getOffset(note)*c.getNoteHeigth());
		notePosition.setLocation(notePosition.getX(), noteY);
		double noteX = notePosition.getX();
		BasicStroke stemStroke = c.getNotesLinkStroke();
		stemX = (int)(noteX + c.getNoteWidth() - stemStroke.getLineWidth()/10);
		stemYBegin = (int)(noteY - c.getNoteHeigth()/6);
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
	
	public int getStemYEnd(int value) {
		return stemYEnd;
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
	
	public int render(Graphics2D context){
		super.render(context);
		context.drawChars(noteChars, 0, 1, (int)notePosition.getX(), (int)notePosition.getY());
		Stroke defaultS = context.getStroke();
		context.setStroke(m_metrics.getStemStroke());
		context.drawLine((int)stemX, stemYBegin,stemX, stemYEnd);
		context.setStroke(defaultS);
		return width;
	}
}

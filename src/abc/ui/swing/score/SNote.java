package abc.ui.swing.score;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.AccidentalType;
import abc.notation.Note;
import abc.notation.ScoreElementInterface;
import abc.ui.swing.JScoreElement;

public class SNote extends JScoreElement {
	protected Note note = null;
	protected Point2D notePosition = null;
	protected Point2D accidentalsPosition = null;
	protected Point2D slurPosition = null;
	protected Point2D dotsPosition = null;
	protected char[] noteChars = null;
	protected char[] accidentalsChars = null;
	public static final char[] WHOLE_NOTE = {'\uF092'};
	public static final char[] HALF_NOTE = {'\uF068'};
	public static final char[] QUARTER_NOTE = {'\uF071'};
	public static final char[] EIGHTH_NOTE = {'\uF065'};
	public static final char[] SIXTEENTH_NOTE = {'\uF072'};
	public static final char[] THIRTY_SECOND_NOTE = {'\uF078'};
	
	public static final char[] DOUBLE_REST = {'\uF0E3'};
	public static final char[] WHOLE_REST = {'\uF0B7'};
	public static final char[] HALF_REST = {'\uF0EE'};
	public static final char[] QUARTER_REST = {'\uF0CE'};
	public static final char[] EIGHTH_REST = {'\uF0E4'};
	public static final char[] SIXTEENTH_REST = {'\uF071'};
	public static final char[] THIRTY_SECOND_REST = {'\uF071'};
	public static final char[] SIXTY_FOUR_REST = {'\uF0F4'};
	
	
	public SNote(Note noteValue, Point2D base, ScoreMetrics c) {
		super(base, c);
		note = noteValue;
		onBaseChanged();
	}
	
	public ScoreElementInterface getScoreElement() {
		return note;
	}
	
	protected void onBaseChanged() {
		ScoreMetrics c = m_metrics;
		Point2D base = m_base;
		int noteY = 0;
		if (note.isRest())
			noteY = (int)(base.getY()-2*c.getNoteHeigth());
		else
			noteY = (int)(base.getY()-getOffset(note)*c.getNoteHeigth());
		double accidentalsWidth = 0;
		if (note.getAccidental()!=AccidentalType.NONE) {
			accidentalsPosition = new Point2D.Double(base.getX(),noteY-c.getNoteHeigth()/2);
			switch (note.getAccidental()) {
				case AccidentalType.FLAT: accidentalsChars = ScoreMetrics.FLAT; 
					accidentalsWidth = c.getFlatBounds().getWidth(); 
					break;
				case AccidentalType.SHARP: accidentalsChars = ScoreMetrics.SHARP; 
					accidentalsWidth = c.getSharpBounds().getWidth(); 
					break;
				case AccidentalType.NATURAL: accidentalsChars = ScoreMetrics.NATURAL; 
					accidentalsWidth = c.getNaturalBounds().getWidth(); 
					break;
			}
		}
		short noteDuration = note.getStrictDuration();
		if (note.isRest()){
			//System.out.println("duration of the rest is " + noteDuration);
			switch (noteDuration) {
			//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
				case Note.THIRTY_SECOND : noteChars = THIRTY_SECOND_REST; break;
				case Note.SIXTEENTH : noteChars = SIXTEENTH_REST; break;
				case Note.EIGHTH : noteChars = EIGHTH_REST; break;
				case Note.QUARTER: noteChars = QUARTER_REST; break;
				case Note.HALF: noteChars = HALF_REST; break;
				case Note.WHOLE: noteChars = WHOLE_REST; break;
			}
		}
		else {
			switch (noteDuration) {
				//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
				case Note.THIRTY_SECOND : noteChars = THIRTY_SECOND_NOTE; break;
				case Note.SIXTEENTH : noteChars = SIXTEENTH_NOTE; break;
				case Note.EIGHTH : noteChars = EIGHTH_NOTE; break;
				case Note.QUARTER: noteChars = QUARTER_NOTE; break;
				case Note.HALF: noteChars = HALF_NOTE; break;
				case Note.WHOLE: noteChars = WHOLE_NOTE; break;
			}
		}
		//System.out.println("note chars " + noteChars[0]);
		//double noteY =(int)(base.getY()-getOffset(note)*c.getNoteHeigth());
		double noteX = base.getX()+accidentalsWidth*1.2;
		notePosition = new Point2D.Double(noteX, noteY);
		if (note.countDots()!=0)
			dotsPosition = new Point2D.Double(noteX + c.getNoteWidth()*1.2, noteY-c.getNoteHeigth()*0.05);
		//if (note.getSlurDefinition()!=null)
		slurPosition = new Point2D.Double(noteX + c.getNoteWidth()/2, noteY+c.getNoteWidth()/2);
		//c.getGraphics().drawChars(chars, 0, chars.length, noteX, noteY);
		/*if (note.getHeight()==Note.C || note.getHeight()==Note.a)
			c.getGraphics().drawChars(ScoreRenditionContext.STROKE, 0, 1, (int)(noteX-context.getNoteWidth()/4), strokeY);*/
		m_width = (int)(accidentalsWidth+c.getNoteWidth());
	}
	
	public static double getOffset(Note note) {
		double positionOffset = 0;
		byte noteHeight = note.getStrictHeight();
		switch (noteHeight) {
			case Note.C : positionOffset = -1.5; break;
			case Note.D : positionOffset = -1;break;
			case Note.E : positionOffset = -0.5;break;
			case Note.F : positionOffset = 0;break;
			case Note.G : positionOffset = 0.5;break;
			case Note.A : positionOffset = 1;break;
			case Note.B : positionOffset = 1.5;break;
		}
		positionOffset = positionOffset + note.getOctaveTransposition()*3.5;
		short noteDuration = note.getStrictDuration();
		switch (noteDuration) {
			case Note.THIRTY_SECOND : 
			case Note.SIXTEENTH : 
			case Note.WHOLE: positionOffset=positionOffset+0.5; break;
		}
		//System.out.println("offset : " + positionOffset );
		return positionOffset;
	}
	
	public Point2D getNotePosition(){
		return notePosition;
	}
	
	public Note getNote(){
		return note;
	}
	
	public Rectangle2D getBoundingBox() {
		double noteGlyphHeight =  m_metrics.getNoteHeigth()*4;
		Rectangle2D bb = new Rectangle2D.Double((int)(notePosition.getX()), (int)(notePosition.getY()-noteGlyphHeight), 
				m_width, noteGlyphHeight);
		return bb;
	}
		
	public double render(Graphics2D g){
		super.render(g);
		renderExtendedStaffLines(g, m_metrics, m_base);
		renderAccidentals(g);
		renderDots(g);
		g.drawChars(noteChars, 0, 1, (int)notePosition.getX(), (int)notePosition.getY());
		return m_width;
	}
	
	protected void renderAccidentals(Graphics2D gfx) {
		if (accidentalsPosition!=null)
			gfx.drawChars(accidentalsChars, 0, 1, (int)accidentalsPosition.getX(), (int)accidentalsPosition.getY()); 
	}
	
	/*protected void renderSlur(Graphics2D gfx) {
		if (slurPosition!=null && note.isEndingSlur()) {
			int width = note.getSlurDefinition().getEnd().
			gfx.drawLine(accidentalsChars, 0, 1, (int)accidentalsPosition.getX(), (int)accidentalsPosition.getY());
		}
	}*/
	
	protected void renderExtendedStaffLines(Graphics2D context, ScoreMetrics metrics, Point2D base){
		int extSize = (int)metrics.getNoteWidth()/3;
		if (note.getHeight()<=Note.C){
			double currentOffset = getOffset(new Note(Note.C, AccidentalType.NONE));
			int currentPosition = (int)(base.getY()-currentOffset*metrics.getNoteHeigth()/1.5);
			double offset = getOffset(note);
			Stroke dfs = context.getStroke();
			context.setStroke(metrics.getStemStroke());
			while (currentOffset>=offset) {
				context.drawLine(
						(int)(notePosition.getX()-extSize), currentPosition,
						(int)(notePosition.getX()+metrics.getNoteWidth()+extSize), currentPosition);
				currentOffset--;
				currentPosition = (int)(currentPosition + metrics.getNoteHeigth());
				//System.out.println("current offset : " + currentOffset + " " + currentPosition);
			}
			context.setStroke(dfs);
		}
		else
			if (note.getHeight()>=Note.a){
				double currentOffset = getOffset(new Note(Note.a, AccidentalType.NONE));
				int currentPosition = (int)(base.getY()-currentOffset*metrics.getNoteHeigth()-metrics.getNoteHeigth()/2);
				double offset = getOffset(note);
				Stroke dfs = context.getStroke();
				context.setStroke(metrics.getStemStroke());
				while (currentOffset<=offset) {
					context.drawLine(
							(int)(notePosition.getX()-extSize), currentPosition,
							(int)(notePosition.getX()+metrics.getNoteWidth()+extSize), currentPosition);
					currentOffset++;
					currentPosition = (int)(currentPosition - metrics.getNoteHeigth());
					//System.out.println("current offset : " + currentOffset + " " + currentPosition);
				}
				context.setStroke(dfs);
			}
	}
	
	protected void renderDots(Graphics2D context){
			if (dotsPosition!=null){
				context.drawChars(ScoreMetrics.DOT, 0, 1, 
					(int)dotsPosition.getX(), (int)dotsPosition.getY());
			}
		}
		
}




package abc.ui.swing.score;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.AccidentalType;
import abc.notation.Note;
import abc.notation.ScoreElementInterface;
import abc.ui.swing.JScoreElement;

public class SNote extends JScoreElement {
	private static final double SPACE_RATIO_FOR_ACCIDENTALS = 1.3;
	
	protected Note note = null;
	/** This position is independant from the note type (height or rhythm)
	 * and, as a way of consequence, can be used to render elements such as sharp,
	 * flats etc around the note.
	 * This is the base to compute the accidentalsPosition, the slurs positions etc. */
	protected Point2D notePosition = null;
	/** This position is used to render the note character in the score. 
	 * This position may (relatively) differ from one note to another and cannot be 
	 * used as a stable reference position to draw elements around the note. */
	protected Point2D displayPosition = null;
	/** Position to draw accidentals. <TT>null</TT> if no accidental for this note. */
	protected Point2D accidentalsPosition = null;
	protected Point2D slurDownAnchor, slurUpAnchor = null;
	protected Point2D dotsPosition = null;
	/** The chars from the font that represent the note to be displayed. */
	protected char[] noteChars = null;
	/** The chars from the font that represent the accidentals to be displayed. 
	 * <TT>null</TT> if the note has no accidental. */
	protected char[] accidentalsChars = null;
	/** Tells if the note should be displayed stem up or down. */
	protected boolean isStemUp = true;
	
	public static final char[] WHOLE_NOTE = {'\uF092'};
	public static final char[] HALF_NOTE = {'\uF068'};
	public static final char[] QUARTER_NOTE = {'\uF071'};
	public static final char[] EIGHTH_NOTE = {'\uF065'};
	public static final char[] SIXTEENTH_NOTE = {'\uF072'};
	public static final char[] THIRTY_SECOND_NOTE = {'\uF078'};
	
	public static final char[] WHOLE_NOTE_STEM_DOWN = {'\uF092'};
	public static final char[] HALF_NOTE_STEM_DOWN = {'\uF048'};
	public static final char[] QUARTER_NOTE_STEM_DOWN = {'\uF051'};
	public static final char[] EIGHTH_NOTE_STEM_DOWN = {'\uF045'};
	public static final char[] SIXTEENTH_NOTE_STEM_DOWN = {'\uF052'};
	public static final char[] THIRTY_SECOND_NOTE_STEM_DOWN = {'\uF058'};

	
	public static final char[] DOUBLE_REST = {'\uF0E3'};
	public static final char[] WHOLE_REST = {'\uF0B7'};
	public static final char[] HALF_REST = {'\uF0EE'};
	public static final char[] QUARTER_REST = {'\uF0CE'};
	public static final char[] EIGHTH_REST = {'\uF0E4'};
	public static final char[] SIXTEENTH_REST = {'\uF0C5'};
	public static final char[] THIRTY_SECOND_REST = {'\uF0A8'};
	public static final char[] SIXTY_FOUR_REST = {'\uF0F4'};
	
	public static final char[] UNKNWON = {'\uF0AD'};
	
	public SNote(Note noteValue, Point2D base, ScoreMetrics c) {
		super(base, c);
		note = noteValue;
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
				default : noteChars = UNKNWON;
			}
		}
		else {
			if (isStemUp) 
				switch (noteDuration) {
					//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
					case Note.THIRTY_SECOND : noteChars = THIRTY_SECOND_NOTE; break;
					case Note.SIXTEENTH : noteChars = SIXTEENTH_NOTE; break;
					case Note.EIGHTH : noteChars = EIGHTH_NOTE; break;
					case Note.QUARTER: noteChars = QUARTER_NOTE; break;
					case Note.HALF: noteChars = HALF_NOTE; break;
					case Note.WHOLE: noteChars = WHOLE_NOTE; break;
					default : noteChars = UNKNWON;
				}
			else
				switch (noteDuration) {
					//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
					case Note.THIRTY_SECOND : noteChars = THIRTY_SECOND_NOTE_STEM_DOWN; break;
					case Note.SIXTEENTH : noteChars = SIXTEENTH_NOTE_STEM_DOWN; break;
					case Note.EIGHTH : noteChars = EIGHTH_NOTE_STEM_DOWN; break;
					case Note.QUARTER: noteChars = QUARTER_NOTE_STEM_DOWN; break;
					case Note.HALF: noteChars = HALF_NOTE_STEM_DOWN; break;
					case Note.WHOLE: noteChars = WHOLE_NOTE_STEM_DOWN; break;
					default : noteChars = UNKNWON;
			}
		}
		onBaseChanged();
	}
	
	public ScoreElementInterface getScoreElement() {
		return note;
	}
	
	public void setStemUp(boolean isUp) {
		isStemUp = isUp;
	}
	
	protected void onBaseChanged() {
		ScoreMetrics c = m_metrics;
		Point2D base = m_base;
		int noteY = 0;
		if (note.isRest())
			noteY = (int)(base.getY()-2*c.getNoteHeigth());
		else
			noteY = (int)(base.getY()-getCorrectedOffset(note)*c.getNoteHeigth());
		double accidentalsWidth = 0;
		if (note.getAccidental()!=AccidentalType.NONE) {
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
				default : throw new IllegalArgumentException("Incorrect accidental for " + note + " : " + note.getAccidental());
			}
		}
		
		//System.out.println("note chars " + noteChars[0]);
		//double noteY =(int)(base.getY()-getOffset(note)*c.getNoteHeigth());
		double noteX = base.getX()+accidentalsWidth*SPACE_RATIO_FOR_ACCIDENTALS;
		displayPosition = new Point2D.Double(noteX, noteY);
		if (note.getStrictDuration()==Note.WHOLE || note.getStrictDuration()==Note.SIXTEENTH || 
				note.getStrictDuration()==Note.THIRTY_SECOND)
			notePosition = new Point2D.Double(displayPosition.getX(), displayPosition.getY()+m_metrics.getNoteHeigth()*0.5);
		else
			notePosition = (Point2D)displayPosition.clone();
		if (note.hasAccidental())
			accidentalsPosition = new Point2D.Double(base.getX(),notePosition.getY()-m_metrics.getNoteHeigth()/2);
		m_width = (int)(accidentalsWidth+c.getNoteWidth());
		onNotePositionChanged();
	}
	
	protected void onNotePositionChanged() {
		if (note.countDots()!=0)
			dotsPosition = new Point2D.Double(notePosition.getX() + m_metrics.getNoteWidth()*1.2, 
					notePosition.getY()-m_metrics.getNoteHeigth()*0.05);
		//if (note.getSlurDefinition()!=null)
		slurDownAnchor = new Point2D.Double(notePosition.getX() + m_metrics.getNoteWidth()/2, 
				notePosition.getY()+m_metrics.getNoteWidth()/4);
		slurUpAnchor = new Point2D.Double(displayPosition.getX() + m_metrics.getNoteWidth()/2, 
				notePosition.getY()-m_metrics.getNoteWidth()/4);
		//if (note.hasAccidental())
		//	accidentalsPosition = new Point2D.Double(notePosition.getX(),notePosition.getY()-m_metrics.getNoteHeigth()/2);
		//c.getGraphics().drawChars(chars, 0, chars.length, noteX, noteY);
		/*if (note.getHeight()==Note.C || note.getHeight()==Note.a)
			c.getGraphics().drawChars(ScoreRenditionContext.STROKE, 0, 1, (int)(noteX-context.getNoteWidth()/4), strokeY);*/
		
	}
	
	public static double getCorrectedOffset(Note note) {
		double positionOffset = getOffset(note);
		short noteDuration = note.getStrictDuration();
		switch (noteDuration) {
			case Note.THIRTY_SECOND : 
			case Note.SIXTEENTH : 
			case Note.WHOLE: positionOffset=positionOffset+0.5; break;
		}
		//System.out.println("offset : " + positionOffset );
		return positionOffset;
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
		return positionOffset;
	}
	
	public Point2D getNotePosition(){
		return notePosition;
	}
	
	protected Point2D getDisplayPosition(){
		return displayPosition;
	}
	
	public Note getNote(){
		return note;
	}
	
	public Point2D getSlurUpAnchor() {
		return slurUpAnchor;
	}
	
	public Point2D getSlurDownAnchor() {
		return slurDownAnchor;
	}
	
	
	public Rectangle2D getBoundingBox() {
		double noteGlyphHeight =  m_metrics.getNoteHeigth()*4;
		Rectangle2D bb = new Rectangle2D.Double((int)(displayPosition.getX()), (int)(displayPosition.getY()-noteGlyphHeight), 
				m_width, noteGlyphHeight);
		return bb;
	}
		
	public double render(Graphics2D g){
		super.render(g);
		renderExtendedStaffLines(g, m_metrics, m_base);
		renderAccidentals(g);
		renderDots(g);
		g.drawChars(noteChars, 0, 1, (int)displayPosition.getX(), (int)displayPosition.getY());
		/*Color previousColor = g.getColor();
		g.setColor(Color.RED);
		m_boundingBox = getBoundingBox();
		g.drawRect((int)(m_boundingBox.getX()), (int)(m_boundingBox.getY()), 
				(int)(m_boundingBox.getWidth()), (int)(m_boundingBox.getHeight()))
		g.drawLine((int)getNotePosition().getX(), (int)getNotePosition().getY(), 
				(int)getNotePosition().getX(), (int)getNotePosition().getY());
		g.setColor(Color.GREEN);
		g.drawLine((int)m_base.getX(), (int)m_base.getY(), 
				(int)m_base.getX(), (int)m_base.getY());
		g.setColor(previousColor);*/
		return m_width;
	}
	
	protected void renderAccidentals(Graphics2D gfx) {
		if (accidentalsPosition!=null)
			gfx.drawChars(accidentalsChars, 0, 1, (int)accidentalsPosition.getX(), (int)accidentalsPosition.getY()); 
	}
	
	protected void renderExtendedStaffLines(Graphics2D context, ScoreMetrics metrics, Point2D base){
		int extSize = (int)metrics.getNoteWidth()/3;
		if (note.getHeight()<=Note.C){
			double currentOffset = getCorrectedOffset(new Note(Note.C, AccidentalType.NONE));
			int currentPosition = (int)(base.getY()-currentOffset*metrics.getNoteHeigth()/1.5);
			double offset = getOffset(note);
			Stroke dfs = context.getStroke();
			context.setStroke(metrics.getStemStroke());
			while (currentOffset>=offset) {
				context.drawLine(
						(int)(displayPosition.getX()-extSize), currentPosition,
						(int)(displayPosition.getX()+metrics.getNoteWidth()+extSize), currentPosition);
				currentOffset--;
				currentPosition = (int)(currentPosition + metrics.getNoteHeigth());
				//System.out.println("current offset : " + currentOffset + " " + currentPosition);
			}
			context.setStroke(dfs);
		}
		else
			if (note.getHeight()>=Note.a){
				double currentOffset = getCorrectedOffset(new Note(Note.a, AccidentalType.NONE));
				int currentPosition = (int)(base.getY()-currentOffset*metrics.getNoteHeigth()-metrics.getNoteHeigth()/2);
				double offset = getOffset(note);
				Stroke dfs = context.getStroke();
				context.setStroke(metrics.getStemStroke());
				while (currentOffset<=offset) {
					context.drawLine(
							(int)(displayPosition.getX()-extSize), currentPosition,
							(int)(displayPosition.getX()+metrics.getNoteWidth()+extSize), currentPosition);
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




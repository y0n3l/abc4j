// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
package abc.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Vector;

import abc.notation.AccidentalType;
import abc.notation.Decoration;
import abc.notation.MusicElement;
import abc.notation.Note;

/** This class is in charge of rendering a single note and it's associated accidentals, dynamics,
 *  and lyric syllables.
 */
class JNote extends JScoreElementAbstract {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	protected int NOTATION_CONTEXT = ScoreMetrics.NOTE_GLYPH;

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
	protected Dimension slurStemOffset = null;
	protected Point2D dotsPosition = null;

	protected Point2D articulationPosition = null;

	// vector of JDecoration instances for this note.
	protected Vector m_jDecorations = null;

	/** The chars from the font that represent the note to be displayed. */
	protected char[] noteChars = null;
	/** The chars from the font that represent the accidentals to be displayed.
	 * <TT>null</TT> if the note has no accidental. */
	protected char[] accidentalsChars = null;
	/** Tells if the note should be displayed stem up or down. */

/* TJM */ // was true
	protected boolean isStemUp = false;
	/** Stem direction will be determined automatically
	 * based on note value. Notes B or higher will be stemed down,
	 * Notes A or lower will be stemed up. True by default.
	 */
	protected boolean autoStem = true;

	protected Point2D stemBeginPosition = null;

	protected Point2D stemUpBeginPosition = null;
	protected Point2D stemDownBeginPosition = null;

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


/* TJM */
	public JNote(ScoreMetrics c) {
		super(c);
	}

	public JNote(Note noteValue, Point2D base, ScoreMetrics c) {
		super(c);
		note = noteValue;
		valuateNoteChars();

		// add JDecorations
		if (note.hasDecorations()) {
			Decoration[] decorations = note.getDecorations();
			for (int i=0; i<decorations.length;i++) {
				addDecoration(new JDecoration(decorations[i], c));
			}
		}
		if (!note.isRest()) {
			if (noteValue.getHeight()<Note.B) {
				setStemUp(true);
			} else {
				setStemUp(false);
			}
		}
		setBase(base);

	}

	public MusicElement getMusicElement() {
		return note;
	}

	/* Add decoration if it hasn't been added previously */
	private void addDecoration (JDecoration decoration) {
		if (m_jDecorations == null) {
			m_jDecorations = new Vector();
		}

		if (!m_jDecorations.contains(decoration)) {
			m_jDecorations.add(decoration);
		}

	}

	/* TJM */
	/* convenience method - saves coding call and assignment */
	protected void valuateNoteChars() {
		noteChars = valuateNoteChars(note);
	}

	/* TJM */
	protected char[] valuateNoteChars(Note noteToValuate) {

		char [] valuatedNoteChars;

		short noteDuration = note.getStrictDuration();
		if (note.isRest()){
			//System.out.println("duration of the rest is " + noteDuration);
			switch (noteDuration) {
				//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
				case Note.THIRTY_SECOND : valuatedNoteChars = THIRTY_SECOND_REST; break;
				case Note.SIXTEENTH : valuatedNoteChars = SIXTEENTH_REST; break;
				case Note.EIGHTH : valuatedNoteChars = EIGHTH_REST; break;
				case Note.QUARTER: valuatedNoteChars = QUARTER_REST; break;
				case Note.HALF: valuatedNoteChars = HALF_REST; break;
				case Note.WHOLE: valuatedNoteChars = WHOLE_REST; break;
				default : valuatedNoteChars = UNKNWON;
			}
		}
		else {
			if (isStemUp)
				switch (noteDuration) {
					//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
					case Note.THIRTY_SECOND : valuatedNoteChars = THIRTY_SECOND_NOTE; break;
					case Note.SIXTEENTH : valuatedNoteChars = SIXTEENTH_NOTE; break;
					case Note.EIGHTH : valuatedNoteChars = EIGHTH_NOTE; break;
					case Note.QUARTER: valuatedNoteChars = QUARTER_NOTE; break;
					case Note.HALF: valuatedNoteChars = HALF_NOTE; break;
					case Note.WHOLE: valuatedNoteChars = WHOLE_NOTE; break;
					default : valuatedNoteChars = UNKNWON;
			}
			else {
				switch (noteDuration) {
					//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
					case Note.THIRTY_SECOND : valuatedNoteChars = THIRTY_SECOND_NOTE_STEM_DOWN; break;
					case Note.SIXTEENTH : valuatedNoteChars = SIXTEENTH_NOTE_STEM_DOWN; break;
					case Note.EIGHTH : valuatedNoteChars = EIGHTH_NOTE_STEM_DOWN; break;
					case Note.QUARTER: valuatedNoteChars = QUARTER_NOTE_STEM_DOWN; break;
					case Note.HALF: valuatedNoteChars = HALF_NOTE_STEM_DOWN; break;
					case Note.WHOLE: valuatedNoteChars = WHOLE_NOTE_STEM_DOWN; break;
					default : valuatedNoteChars = UNKNWON;
				}
			}
		}
		return(valuatedNoteChars);

	}

/* TJM : start */
	public boolean hasStemUp() {
		return (isStemUp);
	}

	public boolean getAutoStem() {
		return autoStem;
	}

	public void setAutoStem(boolean auto) {
		autoStem = auto;
	}

/* TJM : end */

	public void setStemUp(boolean isUp) {
		isStemUp = isUp;
		valuateNoteChars();
		if (isUp)
			stemBeginPosition = stemUpBeginPosition;
		else
			stemBeginPosition= stemDownBeginPosition;
	}

	public Point2D getStemBegin() {
		return stemBeginPosition;
		/*new Point2D.Double(notePosition.getX()+ m_metrics.getNoteWidth()*0.93,
				notePosition.getY()-m_metrics.getNoteHeigth()/2);*/
	}

	/**
	 * Moves the note to a new position in order to set the new stem
	 * begin position to the new location .
	 *
	 */
	public void setStemBegin(Point2D newStemBeginPos) {
		double xDelta = newStemBeginPos.getX() - getStemBegin().getX();
		double yDelta = newStemBeginPos.getY() - getStemBegin().getY();
		//System.out.println("translating " + this + " with " + xDelta + "/" + yDelta);
		Point2D newBase = new Point2D.Double(m_base.getX()+xDelta, m_base.getY()+yDelta);
		setBase(newBase);
	}


	// all sizing/positioning is derived in this method!!
	//
	protected void onBaseChanged() {

		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);

		Point2D base = m_base;
		int noteY = 0;
		if (note.isRest())
			noteY = (int)(base.getY()-2*glyphDimension.getHeight());
		else
			noteY = (int)(base.getY()+getOffset(note));
		double accidentalsWidth = 0;
		if (note.getAccidental()!=AccidentalType.NONE) {
			switch (note.getAccidental()) {
				case AccidentalType.FLAT: accidentalsChars = ScoreMetrics.FLAT;
					accidentalsWidth = m_metrics.getFlatBounds().getWidth();
					break;
				case AccidentalType.SHARP: accidentalsChars = ScoreMetrics.SHARP;
					accidentalsWidth = m_metrics.getSharpBounds().getWidth();
					break;
				case AccidentalType.NATURAL: accidentalsChars = ScoreMetrics.NATURAL;
					accidentalsWidth = m_metrics.getNaturalBounds().getWidth();
					break;
				default : throw new IllegalArgumentException("Incorrect accidental for " + note + " : " + note.getAccidental());
			}
		}

		//System.out.println("note chars " + noteChars[0]);
		//double noteY =(int)(base.getY()+getOffset(note)*glyphDimension.getHeight());
		double noteX = base.getX()+accidentalsWidth*SPACE_RATIO_FOR_ACCIDENTALS;
		notePosition = new Point2D.Double(noteX, noteY);
		// get font glyph correction
		if (note.getStrictDuration()==Note.WHOLE || note.getStrictDuration()==Note.SIXTEENTH ||
				note.getStrictDuration()==Note.THIRTY_SECOND)
			displayPosition = new Point2D.Double(notePosition.getX(), notePosition.getY()+getCorrectedGlyphOffest(note));
		else
			displayPosition = (Point2D)notePosition.clone();


		stemUpBeginPosition = new Point2D.Double(notePosition.getX()+ glyphDimension.getWidth()*0.93,
				notePosition.getY()-glyphDimension.getHeight()/2);
		stemDownBeginPosition = new Point2D.Double(notePosition.getX(),
				notePosition.getY()-glyphDimension.getHeight()/2);
		//reinit stem position
		setStemUp(isStemUp);
		if (note.hasAccidental())
			accidentalsPosition = new Point2D.Double(base.getX(),notePosition.getY()-glyphDimension.getHeight()/2);
		m_width = (int)(accidentalsWidth+glyphDimension.getWidth());
/* TJM */
//System.out.println("base:display:note=["+m_base.getX()+","+m_base.getY()+"]["+
//displayPosition.getX()+","+displayPosition.getY()+"]["+
//notePosition.getX()+","+notePosition.getY()+"]");

		onNotePositionChanged();
	}

	protected void onNotePositionChanged() {

		calcDotsPosition();
		calcDecorationPosition();
		// calculate slur/tie position last because slurs/ties must
		//  go over any decorations
		calcSlursAndTiesPosition();

	}


	protected void calcDotsPosition() {

		if (note.countDots()!=0) {

			Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);

			dotsPosition = new Point2D.Double(notePosition.getX() + glyphDimension.getWidth()*1.2,
					notePosition.getY()-glyphDimension.getHeight()*0.05);
		}
	}

	protected void calcDecorationPosition() {
		/* ********************************************* */
		// GENERAL RULES implemented here:
		// Articulations:
		//		upbow          -> always above staff
		//		downbow        -> always above staff
		//		staccato       -> placed with note
		//		staccatissimo  -> placed with note
		//		tenuto         -> placed with note
		//		[the rest]     ->
		//
		// Decorations/Ornaments
		//		[all]          -> above staff
		//
		//
		// Dynamics:
		//		single staff   -> place below
		//		double staff   -> between staffs
		//		vocal music    -> above staff
		//
		/* ********************************************* */

		if (m_jDecorations != null && m_jDecorations.size() > 0){

			double decorationHeight = m_metrics.getDecorationHeight();
			double decorationWidth = 0;

			double yOverStaff = 0;
			double yOnStaff = 0;
			double yUnderStaff = 0;
			// always use full note height ... even if this is a gracenote
			double noteHeight = m_metrics.getNoteHeight() + m_metrics.getStemLength();
			double stemedNoteHeight = noteHeight + m_metrics.getStemLength();

			// a little convience multiplier so that Y co-ord can be
			// incremented up/down without isStemUp testing
			int yCoordMultiplier = 1;

			if (isStemUp) {

				// ensure yOver is over stave AND higher than top of stem
				yOverStaff = m_base.getY() - m_metrics.getStaffLineHeight()*5 - decorationHeight;
				if (yOverStaff <= notePosition.getY() - stemedNoteHeight) {
					yOverStaff = notePosition.getY() - stemedNoteHeight - decorationHeight;
				}

				// yOnStaff starts under the note head
				yOnStaff = notePosition.getY() + decorationHeight*0.25;

				// yUnderStaff starts under the note head
				yUnderStaff = m_base.getY() + decorationHeight;
				if (yUnderStaff <= notePosition.getY()) {
					yUnderStaff = notePosition.getY() + decorationHeight;
				}

			} else {

				yCoordMultiplier = -1;

				// ensure yOver is over stave AND higher than note head
				yOverStaff = notePosition.getY() - m_metrics.getStaffLineHeight()*5 - decorationHeight;
				if (yOverStaff <= notePosition.getY() - noteHeight) {
					yOverStaff = notePosition.getY() - noteHeight - decorationHeight*0.75;
				}

				// yOnStaff starts over the note head
				yOnStaff = notePosition.getY() /*- noteHeight*/ - decorationHeight*0.25;

				// yUnderStaff starts under the stem
				yUnderStaff = m_base.getY() + decorationHeight;
				if (yUnderStaff <= notePosition.getY() + stemedNoteHeight) {
					yUnderStaff = notePosition.getY() /*+ stemedNoteHeight*/ + decorationHeight;
				}

			}

			JDecoration decoration = null;
			Point2D pos = null;
			Iterator iter = m_jDecorations.iterator();
			byte dType = -1;
			while (iter.hasNext()) {
				decoration = (JDecoration)iter.next();
				dType = ((Decoration)decoration.getMusicElement()).getType();
				switch (dType) {
					case Decoration.STACCATO:
							decorationWidth = m_metrics.getDecorationWidth(((Decoration)decoration.getMusicElement()).getChars());

							pos = new Point2D.Double(notePosition.getX() + decorationWidth/3, yOnStaff);
							decoration.setBase(pos);
							yOnStaff += (decorationHeight*yCoordMultiplier);
						break;

					case Decoration.UPBOW:
					case Decoration.DOWNBOW:
					case Decoration.ROLL:
					case Decoration.TRILL:
					case Decoration.FERMATA:
					case Decoration.ACCENT:
					case Decoration.LOWERMORDENT:
					case Decoration.UPPERMORDENT:
					case Decoration.SEGNO:
					case Decoration.CODA:
							pos = new Point2D.Double(notePosition.getX(), yOverStaff);
							decoration.setBase(pos);
							yOverStaff += (decorationHeight*yCoordMultiplier);
						break;

					default: break;
				}

			}
		}

	}

	protected void calcSlursAndTiesPosition() {
		// calculate slur/tie position last because slurs/ties must
		//  go over any decorations

		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);

		if (note.isTied() || note.getSlurDefinition()!=null) {
			slurDownAnchor = new Point2D.Double(notePosition.getX() + glyphDimension.getWidth()/2,
					notePosition.getY()+glyphDimension.getWidth()/4);
			slurUpAnchor = new Point2D.Double(displayPosition.getX() + glyphDimension.getWidth()/2,
					notePosition.getY()-glyphDimension.getHeight()*1.5);

			// dimensional offset for cases where slur/ties points to the stem
			slurStemOffset = new Dimension((int)glyphDimension.getWidth(),
											(int)((glyphDimension.getHeight()*2)/3));

		}

	}


	// correct for font glyph positioning
	public double getCorrectedGlyphOffest(Note note) {
		double positionOffset = 0;
		short noteDuration = note.getStrictDuration();
		switch (noteDuration) {
			case Note.THIRTY_SECOND :
			case Note.SIXTEENTH : positionOffset=m_metrics.getStaffLineHeight()*0.5; break;
			case Note.WHOLE: positionOffset=m_metrics.getStaffLineHeight()*0.55; break;
		}
		// correct for correct X,Y co-ordinate adjustment
		return positionOffset * -1;
	}


/* TJM : called from JKeySignature to calculate accidental positions */
	public double getOffset(Note note) {
		return (getOffset(m_metrics, note));
	}

	public static double getOffset(ScoreMetrics m, Note note) {
		double offsetMultiplier = 0;
		double positionOffset = 0;
		double lineHeight = 6.0; // default value for SONORA 45pt staff line
		byte noteHeight = note.getStrictHeight();
		switch (noteHeight) {
			case Note.C : offsetMultiplier = -1.5; break;
			case Note.D : offsetMultiplier = -1;break;
			case Note.E : offsetMultiplier = -0.5;break;
			case Note.F : offsetMultiplier = -0;break;
			case Note.G : offsetMultiplier = 0.5;break;
			case Note.A : offsetMultiplier = 1;break;
			case Note.B : offsetMultiplier = 1.5;break;
		}


		lineHeight = m.getStaffLineHeight();

		positionOffset = //(lineHeight/2) /*initial correction */ +
							(lineHeight*offsetMultiplier) /*staff position correction*/ +
							(note.getOctaveTransposition()*(3.5*lineHeight));

//System.out.println("note("+note+") \n\t base : "
//						+ positionOffset+"\ttransposition : "
//						+ note.getOctaveTransposition());

		// correct for correct X,Y co-ordinate adjustment
		return positionOffset * -1;
	}

	public Point2D getNotePosition(){
		return notePosition;
	}

	protected Point2D getDisplayPosition(){
		return displayPosition;
	}

	/*
public Note getNote(){
		return note;
	}*/

	public Point2D getSlurUpAnchor() {
		return slurUpAnchor;
	}

	public Point2D getSlurDownAnchor() {
		return slurDownAnchor;
	}

	public Rectangle2D getBoundingBox() {
		/* TJM */
		// FIXME: height should be based on actual glyph being used for note,
		//			eg. whole, 1/16, gracenote, etc.
		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);
		double noteGlyphHeight =  glyphDimension.getHeight()*4;
		Rectangle2D bb = new Rectangle2D.Double((int)(displayPosition.getX()), (int)(displayPosition.getY()-noteGlyphHeight),
				m_width, noteGlyphHeight);
		return bb;
	}

	public double render(Graphics2D g){
		super.render(g);
		renderExtendedStaffLines(g, m_metrics, m_base);
		renderAccidentals(g);
		/* TJM */
// rendering gracenotes here puts them in a jumble ontop of the melody note
// Solution: recalc. bounds of melody note to account for gracings .....
//renderGracings(g);
		renderDots(g);
		renderDecorations(g);
		renderNoteChars(g);

/* TJM */
// visual testing of base/offset/note positions
/*
		Color previousColor = g.getColor();
		g.setColor(Color.RED);

double nh = m_metrics.getStaffLineHeight();
double offset = getOffset(note);
g.drawRect((int)(m_base.getX()), (int)(m_base.getY()),1, 1);
g.setColor(Color.GREEN);
g.drawRect((int)(m_base.getX()), (int)(m_base.getY()+offset),1, 1);

		g.setColor(previousColor);
*/
// end TJM

		return m_width;
	}

	/* TJM */
	/* Render each indiviual gracenote associated with this note. */
	protected void renderGracings(Graphics2D gfx) {
//		gfx.drawChars(noteChars, 0, 1, (int)displayPosition.getX(), (int)displayPosition.getY());
/* TJM */

		if (note.hasGracingNotes()) {
			Note [] graceNotes = note.getGracingNotes();
			int len = graceNotes.length;
			int j =0;
			for (int i=0; i<len; i++) {
				gfx.drawChars(valuateNoteChars(graceNotes[i]), 0, 1, ((int)displayPosition.getX())+j, (int)displayPosition.getY());
/* TJM : TODO : use proper note widths obtained from ScoreMetrics */
				j+=5;
			}
		}

	}


	protected void renderNoteChars(Graphics2D gfx) {
		gfx.drawChars(noteChars, 0, 1, (int)displayPosition.getX(), (int)displayPosition.getY());
	}

	protected void renderAccidentals(Graphics2D gfx) {
		if (accidentalsPosition!=null)
			gfx.drawChars(accidentalsChars, 0, 1, (int)accidentalsPosition.getX(), (int)accidentalsPosition.getY());
	}

	protected void renderExtendedStaffLines(Graphics2D context, ScoreMetrics metrics, Point2D base){

		Dimension glyphDimension = metrics.getGlyphDimension(NOTATION_CONTEXT);

		double lineHeight = 0;
		double topLineY = 0;
		double bottomLineY = 0;
		double notePosY = 0;
		double noteCenterPosY = 0;

		boolean hasExtLines = false;
		boolean overStaff = true;
		int lineCount = 0;

		lineHeight = m_metrics.getStaffLineHeight();
		topLineY = m_base.getY() - (lineHeight*5);
		bottomLineY = m_base.getY();

		notePosY = notePosition.getY();
		noteCenterPosY = notePosY - (glyphDimension.getHeight()/2);

		//  over/under tests should probably use same reference point
		if (notePosY > bottomLineY) {
			hasExtLines = true;
			overStaff = false;
		} else if (noteCenterPosY < topLineY) {
			hasExtLines = true;
			overStaff = true;
		}


		if (hasExtLines) {
			byte noteHeight = note.getStrictHeight();
			switch (noteHeight) {
				case Note.G : lineCount = (overStaff) ? 0 : 2; break;
				case Note.A : lineCount = (overStaff) ? 1 : 2; break;
				case Note.B : lineCount = 1; break;
				case Note.C : lineCount = (overStaff) ? 2 : 1; break;
				case Note.D : lineCount = (overStaff) ? 2 : 0; break;
			}

			if (lineCount != 0) {

				int extSize = (int)glyphDimension.getWidth()/3;

				Stroke dfs = context.getStroke();
				context.setStroke(metrics.getStemStroke());

				while(lineCount > 0) {
					if (overStaff) {
						context.drawLine(
							// Y pos calculation has +lineHeight as a hack-correction!
							(int)(notePosition.getX()-extSize), (int)(topLineY+lineHeight-(lineHeight*(lineCount))),
							(int)(notePosition.getX()+glyphDimension.getWidth()+extSize), (int)(topLineY+lineHeight-(lineHeight*(lineCount))));
					} else {
						context.drawLine(
							(int)(notePosition.getX()-extSize), (int)(bottomLineY+(lineHeight*lineCount)),
							(int)(notePosition.getX()+glyphDimension.getWidth()+extSize), (int)(bottomLineY+(lineHeight*lineCount)));
					}
					lineCount--;
				}

				context.setStroke(dfs);
			}
		}
	}

	protected void renderDots(Graphics2D context){
		if (dotsPosition!=null){
			context.drawChars(ScoreMetrics.DOT, 0, 1,
				(int)dotsPosition.getX(), (int)dotsPosition.getY());
		}
	}

	protected void renderDecorations(Graphics2D context){

		if (m_jDecorations != null && m_jDecorations.size() > 0) {

			Iterator iter = m_jDecorations.iterator();
			JDecoration decoration = null;

			while (iter.hasNext()) {
				decoration = (JDecoration)iter.next();
				decoration.render(context);
			}
		}
	}

}


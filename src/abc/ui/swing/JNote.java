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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Vector;

import abc.notation.AccidentalType;
import abc.notation.Decoration;
import abc.notation.MusicElement;
import abc.notation.Note;

/**
 * This class is in charge of rendering a single note and it's associated
 * accidentals, dynamics, and lyric syllables.
 */
class JNote extends JNoteElementAbstract {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	protected int NOTATION_CONTEXT = ScoreMetrics.NOTE_GLYPH;

	private static final double SPACE_RATIO_FOR_ACCIDENTALS = 1.1;

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

	protected Point2D articulationPosition = null;

	protected Point2D dotsPosition = null;
	/** The chars from the font that represent the note to be displayed. */
	protected char[] noteChars = null;
	/** The chars from the font that represent the accidentals to be displayed.
	 * <TT>null</TT> if the note has no accidental. */
	protected char[] accidentalsChars = null;

	protected Point2D stemUpBeginPosition = null;
	protected Point2D stemDownBeginPosition = null;


	public JNote(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
		note = noteValue;
		valuateNoteChars();

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

	/** Sets the Unicode value of the note as a char array.
	 * @see #noteChars
 	 */
	protected void valuateNoteChars() {
		short noteDuration = note.getStrictDuration();
		if (note.isRest()){
			noteChars = m_metrics.getRestChar(noteDuration);
			//System.out.println("duration of the rest is " + noteDuration);
		}
		else {
			if (isStemUp())
				noteChars = m_metrics.getNoteStemUpChar(noteDuration);
			else
				noteChars = m_metrics.getNoteStemDownChar(noteDuration);
		}
	}

	/** Sets note stem direction.
	 * @param isUp true or false
	 */
	public void setStemUp(boolean isUp) {
		super.setStemUp(isUp);
		valuateNoteChars();
	}

	/**
	 * Moves the note to a new position in order to set the new stem
	 * begin position to the new location .
	 *
	 */
	public void setStemBeginPosition(Point2D newStemBeginPos) {
		double xDelta = newStemBeginPos.getX() - getStemBeginPosition().getX();
		double yDelta = newStemBeginPos.getY() - getStemBeginPosition().getY();
		//System.out.println("translating " + this + " with " + xDelta + "/" + yDelta);
		Point2D newBase = new Point2D.Double(getBase().getX()+xDelta, getBase().getY()+yDelta);
		setBase(newBase);
	}

	// all sizing/positioning is derived in this method!!
	//
	protected void onBaseChanged() {
		//System.out.println("JNote.onBaseChanged : "+note.toString());
		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);

		Point2D base = getBase();
		int noteY = 0;
		if (note.isRest())
			noteY = (int)(base.getY()-2*glyphDimension.getHeight());
		else
			noteY = (int)(base.getY()-getCorrectedOffset(note)*glyphDimension.getHeight());
		double graceNotesWidth = 0;
		if (m_jGracenotes != null)
			graceNotesWidth = m_jGracenotes.getWidth() + (getWidth()*SPACE_RATIO_FOR_GRACENOTES);
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
		//double noteY =(int)(base.getY()-getOffset(note)*c.getNoteHeigth());
		//double noteY =(int)(base.getY()-getOffset(note)*glyphDimension.getHeigth());
		double noteX = base.getX()+graceNotesWidth+accidentalsWidth*SPACE_RATIO_FOR_ACCIDENTALS;
		displayPosition = new Point2D.Double(noteX, noteY);
		if (note.getStrictDuration()==Note.WHOLE || note.getStrictDuration()==Note.SIXTEENTH ||
				note.getStrictDuration()==Note.THIRTY_SECOND || note.getStrictDuration()==Note.SIXTY_FOURTH)
			notePosition = new Point2D.Double(displayPosition.getX(), displayPosition.getY()+glyphDimension.getHeight()*0.5);
		else
			notePosition = (Point2D)displayPosition.clone();
		stemUpBeginPosition = new Point2D.Double(notePosition.getX()+ glyphDimension.getWidth()*0.93,
				notePosition.getY()-glyphDimension.getHeight()/2);
		stemDownBeginPosition = new Point2D.Double(notePosition.getX(),
				notePosition.getY()-glyphDimension.getHeight()/2);
		if (note.hasAccidental())
			accidentalsPosition = new Point2D.Double(base.getX(),notePosition.getY()-glyphDimension.getHeight()/2);
		if (note.getHeight()>=Note.c && note.getStrictDuration() < Note.EIGHTH) {
			stemUpBeginPosition.setLocation(stemUpBeginPosition.getX(), stemUpBeginPosition.getY()-glyphDimension.getHeight()/2);
			stemDownBeginPosition.setLocation(stemDownBeginPosition.getX(), stemDownBeginPosition.getY()-glyphDimension.getHeight()/2);
			if (accidentalsPosition != null)
				accidentalsPosition.setLocation(accidentalsPosition.getX(), accidentalsPosition.getY()-glyphDimension.getHeight()/2);
		}
		//reinit stem position
		setStemUp(isStemUp());
		m_width = (int)(graceNotesWidth+accidentalsWidth+glyphDimension.getWidth());

		onNotePositionChanged();
	}

	protected void onNotePositionChanged() {

		if (m_jGracenotes != null) m_jGracenotes.setBase(getBase());
		calcDotsPosition();
		calcDecorationPosition();
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
		// TODO move this in JDecoration (or subclass) ?
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
		// Dynamics:
		//		single staff   -> place below
		//		double staff   -> between staffs
		//		vocal music    -> above staff
		//
		/* ********************************************* */
		//TODO getDecorations.size==0, return (if null, =new Vector)
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

			if (isStemUp()) {

				// ensure yOver is over stave AND higher than top of stem
				yOverStaff = getBase().getY() - m_metrics.getStaffLineHeight()*5 - decorationHeight;
				if (yOverStaff <= notePosition.getY() - stemedNoteHeight) {
					yOverStaff = notePosition.getY() - stemedNoteHeight - decorationHeight;
				}

				// yOnStaff starts under the note head
				yOnStaff = notePosition.getY() + decorationHeight*0.25;

				// yUnderStaff starts under the note head
				yUnderStaff = getBase().getY() + decorationHeight;
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
				yUnderStaff = getBase().getY() + decorationHeight;
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
		//if (note.getSlurDefinition()!=null)
		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);
		slurUnderAnchor = new Point2D.Double(notePosition.getX() + glyphDimension.getWidth()/2,
				notePosition.getY()+m_metrics.getSlurAnchorYOffset());
		slurAboveAnchor = new Point2D.Double(displayPosition.getX() + glyphDimension.getWidth()/2,
				notePosition.getY()-glyphDimension.getHeight()-m_metrics.getSlurAnchorYOffset());
		if (note.getHeight()>=Note.c && note.getStrictDuration() <= Note.SIXTEENTH) {
			slurAboveAnchor.setLocation(slurAboveAnchor.getX(), slurAboveAnchor.getY()-glyphDimension.getHeight()/2);
			slurUnderAnchor.setLocation(slurUnderAnchor.getX(), slurUnderAnchor.getY()+glyphDimension.getHeight()/2);
		}
		slurUnderAnchorOutOfStem = isStemUp()
			?slurUnderAnchor
			:new Point2D.Double(getBoundingBox().getMinX(),
								getBoundingBox().getMaxY()+m_metrics.getSlurAnchorYOffset());
		slurAboveAnchorOutOfStem = isStemUp()
			?new Point2D.Double(getBoundingBox().getMaxX(),
								getBoundingBox().getMinY()-m_metrics.getSlurAnchorYOffset())
			:slurAboveAnchor;
	}

	/* FIXME : called from JKeySignature to calculate accidental positions */
	/*public double getOffset(Note note) {
		return (getOffset(m_metrics, note));
	}*/

	//TODO move getCorrectedOffset to ScoreMetrics
	public static double getCorrectedOffset(Note note) {
		double positionOffset = getOffset(note);
		if ((note.getOctaveTransposition() <= 0)
				|| (note.getStrictDuration() == Note.WHOLE)) {
			short noteDuration = note.getStrictDuration();
			switch (noteDuration) {
				case Note.SIXTY_FOURTH:
				case Note.THIRTY_SECOND :
				case Note.SIXTEENTH :
				case Note.WHOLE: positionOffset=positionOffset+0.5; break;
			}
			//System.out.println("offset : " + positionOffset );
		}
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
		positionOffset += note.getOctaveTransposition()*3.5;
		return positionOffset;
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

	public Rectangle2D getBoundingBox() {
		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);
		double noteGlyphHeight = glyphDimension.getHeight()*4;
		if (note.getStrictDuration() == Note.THIRTY_SECOND)
			noteGlyphHeight = glyphDimension.getHeight()*5;
		else if (note.getStrictDuration() == Note.SIXTY_FOURTH)
			noteGlyphHeight = glyphDimension.getHeight()*6;
		else if (note.getStrictDuration() == Note.WHOLE)
			noteGlyphHeight = glyphDimension.getHeight();
		double correctOffset = 0;
		if (((note.getOctaveTransposition() <= 0)
				&& (note.getStrictDuration() < Note.EIGHTH))
				|| (note.getStrictDuration() == Note.WHOLE)) {
			correctOffset = -glyphDimension.getHeight()/2;
		}
		if (isStemUp()) {
			return new Rectangle2D.Double(
					(int)(displayPosition.getX()),
					(int)(displayPosition.getY()-correctOffset-noteGlyphHeight),
					m_width,
					noteGlyphHeight);
		}
		else {
			return new Rectangle2D.Double(
					(int)(displayPosition.getX()),
					(int)(displayPosition.getY()-correctOffset-glyphDimension.getHeight()),
					m_width,
					noteGlyphHeight);
		}
	}

	public double render(Graphics2D g){
		super.render(g);
		renderExtendedStaffLines(g, m_metrics, getBase());
		renderAccidentals(g);
		renderGraceNotes(g);
		renderDots(g);
		renderDecorations(g);
		renderNoteChars(g);
		renderChordName(g);

		return m_width;
	}

	protected void renderNoteChars(Graphics2D gfx) {
		gfx.drawChars(noteChars, 0, 1, (int)displayPosition.getX(), (int)displayPosition.getY());
	}

	protected void renderChordName(Graphics2D gfx) {
		if (note.getChordName()!=null) {
			Font oldFont = gfx.getFont();
			try {
				Font chordFont = m_metrics.getTextFont(ScoreMetrics.FONT_CHORDS);
				gfx.setFont(chordFont);
				Rectangle2D bounds = chordFont.getStringBounds(note.getChordName(), gfx.getFontRenderContext());
				double y = getStaffLine().getBase().getY()/* not yet defined*/
					//- (displayPosition.getY()%m_metrics.getStaffLinesSpacing())
					- m_metrics.getStaffLinesSpacing()
					+ bounds.getHeight();
				gfx.drawString(note.getChordName(), (int)displayPosition.getX(), (int)y);
			} finally {
				gfx.setFont(oldFont);
			}
		}
	}

	protected void renderAccidentals(Graphics2D gfx) {
		if (accidentalsPosition!=null)
			gfx.drawChars(accidentalsChars, 0, 1, (int)accidentalsPosition.getX(), (int)accidentalsPosition.getY());
	}

	protected void renderExtendedStaffLines(Graphics2D context, ScoreMetrics metrics, Point2D base){
		//FIXME: "Gracing" branch changes not integrated here
		Dimension glyphDimension = metrics.getGlyphDimension(NOTATION_CONTEXT);
		int extSize = (int)glyphDimension.getWidth()/3;
		if (note.getHeight()<=Note.C){
			double currentOffset = getCorrectedOffset(new Note(Note.C, AccidentalType.NONE));
			int currentPosition = (int)(base.getY()-currentOffset*glyphDimension.getHeight()/1.5);
			double offset = getOffset(note);
			Stroke dfs = context.getStroke();
			context.setStroke(metrics.getStemStroke());
			while (currentOffset>=offset) {
				context.drawLine(
						(int)(displayPosition.getX()-extSize), currentPosition,
						(int)(displayPosition.getX()+glyphDimension.getWidth()+extSize), currentPosition);
				currentOffset--;
				currentPosition = (int)(currentPosition + glyphDimension.getHeight());
				//System.out.println("current offset : " + currentOffset + " " + currentPosition);
			}
			context.setStroke(dfs);
		}
		else if (note.getHeight()>=Note.a){
			double currentOffset = getCorrectedOffset(new Note(Note.a, AccidentalType.NONE));
			int currentPosition = (int)(base.getY()-currentOffset*glyphDimension.getHeight()-glyphDimension.getHeight()/2);
			double offset = getOffset(note);
			Stroke dfs = context.getStroke();
			context.setStroke(metrics.getStemStroke());
			while (currentOffset<=offset) {
				context.drawLine(
						(int)(displayPosition.getX()-extSize), currentPosition,
						(int)(displayPosition.getX()+glyphDimension.getWidth()+extSize), currentPosition);
				currentOffset++;
				currentPosition = (int)(currentPosition - glyphDimension.getHeight());
				//System.out.println("current offset : " + currentOffset + " " + currentPosition);
			}
			context.setStroke(dfs);
		}
	}

	protected void renderDots(Graphics2D context){
		//FIXME draw 2 dots if note is double dotted
		if (dotsPosition!=null){
			context.drawChars(ScoreMetrics.DOT, 0, 1,
				(int)dotsPosition.getX(), (int)dotsPosition.getY());
		}
	}


	/**
	 * @return Returns the stemBeginPosition.
	 */
	public Point2D getStemBeginPosition() {
		if (isStemUp())
			return stemUpBeginPosition;
		else
			return stemDownBeginPosition;
		//return stemBeginPosition;
		/*new Point2D.Double(notePosition.getX()+ m_metrics.getNoteWidth()*0.93,
		notePosition.getY()-m_metrics.getNoteHeigth()/2);*/
	}

	/**
	 * @return Returns the stemDownBeginPosition.
	 */
	protected Point2D getStemDownBeginPosition() {
		return stemDownBeginPosition;
	}

	/**
	 * @param stemDownBeginPosition The stemDownBeginPosition to set.
	 */
	protected void setStemDownBeginPosition(Point2D stemDownBeginPosition) {
		this.stemDownBeginPosition = stemDownBeginPosition;
	}

	/**
	 * @return Returns the stemUpBeginPosition.
	 */
	protected Point2D getStemUpBeginPosition() {
		return stemUpBeginPosition;
	}

	/**
	 * @param stemUpBeginPosition The stemUpBeginPosition to set.
	 */
	protected void setStemUpBeginPosition(Point2D stemUpBeginPosition) {
		this.stemUpBeginPosition = stemUpBeginPosition;
	}

}

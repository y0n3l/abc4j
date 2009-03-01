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

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

/**
 * This class encapsulates all requiered dimensions needed to draw
 * correctly a score (notes spacing, space between accidental and notes etc...).
 * All those values are calculated from the font size. */
class ScoreMetrics {

	public static final int NOTE_GLYPH = 100;
	public static final int GRACENOTE_GLYPH = 200;


	public static final char STAFF_SIX_LINES = '\uF03D';
	public static final double STAFF_SIX_LINES_LINE_HEIGHT_RATIO = 4.1;
	public static final char[] BAR_LINE = {'\uF05C'};

	public static final char[] DOT = {'\uF06B'};

	public static final char[] STROKE = {'\uF05F'};
	/** The pure note character without any stem for notes such as 1/4, 1/8 and quicker... */
	public static final char[] NOTE = {'\uF0CF'};
	/** The pure note character without any stem for notes such as whole & half */
	public static final char[] NOTE_LONGER = {'\uF092'};

	public static final char STEM_COMBINE_UP_SINGLE = '\uF021';
	public static final char STEM_COMBINE_UP_DOUBLE = '\uF040';

	public static final char[] SHARP = {'\uF023'};
	public static final char[] FLAT = {'\uF062'};
	public static final char[] NATURAL = {'\uF06E'};
	public static final float DEFAULT_NOTATION_SIZE = 48;
	public static final float DEFAULT_TEXT_SIZE = DEFAULT_NOTATION_SIZE / 4;


	public static final String NOTATION_FONT = "SONORA.TTF";
	public static final String TEXT_FONT = "Dialog";

	double noteHeight = -1;
	double noteWidth = -1;
	double staffLineHeight = -1;
	//double staffCharWidth = -1;
	Rectangle2D staffCharBounds = null;
	Rectangle2D sharpBounds = null;
	Rectangle2D naturalBounds = null;
	Rectangle2D flatBounds = null;
	Rectangle2D quarterNoteBounds = null;
	BasicStroke notesLinkStroke = null;
	BasicStroke stemStroke = null;
	int noteStrokeLength = -1;
	int stemLength = -1;
	int notesSpacing = -1;
	double biggestAccidentalWidth = -1;

	// gracings support
	double graceNoteHeight = 0;
	double graceNoteWidth = 0;
	BasicStroke graceNotesLinkStroke = null;
	BasicStroke graceNoteStemStroke = null;
	int graceNoteStrokeLength = 0;
	int graceNoteStemLength = 0;
	int graceNotesSpacing = 0;



/* TJM */

	Font baseNotationFont = null;
	Font noteFont = null;
	Font gracingsFont = null;
	Font baseTextFont = null;
	Font titleFont = null;
	Font subtitleFont = null;
	Font annotationFont = null;
	Font lyricsFont = null;

	FontMetrics noteFontMetrics  = null;
	FontMetrics gracingsFontMetrics  = null;
	FontMetrics titleFontMetrics  = null;
	FontMetrics subtitleFontMetrics  = null;
	FontMetrics lyricsFontMetrics  = null;
	FontMetrics annotationFontMetrics  = null;

	int titleHeight = -1;
	int subtitleHeight = -1;
	int annotationHeight = -1;
	int decorationHeight = -1;
	int lyricsHeight = -1;


/* TJM */
	public ScoreMetrics(Graphics2D g2) {
		this(g2, DEFAULT_NOTATION_SIZE, TEXT_FONT, DEFAULT_TEXT_SIZE);
	}

/* TJM */
	public ScoreMetrics(Graphics2D g2, float notationSize) {
		this(g2, notationSize, TEXT_FONT, notationSize/4);
	}

/* TJM */
	public ScoreMetrics(Graphics2D g2, float notationSize, String textFontName, float textSize) {

		try {
			//getClass().getResourceAsStream("SONORA.TTF");
			//File file =new File("D:/Perso/musicfonts/MIDIDESI/TRUETYPE/SONORA.TTF");
			//FileInputStream fontStream = new FileInputStream(file);
		    baseNotationFont = Font.createFont(Font.TRUETYPE_FONT, ScoreMetrics.this.getClass().getResourceAsStream(NOTATION_FONT));
			baseTextFont = new Font(textFontName, Font.PLAIN, (int)textSize);

			initNoteFont(g2, notationSize);
			initGracingsFont(g2,60.00f);
			initTextFonts(g2);

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/* Sets reference font size (pts) that all other sizes are derived from.
		Gracings = 60%,
	   150% = 67.5pt, etc.
	*/
	public void setNotationFontSize(Graphics2D g2, float notationSize) {
		initNoteFont(g2, notationSize);
		initGracingsFont(g2,60.00f);
	}

	/* Sets reference font size (pts) that all other sizes are derived from.
		Gracings = 60%,
	   150% = 67.5pt, etc.
	*/
	public void setTextFontSize(Graphics2D g2, float notationSize) {
		initTextFonts(g2);
	}

	private void initNoteFont (Graphics2D g2, float size) {
		try {

		  noteFont = baseNotationFont.deriveFont(size);
		  noteFontMetrics = g2.getFontMetrics(noteFont);

		  FontRenderContext frc = g2.getFontRenderContext();

		  staffCharBounds = new TextLayout(new Character(STAFF_SIX_LINES).toString(), noteFont, frc).getBounds();

		  TextLayout noteLayout = null;
		  noteLayout = new TextLayout(new Character(NOTE[0]).toString(), noteFont, frc);
		  noteHeight = noteLayout.getBounds().getHeight();
//		  noteHeight = staffCharBounds.getHeight()/STAFF_SIX_LINES_LINE_HEIGHT_RATIO;
		  noteWidth =  noteLayout.getBounds().getWidth();
		  // FIXME: get proper decoration height ... or adjust this to be more generalized ...
		  decorationHeight = (int)(noteLayout.getBounds().getHeight()*2.5);

		  sharpBounds = new TextLayout(new Character(SHARP[0]).toString(), noteFont, frc).getBounds();
		  flatBounds = new TextLayout(new Character(FLAT[0]).toString(), noteFont, frc).getBounds();
		  naturalBounds = new TextLayout(new Character(NATURAL[0]).toString(), noteFont, frc).getBounds();
		  quarterNoteBounds =  new TextLayout(new Character(JNote.QUARTER_NOTE[0]).toString(), noteFont, frc).getBounds();
//		  noteWidth =  new TextLayout(new Character(NOTE[0]).toString(), noteFont, frc).getBounds().getWidth();
		  notesLinkStroke = new BasicStroke((float)(noteWidth/3), 0, 0);
		  stemStroke = new BasicStroke((float)(noteWidth/12));
		  stemLength = (int)(noteHeight*3);
		  noteStrokeLength = 2;
		  notesSpacing = (int)(1.5*noteWidth);
		  biggestAccidentalWidth = (getFlatBounds().getWidth()>getNaturalBounds().getWidth())
			?getFlatBounds().getWidth():getNaturalBounds().getWidth();
			biggestAccidentalWidth = (getSharpBounds().getWidth()>biggestAccidentalWidth)
			?getSharpBounds().getWidth():biggestAccidentalWidth;

/* TJM */
		  staffLineHeight = staffCharBounds.getHeight()/STAFF_SIX_LINES_LINE_HEIGHT_RATIO;

		} catch (Exception e){
			e.printStackTrace();
		}

	}


	private void initGracingsFont (Graphics2D g2, float percentOfNotationSize) {
		try {

			int notationSize = noteFont.getSize();
			float gracingsSize = notationSize*(percentOfNotationSize/100);

			gracingsFont = baseNotationFont.deriveFont(gracingsSize);
			gracingsFontMetrics = g2.getFontMetrics(gracingsFont);

			FontRenderContext frc = g2.getFontRenderContext();


			TextLayout layout = null;
			layout = new TextLayout(new Character(NOTE[0]).toString(), gracingsFont, frc);
			graceNoteHeight = layout.getBounds().getHeight();
			graceNoteWidth = layout.getBounds().getWidth();

			graceNotesLinkStroke = new BasicStroke((float)(graceNoteWidth/3), 0, 0);
			graceNoteStemStroke = new BasicStroke((float)(graceNoteWidth/12));
			graceNoteStemLength = (int)(graceNoteHeight*3);
			graceNoteStrokeLength = 2;
			graceNotesSpacing = (int)(1.5*graceNoteWidth);

		} catch (Exception e){
			e.printStackTrace();
		}

	}

	private void initTextFonts (Graphics2D g2) {
		try {

		  int baseTextSize = baseTextFont.getSize();
		  titleFont = baseTextFont.deriveFont(Font.BOLD, baseTextSize*3);
		  titleFontMetrics = g2.getFontMetrics(titleFont);
		  titleHeight = titleFontMetrics.getHeight();

		  subtitleFont = baseTextFont.deriveFont(Font.BOLD, baseTextSize*2);
		  subtitleFontMetrics = g2.getFontMetrics(subtitleFont);
		  subtitleHeight = subtitleFontMetrics.getHeight();

		  annotationFont = baseTextFont.deriveFont((int) (baseTextSize*0.8) );
		  annotationFontMetrics = g2.getFontMetrics(annotationFont);
		  annotationHeight = annotationFontMetrics.getHeight();

		  lyricsFont = baseTextFont.deriveFont(baseTextSize);
		  lyricsFontMetrics = g2.getFontMetrics(lyricsFont);
		  lyricsHeight = lyricsFontMetrics.getHeight();

		} catch (Exception e){
			e.printStackTrace();
		}
	}


	public Dimension getGlyphDimension(int notationContext) {
		Dimension d = null;

		switch (notationContext) {
			case NOTE_GLYPH: 		d = new Dimension((int)noteHeight, (int)noteWidth);
									break;
			case GRACENOTE_GLYPH : 	d = new Dimension((int)graceNoteHeight, (int)graceNoteWidth);
									break;
			default:
					break;
		}
		return (d);
	}

/* TJM */
	public double getStaffLineHeight() {
		return staffLineHeight;
	}

	public double getNoteHeigth() {
		return (getNoteHeight());
	}
/* TJM */
	public double getNoteHeight() {
		return noteHeight;
	}
	public double getNoteStrokeLength() {
		return noteStrokeLength;
	}

	public double getNoteWidth() {
		return noteWidth;
	}

	/** Returns the spacing between notes.
	 * @return The spacing between notes, expressed in pixels. */
	public double getNotesSpacing() {
		return notesSpacing;
	}

	public BasicStroke getNotesLinkStroke(){
		return notesLinkStroke;
	}

	/** Returns the bounding box of a staff line character.
	 * @return Returns the bounding box of a staff line character. */
	public Rectangle2D getStaffCharBounds(){
		return staffCharBounds;
	}

	public BasicStroke getStemStroke(){
		return stemStroke;
	}

	public int getStemLength(){
		return stemLength;
	}

	public int getStemLength(int notationContext){
		int len = 0;

		switch (notationContext) {
			case NOTE_GLYPH: 		len = getStemLength();
									break;
			case GRACENOTE_GLYPH : 	len = getGraceNoteStemLength();
									break;
			default:
					break;
		}
		return (len);
}

	/** Returns the bounding box of a sharp character.
	 * @return Returns the bounding box of a sharp character. */
	public Rectangle2D getSharpBounds(){
		return sharpBounds;
	}

	/** Returns the bounding box of a natural character.
	 * @return Returns the bounding box of a natural character. */
	public Rectangle2D getNaturalBounds(){
		return naturalBounds;
	}

	/** Returns the bounding box of a flat character.
	 * @return Returns the bounding box of a flat character. */
	public Rectangle2D getFlatBounds(){
		return flatBounds;
	}

	public double getBiggestAccidentalWidth() {
		return biggestAccidentalWidth;
	}

	public Rectangle2D getQuarterNoteBounds(){
		return quarterNoteBounds;
	}


/* TJM */

	/** Returns height of character rendered in the Decorations font.
	 * @return the width of a string in this font used for this score metrics.
	 */
	public int getDecorationHeight(){
		return (decorationHeight);
	}

	/** Returns width of character rendered in the Decorations font.
	 * @return the width of a string in this font used for this score metrics.
	 */
	public int getDecorationWidth(char[] c){
		return (noteFontMetrics.charsWidth(c,0,c.length));
	}



	/* *** graceNotes support *** */
	public double getGraceNoteHeight() {
		return graceNoteHeight;
	}

	public double getGraceNoteWidth() {
		return graceNoteWidth;
	}

	/** Returns the spacing between graceNotes.
	 * @return The spacing between graceNotes, expressed in pixels. */
	public double getGraceNotesSpacing() {
		return graceNotesSpacing;
	}

	public double getGraceNoteStrokeLength() {
		return graceNoteStrokeLength;
	}

	public BasicStroke getGraceNotesLinkStroke(){
		return graceNotesLinkStroke;
	}


	public BasicStroke getGraceNoteStemStroke(){
		return graceNoteStemStroke;
	}

	public int getGraceNoteStemLength(){
		return graceNoteStemLength;
	}


// get heights/widths for stings in various fonts
	/** Returns height of text line rendered in the Title font.
	 * @return the height of a line of text in this font used for this score metrics.
	 */
	public int getTitleHeight(){
		return titleHeight;
	}

	/** Returns width of string rendered in the Title font.
	 * @return the width of a string in this font used for this score metrics.
	 */
	public int getTitleWidth(String text){
		return (titleFontMetrics.stringWidth(text));
	}

	/** Returns height of text line rendered in the Subtitle font.
	 * @return the height of a line of text in this font used for this score metrics.
	 */
	public int getSubtitleHeight(){
		return subtitleHeight;
	}

	/** Returns width of string rendered in the Subtitle font.
	 * @return the width of a string in this font used for this score metrics.
	 */
	public int getSubtitleWidth(String text){
		return (subtitleFontMetrics.stringWidth(text));
	}

	/** Returns height of text line rendered in the Annotation font.
	 * @return the height of a line of text in this font used for this score metrics.
	 */
	public int getAnnotationHeight(){
		return annotationHeight;
	}

	/** Returns width of string rendered in the Annotation font.
	 * @return the width of a string in this font used for this score metrics.
	 */
	public int getAnnotationWidth(String text){
		return (annotationFontMetrics.stringWidth(text));
	}

	/** Returns height of text line rendered in the Lyrics font.
	 * @return the height of a line of text in this font used for this score metrics.
	 */
	public int getLyricsHeight(){
		return lyricsHeight;
	}

	/** Returns width of string rendered in the Lyrics font.
	 * @return the width of a string in this font used for this score metrics.
	 */
	public int getLyricsWidth(String text){
		return (lyricsFontMetrics.stringWidth(text));
	}


// fontrenderingcontext specific transformations

	public Shape getRotatedDecoration(Graphics2D g2, char[] chars, double radians) {
		Shape glyphOutline = null;
		AffineTransform transform = AffineTransform.getRotateInstance(radians);
		GlyphVector glyphVector = getNotationFont()
										.createGlyphVector(g2.getFontRenderContext(),chars);

		if (glyphVector.getNumGlyphs() > 0) {

			glyphOutline = glyphVector.getGlyphOutline(0);
			glyphOutline = transform.createTransformedShape(glyphOutline);

		}

		return(glyphOutline);
	}



// get various font instances


	/** Returns the notation font used for this score metrics.
	 * @return the notation font used for this score metrics. */
	public Font getNotationFont() {
		return noteFont;
	}


	/** Returns the font used for gracings in this score metrics.
	 * @return the font used for gracings in this score metrics. */
	public Font getGracingsFont() {
		return gracingsFont;
	}

	/** Returns the font used for text titles in this score metrics.
	 * @return the font used for text titles in this score metrics. */
	public Font getTitleFont() {
		return titleFont;
	}

	/** Returns the font used for  text subtitles in this score metrics.
	 * @return the font used for text subtitles in this score metrics. */
	public Font getSubtitleFont() {
		return subtitleFont;
	}

	/** Returns the font used for text annotations in this score metrics.
	 * @return the font used for text annotations in this score metrics. */
	public Font getAnnotationFont() {
		return annotationFont;
	}

	/** Returns the font used for lyrics in this score metrics.
	 * @return the font used for lyrics in this score metrics. */
	public Font getLyricsFont() {
		return lyricsFont;
	}
}

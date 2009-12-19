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
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import abc.notation.AccidentalType;
import abc.notation.Clef;
import abc.notation.Note;

/**
 * This class encapsulates all requiered dimensions needed to draw
 * correctly a score (notes spacing, space between accidental and notes etc...).
 * All those values are calculated from the font size.
 */
public class ScoreMetrics {

	public static final int NOTE_GLYPH = 100;
	public static final int GRACENOTE_GLYPH = 200;

	public static final char STAFF_SIX_LINES = '\uF03D';
	/** staff six lines / ratio = note height */
	public static final double STAFF_SIX_LINES_LINE_HEIGHT_RATIO = 4.1;
	public static final char[] BAR_LINE = {'\uF05C'};

	public static final char[] DOT = {'\uF06B'};

	public static final char[] STROKE = {'\uF05F'};
	/** The pure note character without any stem for notes such as 1/4, 1/8 and quicker... */
	public static final char[] NOTE = {'\uF0CF'};
	/** The pure note character without any stem for notes such as whole & half */
	public static final char[] NOTE_LONGER = {'\uF092'};

	/** Tuplet digits, in italics */
	private static final char[] TUPLET_DIGITS = {
		'\uF0C1', //1
		'\uF0AA', //2
		'\uF0A3', //3
		'\uF0A2', //4
		'\uF0B0', //5
		'\uF0A4', //6
		'\uF0A6', //7
		'\uF0A5', //8
		'\uF0BB'}; //9

	/** Digits for time signature */
	private static final char[] TIME_SIGNATURE_DIGITS = {
			'\uF030', //0
			'\uF031', //1
			'\uF032', //2
			'\uF033', //3
			'\uF034', //4
			'\uF035', //5
			'\uF036', //6
			'\uF037', //7
			'\uF038', //8
			'\uF039', //9
			};

	public static final char STEM_COMBINE_UP_SINGLE = '\uF021';
	public static final char STEM_COMBINE_UP_DOUBLE = '\uF040';

	private static final char G_CLEF = '\uF026';
	public static final char[] SHARP = {'\uF023'};
	public static final char[] SHARP_DOUBLE = {'\uF099'};//F0DC
	//TODO public static final char[] SHARP_HALF = {'\uF023'};
	public static final char[] FLAT = {'\uF062'};
	public static final char[] FLAT_DOUBLE = {'\uF0BA'};
	//TODO public static final char[] FLAT_HALF = {'\uF062'};
	//also exists "sharp and half", "flat and half" but
	//don't know how to write them in ABC
	public static final char[] NATURAL = {'\uF06E'};

	//Notes glyphes stem up
	private static final char[] WHOLE_NOTE = {'\uF092'};
	private static final char[] HALF_NOTE = {'\uF068'};
	private static final char[] QUARTER_NOTE = {'\uF071'};
	private static final char[] EIGHTH_NOTE = {'\uF065'};
	private static final char[] SIXTEENTH_NOTE = {'\uF072'};
	private static final char[] THIRTY_SECOND_NOTE = {'\uF078'};
	private static final char[] SIXTY_FOURTH_NOTE = {'\uF01C'};

	//Notes glyphes stem down
	private static final char[] WHOLE_NOTE_STEM_DOWN = {'\uF092'};
	private static final char[] HALF_NOTE_STEM_DOWN = {'\uF048'};
	private static final char[] QUARTER_NOTE_STEM_DOWN = {'\uF051'};
	private static final char[] EIGHTH_NOTE_STEM_DOWN = {'\uF045'};
	private static final char[] SIXTEENTH_NOTE_STEM_DOWN = {'\uF052'};
	private static final char[] THIRTY_SECOND_NOTE_STEM_DOWN = {'\uF058'};
	private static final char[] SIXTY_FOURTH_NOTE_STEM_DOWN = {'\uF01D'};

	//Rests glyphes
	private static final char[] DOUBLE_REST = {'\uF0E3'};
	private static final char[] WHOLE_REST = {'\uF0B7'};
	private static final char[] HALF_REST = {'\uF0EE'};
	private static final char[] QUARTER_REST = {'\uF0CE'};
	private static final char[] EIGHTH_REST = {'\uF0E4'};
	private static final char[] SIXTEENTH_REST = {'\uF0C5'};
	private static final char[] THIRTY_SECOND_REST = {'\uF0A8'};
	private static final char[] SIXTY_FOURTH_REST = {'\uF0F4'};

	/** Unknown note length */
	private static final char[] UNKNWON_NOTE = {'\uF0AD'};

	/** Default font size for musical symbols and texts */
	private static final float DEFAULT_NOTATION_SIZE = 45;
	private static final float DEFAULT_TEXT_SIZE = DEFAULT_NOTATION_SIZE / 4;
	private static final String NOTATION_FONT = "SONORA3.TTF";
//	private static final String DEFAULT_TEXT_FONT = "Dialog";

	/** Textual font for title
	 * @see #getTextFont(short)
	 */
	public static final short FONT_TITLE = 0;
	/** Textual font for subtitle
	 * @see #getTextFont(short)
	 */
	public static final short FONT_SUBTITLE = 1;
	/** Textual font for composer
	 * @see #getTextFont(short)
	 */
	public static final short FONT_COMPOSER = 2;
	/** Textual font for annotation
	 * @see #getTextFont(short)
	 */
	public static final short FONT_ANNOTATION = 3;
	/** Textual font for chord names
	 * @see #getTextFont(short)
	 */
	public static final short FONT_CHORDS = 4;
	/** Textual font for lyrics
	 * @see #getTextFont(short)
	 */
	public static final short FONT_LYRICS = 5;
	
	private String[][] textFontPreferences = new String[][] {
		/*FONT_TITLE*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_SUBTITLE*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_COMPOSER*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_ANNOTATION*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_CHORDS*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_LYRICS*/{"Palatino Linotype", "Arial", "Dialog"}
	};

	/**
	 * Map to store the calculated bounds of various glyphes
	 * 
	 * Avoid numerous <TT>*Bounds</TT> members for this class
	 */
	private Map bounds = new HashMap();
	
	private double noteHeight = -1;
	private double noteWidth = -1;

	private double timeSignatureNumberWidth = -1;
	private double timeSignatureNumberHeight = -1;
	private double tupletNumberYOffset = -1;

	private Rectangle2D staffCharBounds = null;
	//private double staffCharWidth = -1;
	double staffLineHeight = -1;
	private int staffLinesSpacing = -1;
	private int firstStaffTopMargin = -1;
	private int chordLineSpacing = -1;
	//TODO chordDiagramsSpacing

	private Font[] textFonts = null;

	private double biggestAccidentalWidth = -1;

	private BasicStroke notesLinkStroke = null;
	private BasicStroke stemStroke = null;
	private int noteStrokeLength = -1;
	private int stemLength = -1;

	private int notesSpacing = -1;

	private double slurAnchorYOffset = -1;
	private double slurThickness = -1;

	//	 gracings support
	double graceNoteHeight = 0;
	double graceNoteWidth = 0;
	BasicStroke graceNotesLinkStroke = null;
	BasicStroke graceNoteStemStroke = null;
	int graceNoteStrokeLength = 0;
	int graceNoteStemLength = 0;
	int graceNotesSpacing = 0;
	private float gracesSizeProportion = .6f;

	// fonts
	private Font baseNotationFont = null;
	private Font noteFont = null;
	private Font gracingsFont = null;

	private FontMetrics noteFontMetrics  = null;
	private FontMetrics gracingsFontMetrics  = null;

	private int decorationHeight = -1;

	private Graphics2D g2 = null;

	private float m_notationFontSize = -1;
	private float m_textFontSize = -1;

	/**
	 * ScoreMetrics with {@link #DEFAULT_NOTATION_SIZE default size}
	 * @param g2d
	 */
	protected ScoreMetrics(Graphics2D g2d) {
		this(g2d, DEFAULT_NOTATION_SIZE, DEFAULT_TEXT_SIZE);
	}

	/**
	 * ScoreMetrics with a specified font size.
	 * @param g2d
	 * @param size
	 * @see #setSize(float)
	 */
	protected ScoreMetrics(Graphics2D g2d, float notationSize) {
		this(g2d, notationSize, notationSize/4);
	}

	protected ScoreMetrics(Graphics2D g2d, float notationSize,
			float textSize) {
		try {
			g2 = g2d;
			//getClass().getResourceAsStream("SONORA.TTF");
			//File file =new File("D:/Perso/musicfonts/MIDIDESI/TRUETYPE/SONORA.TTF");
			//FileInputStream fontStream = new FileInputStream(file);
		    baseNotationFont = Font.createFont(Font.TRUETYPE_FONT, ScoreMetrics.this.getClass().getResourceAsStream(NOTATION_FONT));
		    m_notationFontSize = notationSize;
			m_textFontSize = textSize;

			initNoteFont(notationSize);
			initGracingsFont();
			initTextFonts();

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/* Sets reference font size (pts) that all other sizes are derived from.
		Gracings = 60%,
	   150% = 67.5pt, etc.
	*/
	/**
	 * Define (or redefine) the font size, then all elements of
	 * the score. {@link #DEFAULT_NOTATION_SIZE Default size} is 45.
	 *
	 * Reinitialize all values you could have set, like
	 * {@link #staffLinesSpacing}, {@link #slurThickness}
	 * @param size
	 */
	public void setNotationFontSize(float notationSize) {
		initNoteFont(notationSize);
		initGracingsFont();
		setTextFontSize(notationSize/DEFAULT_NOTATION_SIZE * DEFAULT_TEXT_SIZE);
	}
	public float getNotationFontSize() {
		return m_notationFontSize;
	}
	
	/**
	 * Return the base text font size<br>
	 * Titles, subtitles... sizes are derived from it.
	 */
	public float getTextFontSize() {
		return m_textFontSize;
	}

	/**
	 * Sets the base text font size<br>
	 * Titles, subtitles... sizes are derived from it.
	 * @param textSize
	 */
	public void setTextFontSize(float textSize) {
		m_textFontSize = textSize;
		initTextFonts();
	}

	private void initNoteFont(float size) {
		try {
			m_notationFontSize = size;

			//reset the bounds collection
			bounds.clear();
			
			//getClass().getResourceAsStream("SONORA.TTF");
			//File file =new File("D:/Perso/musicfonts/MIDIDESI/TRUETYPE/SONORA.TTF");
			//FileInputStream fontStream = new FileInputStream(file);
			//noteFont = Font.createFont(Font.TRUETYPE_FONT,
			//			getClass().getResourceAsStream("SONORA2.TTF"));
			//noteFont = noteFont.deriveFont(size);
			noteFont = baseNotationFont.deriveFont(size);
			noteFontMetrics = g2.getFontMetrics(noteFont);

			FontRenderContext frc = g2.getFontRenderContext();
			
			staffCharBounds = new TextLayout(new Character(STAFF_SIX_LINES).toString(), noteFont, frc).getBounds();
			//staffCharWidth = staffCharBounds.getWidth();
			staffLineHeight = staffCharBounds.getHeight()/STAFF_SIX_LINES_LINE_HEIGHT_RATIO;

			TextLayout noteLayout = null;
			noteLayout = new TextLayout(new Character(NOTE[0]).toString(), noteFont, frc);
			noteHeight = noteLayout.getBounds().getHeight();
			noteWidth = noteLayout.getBounds().getWidth();
			//noteHeight = staffCharBounds.getHeight()/STAFF_SIX_LINES_LINE_HEIGHT_RATIO; //4.1
			//noteWidth = new TextLayout(new Character(NOTE[0]).toString(), noteFont, frc).getBounds().getWidth();

			//FIXME: get proper decoration height ... or adjust this to be more generalized ...
			decorationHeight = (int)(noteLayout.getBounds().getHeight()*2.5);

			staffLinesSpacing = (int)(staffCharBounds.getHeight()*1.5);
			firstStaffTopMargin = (int)(staffCharBounds.getHeight()*.75);
			chordLineSpacing = (int)(staffCharBounds.getHeight()*1.25);

			//slurs
			slurAnchorYOffset = (noteHeight / 3);
			slurThickness = Math.max(noteHeight / 7, 1);

			//accidental
			bounds.put(SHARP, new TextLayout(new Character(SHARP[0]).toString(), noteFont, frc).getBounds());
			bounds.put(FLAT, new TextLayout(new Character(FLAT[0]).toString(), noteFont, frc).getBounds());
			bounds.put(NATURAL, new TextLayout(new Character(NATURAL[0]).toString(), noteFont, frc).getBounds());
			biggestAccidentalWidth = Math.max(
					Math.max(getFlatBounds().getWidth(),
							getNaturalBounds().getWidth()),
						getSharpBounds().getWidth());


			//numbers, digits
			Rectangle2D timeSignatureBounds = new TextLayout(new Character(getTimeSignatureDigitChar(2)).toString(), noteFont, frc).getBounds();
			timeSignatureNumberWidth = timeSignatureBounds.getWidth();
			timeSignatureNumberHeight = timeSignatureBounds.getHeight();
			
			tupletNumberYOffset = noteHeight / 4;

			notesLinkStroke = new BasicStroke((float)(noteWidth/3), 0, 0);
			stemStroke = new BasicStroke((float)(noteWidth/12));
			stemLength = (int)(noteHeight*3);
			noteStrokeLength = 2;

			//default note spacing, Engrave is here to arrange this, hehe :)
			notesSpacing = (int)(1.5*noteWidth);

			m_notationFontSize = size;
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	private void initGracingsFont() {
		try {
			int notationSize = noteFont.getSize();
			float gracingsSize = notationSize*getGracesSizeProportion();

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

	private void initTextFonts() {
		try {
			//skip that search if fonts already loaded, just resize them
			if (textFonts == null) {
				//Find fonts for title, chords, lyrics...
				//First, make a list of all wanted fonts, so we'll iterate only
				//once over the getAllFonts array
				Set wantedFontNames = new HashSet();
				for (int i = 0; i < textFontPreferences.length; i++) {
					for (int j = 0; j < textFontPreferences[i].length; j++) {
						wantedFontNames.add(textFontPreferences[i][j]);
					}
				}
				int wantedFontCount = wantedFontNames.size();
				Map foundFonts = new HashMap();
				Font[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
				for (int i = 0; i < availableFonts.length; i++) {
					if (wantedFontNames.contains(availableFonts[i].getFontName())) {
						foundFonts.put(availableFonts[i].getFontName(), availableFonts[i]);
						if (foundFonts.size() == wantedFontCount) {
							break; //Yeah! we got all we wanted!
						}
					}
				}
				//Now we know which wanted fonts are available
				textFonts = new Font[textFontPreferences.length];
				for (int i = 0; i < textFontPreferences.length; i++) {
					for (int j = 0; j < textFontPreferences[i].length; j++) {
						if (foundFonts.containsKey(textFontPreferences[i][j])) {
							textFonts[i] = (Font) foundFonts.get(textFontPreferences[i][j]);
							break;
						}
					}
				}
			}
//			for (int i = 0; i < textFonts.length; i++) {
//				textFonts[i] = textFonts[i].deriveFont(14*size/DEFAULT_NOTATION_SIZE);
//			}
//			for (int i = 0; i < textFonts.length; i++) {
//				System.out.println("textFonts["+i+"] = "+textFonts[i].getFontName());
//				Rectangle2D testFont = new TextLayout("A", textFonts[i], frc).getBounds();
//				System.out.println("h="+testFont.getHeight()+",w="+testFont.getWidth());
//			}

			textFonts[FONT_TITLE] = textFonts[FONT_TITLE].deriveFont(Font.BOLD, m_textFontSize*2);
			textFonts[FONT_SUBTITLE] = textFonts[FONT_SUBTITLE].deriveFont(Font.BOLD, (float)(m_textFontSize*1.5));
			textFonts[FONT_COMPOSER] = textFonts[FONT_COMPOSER].deriveFont(Font.BOLD, (float)(m_textFontSize*1.5));
			textFonts[FONT_ANNOTATION] = textFonts[FONT_ANNOTATION].deriveFont(Font.ITALIC, m_textFontSize);
			textFonts[FONT_LYRICS] = textFonts[FONT_LYRICS].deriveFont(m_textFontSize);
			textFonts[FONT_CHORDS] = textFonts[FONT_CHORDS].deriveFont(m_textFontSize);

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param notationContext {@link #NOTE_GLYPH}, {@link #GRACENOTE_GLYPH}
	 * @return
	 */
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

	/**
	 * Returns a text font (for lyrics, chord names, title...)
	 * @param textType {@link #FONT_CHORDS}, {@link #FONT_LYRICS}, {@link #FONT_TITLE}
	 * @return a Font object
	 */
	public Font getTextFont(short textType) {
		return textFonts[textType];
	}
	
	/**
	 * Sets a text font name (if exists) for one type of text<br>
	 * The font size is derived from the {@link #getTextFontSize() default text font size}.
	 * @param textType One of {@link #FONT_TITLE}, {@link #FONT_LYRICS}...
	 * @param fontNames Array of font names, in order of preference. If the first doesn't exist,
	 * it'll try to use the second...
	 */
	public void setTextFont(short textType, String[] fontNames) {
		/*Vector v = new Vector();
		for (int i = 0; i < fontNames.length; i++) {
			v.add((String) fontNames[i]);
		}
		for (int i = 0; i < textFontPreferences[textType].length; i++) {
			v.add((String) textFontPreferences[textType][i]);
		}*/
		String[] array = new String[fontNames.length+textFontPreferences[textType].length];
		System.arraycopy(fontNames, 0, array, 0, fontNames.length);
		System.arraycopy(textFontPreferences[textType], 0, array, fontNames.length, textFontPreferences[textType].length);
		textFontPreferences[textType] = array;
		//reset in order to check if wanted font exists
		textFonts = null; 
		initTextFonts();
	}
	
	/**
	 * Returns height of one line rendered in the textType font.
	 * @param textType one of {@link #FONT_TITLE}, {@link #FONT_CHORDS}...
	 * @return the height of a string in this font used for this score metrics
	 */
	public int getTextFontHeight(short textType) {
		FontMetrics fontMetrics = g2.getFontMetrics(getTextFont(textType));
		return fontMetrics.getHeight();
	}

	/**
	 * Returns width of string rendered in the textType font.
	 * @param textType one of {@link #FONT_TITLE}, {@link #FONT_CHORDS}...
	 * @param text The text we want to know its width
	 * @return the width of a string in this font used for this score metrics
	 */
	public int getTextFontWidth(short textType, String text) {
		if (text == null)
			return 0;
		FontMetrics fontMetrics = g2.getFontMetrics(getTextFont(textType));
		return fontMetrics.stringWidth(text);
	}
	
	public double getNoteHeight() {
		return noteHeight;
	}
	/** @deprecated {@link getNoteHeight()} */
	public double getNoteHeigth() {
		return getNoteHeight();
	}

	/**
	 * Returns the vertical offset between note head and slur/tie point
	 */
	public double getSlurAnchorYOffset() {
		return slurAnchorYOffset;
	}

	/**
	 * Sets the vertical offset between note head and slur/tie point
	 * @param d
	 */
	public void setSlurAnchorYOffset(double d) {
		slurAnchorYOffset = d;
	}

	/**
	 * Returns the slur/tie thickness
	 */
	public double getSlurThickness() {
		return slurThickness;
	}

	/**
	 * Sets the slur/tie thickness
	 * @param d
	 */
	public void setSlurThickness(double d) {
		slurThickness = Math.max(d, 1);
	}

	public double getNoteStrokeLength() {
		return noteStrokeLength;
	}

	public double getNoteWidth() {
		return noteWidth;
	}

	public double getTimeSignatureNumberWidth() {
		return timeSignatureNumberWidth;
	}

	public double getTimeSignatureNumberHeight() {
		return timeSignatureNumberHeight;
	}

	/**
	 * Returns the spacing between notes.
	 * Note that the engraver can change the spacings, this one is
	 * the default spacing, used as reference for engraver.
	 * @return The spacing between notes, expressed in pixels.
	 */
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

	public double getStaffLineHeight() {
		return staffLineHeight;
	}

	public int getStaffLinesSpacing() {
		return staffLinesSpacing;
	}
	public void setStaffLinesSpacing(int i) {
		staffLinesSpacing = i;
	}
	public int getFirstStaffTopMargin() {
		return firstStaffTopMargin;
	}
	public void setFirstStaffTopMargin(int i) {
		firstStaffTopMargin = i;
	}
	public int getChordLineSpacing() {
		return chordLineSpacing;
	}
	public void setChordLineSpacing(int i) {
		chordLineSpacing = i;
	}
	
	public BasicStroke getStemStroke(){
		return stemStroke;
	}

	public int getStemLength(){
		return stemLength;
	}

	/**
	 *
	 * @param notationContext {@link #NOTE_GLYPH}, {@link #GRACENOTE_GLYPH}
	 * @return
	 */
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
	 * @return Returns the bounding box of a sharp character.
	 * @deprecated use {@link #getAccidentalBounds(byte)}
	 */
	public Rectangle2D getSharpBounds(){
		return getAccidentalBounds(AccidentalType.SHARP);
	}

	/** Returns the bounding box of a natural character.
	 * @return Returns the bounding box of a natural character.
	 * @deprecated use {@link #getAccidentalBounds(byte)}
	 */
	public Rectangle2D getNaturalBounds(){
		return getAccidentalBounds(AccidentalType.NATURAL);
	}

	/** Returns the bounding box of a flat character.
	 * @return Returns the bounding box of a flat character.
	 * @deprecated use {@link #getAccidentalBounds(byte)}
	 */
	public Rectangle2D getFlatBounds(){
		return getAccidentalBounds(AccidentalType.FLAT);
	}
	
	/**
	 * Get the bounds of a glyph
	 * @param glyph {@link #SHARP}, {@link #FLAT}...
	 * @return
	 */
	public Rectangle2D getBounds(char[] glyph) {
		if (bounds.get(glyph) == null) {
			FontRenderContext frc = g2.getFontRenderContext();
			bounds.put(glyph, new TextLayout(
					new Character(glyph[0]).toString(),
					noteFont, frc).getBounds());
		}
		return (Rectangle2D) (bounds.get(glyph));
	}
	
	/**
	 * Return the glyph corresponding to the accidental, <TT>null</TT>
	 * if accidental = {@link AccidentalType#NONE}
	 * @param accidental
	 * @return
	 * @throws IllegalArgumentException
	 */
	public char[] getAccidentalGlyph(byte accidental)
		throws IllegalArgumentException {
		switch(accidental) {
		case AccidentalType.NONE:
			return null;
		case AccidentalType.FLAT:
			return FLAT;
		case AccidentalType.NATURAL:
			return NATURAL;
		case AccidentalType.SHARP:
			return SHARP;
		case AccidentalType.DOUBLE_FLAT:
			return FLAT_DOUBLE;
		case AccidentalType.DOUBLE_SHARP:
			return SHARP_DOUBLE;
		//TODO half sharp, half flat
		default: throw new IllegalArgumentException("Unknow accidental type : "+accidental);
		}
	}
	
	/**
	 * Return the bounding box of an accidental
	 * @return
	 * @throws IllegalArgumentException
	 */
	public Rectangle2D getAccidentalBounds(byte accidental)
		throws IllegalArgumentException {
		if (accidental == AccidentalType.NONE)
			return new Rectangle2D.Double(0, 0, 0, 0);
		else {
			return getBounds(getAccidentalGlyph(accidental));
		}
	}

	//TODO what is it for?
	public double getBiggestAccidentalWidth() {
		return biggestAccidentalWidth;
	}

	public Rectangle2D getQuarterNoteBounds(){
		return getBounds(QUARTER_NOTE);
	}

	/**
	 * @return Returns the tupletNumberYOffset.
	 */
	public double getTupletNumberYOffset() {
		return tupletNumberYOffset;
	}
	public void setTupletNumberYOffset(double d) {
		tupletNumberYOffset = d;
	}

	/**
	 * Return the font char for a tuplet number
	 * @param tupletNumber from 1 to 9
	 * @return
	 */
	public char getTupletDigitChar(int tupletNumber) {
		return TUPLET_DIGITS[tupletNumber - 1];
	}

	/**
	 * Returns the font char for a digit of the time signature
	 * @param digit from 0 to 9
	 * @return
	 */
	public char getTimeSignatureDigitChar(int digit) {
		return TIME_SIGNATURE_DIGITS[digit];
	}

	/**
	 * Return the font char for the clef
	 * @param clef {@link Clef#G} is the only one supported
	 * @return {@link #G_CLEF}
	 * @throws IllegalArgumentException if clefType is not equal to "G"
	 */
	public char getClefChar(Clef clef)
		throws IllegalArgumentException {
		if (clef.equals(Clef.G))
			return G_CLEF;
		else
			throw new IllegalArgumentException("Unsupported clef type "+clef.getName());
	}

	/**
	 * Return the char for a given note with a stem up
	 * @param strictDuration {@link Note#getStrictDuration()}, for
	 * example {@link {@link Note#QUARTER}
	 * @return a char[] containing glyph
	 */
	protected char[] getNoteStemUpChar(short strictDuration) {
		switch (strictDuration) {
			case Note.SIXTY_FOURTH: return SIXTY_FOURTH_NOTE;
			case Note.THIRTY_SECOND: return THIRTY_SECOND_NOTE;
			case Note.SIXTEENTH : return SIXTEENTH_NOTE;
			case Note.EIGHTH : return EIGHTH_NOTE;
			case Note.QUARTER: return QUARTER_NOTE;
			case Note.HALF: return HALF_NOTE;
			case Note.WHOLE: return WHOLE_NOTE;
			default : return UNKNWON_NOTE;
		}
	}

	/**
	 * Return the char for a given note with a stem down
	 * @param strictDuration {@link Note#getStrictDuration()}, for
	 * example {@link {@link Note#QUARTER}
	 * @return a char[] containing glyph
	 */
	protected char[] getNoteStemDownChar(short strictDuration) {
		switch (strictDuration) {
			case Note.SIXTY_FOURTH: return SIXTY_FOURTH_NOTE_STEM_DOWN;
			case Note.THIRTY_SECOND: return THIRTY_SECOND_NOTE_STEM_DOWN;
			case Note.SIXTEENTH : return SIXTEENTH_NOTE_STEM_DOWN;
			case Note.EIGHTH : return EIGHTH_NOTE_STEM_DOWN;
			case Note.QUARTER: return QUARTER_NOTE_STEM_DOWN;
			case Note.HALF: return HALF_NOTE_STEM_DOWN;
			case Note.WHOLE: return WHOLE_NOTE_STEM_DOWN;
			default: return UNKNWON_NOTE;
		}
	}

	/**
	 * Return the char for a given rest.
	 * @param strictDuration {@link Note#getStrictDuration()}, for
	 * example {@link {@link Note#QUARTER}
	 * @return a char[] containing glyph
	 */
	protected char[] getRestChar(short strictDuration) {
		switch (strictDuration) {
			case Note.SIXTY_FOURTH: return SIXTY_FOURTH_REST;
			case Note.THIRTY_SECOND: return THIRTY_SECOND_REST;
			case Note.SIXTEENTH : return SIXTEENTH_REST;
			case Note.EIGHTH : return EIGHTH_REST;
			case Note.QUARTER: return QUARTER_REST;
			case Note.HALF: return HALF_REST;
			case Note.WHOLE: return WHOLE_REST;
			//case Note.DOUBLE: return DOUBLE_REST; //is it breve?
			default: return UNKNWON_NOTE;
		}
	}

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

//fontrenderingcontext specific transformations

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

	/**
	 * Gets the proportion of graces size
	 * @return Float value between 0 and 1
	 */
	public float getGracesSizeProportion() {
		return gracesSizeProportion;
	}

	/**
	 * Sets the proportion of graces size
	 * @param f between 0f and 1f.
	 */
	public void setGracesSizeProportion(float f) {
		this.gracesSizeProportion = Math.max(Math.min(f, 1), 0);
		initGracingsFont();
	}

}
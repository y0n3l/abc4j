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
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import abc.notation.AccidentalType;
import abc.notation.Clef;
import abc.notation.Note;
import abc.ui.fonts.MissingGlyphException;
import abc.ui.fonts.MusicalFont;
import abc.ui.fonts.SonoraFont;

/**
 * This class encapsulates all requiered dimensions needed to draw
 * correctly a score (notes spacing, space between accidental and notes etc...).
 * All those values are calculated from the font size.
 */
public class ScoreMetrics {

	public static final int NOTATION_CONTEXT_NOTE = 100;
	public static final int NOTATION_CONTEXT_GRACENOTE = 200;
	public static final int NOTATION_CONTEXT_TEMPO = 150;

	/** staff six lines / ratio = note height */
	private static final double STAFF_FIVE_LINES_LINE_HEIGHT_RATIO = 4.1;

	/** Default font size for musical symbols and texts */
	private static final float DEFAULT_NOTATION_SIZE = 45;
	private static final float DEFAULT_TEXT_SIZE = DEFAULT_NOTATION_SIZE / 4;
	private static final String NOTATION_FONT = "SONORA3.TTF";
//	private static final String DEFAULT_TEXT_FONT = "Dialog";
	public static final double SPACE_RATIO_FOR_ACCIDENTALS = 0.3;

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
	/** Textual font for part labels
	 * @see #getTextFont(short)
	 */
	public static final short FONT_PART_LABEL = 6;
	
	private String[][] textFontPreferences = new String[][] {
		/*FONT_TITLE*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_SUBTITLE*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_COMPOSER*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_ANNOTATION*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_CHORDS*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_LYRICS*/{"Palatino Linotype", "Arial", "Dialog"},
		/*FONT_PART_LABEL*/{"Georgia", "Verdana", "Dialog"}
	};

	/**
	 * Map to store the calculated bounds of various glyphes
	 * 
	 * Avoid numerous <TT>*Bounds</TT> members for this class
	 */
	private Map bounds = new HashMap();
	
	private double tupletNumberYOffset = -1;

	//private Rectangle2D staffCharBounds = null;
	//private double staffCharWidth = -1;
	double staffLineHeight = -1;
	private int staffLinesSpacing = -1;
	private int firstStaffTopMargin = -1;
	private int firstStaffLeftMargin = -1;
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
	private BasicStroke graceNotesLinkStroke = null;
	private BasicStroke graceNoteStemStroke = null;
	private int graceNoteStrokeLength = 0;
	private int graceNoteStemLength = 0;
	private int graceNotesSpacing = 0;
	private float gracesSizeProportion = .6f;

	// fonts
	private Font baseNotationFont = null;
	private Font noteFont = null;
	private Font gracingsFont = null;
	private Font tempoFont = null;

	private FontMetrics noteFontMetrics  = null;
	//private FontMetrics gracingsFontMetrics  = null;

	private int decorationHeight = -1;

	private Graphics2D g2 = null;

	private float m_notationFontSize = -1;
	private float m_textFontSize = -1;
	private MusicalFont m_musicalFont = new SonoraFont();
	private char m_noteHeadWithoutStem;

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
			
			m_noteHeadWithoutStem = getMusicalFont().getNoteWithoutStem(Note.QUARTER);
			
			//getClass().getResourceAsStream("SONORA.TTF");
			//File file =new File("D:/Perso/musicfonts/MIDIDESI/TRUETYPE/SONORA.TTF");
			//FileInputStream fontStream = new FileInputStream(file);
			//noteFont = Font.createFont(Font.TRUETYPE_FONT,
			//			getClass().getResourceAsStream("SONORA2.TTF"));
			//noteFont = noteFont.deriveFont(size);
			noteFont = baseNotationFont.deriveFont(size);
			noteFontMetrics = g2.getFontMetrics(noteFont);
			
			tempoFont = baseNotationFont.deriveFont(size * .75f);
			
			Rectangle2D staffCharBounds = getStaffCharBounds();
			//staffCharWidth = staffCharBounds.getWidth();
			staffLineHeight = staffCharBounds.getHeight()/STAFF_FIVE_LINES_LINE_HEIGHT_RATIO;

			double noteHeight = getNoteHeight();
			double noteWidth = getNoteWidth();
			//noteHeight = staffCharBounds.getHeight()/STAFF_SIX_LINES_LINE_HEIGHT_RATIO; //4.1
			//noteWidth = new TextLayout(new Character(NOTE[0]).toString(), noteFont, frc).getBounds().getWidth();

			//FIXME: get proper decoration height ... or adjust this to be more generalized ...
			decorationHeight = (int)(noteHeight*2.5);

			staffLinesSpacing = (int)(staffCharBounds.getHeight()*1.5);
			firstStaffTopMargin = (int)(staffCharBounds.getHeight()*.75);
			firstStaffLeftMargin = 0;//*(int)staffCharBounds.getWidth();
			chordLineSpacing = (int)(staffCharBounds.getHeight()*1.25);

			//slurs
			slurAnchorYOffset = (noteHeight / 3);
			slurThickness = Math.max(noteHeight / 7, 1);

			//accidental
			biggestAccidentalWidth = Math.max(
					Math.max(getFlatBounds().getWidth(),
							getNaturalBounds().getWidth()),
						getSharpBounds().getWidth());

			tupletNumberYOffset = noteHeight / 4;

			notesLinkStroke = new BasicStroke((float)(noteWidth/3), 0, 0);
			stemStroke = new BasicStroke(Math.max(1f, (float)(noteWidth/12)));
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
			//gracingsFontMetrics = g2.getFontMetrics(gracingsFont);

			double graceNoteHeight = getGraceNoteHeight();
			double graceNoteWidth = getGraceNoteWidth();

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
			textFonts[FONT_PART_LABEL] = textFonts[FONT_PART_LABEL].deriveFont(Font.BOLD, (float)(m_textFontSize*1.5));

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param notationContext {@link #NOTATION_CONTEXT_NOTE}, {@link #NOTATION_CONTEXT_GRACENOTE}
	 * @return
	 */
	public Dimension getGlyphDimension(int notationContext) {
		Rectangle2D bounds = getBounds(m_noteHeadWithoutStem, notationContext);
		return new Dimension((int)bounds.getHeight(),
				(int)bounds.getWidth());
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
		return getBounds(m_noteHeadWithoutStem).getHeight();
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
		return getBounds(m_noteHeadWithoutStem).getWidth();
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
		return getBounds(getMusicalFont().getStaffFiveLines());
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
	public int getFirstStaffLeftMargin() {
		return firstStaffLeftMargin;
	}
	public void setFirstStaffLeftMargin(int i) {
		firstStaffLeftMargin = i;
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
	 * @param notationContext {@link #NOTATION_CONTEXT_NOTE}, {@link #NOTATION_CONTEXT_GRACENOTE}
	 * @return
	 */
	public int getStemLength(int notationContext){
		int len = 0;

		switch (notationContext) {
			case NOTATION_CONTEXT_NOTE: 		len = getStemLength();
									break;
			case NOTATION_CONTEXT_GRACENOTE : 	len = getGraceNoteStemLength();
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
	 * Get the bounds of a glyph in the default notation
	 * context.
	 * @param glyph
	 */
	public Rectangle2D getBounds(char glyph) {
		return getBounds(new char[]{glyph}, NOTATION_CONTEXT_NOTE);
	}
	
	/**
	 * Get the bounds of a glyph in the default notation
	 * context.
	 * @param glyph {@link #SHARP}, {@link #FLAT}...
	 */
	public Rectangle2D getBounds(char[] glyph) {
		return getBounds(glyph, NOTATION_CONTEXT_NOTE);
	}
	
	/**
	 * Get the bounds of a glyph in the default notation
	 * context.
	 * @param glyph
	 * @param notationContext {@link #NOTATION_CONTEXT_NOTE} or {@link #NOTATION_CONTEXT_GRACENOTE}
	 */
	public Rectangle2D getBounds(char glyph, int notationContext) {
		return getBounds(new char[]{glyph}, notationContext);
	}
	
	/**
	 * Get the bounds of a glyph in the given notation context
	 * @param glyph {@link #SHARP}, {@link #FLAT}...
	 * @param notationContext {@link #NOTATION_CONTEXT_NOTE} or {@link #NOTATION_CONTEXT_GRACENOTE}
	 */
	public Rectangle2D getBounds(char[] glyph, int notationContext) {
		String key = String.valueOf(notationContext)
			+ "-" + String.valueOf(glyph);
		if (bounds.get(key) == null) {
			FontRenderContext frc = g2.getFontRenderContext();
			bounds.put(key, new TextLayout(
					String.valueOf(glyph),//new Character(glyph[0]).toString(),
					getNotationFontForContext(notationContext),
					frc).getBounds());
		}
		return (Rectangle2D) (bounds.get(key));
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
		try {
			return new char[] { getMusicalFont().getAccidental(accidental) };
		} catch (MissingGlyphException mge) {
			throw new IllegalArgumentException("Unknow accidental type : "+accidental);
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
	 * Return the font char for the clef
	 * @param clef {@link Clef#G} is the only one supported
	 * @return {@link #G_CLEF}
	 * @throws IllegalArgumentException if clefType is not equal to "G"
	 * @throws MissingGlyphException
	 */
	public char getClefChar(Clef clef)
		throws IllegalArgumentException, MissingGlyphException {
		if (clef.equals(Clef.G))
			return getMusicalFont().getClef(clef);
		else
			throw new IllegalArgumentException("Unsupported clef type "+clef.getName());
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
		return getBounds(m_noteHeadWithoutStem, NOTATION_CONTEXT_GRACENOTE).getHeight();
	}

	public double getGraceNoteWidth() {
		return getBounds(m_noteHeadWithoutStem, NOTATION_CONTEXT_GRACENOTE).getWidth();
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

	/** Returns the notation font used for this score metrics.
	 * @return the notation font used for this score metrics. */
	public Font getNotationFont() {
		return getNotationFontForContext(NOTATION_CONTEXT_NOTE);
	}

	/**
	 * Return the notation for from the given notation context
	 * 
	 * @param notationContext
	 *            {@link #NOTATION_CONTEXT_NOTE},
	 *            {@link #NOTATION_CONTEXT_GRACENOTE}...
	 */
	public Font getNotationFontForContext(int notationContext) {
		switch(notationContext) {
		case NOTATION_CONTEXT_GRACENOTE: return gracingsFont;
		case NOTATION_CONTEXT_TEMPO: return tempoFont;
		case NOTATION_CONTEXT_NOTE:
		default:
			return noteFont;
		}
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
		bounds.clear();
		initGracingsFont();
	}
	
	/** Returns the musical font definition */
	public MusicalFont getMusicalFont() {
		return m_musicalFont;
	}

}
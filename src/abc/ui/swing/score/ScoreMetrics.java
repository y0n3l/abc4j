package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

/**
 * This class encapsulates all needed dimensions needed to draw
 * correctly a score (notes spacing, space between accidental and notes etc...).
 * All those values are calculated from the font size. */
public class ScoreMetrics {
	
	public static final char STAFF_SIX_LINES = '\uF03D';
	public static final char[] BAR_LINE = {'\uF05C'};
	
	public static final char[] DOT = {'\uF06B'};
	
	public static final char[] STROKE = {'\uF05F'};
	
	public static final char[] NOTE = {'\uF0CF'};
	
	public static final char STEM_COMBINE_UP_SINGLE = '\uF021';
	public static final char STEM_COMBINE_UP_DOUBLE = '\uF040';
	
	public static final char[] SHARP = {'\uF023'};
	public static final char[] FLAT = {'\uF062'};
	public static final char[] NATURAL = {'\uF06E'};
	public static final float DEFAULT_SIZE = 45;

	private double noteHeigth = -1;
	private double noteWidth = -1;
	private double staffCharWidth = -1;
	private Font myFont = null; 
	private Rectangle2D staffCharBounds = null;
	private Rectangle2D sharpBounds = null;
	private Rectangle2D naturalBounds = null;
	private Rectangle2D flatBounds = null;
	private Rectangle2D quarterNoteBounds = null;
	private BasicStroke notesLinkStroke = null; 
	private BasicStroke stemStroke = null;
	private int noteStrokeLength = -1;
	private int stemLength = -1;
	private int notesSpacing = -1;
	
	public ScoreMetrics(Graphics2D g2) {
		this(g2, DEFAULT_SIZE);
	}
	
	public ScoreMetrics(Graphics2D g2, float size) {
		try {
			  FontRenderContext frc = g2.getFontRenderContext();
			  //getClass().getResourceAsStream("SONORA.TTF");
			  //File file =new File("D:/Perso/musicfonts/MIDIDESI/TRUETYPE/SONORA.TTF");
			  //FileInputStream fontStream = new FileInputStream(file);
			  myFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("SONORA.TTF"));
			  myFont = myFont.deriveFont(size);
			  staffCharBounds = new TextLayout(new Character(STAFF_SIX_LINES).toString(), myFont, frc).getBounds();
			  noteHeigth = staffCharBounds.getHeight()/4.1;
			  staffCharWidth = staffCharBounds.getWidth();
			  sharpBounds = new TextLayout(new Character(SHARP[0]).toString(), myFont, frc).getBounds();
			  flatBounds = new TextLayout(new Character(FLAT[0]).toString(), myFont, frc).getBounds();
			  naturalBounds = new TextLayout(new Character(NATURAL[0]).toString(), myFont, frc).getBounds();
			  quarterNoteBounds =  new TextLayout(new Character(SNote.QUARTER_NOTE[0]).toString(), myFont, frc).getBounds();
			  noteWidth =  new TextLayout(new Character(NOTE[0]).toString(), myFont, frc).getBounds().getWidth();
			  notesLinkStroke = new BasicStroke((float)(noteWidth/3), 0, 0);
			  stemStroke = new BasicStroke((float)(noteWidth/12));
			  stemLength = (int)(noteHeigth*3);
			  noteStrokeLength = 2;
			  notesSpacing = (int)(1.5*noteWidth);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public double getNoteHeigth() {
		return noteHeigth;
	}
	
	public double getNoteStrokeLength() {
		return noteStrokeLength;
	}
	
	public double getNoteWidth() {
		return noteWidth;
	}
	
	public double getNotesSpacing() {
		return notesSpacing;
	}
	
	public BasicStroke getNotesLinkStroke(){
		return notesLinkStroke;
	}
	
	public Rectangle2D getStaffCharBounds(){
		return staffCharBounds;
	}
	
	public BasicStroke getStemStroke(){
		return stemStroke;
	}
	
	public int getStemLength(){
		return stemLength;
	}
	
	public Rectangle2D getSharpBounds(){
		return sharpBounds;
	}
	
	public Rectangle2D getNaturalBounds(){
		return naturalBounds;
	}
	
	public Rectangle2D getFlatBounds(){
		return flatBounds;
	}
	
	public Rectangle2D getQuarterNoteBounds(){
		return quarterNoteBounds;
	}
	
	public Font getFont() {
		return myFont;
	}
	
	public double getStaffCharWidth() {
		return staffCharWidth;
	}
}

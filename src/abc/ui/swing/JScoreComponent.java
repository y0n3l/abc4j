package abc.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import abc.notation.BarLine;
import abc.notation.KeySignature;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.RepeatBarLine;
import abc.notation.ScoreElementInterface;
import abc.notation.StaffEndOfLine;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.Tuplet;
import abc.notation.Tune.Score;
import abc.ui.swing.score.GroupOfNotesRenderer;
import abc.ui.swing.score.SBar;
import abc.ui.swing.score.SClef;
import abc.ui.swing.score.SKeySignature;
import abc.ui.swing.score.SNote;
import abc.ui.swing.score.SRenderer;
import abc.ui.swing.score.SRepeatBar;
import abc.ui.swing.score.STimeSignature;
import abc.ui.swing.score.ScoreMetrics;
import abc.ui.swing.score.StaffLine;

/**
 * This JComponent displays tunes scores.
 * <BR/> You can get them rendered like :
 * <IMG src="../../../images/scoreEx.jpg"/> 
 * <BR/>
 * To render a tune score, just invoke the <TT>setTune(Tune)</TT> method
 * with your tune.
 * @see #setTune(Tune) 
 * 
 */
public class JScoreComponent extends JComponent {
	/** The tune to be displayed. */
	protected Tune m_tune = null; 
	
	private int staffLinesOffset = -1;
	/** The dimensions of this score. */
	protected Dimension m_dimension = null;
	/** WTF ??? does not seem to be really taken into account anyway... */
	private int XOffset = 0;
	/** The place where all spacing dimensions are expressed. */
	protected ScoreMetrics m_metrics = null;
	/** The buffer where the score image is put before rendition in the swing component. */
	protected BufferedImage m_bufferedImage = null;
	/** The graphic context of the buffered image used to generate the score. */
	protected Graphics2D m_bufferedImageGfx = null;
	/** Set to <TT>true</TT> if the score drawn into the buffered image is
	 * outdated and does not represent the tune currently set. */
	protected boolean m_isBufferedImageOutdated = true;
	/** The staff lines drawings. */
	protected Vector m_staffLines = null;
	/** The size used for the score scale. */
	protected float m_size = 45;
	/** <TT>true</TT> if the rendition of the score should be justified, 
	 * <TT>false</TT> otherwise. */
	protected boolean m_isJustified = false;
	
	/** Default constructor. */
	public JScoreComponent() {
		m_staffLines = new Vector();
		m_dimension = new Dimension(1,1);
		initGfx();
	}
	
	protected void initGfx(){
		m_bufferedImage = new BufferedImage((int)m_dimension.getWidth(), (int)m_dimension.getHeight(), BufferedImage.TYPE_INT_RGB);
		m_bufferedImageGfx = (Graphics2D)m_bufferedImage.createGraphics();
		m_metrics = new ScoreMetrics(m_bufferedImageGfx, m_size);
	}
	
	/** Draws the current tune score into the given graphic context.
	 * @param g Graphic context. */
	public void drawIn(Graphics2D g){
		if(m_tune!=null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setFont(m_metrics.getFont());
			g2.setColor(Color.BLACK);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			// staff line width
			int staffCharNb = (int)(m_dimension.getWidth()/m_metrics.getStaffCharWidth());
			char[] staffS = new char[staffCharNb+1];
			for (int i=0; i<staffS.length; i++)
				staffS[i] = ScoreMetrics.STAFF_SIX_LINES;
			
			StaffLine currentStaffLine = null;
			for (int i=0; i<m_staffLines.size(); i++) {
				currentStaffLine = (StaffLine)m_staffLines.elementAt(i);
				currentStaffLine.render((Graphics2D)g);
				g.drawChars(staffS, 0, staffS.length, 0, (int)(currentStaffLine.getBase().getY()));
			}
		}
	}
	
	public void paint(Graphics g){
		if (m_isBufferedImageOutdated) {
			//System.out.println("buf image is outdated");
			if (m_bufferedImage==null || m_dimension.getWidth()>m_bufferedImage.getWidth() 
					|| m_dimension.getHeight()>m_bufferedImage.getHeight()) {
				initGfx();
			}
			m_bufferedImageGfx.setColor(getBackground());
			m_bufferedImageGfx.fillRect(0, 0, (int)m_bufferedImage.getWidth(), (int)m_bufferedImage.getHeight());
			drawIn(m_bufferedImageGfx);
			m_isBufferedImageOutdated=false;
		}
		((Graphics2D)g).drawImage(m_bufferedImage, 0, 0, null);
	}
	
	/** The size of the font used to display the music score.
	 * @param size The size of the font used to display the music score expressed in ? */
	public void setSize(float size){
		m_size = size;
		initGfx();
		if (m_tune!=null)
			setTune(m_tune);
		repaint();
	}
	
	/** Writes the currently set tune score to a jpg file.
	 * @param file The output JPEG file.
	 * @throws IOException Thrown if the given file cannot be accessed. */ 
	public void writeScoreTo(File file) throws IOException {
		if (m_bufferedImage==null || m_dimension.getWidth()>m_bufferedImage.getWidth() 
				|| m_dimension.getHeight()>m_bufferedImage.getHeight()) {
			initGfx();
		}
		m_bufferedImageGfx.setColor(getBackground());
		m_bufferedImageGfx.fillRect(0, 0, (int)m_bufferedImage.getWidth(), (int)m_bufferedImage.getHeight());
		drawIn(m_bufferedImageGfx);
		m_isBufferedImageOutdated=false;
		ImageIO.write(m_bufferedImage,"jpg",file);
	}
	
	/** Sets the tune to be renderered.
	 * @param tune The tune to be displayed. */
	public void setTune(Tune tune){
		m_tune = tune;
		if (m_metrics==null)
			m_metrics = new ScoreMetrics((Graphics2D)getGraphics());
		m_staffLines.removeAllElements();
		Score score = tune.getScore();
		//The spacing between two staff lines
		staffLinesOffset = (int)(m_metrics.getStaffCharBounds().getHeight()*2.5);
		Point2D cursor = new Point(XOffset, 0);
		double componentWidth =0, componentHeight = 0;
		double width = 0;
		ArrayList lessThanQuarter = new ArrayList();
		int durationInGroup = 0;
		int maxDurationInGroup = Note.QUARTER;
		int durationInCurrentMeasure = 0;
		KeySignature currentKey = tune.getKey();
		Tuplet tupletContainer = null;
		//TimeSignature currentTimeSignature = null;
		boolean currentStaffLineInitialized = false;
		StaffLine currentStaffLine = null;
		int staffLineNb = 0;
		for (int i=0; i<score.size(); i++) {
			ScoreElementInterface s = (ScoreElementInterface)score.elementAt(i);
			if (
					//detects the end of a group.
					(!(s instanceof Note)  
					|| (s instanceof Note && ((Note)s).isRest()) 
					//if we were in a tuplet and the current note isn't part of tuplet anymore or part of another tuplet
					|| (s instanceof NoteAbstract && tupletContainer!=null && (!tupletContainer.equals(((NoteAbstract)s).getTuplet())))
					//if we weren't in a tuplet and the new note is part of a tuplet.
					|| (s instanceof NoteAbstract && tupletContainer==null && ((NoteAbstract)s).isPartOfTuplet())
					|| (s instanceof Note && ((Note)s).getStrictDuration()>=Note.QUARTER) 
					|| (durationInCurrentMeasure!=0 && durationInCurrentMeasure%maxDurationInGroup==0))
					&& lessThanQuarter.size()!=0)
				{
				if (!currentStaffLineInitialized) {
					currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
					m_staffLines.addElement(currentStaffLine);
					currentStaffLineInitialized = true;
				}
				currentStaffLine.addElement(renderNotesInGroup(cursor, lessThanQuarter));
				lessThanQuarter.clear();
				durationInGroup = 0;
			}
			if (s instanceof KeySignature) 
				currentKey = (KeySignature)s;
			else
				if (s instanceof TimeSignature) {
					if (!currentStaffLineInitialized) {
						currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
						m_staffLines.addElement(currentStaffLine);
						currentStaffLineInitialized = true;
					}
					STimeSignature ts = new STimeSignature((TimeSignature)s, cursor, m_metrics);
					currentStaffLine.addElement(ts);
					width = ts.getWidth();//SingleNoteRenderer.render(m_metrics, (Point2D)cursor.clone(), (Note)s);
					int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
					cursor.setLocation(cursorNewLocationX, cursor.getY());
					//width = TimeSignatureRenderer.render(m_metrics, (Point2D)cursor.clone(), (TimeSignature)s);
					//int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
					//cursor.setLocation(cursorNewLocationX, cursor.getY());
					if(s.equals(TimeSignature.SIGNATURE_4_4)) maxDurationInGroup = 2*Note.QUARTER;
					else if(((TimeSignature)s).getDenominator()==8) maxDurationInGroup = 3*Note.EIGHTH;
						else if(((TimeSignature)s).getDenominator()==4) maxDurationInGroup = Note.QUARTER;
						/*else if(s.equals(TimeSignature.SIGNATURE_3_4)) maxDurationInGroup = Note.QUARTER;*/
				}
				else
					if (s instanceof NoteAbstract) {
						Note note = null;
						if (s instanceof MultiNote)
							note = ((MultiNote)s).getShortestNote();
						else
							note = ((Note)s);
						short strictDur = note.getStrictDuration();
						tupletContainer = note.getTuplet();
						// checks if this note should be part of a group.
						if (strictDur<Note.QUARTER && !note.isRest()) {
							durationInGroup+=(note).getDuration();
							//System.out.println("duration in group " + durationInGroup);
							lessThanQuarter.add(note);
							if (durationInGroup>=maxDurationInGroup) {
								if (!currentStaffLineInitialized) {
									currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
									m_staffLines.addElement(currentStaffLine);
									//currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
									currentStaffLineInitialized = true;
								}
								currentStaffLine.addElement(renderNotesInGroup(cursor, lessThanQuarter));
								lessThanQuarter.clear();
								durationInGroup = 0;
							}
						}
						else {
							if (!currentStaffLineInitialized) {
								currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
								m_staffLines.addElement(currentStaffLine);
								//currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
								currentStaffLineInitialized = true;
							}
							SNote noteR = new SNote(note, cursor, m_metrics);
							currentStaffLine.addElement(noteR);
							width = noteR.getWidth();//SingleNoteRenderer.render(m_metrics, (Point2D)cursor.clone(), (Note)s);
							int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
							cursor.setLocation(cursorNewLocationX, cursor.getY());
						}
						durationInCurrentMeasure+=note.getDuration();
						//System.out.println("duration in current measure " + durationInCurrentMeasure);
					}
					else
						if (s instanceof RepeatBarLine) {
							if (!currentStaffLineInitialized) {
								currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
								m_staffLines.addElement(currentStaffLine);
								//currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
								currentStaffLineInitialized = true;
							}
							SRepeatBar bar = new SRepeatBar((RepeatBarLine)s, cursor, m_metrics);
							currentStaffLine.addElement(bar);
							width = bar.getWidth();
							int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
							cursor.setLocation(cursorNewLocationX, cursor.getY());
							durationInCurrentMeasure=0;
						}
						else
						if (s instanceof BarLine) {
							if (!currentStaffLineInitialized) {
								currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
								m_staffLines.addElement(currentStaffLine);
								//currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
								currentStaffLineInitialized = true;
							}
							SBar bar = new SBar((BarLine)s, cursor, m_metrics);
							currentStaffLine.addElement(bar);
							width = bar.getWidth();
							int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
							cursor.setLocation(cursorNewLocationX, cursor.getY());
							durationInCurrentMeasure=0;
						}
						else
							if (s instanceof StaffEndOfLine) {
								//renderStaffLines(g2, cursor);
								staffLineNb++;
								if (cursor.getX()>componentWidth)
									componentWidth = (int)cursor.getX(); 
								/*cursor.setLocation(0, cursor.getY()+staffLinesOffset);*/
								//initNewStaffLine(currentKey, cursor, m_metrics);
								currentStaffLineInitialized = false;
							}
		}// Enf of score elements iteration.
		if (lessThanQuarter.size()!=0) {
			if (!currentStaffLineInitialized) {
				currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
				m_staffLines.addElement(currentStaffLine);
				//currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
				currentStaffLineInitialized = true;
			}
			//renderNotesInGroup(g2, lessThanQuarter, cursor);
			lessThanQuarter.clear();
			durationInGroup = 0;
		}
		//System.out.println("char staff woidth : " + m_metrics.getStaffCharWidth());
		//renderStaffLines(g2, cursor);
		if (cursor.getX()>componentWidth)
			componentWidth = (int)cursor.getX();
		componentHeight = (int)cursor.getY();
		m_dimension.setSize(componentWidth+m_metrics.getStaffCharBounds().getWidth(), 
				componentHeight+m_metrics.getStaffCharBounds().getHeight());
		setPreferredSize(m_dimension);
		setSize(m_dimension);
		if (m_isJustified)
			justify();
		m_isBufferedImageOutdated=true;
		repaint();
	}
	
/*	protected void renderStaffLines(Graphics2D g) {
		//System.out.println("char staff nb : " + staffCharNb);
		for (int i=0; i<m_staffLines.size(); i++) {
			StaffLine currentSL = (StaffLine)m_staffLines.elementAt(i);
		}
	}*/
	
	/** Changes the justification of the rendition score. This will
	 * set the staff lines aligment to justify in order to have a more
	 * elegant display. 
	 * @param isJustified <TT>true</TT> if the score rendition should be
	 * justified, <TT>false</TT> otherwise. */
	public void setJustification(boolean isJustified) {
		m_isJustified = isJustified;
		repaint();
	}
	
	/** Return <TT>true</TT> if the rendition staff lines alignment is
	 * justified, <TT>false</TT> otherwise.
	 * @return <TT>true</TT> if the rendition staff lines alignment is
	 * justified, <TT>false</TT> otherwise. */
	public boolean iJustified() {
		return m_isJustified;
	}

	/** Triggers the re computation of all staff lines elements in order to 
	 * get the alignment justified. */
	protected void justify() {
		if (m_staffLines.size()>0) {
			double maxWidth = ((StaffLine)m_staffLines.elementAt(0)).getWidth();
			for (int i=1; i<m_staffLines.size();i++){
				StaffLine currentStaffLine = (StaffLine)m_staffLines.elementAt(i);
				if (currentStaffLine.getWidth()>maxWidth)
					maxWidth = currentStaffLine.getWidth();
			}
			for (int i=0; i<m_staffLines.size();i++) {
				StaffLine currentStaffLine = (StaffLine)m_staffLines.elementAt(i);
				if (currentStaffLine.getWidth()>maxWidth/2)
					currentStaffLine.scaleToWidth(maxWidth);
			}
		}
	}
	
	private SRenderer renderNotesInGroup(Point2D cursor, ArrayList lessThanQuarter){
		SRenderer renditionResult = null;
		Note[] notes = (Note[])lessThanQuarter.toArray(new Note[lessThanQuarter.size()]);
		double width = 0;
		if (notes.length==1) {
			renditionResult = new SNote(notes[0], cursor, m_metrics);
			//width = renditionResultsn.render(context);
		}
		else {
			renditionResult = new GroupOfNotesRenderer(m_metrics, cursor, notes);
			//width = (int)rend.render(context);
		}
		//System.out.println("width of group is " + width);
		width = renditionResult.getWidth();
		int cursorNewLocationX = (int)(cursor.getX() + width);
		cursor.setLocation(cursorNewLocationX, cursor.getY());
		cursorNewLocationX = (int)(cursor.getX() + m_metrics.getNotesSpacing());
		cursor.setLocation(cursorNewLocationX, cursor.getY());
		return renditionResult;
	}
	
	private StaffLine initNewStaffLine(KeySignature key, TimeSignature ts, Point2D cursor, ScoreMetrics metrix){
		StaffLine sl = new StaffLine(cursor, metrix);
		//Vector initElements = new Vector();
		cursor.setLocation(0, cursor.getY()+staffLinesOffset);
		SClef clef = new SClef(cursor, metrix);
		sl.addElement(clef);
		//initElements.addElement(clef);
		double width = clef.getWidth();
		cursor.setLocation(cursor.getX()+width, cursor.getY());
		if (ts!=null) {
			STimeSignature sig = new STimeSignature(ts, cursor, metrix);
			sl.addElement(sig);
			//initElements.addElement(sig);
			width = (int)sig.getWidth();
			int cursorNewLocationX = (int)(cursor.getX() + width + metrix.getNotesSpacing());
			cursor.setLocation(cursorNewLocationX, cursor.getY());
		}
		if (key!=null) {
			SKeySignature sk = new SKeySignature(key, cursor, metrix);
			sl.addElement(sk);
			//initElements.addElement(sk);
			width = sk.getWidth();
			int cursorNewLocationX = (int)(cursor.getX() + width + metrix.getNotesSpacing());
			cursor.setLocation(cursorNewLocationX, cursor.getY());
		}
		//SRenderer[] initElementsAsArray = new SRenderer[initElements.size()];
		//initElements.toArray(initElementsAsArray);
		return sl;//new StaffLine(cursor, metrix, initElementsAsArray);
	}
}

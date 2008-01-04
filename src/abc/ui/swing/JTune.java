package abc.ui.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import abc.notation.BarLine;
import abc.notation.KeySignature;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.NotesSeparator;
import abc.notation.RepeatBarLine;
import abc.notation.MusicElement;
import abc.notation.EndOfStaffLine;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.Tuplet;
import abc.notation.TwoNotesLink;
import abc.notation.Tune.Music;

/**
 * This class role is to render properly a tune using Java 2D.
 * @see #setTune(Tune)   
 */
class JTune extends JScoreElementAbstract {
	/** The tune to be displayed. */
	protected Tune m_tune = null; 
	/** Hashmap that associates ScoreElement instances (key) and JScoreElement instances(value).
	 * It contains : JChord, JNote, JNotePartOfGroup, JChordNote instances.
	 * */
	protected Hashtable m_scoreElements = null;
	/** Note instances starting Slurs and ties. */
	protected Vector m_beginningNotesLinkElements = null;
	
	private int m_staffLinesOffset = -1;
	/** The dimensions of this score. */
	//protected Dimension m_dimension = null;
	/** WTF ??? does not seem to be really taken into account anyway... */
	private int XOffset = 0;
	/** The staff lines drawings. */
	protected Vector m_staffLines = null;
	/** <TT>true</TT> if the rendition of the score should be justified, 
	 * <TT>false</TT> otherwise. */
	protected boolean m_isJustified = false;
	
	protected double m_height = -1;
	
	//temporary variables used only to cumpute the score when tune is set.
	private boolean currentStaffLineInitialized = false;
	private JStaffLine currentStaffLine = null;
	private KeySignature currentKey = null;
	private TimeSignature currentTime = null;
	private Point2D cursor = null;
	
	public JTune(Tune tune, Point2D base, ScoreMetrics c) {
		this(tune, base, c, false);
	}
	
	public JTune(Tune tune, Point2D base, ScoreMetrics c, boolean isJustified) {
		super(c);
		m_staffLines = new Vector();
		m_isJustified = isJustified;
		m_scoreElements = new Hashtable();
		m_beginningNotesLinkElements = new Vector();
		setTune(tune);
		setBase(base);
	}
	
	public void onBaseChanged() {
		
	}
	
	public MusicElement getMusicElement() {
		return null;
	}
	
	public double getHeight() {
		return m_height;
	}
	
	public Tune getTune() {
		return m_tune;
	}
	
	/** Returns the hashtable that maps pure music objects to their corresponding 
	 * rendition objects.
	 * @return Returns the hashtable that maps pure music objects (ScoreElement instances 
	 * from abc.notation.* package) to rendition objects (JScoreElement instances from
	 * abc.ui.swing.score.* package) */
	public Hashtable getRenditionObjectsMapping() {
		return m_scoreElements;
	}
	
	/** Returns the part of the score (as a JScoreElement instance) located
	 * at a given point. 
	 * @param location A location as a point.
	 * @return The part of the score (as a JScoreElement instance) located
	 * at a given point. <TT>NULL</NULL> is returned if not JScoreElement is 
	 * matching the location.
	 */
	public JScoreElementAbstract getScoreElementAt(Point location) {
		JScoreElementAbstract scoreEl = null;
		for (int i=0; i<m_staffLines.size(); i++) {
			scoreEl = ((JStaffLine)m_staffLines.elementAt(i)).getScoreElementAt(location);
			if (scoreEl!=null)
				return scoreEl;
			scoreEl = null;
		}
		return scoreEl;
	}
	
	/** Sets the tune to be renderered.
	 * @param tune The tune to be displayed. */
	public void setTune(Tune tune){
		m_tune = tune;
		m_scoreElements.clear();
		m_staffLines.removeAllElements();
		Music score = tune.getScore();
		//The spacing between two staff lines
		m_staffLinesOffset = (int)(m_metrics.getStaffCharBounds().getHeight()*2.5);
		cursor = new Point(XOffset, 0);
		double componentWidth =0, componentHeight = 0;
		ArrayList lessThanQuarter = new ArrayList();
		//int durationInGroup = 0;
		//int maxDurationInGroup = Note.QUARTER;
		//int durationInCurrentMeasure = 0;
		Tuplet tupletContainer = null;
		int staffLineNb = 0;
		//init attributes that are for iterating through the score of the tune.
		currentKey = tune.getKey();
		currentTime = null;
		currentStaffLineInitialized = false;
		currentStaffLine = null;
		for (int i=0; i<score.size(); i++) {
			MusicElement s = (MusicElement)score.elementAt(i);
			if (
					(
						!(s instanceof Note  || s instanceof MultiNote)  
					|| (s instanceof Note && ((Note)s).isRest()) 
					//if we were in a tuplet and the current note isn't part of tuplet anymore or part of another tuplet
					|| (s instanceof NoteAbstract && tupletContainer!=null && (!tupletContainer.equals(((NoteAbstract)s).getTuplet())))
					//if we weren't in a tuplet and the new note is part of a tuplet.
					|| (s instanceof NoteAbstract && tupletContainer==null && ((NoteAbstract)s).isPartOfTuplet())
					|| (s instanceof Note && ((Note)s).getStrictDuration()>=Note.QUARTER)
					|| (s instanceof MultiNote && (  !((MultiNote)s).hasUniqueStrictDuration() || 
														((MultiNote)s).getLongestNote().getStrictDuration()>=Note.QUARTER)))

					&& lessThanQuarter.size()!=0) {
				//this is is the end of the group, append the current group content to the score.
				appendToScore(lessThanQuarter);
				lessThanQuarter.clear();
			}
			if (s instanceof KeySignature) 
				currentKey = (KeySignature)s;
			else
				if (s instanceof TimeSignature) {
					currentTime = (TimeSignature)s;
				}
				else
					if (s instanceof MultiNote) {
						tupletContainer = ((MultiNote)s).getTuplet();
						Note[] tiesStart = ((MultiNote)s).getNotesBeginningTie();
						if (tiesStart!=null)
							for (int j=0; j<tiesStart.length; j++)
							m_beginningNotesLinkElements.addElement(tiesStart[j]);
						if (((MultiNote)s).hasUniqueStrictDuration() && 
								((MultiNote)s).getLongestNote().getStrictDuration()<Note.QUARTER)
							lessThanQuarter.add(s);
						else {
							appendToScore(new JChord((MultiNote)s, m_metrics,cursor));
						}
						
						//durationInCurrentMeasure+=((MultiNote)s).getLongestNote().getDuration();
					}
					else
					if (s instanceof Note) {
						Note note = (Note)s;
						if (note.isBeginingSlur() || note.isBeginningTie())
							m_beginningNotesLinkElements.addElement(note);
						short strictDur = note.getStrictDuration();
						tupletContainer = note.getTuplet();
						// checks if this note should be part of a group.
						if (strictDur<Note.QUARTER && !note.isRest()) {
							//durationInGroup+=(note).getDuration();
							//System.out.println("duration in group " + durationInGroup);
							lessThanQuarter.add(note);
							/*if (durationInGroup>=maxDurationInGroup) {
								appendToScore(lessThanQuarter);
								lessThanQuarter.clear();
								durationInGroup = 0;
							}*/
						}
						else {
							
							JNote noteR = new JNote(note, cursor, m_metrics);
							if (note.getHeight()>Note.c)
								noteR.setStemUp(false);
							appendToScore(noteR);
						}
						//durationInCurrentMeasure+=note.getDuration();
					}
					else
						if (s instanceof RepeatBarLine) {
							appendToScore(new JRepeatBar((RepeatBarLine)s, cursor, m_metrics));
							//durationInCurrentMeasure=0;
						}
						else
						if (s instanceof BarLine) {
							appendToScore(new JBar((BarLine)s, cursor, m_metrics));
							//durationInCurrentMeasure=0;
						}
						else
							if (s instanceof EndOfStaffLine) {
								//renderStaffLines(g2, cursor);
								staffLineNb++;
								if (cursor.getX()>componentWidth)
									componentWidth = (int)cursor.getX(); 
								/*cursor.setLocation(0, cursor.getY()+staffLinesOffset);*/
								//initNewStaffLine(currentKey, cursor, m_metrics);
								currentStaffLineInitialized = false;
							}
							else
								if (s instanceof NotesSeparator) {
									appendToScore(lessThanQuarter);
									lessThanQuarter.clear();
								}
		}// Enf of score elements iteration.
		if (lessThanQuarter.size()!=0) {
			appendToScore(lessThanQuarter);
			lessThanQuarter.clear();
			//durationInGroup = 0;
		}
		if (cursor.getX()>componentWidth)
			componentWidth = (int)cursor.getX();
		componentHeight = (int)cursor.getY();
		
		m_width = componentWidth+m_metrics.getStaffCharBounds().getWidth();
		m_height = componentHeight+m_metrics.getStaffCharBounds().getHeight();
		
		if (m_isJustified)
			justify();
		
	}
	
	protected void appendToScore(JScoreElementAbstract element) {
		if (!currentStaffLineInitialized) {
			currentStaffLine=initNewStaffLine();
			m_staffLines.addElement(currentStaffLine);
			//currentStaffLine=initNewStaffLine(currentKey, null, cursor, m_metrics);
			currentStaffLineInitialized = true;
			element.setBase(cursor);
		}
		//element.setBase(cursor);
		currentStaffLine.addElement(element);
		double width = element.getWidth();
		int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
		cursor.setLocation(cursorNewLocationX, cursor.getY());
		if (element instanceof JNote)
			m_scoreElements.put(((JScoreElementAbstract)element).getMusicElement(), element);
		else
			if (element instanceof JGroupOfNotes) {
				JGroupOfNotes g = (JGroupOfNotes)element;
				for (int j=0; j<g.getRenditionElements().length; j++) 
					if ((g.getRenditionElements()[j]) instanceof JNote)
						m_scoreElements.put(g.getMusicElements()[j], g.getRenditionElements()[j]);
					else
						if (g.getRenditionElements()[j] instanceof JChord){
							JNote[] jnotes = ((JChord)g.getRenditionElements()[j]).getScoreElements();
							Vector notes = ((MultiNote)((JChord)g.getRenditionElements()[j]).getMusicElement()).getNotesAsVector();
							//adds all the notes of the chords into the hashtable
							//TODO the ordering of the get notes as vector and the jnotes should be the same...
							//System.out.println("Warning - abc4j - current limitation prevents you from using chords with different notes lengths.");
							for (int i=0; i<jnotes.length; i++)
								m_scoreElements.put(notes.elementAt(i), jnotes[i]);
							//adds also the chords itself
							m_scoreElements.put(g.getRenditionElements()[j].getMusicElement(), g.getRenditionElements()[j]);
						}
			}
			else
				if (element instanceof JChord) {
					JNote[] jnotes = ((JChord)element).getScoreElements();
					Vector notes = ((MultiNote)((JChord)element).getMusicElement()).getNotesAsVector();
					//adds all the notes of the chords into the hashtable
					//TODO the ordering of the get notes as vector and the jnotes should be the same...
					//System.out.println("Warning - abc4j - current limitation prevents you from using chords with different notes lengths.");
					for (int i=0; i<jnotes.length; i++)
						m_scoreElements.put(notes.elementAt(i), jnotes[i]);
					//adds also the chords itself
					m_scoreElements.put(((JScoreElementAbstract)element).getMusicElement(), element);
				}
	}
	
	private void appendToScore(ArrayList lessThanQuarterGroup){
		if (lessThanQuarterGroup.size()>0) {
		JScoreElementAbstract renditionResult = null;
		JScoreElementAbstract[] renditionResultRootsElmts = new JScoreElementAbstract[lessThanQuarterGroup.size()];
		NoteAbstract[] notes = (NoteAbstract[])lessThanQuarterGroup.toArray(new NoteAbstract[lessThanQuarterGroup.size()]);
		
			if (notes.length==1) {
				if (notes[0] instanceof Note) {
					renditionResult = new JNote((Note)notes[0], cursor, m_metrics);
					renditionResultRootsElmts[0] = renditionResult;
				}
				else {
					//This is a multi note
					renditionResult = new JChord((MultiNote)notes[0], m_metrics, cursor);
						renditionResultRootsElmts[0] = renditionResult;
				}
					
			}
			else {
				renditionResult = new JGroupOfNotes(m_metrics, cursor, notes);
				renditionResultRootsElmts = ((JGroupOfNotes)renditionResult).getRenditionElements();
			}
		appendToScore(renditionResult);
		}
	}
	
	public double render(Graphics2D g2) {
		g2.setFont(m_metrics.getFont());
		g2.setColor(Color.BLACK);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
		// staff line width
		int staffCharNb = (int)(m_width/m_metrics.getStaffCharBounds().getWidth());
		char[] staffS = new char[staffCharNb+1];
		for (int i=0; i<staffS.length; i++)
			staffS[i] = ScoreMetrics.STAFF_SIX_LINES;
				
		JStaffLine currentStaffLine = null;
		for (int i=0; i<m_staffLines.size(); i++) {
			currentStaffLine = (JStaffLine)m_staffLines.elementAt(i);
			currentStaffLine.render(g2);
			g2.drawChars(staffS, 0, staffS.length, 0, (int)(currentStaffLine.getBase().getY()));
		}
		renderSlursAndTies(g2);
		return m_width;
	}
	
	/** Triggers the re computation of all staff lines elements in order to 
	 * get the alignment justified. */
	protected void justify() {
		if (m_staffLines.size()>0) {
			double maxWidth = ((JStaffLine)m_staffLines.elementAt(0)).getWidth();
			for (int i=1; i<m_staffLines.size();i++){
				JStaffLine currentStaffLine = (JStaffLine)m_staffLines.elementAt(i);
				if (currentStaffLine.getWidth()>maxWidth)
					maxWidth = currentStaffLine.getWidth();
			}
			for (int i=0; i<m_staffLines.size();i++) {
				JStaffLine currentStaffLine = (JStaffLine)m_staffLines.elementAt(i);
				if (currentStaffLine.getWidth()>maxWidth/2)
					currentStaffLine.scaleToWidth(maxWidth);
			}
		}
	}
	
	protected void renderSlursAndTies(Graphics2D g2) {
		for (int j=0; j<m_beginningNotesLinkElements.size(); j++) {
			NoteAbstract n = (NoteAbstract)m_beginningNotesLinkElements.elementAt(j);
			TwoNotesLink link = n.getSlurDefinition() ;
			if (link==null)
				link = ((Note)n).getTieDefinition();
			if (link.getEnd()!=null)
				drawLinkDown(g2, link);
		}
	}
	//FIXME : well, slurs and ties shouldn't be drawn the same way :
	// slurs, should be drawn under/upper all the notes that are part of the slur
	// ties, should consider notes between the 2 notes tied.
	protected void drawLinkDown(Graphics2D g2, TwoNotesLink slurDef) {
		JNote elmtStart =  (JNote)m_scoreElements.get(slurDef.getStart());
		if (slurDef.getEnd()!=null){
			JNote elmtEnd =  (JNote)m_scoreElements.get(slurDef.getEnd());
			if (elmtStart.getStaffLine().equals(elmtEnd.getStaffLine())) {
				Point2D controlPoint = null;
				Note lowestNote = m_tune.getScore().getLowestNoteBewteen(slurDef.getStart(), slurDef.getEnd());
				if (lowestNote.equals(slurDef.getStart()))
					controlPoint = new Point2D.Double(
							elmtStart.getSlurDownAnchor().getX()+ (elmtEnd.getSlurDownAnchor().getX()-elmtStart.getSlurDownAnchor().getX())/2,
							elmtStart.getSlurDownAnchor().getY()+ m_metrics.getNoteWidth()/4);
				else
					if (lowestNote.equals(slurDef.getEnd()))
						controlPoint = new Point2D.Double(
								elmtStart.getSlurDownAnchor().getX()+ (elmtEnd.getSlurDownAnchor().getX()-elmtStart.getSlurDownAnchor().getX())/2,
								elmtEnd.getSlurDownAnchor().getY()+ m_metrics.getNoteWidth()/4);
				
				else {
					JNote lowestNoteGlyph = (JNote)m_scoreElements.get(lowestNote);
					controlPoint = new Point2D.Double(lowestNoteGlyph.getSlurDownAnchor().getX(), 
							lowestNoteGlyph.getSlurDownAnchor().getY() + m_metrics.getNoteWidth()/2);
				}
				GeneralPath path = new GeneralPath();
				path.moveTo((int)elmtStart.getSlurDownAnchor().getX(), (int)elmtStart.getSlurDownAnchor().getY());
				QuadCurve2D q = new QuadCurve2D.Float();
				q.setCurve(
						elmtStart.getSlurDownAnchor(),
						newControl(elmtStart.getSlurDownAnchor(), controlPoint, elmtEnd.getSlurDownAnchor()), 
						elmtEnd.getSlurDownAnchor());
				path.append(q, true);
				q = new QuadCurve2D.Float();
				controlPoint.setLocation(controlPoint.getX(), controlPoint.getY()+m_metrics.getNoteWidth()/8);
				q.setCurve(
						elmtEnd.getSlurDownAnchor(),
						newControl(elmtStart.getSlurDownAnchor(), controlPoint, elmtEnd.getSlurDownAnchor()), 
						elmtStart.getSlurDownAnchor());
				path.append(q, true);
				
				g2.fill(path);
				g2.draw(path);
			}
			else
				System.err.println("Warning - ab4j limitation : Slurs / ties cannot be drawn accross several lines for now.");
		}
	}
	
	private JStaffLine initNewStaffLine() {
		JStaffLine sl = new JStaffLine(cursor, m_metrics);
		//Vector initElements = new Vector();
		cursor.setLocation(0, cursor.getY() + m_staffLinesOffset);
		JClef clef = new JClef(cursor, m_metrics);
		sl.addElement(clef);
		//initElements.addElement(clef);
		double width = clef.getWidth();
		cursor.setLocation(cursor.getX()+width, cursor.getY());
		if (currentKey!=null) {
			JKeySignature sk = new JKeySignature(currentKey, cursor, m_metrics);
			sl.addElement(sk);
			//initElements.addElement(sk);
			width = sk.getWidth();
			int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
			cursor.setLocation(cursorNewLocationX, cursor.getY());
		}
		if (currentTime!=null && m_staffLines.size()==0) {
			try {
				JTimeSignature sig = new JTimeSignature(currentTime, cursor, m_metrics);
				sl.addElement(sig);
				//initElements.addElement(sig);
				width = (int)sig.getWidth();
				int cursorNewLocationX = (int)(cursor.getX() + width + m_metrics.getNotesSpacing());
				cursor.setLocation(cursorNewLocationX, cursor.getY());
			}
			catch (Exception e) {
				// This happens when the time signature is a bit "exotic", the exception 
				// will lead the time signature not be displayed.
			}
		}
		//SRenderer[] initElementsAsArray = new SRenderer[initElements.size()];
		//initElements.toArray(initElementsAsArray);
		return sl;//new StaffLine(cursor, metrix, initElementsAsArray);
	}
	
	/**
	 * implementation found at 
	 * http://forum.java.sun.com/thread.jspa?threadID=609888&messageID=3362448
	 * This enables the bezier curve to be tangent to the control point.
	 */
	private Point2D newControl (Point2D start, Point2D ctrl, Point2D end) {
	        Point2D.Double newCtrl = new Point2D.Double();
	        newCtrl.x = 2 * ctrl.getX() - (start.getX() + end.getX()) / 2;
	        newCtrl.y = 2 * ctrl.getY() - (start.getY() + end.getY()) / 2;
	        return newCtrl;
	    }


}

package abc.ui.swing.score;

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
import abc.notation.RepeatBarLine;
import abc.notation.ScoreElementInterface;
import abc.notation.StaffEndOfLine;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.Tuplet;
import abc.notation.TwoNotesLink;
import abc.notation.Tune.Score;
import abc.ui.swing.JScoreElement;

public class JTune extends JScoreElement {
	/** The tune to be displayed. */
	protected Tune m_tune = null; 
	/** Hashmap that associates ScoreElement instances (key) and JScoreElement instances(value) */
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
	/** The size used for the score scale. */
	//protected float m_size = 45;
	/** <TT>true</TT> if the rendition of the score should be justified, 
	 * <TT>false</TT> otherwise. */
	protected boolean m_isJustified = false;
	
	protected double m_height = -1;
	
	public JTune(Tune tune, Point2D base, ScoreMetrics c) {
		this(tune, base, c, false);
	}
	
	
	public JTune(Tune tune, Point2D base, ScoreMetrics c, boolean isJustified) {
		super(base, c);
		m_staffLines = new Vector();
		m_isJustified = isJustified;
		m_scoreElements = new Hashtable();
		m_beginningNotesLinkElements = new Vector();
		setTune(tune);
		onBaseChanged();
	}
	
	public void onBaseChanged() {
		
	}
	
	public ScoreElementInterface getScoreElement() {
		return null;
	}
	
	public double getHeight() {
		return m_height;
	}
	
	public Tune getTune() {
		return m_tune;
	}
	
	public Hashtable getRenditionObjectsMapping() {
		return m_scoreElements;
	}
	
	
	public JScoreElement getScoreElementAt(Point point) {
		JScoreElement scoreEl = null;
		for (int i=0; i<m_staffLines.size(); i++) {
			scoreEl = ((StaffLine)m_staffLines.elementAt(i)).getScoreElementAt(point);
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
		/*if (m_metrics==null)
			m_metrics = new ScoreMetrics((Graphics2D)getGraphics());*/
		m_staffLines.removeAllElements();
		Score score = tune.getScore();
		//The spacing between two staff lines
		m_staffLinesOffset = (int)(m_metrics.getStaffCharBounds().getHeight()*2.5);
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
				JScoreElement groupRendition= renderNotesInGroup(cursor, lessThanQuarter);
				currentStaffLine.addElement(groupRendition);
				if (groupRendition instanceof SNote)
					m_scoreElements.put(((SNote)groupRendition).getNote(), groupRendition);
				else {
					GroupOfNotesRenderer g = (GroupOfNotesRenderer)groupRendition;
					for (int j=0; j<g.getScoreElements().length; j++)
						m_scoreElements.put(g.getScoreElements()[j], g.getRenditionElements()[j]);
				}
					
						
					
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
						if (note.isBeginingSlur() || note.isBeginningTie())
							m_beginningNotesLinkElements.addElement(note);
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
								JScoreElement groupRendition= renderNotesInGroup(cursor, lessThanQuarter);
								currentStaffLine.addElement(groupRendition);
								if (groupRendition instanceof SNote)
									m_scoreElements.put(((SNote)groupRendition).getNote(), groupRendition);
								else {
									GroupOfNotesRenderer g = (GroupOfNotesRenderer)groupRendition;
									for (int j=0; j<g.getScoreElements().length; j++)
										m_scoreElements.put(g.getScoreElements()[j], g.getRenditionElements()[j]);
								}
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
							m_scoreElements.put(note, noteR);
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
		
		/*m_dimension.setSize(componentWidth+m_metrics.getStaffCharBounds().getWidth(), 
				componentHeight+m_metrics.getStaffCharBounds().getHeight());*/
		
		m_width = componentWidth+m_metrics.getStaffCharBounds().getWidth();
		m_height = componentHeight+m_metrics.getStaffCharBounds().getHeight();
		//setPreferredSize(m_dimension);
		//setSize(m_dimension);
		
		if (m_isJustified)
			justify();
		
		//m_isBufferedImageOutdated=true;
		//repaint();
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
				
		StaffLine currentStaffLine = null;
		for (int i=0; i<m_staffLines.size(); i++) {
			currentStaffLine = (StaffLine)m_staffLines.elementAt(i);
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
	
	protected void drawLinkDown(Graphics2D g2, TwoNotesLink slurDef) {
		SNote elmtStart =  (SNote)m_scoreElements.get(slurDef.getStart());
		if (slurDef.getEnd()!=null){
			SNote elmtEnd =  (SNote)m_scoreElements.get(slurDef.getEnd());
			Point2D controlPoint = null;
			
			Note lowestNote = m_tune.getScore().getLowestNoteBewteen(slurDef.getStart(), slurDef.getEnd());
			if (lowestNote.equals(slurDef.getStart()))
				controlPoint = new Point2D.Double(
						//TODO nullpointer occurs here sometimes...
						elmtStart.getSlurDownAnchor().getX()+ (elmtEnd.getSlurDownAnchor().getX()-elmtStart.getSlurDownAnchor().getX())/2,
						elmtStart.getSlurDownAnchor().getY()+ m_metrics.getNoteWidth()/4);
			else
				if (lowestNote.equals(slurDef.getEnd()))
					controlPoint = new Point2D.Double(
							//TODO nullpointer occurs here sometimes...
							elmtStart.getSlurDownAnchor().getX()+ (elmtEnd.getSlurDownAnchor().getX()-elmtStart.getSlurDownAnchor().getX())/2,
							elmtEnd.getSlurDownAnchor().getY()+ m_metrics.getNoteWidth()/4);
			
			else {
				SNote lowestNoteGlyph = (SNote)m_scoreElements.get(lowestNote);
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
	}
	
	private JScoreElement renderNotesInGroup(Point2D cursor, ArrayList lessThanQuarter){
		JScoreElement renditionResult = null;
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
		cursor.setLocation(0, cursor.getY() + m_staffLinesOffset);
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

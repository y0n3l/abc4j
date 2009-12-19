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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
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
import abc.notation.SlurDefinition;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.Tuplet;
import abc.notation.TwoNotesLink;
import abc.notation.Tune.Music;
import abc.notation.Words;

/**
 * This class role is to render properly a tune using Java 2D.
 * @see #setTune(Tune)
 */
class JTune extends JScoreElementAbstract {

	private static int DEBUG = 0;
	
	private final static short START = 0;
	private final static short CONTROL = 1;
	private final static short END = 2;

	/** The tune to be displayed. */
	private Tune m_tune = null;
	/**
	 * Hashmap that associates <DEL>ScoreElement</DEL> <B>MusicElement</B> instances (key) and JScoreElement instances(value).
	 * It contains : JChord, JNote, JNotePartOfGroup, JChordNote,
	 * JGraceNote, JGraceNotePartOfGroup instances.
	 */
	private Hashtable m_scoreElements = null;
	private Vector m_scoreNoteGroups = null;


	/** Note instances starting Slurs and ties. */
	private Vector m_beginningNotesLinkElements = null;

	/** WTF ??? does not seem to be really taken into account anyway... */
	private int MARGIN_LEFT = 0;
	/** Top margin */
	private int MARGIN_TOP = 0;
	/** The staff lines drawings. */
	private Vector m_staffLines = null;
	/** <TT>true</TT> if the rendition of the score should be justified,
	 * <TT>false</TT> otherwise. */
	private boolean m_isJustified = false;

	private double m_height = -1;
	private double m_width = -1;

	private Engraver m_engraver = null;

	// titles and subtitles
	// FIXME: m_showTitles shouldn't be static .... not sure why it doesn't work otherwise!!
	static protected boolean m_showTitles = true;
	protected ArrayList m_titles = null;
	protected JText m_composer = null;
	protected JText m_subtitles = null;

	//temporary variables used only to cumpute the score when tune is set.
	private boolean currentStaffLineInitialized = false;
	private JStaffLine currentStaffLine = null;
	private KeySignature previousKey = null;
	private KeySignature currentKey = null;
	private TimeSignature previousTime = null;
	private TimeSignature currentTime = null;
	private Point2D cursor = null;

	public JTune(Tune tune, Point2D base, ScoreMetrics c) {
		this(tune, base, c, false);
	}

	public JTune(Tune tune, Point2D base, ScoreMetrics c, boolean isJustified) {
		this(tune, base, c, new Engraver(Engraver.NONE), isJustified);
	}

	public JTune(Tune tune, Point2D base, ScoreMetrics c,
			Engraver e, boolean isJustified) {
		super(c);
		MARGIN_LEFT = (int)base.getX();
		MARGIN_TOP = (int)base.getY();
		m_titles = new ArrayList();
		m_staffLines = new Vector();
		m_isJustified = isJustified;
		m_scoreElements = new Hashtable();
		m_scoreNoteGroups = new Vector();
		m_beginningNotesLinkElements = new Vector();
		e.adaptToTune(tune, c);
		m_engraver = e;
		setTune(tune);
		setBase(base);
	}

	public void setMarginLeft(int margin) {
		MARGIN_LEFT = margin;
	}

	public void setMarginTop(int margin) {
		MARGIN_TOP = margin;
	}

	public void displayTitles(boolean show) {
		m_showTitles = show;
	}

	public void onBaseChanged() {

	}

	public MusicElement getMusicElement() {
		return null;
	}

	public double getHeight() {
		return m_height; //suppose it has been calculated
	}
	public double getWidth() {
		return m_width; //suppose it has been calculated
	}

	/*public void setStaffLinesSpacing(int staffLinesSpacing) {
		m_staffLinesSpacing = staffLinesSpacing;
		setTune(m_tune);
	}*/

	/*public int getStaffLinesSpacing() {
		return m_staffLinesSpacing;
	}*/

	public Tune getTune() {
		return m_tune;
	}

	/** Returns the hashtable that maps pure music objects to their corresponding
	 * rendition objects.
	 * * @return Returns the hashtable that maps pure music objects (<DEL>ScoreElement</DEL> <B>MusicElement</B> instances
	 * from abc.notation.* package) to rendition objects (JScoreElement instances from
	 * abc.ui.swing.score.* package) */
	public Hashtable getRenditionObjectsMapping() {
		return m_scoreElements;
	}
	
	/**
	 * Returns the rendition object (JNote, JChord...) for
	 * the given notation (Note, MultiNote)
	 * @param note
	 * @return
	 */
	public JScoreElement getRenditionObjectFor(MusicElement musicElement) {
		JScoreElement ret = (JScoreElement) m_scoreElements.get(musicElement);
		//If it's a Note, look for chords which may contain this note
		if (musicElement instanceof Note) {
			Note note = (Note) musicElement;
			Enumeration e = m_scoreElements.keys();
			while (e.hasMoreElements()) {
				MusicElement m = (MusicElement) e.nextElement();
				if ((m instanceof MultiNote)
						&& (((MultiNote) m).contains(note))) {
					JChord chord = (JChord) m_scoreElements.get(m);
					if (chord.m_normalizedChords == null) {
						JNote[] jnotes = chord.getScoreElements();
						for (int i = 0; i < jnotes.length; i++) {
							if (jnotes[i].getMusicElement().equals(note))
								return (jnotes[i]);
						}
					}
				}
			}
		}
		return ret;
	}

	/** Returns a vector of score elements that represent goups of notes.
	 * rendition objects.
	 * @return Returns a vector of JGroupOfNotes instances appearing in this score
	 */
	public Vector getNoteGroups() {
		return m_scoreNoteGroups;
	}


	private Engraver getEngraver() {
		if (m_engraver == null)
			m_engraver = new Engraver();
		return m_engraver;
	}

	/** Returns the part of the score (as a JScoreElement instance) located
	 * at a given point.
	 * @param location A location as a point.
	 * @return The part of the score (as a JScoreElement instance) located
	 * at a given point. <TT>NULL</NULL> is returned if not JScoreElement is
	 * matching the location.
	 */
	public JScoreElement getScoreElementAt(Point location) {
		JScoreElement scoreEl = null;
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
	public void setTune(Tune tune) {
		//The spacing between two staff lines
		//m_staffLinesSpacing = (int)(m_metrics.getStaffCharBounds().getHeight()*2.5);
		cursor = new Point(MARGIN_LEFT, MARGIN_TOP);
		double componentWidth = 0, componentHeight = 0;
		
		System.out.println("Passage #"+(++DEBUG));

		m_tune = tune;
		m_scoreElements.clear();
        m_scoreNoteGroups.clear();
        m_staffLines.removeAllElements();
		m_beginningNotesLinkElements.clear();
		
		currentKey = null;
		previousKey = null;


		// set headings, eg. titles, subtitles, composer, etc.
		// TODO: align center, left and right as appropriate
		m_titles.clear();
		m_subtitles = null;
		m_composer = null;

		if (m_showTitles) {

			JText text = null;
			String [] titles = tune.getTitles();
			double textHeight = 0;
			double textWidth = 0;
			double y = 0;

			for (int i=0; i< titles.length; i++) {
				if (titles[i].length() == 0)
					continue;
				if (i == 0) {
					//title
					text = new JTitle(getMetrics(), titles[i]);
				} else {
					// subtitles
					text = new JSubtitle(getMetrics(), titles[i]);
				}

				// should be center aligned .... but cannot align without ability to
				//  get JScoreComponent width
				textHeight = text.getHeight();
				textWidth = text.getWidth();
				y = cursor.getY();
				y+= textHeight;
				cursor.setLocation(MARGIN_LEFT, y);
				m_titles.add(text);
				text.setBase(cursor);
			}

			String txt = null;
			// should be left aligned .... but cannot align without ability to
			//  get JScoreComponent width
			txt = tune.getRhythm();
			if ((txt != null) && (txt.length() > 0)) {
				m_subtitles = new JSubtitle(getMetrics(), txt);
				textHeight = m_subtitles.getHeight();
				textWidth = m_subtitles.getWidth();
				y = cursor.getY();
				y+= textHeight;
				cursor.setLocation(MARGIN_LEFT, y);
				m_subtitles.setBase(cursor);
			}

			// should be right aligned .... but cannot align without ability to
			//  get JScoreComponent width
			txt = tune.getComposer();
			if ((txt != null) && (txt.length() > 0)) {
				m_composer = new JSubtitle(getMetrics(), txt);
				textHeight = m_composer.getHeight();
				textWidth = m_composer.getWidth();
				y = cursor.getY();
				y+= textHeight;
				cursor.setLocation(MARGIN_LEFT, y);
				m_composer.setBase(cursor);
			}

		}


		Music score = tune.getMusic();

		ArrayList lessThanQuarter = new ArrayList();
		//int durationInGroup = 0;
		//int maxDurationInGroup = Note.QUARTER;
		//int durationInCurrentMeasure = 0;
		Tuplet tupletContainer = null;
		int staffLineNb = 0;
		//init attributes that are for iterating through the score of the tune.
		currentKey = tune.getKey();
		if (currentKey != null)
			previousKey = (KeySignature) currentKey.clone();
		else
			previousKey = null;
		previousTime = null;
		currentTime = null;
		currentStaffLineInitialized = false;
		currentStaffLine = null;
		for (int i=0; i<score.size(); i++) {
			MusicElement s = (MusicElement)score.elementAt(i);
			// ==== Notes>quarter, rests, notes without slur,tuplet ====
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
			// ==== Key signature ====
			if (s instanceof KeySignature) {
				currentKey = (KeySignature)s;
				//TODO if end of staffline, add the key at right
				//here or in initStaffLine?
				if (currentStaffLineInitialized) {
					appendToScore(new JKeySignature(currentKey, previousKey, cursor, getMetrics()));
					previousKey = (KeySignature) currentKey.clone();
				}
			} else
			// ==== Time signature ====
			if (s instanceof TimeSignature) {
				if (currentTime != null)
					previousTime = (TimeSignature) currentTime.clone();
				currentTime = (TimeSignature)s;
				if (previousTime != null)
					if (!currentTime.equals(previousTime))
						appendToScore(new JTimeSignature(currentTime, cursor, getMetrics()));
			}
			else
			// ==== MultiNote ====
			if (s instanceof MultiNote) {
				NoteAbstract note = (NoteAbstract) s;
				if (note.isBeginingSlur())
					m_beginningNotesLinkElements.addElement(note);
				tupletContainer = ((MultiNote)s).getTuplet();
				Note[] tiesStart = ((MultiNote)s).getNotesBeginningTie();
				if (tiesStart!=null)
					for (int j=0; j<tiesStart.length; j++)
						m_beginningNotesLinkElements.addElement(tiesStart[j]);
				//checks if the shortest durations of the multi note is less than a quarter note.
				// if yes, this multi note will be put into a group.
				if (((MultiNote)s).getStrictDurations()[0]<Note.QUARTER)
					lessThanQuarter.add(s);
				else {
					appendToScore(new JChord((MultiNote)s, getMetrics(), cursor));
				}

				//durationInCurrentMeasure+=((MultiNote)s).getLongestNote().getDuration();
			} //end MultiNote
			else
			// ==== Note ====
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
					JNote noteR = new JNote(note, cursor, getMetrics());
					//if (note.getHeight()>=Note.c)
					//	noteR.setStemUp(false);
					appendToScore(noteR);
				}
				//durationInCurrentMeasure+=note.getDuration();
			} //end Note
			else
			// ==== RepeatBarLine ===
			if (s instanceof RepeatBarLine) {
				appendToScore(new JRepeatBar((RepeatBarLine)s, cursor, getMetrics()));
				//durationInCurrentMeasure=0;
			}
			else
			// ==== BarLine ====
			if (s instanceof BarLine) {
				appendToScore(new JBar((BarLine)s, cursor, getMetrics()));
				//durationInCurrentMeasure=0;
			}
			else
			// ==== EndOfStaffLine ====
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
			// ==== NotesSeparator ====
			if (s instanceof NotesSeparator) {
				appendToScore(lessThanQuarter);
				lessThanQuarter.clear();
			}
			else
			// ==== Words ====
			if (s instanceof Words) {
				appendLyrics(new JWords(getMetrics(), (Words)s));
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

		m_width = componentWidth+getMetrics().getStaffCharBounds().getWidth();
		m_height = componentHeight+getMetrics().getStaffCharBounds().getHeight();

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
		int cursorNewLocationX = (int)(cursor.getX() + width);
		
		//fixed space + variable space (engraver)
		if ((element instanceof JGraceNote)
				|| (element instanceof JGroupOfGraceNotes)
				|| (element instanceof JGraceNotePartOfGroup)) {
			cursorNewLocationX += getMetrics().getGraceNotesSpacing()
				+ getEngraver().getNoteSpacing(element);
		} else {
			cursorNewLocationX += getMetrics().getNotesSpacing()
				+ getEngraver().getNoteSpacing(element);
		}

		cursor.setLocation(cursorNewLocationX, cursor.getY());

		if (element instanceof JNote)
			m_scoreElements.put(((JScoreElementAbstract)element).getMusicElement(), element);
		else
		if (element instanceof JGroupOfNotes) {
			JGroupOfNotes g = (JGroupOfNotes)element;
			m_scoreNoteGroups.add(g);

			for (int j=0; j<g.getRenditionElements().length; j++) {
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
		}
		else
		if (element instanceof JChord) {
			JNote[] jnotes = ((JChord)element).getScoreElements();
			//Vector notes = ((MultiNote)((JChord)element).getMusicElement()).getNotes();
			//adds all the notes of the chords into the hashtable
			//TODO the ordering of the get notes as vector and the jnotes should be the same...
			//System.out.println("Warning - abc4j - current limitation prevents you from using chords with different notes lengths.");
			for (int i=0; i<jnotes.length; i++)
				m_scoreElements.put(jnotes[i].getMusicElement(), jnotes[i]);
			//adds also the chords itself
			m_scoreElements.put(element.getMusicElement(), element);
		}
	}

	private void appendToScore(ArrayList lessThanQuarterGroup){
		if (lessThanQuarterGroup.size()>0) {
			JScoreElementAbstract renditionResult = null;
			JScoreElementAbstract[] renditionResultRootsElmts = new JScoreElementAbstract[lessThanQuarterGroup.size()];
			NoteAbstract[] notes = (NoteAbstract[])lessThanQuarterGroup.toArray(new NoteAbstract[lessThanQuarterGroup.size()]);
			if (notes.length==1) {
				if (notes[0] instanceof Note) {
					renditionResult = new JNote((Note)notes[0], cursor, getMetrics());
					renditionResultRootsElmts[0] = renditionResult;
				}
				else {
					//This is a multi note
					renditionResult = new JChord((MultiNote)notes[0], getMetrics(), cursor);
					renditionResultRootsElmts[0] = renditionResult;
				}
			}
			else {
				renditionResult = new JGroupOfNotes(
						getMetrics(), cursor, notes, getEngraver());
				//if tuplet, add to m_beginningNotesLinkElements
				if (notes[0].getTuplet()!=null)
					m_beginningNotesLinkElements.add(notes[0]);
				renditionResultRootsElmts = ((JGroupOfNotes)renditionResult).getRenditionElements();
			}
			appendToScore(renditionResult);
		}
	}

	private void appendLyrics(JWords lyrics) {
		double y = cursor.getY();
		int adjustment = (int)lyrics.getHeight();
		if (!currentStaffLine.hasLyrics()) {
			// add a 2 line gap under staff line for lyrics
			adjustment *= 2;
		}
		y += adjustment;
		// lyrics always occur under a staff line, so it should be safe
		// to reset base to X=0, Y=(current Y + height of char in lyrics font)
		cursor.setLocation(MARGIN_LEFT, y);

		currentStaffLine.addLyrics(lyrics);
//		lyrics.setStaffLine(currentStaffLine);
		lyrics.setBase(cursor);
	}

	public double render(Graphics2D g2) {
		g2.setFont(getMetrics().getNotationFont());
		g2.setColor(Color.BLACK);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    renderTitles(g2);
		// staff line width
		int staffCharNb = (int)(getWidth()/getMetrics().getStaffCharBounds().getWidth());
		char[] staffS = new char[staffCharNb+1];
		for (int i=0; i<staffS.length; i++)
			staffS[i] = ScoreMetrics.STAFF_SIX_LINES;

		JStaffLine currentStaffLine = null;
		for (int i=0; i<m_staffLines.size(); i++) {
			currentStaffLine = (JStaffLine)m_staffLines.elementAt(i);
			currentStaffLine.render(g2);
			g2.drawChars(staffS, 0, staffS.length, MARGIN_LEFT, (int)(currentStaffLine.getBase().getY()));
		}
		renderSlursAndTies(g2);

		return getWidth();
	}

	protected void renderTitles(Graphics2D g2) {
		Iterator iter = m_titles.iterator();
		while (iter.hasNext()) {
			((JText)iter.next()).render(g2);
		}
		if (m_subtitles != null)
			((JText)m_subtitles).render(g2);
		if (m_composer != null)
			((JText)m_composer).render(g2);
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
		//System.err.println("renderSlursAndTies");
		for (int j=0; j<m_beginningNotesLinkElements.size(); j++) {
			NoteAbstract n = (NoteAbstract)m_beginningNotesLinkElements.elementAt(j);
			TwoNotesLink link = n.getTieDefinition();
			if (link != null && link.getEnd() != null) {
				drawLink(g2, link);
			}
			Vector slurs = n.getSlurDefinitions();
			int i = 0;
			while (i < slurs.size()) {
				link = (SlurDefinition) slurs.elementAt(i);
				if (link != null && link.getEnd() != null) {
					drawLink(g2, link);
				}
				i++;
			}
		}
	}

	/**
	 * Draw a link between 2 notes
	 *
	 * @see #getLinkPoints(TwoNotesLink) calculates the best link curve
	 * @param g2
	 * @param slurDef
	 */
	protected void drawLink(Graphics2D g2, TwoNotesLink slurDef) {
		Point2D[] points = getLinkPoints(slurDef);
		if (points.length != 3)
			return;

		//TODO move this in JSlurOrTie.render(g2);
		JSlurOrTie jSlurDef = getJSlurOrTie(slurDef);
		if (jSlurDef.isTuplet()) {
			//we consider tuplet are always above notes
			if (jSlurDef.getTupletControlPoint() == null) {
				jSlurDef.setTupletControlPoint(new Point2D.Double(
					points[START].getX()+(points[END].getX()-points[START].getX())/2,
					points[START].getY()-getMetrics().getSlurAnchorYOffset()*5
				));
			}
			points[CONTROL].setLocation(
				jSlurDef.getTupletControlPoint()
			);
		}
		
		System.out.println("drawLink "+slurDef);
		System.out.println("  - start = "+points[START]);
		System.out.println("  - end = "+points[END]);

		Color previousColor = g2.getColor();
		boolean isAboveNotes = false;
		if (points[CONTROL].getY() < points[START].getY()
			&& (points[CONTROL].getY() < points[END].getY()))
			isAboveNotes = true;
		else if (points[CONTROL].getY() > points[START].getY()
				&& (points[CONTROL].getY() > points[END].getY()))
			isAboveNotes = false;
		else {//problem, mark it red
			g2.setColor(Color.RED);
		}

		GeneralPath path = new GeneralPath();
		path.moveTo((float)points[START].getX(), (float)points[START].getY());
		QuadCurve2D q = new QuadCurve2D.Float();
		q.setCurve(
				points[START],
				newControl(points[START], points[CONTROL], points[END]),
				points[END]);
		path.append(q, true);
		q = new QuadCurve2D.Float();
		if (!isAboveNotes) {
			points[CONTROL].setLocation(points[CONTROL].getX(),
				points[CONTROL].getY()+getMetrics().getSlurThickness());
		} else {
			points[CONTROL].setLocation(points[CONTROL].getX(),
					points[CONTROL].getY()-getMetrics().getSlurThickness());
		}
		q.setCurve(
				points[END],
				newControl(points[START], points[CONTROL], points[END]),
				points[START]);
		path.append(q, true);
		g2.fill(path);
		g2.draw(path);

		g2.setColor(previousColor);
	}

	private JSlurOrTie getJSlurOrTie(TwoNotesLink slurDef) {
		JNoteElementAbstract elmtStart;
		if (slurDef.getStart() instanceof Note) {
			elmtStart = (JNote)m_scoreElements.get(slurDef.getStart());
		} else {
			//instanceof MultiNote
			elmtStart = (JChord)m_scoreElements.get(slurDef.getStart());
		}
		return elmtStart.getJSlurDefinition();
	}

	//FIXME : well, slurs and ties shouldn't be drawn the same way :
	// slurs, should be drawn under/upper all the notes that are part of the slur
	// ties, should consider notes between the 2 notes tied.
	/**
	 * Returns the 3 points of a slur
	 * <br>Method used by {@link #drawLink(Graphics2D, TwoNotesLink)}
	 * @param slurDef
	 * @return [0]=>start, [1]=control, [2]=end
	 */
	private Point2D[] getLinkPoints(TwoNotesLink slurDef) {
		if (slurDef.getEnd()==null){
			return new Point2D[] {};
		}
		JNoteElementAbstract elmtStart, elmtEnd;
		//JNote elmtStartLow, elmtStartHigh, elmtEndLow, elmtEndHigh;
		elmtStart = (JNoteElementAbstract) m_scoreElements.get(slurDef.getStart());
		elmtEnd = (JNoteElementAbstract) m_scoreElements.get(slurDef.getEnd());
		//elmtStart = (JNoteElementAbstract) getRenditionObjectFor(slurDef.getStart());
		//elmtEnd = (JNoteElementAbstract) getRenditionObjectFor(slurDef.getEnd());
		/*if (slurDef.getStart() instanceof MultiNote) {
			elmtStart = (JChord)m_scoreElements.get(slurDef.getStart());
			elmtStartLow = ((JChord)elmtStart).getLowestNote();
			elmtStartHigh = ((JChord)elmtStart).getHighestNote();
		} else {
			//instanceof Note
			elmtStart = (JNote)m_scoreElements.get(slurDef.getStart());
			elmtStartLow = elmtStartHigh = (JNote) elmtStart;
		}
		if (slurDef.getEnd() instanceof MultiNote) {
			elmtEnd = (JChord)m_scoreElements.get(slurDef.getEnd());
			elmtEndLow = ((JChord)elmtEnd).getLowestNote();
			elmtEndHigh = ((JChord)elmtEnd).getHighestNote();
		} else {
			//instanceof Note
			elmtEnd = (JNote)m_scoreElements.get(slurDef.getEnd());
			elmtEndLow = elmtEndHigh = (JNote) elmtEnd;
		}*/
		
		if (!elmtStart.getStaffLine().equals(elmtEnd.getStaffLine())) {
			System.err.println("Warning - abc4j limitation : Slurs / ties cannot be drawn accross several lines for now.");
			return new Point2D[] {};
		}
		final short UNDER_IN = 0; //the only existing in old abc4j
		final short UNDER_OUT = 1; //down and out of stems
		final short ABOVE_IN = 2; //up and can go across the stems
		final short ABOVE_OUT = 3; //up and out of stems (e.g. for tuplet)
		Point2D[][] p = new Point2D[4][3];

		p[UNDER_IN][START] = elmtStart.getSlurUnderAnchor();
		p[UNDER_OUT][START] = elmtStart.getSlurUnderAnchorOutOfStem();
		p[ABOVE_IN][START] = elmtStart.getSlurAboveAnchor();
		p[ABOVE_OUT][START] = elmtStart.getSlurAboveAnchorOutOfStem();
		p[UNDER_IN][END] = elmtEnd.getSlurUnderAnchor();
		p[UNDER_OUT][END] = elmtEnd.getSlurUnderAnchorOutOfStem();
		p[ABOVE_IN][END] = elmtEnd.getSlurAboveAnchor();
		p[ABOVE_OUT][END] = elmtEnd.getSlurAboveAnchorOutOfStem();
/*
		p[UNDER_IN][START] = elmtStartLow.getSlurUnderAnchor();
		p[UNDER_OUT][START] = elmtStartLow.getSlurUnderAnchorOutOfStem();
		p[ABOVE_IN][START] = elmtStartHigh.getSlurAboveAnchor();
		p[ABOVE_OUT][START] = elmtStartHigh.getSlurAboveAnchorOutOfStem();
		p[UNDER_IN][END] = elmtEndLow.getSlurUnderAnchor();
		p[UNDER_OUT][END] = elmtEndLow.getSlurUnderAnchorOutOfStem();
		p[ABOVE_IN][END] = elmtEndHigh.getSlurAboveAnchor();
		p[ABOVE_OUT][END] = elmtEndHigh.getSlurAboveAnchorOutOfStem();
*/
		//
		//determinate peaks (lowest/highest) note and note glyph
		// FIXME: ties should enclose note decorations
		//
		NoteAbstract[] peakNote = new NoteAbstract[4];
		JNoteElementAbstract[] peakNoteGlyph = new JNoteElementAbstract[4];

		//under in
		peakNote[UNDER_IN] = m_tune.getMusic().getLowestNoteBewteen(slurDef.getStart(), slurDef.getEnd());
		peakNoteGlyph[UNDER_IN] = (JNoteElementAbstract)m_scoreElements.get(peakNote[UNDER_IN]);

		//under out of stems
		peakNoteGlyph[UNDER_OUT] = getLowestNoteGlyphBetween(slurDef.getStart(), slurDef.getEnd(), true);
		//enhance: if lowest glyph strictly between start/end is at same Y than start or end
		if (peakNoteGlyph[UNDER_OUT] == null) { //no notes between start and end
			peakNoteGlyph[UNDER_OUT] = p[UNDER_OUT][START].getY()>p[UNDER_OUT][END].getY()
				?elmtStart:elmtEnd; //Low
		} else {
			Point2D lowAnchor = peakNoteGlyph[UNDER_OUT].getSlurUnderAnchorOutOfStem();
			if (lowAnchor.getY() < p[UNDER_OUT][START].getY())
				peakNoteGlyph[UNDER_OUT] = elmtStart;//Low;
			if (lowAnchor.getY() < p[UNDER_OUT][END].getY())
				peakNoteGlyph[UNDER_OUT] = elmtEnd;//Low;
		}
		peakNote[UNDER_OUT] = (NoteAbstract) peakNoteGlyph[UNDER_OUT].getMusicElement();

		//above in
		peakNote[ABOVE_IN] = m_tune.getMusic().getHighestNoteBewteen(slurDef.getStart(), slurDef.getEnd());
		peakNoteGlyph[ABOVE_IN] = (JNoteElementAbstract)m_scoreElements.get(peakNote[ABOVE_IN]);

		//above out of stems (e.g. truplet)
		peakNoteGlyph[ABOVE_OUT] = getHighestNoteGlyphBetween(slurDef.getStart(), slurDef.getEnd(), true);
		//enhance: if lowest glyph strictly between start/end is at same Y than start or end
		if (peakNoteGlyph[ABOVE_OUT] == null) { //no notes between start and end
			peakNoteGlyph[ABOVE_OUT] = p[ABOVE_OUT][START].getY()<p[ABOVE_OUT][END].getY()
				?elmtStart:elmtEnd;//High
		} else {
			Point2D highAnchor = peakNoteGlyph[ABOVE_OUT].getSlurAboveAnchorOutOfStem();
			if (highAnchor.getY() > p[ABOVE_OUT][START].getY())
				peakNoteGlyph[ABOVE_OUT] = elmtStart;//High
			if (highAnchor.getY() > p[ABOVE_OUT][END].getY())
				peakNoteGlyph[ABOVE_OUT] = elmtEnd;//High
		}
		peakNote[ABOVE_OUT] = (NoteAbstract) peakNoteGlyph[ABOVE_OUT].getMusicElement();

		//
		//if peak=start then control=start
		//if peak=end then control=end
		//else control=peak
		//
		for (short i = 0; i < 4; i++) {
			int factor = (i<=UNDER_OUT)?(1):(-1);
			if (peakNote[i].equals(slurDef.getStart())) {
				p[i][CONTROL] = new Point2D.Double(
					p[i][START].getX() + (p[i][END].getX()-p[i][START].getX())/2,
					p[i][START].getY() + factor*getMetrics().getSlurAnchorYOffset()
				);
			}
			else if (peakNote[i].equals(slurDef.getEnd())) {
				p[i][CONTROL] = new Point2D.Double(
					p[i][START].getX() + (p[i][END].getX()-p[i][START].getX())/2,
					p[i][END].getY() + factor*getMetrics().getSlurAnchorYOffset()
				);
			}
			else { //control=peak
				switch(i) {
				case UNDER_IN: p[i][CONTROL]=peakNoteGlyph[UNDER_IN].getSlurUnderAnchor();break;
				case UNDER_OUT: p[i][CONTROL]=peakNoteGlyph[UNDER_OUT].getSlurUnderAnchorOutOfStem();break;
				case ABOVE_IN: p[i][CONTROL]=peakNoteGlyph[ABOVE_IN].getSlurAboveAnchor();break;
				case ABOVE_OUT: p[i][CONTROL]=peakNoteGlyph[ABOVE_OUT].getSlurAboveAnchorOutOfStem();break;
				}
			}

			//we don't like straight slurs, if Ystart=Ycontrol=Yend, Ycontrol+=slurOffset
			//FIXME if tie (not slur), we can keep it straight
			double Ycontrol = p[i][CONTROL].getY(),
					Ystart = p[i][START].getY(),
					Yend = p[i][END].getY();
			if ((Ycontrol == Ystart) || (Ycontrol == Yend)) {
				double Xoffset = 0;
				if (Ycontrol != Ystart) Xoffset = -getMetrics().getNoteWidth()/2;
				else if (Ycontrol != Yend) Xoffset = getMetrics().getNoteWidth()/2;
				p[i][CONTROL] = new Point2D.Double(
					p[i][CONTROL].getX() + Xoffset,
					p[i][CONTROL].getY() + factor*getMetrics().getSlurAnchorYOffset()*2);
			}
		}

		//
		//Now the funny part, determinate which curve is the best
		//
		//Point2D[] ret = null;
		Vector curves = new Vector(); //Combinaison to compare
		JSlurOrTie jSlurDef = getJSlurOrTie(slurDef);
		if (!jSlurDef.isOutOfStems()) { //we can add all curves
			Vector underCurves = new Vector(), aboveCurves = new Vector();
			if (!jSlurDef.isAbove()) { //down or auto
				curves.add(p[UNDER_IN]);
				underCurves.add(new Point2D[] {p[UNDER_IN][START],p[UNDER_IN][CONTROL],p[UNDER_OUT][END]});
				underCurves.add(new Point2D[] {p[UNDER_IN][START],p[UNDER_OUT][CONTROL],p[UNDER_IN][END]});
				underCurves.add(new Point2D[] {p[UNDER_IN][START],p[UNDER_OUT][CONTROL],p[UNDER_OUT][END]});
				underCurves.add(new Point2D[] {p[UNDER_OUT][START],p[UNDER_IN][CONTROL],p[UNDER_IN][END]});
				underCurves.add(new Point2D[] {p[UNDER_OUT][START],p[UNDER_IN][CONTROL],p[UNDER_OUT][END]});
				underCurves.add(new Point2D[] {p[UNDER_OUT][START],p[UNDER_OUT][CONTROL],p[UNDER_IN][END]});
			}
			if (!jSlurDef.isUnder()) { //up or auto
				curves.add(p[ABOVE_IN]);
				aboveCurves.add(new Point2D[] {p[ABOVE_IN][START],p[ABOVE_IN][CONTROL],p[ABOVE_OUT][END]});
				aboveCurves.add(new Point2D[] {p[ABOVE_IN][START],p[ABOVE_OUT][CONTROL],p[ABOVE_IN][END]});
				aboveCurves.add(new Point2D[] {p[ABOVE_IN][START],p[ABOVE_OUT][CONTROL],p[ABOVE_OUT][END]});
				aboveCurves.add(new Point2D[] {p[ABOVE_OUT][START],p[ABOVE_IN][CONTROL],p[ABOVE_IN][END]});
				aboveCurves.add(new Point2D[] {p[ABOVE_OUT][START],p[ABOVE_IN][CONTROL],p[ABOVE_OUT][END]});
				aboveCurves.add(new Point2D[] {p[ABOVE_OUT][START],p[ABOVE_OUT][CONTROL],p[ABOVE_IN][END]});
			}
			//verify above and under curves must be above / under
			for (Iterator it = aboveCurves.iterator(); it.hasNext();) {
				Point2D[] p2d = (Point2D[]) it.next();
				if ((p2d[CONTROL].getY()<p2d[START].getY())
					&& (p2d[CONTROL].getY()<p2d[END].getY()))
					curves.add(p2d);
			}
			for (Iterator it = underCurves.iterator(); it.hasNext();) {
				Point2D[] p2d = (Point2D[]) it.next();
				if ((p2d[CONTROL].getY()>p2d[START].getY())
					&& (p2d[CONTROL].getY()>p2d[END].getY()))
					curves.add(p2d);
			}
		}
		//Out of stems
		if (!jSlurDef.isAbove()) //under or auto
			curves.add(p[UNDER_OUT]);
		if (!jSlurDef.isUnder()) //above or auto
			curves.add(p[ABOVE_OUT]);


		//
		//Correct controlPoint (center it) if the curve gets ugly
		//(something like a little nose lefter/righter than min/maxX
		//and compute a mark to determinate best curve
		//
		//vector for newly generated curves
		Vector additionnalCurves = new Vector();
		Point2D[] bestCurve = null;
		float bestMark = -99;
		//int cpt = 0; int bestCurveIdx = -1;
		for (Iterator itCurves = curves.iterator(); itCurves.hasNext();) {
			Point2D[] p2d = (Point2D[]) itCurves.next();
			//System.out.print((cpt++) + " : ");

			try {
				SlurInfos slurInfos = new SlurInfos(p2d,
					getNoteGlyphesBetween(slurDef.getStart(), slurDef.getEnd()),
					getMetrics());

				//Check results
				//If some intersections, try to draw a new curve
				//by moving the control point
				if (slurInfos.intersect > 0) {
					Point2D newPtControl;
					double yOffset = getMetrics().getNoteHeight();
					int factor = slurInfos.isAbove?-1:1;
					//move Y=1 note height
					//newPtControl = new Point2D.Double(p2d[CONTROL].getX(),
					//		p2d[CONTROL].getY()+factor*yOffset);
					//additionnalCombos.add(new Point2D[] {p2d[START],newPtControl,p2d[END]});
					//move Y=2 note height
					newPtControl = new Point2D.Double(p2d[CONTROL].getX(),
							p2d[CONTROL].getY()+2*factor*yOffset);
					additionnalCurves.add(new Point2D[] {p2d[START],newPtControl,p2d[END]});
				}

				//TODO if flatness is high, away start/end position from original
				//(distance allowed = nb notes*m_metrics.getNoteHeight)

				if (slurInfos.mark>bestMark || (bestCurve==null)) {
					bestMark = slurInfos.mark;
					bestCurve = p2d;
					//bestCurveIdx = cpt-1;
				}
			} catch (MalFormedCurveException mfce) {
				//not good curve, remove it
				if (curves.size() > 1) {
					itCurves.remove();
					continue;
				} else {
					break;
				}
			}
		}

		//Checks additional combos
		for (Iterator itAddCurves = additionnalCurves.iterator(); itAddCurves.hasNext();) {
			Point2D[] p2d = (Point2D[]) itAddCurves.next();
			//System.out.print((cpt++) + " : ");
			try {
				SlurInfos slurInfos = new SlurInfos(p2d,
					getNoteGlyphesBetween(slurDef.getStart(), slurDef.getEnd()),
					getMetrics());
				if (slurInfos.mark>bestMark || (bestCurve==null)) {
					bestMark = slurInfos.mark;
					bestCurve = p2d;
					//bestComboIdx = cpt-1;
				}
				curves.add(p2d);
			} catch (MalFormedCurveException mfce) {
				//nothing to do
			}
		}

		//System.out.println("curves.size()="+curves.size());
		//System.out.println("best curve n° "+bestCurveIdx+", mark="+bestMark);
		return bestCurve!=null
				? bestCurve
				: (curves.size()==0 ? new Point2D[] {} : (Point2D[]) curves.get(0));
	}

	/**
	 * Draw a link between 2 notes, forcing slur position to under
	 *
	 * @see #drawLink(Graphics2D, TwoNotesLink)
	 * @param g2
	 * @param slurDef
	 */
	protected void drawLinkDown(Graphics2D g2, TwoNotesLink slurDef) {
		getJSlurOrTie(slurDef).setPosition(JSlurOrTie.POSITION_UNDER);
	}

	/**
	 * Draw a link between 2 notes, forcing slur position to above
	 *
	 * @see #drawLink(Graphics2D, TwoNotesLink)
	 * @param g2
	 * @param slurDef
	 */
	protected void drawLinkUp(Graphics2D g2, TwoNotesLink slurDef) {
		getJSlurOrTie(slurDef).setPosition(JSlurOrTie.POSITION_ABOVE);
	}

	/**
	 * Return the JNote that have the highest bounding box
	 * @param start
	 * @param end
	 * @param excludeStartAndEnd
	 * @return a JNote. May return <code>null</code> if exclude is true and no notes between start and end!
	 */
	private JNoteElementAbstract getHighestNoteGlyphBetween(NoteAbstract start, NoteAbstract end, boolean excludeStartAndEnd) {
		Collection jnotes = getNoteGlyphesBetween(start, end);
		JNoteElementAbstract ret = null;
		boolean first = true;
		for (Iterator it = jnotes.iterator(); it.hasNext();) {
			JNoteElementAbstract n = (JNoteElementAbstract) it.next();
			if (first && excludeStartAndEnd) {
				//ignore start ?
				first = false;
				continue;
			}
			if (!it.hasNext() && excludeStartAndEnd) {
				//ignore end ?
				break;
			}
			if (ret == null)
				ret = n;
			else {
				if (n.getBoundingBox().getMinY()
					< ret.getBoundingBox().getMinY())
					ret = n;
			}
		}
		return ret;
	}

	/**
	 * Return the JNote that have the lowest bounding box
	 * @param start
	 * @param end
	 * @param excludeStartAndEnd
	 * @return a JNote. May return <code>null</code> if exclude is true and no notes between start and end!
	 */
	private JNoteElementAbstract getLowestNoteGlyphBetween(NoteAbstract start, NoteAbstract end, boolean excludeStartAndEnd) {
		Collection jnotes = getNoteGlyphesBetween(start, end);
		JNoteElementAbstract ret = null;
		boolean first = true;
		for (Iterator it = jnotes.iterator(); it.hasNext();) {
			JNoteElementAbstract n = (JNoteElementAbstract) it.next();
			if (first && excludeStartAndEnd) {
				//ignore start ?
				first = false;
				continue;
			}
			if (!it.hasNext() && excludeStartAndEnd) {
				//ignore end ?
				break;
			}
			if (ret == null)
				ret = n;
			else {
				if (n.getBoundingBox().getMaxY()
					> ret.getBoundingBox().getMaxY())
					ret = n;
			}
		}
		return ret;
	}

	/**
	 * Returns a Collection of JNote representing the note glyphes
	 * between start and end NoteAbstract.
	 * @param start
	 * @param end
	 * @return
	 */
	private Collection getNoteGlyphesBetween(NoteAbstract start, NoteAbstract end) {
		Collection jnotes = new Vector();
		try {
			Collection notes = m_tune.getMusic().getNotesBetween(start, end);
			for (Iterator it = notes.iterator(); it.hasNext();) {
				NoteAbstract n = (NoteAbstract) it.next();
				jnotes.add((JNoteElementAbstract) m_scoreElements.get(n));
			}
		} catch (Exception e) {
			// TODO: handle exception, shouldn't happen
		}
		return jnotes;
	}

	private JStaffLine initNewStaffLine() {
		JStaffLine sl = new JStaffLine(cursor, getMetrics());
		if (m_staffLines.size() == 0) {
			//first staff top margin
			cursor.setLocation(MARGIN_LEFT, cursor.getY()+getMetrics().getFirstStaffTopMargin());
		} else /*if (m_staffLines.size() > 0)*/ {
			//add a space between each lines
			cursor.setLocation(MARGIN_LEFT, cursor.getY() + getMetrics().getStaffLinesSpacing());
		}
		//add space if tune has chord
		Music music = m_tune.getMusic();
		if (music.hasChordNames())
			cursor.setLocation(MARGIN_LEFT, cursor.getY() + getMetrics().getChordLineSpacing());
		//TODO add space for lyrics, high and low notes
		
		cursor.setLocation(MARGIN_LEFT, cursor.getY()+getMetrics().getStaffCharBounds().getHeight());
		//Vector initElements = new Vector();
		JClef clef = new JClef(cursor, getMetrics());
		sl.addElement(clef);
		//initElements.addElement(clef);
		double width = clef.getWidth();
		cursor.setLocation(cursor.getX()+width, cursor.getY());
		if (currentKey!=null) {
			JKeySignature sk = new JKeySignature(currentKey, previousKey, cursor, getMetrics());
			previousKey = (KeySignature) currentKey.clone();
			sl.addElement(sk);
			//initElements.addElement(sk);
			width = sk.getWidth();
			int cursorNewLocationX = (int)(cursor.getX() + width + getMetrics().getNotesSpacing());
			cursor.setLocation(cursorNewLocationX, cursor.getY());
		}
		if (currentTime!=null && m_staffLines.size()==0) {
			try {
				JTimeSignature sig = new JTimeSignature(currentTime, cursor, getMetrics());
				sl.addElement(sig);
				//initElements.addElement(sig);
				width = (int)sig.getWidth();
				int cursorNewLocationX = (int)(cursor.getX() + width + getMetrics().getNotesSpacing());
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
	static protected Point2D newControl (Point2D start, Point2D ctrl, Point2D end) {
        Point2D.Double newCtrl = new Point2D.Double();
        newCtrl.x = 2 * ctrl.getX() - (start.getX() + end.getX()) / 2;
        newCtrl.y = 2 * ctrl.getY() - (start.getY() + end.getY()) / 2;
        return newCtrl;
    }

}

class MalFormedCurveException extends Exception {
	private static final long serialVersionUID = -7800529107437720053L;
}

/**
 * Calculates informations about a slur, and give it a mark
 * telling if slur is good or not.
 */
class SlurInfos {

	private final static short START = 0;
	private final static short CONTROL = 1;
	private final static short END = 2;

	/** Is the curve up or down? */
	boolean isAbove = false;
	/** Number of notes in the slur */
	float countNotes = 0;
	/** Number of up-stem glyphs in slur */
	float upNotes = 0;
	/** Number of down-stem glyphs in slur */
	float downNotes = 0;
	/** Number of stems and note intersected by the curve path */
	float intersect = 0;
	/** Is intersect important ? */
	//boolean countIntersect = true;
	/** Flatness of the curve */
	double flatness = 0;
	/** Y distance (in note height) between start curve and start note */
	double YfromStartHead = 0;
	/** Y distance (in note height) between end curve and end note */
	double YfromEndHead = 0;
	/** Mark */
	float mark = 0;

	/**
	 *
	 * @param threePoints The 3 points of the curve
	 * @param noteGlyphs The glyphs ({@link JNote}) in the slur
	 * @param m_metrics
	 */
	protected SlurInfos(Point2D[] p2d, Collection noteGlyphs,
		ScoreMetrics m_metrics)
		throws MalFormedCurveException {

		isAbove = false;
		if (p2d[CONTROL].getY() < p2d[START].getY()
			&& (p2d[CONTROL].getY() < p2d[END].getY()))
			isAbove = true;
		else if (p2d[CONTROL].getY() > p2d[START].getY()
				&& (p2d[CONTROL].getY() > p2d[END].getY()))
			isAbove = false;
		else {
			throw new MalFormedCurveException();
		}

		Point2D startSlurAnchorNearHead = p2d[START],
			endSlurAnchorNearHead = p2d[END];

		int divisor = 1;

		QuadCurve2D q = new QuadCurve2D.Float();
		q.setCurve(
				p2d[START],
				JTune.newControl(p2d[START], p2d[CONTROL], p2d[END]),
				p2d[END]);
		if ((q.getBounds2D().getMinX() < p2d[START].getX()
				|| (q.getBounds2D().getMaxX() > p2d[END].getX()))) {
			//redefine controlPoint at the center
			p2d[CONTROL] = new Point2D.Double(q.getBounds2D().getCenterX(),
						isAbove?q.getBounds2D().getMinY()
							:q.getBounds2D().getMaxY());
			q.setCurve(
					p2d[START],
					JTune.newControl(p2d[START], p2d[CONTROL], p2d[END]),
					p2d[END]);
			mark += -5; //A malus for that curve
		}

		//get the curve path (for intersection calculation)
		//good example at http://java.sun.com/products/java-media/2D/samples/suite/Arcs_Curves/Curves.java
		PathIterator pathIt = q.getPathIterator(null);
		FlatteningPathIterator f = new FlatteningPathIterator(pathIt,0.1);
		float[] oldPts = null;
		Collection curveSegments = new Vector();
		while ( !f.isDone() ) {
			float[] pts = new float[6];
			int segType = f.currentSegment(pts);
			if (segType == PathIterator.SEG_MOVETO
				|| segType == PathIterator.SEG_LINETO) {
				if (oldPts != null) {
					curveSegments.add(new Line2D.Float(
							oldPts[0], oldPts[1],
							pts[0], pts[1]));
				}
				oldPts = pts;
			}
			f.next();
		}

		//
		//compute the mark of this curve
		//
		boolean start = true;
		for (Iterator itGlyphs = noteGlyphs.iterator(); itGlyphs.hasNext();) {
			JNoteElementAbstract jnote = (JNoteElementAbstract) itGlyphs.next();
			NoteAbstract note = (NoteAbstract) jnote.getMusicElement();
			int duration = (note instanceof MultiNote)
				?((MultiNote) note).getShortestNote().getDuration()
				:((Note) note).getDuration();
			boolean isRest = (note instanceof Note) && ((Note) note).isRest();
			if ((duration < Note.WHOLE) && !isRest) {
				if (jnote.isStemUp()) upNotes++;
				else downNotes++;
			}
			//Determinate the points near note head for distance check
			if (start) {
				startSlurAnchorNearHead =
					isAbove?jnote.getSlurAboveAnchor()
						:jnote.getSlurUnderAnchor();
			} else if (!itGlyphs.hasNext()) {
				endSlurAnchorNearHead =
					isAbove?jnote.getSlurAboveAnchor()
						:jnote.getSlurUnderAnchor();
			} else {
			//if (!start && itGlyphs.hasNext()) {
				//exclude first and last note from intersect checks
				//if (q.intersects(jnote.getBoundingBox()))
				//--not good : it's the whole shape between straight line start-end and curve
				//  that's why we check only segments of the curve
				Rectangle2D bb = jnote.getBoundingBox();
				//resize boundingBox for checking, curve must not be to close than border of the box
				bb.add(bb.getCenterX(), bb.getMinY()-m_metrics.getSlurAnchorYOffset());
				bb.add(bb.getCenterX(), bb.getMaxY()+m_metrics.getSlurAnchorYOffset());
				for (Iterator itSeg = curveSegments.iterator(); itSeg.hasNext();) {
					Line2D segment = (Line2D) itSeg.next();
					if (segment.intersects(bb)) {
						intersect++;
						break;
					}
				}
			}
			start = false;
		}
		countNotes = upNotes + downNotes;

	//	countIntersect = true;
		//- opposite of stems (10 +%opposite -%same direction)
		divisor++;
		if (isAbove) {
			mark += (float)(-(upNotes/countNotes)*10 + (downNotes/countNotes)*10);
	//		if (downNotes == countNotes)
	//			countIntersect = false;
		}
		else {
			mark += (float)((upNotes/countNotes)*10 - (downNotes/countNotes)*10);
	//		if (upNotes == countNotes)
	//			countIntersect = false;
		}

		//- flatness as low as possible (20 pt - nb notes*flatness/10 ?)
		divisor++;
		flatness = q.getFlatness();
		mark += 20 - countNotes*(flatness/10);

		//- avoid stems (-1 pt per crossed stem except start and end)
		//if (countIntersect) {
		if (intersect > 0) {
			divisor++;
			mark -= countNotes*intersect; //(*4)
		}

		//- as close as possible as start/end position
		YfromStartHead = Math.abs(p2d[START].getY() - startSlurAnchorNearHead.getY())
			/ m_metrics.getNoteHeight();
		if (YfromStartHead > 1) {
			divisor++;
			mark -= YfromStartHead*2;
		}
		YfromEndHead = Math.abs(p2d[END].getY() - endSlurAnchorNearHead.getY())
			/ m_metrics.getNoteHeight();
		if (YfromEndHead > 1) {
			divisor++;
			mark -= YfromEndHead*2;
		}

		//- control as near as possible of lowest/highest note and staff

		mark = mark/divisor;

		/* * /System.out.println("mark="+mark+" - isAbove="+(isAbove?"true":"false")
				//+", countIntersect="+(countIntersect?"true":"false")
				+", intersect="+intersect);
				//+" pts="+p2d[START]+","+p2d[CONTROL]+","+p2d[END]);
		/**/
	}

}
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import abc.notation.BarLine;
import abc.notation.Clef;
import abc.notation.EndOfStaffLine;
import abc.notation.KeySignature;
import abc.notation.MeasureRepeat;
import abc.notation.MultiNote;
import abc.notation.Music;
import abc.notation.MusicElement;
import abc.notation.MusicElementReference;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.NotesSeparator;
import abc.notation.Part;
import abc.notation.PartLabel;
import abc.notation.RepeatBarLine;
import abc.notation.SlurDefinition;
import abc.notation.Spacer;
import abc.notation.StemPolicy;
import abc.notation.Tablature;
import abc.notation.Tempo;
import abc.notation.TieDefinition;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.Tuplet;
import abc.notation.TwoNotesLink;
import abc.notation.Voice;
import abc.notation.Words;
import abc.ui.scoretemplates.HorizontalPosition;
import abc.ui.scoretemplates.ScoreAttribute;
import abc.ui.scoretemplates.ScoreElements;
import abc.ui.scoretemplates.TextJustification;
import abc.ui.scoretemplates.VerticalPosition;

/**
 * This class role is to render properly a tune using Java 2D.
 * @see #setTune(Tune)
 */
public class JTune extends JScoreElementAbstract {

	//private static int DEBUG = 0;
	
	private final static short START = 0;
	private final static short CONTROL = 1;
	private final static short END = 2;
	
	private Tune m_tuneBeforeTransposition = null;

	/** The tune to be displayed. */
	private Tune m_tune = null;
	private Music m_music = null;

	/**
	 * Sets this to <TT>true</TT> each time you need that
	 * {@link #render(Graphics2D)} recalculate the complete
	 * tune
	 */
	private boolean m_isOutdated = true;

	/** Current positions in Voice (for {@link #compute()}) */
	//private int m_index = -1;
	private Map<String, Integer> m_indexes = null;
	
	/**
	 * Hashmap that associates <B>MusicElement</B> instances (key) and JScoreElement instances(value).
	 * It contains : JChord, JNote, JNotePartOfGroup, JChordNote,
	 * JGraceNote, JGraceNotePartOfGroup instances.
	 */
	private Hashtable<MusicElementReference, JScoreElementAbstract> m_scoreElements = null;
	private Vector<JGroupOfNotes> m_scoreNoteGroups = null;

	/** Note instances starting Slurs and ties. */
	private Vector<NoteAbstract> m_beginningNotesLinkElements = null;

	/* The staff lines drawings. */
	//private Vector m_staffLines = null;
	/** The systems */
	private Vector<JSystem> m_systems = null;

	private double m_height = -1;
	private double m_width = -1;

	private Engraver m_engraver = null;

	private Color m_color = Color.BLACK;
	
	private ArrayList<JText> m_headerAndFooterTexts = null;
	
	//temporary variables used only to cumpute the score when tune is set.
	//private boolean currentStaffLineInitialized = false;
	private JSystem m_currentSystem = null;
	//private JStaffLine currentStaffLine = null;
	//private KeySignature previousKey = null;
	//private KeySignature currentKey = null;
	//private Clef currentClef = null;
	//private TimeSignature previousTime = null;
	//private TimeSignature currentTime = null;
	//private Tablature m_currentTablature = null;
	private String m_currentVoice = "1";
	private Point2D cursor = null;

	protected JTune(Tune tune, Point2D base, ScoreTemplate st) {
		super(base, st.getMetrics());
		m_headerAndFooterTexts = new ArrayList<JText>();
		//m_staffLines = new Vector();
		m_systems = new Vector<JSystem>();
		m_scoreElements = new Hashtable<MusicElementReference, JScoreElementAbstract>();
		m_scoreNoteGroups = new Vector<JGroupOfNotes>();
		m_beginningNotesLinkElements = new Vector<NoteAbstract>();
		m_engraver = st.getEngraver();
		setTune(tune);
		setBase(base);
	}
	
	public boolean isJustified() {
		return getTemplate().isJustified();
	}
	
	public void setColor(Color c) {
		m_color = c;
	}
	public Color getColor() {
		return m_color;
	}
	private int getTransposition() {
		return (int)getTemplate().getAttributeNumber(ScoreAttribute.TRANSPOSITION);
	}

	public void onBaseChanged() {
		m_isOutdated = true;
	}

	public MusicElement getMusicElement() {
		return null;
	}

	public double getHeight() {
		if (m_isOutdated)
			compute();
		return m_height;
	}
	public double getWidth() {
		if (m_isOutdated)
			compute();
		return m_width;
	}
	public Rectangle2D getBoundingBox() {
		Rectangle2D bb = new Rectangle2D.Double(
				getBase().getX(),
				getBase().getY(),
				getWidth(),
				getHeight());
		return bb;
	}

	public Tune getTune() {
		return m_tuneBeforeTransposition;
	}
	
	/**
	 * Returns the rendition object (JNote, JChord...) for
	 * the given music element reference
	 * @param note
	 * @return
	 */
	public JScoreElement getRenditionObjectFor(MusicElementReference ref) {
		//fast way
		JScoreElement jse = (JScoreElement) m_scoreElements.get(ref);
		if (jse == null) {
			//if null, search comparing ref with all keys
			//System.out.println("Can't find "+ref+", compare keys");
			Enumeration<MusicElementReference> e = m_scoreElements.keys();
			while (e.hasMoreElements()) {
				MusicElementReference mer = e.nextElement();
				if (ref.equals(mer)) {
					jse = (JScoreElement) m_scoreElements.get(mer);
					//System.out.println("found!");
					break;
				}
			}
		}
		return jse;
	}
	
	/**
	 * Returns the rendition object (JNote, JChord...) for
	 * the given notation (Note, MultiNote)
	 * @param note
	 * @return
	 */
	public JScoreElement getRenditionObjectFor(MusicElement musicElement) {
		JScoreElement ret = getRenditionObjectFor(musicElement.getReference());
		//If it's a Note, look for chords which may contain this note
		/*if ((ret == null) && (musicElement instanceof Note)) {
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
		}*/
		return ret;
	}

	/** Returns a vector of score elements that represent goups of notes.
	 * rendition objects.
	 * @return Returns a vector of JGroupOfNotes instances appearing in this score
	 */
	public Vector<JGroupOfNotes> getNoteGroups() {
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
		for (JSystem jSys : m_systems) {
			for (JStaffLine jStaff : jSys.getStaffLines()) {
				scoreEl = jStaff.getScoreElementAt(location);
				if (scoreEl != null)
					return scoreEl;
				scoreEl = null; //WTF?
			}
		}
		return scoreEl;
	}

	/** Sets the tune to be renderered.
	 * @param tune The tune to be displayed. */
	protected void setTune(Tune tune) {
		m_tuneBeforeTransposition = tune;
		m_tune = null;
		m_isOutdated = true;
		int nbOfVoices = tune.getVoices().size();
		m_currentClef = new HashMap<String, Clef>(nbOfVoices);
		m_currentKey = new HashMap<String, KeySignature>(nbOfVoices);
		m_currentTime = new HashMap<String, TimeSignature>(nbOfVoices);
		m_previousKey = new HashMap<String, KeySignature>(nbOfVoices);
		m_previousTime = new HashMap<String, TimeSignature>(nbOfVoices);
		m_staffLineInitialized = new HashMap<String, Boolean>(nbOfVoices);
		m_tablatures = new HashMap<String, Tablature>(nbOfVoices);
		m_indexes = new HashMap<String, Integer>(nbOfVoices);
	}
	
	protected void setOutdated() {
		m_isOutdated = true;
		m_tune = null;
	}
	
	private void computeTextFieldToJText(byte textField) {
		if ((textField == ScoreElements.TEXT_TITLE)
				|| (textField == ScoreElements.TEXT_SUBTITLE)) {
			String[] titles = m_tune.getTitles();
			if (titles != null) {
				for (int i=0; i < titles.length; i++) {
					if (titles[i].length() == 0)
						continue;
					if ((i == 0) && (textField == ScoreElements.TEXT_TITLE)) {
						//title
						m_headerAndFooterTexts.add(new JText(getMetrics(), titles[i], ScoreElements.TEXT_TITLE));
					} else if ((i > 0) && (textField == ScoreElements.TEXT_SUBTITLE)) {
						// subtitles
						m_headerAndFooterTexts.add(new JText(getMetrics(), titles[i], ScoreElements.TEXT_SUBTITLE));
					}
				}
			}
		}
		else if (textField == ScoreElements.TEXT_PARTS_ORDER) {
			if (m_tune.getMultiPartsDefinition() != null) {
				Part[] parts = m_tune.getMultiPartsDefinition().toPartsArray();
				String txt = "";
				//if all parts are only played one time
				//no need to add this information
				//e.g. A B C D
				//but A B A C D will display (because 2 A)
				boolean hasDouble = false;
				for (int i = 0; i < parts.length; i++) {
					char c = parts[i].getIdentifier();
					if (txt.indexOf(c) != -1)
						hasDouble = true;
					if (txt.length() > 0)
						txt += ", ";
					txt += c;
				}
				if (hasDouble) {
					m_headerAndFooterTexts.add(
							new JText(getMetrics(), txt, ScoreElements.TEXT_PARTS_ORDER));
				}
			}
		}
		else {
			String text = null;
			switch (textField) {
			case ScoreElements.TEXT_ANNOTATIONS://=NOTES
				text = m_tune.getNotes(); break;
			case ScoreElements.TEXT_BOOK:
				text = m_tune.getBook(); break;
			case ScoreElements.TEXT_COMPOSER:
				text = m_tune.getComposer(); break;
			case ScoreElements.TEXT_DISCOGRAPHY:
				text = m_tune.getDiscography(); break;
			case ScoreElements.TEXT_FILEURL:
				text = m_tune.getFileURL(); break;
			case ScoreElements.TEXT_GROUP:
				text = m_tune.getGroup(); break;
			case ScoreElements.TEXT_HISTORY:
				text = m_tune.getHistory(); break;
			case ScoreElements.TEXT_INFORMATIONS:
				text = m_tune.getInformation(); break;
			case ScoreElements.TEXT_LYRICIST:
				text = m_tune.getLyricist(); break;
			case ScoreElements.TEXT_ORIGIN://=AREA
				text = m_tune.getOrigin();
				if (text == null)
					text = "";
				if ((m_tune.getArea() != null) && (m_tune.getArea().length() > 0))
					text += (text.length()>0?", ":"")
						+ m_tune.getArea();
				break;
			case ScoreElements.TEXT_RHYTHM:
				text = m_tune.getRhythm(); break;
			case ScoreElements.TEXT_SOURCE:
				text = m_tune.getSource(); break;
			case ScoreElements.TEXT_TRANSCRNOTES:
				text = m_tune.getTranscriptionNotes(); break;
			case ScoreElements.TEXT_WORDS:
				text = m_tune.getLyricist(); break;
			}
			if ((text != null) && (text.length() > 0)) {
				m_headerAndFooterTexts.add(
					new JText(getMetrics(), text, textField));
			}
		}
	}
	
	private Map<String, KeySignature> m_currentKey, m_previousKey;
	
	private Map<String, Clef> m_currentClef;
	private Map<String, TimeSignature> m_currentTime,
		m_previousTime;
	private Map<String, Boolean> m_staffLineInitialized;
	
	private Map<String, Tablature> m_tablatures;

	/**
	 * (Re)calculate everything for the tune.
	 */
	private void compute() {
		getBase().setLocation(getTemplate().getAttributeSize(ScoreAttribute.MARGIN_LEFT),
				getTemplate().getAttributeSize(ScoreAttribute.MARGIN_TOP));
		cursor = (Point2D) getBase().clone();
		
		double componentWidth = 0, componentHeight = 0;
		
		//System.out.println("Pass #"+(++DEBUG));
		
		//if (m_isOutdated || (m_tuneBeforeTransposition == null))
		//	m_tuneBeforeTransposition = (Tune) m_tune.clone();
		m_isOutdated = false;
		m_music = null;
		m_tune = Tune.transpose(m_tuneBeforeTransposition,
				getTransposition());

		getEngraver().adaptToTune(m_tune, getMetrics());

		m_scoreElements.clear();
		m_scoreNoteGroups.clear();
		//m_staffLines.removeAllElements();
		m_systems.clear();
		m_beginningNotesLinkElements.clear();
		
		
		//currentKey = null;
		//previousKey = null;
		//currentClef = null;
		m_currentClef.clear();
		m_currentKey.clear();
		m_currentTime.clear();
		m_previousKey.clear();
		m_previousTime.clear();
		m_staffLineInitialized.clear();
		m_tablatures.clear();

		// clear headings, eg. titles, subtitles, composer, etc.
		m_headerAndFooterTexts.clear();
		
		byte[] headerFields = getTemplate().getHeaderFields();
		for (int i = 0; i < headerFields.length; i++) {
			computeTextFieldToJText(headerFields[i]);
		}
		//calculate Y position
		double y = cursor.getY();
		double yLeft = y, yCenter = y, yRight = y;
		for (JText oneText : m_headerAndFooterTexts) {
			//only headers
			if (oneText.getVerticalPosition()
					!= VerticalPosition.TOP)
				continue;
			byte align = oneText.getHorizontalPosition();
			//double textWidth = oneText.getWidth();
			if (align == HorizontalPosition.CENTER)
				y = Math.max(yLeft, Math.max(yCenter, yRight));
			else if (align == HorizontalPosition.RIGHT)
				y = Math.max(yRight, yCenter);
			else //LEFT || LEFT_TAB
				y = Math.max(yLeft, yCenter);
			y += oneText.getHeight();
			//now it's aligned on left, we'll recalculate
			//X later (see renderTitles)
			//for centered and right texts
			cursor.setLocation(getBase().getX(), y);
			if (align == HorizontalPosition.CENTER)
				yCenter = y;
			else if (align == HorizontalPosition.RIGHT)
				yRight = y;
			else //LEFT || LEFT_TAB
				yLeft = y;
			oneText.setBase(cursor);
		}
		//move the cursor at the max Y of the headers
		double headerHeight = Math.max(yLeft, Math.max(yCenter, yRight));
		cursor.setLocation(getBase().getX(), headerHeight);

		//if tune has part, tune.getMusic() will return
		//duplicate parts when repeated
		//e.g. structure ABBA, P:A one line, P:B one line
		//getMusic() returns 4 lines
		//it's ok for audio rendition, but for graphical
		//we just need one occurence of each part.
		//m_tune.getVoice("1").setTablature(Tablature.BOUZOUKI_GREEK_TRICHORDO);
		m_music = m_tune.getMusicForGraphicalRendition();
		
		ArrayList<NoteAbstract> lessThanQuarter = new ArrayList<NoteAbstract>();
		//int durationInGroup = 0;
		//int maxDurationInGroup = Note.QUARTER;
		//int durationInCurrentMeasure = 0;
		Tuplet tupletContainer = null;
		int staffLineNb = 0;
		//init attributes that are for iterating through the score of the tune.
		
		for (Voice v : m_music.getVoices()) {
			KeySignature key = v.getKey();
			if (key != null) {
				m_currentKey.put(v.getVoiceId(), key);
				try {
					m_previousKey.put(v.getVoiceId(), (KeySignature)key.clone());
				} catch (CloneNotSupportedException never) {
					never.printStackTrace();
				}
			}
			Tablature tab = v.getTablature();
			if (tab != null) {
				tab.computeFingerings(v);
				m_tablatures.put(v.getVoiceId(), tab);
			}
		}
//		currentKey = m_tune.getKey();
//		if (currentKey != null) {
//			try {
//				previousKey = (KeySignature) currentKey.clone();
//			} catch (CloneNotSupportedException never) {
//				never.printStackTrace();
//			}
//		}
//		else
//			previousKey = null;
//		previousTime = null;
//		currentTime = null;
//		currentStaffLineInitialized = false;
//		currentStaffLine = null;
		m_currentSystem = null;
//		m_currentTablature = null;
		while (!allVoicesCompleted()) {
			m_currentSystem = initNewSystem();
//DEBUG			System.out.println("DEBUG "+m_systems.size()+" system(s)");
			for (Voice voice : m_music.getVoices()) {
				m_currentVoice = voice.getVoiceId();
//DEBUG				System.out.println("DEBUG voice="+m_currentVoice);
//			m_currentTablature = voice.getTablature();
//			//m_currentTablature = Tablature.GUITAR;
//			if (m_currentTablature != null) {
//				m_currentTablature.computeFingerings(voice);
//			}
				//continue at last index for this voice
				int size = voice.size();
				int index = getIndex(m_currentVoice);
				while (index < size) {
					MusicElement s = (MusicElement)voice.elementAt(index);
//					if (s instanceof PositionableInTime) {
//						PositionableInTime pit = (PositionableInTime) s;
//						System.out.println(s.toString() + " : "
//								+pit.getPositionInBars()+","
//								+pit.getPositionInBeats()
//								+" ("+pit.getPositionInUnits()+")");
//					}
					index++;
					m_indexes.put(m_currentVoice, new Integer(index));
					//System.out.println(s.toString() + " " + s.getReference().toString());
					// ==== Notes>quarter, rests, notes without slur,tuplet ====
					if (!lessThanQuarter.isEmpty()
						&& (
						!(s instanceof Note  || s instanceof MultiNote)
						|| (s instanceof Note && ((Note)s).isRest())
						//if we were in a tuplet and the current note isn't part of tuplet anymore or part of another tuplet
						|| (s instanceof NoteAbstract && tupletContainer!=null && (!tupletContainer.equals(((NoteAbstract)s).getTuplet())))
						//if we weren't in a tuplet and the new note is part of a tuplet.
						|| (s instanceof NoteAbstract && tupletContainer==null && ((NoteAbstract)s).isPartOfTuplet())
						|| (s instanceof Note && ((Note)s).getStrictDuration()>=Note.QUARTER)
						|| (s instanceof MultiNote && (  !((MultiNote)s).hasUniqueStrictDuration() ||
														((MultiNote)s).getLongestNote().getStrictDuration()>=Note.QUARTER)))

					) {
						//this is is the end of the group, append the current group content to the score.
						appendToScore(lessThanQuarter);
						lessThanQuarter.clear();
					}
					
					// ==== Key signature ====
					if (s instanceof KeySignature) {
						KeySignature currKey = (KeySignature)s;
						Object o = m_previousKey.get(m_currentVoice);
						KeySignature prevKey = (o==null)?null:(KeySignature)o;
						m_currentKey.put(m_currentVoice, currKey);
						m_currentClef.put(m_currentVoice, currKey.getClef());
						if (isInitialized(m_currentVoice) && !currKey.equals(prevKey)) {
							appendToScore(new JKeySignature(currKey, prevKey, cursor, getMetrics()));
							try {
								m_previousKey.put(m_currentVoice, (KeySignature) currKey.clone());
							} catch (CloneNotSupportedException never) {
								never.printStackTrace();
							}
						}
					}
					// ==== Time signature ====
					else if (s instanceof TimeSignature) {
						Object o = m_currentTime.get(m_currentVoice);
						TimeSignature prevTime = (o==null)?null:(TimeSignature)o;
						if (prevTime != null) {
							try {
								m_previousTime.put(m_currentVoice, (TimeSignature) prevTime.clone());
							} catch (CloneNotSupportedException never) {
								never.printStackTrace();
							}
						}
						TimeSignature currTime = (TimeSignature)s;
						m_currentTime.put(m_currentVoice, currTime);
						//if (previousTime != null)
						if (isInitialized(m_currentVoice) && !currTime.equals(prevTime))
							appendToScore(new JTimeSignature(currTime, cursor, getMetrics()));
					}
					// ==== MultiNote ====
					else if (s instanceof MultiNote) {
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
							lessThanQuarter.add((MultiNote)s);
						else {
							appendToScore(new JChord((MultiNote)s,
									(Clef)m_currentClef.get(m_currentVoice),
									getMetrics(), cursor));
						}
		
						//durationInCurrentMeasure+=((MultiNote)s).getLongestNote().getDuration();
					} //end MultiNote
					// ==== Note ====
					else if (s instanceof Note) {
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
							appendToScore(new JNote(note,
									(Clef)m_currentClef.get(m_currentVoice),
									cursor, getMetrics()));
						}
						//durationInCurrentMeasure+=note.getDuration();
					} //end Note
					// ==== RepeatBarLine ===
					else if (s instanceof RepeatBarLine) {
						appendToScore(new JRepeatBar((RepeatBarLine)s, cursor, getMetrics()));
						//durationInCurrentMeasure=0;
					}
					// ==== BarLine ====
					else if (s instanceof BarLine) {
						appendToScore(new JBar((BarLine)s, cursor, getMetrics()));
						//durationInCurrentMeasure=0;
					}
					// ==== EndOfStaffLine ====
					else if (s instanceof EndOfStaffLine) {
						appendToScore(lessThanQuarter);
						lessThanQuarter.clear();
						staffLineNb++;
						m_staffLineInitialized.put(m_currentVoice, new Boolean(false));
						break;
					}
					// ==== NotesSeparator ====
					else if (s instanceof NotesSeparator) {
						appendToScore(lessThanQuarter);
						lessThanQuarter.clear();
					}
					// ==== Spacer ====
					else if (s instanceof Spacer) {
						appendToScore(lessThanQuarter);
						lessThanQuarter.clear();
						appendToScore(new JSpacer(getMetrics(), cursor, (Spacer)s));
					}
					// ==== Words ====
					//TODO Words really here? see music model, attach to note?
					else if (s instanceof Words) {
						appendLyrics(new JWords(getMetrics(), (Words)s));
					}
					// ==== Part label ====
					else if (s instanceof PartLabel) {
						appendToScore(new JPartLabel(getMetrics(), cursor, (PartLabel)s));
					}
					// ==== Tempo ====
					else if ((s instanceof Tempo)/* && (s != m_tune.getGeneralTempo())*/) {
						appendToScore(new JTempo(getMetrics(), cursor, (Tempo)s));
					}
					// ==== MeasureRepeat ====
					else if (s instanceof MeasureRepeat) {
						appendToScore(new JMeasureRepeat(getMetrics(), cursor, (MeasureRepeat)s));
					}
				}//end each element in voice
				//if end reached and no spacer, line break...
				//add last group of notes
				if (lessThanQuarter.size()!=0) {
					appendToScore(lessThanQuarter);
					lessThanQuarter.clear();
					//durationInGroup = 0;
				}
			}//end each voice in music, maybe a new system
		}//end while !allVoicesCompleted()

		if (isJustified())
			justify();

		cursor.setLocation(cursor.getX(),
			(m_currentSystem!=null
					?m_currentSystem.getBottomY()
					:cursor.getY())
				+getMetrics().getStaffCharBounds().getHeight());

		// ==== Foot notes ====
		byte[] footerFields = getTemplate().getFooterFields();
		for (int i = 0; i < footerFields.length; i++) {
			computeTextFieldToJText(footerFields[i]);
		}
		double height = 0;
		for (JText jtext : m_headerAndFooterTexts) {
			if (jtext.getVerticalPosition() != VerticalPosition.BOTTOM)
				continue;
			height = jtext.getHeight();//*1.2;
			cursor.setLocation(cursor.getX(), cursor.getY()+height);
			jtext.setBase(cursor);
		}
		//a little more space for bottom baseline letters (like p, q...)
		cursor.setLocation(cursor.getX(), cursor.getY()+1);

		if (m_systems != null) {
			double maxWidth = 0;
			for (JSystem jSys : m_systems) {
				maxWidth = Math.max(maxWidth, jSys.getWidth());
			}
			componentWidth = maxWidth + 1;
		} else {
			if (cursor.getX() > componentWidth)
				componentWidth = (int)cursor.getX() + 1;
		}
		
		//if header/footer texts are too long, extend component width
		for (JText jt : m_headerAndFooterTexts) {
			componentWidth = Math.max(componentWidth, jt.getWidth());
		}
		componentWidth += getTemplate().getAttributeSize(
				ScoreAttribute.MARGIN_RIGHT);
		componentHeight = (int)cursor.getY();
		componentHeight += getTemplate().getAttributeSize(
				ScoreAttribute.MARGIN_BOTTOM);

		m_width = componentWidth;
		m_height = componentHeight;
	}
	
	private boolean isInitialized(String voiceId) {
		Object o = m_staffLineInitialized.get(voiceId);
		return (o == null) ? false : ((Boolean)o).booleanValue();
	}
	private int getIndex(String voiceId) {
		Integer i = m_indexes.get(voiceId);
		if (i == null) {
			i = new Integer(0);
			m_indexes.put(voiceId, i);
		}
		return i.intValue();
	}
	private boolean allVoicesCompleted() {
		for (Voice v : m_music.getVoices()) {
			String vName = v.getVoiceId();
			if (getIndex(vName) < v.size())
				return false;
		}
		//for all voices, index = v.size()
		return true;
	}

	/**
	 * Append an element to the score, calculating its
	 * position
	 * @param element
	 */
	private void appendToScore(JScoreElementAbstract element) {
		JStaffLine currentStaffLine = null;
		if (!isInitialized(m_currentVoice)) {
			currentStaffLine = initNewStaffLine();
			m_staffLineInitialized.put(m_currentVoice, new Boolean(true));
		} else {
			currentStaffLine = m_currentSystem.lastStaffLine();
		}
		
		JScoreElement lastElement = currentStaffLine.getLastElement();
		if (lastElement != null) {
			if ((lastElement instanceof JPartLabel)
					&& !(element instanceof JBar)
					&& !(element instanceof JKeySignature)
					&& !(element instanceof JTempo)
					&& !(element instanceof JTimeSignature)) {
				cursor.setLocation(cursor.getX()+getMetrics().getNotesSpacing(), cursor.getY());
				//element.setBase(cursor);
			}
		}
		currentStaffLine.addElement(element);
		applyStemmingPolicy(element);
		element.setBase(cursor);
		double width = element.getWidth();
		//System.out.println("  -> width="+width);
		int cursorNewLocationX = (int)(cursor.getX() + width);

		//a part label doesn't need space after it
		//if the next element is a bar line.
		if (!(element instanceof JPartLabel)
				&& !(element instanceof JTempo)) {
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
		}
		//if a PartLabel is the first element on the line (no notes)
		//justify it to the right, not to override first note/chord
		if ((element instanceof JPartLabel)
				&& !currentStaffLine.hasNotes()) {
			if (((JPartLabel)element).getText().length() == 1)
				((JPartLabel)element).setTextJustification(TextJustification.RIGHT);
		}

		//Position the cursor for next element
		cursor.setLocation(cursorNewLocationX, cursor.getY());

		if (element instanceof JNote)
			m_scoreElements.put(element.getMusicElement().getReference(), element);
		else
		if (element instanceof JGroupOfNotes) {
			JGroupOfNotes g = (JGroupOfNotes)element;
			m_scoreNoteGroups.add(g);

			for (int j=0; j<g.getRenditionElements().length; j++) {
				if ((g.getRenditionElements()[j]) instanceof JNote)
					m_scoreElements.put(g.getMusicElements()[j].getReference(), g.getRenditionElements()[j]);
				else
				if (g.getRenditionElements()[j] instanceof JChord){
					JNote[] jnotes = ((JChord)g.getRenditionElements()[j]).getScoreElements();
					//Vector ...=... getNotesAsVector() <-- CLONES!!!
					//Note[] notes = ((MultiNote)((JChord)g.getRenditionElements()[j]).getMusicElement()).toArray();
					//adds all the notes of the chords into the hashtable
					//TODO the ordering of the get notes as vector and the jnotes should be the same...
					//System.out.println("Warning - abc4j - current limitation prevents you from using chords with different notes lengths.");
					for (int i=0; i<jnotes.length; i++)
						m_scoreElements.put(jnotes[i].getMusicElement().getReference(), jnotes[i]);
					//adds also the chords itself
					m_scoreElements.put(g.getRenditionElements()[j].getMusicElement().getReference(), g.getRenditionElements()[j]);
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
				m_scoreElements.put(jnotes[i].getMusicElement().getReference(), jnotes[i]);
			//adds also the chords itself
			m_scoreElements.put(element.getMusicElement().getReference(), element);
		}
	}

	private void appendToScore(ArrayList<NoteAbstract> lessThanQuarterGroup){
		if (lessThanQuarterGroup.size()>0) {
			JScoreElementAbstract renditionResult = null;
			JScoreElementAbstract[] renditionResultRootsElmts = new JScoreElementAbstract[lessThanQuarterGroup.size()];
			NoteAbstract[] notes = (NoteAbstract[])lessThanQuarterGroup.toArray(new NoteAbstract[lessThanQuarterGroup.size()]);
			if (notes.length==1) {
				if (notes[0] instanceof Note) {
					renditionResult = new JNote((Note)notes[0],
							(Clef) m_currentClef.get(m_currentVoice),
							cursor, getMetrics());
					renditionResultRootsElmts[0] = renditionResult;
				}
				else {
					//This is a multi note
					renditionResult = new JChord((MultiNote)notes[0],
							(Clef) m_currentClef.get(m_currentVoice),
							getMetrics(), cursor);
					renditionResultRootsElmts[0] = renditionResult;
				}
			}
			else {
				renditionResult = new JGroupOfNotes(
						getMetrics(), cursor, notes,
						(Clef) m_currentClef.get(m_currentVoice),
						getEngraver());
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
		if (!m_currentSystem.lastStaffLine().hasLyrics()) {
			// add a 2 line gap under staff line for lyrics
			adjustment *= 2;
		}
		y += adjustment;
		// lyrics always occur under a staff line, so it should be safe
		// to reset base to X=0, Y=(current Y + height of char in lyrics font)
		cursor.setLocation(getBase().getX(), y);

		m_currentSystem.lastStaffLine().addLyrics(lyrics);
//		lyrics.setStaffLine(currentStaffLine);
		lyrics.setBase(cursor);
	}
	
	/** apply stemming policy to an element */
	private void applyStemmingPolicy(JScoreElementAbstract element) {
		if (element != null && element instanceof JStemmableElement) {
			JStemmableElement stemmable = (JStemmableElement) element;
			if (stemmable.isFollowingStemmingPolicy()) {
				byte noteStemPolicy = (byte) getTemplate().getAttributeNumber(ScoreAttribute.NOTE_STEM_POLICY);
				if (noteStemPolicy == StemPolicy.STEMS_AUTO) {
					stemmable.setAutoStem(true);
				} else {
					boolean isup = (noteStemPolicy == StemPolicy.STEMS_UP) ? true : false;
					stemmable.setAutoStem(false);
					stemmable.setStemUp(isup);
				}
			}
			//if element N is up, element N-1 is middle staff,
			//and N-2 is up too, then set N-1 up
			//e.g.: in G clef A B A (B is up), c B c (B is down)
			JStaffLine currentStaffLine = m_currentSystem.lastStaffLine();
			int count = currentStaffLine.countElement();
			if (stemmable.isStemUp() && (count >= 2)) {
				Vector<JScoreElementAbstract> staffElements = currentStaffLine.getStaffElements();
				JScoreElement N1 = (JScoreElement) staffElements.get(count-2);
				JScoreElement N2 = (JScoreElement) staffElements.get(count-3);
				if ((N1 instanceof JStemmableElement)
					&& (N1 instanceof JNote)
					&& (((JStemmableElement)N1).isFollowingStemmingPolicy())
					&& (N2 instanceof JStemmableElement)
					&& !((JStemmableElement) N1).isStemUp()
					&& ((JStemmableElement) N2).isStemUp()) {
					Note n = (Note) ((JNote) N1).getMusicElement();
					if (n.getHeight() == ((JNote) N1).getClef().getMiddleNote().getHeight()) {
						((JStemmableElement) N1).setAutoStem(false);
						((JStemmableElement) N1).setStemUp(true);
					}
				}
			}
			//grace notes
			byte gracenoteStemPolicy = (byte) getTemplate().getAttributeNumber(ScoreAttribute.GRACENOTE_STEM_POLICY);
			if (element instanceof JNoteElementAbstract) {
				JNoteElementAbstract jnea = (JNoteElementAbstract) element;
				if ((jnea.m_jGracenotes != null)
					&& (jnea.m_jGracenotes instanceof JStemmableElement)) {
					JStemmableElement stemmableGN = (JStemmableElement) jnea.m_jGracenotes;
					if (gracenoteStemPolicy == StemPolicy.STEMS_AUTO) {
						stemmableGN.setAutoStem(true);
					} else {
						boolean isup = (gracenoteStemPolicy == StemPolicy.STEMS_UP) ? true : false;
						stemmableGN.setAutoStem(false);
						stemmableGN.setStemUp(isup);
					}
				}
			} else if (element instanceof JGroupOfNotes) {
				JGroupOfNotes jgon = (JGroupOfNotes) element;
				for (int i = 0; i < jgon.m_jNotes.length; i++) {
					JNoteElementAbstract jnea = (JNoteElementAbstract) jgon.m_jNotes[i];
					if (jnea.m_jGracenotes != null) {
						JStemmableElement stemmableGN =
							(JStemmableElement) jnea.m_jGracenotes;
						if (gracenoteStemPolicy == StemPolicy.STEMS_AUTO) {
							stemmableGN.setAutoStem(true);
						} else {
							boolean isup = (gracenoteStemPolicy == StemPolicy.STEMS_UP) ? true : false;
							stemmableGN.setAutoStem(false);
							stemmableGN.setStemUp(isup);
						}
					}
				}
			}
		}
	}

	public double render(Graphics2D g2) {
		if (m_isOutdated)
			compute();
		
		g2.setFont(getMetrics().getNotationFontForContext(
				ScoreMetrics.NOTATION_CONTEXT_NOTE));
		g2.setColor(m_color);
		super.setColor(g2, ScoreElements._DEFAULT);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    renderTitlesAndFootnotes(g2);
	    for (JSystem jSys : m_systems) {
	    	jSys.render(g2);
	    }
		renderSlursAndTies(g2);

		return getWidth();
	}

	private void renderTitlesAndFootnotes(Graphics2D g2) {
		for (JText text : m_headerAndFooterTexts) {
			//System.out.println(text.getText());
			if (text.getBase() == null) {
				System.err.println("base is null for "+text);
				continue;
			}
			short align = text.getHorizontalPosition();
			//double textWidth = text.getWidth();
			double y = text.getBase().getY();
			if (align == HorizontalPosition.CENTER) {
				text.setBase(new Point2D.Double(getWidth()/2, y));
			} else if (align == HorizontalPosition.RIGHT) {
				text.setBase(new Point2D.Double(getWidth(), y));
			} else if (align == HorizontalPosition.LEFT_TAB) {
				text.setBase(new Point2D.Double(getBase().getX()
					+ getTemplate().getAttributeSize(ScoreAttribute.FIRST_STAFF_LEFT_MARGIN)
					+ 30, y));
			} else {//HorizontalAlign.LEFT
				text.setBase(new Point2D.Double(getBase().getX(), y));
			}
			text.render(g2);
		}
	}

	/** Triggers the re computation of all staff lines elements in order to
	 * get the alignment justified. */
	private void justify() {
		if (m_systems.size() > 1) {
			double maxWidth = 0;
			//take into account the left margin of first system
			double maxRightX = 0;
			for (JSystem sys : m_systems) {
				double sysWidth = sys.getWidth();
				maxWidth = Math.max(sysWidth, maxWidth);
				maxRightX = Math.max(sysWidth + sys.getBase().getX(), maxRightX);
			}
			for (JSystem sys : m_systems) {
				if (sys.getWidth() > maxWidth / 2)
					sys.scaleToWidth(maxRightX - sys.getBase().getX());
			}
		}
	}

	private void renderSlursAndTies(Graphics2D g2) {
		Color previousColor = g2.getColor();
		//System.err.println("renderSlursAndTies");
		for (int j=0; j<m_beginningNotesLinkElements.size(); j++) {
			NoteAbstract n = (NoteAbstract)m_beginningNotesLinkElements.elementAt(j);
			TwoNotesLink link = n.getTieDefinition();
			if (link != null && link.getEnd() != null) {
				setColor(g2, ScoreElements.TIE);
				drawLink(g2, link);
			}
			setColor(g2, ScoreElements.SLUR);
			Vector<SlurDefinition> slurs = n.getSlurDefinitions();
			int i = 0;
			while (i < slurs.size()) {
				link = (SlurDefinition) slurs.elementAt(i);
				if (link != null && link.getEnd() != null) {
					drawLink(g2, link);
				}
				i++;
			}
		}
		g2.setColor(previousColor);
	}

	/**
	 * Draw a link between 2 notes
	 *
	 * @see #getLinkPoints(TwoNotesLink) calculates the best link curve
	 * @param g2
	 * @param slurDef
	 */
	protected void drawLink(Graphics2D g2, TwoNotesLink slurDef) {
		Point2D[] points = null;
		try {
			points = getLinkPoints(slurDef);
		} catch (Exception e) {
			System.err.println("Exception in drawLink : "+e.getMessage());
			e.printStackTrace();
		}
		if ((points == null) || (points.length != 3))
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
		
//		System.out.println("drawLink "+slurDef);
//		System.out.println("  - start = "+points[START]);
//		System.out.println("  - end = "+points[END]);

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
		double slurThickness = Math.max(1.0,
				getTemplate().getAttributeSize(
						ScoreAttribute.SLUR_THICKNESS));
		if (!isAboveNotes) {
			points[CONTROL].setLocation(points[CONTROL].getX(),
				points[CONTROL].getY()+slurThickness);
		} else {
			points[CONTROL].setLocation(points[CONTROL].getX(),
					points[CONTROL].getY()-slurThickness);
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
		JNoteElementAbstract elmtStart
			= (JNoteElementAbstract) getRenditionObjectFor(slurDef.getStart());
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
		elmtStart = (JNoteElementAbstract) getRenditionObjectFor(slurDef.getStart());
		elmtEnd = (JNoteElementAbstract) getRenditionObjectFor(slurDef.getEnd());
		if (elmtStart == null || elmtStart == null) {
			System.err.println("getLinkPoints error: elmtStart or elmtEnd are null");
			return new Point2D[] {};
		}
		
		if ((elmtStart.getStaffLine() != null) && (elmtEnd.getStaffLine() != null)) {
			if (!elmtStart.getStaffLine().equals(elmtEnd.getStaffLine())) {
				System.err.println("Warning - abc4j limitation : Slurs / ties cannot be drawn accross several lines for now.");
				return new Point2D[] {};
			}
		}
		final short UNDER_IN = 0; //the only existing in old abc4j
		final short UNDER_OUT = 1; //down and out of stems
		final short ABOVE_IN = 2; //up and can go across the stems
		final short ABOVE_OUT = 3; //up and out of stems (e.g. for tuplet)
		Point2D[][] p = new Point2D[4][3];

		//it's a tie, anchors are different and curve is flater
		if (slurDef instanceof TieDefinition) {
			Point2D[] above = new Point2D[3];
			Point2D[] under = new Point2D[3];
			Point2D[] ret;
			above[START] = elmtStart.getTieStartAboveAnchor();
			above[END] = elmtEnd.getTieEndAboveAnchor();
			under[START] = elmtStart.getTieStartUnderAnchor();
			under[END] = elmtEnd.getTieEndUnderAnchor();
		
			short position = elmtStart.getJSlurDefinition().getPosition();
			if (position==JSlurOrTie.POSITION_AUTO
				&& (elmtStart instanceof JChordNote)) {
				if (elmtStart.isStemUp()) {
					//in a chord stem up, all ties are under
					//the highest note tie is above
					position = ((JChordNote) elmtStart).isHighest()
						?JSlurOrTie.POSITION_ABOVE
						:JSlurOrTie.POSITION_UNDER;
				} else {
					//in a chord stem down, all ties are above
					//the lowest note tie is under
					position = ((JChordNote) elmtStart).isLowest()
						?JSlurOrTie.POSITION_UNDER
						:JSlurOrTie.POSITION_ABOVE;
				}
			}
			int factor;
			if ((position==JSlurOrTie.POSITION_ABOVE)
				|| (position==JSlurOrTie.POSITION_AUTO
						&& (!elmtStart.isStemUp() || !elmtEnd.isStemUp()))) {
				//tie above
				ret = above;
				factor = -1;
			} else {// if ((position==JSlurOrTie.POSITION_UNDER)
					//|| (position==JSlurOrTie.POSITION_AUTO
					//		&& elmtStart.isStemUp() && elmtEnd.isStemUp())) {
				//tie under
				ret = under;
				factor = 1;
			}
			ret[CONTROL] = new Point2D.Double(
					ret[START].getX() + (ret[END].getX()-ret[START].getX())/2,
					ret[START].getY() + factor*getMetrics().getSlurAnchorYOffset()
				);
			return ret;
		}
		
		p[UNDER_IN][START] = elmtStart.getSlurUnderAnchor();
		p[UNDER_OUT][START] = elmtStart.getSlurUnderAnchorOutOfStem();
		p[ABOVE_IN][START] = elmtStart.getSlurAboveAnchor();
		p[ABOVE_OUT][START] = elmtStart.getSlurAboveAnchorOutOfStem();
		p[UNDER_IN][END] = elmtEnd.getSlurUnderAnchor();
		p[UNDER_OUT][END] = elmtEnd.getSlurUnderAnchorOutOfStem();
		p[ABOVE_IN][END] = elmtEnd.getSlurAboveAnchor();
		p[ABOVE_OUT][END] = elmtEnd.getSlurAboveAnchorOutOfStem();

		//
		//determinate peaks (lowest/highest) note and note glyph
		// FIXME: ties should enclose note decorations
		//
		NoteAbstract[] peakNote = new NoteAbstract[4];
		JNoteElementAbstract[] peakNoteGlyph = new JNoteElementAbstract[4];

		NoteAbstract startNote =// (NoteAbstract) m_music.getElementByReference(slurDef.getStart());
			(NoteAbstract) elmtStart.getMusicElement();
		NoteAbstract endNote =// (NoteAbstract) m_music.getElementByReference(slurDef.getEnd());
			(NoteAbstract) elmtEnd.getMusicElement();
		String voiceNo = startNote.getReference().getVoice();
		//under in
		peakNote[UNDER_IN] = m_music.getVoice(voiceNo).getLowestNoteBewteen(startNote, endNote);
		peakNoteGlyph[UNDER_IN] = (JNoteElementAbstract) getRenditionObjectFor(peakNote[UNDER_IN]);
		
		//under out of stems
		peakNoteGlyph[UNDER_OUT] = getLowestNoteGlyphBetween(startNote, endNote, true);
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
		peakNote[ABOVE_IN] = m_music.getVoice(voiceNo).getHighestNoteBewteen(startNote, endNote);
		peakNoteGlyph[ABOVE_IN] = (JNoteElementAbstract)getRenditionObjectFor(peakNote[ABOVE_IN]);

		//above out of stems (e.g. tuplet)
		peakNoteGlyph[ABOVE_OUT] = getHighestNoteGlyphBetween(startNote, endNote, true);
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
			if (peakNote[i].getReference().equals(slurDef.getStart())) {
				p[i][CONTROL] = new Point2D.Double(
					p[i][START].getX() + (p[i][END].getX()-p[i][START].getX())/2,
					p[i][START].getY() + factor*getMetrics().getSlurAnchorYOffset()
				);
			}
			else if (peakNote[i].getReference().equals(slurDef.getEnd())) {
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
		Vector<Point2D[]> curves = new Vector<Point2D[]>(); //Combinaison to compare
		JSlurOrTie jSlurDef = getJSlurOrTie(slurDef);
		if (!jSlurDef.isOutOfStems()) { //we can add all curves
			Vector<Point2D[]> underCurves = new Vector<Point2D[]>(),
			aboveCurves = new Vector<Point2D[]>();
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
			for (Point2D[] p2d : aboveCurves) {
				if ((p2d[CONTROL].getY()<p2d[START].getY())
					&& (p2d[CONTROL].getY()<p2d[END].getY()))
					curves.add(p2d);
			}
			for (Point2D[] p2d : underCurves) {
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
		Vector<Point2D[]> additionnalCurves = new Vector<Point2D[]>();
		Point2D[] bestCurve = null;
		float bestMark = -99;
		//int cpt = 0; int bestCurveIdx = -1;
		for (Iterator<Point2D[]> itCurves = curves.iterator(); itCurves.hasNext();) {
			Point2D[] p2d = itCurves.next();
			//System.out.print((cpt++) + " : ");

			try {
				SlurInfos slurInfos = new SlurInfos(p2d,
					getNoteGlyphesBetween(startNote, endNote),
					getMetrics());

				//Check results
				//If some intersections, try to draw a new curve
				//by moving the control point
				if (slurInfos.intersect > 0) {
					Point2D newPtControl;
					double yOffset = getMetrics().getNoteHeight();
					double xOffset = getMetrics().getNoteWidth();
					int yFactor = slurInfos.isAbove?-1:1;
					double distanceControlEnd = p2d[END].getX()-p2d[CONTROL].getX();
					double distanceControlStart = p2d[CONTROL].getX()-p2d[START].getX();
					int xFactor = 0;
					if (distanceControlEnd < distanceControlStart)
						xFactor = -1;
					else if (distanceControlEnd > distanceControlStart)
						xFactor = 1;
					for (float xO = 0f; xO <= 1; xO += .25f) {
						for (float yO = .5f; yO <= 2f; yO += .25f) {
							//move X note width, Y*factor note height
							newPtControl = new Point2D.Double(
									p2d[CONTROL].getX()+xO*xFactor*xOffset,
									p2d[CONTROL].getY()+yO*yFactor*yOffset);
							additionnalCurves.add(new Point2D[] {p2d[START],newPtControl,p2d[END]});
						}
					}
//					//move Y=1 note height
//					newPtControl = new Point2D.Double(p2d[CONTROL].getX(),
//							p2d[CONTROL].getY()+factor*yOffset);
//					additionnalCurves.add(new Point2D[] {p2d[START],newPtControl,p2d[END]});
//					//move Y=1.5 note height
//					newPtControl = new Point2D.Double(p2d[CONTROL].getX(),
//							p2d[CONTROL].getY()+1.5*factor*yOffset);
//					additionnalCurves.add(new Point2D[] {p2d[START],newPtControl,p2d[END]});
//					//move Y=2 note height
//					newPtControl = new Point2D.Double(p2d[CONTROL].getX(),
//							p2d[CONTROL].getY()+2*factor*yOffset);
//					additionnalCurves.add(new Point2D[] {p2d[START],newPtControl,p2d[END]});
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

		//Checks additional curves
		for (Point2D[] p2d : additionnalCurves) {
			//System.out.print((cpt++) + " : ");
			try {
				SlurInfos slurInfos = new SlurInfos(p2d,
					getNoteGlyphesBetween(startNote, endNote),
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
		//System.out.println("best curve n� "+bestCurveIdx+", mark="+bestMark);
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
		Collection<JNoteElementAbstract> jnotes = getNoteGlyphesBetween(start, end);
		JNoteElementAbstract ret = null;
		boolean first = true;
		for (Iterator<JNoteElementAbstract> it = jnotes.iterator(); it.hasNext();) {
			JNoteElementAbstract n = it.next();
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
		Collection<JNoteElementAbstract> jnotes = getNoteGlyphesBetween(start, end);
		JNoteElementAbstract ret = null;
		boolean first = true;
		for (Iterator<JNoteElementAbstract> it = jnotes.iterator(); it.hasNext();) {
			JNoteElementAbstract n = it.next();
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
	private Collection<JNoteElementAbstract> getNoteGlyphesBetween(NoteAbstract start, NoteAbstract end) {
		Collection<JNoteElementAbstract> jnotes = new Vector<JNoteElementAbstract>();
		try {
			Collection<NoteAbstract> notes = m_music.getVoice(start.getReference().getVoice())
					.getNotesBetween(start, end);
			for (NoteAbstract n : notes) {
				jnotes.add((JNoteElementAbstract) getRenditionObjectFor(n));
			}
		} catch (Exception e) {
			//should happen if start and end not in the same voice
			System.out.println(e.getMessage());
		}
		return jnotes;
	}
	
	/**
	 * Returns a new system (not assigned to m_currentSystem)
	 * <ul>
	 * <li>first system is position with top/left first margin
	 * <li>add a JBar if needed to last system/last staff
	 * <li>add system to m_systems
	 * </ul>
	 */
	private JSystem initNewSystem() {
		JSystem sys = new JSystem(cursor, getMetrics(),
				getEngraver(), m_music.getVoices().size());
		if (m_systems.size() == 0) {
			//first system top margin
			cursor.setLocation(
				getBase().getX() + getTemplate().getAttributeSize(
						ScoreAttribute.FIRST_STAFF_LEFT_MARGIN),
				cursor.getY() + getTemplate().getAttributeSize(
						ScoreAttribute.FIRST_STAFF_TOP_MARGIN));
		} else {
			//if needed, add a bar line to previous system's last staff
			JSystem lastSystem = (JSystem) m_systems.lastElement();
			if (!(lastSystem.lastStaffLine().m_staffElements
					.lastElement() instanceof JBar)) {
				lastSystem.lastStaffLine().addElement(
						new JBar(new BarLine(), cursor, getMetrics()));
			}
			//add a space between each system
			cursor.setLocation(
				getBase().getX(),
				lastSystem.getBottomY()
				+ getTemplate().getAttributeSize(
					(m_music.getVoices().size() > 1)
						?ScoreAttribute.SYSTEM_SPACING
						:ScoreAttribute.STAFF_LINES_SPACING));
		}
		sys.setBase(cursor);
		m_systems.add(sys);
		return sys;
	}
	
	/**
	 * Returns a newly created staff line
	 * <ul>
	 * <li>m_currentVoice must be defined to voice's staff to create
	 * <li>add a JBar if needed to last staff
	 * <li>add this staff line to m_currentSystem
	 * <li>TODO check: add clef/key/time change to previousSystem/same voice
	 * </ul>
	 */
	private JStaffLine initNewStaffLine() {
		JStaffLine sl = new JStaffLine(cursor, getMetrics(), getEngraver(), m_currentVoice);
		Object o = m_tablatures.get(m_currentVoice);
		sl.setTablature(o==null ? null : (Tablature)o);
		if (m_currentSystem.getStaffLines().isEmpty()) {
			cursor.setLocation(m_currentSystem.getBase());
		} else /*if (m_staffLines.size() > 0)*/ {
			//if needed, add a bar line to previous staff
			if (!(m_currentSystem.lastStaffLine().m_staffElements
					.lastElement() instanceof JBar)) {
				m_currentSystem.lastStaffLine().addElement(
						new JBar(new BarLine(), cursor, getMetrics()));
			}
			//add a space between each lines
			cursor.setLocation(
				m_currentSystem.getBase().getX(),
				m_currentSystem.getBottomY()
				/*cursor.getY()*/ + getTemplate().getAttributeSize(
						ScoreAttribute.STAFF_LINES_SPACING));
		}
		//store the highest point of the staff area including
		//space for chords and others...
		sl.setBase(cursor);
		sl.setTopY(cursor.getY());
		m_currentSystem.getStaffLines().add(sl);
		
		//add space if tune has chord, part label or tempo
		//Music music = m_tune.getMusicForGraphicalRendition();
		float max = 0;
		if (m_music.getVoice(m_currentVoice).hasChordNames())
			max = Math.max(max, getTemplate().getAttributeSize(
						ScoreAttribute.CHORD_LINE_SPACING));
		if (m_music.hasPartLabel())
			max = Math.max(max, 1.5f *(float) getMetrics().getStaffCharBounds().getHeight());
		if (m_music.hasTempo())
			max = Math.max(max, getTemplate().getAttributeSize(
								ScoreAttribute.TEMPO_LINE_SPACING));
		if (m_music.hasRepeatOrJumps())
			max = Math.max(max, getTemplate().getAttributeSize(
								ScoreAttribute.REPEATS_JUMP_LINE_SPACING));
		
		cursor.setLocation(cursor.getX(), cursor.getY() + max);
		
		cursor.setLocation(cursor.getX(), cursor.getY()+getMetrics().getStaffCharBounds().getHeight());
		
		boolean hasKeyChange = false, hasTimeChange = false, hasClefChange = false;
		if (m_systems.size() > 1) {
			Voice voice = m_music.getVoice(m_currentVoice);
			for (int i = getIndex(m_currentVoice); i < voice.size(); i++) {
				MusicElement s = (MusicElement)voice.elementAt(i);
				if (s instanceof KeySignature) hasKeyChange = true;
				else if (s instanceof TimeSignature) hasTimeChange = true;
				else if (s instanceof Clef) hasClefChange = true;
				else if ((s instanceof PartLabel)
					|| (s instanceof Tempo)) continue;
				else break;//barline, note...
			}
		}
		double width = 0;
		
		//Vector initElements = new Vector();
		o = m_currentKey.get(m_currentVoice);
		KeySignature currKey = (o == null) ? null : (KeySignature)o;
		Clef currClef = (currKey == null) ? Clef.TREBLE() : currKey.getClef();
		m_currentClef.put(m_currentVoice, currClef);
		if (!hasClefChange) {
			JClef jclef = new JClef(cursor, currClef, getMetrics());
			sl.addElement(jclef);
			//initElements.addElement(clef);
			width = jclef.getWidth();
			cursor.setLocation(cursor.getX()+width, cursor.getY());
		}
		if ((currKey!=null) && !hasKeyChange) {
			o = m_previousKey.get(m_currentVoice);
			KeySignature prevKey = (o == null) ? null : (KeySignature)o;
			JKeySignature sk = new JKeySignature(currKey,
					prevKey, cursor, getMetrics());
			try {
				m_previousKey.put(m_currentVoice, (KeySignature) currKey.clone());
			} catch (CloneNotSupportedException never) {
				never.printStackTrace();
			}
			sl.addElement(sk);
			//initElements.addElement(sk);
			width = sk.getWidth();
			int cursorNewLocationX = (int)(cursor.getX() + width + getMetrics().getNotesSpacing());
			cursor.setLocation(cursorNewLocationX, cursor.getY());
		}
		o = m_currentTime.get(m_currentVoice);
		TimeSignature currTime = (o==null) ? null : (TimeSignature)o;
		if ((currTime!=null) && (m_systems.size()==1) && !hasTimeChange) {
			try {
				JTimeSignature sig = new JTimeSignature(currTime, cursor, getMetrics());
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
	protected SlurInfos(Point2D[] p2d, Collection<JNoteElementAbstract> noteGlyphs,
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
		Collection<Line2D.Float> curveSegments = new Vector<Line2D.Float>();
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
		for (Iterator<JNoteElementAbstract> itGlyphs = noteGlyphs.iterator(); itGlyphs.hasNext();) {
			JNoteElementAbstract jnote = itGlyphs.next();
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
				for (Line2D segment : curveSegments) {
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
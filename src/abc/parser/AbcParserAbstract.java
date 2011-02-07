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
package abc.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.parboiled.common.StringUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import abc.instructions.Xcommand;
import abc.notation.Accidental;
import abc.notation.Annotation;
import abc.notation.BarLine;
import abc.notation.Chord;
import abc.notation.Clef;
import abc.notation.DecorableElement;
import abc.notation.Decoration;
import abc.notation.Dynamic;
import abc.notation.EndOfStaffLine;
import abc.notation.Fraction;
import abc.notation.GracingType;
import abc.notation.KeySignature;
import abc.notation.MeasureRepeat;
import abc.notation.MeasureRest;
import abc.notation.MultiNote;
import abc.notation.MultiPartsDefinition;
import abc.notation.Music;
import abc.notation.MusicElement;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.NotesSeparator;
import abc.notation.RepeatBarLine;
import abc.notation.RepeatEnd;
import abc.notation.RepeatedPart;
import abc.notation.RepeatedPartAbstract;
import abc.notation.SlurDefinition;
import abc.notation.Spacer;
import abc.notation.SymbolElement;
import abc.notation.Tempo;
import abc.notation.TieDefinition;
import abc.notation.TimeSignature;
import abc.notation.Tune;
import abc.notation.TuneBook;
import abc.notation.Tuplet;

public abstract class AbcParserAbstract implements AbcTokens {

	private static class DurationDescription {
    	private byte m_dotsNb;
    	private short m_strictDuration;
    	DurationDescription(short noteDuration, byte dotsNb){
    		m_strictDuration = noteDuration;
    		m_dotsNb = dotsNb;
    	}
    	byte countDots() { return m_dotsNb; }
    	short getStrictDuration() { return m_strictDuration; }
    }

	private static final boolean DEBUG = false;
	private static final byte DEBUG_LEVEL = 3;

	public static synchronized void debugTree(AbcNode node) {
		debugTree(node, 0);
	}

	private static void debugTree(AbcNode node, int level) {
		AbcNode parent = node.getParent();
		if ((level >= DEBUG_LEVEL) && !DEBUG && !node.hasError()
			&& (parent!=null) && !parent.hasError()) return;
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.repeat(' ', level * 2));
		sb.append(node.getLabel());
		if (node.hasError())
			sb.append("!E!");
		sb.append(" = '");
		String v = node.getValue();
		if (v.length() > 103) {
			v = v.substring(0, 50) + "..."
				+ v.substring(v.length() - 50)
				+ " (length="+v.length()+")";
		}
		sb.append(v.replaceAll("\n", "<LF>").replaceAll("\r", "<CR>"));
		sb.append("' (" + node.getCharStreamPosition().toString() + ")");
		System.out.println(sb.toString());
		Iterator it = node.getChilds().iterator();
		level++;
		while (it.hasNext()) {
			debugTree((AbcNode) it.next(), level);
		}
	}
	
	private List m_annotations = new ArrayList();

	/** The number of dots inherited from the previous note broken rythm. */
	private byte m_brknRthmDotsCorrection = 0;
	
	private String m_currentVoice = "1";
	
	/** The current default note length. */
	private short m_defaultNoteLength = Note.EIGHTH;

	/** Graces notes to add to next NoteAbstract */
	private List m_graceNotes = new ArrayList();
	
	private byte m_graceNotesType = GracingType.APPOGGIATURA;
	
	private KeySignature m_lastParsedKey = new KeySignature(Note.C,
			KeySignature.MAJOR);

	/**
	 * Keep track of the last parsed note. Used for instance to value the end
	 * slur in case of slur
	 */
	private NoteAbstract m_lastParsedNote = null;

	/** Listeners of this parser. */
	private ArrayList m_listeners = new ArrayList(3);

	/** The music of the current tune. */
	private Music m_music = null;

	private List m_notesStartingTies = new ArrayList();

	private List m_slursDefinitionStack = new ArrayList();

	private List m_symbols = new ArrayList();

	private TimeSignature m_timeSignature = null;

	/** The tune resulting of the last parsing. */
	private AbcTune m_tune = null;

	private int m_tupletInTimeOf = -1;

	private List m_tupletNotes = new ArrayList();

	private int m_tupletNumber = -1;

	private int m_tupletNumberOfNotes = -1;

	private CharStreamPosition m_tupletPosition = null;
	
	/** void constructor */
	protected AbcParserAbstract() {
		//
	}

	/**
	 * Adds a listener to catch events thrown by the parser during tunebook and
	 * tune parsing, or tune book changes
	 * 
	 * @param listener
	 *            Object that implements the
	 *            {@link TuneBookParserListenerInterface},
	 *            {@link TuneParserListenerInterface} or
	 *            {@link TuneBookListenerInterface}.
	 */
	public void addListener(EventListener listener) {
		m_listeners.add(listener);
	}

	/**
	 * Add to current voice, set annotations/chord/decoration if
	 * element accept them (is DecorableElement).
	 * @param me
	 */
	private void addToMusic(MusicElement me) {
		if (me != null) {
			if (me instanceof DecorableElement)
				applySymbolsAndAnnotations((DecorableElement) me);
			if (!(me instanceof NotesSeparator)
					&& !(me instanceof Spacer)) {
				m_graceNotes.clear();
				m_graceNotesType = GracingType.APPOGGIATURA;
				//TODO attach graces to last parsed note ?
			}
			if ((m_tupletNumberOfNotes != -1)
				&& (me instanceof NoteAbstract)) {
				//we are filling a tuplet
				m_tupletNotes.add(me);
				if (m_tupletNotes.size() == m_tupletNumberOfNotes)
					closeTuplet();
			}
			m_music.getVoice(m_currentVoice).addElement(me);
		}
	}

	private void applyBrokenRhythmToNote(NoteAbstract note) {
		if ((note != null) && (m_brknRthmDotsCorrection != 0)) {
			List notes = new ArrayList();
			if (note instanceof Note)
				notes.add(note);
			else { //MultiNote
				short shortest = ((MultiNote) note).getShortestNote().getDuration();
				Iterator it = ((MultiNote) note).getNotesAsVector().iterator();
				while (it.hasNext()) {
					Note note2 = (Note) it.next();
					if (note2.getDuration() == shortest)
						notes.add(note2);
				}
			}
			Iterator it = notes.iterator();
			while (it.hasNext()) {
				Note n = (Note) it.next();
				if (m_brknRthmDotsCorrection > 0) {
					n.setDotted(m_brknRthmDotsCorrection);
				} else {
					short correctedDuration = (short)
							(n.getStrictDuration() / Math.pow(2,
							-m_brknRthmDotsCorrection));
					try {
						n.setStrictDuration(correctedDuration);
					} catch (IllegalArgumentException e) {
						n.setDuration(correctedDuration);
					}
				}
			}
			m_brknRthmDotsCorrection = (byte) -m_brknRthmDotsCorrection;
		} else
			m_brknRthmDotsCorrection = 0;
	}
	
	private void applySymbolsAndAnnotations(DecorableElement decorable) {
		if (m_symbols.isEmpty() && m_annotations.isEmpty())
			return;
		Dynamic dynamic = null;
		Iterator it = m_symbols.iterator();
		while (it.hasNext()) {
			SymbolElement symbol = (SymbolElement) it.next();
			if (symbol instanceof Dynamic) {
				dynamic = (Dynamic) symbol;
				it.remove();
			}
		}
		if (dynamic != null)
			decorable.setDynamic(dynamic);
		if (m_symbols.size() > 0)
			decorable.setDecorations((Decoration[]) m_symbols
				.toArray(new Decoration[0]));
		
		Chord chord = null;
		it = m_annotations.iterator();
		while (it.hasNext()) {
			Annotation ann = (Annotation) it.next();
			if (ann instanceof Chord) {
				chord = (Chord) ann;
				it.remove();
			} else
				decorable.addAnnotation(ann);
		}
		if (chord != null)
			decorable.setChord(chord);
		
		m_symbols.clear();
		m_annotations.clear();
		
	}
	
	private void closeTuplet() {
		if (m_tupletNumberOfNotes != -1) {
			Tuplet t = new Tuplet(m_tupletNumber,
					m_tupletNotes,
					(short)m_tupletInTimeOf,
					m_defaultNoteLength
					);
			t.setCharStreamPosition(m_tupletPosition);
			m_tupletNotes.clear();
			m_tupletNumber = -1;
			m_tupletInTimeOf = -1;
			m_tupletNumberOfNotes = -1;
			m_tupletPosition = null;
		}
	}
	
	/**
	 * Returns the absolute note duration for the specified relative note with
	 * taking into account the default note length.
	 * 
	 * @return The absolute note duration for the specified relative note with
	 *         taking into account the default note length. ONLY Possible values
	 *         are {@link Note#LONG}, {@link Note#BREVE}, {@link Note#WHOLE},
	 *         {@link Note#HALF}, {@link Note#QUARTER}, {@link Note#EIGHTH},
	 *         {@link Note#SIXTEENTH}, {@link Note#THIRTY_SECOND},
	 *         {@link Note#SIXTY_FOURTH}.
	 * 
	 * Usefull to convert notes such as : <IMG
	 * src="./images/dottedcrotchet.gif"/>
	 * @exception IllegalArgumentException
	 *                Thrown if the computing of the absolute note duration is
	 *                impossible.
	 */
	private DurationDescription getAbsoluteDurationFor(Fraction relativeDuration)
			throws IllegalArgumentException {
		// This algorithm is closely linked to the way constants are defined
		// in the Note class !!!
		int absoluteDuration = -1;
		byte dotsNumber = 0;
		if (!Note.isStrictDuration(m_defaultNoteLength))
			throw new IllegalArgumentException("Invalid default note duration "
					+ m_defaultNoteLength);
		absoluteDuration = m_defaultNoteLength
				* relativeDuration.getNumerator();
		absoluteDuration = absoluteDuration / relativeDuration.getDenominator();
		int remainingDurTmp = 0;
		if (absoluteDuration >= 2 * Note.LONG) {
			throw new IllegalArgumentException("Cannot calculate the dots for "
					+ relativeDuration + " with a default duration equals to "
					+ m_defaultNoteLength
					+ " : absolute note length was equal to "
					+ absoluteDuration);
		} else {
			short[] durs = new short[] { Note.LONG, Note.BREVE, Note.WHOLE,
					Note.HALF, Note.QUARTER, Note.EIGHTH, Note.SIXTEENTH,
					Note.THIRTY_SECOND, Note.SIXTY_FOURTH };
			for (int i = 0; i < durs.length; i++) {
				if (absoluteDuration >= durs[i]) {
					remainingDurTmp = absoluteDuration - durs[i];
					absoluteDuration = durs[i];
					break;
				}
			}
		}
		// from here, absDurTemp contains the *real* note duration (without the
		// dots) and remainingDurTemp contains the part that should be expressed
		// using dots.
		if (remainingDurTmp != 0) {
			// valuates the number of dots.
			int durationRepresentedByDots = 0;
			int currentDur = absoluteDuration;
			while (durationRepresentedByDots != remainingDurTmp) {
				dotsNumber++;
				currentDur = currentDur / 2;
				durationRepresentedByDots = durationRepresentedByDots
						+ currentDur;
				if (durationRepresentedByDots > remainingDurTmp)
					throw new IllegalArgumentException(
							"Cannot calculate the dots for " + relativeDuration
									+ " with a default duration equals to "
									+ m_defaultNoteLength
									+ " : absolute note length was equal to "
									+ absoluteDuration);
			}
		}
		return new DurationDescription((short) absoluteDuration, dotsNumber);
	}
	
	protected Note getNoteStartingTieFor(Note aNote) {
		for (int i = m_notesStartingTies.size() - 1; i >= 0; i--) {
			Note n = (Note) m_notesStartingTies.get(i);
			if (n == aNote)
				continue;//do not return the same note
			// This is the end of the tie, the two notes have same height.
			if (n.getHeight() == aNote.getHeight())
				return n;
		}
		return null;
	}
	/**
	 * Parse the {@link Reader} and get the parsing tree by its root
	 * {@link AbcNode}.
	 * 
	 * @param reader
	 * @see #getParseTree(String) the whole content of the reader is read as
	 *      String
	 * @throws IOException
	 */
	protected AbcNode getParseTree(Reader reader) throws IOException {
		StringWriter writer = new StringWriter();
		char[] buffer = new char[32 * 1024];
		int n;
		while ((n = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}
		return getParseTree(writer.toString());
	}
	
	/**
	 * Parse the {@link Reader} and get the parsing tree by its root
	 * {@link AbcNode}.
	 * 
	 * @param abcString
	 */
	protected AbcNode getParseTree(String abcString) {
		long startTime = System.currentTimeMillis();
		AbcGrammar grammar = AbcGrammar.getInstance();
		// AbcGrammar parser = Parboiled.createParser(AbcGrammar.class);
		ParsingResult<?> result = RecoveringParseRunner.run(grammar.AbcFile(),
				abcString);
		if (DEBUG) {
			System.out.println("Parse : "+(System.currentTimeMillis() - startTime) + "ms");
		}
		if (result.hasErrors()) {
			System.err.println("Parse errors: "
					+ result.parseErrors.size());
			/*Iterator it = result.parseErrors.iterator();
			while (it.hasNext()) {
				ParseError pe = (ParseError) it.next();
				System.err.println("- " + pe.getErrorMessage()
						+ "("+pe.getStartIndex()+"->"+pe.getEndIndex()+")");
			}*/
		}

		AbcNode abcRoot = new AbcNode(result.parseTreeRoot, result.inputBuffer,
				result.parseErrors, new AbcInputBuffer(abcString.toCharArray()));
		if (result.hasErrors() || DEBUG)
			debugTree(abcRoot);
		return abcRoot;
	}
	
	/** Inits all attributes that are related to one parsing sequence ONLY. */
	protected void initNewTune() {
		m_brknRthmDotsCorrection = 0;
		m_slursDefinitionStack.clear();
		m_lastParsedNote = null;
		m_notesStartingTies.clear();
		m_defaultNoteLength = Note.EIGHTH;
		m_timeSignature = null;
		m_graceNotes.clear();
		m_graceNotesType = GracingType.APPOGGIATURA;
		m_tune = new AbcTune();
		m_music = m_tune.getMusic();
	}
	
	/**
	 * Instanciate a new AbcTuneBook and transfere TuneBookListeners
	 * to the newly created object
	 */
	protected AbcTuneBook newAbcTuneBook() {
		AbcTuneBook ret = new AbcTuneBook();
		for (int i = 0; i < m_listeners.size(); i++) {
			Object o = m_listeners.get(i);
			if (o instanceof TuneBookListenerInterface)
				ret.addListener((TuneBookListenerInterface) o);
		}
		return ret;
	}
	
	protected void notifyListenersForEmptyTuneBook() {
		for (int i = 0; i < m_listeners.size(); i++) {
			Object o = m_listeners.get(i);
			if (o instanceof TuneBookParserListenerInterface)
				((TuneBookParserListenerInterface) o).emptyTuneBook();
		}
	}
	
	protected void notifyListenersForNoTune() {
		for (int i = 0; i < m_listeners.size(); i++) {
			Object o = m_listeners.get(i);
			if (o instanceof TuneParserListenerInterface)
				((TuneParserListenerInterface) o).noTune();
		}
	}
	
	protected void notifyListenersForTuneBegin() {
		for (int i = 0; i < m_listeners.size(); i++) {
			Object o = m_listeners.get(i);
			if (o instanceof TuneParserListenerInterface)
				((TuneParserListenerInterface) o).tuneBegin();
		}
	}
	
	protected void notifyListenersForTuneBookBegin() {
		for (int i = 0; i < m_listeners.size(); i++) {
			Object o = m_listeners.get(i);
			if (o instanceof TuneBookParserListenerInterface)
				((TuneBookParserListenerInterface) o)
					.tuneBookBegin();
		}
	}
	
	protected void notifyListenersForTuneBookEnd(TuneBook tuneBook,
			AbcNode abcRoot) {
		for (int i = 0; i < m_listeners.size(); i++) {
			Object o = m_listeners.get(i);
			if (o instanceof TuneBookParserListenerInterface)
				((TuneBookParserListenerInterface) o).tuneBookEnd(tuneBook,
						abcRoot);
		}
	}
	
	protected void notifyListenersForTuneEnd(Tune tune, AbcNode abcRoot) {
		for (int i = 0; i < m_listeners.size(); i++) {
			Object o = m_listeners.get(i);
			if (o instanceof TuneParserListenerInterface)
				((TuneParserListenerInterface) o).tuneEnd(tune, abcRoot);
		}
	}
	
	private void parseAbcEol(AbcNode abcEol) {
		if (abcEol != null) {
			if (!abcEol.hasChild(LineContinuation))
				addToMusic(new EndOfStaffLine());
			//HardLineBreak or nothing -> new EndOfStaffLine()
		}
	}
	
	/** If you call directly parseAbcHeader, be sure to
	 * call {@link #initNewTune()} and listener notifiers.
	 * <p>
	 * See {@link TuneParser#parseHeader0(AbcNode)} for example.
	 */
	protected AbcTune parseAbcHeader(AbcNode abcHeader) {
		// X: number
		AbcNode digits = abcHeader.getChild(FieldNumber+"/"+DIGITS);
		if (digits != null)
			m_tune.setReferenceNumber(Integer.parseInt(digits.getValue()));
		// T: titles
		parseTitleFields(abcHeader.getChild(TitleFields));
		parseOtherFields(abcHeader);
		//TODO getChildsInAllGeneration(Xcommand)
		parseFieldKey(abcHeader.getChild(FieldKey));
		return m_tune;
	}
	
	private void parseAbcLine(AbcNode abcLine) {
		Iterator it = abcLine.getChilds().iterator();
		String label;
		while (it.hasNext()) {
			AbcNode node = (AbcNode) it.next();
			label = node.getLabel();
			if (label.equals(Element))
				parseElement(node);
			else if (label.equals(AbcEol)) {
				parseAbcEol(node);
			}
		}
		closeTuplet();
		m_brknRthmDotsCorrection = 0;
	}
	
	private void parseAbcMusic(AbcNode abcMusic) {
		Iterator it = abcMusic.getChilds().iterator();
		String label;
		while (it.hasNext()) {
			AbcNode node = (AbcNode) it.next();
			label = node.getLabel();
			if (label.equals(AbcLine)) {
				parseAbcLine(node);
			} else if (label.equals(TuneField)) {
				parseTuneAndInlineFields(node);
			} else if (label.equals(Xcommand)) {
				parseXcommand(node);
			}
		}
	}
	
	protected AbcTune parseAbcTune(AbcNode abcTune) {
		notifyListenersForTuneBegin();
		initNewTune();
		if (abcTune == null) {
			m_tune.setAbcString("");
			notifyListenersForNoTune();
		} else {
			m_tune.setAbcString(abcTune.getValue());
			AbcNode abcHeader = abcTune.getChild(AbcHeader);
			if (abcHeader != null)
				parseAbcHeader(abcHeader);
			AbcNode abcMusic = abcTune.getChild(AbcMusic);
			if (abcMusic != null)
				parseAbcMusic(abcMusic);
		}
		notifyListenersForTuneEnd(m_tune, abcTune);
		return m_tune;
	}
	
	private Accidental parseAccidental(AbcNode accidental) {
		if (accidental != null)
			return Accidental.convertToAccidental(accidental.getValue());
		else
			return Accidental.NONE;
	}
	
	private void parseAnnotationsOrChord(AbcNode chordOrText) {
		if (chordOrText != null) {
			Iterator it = chordOrText.getChilds().iterator();
			while (it.hasNext()) {
				AbcNode node = (AbcNode) it.next();
				String label = node.getLabel();
				if ((label.equals(Chord)
						|| label.equals(TextExpression))
					&& (node.getValue().length() > 0)) {
					//Chord pc = parseChord(node);
					Chord pc = new Chord(node.getValue());
					if (pc.isChord()) {
						m_annotations.add(pc);
						pc.setCharStreamPosition(node.getCharStreamPosition());
					} else {
						Annotation ann = new Annotation(pc.getText());
						ann.setCharStreamPosition(node.getCharStreamPosition());
						m_annotations.add(ann);
					}
					//m_annotations.add(parseChord(node));					
				}
			//	else if (label.equals("TextExpression")
			//			&& (node.getValue().length() > 0)) {
			//		m_annotations.add(new Annotation(node.getTexTextValue()));
			//	}
			}
		}
	}
	
	private void parseBarlineOrNthRepeat(AbcNode barlineOrNthRepeat) {
		String label = barlineOrNthRepeat.getLabel();
		String value = barlineOrNthRepeat.getValue();
		CharStreamPosition pos = barlineOrNthRepeat.getCharStreamPosition();
		if (label.equals(Barline)) {
			byte[] b = BarLine.convertToBarLine(value);
			if ((b == null) || (b.length == 0)) {
				//not found - repeat bar?
			} else {
				for (int i = 0; i < b.length; i++) {
					BarLine bl = new BarLine(b[i]);
					bl.setCharStreamPosition(pos);
					addToMusic(bl);
				}
			}
		}
		else if (label.equals(NthRepeat)) {
			AbcNode barline = barlineOrNthRepeat.getChild(Barline);
			byte nthBarlineType = BarLine.SIMPLE;
			if (barline != null) {
				nthBarlineType = BarLine.convertToBarLine(barline.getValue())[0];
				//if (barline.getValue().equals(":|"))
				//	nthBarlineType = BarLine.REPEAT_CLOSE;
			}
			AbcNode nthRepeatNum = barlineOrNthRepeat.getChild(NthRepeatNum);
			if (nthRepeatNum != null) {
				List digits = nthRepeatNum.getValuedChilds(DIGITS);
				if (digits.size() > 0) {
					byte[] nums = new byte[digits.size()];
					for (int i = 0; i < nums.length; i++) {
						nums[i] = (byte) ((AbcNode)digits.get(i)).getIntValue();
						if (nums[i] == 1)
							nthBarlineType = BarLine.SIMPLE;
					}
					RepeatBarLine rbl = new RepeatBarLine(nthBarlineType, nums);
					rbl.setCharStreamPosition(pos);
					addToMusic(rbl);
				}
			} else {
				AbcNode nthRepeatText = barlineOrNthRepeat.getChild(NthRepeatText);
				if (nthRepeatText != null) {
					RepeatBarLine rbl = new RepeatBarLine(nthBarlineType,
							nthRepeatText.getValue());
					rbl.setCharStreamPosition(pos);
					addToMusic(rbl);
				} else {
					//really not found, a simple barline
					BarLine bl = new BarLine(nthBarlineType);
					bl.setCharStreamPosition(pos);
					addToMusic(bl);
				}
			}
		}
		else if (label.equals(EndNthRepeat)) {
			RepeatEnd re = new RepeatEnd();
			re.setCharStreamPosition(pos);
			addToMusic(re);
		}
	}
	
	private byte parseBaseNote(AbcNode baseNote) {
		return Note.convertToNoteType(baseNote.getValue());
	}
	
	private void parseBrokenRhythm(AbcNode brokenRhythm) {
		if (brokenRhythm != null) {
			String s = brokenRhythm.getValue();
			byte br = (byte) s.length();
			if (s.charAt(0) == '<')
				m_brknRthmDotsCorrection = (byte) -br;
			else if (s.charAt(0) == '>')
				m_brknRthmDotsCorrection = (byte) br;
			else
				m_brknRthmDotsCorrection = 0;

			// correct last note
			// and invert dot correction for next note
			applyBrokenRhythmToNote(m_lastParsedNote);
		}
	}
	
	private Clef parseClef(AbcNode clef) {
		if (clef != null) {
			AbcNode node;
			String clefName = "";
			if ((node = clef.getChild(ClefName)) != null)
				clefName = node.getValue();
			String clefNote = "";
			if ((node = clef.getChild(ClefNote)) != null)
				clefNote = node.getValue();
			Clef ret = Clef.convertFromNameOrNote(clefName, clefNote);
			ret.setCharStreamPosition(clef.getCharStreamPosition());
			int clefLine = -1;
			if ((node = clef.getChild("ClefLine")) != null) {
				clefLine = Integer.parseInt(node.getValue());
				ret.setLineNumber((byte) clefLine);
			}
			String clefOctave = "";
			if ((node = clef.getChild(ClefOctave)) != null) {
				clefOctave = node.getValue();
				if (clefOctave.equals("+8"))
					ret.setOctaveTransposition((byte) 1);
				else if (clefOctave.equals("-8"))
					ret.setOctaveTransposition((byte) -1);
			}
			Note clefMiddle = null;
			if ((node = clef.getChild(ClefMiddle)) != null) {
				byte height = parseBaseNote(node.getChild(BaseNote));
				byte octave = parseOctave(node.getChild(Octave));
				clefMiddle = new Note(height, Accidental.NONE, octave);
				ret.setMiddleNote(clefMiddle);
			}
			return ret;
		}
		return Clef.TREBLE();
	}
	
	/*private Chord parseChord(AbcNode chord) {
		//chord.getValue() can contain extra chars
		//"non-quote" in grammar, but all childs are valid
		//component for a Chord, concat them and give it to
		//Chord contructor which parse it.
		StringBuffer sb = new StringBuffer();
		Iterator it = chord.getChilds().iterator();
		while (it.hasNext()) {
			AbcNode node = (AbcNode) it.next();
			sb.append(node.getValue());
		}
		return new Chord(sb.toString());
	}*/

	private void parseElement(AbcNode element) {
		AbcNode firstChild = element.getFirstChild();
		if (firstChild != null) {
			String label = firstChild.getLabel();
			if (label.equals(Stem)) {
				addToMusic(parseStem(firstChild));
			}
			else if (label.equals(Barline)
					|| label.equals(NthRepeat)
					|| label.equals(EndNthRepeat)) {
				closeTuplet();
				m_brknRthmDotsCorrection = 0;
				parseBarlineOrNthRepeat(firstChild);
			}
			else if (label.equals(Space)) {
				//separate group of notes
				addToMusic(new NotesSeparator());
			}
			else if (label.equals(GraceNotes)) {
				parseGraceNotes(firstChild);
			}
			else if (label.equals(Gracing)) {
				SymbolElement se = parseGracing(firstChild);
				if (se != null)
					m_symbols.add(se);
			}
			else if (label.equals(ChordOrText)) {
				parseAnnotationsOrChord(firstChild);
			}
			else if (label.equals(Rest)) {
				addToMusic(parseRest(firstChild));
			}
			else if (label.equals(SlurBegin)) {
				parseSlurBegin(firstChild);
			}
			else if (label.equals(SlurEnd)) {
				parseSlurEnd(firstChild);
			}
			else if (label.equals(Tuplet)) {
				closeTuplet();
				parseTuplet(firstChild);
				m_brknRthmDotsCorrection = 0;
			}
			else if (label.equals(BrokenRhythm)) {
				parseBrokenRhythm(firstChild);
			}
			else if (label.equals(MultiMeasureRest)) {
				closeTuplet();
				m_brknRthmDotsCorrection = 0;
				addToMusic(parseMultiMeasureRest(firstChild));
			}
			else if (label.equals(MeasureRepeat)) {
				closeTuplet();
				m_brknRthmDotsCorrection = 0;
				addToMusic(parseMeasureRepeat(firstChild));
			}
			else if (label.equals(InlineField)) {
				closeTuplet();
				m_brknRthmDotsCorrection = 0;
				parseTuneAndInlineFields(firstChild);
			}
			else if (label.equals(UnusedChar)
					|| label.equals(Rollback)) {
				//do nothing
			}
			else {
				System.err.println("Element type "+label+" not handled!");
			}
		}
	}
	
	private void parseFieldArea(AbcNode fieldArea) {
		if (fieldArea != null) {
			m_tune.setArea(parseTexText(fieldArea.getChild(TexText)));
		}
	}
	
	private void parseFieldBook(AbcNode fieldBook) {
		if (fieldBook != null) {
			m_tune.addBook(parseTexText(fieldBook.getChild(TexText)));
		}
	}
	
	private void parseFieldComposer(AbcNode fieldComposer) {
		if (fieldComposer != null) {
			m_tune.addComposer(parseTexText(fieldComposer.getChild(TexText)));
		}
	}

	private void parseFieldDiscography(AbcNode fieldDiscography) {
		if (fieldDiscography != null) {
			m_tune.addDiscography(parseTexText(fieldDiscography.getChild(TexText)));
		}
	}

	private void parseFieldFile(AbcNode fieldFile) {
		if (fieldFile != null) {
			m_tune.setFileURL(parseTexText(fieldFile.getChild(TexText)));
		}
	}
	
	private void parseFieldGroup(AbcNode fieldGroup) {
		if (fieldGroup != null) {
			m_tune.addGroup(parseTexText(fieldGroup.getChild(TexText)));
		}
	}
	
	private void parseFieldHistory(AbcNode fieldHistory) {
		if (fieldHistory != null) {
			m_tune.addHistory(parseTexText(fieldHistory.getChild(TexText)));
		}
	}
	private String parseTexText(AbcNode texTextNode) {
		if (texTextNode != null)
			return texTextNode.getTexTextValue();
		else
			return null;
	}

	private KeySignature parseFieldKey(AbcNode fieldKey) {
		KeySignature ret = null;
		if (fieldKey != null) {
			AbcNode key = fieldKey.getChild(Key);
			if (key != null) {
				if (key.hasChild("HP"))
					//TODO what to do for K:HP? no key, but impose bagpipe notation
					//gracenotes stem down, notes stem up
					ret = null;
				else if (key.hasChild("Hp")) {
					//K:Hp = A major with natural G (only F# and C#)
					ret = new KeySignature(Note.A, KeySignature.MAJOR);
					ret.setAccidental(Note.G, Accidental.NATURAL);
				}
				AbcNode keyDef = key.getChild(KeyDef);
				if (keyDef != null) {
					ret = parseKeyDef(keyDef);
					if (ret == null) {
						try {
							ret = (KeySignature) m_lastParsedKey.clone();
						} catch (CloneNotSupportedException never) {}
					}
				}
				AbcNode clef = key.getChild(_Clef);
				if (clef != null) {
					if (ret == null && m_lastParsedKey != null) {
						try {
							ret = (KeySignature)m_lastParsedKey.clone();
						} catch (CloneNotSupportedException cnse) {
							cnse.printStackTrace();
						}
					}
					if (ret != null)
						ret.setClef(parseClef(clef));
				}
			}
			if (ret != null)
				addToMusic(ret);
		}
		return ret;
	}
	
	private short parseFieldLength(AbcNode fieldLength) {
		if (fieldLength != null) {
			AbcNode noteLengthStrict = fieldLength.getChild(NoteLengthStrict);
			if (noteLengthStrict != null) {
				Fraction fraction = parseNoteLengthStrict(noteLengthStrict);
				try {
					short ret = Note.convertToNoteLengthStrict(fraction);
					if (ret != -1)
						m_defaultNoteLength = ret;
					return ret;
				} catch (IllegalArgumentException e) {
					// will return -1
				}
			}
		}
		return -1;
	}

	private TimeSignature parseFieldMeter(AbcNode fieldMeter) {
		TimeSignature ret = null;
		if (fieldMeter != null) {
			AbcNode timeSignature = fieldMeter.getChild(TimeSignature);
			if (timeSignature != null && timeSignature.hasChilds()) {
				AbcNode tsChild = (AbcNode) timeSignature.getChilds().get(0);
				String label = tsChild.getLabel();
				if (label.equals(MeterNum)) {
					String meterNum = tsChild.getValue();
					String[] fraction = meterNum.split("/");
					String num = fraction[0];
					String denom = fraction[1];
					// num may contain x+y+z
					num = num.replace('.', '+');
					num = num.replaceAll("\\(", "")
							.replaceAll("\\)", "");
					String[] sumOfNumS = num.split("\\+");
					int[] sumOfNum = new int[sumOfNumS.length];
					for (int i = 0; i < sumOfNumS.length; i++) {
						sumOfNum[i] = Integer.parseInt(sumOfNumS[i]);
					}
					int iDenom = Integer.parseInt(denom);
					ret = new TimeSignature(sumOfNum, iDenom);
				} else if (label.equals("C"))
					ret = new TimeSignature(4, 4);
				else if (label.equals("C|"))
					ret = new TimeSignature(2, 2);
				else if (label.equals("none"))
					ret = abc.notation.TimeSignature.SIGNATURE_NONE;
				else if (label.equals(DIGIT))
					ret = new TimeSignature(Integer.parseInt(tsChild
							.getValue()), m_defaultNoteLength);
			}
			if (ret != null) {
				//TODO add timeSig to "master" voice
				ret.setCharStreamPosition(fieldMeter.getCharStreamPosition());
				addToMusic(ret);
				m_defaultNoteLength = ret.getDefaultNoteLength();
				m_timeSignature = ret;
			}
		}
		return ret;
	}
	
	private void parseFieldNotes(AbcNode fieldNotes) {
		if (fieldNotes != null) {
			m_tune.addNotes(parseTexText(fieldNotes.getChild(TexText)));
		}
	}
	
	private void parseFieldOrigin(AbcNode fieldOrigin) {
		if (fieldOrigin != null) {
			m_tune.setOrigin(parseTexText(fieldOrigin.getChild(TexText)));
		}
	}
	
	private void parseFieldPart(AbcNode fieldPart) {
		if (fieldPart != null) {
			AbcNode alpha = fieldPart.getChild(ALPHA);
			if (alpha != null) {
				closeTuplet();
				m_brknRthmDotsCorrection = 0;
				String value = alpha.getValue();
				AbcNode next = fieldPart.getChild(TexText);
				if (next != null)
					value += next.getTexTextValue();
				m_music = m_tune.createPart(value).getMusic();
			}
		}
	}
	
	private MultiPartsDefinition parseFieldParts(AbcNode fieldParts) {
		if (fieldParts != null) {
			AbcNode partsPlayOrder = fieldParts.getChild(PartsPlayOrder);
			if (partsPlayOrder != null) {
				Stack lifo = new Stack();
				Iterator it = partsPlayOrder.getChilds().iterator();
				RepeatedPartAbstract last = new MultiPartsDefinition();
				lifo.push(last);
				String label;
				while (it.hasNext()) {
					AbcNode node = (AbcNode) it.next();
					label = node.getLabel();
					//here we will find "(", ")", ALPHA or DIGITS
					if (label.equals("(")) {
						last = new MultiPartsDefinition();
						lifo.push(last);
					} else if (label.equals(")")) {
						if (lifo.size() >= 2) {
							last = (MultiPartsDefinition) lifo.pop();
							((MultiPartsDefinition) lifo.lastElement())
								.addPart(last);
						} else
							last = null;
					} else if (label.equals(ALPHA)) {
						last = new RepeatedPart(m_tune.createPart(node.getValue()));
						((MultiPartsDefinition) lifo.lastElement())
							.addPart(last);
					} else if (label.equals(DIGITS)) {
						if (last != null)
							last.setNumberOfRepeats((byte)node.getIntValue());
					}
				}
				//if more opened parenth than closed
				while (lifo.size() > 1) {
					last = (MultiPartsDefinition) lifo.pop();
					((MultiPartsDefinition) lifo.lastElement())
					.addPart(last);
				}
				MultiPartsDefinition ret = (MultiPartsDefinition) lifo.pop();
				m_tune.setMultiPartsDefinition(ret);
				return ret;
			}
		}
		return null;
	}
	
	private void parseFieldRhythm(AbcNode fieldRhythm) {
		if (fieldRhythm != null) {
			m_tune.setRhythm(parseTexText(fieldRhythm.getChild(TexText)));
		}
	}
	
	private void parseFieldSource(AbcNode fieldSource) {
		if (fieldSource != null) {
			m_tune.addSource(parseTexText(fieldSource.getChild(TexText)));
		}
	}
	
	private Tempo parseFieldTempo(AbcNode fieldTempo) {
		Tempo ret = null;
		if (fieldTempo != null) {
			AbcNode tempo = fieldTempo.getChild(Tempo);
			if (tempo != null) {
				short digits = tempo.getChild(DIGITS).getShortValue();
				AbcNode noteLengthStrict = tempo.getChild(NoteLengthStrict);
				if (tempo.hasChild("C")) {
					//Q:C [note-length] = DIGITS
					Fraction fraction = parseNoteLength(tempo.getChild(NoteLength));
					ret = new Tempo(
							(short)(m_defaultNoteLength * fraction.floatValue()),
							digits);
				} else if (noteLengthStrict == null) {
					//Q:DIGIT
					ret = new Tempo(m_defaultNoteLength, digits);
				} else {
					//Q:num/denom = DIGITS
					Fraction fraction = parseNoteLengthStrict(noteLengthStrict);
					try {
						ret = new Tempo(
							Note.convertToNoteLengthStrict(fraction),
							digits);
					} catch (IllegalArgumentException e) {
						ret = new Tempo(m_defaultNoteLength, digits);
					}
				}
			}
			if (ret != null) {
				ret.setCharStreamPosition(tempo.getCharStreamPosition());
				addToMusic(ret);
			}
		}
		return ret;
	}
	
	private void parseFieldTitle(AbcNode fieldTitle) {
		if (fieldTitle != null) {
			m_tune.addTitle(parseTexText(fieldTitle.getChild(TexText)));
		}
	}
	
	private void parseFieldTranscription(AbcNode fieldTranscription) {
		if (fieldTranscription != null) {
			m_tune.addTranscriptionNotes(parseTexText(fieldTranscription.getChild(TexText)));
		}
	}
	
	private void parseFieldWords(AbcNode fieldWords) {
		if (fieldWords != null) {
			m_tune.addWords(parseTexText(fieldWords.getChild(TexText)));
		}
	}
	
	private MultiNote parseGraceMultiNote(AbcNode graceMultiNote) {
		if (graceMultiNote != null) {
			Vector notes = new Vector(graceMultiNote.getChilds().size(), 1);
			Iterator it = graceMultiNote.getChilds(GraceNote).iterator();
			while (it.hasNext()) {
				AbcNode node = (AbcNode) it.next();
				notes.add(parseGraceNote(node));
			}
			return new MultiNote(notes);
		}
		return null;
	}
	
	private Note parseGraceNote(AbcNode graceNote) {
		if (graceNote != null) {
			Note n = parsePitch(graceNote.getChild(Pitch));
			Fraction fraction = parseNoteLength(graceNote.getChild(NoteLength));
			if (n != null) {
				fraction.setDenominator(fraction.getDenominator() * 2);
				//if (fraction.floatValue() == 1f) {
				//	n.setStrictDuration((short)(m_defaultNoteLength / 2));
				/*} else*/ {
					try {
						// Try to convert the abc duration into standard note
						// duration
						DurationDescription dd = getAbsoluteDurationFor(fraction);
						n.setStrictDuration(dd.getStrictDuration());
						n.setDotted(dd.countDots());
					} catch (IllegalArgumentException e) {
						// The duration cannot be converted into standard
						// duration
						// So use the absolute duration instead.
						n.setDuration((short) (m_defaultNoteLength * fraction
								.floatValue()));
					}
				}
				return n;
			}
		}
		return null;
	}
	
	private void parseGraceNotes(AbcNode graceNotes) {
		if (graceNotes != null) {
			m_graceNotes.clear();
			m_graceNotesType = graceNotes.hasChild(Acciaccatura)
				? GracingType.ACCIACCATURA : GracingType.APPOGGIATURA;
			Iterator it = graceNotes.getChilds(GraceNoteStem).iterator();
			while (it.hasNext()) {
				AbcNode gnStem = (AbcNode) it.next();
				AbcNode firstChild = gnStem.getFirstChild();
				NoteAbstract note;
				if (firstChild.is(GraceNote))
					note = parseGraceNote(firstChild);
				else //GraceMultiNote
					note = parseGraceMultiNote(firstChild);
				if (note != null)
					m_graceNotes.add(note);
			}
		}
	}
	
	private SymbolElement parseGracing(AbcNode gracing) {
		//Ignore "Position" child node
		AbcNode node = gracing.getChild(LongGracing);
		if (node == null)
			node = gracing;
		byte de = Decoration.convertToType(node.getValue());
		if (de != Decoration.UNKNOWN) {
			Decoration d = new Decoration(de);
			d.setCharStreamPosition(gracing.getCharStreamPosition());
			return d;
		}
		byte dy = Dynamic.convertToType(node.getValue());
		if (dy != Dynamic.UNKNOWN) {
			Dynamic d = new Dynamic(dy);
			d.setCharStreamPosition(gracing.getCharStreamPosition());
			return d;
		}
		return null;
	}
	
	private KeySignature parseKeyDef(AbcNode keyDef) {
		//base note [accidental]
    	//found some abc files with only K: global-accidentals
    	AbcNode keyBaseNote = keyDef.getChild(BaseNote);
		byte baseNote = Note.C;
		if (keyBaseNote != null)
			baseNote = parseBaseNote(keyBaseNote);
		AbcNode keyNoteAcc = keyDef.getChild(KeyNoteAccidental);
		Accidental keyAcc = Accidental.NATURAL;
		if (keyNoteAcc != null) {
			keyAcc = KeySignature.convertToAccidental(keyNoteAcc.getValue());
		}
		Note keyNote = new Note(baseNote, keyAcc);
		
		//[mode]
		byte keyMode = KeySignature.MAJOR;
		AbcNode mode = keyDef.getChild(Mode);
		if (mode != null) {
			AbcNode mode2 = mode.getFirstChild();
			if (mode2 != null) {
				String label = mode2.getLabel();
				if (label.equals(Minor))
					keyMode = KeySignature.MINOR;
				else if (label.equals(Major))
					keyMode = KeySignature.MAJOR;
				else if (label.equals(Lydian))
					keyMode = KeySignature.LYDIAN;
				else if (label.equals(Ionian))
					keyMode = KeySignature.IONIAN;
				else if (label.equals(Mixolydian))
					keyMode = KeySignature.MIXOLYDIAN;
				else if (label.equals(Dorian))
					keyMode = KeySignature.DORIAN;
				else if (label.equals(Aeolian))
					keyMode = KeySignature.AEOLIAN;
				else if (label.equals(Phrygian))
					keyMode = KeySignature.PHRYGIAN;
				else if (label.equals(Locrian))
					keyMode = KeySignature.LOCRIAN;
				else //label.equals(Explicit)
					keyMode = KeySignature.OTHER;
			}
		}
		KeySignature keySig = new KeySignature(keyNote.getStrictHeight(),
				keyNote.getAccidental(), keyMode);
		keySig.setCharStreamPosition(keyDef.getCharStreamPosition());
		
		//*[accidental base-note]
		Iterator globalAccIt = keyDef.getChilds(GlobalAccidental).iterator();
		while (globalAccIt.hasNext()) {
			AbcNode globalAccidental = (AbcNode) globalAccIt.next();
			Accidental gaAcc = parseAccidental(globalAccidental.getChild(_Accidental));
			byte gaBaseNote = Note.getStrictHeight(
					parseBaseNote(globalAccidental.getChild(BaseNote))
					);
			keySig.setAccidental(gaBaseNote, gaAcc);
		}
		return keySig;
	}
	
    private MeasureRepeat parseMeasureRepeat(AbcNode measureRepeat) {
		if (measureRepeat != null) {
			MeasureRepeat mr = new MeasureRepeat(measureRepeat.getValue().length());
			mr.setCharStreamPosition(measureRepeat.getCharStreamPosition());
			return mr;
		}
		return null;
	}
    
    private MeasureRest parseMultiMeasureRest(AbcNode multiMeasureRest) {
		if (multiMeasureRest != null) {
			int nb = 1;
			AbcNode digits = multiMeasureRest.getChild(DIGITS);
			if (digits != null) {
				nb = digits.getIntValue();
			}
			MeasureRest mr = new MeasureRest(nb);
			mr.setCharStreamPosition(multiMeasureRest.getCharStreamPosition());
			return mr;
		}
		return null;
	}
	
	private NoteAbstract parseMultiNote(AbcNode multiNote) {
		if (multiNote != null) {
			AbcNode multiNoteTie = multiNote.getChild(Tie);
			AbcNode multiNoteLength = multiNote.getChild(NoteLength);
			Vector notes = new Vector(multiNote.getChilds().size(), 1);
			Iterator it = multiNote.getChilds(_Note).iterator();
			while (it.hasNext()) {
				AbcNode node = (AbcNode) it.next();
				notes.add(parseNote(node, multiNoteLength, multiNoteTie));
			}
			//somtimes a single note is writed like a multinote
			//[C]... this is bogus but I saw it often
			if (notes.size() > 1) {
				MultiNote mn = new MultiNote(notes);
				mn.setCharStreamPosition(multiNote.getCharStreamPosition());
				return mn;
			} else {
				return (Note) notes.firstElement();
			}
		}
		return null;
	}
	
	private Note parseNote(AbcNode note, AbcNode multiLength, AbcNode multiTie) {
		if (note != null) {
			Note n = parsePitch(note.getChild(Pitch));
			Fraction fraction = parseNoteLength(note.getChild(NoteLength));
			if (multiLength != null)
				fraction.multiplyBy(parseNoteLength(multiLength));
			boolean isTied = (multiTie != null)
					?true
					:note.hasChild(Tie);
			if (n != null) {
				if (fraction.equalsOne()) {
					n.setStrictDuration(m_defaultNoteLength);
				} else {
					try {
						// Try to convert the abc duration into standard note
						// duration
						DurationDescription dd = getAbsoluteDurationFor(fraction);
						n.setStrictDuration(dd.getStrictDuration());
						n.setDotted(dd.countDots());
					} catch (IllegalArgumentException e) {
						// The duration cannot be converted into standard
						// duration
						// So use the absolute duration instead.
						n.setDuration((short) (m_defaultNoteLength * fraction
								.floatValue()));
					}
				}
				if (isTied) {
					TieDefinition tieDef = new TieDefinition();
					AbcNode tieNode = multiTie!=null?multiTie:note.getChild(Tie);
					tieDef.setCharStreamPosition(tieNode.getCharStreamPosition());
					tieDef.setStart(n.getReference());
					n.setTieDefinition(tieDef);
					m_notesStartingTies.add(n);
				}
				Note startTieNote = getNoteStartingTieFor(n);
				if (startTieNote != null) {
					startTieNote.getTieDefinition().setEnd(n.getReference());
					n.setTieDefinition(startTieNote.getTieDefinition());
					m_notesStartingTies.remove(startTieNote);
				}
				return n;
			}
		}
		return null;
	}
	
	private Fraction parseNoteLength(AbcNode noteLength) {
		Fraction fraction = new Fraction(1, 1);
		if (noteLength != null) {
			String s = noteLength.getValue();
			if (!s.equals("")) {
				//is a fraction [x]/[y] or only "/", "//", "///" ?
				boolean isFraction = false;
				char[] c = s.toCharArray();
				for (int i = 0; i < c.length; i++) {
					if (c[i] != '/') {
						isFraction = true;
						break;
					}
				}
				if (isFraction) {
					int pos = s.indexOf('/');
					String num = pos != -1
						? s.substring(0, pos) : s;
					pos = s.lastIndexOf('/');
					String denom = pos != -1
						? s.substring(pos+1) : "1";
					int iNum = num.length()>0 ? Integer.parseInt(num) : 1;
					int iDenom = denom.length()>0 ? Integer.parseInt(denom) : 1;
					//avoid 0-length and divide by zero!
					if ((iNum != 0) && (iDenom != 0)) {
						fraction.setNumerator(iNum);
						fraction.setDenominator(iDenom);
					}
				} else {
					fraction.setDenominator((int) Math.pow(2, c.length));
				}
			}
		}
		return fraction;
	}
	
	private Fraction parseNoteLengthStrict(AbcNode noteLengthStrict) {
		String fraction = noteLengthStrict.getValue();
		String[] numbers = fraction.split("/");
		int num = 1;
		int denom = 1;
		try {
			num = Integer.parseInt(numbers[0]);
		} catch (Exception e) {}
		try {
			denom = Integer.parseInt(numbers[1]);
		} catch (Exception e) {}
		return new Fraction(num, denom);
	}
	
	private byte parseOctave(AbcNode octave) {
		if (octave != null) {
			char[] c = octave.getValue().toCharArray();
			byte ret = 0;
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '\'')
					ret++;
				else if (c[i] == ',')
					ret--;
			}
			return ret;
		}
		return 0;
	}
	
	private void parseOtherFields(AbcNode otherFields) {
		if (otherFields == null)
			return;
		// A: area
		// FIXME v2 A: lyricist ?
		parseFieldArea(otherFields.getChild(FieldArea));
		// B: book
		Iterator it = otherFields.getChilds(FieldBook).iterator();
		while (it.hasNext()) {
			parseFieldBook((AbcNode) it.next());
		}
		// C: composer
		it = otherFields.getChilds(FieldComposer).iterator();
		while (it.hasNext()) {
			parseFieldComposer((AbcNode) it.next());
		}
		// D: discography
		it = otherFields.getChilds(FieldDiscography).iterator();
		while (it.hasNext()) {
			parseFieldDiscography((AbcNode) it.next());
		}
		// F: file
		parseFieldFile(otherFields.getChild(FieldFile));
		// G: group
		it = otherFields.getChilds(FieldGroup).iterator();
		while (it.hasNext()) {
			parseFieldGroup((AbcNode) it.next());
		}
		// H: history
		it = otherFields.getChilds(FieldHistory).iterator();
		while (it.hasNext()) {
			parseFieldHistory((AbcNode) it.next());
		}
		// N: notes
		it = otherFields.getChilds(FieldNotes).iterator();
		while (it.hasNext()) {
			parseFieldNotes((AbcNode) it.next());
		}
		// O: origin
		parseFieldOrigin(otherFields.getChild(FieldOrigin));
		// R: rhythm
		parseFieldRhythm(otherFields.getChild(FieldRhythm));
		// S: source
		it = otherFields.getChilds(FieldSource).iterator();
		while (it.hasNext()) {
			parseFieldSource((AbcNode) it.next());
		}
		// Z: transcription
		it = otherFields.getChilds(FieldTranscription).iterator();
		while (it.hasNext()) {
			parseFieldTranscription((AbcNode) it.next());
		}
		// W: words
		it = otherFields.getChilds(FieldWords).iterator();
		while (it.hasNext()) {
			parseFieldWords((AbcNode) it.next());
		}

		// TODO I: instructions
		// TODO U: FieldUserdefPrint
		// TODO u: FieldUserdefPlay
		// TODO V: voice
		// TODO m: macro

		// M: meter
		parseFieldMeter(otherFields.getChild(FieldMeter));
		// L: length
		parseFieldLength(otherFields.getChild(FieldLength));
		//Q: tempo
		parseFieldTempo(otherFields.getChild(FieldTempo));

		//P: parts
		parseFieldParts(otherFields.getChild(FieldParts));

	}

	private Note parsePitch(AbcNode pitch) {
        Accidental accidental = parseAccidental(pitch.getChild(_Accidental));
        byte height = parseBaseNote(pitch.getChild(BaseNote));
        byte octav = parseOctave(pitch.getChild(Octave));
        Note n = new Note(height, accidental, octav);
        n.setCharStreamPosition(pitch.getCharStreamPosition());
        return n;
	}
	
	private MusicElement parseRest(AbcNode rest) {
		MusicElement ret = null;
		Fraction fraction = parseNoteLength(rest.getChild(NoteLength));
		if (rest.hasChild(NormalRest)) { //z
			ret = new Note(Note.REST);
		} else if (rest.hasChild(InvisibleRest)) { //x
			ret = new Note(Note.REST);
			((Note)ret).setInvisibleRest(true);
		}
		if (ret != null) {
			if (fraction.floatValue() == 1f) {
				((Note)ret).setStrictDuration(m_defaultNoteLength);
			} else {
				try {
					// Try to convert the abc duration into standard note
					// duration
					DurationDescription dd = getAbsoluteDurationFor(fraction);
					((Note)ret).setStrictDuration(dd.getStrictDuration());
					((Note)ret).setDotted(dd.countDots());
				} catch (IllegalArgumentException e) {
					// The duration cannot be converted into standard
					// duration
					// So use the absolute duration instead.
					((Note)ret).setDuration((short) (m_defaultNoteLength * fraction
							.floatValue()));
				}
			}
			applyBrokenRhythmToNote((Note)ret);
			m_brknRthmDotsCorrection = 0;
			m_lastParsedNote = (Note)ret;
			return ret;
		}
		//Spacer
		if (rest.hasChild(InaudibleRest)) { //y
			ret = new Spacer(fraction.floatValue());
		}
		if (ret != null)
			ret.setCharStreamPosition(rest.getCharStreamPosition());
		return ret;
	}
	
	private void parseSlurBegin(AbcNode slurBegin) {
		SlurDefinition sd = new SlurDefinition();
		sd.setCharStreamPosition(slurBegin.getCharStreamPosition());
		m_slursDefinitionStack.add(sd);
	}
	
	private void parseSlurEnd(AbcNode slurEnd) {
		if (!m_slursDefinitionStack.isEmpty()
				&& (m_lastParsedNote != null)) {
			int i = m_slursDefinitionStack.size() - 1;
			while (i >= 0) {
				SlurDefinition slurDef = (SlurDefinition) m_slursDefinitionStack
						.get(i);
				if ((slurDef != null)
					&& (slurDef.getStart() != null)
					&& !slurDef.getStart().equals(
						m_lastParsedNote.getReference())) {
					slurDef.setEnd(m_lastParsedNote.getReference());
					m_slursDefinitionStack.remove(i);
					m_lastParsedNote.addSlurDefinition(slurDef);
					break;
				}
				i--;
			}
		}
	}
	
	private NoteAbstract parseStem(AbcNode stem) {
		NoteAbstract note = null;
		if (stem != null) {
			AbcNode firstChild = stem.getFirstChild();
			if (firstChild != null) {
				String label = firstChild.getLabel();
				if (label.equals(_Note))
					note = parseNote(firstChild, null, null);
				else if (label.equals(_MultiNote))
					note = parseMultiNote(firstChild);
			}
			if (note != null) {
				if (m_graceNotes != null) {
					note.setGracingNotes(m_graceNotes);
					m_graceNotes.clear();
					note.setGracingType(m_graceNotesType);
					m_graceNotesType = GracingType.APPOGGIATURA;
				}

				// slurs
				if (!m_slursDefinitionStack.isEmpty()) {
					note.setPartOfSlur(true);
					int i = m_slursDefinitionStack.size() - 1;
					while (i >= 0) {
						SlurDefinition currentSlurDef =
							(SlurDefinition) m_slursDefinitionStack
								.get(i);
						if (currentSlurDef.getStart() == null) {
							currentSlurDef.setStart(note.getReference());
							note.addSlurDefinition(currentSlurDef);
						}
						i--;
					}
				}
				applyBrokenRhythmToNote(note);
				m_brknRthmDotsCorrection = 0;
				m_lastParsedNote = note;
			}
		}
		return note;
	}
	
	private void parseTitleFields(AbcNode titleFields) {
		if (titleFields != null) {
			List fieldTitle = titleFields.getChilds(FieldTitle);
			Iterator it = fieldTitle.iterator();
			while (it.hasNext()) {
				parseFieldTitle((AbcNode) it.next());
			}
		}
	}
	
	private void parseTuneAndInlineFields(AbcNode field) {
		if (field == null)
			return;
		AbcNode firstChild = field.getFirstChild();
		if (firstChild == null)
			return;
		String label = firstChild.getLabel();
		if (label.endsWith("Part"))
			parseFieldPart(firstChild);
		else if (label.endsWith("Length"))
			parseFieldLength(firstChild);
		else if (label.endsWith("Meter"))
			parseFieldMeter(firstChild);
		else if (label.endsWith("Key"))
			parseFieldKey(firstChild);
		else if (label.endsWith("Tempo"))
			parseFieldTempo(firstChild);
		else if (label.endsWith("Voice")) {
			//TODO parseFieldVoice(firstChild);
		}
		else if (label.endsWith("Lyrics")) {
			//TODO parseFieldLyrics(firstChild);
		}
		else if (label.endsWith("Area"))
			parseFieldArea(firstChild);
		else if (label.endsWith("Book"))
			parseFieldBook(firstChild);
		else if (label.endsWith("Composer"))
			parseFieldComposer(firstChild);
		else if (label.endsWith("Discography"))
			parseFieldDiscography(firstChild);
		else if (label.endsWith("File"))
			parseFieldFile(firstChild);
		else if (label.endsWith("Group"))
			parseFieldFile(firstChild);
		else if (label.endsWith("History"))
			parseFieldHistory(firstChild);
		else if (label.endsWith("Instruction"))
			parseXcommand(firstChild);
		else if (label.endsWith("Notes"))
			parseFieldNotes(firstChild);
		else if (label.endsWith("Origin"))
			parseFieldOrigin(firstChild);
		else if (label.endsWith("Rhythm"))
			parseFieldRhythm(firstChild);
		else if (label.endsWith("Source"))
			parseFieldSource(firstChild);
		else if (label.endsWith("Title"))
			parseFieldTitle(firstChild);
		else if (label.endsWith("Transcription"))
			parseFieldTranscription(firstChild);
		else if (label.endsWith("Words"))
			parseFieldWords(firstChild);
		
	}
	
	protected AbcTuneBook parseTuneBookHeader(AbcNode tbHeader) {
		AbcTuneBook tb = newAbcTuneBook();
		if (tbHeader != null) {
			tb.setAbcHeaderString(tbHeader.getValue());
			//init a new tune to put header informations
			//and instruction into
			initNewTune();
			Iterator it = tbHeader.getChilds().iterator();
			while (it.hasNext()) {
				AbcNode node = (AbcNode) it.next();
				String label = node.getLabel();
				if (label.equals(FileField))
					parseTuneAndInlineFields(node);
				else if (label.equals(Xcommand))
					parseXcommand(node);
			}
			tb.getInstructions().addAll(m_tune.getInstructions());
			tb.setBookInfos(m_tune.getTuneInfos());
		}
		return tb;
	}
	
	private void parseTuplet(AbcNode tuplet) {
		if (tuplet != null) {
			m_tupletPosition = tuplet.getCharStreamPosition();
			Iterator it = tuplet.getChilds().iterator();
			int i = 1;
			while (it.hasNext()) {
				AbcNode node = (AbcNode) it.next();
				if (node.is(":"))
					i++;
				else if (node.getValue().length() > 0){
					int digits = node.getIntValue();
					switch(i) {
					case 1: m_tupletNumber = digits;break;
					case 2: m_tupletInTimeOf = digits;break;
					case 3: m_tupletNumberOfNotes = digits;break;
					}
				}
			}
			if (m_tupletNumberOfNotes <= 1)
				m_tupletNumberOfNotes = m_tupletNumber;
			if (m_tupletInTimeOf == -1) {
				//* The values of the particular tuplets are
				//(to quote the abc specification)
				//(2 	2 notes in the time of 3
				//(3 	3 notes in the time of 2
				//(4 	4 notes in the time of 3
				//(5 	5 notes in the time of n
				//(6 	6 notes in the time of 2
				//(7 	7 notes in the time of n
				//(8 	8 notes in the time of 3
				//(9 	9 notes in the time of n
				//n is 3 in compound time signatures (3/4, 3/8, 9/8 etc),
				//and 2 in simple time signatures (C, 4/4, 2/4 etc.)
				if (m_tupletNumber==2 || m_tupletNumber==4 || m_tupletNumber==8)
					m_tupletInTimeOf = 3;
				else if (m_tupletNumber==3 | m_tupletNumber==6)
					m_tupletInTimeOf = 2;
				else { //5, 7, 9
					if (m_timeSignature != null) {
						m_tupletInTimeOf = m_timeSignature.isCoumpound()?3:2;
					}
				}
			}
		}
	}
	
	private void parseXcommand(AbcNode xcommand) {
		if (xcommand != null) {
			AbcNode xcom = xcommand.getChild(Xcom);
			if (xcom != null) {
				m_tune.addInstruction(
						new Xcommand(xcom.getTexTextValue())
				);
			}
		}
	}

	/**
	 * Removes a listener from this parser.
	 * 
	 * @param listener
	 *            The listener to be removed. Object that implements the
	 *            {@link TuneBookParserListenerInterface},
	 *            {@link TuneParserListenerInterface} or
	 *            {@link TuneBookListenerInterface}.
	 */
	public void removeListener(EventListener listener) {
		m_listeners.remove(listener);
	}

}

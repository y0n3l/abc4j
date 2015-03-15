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

/**
 * This is a convenient interface defining all token constant
 * names.
 * <br>
 * In any class, you can call {@link AbcTokens#AbcFile} or to
 * light up a little the code, you can extend this interface
 * and then call directly {@link #AbcFile}.
 */
public interface AbcTokens {

	/** The whole file, tune or tunebook */
	public static final String AbcFile = "AbcFile";
	/** in tunebooks, headers before the first tune */
	public static final String AbcTuneBookHeader = "AbcTuneBookHeader";
	public static final String FileField = "FileField";
	public static final String TextLine = "TextLine";
	public static final String AbcTune = "AbcTune";
	public static final String WhiteLine = "WhiteLine";
	public static final String AbcHeader = "AbcHeader";
	public static final String FieldNumber = "FieldNumber";
	public static final String TitleFields = "TitleFields";
	public static final String FieldTitle = "FieldTitle";
	public static final String OtherFields = "OtherFields";
	public static final String FieldArea = "FieldArea";
	public static final String FieldBook = "FieldBook";
	public static final String FieldComposer = "FieldComposer";
	public static final String FieldDiscography = "FieldDiscography";
	public static final String FieldFile = "FieldFile";
	public static final String FieldGroup = "FieldGroup";
	public static final String FieldHistory = "FieldHistory";
	public static final String FieldInstruction = "FieldInstruction";
	public static final String FieldLength = "FieldLength";
	public static final String FieldMeter = "FieldMeter";
	public static final String FieldNotes = "FieldNotes";
	public static final String FieldOrigin = "FieldOrigin";
	/** Parts order in AbcHeader */
	public static final String FieldParts = "FieldParts";
	public static final String FieldTempo = "FieldTempo";
	public static final String FieldRhythm = "FieldRhythm";
	public static final String FieldSource = "FieldSource";
	public static final String FieldUserdefPrint = "FieldUserdefPrint";
	public static final String FieldUserdefPlay = "FieldUserdefPlay";
	public static final String FieldVoice = "FieldVoice";
	public static final String FieldWords = "FieldWords";
	public static final String FieldTranscription = "FieldTranscription";
	public static final String FieldMacro = "FieldMacro";
	public static final String FieldKey = "FieldKey";
	public static final String UnusedField = "UnusedField";
	public static final String TimeSignature = "TimeSignature";
	public static final String MeterNum = "MeterNum";
	public static final String Tempo = "Tempo";
	public static final String TempoText = "TempoText";
	public static final String NoteLengthStrict = "NoteLengthStrict";
	public static final String Key = "Key";
	/** A parameter is an additionnal <TT>name=value</TT> after key or clef,
	 * it may contains two childs : {@link #ParameterName} and
	 * {@link #ParameterValue}. */
	public static final String Parameter = "Parameter";
	public static final String ParameterName = "ParameterName";
	public static final String ParameterValue = "ParameterValue";
	public static final String BaseNote = "BaseNote";
	public static final String Octave = "Octave";
	public static final String KeyDef = "KeyDef";
	public static final String KeyNoteAccidental = "KeyNoteAccidental";
	public static final String Mode = "Mode";
	public static final String Minor = "Minor";
	public static final String Major = "Major";
	public static final String Lydian = "Lydian";
	public static final String Ionian = "Ionian";
	public static final String Mixolydian = "Mixolydian";
	public static final String Dorian = "Dorian";
	public static final String Aeolian = "Aeolian";
	public static final String Phrygian = "Phrygian";
	public static final String Locrian = "Locrian";
	public static final String Explicit = "Explicit";
	public static final String GlobalAccidental = "GlobalAccidental";
	public static final String PartsPlayOrder = "PartsPlayOrder";
	public static final String VoiceOther = "VoiceOther";
	public static final String Voice = "Voice";
	public static final String VoiceNumber = "VoiceNumber";
	public static final String VoiceName = "VoiceName";
	public static final String VoiceSubname = "VoiceSubname";
	public static final String VoiceTranspose = "VoiceTranspose";
	public static final String VoiceMerge = "VoiceMerge";
	public static final String VoiceStems = "VoiceStems";
	public static final String VoiceStaves = "VoiceStaves";
	public static final String VoiceBracket = "VoiceBracket";
	public static final String _Clef = "Clef";
	public static final String ClefNote = "ClefNote";
	public static final String ClefName = "ClefName";
	public static final String ClefLine = "ClefLine";
	public static final String ClefOctave = "ClefOctave";
	public static final String ClefMiddle = "ClefMiddle";
	public static final String ClefStafflines = "ClefStafflines";
	public static final String ClefTranspose = "ClefTranspose";
	public static final String Userdef = "Userdef";
	public static final String HeaderEol = "HeaderEol";
	public static final String AbcMusic = "AbcMusic";
	public static final String AbcLine = "AbcLine";
	public static final String Barline = "Barline";
	public static final String InvisibleBarline = "InvisibleBarline";
	public static final String TripleBarline = "TripleBarline";
	public static final String DashedBarline = "DashedBarline";
	public static final String Space = "Space";
	public static final String Element = "Element";
	public static final String Stem = "Stem";
	public static final String _Note = "Note";
	public static final String _MultiNote = "MultiNote";
	public static final String Pitch = "Pitch";
	public static final String Rest = "Rest";
	public static final String NormalRest = "NormalRest";
	public static final String InvisibleRest = "InvisibleRest";
	public static final String InaudibleRest = "InaudibleRest";
	public static final String _Accidental = "Accidental";
	public static final String NoteLength = "NoteLength";
	public static final String Tie = "Tie";
	public static final String BrokenRhythm = "BrokenRhythm";
	public static final String BElem = "BElem";
	public static final String Tuplet = "Tuplet";
	public static final String TElem = "TElem";
	public static final String Position = "Position";
	public static final String Gracing = "Gracing";
	public static final String UserdefSymbol = "UserdefSymbol";
	public static final String GraceNotes = "GraceNotes";
	public static final String GraceMultiNote = "GraceMultiNote";
	public static final String GraceNoteStem = "GraceNoteStem";
	public static final String GraceNote = "GraceNote";
	public static final String Acciaccatura = "Acciaccatura";
	public static final String ChordOrText = "ChordOrText";
	public static final String Chord = "Chord";
	public static final String ChordBass = "ChordBass";
	public static final String ChordAccidental = "ChordAccidental";
	public static final String ChordType = "ChordType";
	public static final String Text = "Text";
	public static final String TextExpression = "TextExpression";
	public static final String NonQuote = "NonQuote";
	public static final String NonQuoteOneTextLine = "NonQuoteOneTextLine";
	public static final String ChordOrTextNewline = "ChordOrTextNewline";
	public static final String SlurBegin = "SlurBegin";
	public static final String SlurEnd = "SlurEnd";
	public static final String Rollback = "Rollback";
	public static final String MeasureRepeat = "MeasureRepeat";
	public static final String MultiMeasureRest = "MultiMeasureRest";
	public static final String NthRepeat = "NthRepeat";
	public static final String NthRepeatNum = "NthRepeatNum";
	public static final String NthRepeatText = "NthRepeatText";
	public static final String EndNthRepeat = "EndNthRepeat";
	public static final String UnusedChar = "UnusedChar";
	public static final String InlineField = "InlineField";
	public static final String UnusedFieldLetter = "UnusedFieldLetter";
	public static final String IUnusedField = "IUnusedField";
	public static final String IfieldArea = "IfieldArea";
	public static final String IfieldBook = "IfieldBook";
	public static final String IfieldComposer = "IfieldComposer";
	public static final String IfieldDiscography = "IfieldDiscography";
	public static final String IfieldGroup = "IfieldGroup";
	public static final String IfieldHistory = "IfieldHistory";
	public static final String IfieldInstruction = "IfieldInstruction";
	public static final String IfieldLength = "IfieldLength";
	public static final String IfieldMeter = "IfieldMeter";
	public static final String IfieldNotes = "IfieldNotes";
	public static final String IfieldOrigin = "IfieldOrigin";
	public static final String IfieldPart = "IfieldPart";
	public static final String IfieldTempo = "IfieldTempo";
	public static final String IfieldRhythm = "IfieldRhythm";
	public static final String IfieldSource = "IfieldSource";
	public static final String IfieldTitle = "IfieldTitle";
	public static final String IfieldUserdefPlay = "IfieldUserdefPlay";
	public static final String IfieldUserdefPrint = "IfieldUserdefPrint";
	public static final String IfieldVoice = "IfieldVoice";
	public static final String IfieldWords = "IfieldWords";
	public static final String IfieldLyrics = "IfieldLyrics";
	public static final String IfieldTranscription = "IfieldTranscription";
	public static final String IfieldKey = "IfieldKey";
	public static final String IUnusedIfield = "IUnusedIfield";
	public static final String NonBracketChar = "NonBracketChar";
	public static final String AbcEol = "AbcEol";
	public static final String LineContinuation = "LineContinuation";
	public static final String HardLineBreak = "HardLineBreak";
	public static final String TuneField = "TuneField";
	public static final String FieldPart = "FieldPart";
	public static final String FieldLyrics = "FieldLyrics";
	public static final String LongGracing = "LongGracing";
	public static final String GracingGrace = "GracingGrace";
	public static final String GracingVol = "GracingVol";
	public static final String GracingStyle = "GracingStyle";
	public static final String GracingLen = "GracingLen";
	public static final String GracingFinger = "GracingFinger";
	public static final String GracingPhrase = "GracingPhrase";
	public static final String GracingExec = "GracingExec";
	public static final String GracingOther = "GracingOther";
	public static final String CommentText = "CommentText";
	public static final String Comment = "Comment";
	public static final String NonCommentChar = "NonCommentChar";
	public static final String Xcommand = "Xcommand";
	public static final String Xcom = "Xcom";
	public static final String XcomStaff = "XcomStaff";
	public static final String XcomStaffbreak = "XcomStaffbreak";
	public static final String XcomMulticol = "XcomMulticol";
	public static final String XcomStaves = "XcomStaves";
	public static final String StaveVoice = "StaveVoice";
	public static final String BracketedVoice = "BracketedVoice";
	public static final String BracedVoice = "BracedVoice";
	public static final String ParenVoice = "ParenVoice";
	public static final String SingleVoice = "SingleVoice";
	public static final String BarStaves = "BarStaves";
	public static final String XcomIndent = "XcomIndent";
	public static final String XcomMeasurenb = "XcomMeasurenb";
	public static final String XcomText = "XcomText";
	public static final String XcomTextline = "XcomTextline";
	public static final String XcomTextcenter = "XcomTextcenter";
	public static final String XcomTextblock = "XcomTextblock";
	public static final String TextblockParam = "TextblockParam";
	public static final String XcomLayout = "XcomLayout";
	public static final String XcomSep = "XcomSep";
	public static final String XcomVskip = "XcomVskip";
	public static final String XcomNewpage = "XcomNewpage";
	public static final String XcomMargins = "XcomMargins";
	public static final String XcomMidi = "XcomMidi";
	public static final String MidiChannel = "MidiChannel";
	public static final String MidiProgram = "MidiProgram";
	public static final String MidiChannelNumber = "MidiChannelNumber";
	public static final String MidiProgramNumber = "MidiProgramNumber";
	public static final String MidiTranspose = "MidiTranspose";
	public static final String XcomOther = "XcomOther";
	public static final String Boolean = "Boolean";
	public static final String XcomNumber = "XcomNumber";
	public static final String XcomUnit = "XcomUnit";
	public static final String Lyrics = "Lyrics";
	public static final String LyricsIfield = "LyricsIfield";
	public static final String LyricsSyllable = "LyricsSyllable";
	public static final String LyricsChar = "LyricsChar";
	public static final String LyricsSyllableBreak = "LyricsSyllableBreak";
	public static final String LyricsNextBar = "LyricsNextBar";
	public static final String LyricsHold = "LyricsHold";
	public static final String LyricsSkipNote = "LyricsSkipNote";
	public static final String LyricsNbsp = "LyricsNbsp";
	public static final String LyricsDash = "LyricsDash";
	public static final String TexText = "TexText";
	public static final String TexEscape = "TexEscape";
	public static final String Tex = "Tex";
	public static final String Eols = "Eols";
	public static final String ALPHA = "ALPHA";
	public static final String CR = "CR";
	public static final String CRLF = "CRLF";
	public static final String LF = "LF";
	public static final String DIGIT = "DIGIT";
	public static final String DQUOTE = "DQUOTE";
	public static final String HTAB = "HTAB";
	public static final String SP = "SP";
	public static final String VCHAR = "VCHAR";
	public static final String WSP = "WSP";
	public static final String DIGITS = "DIGITS";
	public static final String ALPHAS = "ALPHAS";
	public static final String WSPS = "WSPS";
	public static final String ALPHASandDIGITS = "ALPHASandDIGITS";
}

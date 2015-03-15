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

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

@SuppressWarnings("rawtypes")
@BuildParseTree
public class AbcGrammar extends BaseParser implements AbcTokens {

	private static AbcGrammar instance = null;
	
	protected static synchronized AbcGrammar getInstance() {
		if (instance == null) {
			instance = new AbcGrammar();
		}
		return instance;
	}
	
	private AbcGrammar() {}
	
	/**
	 * abc-file ::= *(abc-tune / comment / xcommand / file-field / text-line /
	 * tex)
	 */
	public Rule AbcFile() {
		//return ZeroOrMore(FirstOfS(AbcTune(), Comment(), Xcommand(),
		//		FileField(), TextLine(), Tex())).label("AbcFile");
		return Sequence(
			ZeroOrMore(
				FirstOfS(Eols(),
					Comment(), Xcommand(), Tex(),
					FileField(), TextLine() 
				)).label(AbcTuneBookHeader),
			AbcTune(),
			ZeroOrMoreS(
				//SequenceS(
				//	ZeroOrMoreS(Comment()),
				//	OptionalS(Eols()),
					//FirstOf(EOI, AbcTune())
					AbcTune()
				//)
			)
		).label(AbcFile);
	}

	/**
	 * Default values for the whole file - all fields except number, title and
	 * voice
	 * 
	 * file-field ::= field-area / field-book / field-composer /
	 * field-discography / field-file / field-group / field-history /
	 * field-length / field-meter / field-notes / field-origin / field-parts /
	 * field-tempo / field-rhythm / field-source / field-userdef-print /
	 * field-userdef-play / field-words / field-transcription / field-key /
	 * unused-field
	 */
	Rule FileField() {
		return FirstOf(FieldArea(), FieldBook(), FieldComposer(),
				FieldDiscography(), FieldFile(), FieldGroup(),
				FieldHistory(), FieldNotes(), FieldOrigin(),
				FieldRhythm(), FieldSource(), FieldUserdefPrint(),
				FieldUserdefPlay(), FieldTranscription(),
				UnusedField()
				//FieldLength(), FieldMeter(), FieldParts(),
				//FieldTempo(), FieldWords(), FieldKey()
			).label(FileField);
	}
	
	/**
	 * Free text between tunes. This is a catch-all rule - check for fields first!
	 * <p>
	 * text-line ::= [non-comment-char tex-text] eol
	 */
	Rule TextLine() {
		//return Sequence(Optional(NonCommentChar(), TexText()),
		//		Eol()).label("TextLine");
		return Sequence(NonCommentChar(),
				FirstOf(WSP(), ALPHA(), DIGIT()),//not a ":"
				TexText(),
				Eol()
			).label(TextLine).suppressSubnodes();
	}
	
	/** abc-tune ::= abc-header abc-music eol */
	public Rule AbcTune() {
		return Sequence(
			AbcHeader(),
			AbcMusic(),
			FirstOf(WhiteLines(), Eols(), EOI).suppressNode()
		).label(AbcTune);
	}
	Rule WhiteLines() {
		return OneOrMore(WSPS(), Eol()).suppressSubnodes()
		.label(WhiteLine);
	}
	
	/**
	 * abc-header ::= [field-number] title-fields *other-field field-key<p>
	 * Note that field-number is optional.
	 */
	Rule AbcHeader() {
		return FirstOf(
				SequenceS(
						OptionalS(FieldNumber()),
						OptionalS(TitleFields()),
						ZeroOrMoreS(OtherFields()),
						FieldKey()),
				SequenceS(
						OptionalS(FieldNumber()),
						TitleFields(),
						ZeroOrMoreS(OtherFields()),
						OptionalS(FieldKey())),
				SequenceS(
					FieldNumber(),
					OptionalS(TitleFields()),
					ZeroOrMoreS(OtherFields()),
					OptionalS(FieldKey()))
			)
			.label(AbcHeader);
	}
	
	/**
	 * field-number ::= %x58.3A *WSP 1*DIGIT header-eol<p>
	 * <tt>X:</tt>
	 */
	Rule FieldNumber() {
		return Sequence(
				ZeroOrMoreS(FirstOfS(Eols().suppressNode(),
								Comment(), Xcommand(), Tex())),
				String("X:"),
				ZeroOrMore(WSP()).suppressNode(),
				DIGITS(),
				HeaderEol()
			).label(FieldNumber);
	}
	
	/**
	 * title-fields ::= *(comment / xcommand / tex) 1*(field-title *(comment / xcommand / tex))
	 */
	Rule TitleFields() {
		return Sequence(
			ZeroOrMoreS(FirstOfS(Comment(), Xcommand(), Tex())),
			OneOrMoreS(
				SequenceS(
					FieldTitle(),
					ZeroOrMoreS(FirstOfS(Comment(), Xcommand(), Tex()))
				)
			)
		).label(TitleFields);
	}
	
	/**
	 * field-title ::= %x54.3A *WSP tex-text header-eol<p>
	 * <tt>T:</tt>
	 */
	Rule FieldTitle() {
		return Sequence(String("T:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(),
				HeaderEol()
				//Eol().suppressNode()
			).label(FieldTitle);
	}
	
	/**
	 * other-field ::= field-area / field-book / field-composer /
	 * field-discography / field-file / field-group / field-history /
	 * field-length / field-meter / field-notes / field-origin / field-parts /
	 * field-tempo / field-rhythm / field-source / field-userdef-print /
	 * field-userdef-play / field-voice / field-words / field-transcription /
	 * field-macro / unused-field / comment / xcommand / tex
	 */
	Rule OtherFields() {
		//FIXME v2 A: lyricist
		return FirstOfS(
				FieldArea(), FieldBook(), FieldComposer(),
				FieldDiscography(), FieldFile(), FieldGroup(), FieldHistory(),
				FieldLength(), FieldMeter(), FieldNotes(), FieldOrigin(), FieldParts(),
				FieldTempo(), FieldRhythm(), FieldSource(),
				FieldTitle(), //if 2 T: are separated by something else
				FieldUserdefPrint(),
				FieldUserdefPlay(), FieldVoice(), FieldWords(), FieldTranscription(),
				FieldMacro(), UnusedField(), Comment(), Xcommand(), Tex()
		).label(OtherFields);
	}
	
	/**
	 * field-area ::= %x41.3A *WSP tex-text header-eol<p>
	 * <tt>A:</tt>
	 */
	Rule FieldArea() {
		return Sequence(String("A:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldArea);
	}
	/**
	 * field-book ::= %x42.3A *WSP tex-text header-eol<p>
	 * <tt>B:</tt>
	 */
	Rule FieldBook() {
		return Sequence(String("B:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldBook);
	}
	/**
	 * field-composer ::= %x43.3A *WSP tex-text header-eol<p>
	 * <tt>C:</tt>
	 */
	Rule FieldComposer() {
		return Sequence(String("C:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldComposer);
	}
	/**
	 * field-discography ::= %x44.3A *WSP tex-text header-eol<p>
	 * <tt>D:</tt>
	 */
	Rule FieldDiscography() {
		return Sequence(String("D:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldDiscography);
	}
	/**
	 * field-file ::= %x46.3A *WSP tex-text header-eol<p>
	 * <tt>F:</tt>
	 */
	Rule FieldFile() {
		return Sequence(String("F:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldFile);
	}
	/**
	 * field-group ::= %x47.3A *WSP tex-text header-eol<p>
	 * <tt>G:</tt>
	 */
	Rule FieldGroup() {
		return Sequence(String("G:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldGroup);
	}
	/**
	 * field-history ::= %x48.3A *WSP 1*(tex-text header-eol)<p>
	 * <tt>H:</tt>
	 * field contents may extend over many lines, which is deprecated (maybe not allowed any longer?)
	 */
	Rule FieldHistory() {
		return Sequence(String("H:"),
				ZeroOrMore(WSP()).suppressNode(),
				/*OneOrMoreS(*/TexText(), HeaderEol()/*)*/
			).label(FieldHistory);
	}
	Rule FieldInstruction() {
		return Sequence(String("I:"),
				ZeroOrMore(WSP()).suppressNode(),
				Xcom(), HeaderEol()
			).label(FieldInstruction);
	}
	/**
	 * Default note length
	 * 
	 * field-length ::= %x4C.3A *WSP note-length-strict header-eol<p>
	 * <tt>L:</tt>
	 */
	Rule FieldLength() {
		return Sequence(String("L:"),
				ZeroOrMore(WSP()).suppressNode(),
				NoteLengthStrict(), HeaderEol()).label(FieldLength);
	}
	/**
	 * field-meter ::= %x4D.3A *WSP time-signature header-eol<p>
	 * <tt>M:</tt>
	 */
	Rule FieldMeter() {
		return Sequence(String("M:"),
				ZeroOrMore(WSP()).suppressNode(),
				OptionalS(TimeSignature()), HeaderEol()).label(FieldMeter);
	}
	/**
	 * field-notes ::= %x4E.3A *WSP tex-text header-eol<p>
	 * <tt>N:</tt>
	 */
	Rule FieldNotes() {
		return Sequence(String("N:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldNotes);
	}
	/**
	 * field-origin ::= %x4F.3A *WSP tex-text header-eol<p>
	 * <tt>O:</tt>
	 */
	Rule FieldOrigin() {
		return Sequence(String("O:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldOrigin);
	}
	/**
	 * In header defines in which order parts should be played
	 * 
	 * field-parts ::= %x50.3A *WSP parts-play-order header-eol<p>
	 * <tt>P:</tt>
	 */
	Rule FieldParts() {
		return Sequence(String("P:"),
				ZeroOrMore(WSP()).suppressNode(),
				PartsPlayOrder(), HeaderEol()).label(FieldParts);
	}
	/**
	 * field-tempo ::= %x51.3A *WSP tempo header-eol<p>
	 * <tt>Q:</tt>
	 */
	Rule FieldTempo() {
		return Sequence(
				String("Q:"),
				ZeroOrMore(WSP()).suppressNode(),
				FirstOfS(Tempo(), TempoText()),
				ZeroOrMore(WSP()).suppressNode(),
				OptionalS(FirstOfS(Tempo(), TempoText())),
				HeaderEol()).label(FieldTempo);
	}
	/**
	 * field-rhythm ::= %x52.3A *WSP tex-text header-eol<p>
	 * <tt>R:</tt>
	 */
	Rule FieldRhythm() {
		return Sequence(String("R:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldRhythm);
	}
	/**
	 * field-source ::= %x53.3A *WSP tex-text header-eol<p>
	 * <tt>S:</tt>
	 */
	Rule FieldSource() {
		return Sequence(String("S:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldSource);
	}
	/**
	 * field-userdef-print ::= %x55.3A *WSP userdef header-eol<p>
	 * <tt>U:</tt>
	 */
	Rule FieldUserdefPrint() {
		return Sequence(String("U:"),
				ZeroOrMore(WSP()).suppressNode(),
				Userdef(), HeaderEol()).label(FieldUserdefPrint);
	}
	/**
	 * field-userdef-play ::= %x75.3A *WSP userdef header-eol<p>
	 * <tt>u:</tt>
	 */
	Rule FieldUserdefPlay() {
		return Sequence(String("u:"),
				ZeroOrMore(WSP()).suppressNode(),
				Userdef(), HeaderEol()).label(FieldUserdefPlay);
	}
	/**
	 * field-voice ::= %x56.3A *WSP voice header-eol<p>
	 * <tt>V:</tt>
	 * 
	 * options should be better defined
	 */
	Rule FieldVoice() {
		return Sequence(String("V:"),
				ZeroOrMore(WSP()).suppressNode(),
				Voice(), HeaderEol()).label(FieldVoice);
	}
	/**
	 * Unformatted words, printed after the tune
	 * 
	 * field-words ::= %x57.3A *WSP tex-text header-eol<p>
	 * <tt>W:</tt>
	 */
	Rule FieldWords() {
		return Sequence(String("W:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldWords);
	}
	/**
	 * field-transcription ::= %x5A.3A *WSP tex-text header-eol<p>
	 * <tt>Z:</tt>
	 */
	Rule FieldTranscription() {
		return Sequence(String("Z:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(FieldTranscription);
	}
	/**
	 * BarFly-style macros - do be defined better
	 * 
	 * field-macro ::= %x6D.3A *WSP 1*(WSP / VCHAR) header-eol<p>
	 * <tt>m:</tt>
	 */
	Rule FieldMacro() {
		return Sequence(String("m:"),
				ZeroOrMore(WSP()).suppressNode(),
				OneOrMore(FirstOf(WSP(), VCHAR()))
					.label("Macro").suppressSubnodes(),
				HeaderEol()
				).label(FieldMacro);
	}
	/**
	 * field-key ::= %x4B.3A *WSP key header-eol<p>
	 * <tt>K:</tt>
	 */
	Rule FieldKey() {
		return Sequence(String("K:"),
				ZeroOrMore(WSP()).suppressNode(),
				Key(), HeaderEol()).label(FieldKey);
	}
	/**
	 * ignore - but for backward and forward compatibility
	 * 
	 * unused-field ::= (%x45 / %x49 / %4A / %59 / %61-6c / %6e-%74 / %76 / %78-7A) %3A *(WSP / VCHAR) eol<p>
	 * <tt>E: I: J: Y: a:-l: n:-t: v: x: y: z:</tt>
	 */
	Rule UnusedField() {
		//TODO remove s:symbol-line
		return Sequence(
				AnyOf("EJYabcdefghijklnopqrstvxyz")
				/*FirstOf('E', 'I', 'J', 'Y',
						CharRange('a', 'l'),
						CharRange('n', 't'),
						'v', 'x', 'y', 'z')*/
					.label("UnusedFieldLetter").suppressSubnodes(),
				String(":"),
				ZeroOrMore(WSP()).suppressNode(),
				TexText(), HeaderEol()).label(UnusedField);
	}
	
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * time-signature ::= %x43 / %x43.7C / "none" / meter-num / 1*DIGIT<p>
	 * <tt>C</tt>, <tt>C|<tt>, <tt>none</tt> or numeric
	 */
	Rule TimeSignature() {
		return FirstOf(
				String("C|"),
				String("C"),
				IgnoreCase("none"),
				MeterNum(),
				DIGIT()
		).label(TimeSignature);
	}
	/**
	 * meter-num ::= (1*DIGIT *("+" 1*DIGIT) "/" 1*DIGIT) [1*SP meter-num]<p> 
	 * e.g. 2/4 , 6/8 , 2+2+3/16 , 11/8 , 2/4 3/4 , 2+2+3/8 2+2+3+2+2+2/8
	 */
	Rule MeterNum() {
		return Sequence(
				MeterNum2(),
				OptionalS(
					Sequence(
						OneOrMore(SP()),
						MeterNum2()
					)
				)
		).label(MeterNum).suppressSubnodes();
	}
	Rule MeterNum2() {
		return Sequence(
				OptionalS(String("(")),
				DIGITS(),
				ZeroOrMore(Sequence(AnyOf("+."), DIGITS())),
				OptionalS(String(")")),
				String("/"),
				DIGITS());
	}
	
	/**
	 * tempo ::= (note-length-strict "=" 1*DIGIT) / (%x43 [note-length] "=" 1*DIGIT) / 1*DIGIT<p>
	 * 1*DIGIT is deprecated, kept for compatibility
	 */
	Rule Tempo() {
		//TODO Q:2/16 3/16=60
		//Q: "Brzo" 4/8 3/8 4/8=70 "kopanica"
		//Q:"faster"
		return FirstOf(
				SequenceS(NoteLengthStrict(),
					OptionalS(WSPS()),
					String("="),
					OptionalS(WSPS()),
					DIGITS(),
					OptionalS(WSPS()),
					OptionalS(TempoText())
				),
				SequenceS(
					String("C"),
					OptionalS(NoteLength()),
					OptionalS(WSPS()),
					String("="),
					OptionalS(WSPS()),
					DIGITS(),
					OptionalS(WSPS()),
					OptionalS(TempoText())
				),
				DIGITS()
		).label(Tempo);
	}
	Rule TempoText() {
		return Sequence(
				OptionalS(String("\"")),
				ZeroOrMore(NonQuote()),
				OptionalS(String("\""))
			).label(TempoText);
	}
	/**
	 * note-length-strict ::= 1*DIGIT "/" 1*DIGIT
	 */
	Rule NoteLengthStrict() {
		return Sequence(DIGITS(), '/', DIGITS())
			.label(NoteLengthStrict).suppressSubnodes();
	}
	/**
	 * key ::= (key-def [1*WSP clef]) / clef / "HP" / "Hp"
	 */
	Rule Key() {
		return FirstOf(
			SequenceS(KeyDef(),
					OptionalS(SequenceS(
							OptionalS(suppr(WSPS())),
							Clef()
					)),
					OptionalS(Parameters())
			),
			Clef(),
			String("HP"),
			String("Hp")
		).label(Key);
	}
	Rule Parameters() {
		return SequenceS(
			OptionalS(WSPS()),
			Parameter(),
			ZeroOrMoreS(SequenceS(WSPS(), Parameter()))
		);
	}
	Rule Parameter() {
		return Sequence(
			ALPHAS().label(ParameterName),
			String("="),
			OneOrMore(FirstOf("-", ALPHA(), DIGIT()))
				.label(ParameterValue).suppressSubnodes()
		).label(Parameter);
	}
	Rule Parameter(String s) {
		return Sequence(
				String(s),
				String("="),
				OneOrMore(FirstOf("-", ALPHA(), DIGIT()))
					.label(ParameterValue).suppressSubnodes()
			).label(Parameter);
	}
	
	/**
	 * key-def ::= base-note [key-note-accidental] [mode] *(1*WSP global-accidental)
	 */
	Rule KeyDef() {
		return FirstOf(
			//found some abc files with only global accidental
			//assuming it's K:C global accidentals
			OneOrMoreS(SequenceS(
				Optional(WSPS()).suppressNode(),
				GlobalAccidental()
			)),
			SequenceS(AnyOf("CDEFGAB").label(BaseNote),
				OptionalS(KeyNoteAccidental()),
				Optional(WSPS()).suppressNode(),
				OptionalS(Mode()),
				ZeroOrMoreS(SequenceS(
						Optional(WSPS()).suppressNode(),
						GlobalAccidental()
				)),
				OptionalS(SequenceS(WSPS(), Parameter(Octave)))
			)
		).label(KeyDef);
	}
	/**
	 * key-note-accidental ::= "#" / "b"
	 */
	Rule KeyNoteAccidental() {
		return AnyOf("#b").label(KeyNoteAccidental);
	}
	/**
	 * mode ::= minor / major / lydian / ionian / mixolydian / dorian / aeolian / phrygian / locrian
	 */
	Rule Mode() {
		return FirstOf(Major(), Lydian(), Ionian(), Mixolydian(),
				Dorian(), Aeolian(), Phrygian(), Locrian(),
				Explicit(), Minor()
			).label(Mode);
	}
	/**
	 * m, min, mino, minor - all modes are case insensitive
	 * 
	 * minor ::= "m" ["in" ["o" ["r"]]]
	 */
	Rule Minor() {
		return Sequence(IgnoreCase("m"),
			Optional(Sequence(IgnoreCase("in"),
				Optional(Sequence(IgnoreCase("o"),
					Optional(IgnoreCase("r"))
				))
			))
		).label(Minor).suppressSubnodes();
	}
	/**
	 * major ::= "maj" ["o" ["r"]]
	 */
	Rule Major() {
		return Sequence(IgnoreCase("maj"),
			Optional(Sequence(IgnoreCase("o"),
				Optional(IgnoreCase("r"))
			))
		).label(Major).suppressSubnodes();
	}
	/**
	 * major with sharp 4th
	 * 
	 * lydian ::= "lyd" ["i" ["a" ["n"]]]
	 */
	Rule Lydian() {
		return Sequence(IgnoreCase("lyd"),
			Optional(Sequence(IgnoreCase("i"),
				Optional(Sequence(IgnoreCase("a"),
					Optional(IgnoreCase("n"))
				))
			))
		).label(Lydian).suppressSubnodes();
	}
	/**
	 * =major
	 * 
	 * ionian ::= "ion" ["i" ["a" ["n"]]]
	 */
	Rule Ionian() {
		return Sequence(IgnoreCase("ion"),
			Optional(Sequence(IgnoreCase("i"),
				Optional(Sequence(IgnoreCase("a"),
					Optional(IgnoreCase("n"))
				))
			))
		).label(Ionian).suppressSubnodes();
	}
	/**
	 * major with flat 7th
	 * 
	 * mixolydian ::= "mix" ["o" ["l" ["y" ["d" ["i" ["a" ["n"]]]]]]]
	 */
	Rule Mixolydian() {
		return Sequence(IgnoreCase("mix"),
			Optional(Sequence(IgnoreCase("o"),
				Optional(Sequence(IgnoreCase("l"),
					Optional(Sequence(IgnoreCase("y"),
						Optional(Sequence(IgnoreCase("d"),
							Optional(Sequence(IgnoreCase("i"),
								Optional(Sequence(IgnoreCase("a"),
									Optional(IgnoreCase("n"))
								))
							))
						))
					))
				))
			))
		).label(Mixolydian).suppressSubnodes();
	}
	/**
	 * minor with sharp 6th
	 * 
	 * dorian ::= "dor" ["i" ["a" ["n"]]]
	 */
	Rule Dorian() {
		return Sequence(IgnoreCase("dor"),
			Optional(Sequence(IgnoreCase("i"),
				Optional(Sequence(IgnoreCase("a"),
					Optional(IgnoreCase("n"))
				))
			))
		).label(Dorian).suppressSubnodes();
	}
	/**
	 * =minor
	 * 
	 * aeolian ::= "aeo" ["l" ["i" ["a" ["n"]]]]
	 */
	Rule Aeolian() {
		return Sequence(IgnoreCase("aeo"),
			Optional(Sequence(IgnoreCase("l"),
				Optional(Sequence(IgnoreCase("i"),
					Optional(Sequence(IgnoreCase("a"),
						Optional(IgnoreCase("n"))
					))
				))
			))
		).label(Aeolian).suppressSubnodes();
	}
	/**
	 * minor with flat 2nd
	 * 
	 * phrygian ::= "phr" ["y" ["g" ["i" ["a" ["n"]]]]]
	 */
	Rule Phrygian() {
		return Sequence(IgnoreCase("phr"),
			Optional(Sequence(IgnoreCase("y"),
				Optional(Sequence(IgnoreCase("g"),
					Optional(Sequence(IgnoreCase("i"),
						Optional(Sequence(IgnoreCase("a"),
								Optional(IgnoreCase("n"))
						))
					))
				))
			))
		).label(Phrygian).suppressSubnodes();
	}
	/**
	 * minor with flat 2nd and 5th
	 * 
	 * locrian ::= "loc" ["r" ["i" ["a" ["n"]]]]
	 */
	Rule Locrian() {
		return Sequence(IgnoreCase("loc"),
			Optional(Sequence(IgnoreCase("r"),
				Optional(Sequence(IgnoreCase("i"),
					Optional(Sequence(IgnoreCase("a"),
						Optional(IgnoreCase("n"))
					))
				))
			))
		).label(Locrian).suppressSubnodes();
	}
	/**
	 * Explicit mode
	 * exp
	 */
	Rule Explicit() {
		return IgnoreCase("exp").label(Explicit);
	}
	/**
	 * global-accidental ::= accidental base-note<p>
	 * e.g. ^f =c _b
	 */
	Rule GlobalAccidental() {
		return Sequence(Accidental(), BaseNote())
			.label(GlobalAccidental);
	}
	
	/**
	 * dots are ignored - for legibility only
	 * parts-play-order ::= 1*( ALPHA / ( "(" parts-play-order ")" ) *DIGIT) / "."
	 */
	Rule PartsPlayOrder() {
		return OneOrMore(FirstOfS(
				CharRange('A', 'Z').label("ALPHA").suppressSubnodes(),
				DIGITS(),
				String("("),
				String(")"),
				suppr("."),
				suppr("\""),//found P:"AABBCCAA"...
				suppr(WSPS())
		)).label(PartsPlayOrder);
		//return OneOrMore(AnyOf("1234567890().ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
		//	.label("PartsPlayOrder").suppressSubnodes();
//		if (cachedPartsPlayOrder == null) {
//			//cachedPartsPlayOrder = String("building...");
//			cachedPartsPlayOrder =
//				FirstOfS(
//					OneOrMoreS(
//						SequenceS(
//							FirstOfS(CharRange('A', 'Z').label("ALPHA"),
//									Sequence('(', cachedPartsPlayOrder, ')')
//									),
//							ZeroOrMore(DIGIT()).label("DIGIT").suppressSubnodes()
//						)
//					),
//					suppr(".")
//				).label("PartsPlayOrder");
//		}
//		return cachedPartsPlayOrder;
	}
	
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * maybe "stems=up" / "stems=down" / "stems=normal" instead?
	 * 
	 * voice ::= 1*(ALPHA / DIGIT) *(1*WSP (clef / voice-name / voice-subname / voice-transpose / "merge" / "up" / "down"))
	 */
	Rule Voice() {
		return Sequence(
			ALPHASandDIGITS().label(VoiceNumber),
			ZeroOrMoreS(
				SequenceS(suppr(WSPS()),
					FirstOfS(VoiceName(), VoiceSubname(),
						VoiceTranspose(), VoiceMerge(),
						VoiceStems(), VoiceStaves(),
						VoiceBracket(), ClefMiddle(),
						/* clef */
						//V: RH1 clef=treble name="Piano"
						//if Clef() instead of its definition,
						//will try to parse name="Piano" as ClefName
						//but it's VoiceName
						SequenceS(FirstOfS(
									ClefName(),
									SequenceS(IgnoreCase("clef="),
											FirstOfS(ClefNote(), ClefName()))
									),
								OptionalS(ClefLine()),
								OptionalS(SequenceS(Optional(WSPS()).suppressNode(), ClefOctave()))),
						ClefMiddle(),
						ClefStafflines(),
						ClefTranspose(),
						/* end clef */
						OneOrMore(VCHAR())
									.label(VoiceOther).suppressSubnodes()
						)
				)
			)
		).label(Voice);
	}
	/**
	 * \n in name = linefeed
	 * 
	 * voice-name ::= ("name=" / "nm=") %x22 *non-quote %x22
	 */
	Rule VoiceName() {
		return SequenceS(
				FirstOfS(IgnoreCase("name="), IgnoreCase("nm=")),
				String("\""),
				ZeroOrMore(NonQuote()).label(VoiceName).suppressSubnodes(),
				String("\""));
	}
	/**
	 * \n in name = linefeed
	 * 
	 * voice-subname ::= ("subname=" / "snm=") %x22 *non-quote %x22
	 */
	Rule VoiceSubname() {
		return SequenceS(
				FirstOfS(IgnoreCase("subname="),
						IgnoreCase("sname="),
						IgnoreCase("snm=")),
				String("\""),
				ZeroOrMore(NonQuote()).label(VoiceSubname).suppressSubnodes(),
				String("\""));
	}
	/**
	 * voice-transpose ::= "transpose=" ["-"] 1*DIGIT
	 */
	Rule VoiceTranspose() {
		return SequenceS(
				IgnoreCase("transpose="),
				Sequence(
					OptionalS(String("-")), DIGITS()
				).label(VoiceTranspose).suppressSubnodes()
		);
	}
	Rule VoiceMerge() {
		return Sequence(IgnoreCase("merge"),
				OptionalS(String("=")),
				OptionalS(ALPHASandDIGITS())
				).label(VoiceMerge);
	}
	Rule VoiceStems() {
		return SequenceS(IgnoreCase("stems="),
				FirstOf(IgnoreCase("up"), IgnoreCase("down"))
					.label(VoiceStems).suppressSubnodes()
			);
	}
	Rule VoiceStaves() {
		return SequenceS(IgnoreCase("staves="), DIGITS()
			).label(VoiceStaves);
	}
	Rule VoiceBracket() {
		return SequenceS(FirstOfS(IgnoreCase("brk="), IgnoreCase("bracket=")),
				DIGITS()
			).label(VoiceBracket);
	}
	
	////////////////////////////////////////////////////////////////////////
	
	/**
	 * clef ::= ( ("clef=" (clef-note / clef-name)) / clef-name)
	 * [clef-line] [1*WSP clef-octave] [1*WSP clef-middle]
	 */
	Rule Clef() {
		return Sequence(
			FirstOfS(
				ClefName(),
				SequenceS(IgnoreCase("clef="),
						FirstOfS(ClefNote(), ClefName()))
			),
			OptionalS(ClefLine()),
			OptionalS(SequenceS(Optional(WSPS()).suppressNode(), ClefOctave())),
			ZeroOrMoreS(FirstOfS(
				WSPS().suppressNode(),
				ClefMiddle(),
				ClefStafflines(),
				ClefTranspose()
			))
		).label(_Clef);
	}
	/**
	 * clef-note ::= "G" / "C" / "F" / "P"
	 * 
	 * accepts octave e.g. "G," => "G -8"
	 */
	Rule ClefNote() {
		return Sequence(
				FirstOfS('G', 'C', 'F', 'P'),
				OptionalS(Octave())).label(ClefNote);
	}
	/**
	 * Maybe also Doh1-4, Fa1-4
	 * 
	 * clef-name ::= "treble" / "alto" / "tenor" / "baritone" / "bass"
	 * / "mezzo" / "soprano" / "perc" / "none"
	 */
	Rule ClefName() {
		return Sequence(
			FirstOfS(IgnoreCase("treble"), IgnoreCase("alto"), IgnoreCase("tenor"),
				IgnoreCase("baritone"), IgnoreCase("bass"), IgnoreCase("mezzo"),
				IgnoreCase("soprano"), IgnoreCase("perc"), IgnoreCase("none")),
			OptionalS(Octave())	
			).label(ClefName).suppressSubnodes();
	}
	/**
	 * clef-line ::= "1" / "2" / "3" / "4" / "5"
	 */
	Rule ClefLine() {
		return FirstOf('1', '2', '3', '4', '5').label(ClefLine).suppressSubnodes();
	}
	Rule ClefOctave() {
		return FirstOf("+8", "-8").label(ClefOctave).suppressSubnodes();
	}
	/**
	 * cleff-middle ::= "middle=" base-note [octave]
	 */
	Rule ClefMiddle() {
		return Sequence(
				FirstOfS(IgnoreCase("m="), IgnoreCase("middle=")),
				BaseNote(), OptionalS(Octave())
			).label(ClefMiddle);
	}
	Rule ClefStafflines() {
		return Sequence(
				FirstOf(IgnoreCase("s="), IgnoreCase("stafflines=")),
				DIGITS()).label(ClefStafflines);
	}
	Rule ClefTranspose() {
		return Sequence(
				FirstOf(IgnoreCase("t="), IgnoreCase("transpose=")),
				OptionalS(String("-")),
				DIGITS()).label(ClefTranspose);
	}
	
	///////////////////////////////////////////////////////////////////////
	
	/**
	 * userdef ::= userdef-symbol *WSP "=" *WSP (long-gracing / chord-or-text)
	 */
	Rule Userdef() {
		return Sequence(UserdefSymbol(),
				ZeroOrMoreS(WSP()).suppressNode(),
				String("="),
				ZeroOrMoreS(WSP()).suppressNode(),
				FirstOfS(
					//SequenceS(String("!"), LongGracing(), String("!")),
					Gracing(),
					ChordOrText())
		).label(Userdef);
	}
	/**
	 * there may be comments at the end of header lines
	 * 
	 * header-eol ::= *WSP (comment / eol)
	 */
	Rule HeaderEol() {
		return Sequence(ZeroOrMore(WSP()).suppressNode(),
			FirstOfS(Comment(), suppr(Eol()), suppr(EOI))
		).label(HeaderEol);
	}

	///////////////////////////////////////////////////////////////////////
	
	/**
	 * abc-music ::= *(abc-line / comment / xcommand / tune-field / tex)
	 */
	Rule AbcMusic() {
		return ZeroOrMore(
			SequenceS(
				ZeroOrMore(WSP()).suppressNode(),
				FirstOfS(
						Xcommand(), Comment(), 
						TuneField(), AbcLine(),
						Tex()
				)
			)
		).label(AbcMusic);
	}
	/**
	 * abc-line ::= barline / ([barline] 1*element *(barline 1*element) [barline]) abc-eol
	 */
	Rule AbcLine() {
		return Sequence(
			OneOrMoreS(Element()),//FirstOfS(Barline(), Element())),
			/*FirstOfS(
				Sequence(
					Optional(Barline()),
					OneOrMore(Element()),
					ZeroOrMore(SequenceS(Barline(), OneOrMoreS(Element()))),
					Optional(Barline())
				),
				Barline()
			),*/
			AbcEol()
		).label(AbcLine);
	}
	/**
	 * barline ::= ( *":" *"[" 1*"|" *"]" ( *":" / nth-repeat-num ) )
	 * / invisible-barline / dashed-barline<p>
	 * e.g. <tt>:| | |:: |2 :||:</tt>
	 */
	Rule Barline() {
		return FirstOf(
			InvisibleBarline(),
			TripleBarline(),
			Sequence("::", ZeroOrMore(':'), '|').suppressNode(),
			String("::").suppressNode(),
			Sequence(
				ZeroOrMore(':'),
				ZeroOrMore('['),
				OneOrMore('|'),
				ZeroOrMore(']'),
				ZeroOrMore(':')
			).suppressNode(),
			DashedBarline()
		).label(Barline);
	}
	/**
	 * invisible-barline ::= "[|]" | "[]"
	 */
	Rule InvisibleBarline() {
		return FirstOf(String("[|]"), String("[]"))
			.label(InvisibleBarline);
	}
	/**
	 * seen on some abc file "|]|" / "|[|" / "|||"
	 */
	Rule TripleBarline() {
		return FirstOf(String("|[|"), String("|]|"),
				String("|||")).label(TripleBarline);
	}
	/**
	 * dashed-barline ::= ":" / ".|"
	 */
	Rule DashedBarline() {
		return /*FirstOf(String(":"), */(String(".|"))
			.label(DashedBarline).suppressSubnodes();
	}
	/**
	 * element ::= stem / WSP / chord-or-text / gracing / grace-notes /
	 * broken-rhythm / tuplet / slur-begin / slur-end / rollback / multi-measure-rest
	 * / measure-repeat / nth-repeat / end-nth-repeat / inline-field / unused-char
	 */
	Rule Element() {
		return FirstOf(Tuplet(), SlurBegin(), SlurEnd(),
			WSPS().label(Space), ChordOrText(),
			NthRepeat(), EndNthRepeat(),
			Barline(), Gracing(), BrokenRhythm(),
			//.| = dashed barline, no staccato barline
			//+>+ = Gracing, > = BrokenRhythm
			//!fermata! before f, e and a parsed into notes
			Stem(), Rest(), 
			GraceNotes(), MultiMeasureRest(), MeasureRepeat(),
			Rollback(), InlineField(), UnusedChar()
		).label(Element);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * stem ::= note / ( "[" 2*note "]" )
	 */
	Rule Stem() {
		return FirstOf(
				Note(),
				Sequence(String("["),
						Note(),
						//some bogus file have [CE][A]
						//for one MultiNote C-E and one
						//note A.
						ZeroOrMoreS(Note()),
						String("]"),
						OptionalS(NoteLength()),
						OptionalS(Tie())).label(_MultiNote)
		).label(Stem);
	}
	/**
	 * note ::= pitch [note-length] [tie]
	 */
	Rule Note() {
		return Sequence(Pitch(),
				OptionalS(NoteLength()),
				OptionalS(SequenceS(
					//not usual but found sometimes
					OptionalS(WSPS()), Tie()
				))
		).label(_Note);
	}
	/**
	 * pitch ::= [accidental] base-note [octave]
	 */
	Rule Pitch() {
		return Sequence(
				OptionalS(Accidental()),
				BaseNote(),
				OptionalS(Octave())
		).label(Pitch);
	}
	/**
	 * rest ::= (normal-rest / invisible-rest / inaudible-rest) [note-length]
	 */
	Rule Rest() {
		return Sequence(
			FirstOfS(NormalRest(), InvisibleRest(), InaudibleRest()),
			OptionalS(NoteLength())
		).label(Rest);
	}
	/**
	 * z = normal rest
	 * 
	 * normal-rest ::= %x7A
	 */
	Rule NormalRest() {
		return String("z").label(NormalRest);
	}
	/**
	 * x = invisible rest
	 * 
	 * invisible-rest ::= %x78
	 */
	Rule InvisibleRest() {
		return String("x").label(InvisibleRest);
	}
	/**
	 * y = inaudible and invisible rest, for spacing
	 * 
	 * inaudible-rest ::= %x79
	 */
	Rule InaudibleRest() {
		return String("y").label(InaudibleRest);
	}
	/**
	 * accidental ::= "^" / "^^" / "_" / "__" / "="
	 * 
	 * + quarter tones
	 */
	Rule Accidental() {
		//TODO Accidental : microtones
		return FirstOf("^^", "^/", "^", "__", "_/", "_", "=",
				"^3/2", "^3/", "_3/2", "_3/")
			.label(_Accidental).suppressSubnodes();
	}
	/**
	 * CDEFGABcdefgab
	 * 
	 * base-note ::= %x43 / %x44 / %x45 / %x46 / %x47 / %x41 / %x42 / %x63 / %x64 / %x65 / %x66 / %x67 / %x61 / %x62
	 */
	Rule BaseNote() {
		return AnyOf("CDEFGABcdefgab").label(BaseNote);
	}
	/**
	 * octave ::= 1*"'" / 1*","
	 */
	Rule Octave() {
		return FirstOf(OneOrMore('\''), OneOrMore(','))
		.label(Octave).suppressSubnodes();
	}
	/**
	 * note-length ::= (*DIGIT ["/" *DIGIT]) / 1*"/"
	 */
	Rule NoteLength() {
		return Sequence(ZeroOrMoreS(DIGIT()),
						ZeroOrMoreS('/'),
						ZeroOrMoreS(DIGIT())
			).label(NoteLength).suppressSubnodes();
	}
	/**
	 * tie ::= "-"
	 */
	Rule Tie() {
		return String("-").label(Tie);
	}
	
	///////////////////////////////////////////////////////////////////
	
	/**
	 * broken-rhythm ::= 1*"<" / 1*">"
	 * <br>% stem *b-elem (1*"<" / 1*">") *b-elem stem
	 */
	Rule BrokenRhythm() {
		return //Sequence(
				//Stem(),
				//ZeroOrMore(BElem()),
				FirstOf(OneOrMore('<'), OneOrMore('>'))
				//ZeroOrMore(BElem()),
				//Stem()
		//)
		.label(BrokenRhythm).suppressSubnodes();
	}
	/**
	 * b-elem ::= WSP / chord-or-text / gracing / grace-notes / slur-begin / slur-end
	 */
	Rule BElem() {
		return FirstOf(
			WSP(), ChordOrText(), Gracing(), GraceNotes(), SlurBegin(), SlurEnd()
		).label(BElem);
	}
	/**
	 * tuplet ::= "("1*DIGIT [":" [1*DIGIT] ":" [1*DIGIT]]
	 * <br>%ignored: 2*(*t-elem stem)
	 */
	Rule Tuplet() {
		return Sequence(
				suppr("("),
				CharRange('2', '9').label("DIGITS").suppressSubnodes(),
				OptionalS(SequenceS(
						String(":").label(":"),
						Optional(CharRange('1', '9')).label("DIGITS").suppressSubnodes(),
						OptionalS(SequenceS(
								String(":").label(":"),
								OptionalS(DIGITS())
								))
						)
				)/*,
				ZeroOrMoreS(TElem()), Stem(),
				OneOrMoreS(ZeroOrMoreS(TElem()), Stem())*/
		).label(Tuplet);
	}
	/**
	 * t-elem ::= WSP / chord-or-text / gracing / grace-notes / broken-rhythm
	 * / slur-begin / slur-end
	 */
	Rule TElem() {
		return FirstOf(
			WSP(), ChordOrText(), Gracing(), GraceNotes(), BrokenRhythm(),
			SlurBegin(), SlurEnd()
		).label(TElem);
	}
	
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * gracing ::= "." / userdef-symbol / long-gracing
	 */
	Rule Gracing() {
		return FirstOf(String("."),
				String("!>!"),
				String("!<!"),
				String("!+!"),
				String("+>+"),
				String("+<+"),
				SequenceS(String("!"),
						OptionalS(AnyOf("^<>_@").label(Position)),
						LongGracing(),
						String("!")),
				//for compatibility
				SequenceS(String("+"), LongGracing(), String("+")),
				UserdefSymbol()
			).label(Gracing);
	}
	/**
	 * user definable symbols <tt>~, H-Y, h-w</tt>
	 * 
	 * userdef-symbol ::= "~" / %x48-59 / %x68-77
	 */
	Rule UserdefSymbol() {
		return AnyOf("~HIJKLMNOPQRSTUVWXYhijklmnopqrstuvw")
		//return FirstOf('~', CharRange('H', 'Y'), CharRange('h','w'))
		.label(UserdefSymbol).suppressSubnodes();
	}
	/**
	 * grace notes can have length
	 * 
	 * grace-notes ::= "{" acciaccatura 1*( grace-note-stem ) "}"
	 */
	Rule GraceNotes() {
		return Sequence(
				String("{"),
				OptionalS(Acciaccatura()),
				ZeroOrMoreS(GraceNoteStem()),
				String("}"))
		.label(GraceNotes);
	}
	/**
	 * grace-note-stem ::= grace-note / ( "[" 2*grace-note "]" )
	 */
	Rule GraceNoteStem() {
		return FirstOf(
			GraceNote(),
			Sequence(suppr("["),
					GraceNote(),
					OneOrMoreS(GraceNote()),
					suppr("]")).label(GraceMultiNote)
		).label(GraceNoteStem);
	}
	/**
	 * as note, but without tie
	 * 
	 * grace-note ::= pitch [note-length]
	 */
	Rule GraceNote() {
		return Sequence(Pitch(),
						OptionalS(NoteLength())
			).label(GraceNote);
	}
	/**
	 * acciaccatura ::= "/"
	 */
	Rule Acciaccatura() {
		return String("/").label(Acciaccatura);
	}
	
	///////////////////////////////////////////////////////////////////
	
	/**
	 * chord-or-text ::= %x22 (chord / text-expression)
	 * *(chord-newline (chord / text-expression)) %x22<p>
	 * <tt>".."</tt>
	 */
	Rule ChordOrText() {
		return Sequence(
				String("\""),
				OptionalS(FirstOfS(Chord(), TextExpression())),
				ZeroOrMoreS(SequenceS(ChordOrTextNewline(),
						FirstOfS(Chord(), TextExpression())
						)),
				String("\"")
		).label(ChordOrText);
	}
	/**
	 * chord ::= ["("] base-note [chord-accidental] [chord-type]
	 * ["/" base-note [chord-accidental]] [")"] *non-quote
	 */
	Rule Chord() {
		return Sequence(
				FirstOfS(ChordChordOptionalBass(),
						ChordOptionalChordWithBass()),
/*			OptionalS(String("(")),
			OptionalS(BaseNote()),
			OptionalS(ChordAccidental()),
			OptionalS(ChordType()),
			OptionalS(Sequence(
					suppr("/"),
					BaseNote(),
					OptionalS(ChordAccidental())).label("ChordBass")),
			OptionalS(String(")")),*/
			ZeroOrMore(NonQuote()).suppressNode()
		).label(Chord);
	}
	Rule ChordChordOptionalBass() {
		return SequenceS(
			OptionalS(String("(")),
			BaseNote(),
			OptionalS(ChordAccidental()),
			OptionalS(ChordType()),
			OptionalS(Sequence(
					suppr("/"),
					BaseNote(),
					OptionalS(ChordAccidental())).label(ChordBass)),
			OptionalS(String(")")));
	}
	Rule ChordOptionalChordWithBass() {
		return SequenceS(
			OptionalS(String("(")),
			OptionalS(BaseNote()),
			OptionalS(ChordAccidental()),
			OptionalS(ChordType()),
			Sequence(
					suppr("/"),
					BaseNote(),
					OptionalS(ChordAccidental())).label(ChordBass),
			OptionalS(String(")")));
	}
	/**
	 * chord-accidental ::= "#" / "b" / "="
	 */
	Rule ChordAccidental() {
		return AnyOf("#b=").label(ChordAccidental);
	}
	/**
	 * e.g. m, 7, m7, +, mb5, sus, sus4, maj7, mmaj7, 7sus4, dim
	 * 
	 * chord-type ::= 1*( ALPHA / DIGIT / "+" / "-" )
	 */
	Rule ChordType() {
		return OneOrMore(FirstOf(ALPHA(), DIGIT(), '+', '-'))
			.label(ChordType).suppressSubnodes();
	}
	/**
	 * above, left, right, below, anywhere
	 * 
	 * text-expression ::= [ "^" / "<" / ">" / "_" / "@" ] 1*non-quote
	 */
	Rule TextExpression() {
		return Sequence(
				OptionalS(AnyOf("^<>_@").label(Position)),
				OneOrMore(NonQuoteOneTextLine()).label(Text).suppressSubnodes()
			).label(TextExpression);
	}
	/**
	 * all characters except quote
	 * 
	 * non-quote ::= SP / %x21 / %x23-7E
	 */
	Rule NonQuote() {
		//TODO TexText?
		return FirstOf(SP(), '!', CharRange('#', '~'),
				LatinExtendedAndOtherAlphabet())
			.label(NonQuote).suppressSubnodes();
	}
	/**
	 * all characters except quote, <tt>;</tt> and <tt>\\ + n</tt> 
	 */
	Rule NonQuoteOneTextLine() {
		//TODO TexText?
		return FirstOf(SP(),
				'!',
				CharRange('#', ':'),
				CharRange('<', '~'),
				LatinExtendedAndOtherAlphabet()
			).label(NonQuoteOneTextLine).suppressSubnodes();
	}
	/**
	 * chord-newline ::= "\n" / ";"
	 */
	Rule ChordOrTextNewline() {
		return FirstOf(Sequence('\\', 'n'), ';')
			.label(ChordOrTextNewline).suppressSubnodes();
	}
	
	////////////////////////////////////////////////////////////////////
	
	/**
	 * slur-begin ::= "("
	 */
	Rule SlurBegin() {
		return String("(").label(SlurBegin);
	}
	/**
	 * slur-end ::= ")"
	 */
	Rule SlurEnd() {
		return String(")").label(SlurEnd);
	}
	/**
	 * rollback ::= "&"
	 */
	Rule Rollback() {
		return String("&").label(Rollback);
	}
	/**
	 * repeat whole measure
	 * 
	 * measure-repeat ::= "/" ["/"]
	 */
	Rule MeasureRepeat() {
		return Sequence('/', Optional('/'))
			.label(MeasureRepeat).suppressSubnodes();
	}
	/**
	 * e.g. Z4
	 * 
	 * multi-measure-rest ::= %5A *DIGIT
	 */
	Rule MultiMeasureRest() {
		return Sequence(String("Z"),
					OptionalS(DIGITS())
			).label(MultiMeasureRest);
	}
	/**
	 * nth-repeat ::= "[" ( nth-repeat-num / nth-repeat-text )
	 * 
	 * for compatibility, accepts (":|" / "|") nth-repeat-num
	 */
	Rule NthRepeat() {
		return FirstOf(
			SequenceS(String("[").label(Barline).suppressSubnodes(),
				FirstOfS(NthRepeatNum(), NthRepeatText())),
			SequenceS(
				Sequence(ZeroOrMore(':'), "|").label(Barline).suppressSubnodes(),
				NthRepeatNum())
			).label(NthRepeat);
	}
	/**
	 * nth-repeat-num ::= 1*DIGIT *(("," / "-") 1*DIGIT)
	 */
	Rule NthRepeatNum() {
		return Sequence(
			DIGITS(),
			ZeroOrMoreS(SequenceS(FirstOf(',', '-').suppressNode(),
								DIGITS()))
		).label(NthRepeatNum);
	}
	/**
	 * nth-repeat-text ::= %x22 *non-quote %x22
	 */
	Rule NthRepeatText() {
		return SequenceS(suppr("\""),
				ZeroOrMore(NonQuote()).label(NthRepeatText).suppressSubnodes(),
				suppr("\""));
	}
	/**
	 * end-nth-repeat ::= "]"
	 */
	Rule EndNthRepeat() {
		return String("]").label(EndNthRepeat);
	}
	/**
	 * ignore for backward and forward compatibility, but maybe warn about them
	 * 
	 * unused-char ::= "#" / "$" / "*" / "+" / ";" / "?" / "@" / "`"
	 */
	Rule UnusedChar() {
		return AnyOf("#$*+;?@`").label(UnusedChar);
	}
	
	//////////////////////////////////////////////////////////////////
	
	/**
	 * inline-field ::= ifield-area / ifield-book / ifield-composer
	 * / ifield-discography / ifield-group / ifield-history / ifield-length
	 * / ifield-meter / ifield-notes / ifield-origin / ifield-part
	 * / ifield-tempo / ifield-rhythm / ifield-source / ifield-title
	 * / ifield-voice / ifield-words / ifield-lyrics / ifield-transcription
	 * / ifield-key
	 */
	Rule InlineField() {
		return FirstOf(IfieldArea(), IfieldBook(), IfieldComposer(),
			IfieldDiscography(), IfieldGroup(), IfieldHistory(), IfieldLength(),
			IfieldMeter(), IfieldNotes(), IfieldOrigin(), IfieldPart(),
			IfieldTempo(), IfieldRhythm(), IfieldSource(), IfieldTitle(),
			IfieldVoice(), IfieldWords(), IfieldLyrics(), IfieldTranscription(),
			IfieldKey(), IfieldUserdefPlay(), IfieldUserdefPrint(),
			IfieldInstruction(), IUnusedField()
			).label(InlineField);
	}
	Rule IUnusedField() {
		return Sequence(String("["),
				AnyOf("EIJYabcdefghijklnopqrstvxyz")
					.label(UnusedFieldLetter).suppressSubnodes(),
				String(":"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IUnusedField);
	}
	/**
	 * ifield-area ::= %5B.%41.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[A:..]</tt>
	 */
	Rule IfieldArea() {
		return Sequence(String("[A:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldArea);
	}
	/**
	 * ifield-book ::= %5B.%42.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[B:..]</tt>
	 */
	Rule IfieldBook() {
		return Sequence(String("[B:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldBook);
	}
	/**
	 * ifield-composer ::= %5B.%43.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[C:..]</tt>
	 */
	Rule IfieldComposer() {
		return Sequence(String("[C:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldComposer);
	}
	/**
	 * ifield-discography ::= %5B.%44.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[D:..]</tt>
	 */
	Rule IfieldDiscography() {
		return Sequence(String("[D:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldDiscography);
	}
	/**
	 * ifield-group ::= %5B.%47.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[G:..]</tt>
	 */
	Rule IfieldGroup() {
		return Sequence(String("[G:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldGroup);
	}
	/**
	 * ifield-history ::= %5B.%48.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[H:..]</tt>
	 */
	Rule IfieldHistory() {
		return Sequence(String("[H:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldHistory);
	}
	Rule IfieldInstruction() {
		return Sequence(String("[I:"),
				ZeroOrMore(WSP()).suppressNode(),
				Xcom(),
				String("]"))
			.label(IfieldInstruction);
	}
	/**
	 * ifield-length ::= %5B.4C.3A *WSP note-length-strict %5D<p>
	 * e.g. <tt>[L:1/8]</tt>
	 */
	Rule IfieldLength() {
		return Sequence(String("[L:"),
				ZeroOrMore(WSP()).suppressNode(),
				NoteLengthStrict(),
				String("]"))
			.label(IfieldLength);
	}
	/**
	 * ifield-meter ::= %5B.4D.3A *WSP meter %5D<p>
	 * e.g. <tt>[M:C|] [M: 2+3/8]</tt>
	 */
	Rule IfieldMeter() {
		return Sequence(String("[M:"),
				ZeroOrMore(WSP()).suppressNode(),
				TimeSignature(),
				String("]"))
			.label(IfieldMeter);
	}
	/**
	 * ifield-notes ::= %5B.%4E.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[N:..]</tt>
	 */
	Rule IfieldNotes() {
		return Sequence(String("[N:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldNotes);
	}
	/**
	 * ifield-origin ::= %5B.%4F.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[O:..]</tt>
	 */
	Rule IfieldOrigin() {
		return Sequence(String("[O:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldOrigin);
	}
	/**
	 * ifield-part ::= %5B.%50.%3A *WSP (ALPHA / tex-text-ifield) %5D<p>
	 * <tt>[P:A]</tt>
	 */
	Rule IfieldPart() {
		return Sequence(String("[P:"),
				ZeroOrMore(WSP()).suppressNode(),
				FirstOfS(CharRange('A', 'Z').label("ALPHA"), TexTextIfield()),
				String("]"))
			.label(IfieldPart);
	}
	/**
	 * ifield-tempo ::= %5B.%51.%3A *WSP tempo %5D<p>
	 * e.g. <tt>[Q:1/4=120]</tt>
	 */
	Rule IfieldTempo() {
		return Sequence(String("[Q:"),
				ZeroOrMore(WSP()).suppressNode(),
				FirstOfS(Tempo(), TempoText()),
				ZeroOrMore(WSP()).suppressNode(),
				OptionalS(FirstOfS(Tempo(), TempoText())),
				String("]"))
			.label(IfieldTempo);
	}
	/**
	 * ifield-rhythm ::= %5B.%52.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[R:reel]</tt>
	 */
	Rule IfieldRhythm() {
		return Sequence(String("[R:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldRhythm);
	}
	/**
	 * ifield-source ::= %5B.%53.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[S:..]</tt>
	 */
	Rule IfieldSource() {
		return Sequence(String("[S:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldSource);
	}
	/**
	 * ifield-title ::= %5B.%54.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[T:second version]</tt>
	 */
	Rule IfieldTitle() {
		return Sequence(String("[T:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldTitle);
	}
	Rule IfieldUserdefPlay() {
		return Sequence(String("[u:"),
				ZeroOrMore(WSP()).suppressNode(),
				Userdef(),
				String("]"))
			.label(IfieldUserdefPlay);
	}
	Rule IfieldUserdefPrint() {
		return Sequence(String("[U:"),
				ZeroOrMore(WSP()).suppressNode(),
				Userdef(),
				String("]"))
			.label(IfieldUserdefPrint);
	}
	/**
	 * ifield-voice ::= %5B.%56.%3A *WSP voice %5D<p>
	 * <tt>[V:..]</tt> options should be better defined
	 */
	Rule IfieldVoice() {
		return Sequence(String("[V:"),
				ZeroOrMore(WSP()).suppressNode(),
				Voice(),
				String("]"))
			.label(IfieldVoice);
	}
	/**
	 * ifield-words ::= %5B.%57.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[W:..]</tt>
	 */
	Rule IfieldWords() {
		return Sequence(String("[W:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldWords);
	}
	/**
	 * ifield-lyrics ::= %5B.%77.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[w:..]</tt>
	 */
	Rule IfieldLyrics() {
		return Sequence(String("[w:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(), //TODO Lyrics() ?
				String("]"))
			.label(IfieldLyrics);
	}
	/**
	 * ifield-transcription ::= %5B.%5A.%3A *WSP tex-text-ifield %5D<p>
	 * <tt>[Z:..]</tt>
	 */
	Rule IfieldTranscription() {
		return Sequence(String("[Z:"),
				ZeroOrMore(WSP()).suppressNode(),
				TexTextIfield(),
				String("]"))
			.label(IfieldTranscription);
	}
	/**
	 * ifield-key ::= %5B.4B.3A *WSP key %5D<p>
	 * e.g. <tt>[K:Ador] [K: Bphr ^d]</tt>
	 */
	Rule IfieldKey() {
		return Sequence(String("[K:"),
				ZeroOrMore(WSP()).suppressNode(),
				Key(),
				String("]"))
			.label(IfieldKey);
	}
	/**
	 * E: I: J: Y: a:-l: n:-t: v: x: y: z: ignore - but for backward and forward compatibility
	 * <p>
	 * unused-ifield ::= %5B (%x45 / %x49 / %4A / %59 / %61-6c / %6e-%74 / %76 / %78-7A)
	 * %3A *(WSP / VCHAR) %5D
	 */
	Rule IUnusedIfield() {
		return Sequence(String("["),
				AnyOf("EIJYabcdefghijklnopqrstvxyz"),
				String(":"),
				ZeroOrMore(FirstOf(WSP(), VCHAR())),
				String("]"))
			.label(IUnusedIfield);
	}
	/**
	 * all except <tt>]<tt>
	 * <p>
	 * non-bracket-char ::= *(WSP / %21-%5C / %5E-7E)
	 */
	Rule NonBracketChar() {
		return ZeroOrMore(FirstOf(
					WSP(),
					CharRange('!','\\'),
					CharRange('^','~'),
					LatinExtendedAndOtherAlphabet()
				)).label(NonBracketChar);
	}
	
	////////////////////////////////////////////////////////////////////
	
	/**
	 * abc-eol ::= comment / ([line-continuation / hard-line-break] *WSP eol)
	 */
	Rule AbcEol() {
		return Sequence(
				OptionalS(Comment()),
				SequenceS(OptionalS(FirstOfS(LineContinuation(), HardLineBreak())),
						ZeroOrMore(WSP()).suppressNode(),
						FirstOf(Eol(), EOI).suppressNode()
				)
				//FirstOfS(Comment(), suppr(Eol()), suppr(EOI))
			).label(AbcEol);
	}
	/**
	 * line-continuation ::= "\"
	 */
	Rule LineContinuation() {
		return String("\\").label(LineContinuation);
	}
	/**
	 * hard-line-break ::= "!"
	 * <p>
	 * kept for compatibility with abc2win
	 */
	Rule HardLineBreak() {
		return String("!").label(HardLineBreak);
	}
	
	//////////////////////////////////////////////////////////////////
	
	/**
	 * tune-field ::= field-area / field-book / field-composer
	 * / field-discography / field-group / field-history / field-length
	 * / field-meter / field-notes / field-origin / field-part
	 * / field-tempo / field-rhythm / field-source / field-title
	 * / field-voice / field-words / field-lyrics / field-transcription
	 * / field-key / unused-field
	 */
	Rule TuneField() {
		return FirstOf(
			FieldArea(), FieldBook(), FieldComposer(),
			FieldDiscography(), FieldGroup(), FieldHistory(), FieldLength(),
			FieldMeter(), FieldNotes(), FieldOrigin(), FieldPart(),
			FieldTempo(), FieldRhythm(), FieldSource(), FieldTitle(),
			FieldKey(), FieldVoice(), FieldWords(), FieldLyrics(),
			FieldInstruction(), UnusedField()
		).label(TuneField);
	}
	/**
	 * field-part ::= %x50.3A *WSP (ALPHA / tex-text) header-eol
	 * <p><tt>P:</tt>
	 */
	Rule FieldPart() {
		return Sequence(
				String("P:"),
				ZeroOrMore(WSP()).suppressNode(),
				FirstOf(CharRange('A', 'Z'),
						CharRange('a', 'z')).label("ALPHA").suppressSubnodes(),
				OptionalS(TexText()),
				HeaderEol())
			.label(FieldPart);
	}
	/**
	 * field-lyrics ::= %x56.3A *WSP lyrics header-eol
	 * <p><tt>w:</tt> formatted lyrics printed under staff
	 */
	Rule FieldLyrics() {
		return Sequence(
				String("w:"),
				ZeroOrMore(WSP()).suppressNode(),
				Lyrics(),
				HeaderEol())
			.label(FieldLyrics);
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * long-gracing ::= gracing-grace / gracing-vol / gracing-style / gracing-len
	 * / gracing-finger / gracing-phrase / gracing-exec / gracing-other
	 */
	Rule LongGracing() {
		return FirstOf(GracingGrace(), GracingStyle(), GracingLen(),
				GracingFinger(), GracingPhrase(), GracingExec(),
				//!fermata! before !f!
				GracingVol(), GracingOther())
			.label(LongGracing);
	}
	/**
	 * gracing-grace ::= "!trill!" / "!mordent!" / "!lowermordent!" / "!pralltriller!"
	 * / "!uppermordent!" / "!turn!" / "!roll!" / "!turnx!" / "!invertedturn!"
	 * / "!invertedturnx!" / "!trill(!" / "!trill)!"
	 * <p>
	 * accepts also <tt>+...+</tt>
	 */
	Rule GracingGrace() {
		return FirstOf("trill(", "trill)", "trill",
						"mordent", "lowermordent", "pralltriller", "uppermordent",
						"turnx", "turn", "roll", "invertedturnx", "invertedturn"
				).label(GracingGrace).suppressSubnodes();
	}
	/**
	 * gracing-vol ::= "!accent!" / "!emphasis!" / "!crescendo(!" / "!crescendo)!"
	 * / "!diminuendo(!" / "!diminuendo)!" / "!p!" / "!pp!" / "!ppp!" / "!pppp!"
	 * / "!mp!" / "!mf!" / "!fp!" / "!f!" / "!ff!" / "!fff!" / "!ffff!" / "!sfz!"
	 * / "!cresc!" / "!decresc!" / "!dimin!"
	 * <p>
	 * accepts also <tt>+...+</tt> and variants of cresc/dimin(decresc)
	 */
	Rule GracingVol() {
		return FirstOf("accent", ">", "emphasis",
						"diminuendo(", "diminuendo)", "diminuendo",
						"dimin(", "dimin)", "dimin",
						"decrescendo(", "decrescendo)", "decrescendo",
						"decresc(", "decresc)", "decresc",
						"crescendo(", "crescendo)", "crescendo", 
						"cresc(", "cresc)", "cresc",
						"mp", "mf", "fp", "sfz",
						"ffff", "fff", "ff", "f",
						"pppp", "ppp", "pp", "p"
			).label(GracingVol).suppressSubnodes();
	}
	/**
	 * gracing-style ::= "!+!" / "!open!" / "!snap!" / "!upbow!" / "!downbow!"
	 * / "!slide!" / "!arpeggio!"
	 */
	Rule GracingStyle() {
		return FirstOf("open", "snap", "upbow", "downbow",
				"slide", "arpeggio", "plus"
			).label(GracingStyle).suppressSubnodes();
	}
	/**
	 * gracing-len ::= "!fermata!" / "!invertedfermata!" / "!tenuto!"
	 */
	Rule GracingLen() {
		return FirstOf("fermata", "invertedfermata", "tenuto"
			).label(GracingLen).suppressSubnodes();
	}
	/**
	 * gracing-finger ::= "!0!" / "!1!" / "!2!" / "!3!" / "!4!" / "!5!"
	 */
	Rule GracingFinger() {
		return CharRange('0', '5')
			.label(GracingFinger).suppressSubnodes();
	}
	/**
	 * gracing-phrase ::= "!shortphrase!" / "!mediumphrase!" / "!longphrase!"
	 */
	Rule GracingPhrase() {
		return FirstOf("shortphrase", "mediumphrase", "longphrase"
			).label(GracingPhrase).suppressSubnodes();
	}
	/**
	 * gracing-exec ::= "!segno!" / "!coda!" / "!D.S.!" / "!D.C.!" / "!fine!"
	 * / ("!repeatbar" *DIGIT "!")
	 */
	Rule GracingExec() {
		return FirstOf("segno", "coda", "D.S.", "D.C.", "fine", "Fine",
					Sequence("repeatbar", ZeroOrMore(DIGIT()))
			).label(GracingExec).suppressSubnodes();
	}
	/**
	 * gracing-other ::= "!wedge!" / "!thumb!" / "!breath!"
	 * / ("!" 1*(SP / %x22-x7E) "!")
	 * <p>
	 * any text between !! is allowed
	 */
	Rule GracingOther() {
		return FirstOf("wedge", "thumb", "breath",
					OneOrMore(FirstOf(CharRange('"', '*'),
									  CharRange(',', '~')))
				).label(GracingOther).suppressSubnodes();
	}
	
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * comment ::= "%" ([non-comment-char *(VCHAR / WSP)]) eol
	 */
	Rule Comment() {
		return Sequence(String("%"),
				Optional(Sequence(NonCommentChar(),
							ZeroOrMore(FirstOf(VCHAR(), WSP()))))
					.label(CommentText).suppressSubnodes(),
				FirstOfS(Eol(), EOI))
			.label(Comment);
	}
	/**
	 * non-comment-char ::= %x20-24 / %x26-%x7E / HTAB
	 * <p>
	 * all characters except %
	 */
	Rule NonCommentChar() {
		return FirstOf(CharRange(' ', '$'),
				CharRange('&', '~'),
				LatinExtendedAndOtherAlphabet(),
				HTAB())
			.label(NonCommentChar);
	}
	/**
	 * xcommand ::= "%%" xcom eol
	 */
	Rule Xcommand() {
		return Sequence(String("%%"),
				Xcom(),
				Eol()
			).label(Xcommand);
	}
	/**
	 * xcom ::= xcom-staff / xcom-measurenb / xcom-text / xcom-layout
	 * / xcom-margins / xcom-midi / xcom-other
	 * <p>
	 * more need to be defined
	 */
	Rule Xcom() {
		return FirstOf(XcomStaff(), XcomMeasurenb(), XcomLayout(),
				XcomMargins(), XcomMidi(), XcomOther(), XcomText())
			.label(Xcom);
	}
	/**
	 * xcom-staff ::= xcom-staffbreak / xcom-multicol / xcom-staves / xcom-indent
	 */
	Rule XcomStaff() {
		return FirstOf(XcomStaffbreak(), XcomMulticol(), XcomStaves(), XcomIndent())
			.label(XcomStaff);
	}
	/**
	 * xcom-staffbreak ::= "staffbreak" 1*WSP xcom-number xcom-unit
	 */
	Rule XcomStaffbreak() {
		return Sequence("staffbreak",
				OneOrMore(WSP()).suppressNode(),
				XcomNumber(), XcomUnit())
			.label(XcomStaffbreak);
	}
	/**
	 * xcom-multicol ::= "multicol" 1*WSP ("start" / "new" / "end")
	 */
	Rule XcomMulticol() {
		return Sequence("multicol",
				OneOrMore(WSP()).suppressNode(),
				FirstOf("start", "new", "end"))
			.label(XcomMulticol);
	}
	/**
	 * xcom-staves ::= "staves" 1*WSP stave-voice *( bar-staves stave-voice)
	 */
	Rule XcomStaves() {
		return Sequence("staves",
				OneOrMore(WSP()).suppressNode(),
				StaveVoice(),
				ZeroOrMore(Sequence(BarStaves(), StaveVoice())))
			.label(XcomStaves);
	}
	/**
	 * stave-voice ::= single-voice / bracketed-voice / braced-voice / paren-voice
	 */
	Rule StaveVoice() {
		return FirstOf(SingleVoice(), BracketedVoice(), BracedVoice(), ParenVoice())
			.label(StaveVoice);
	}
	/**
	 * bracketed-voice ::= "[" *WSP (single-voice / braced-voice / paren-voice)
	 * 1*(bar-staves (single-voice / braced-voice / paren-voice)) *WSP "]"
	 * <p>
	 * staves joined by bracket
	 */
	Rule BracketedVoice() {
		return Sequence('[', ZeroOrMore(WSP()),
				FirstOf(SingleVoice(), BracedVoice(), ParenVoice()),
				OneOrMore(Sequence(BarStaves(),
									FirstOf(SingleVoice(), BracedVoice(), ParenVoice()))),
				ZeroOrMore(WSP()),
				']')
			.label(BracketedVoice);
	}
	/**
	 * braced-voice ::= "{" *WSP (single-voice / paren-voice)
	 * 1*(bar-staves (single-voice / paren-voice)) *WSP "}"
	 * <p>
	 * staves joined by brace
	 */
	Rule BracedVoice() {
		return Sequence('{', ZeroOrMore(WSP()),
				FirstOf(SingleVoice(), ParenVoice()),
				OneOrMore(Sequence(BarStaves(),
									FirstOf(SingleVoice(), ParenVoice()))),
				ZeroOrMore(WSP()),
				'}')
			.label(BracedVoice);
	}
	/**
	 * paren-voice ::= "(" single-voice 1*( 1*WSP single-voice) ")"
	 * <p>
	 * on same staff
	 */
	Rule ParenVoice() {
		return Sequence('(', SingleVoice(),
				OneOrMore(Sequence(OneOrMore(WSP()), SingleVoice())))
			.label(ParenVoice);
	}
	/**
	 * single-voice ::= 1*(ALPHA / DIGIT)
	 */
	Rule SingleVoice() {
		return OneOrMore(FirstOf(ALPHA(), DIGIT()))
			.label(SingleVoice).suppressSubnodes();
	}
	/**
	 * bar-staves ::= (*WSP "|" *WSP) / 1*WSP
	 * <p>
	 * <tt>|</tt> to not bar
	 */
	Rule BarStaves() {
		return FirstOf(
					Sequence(ZeroOrMore(WSP()), '|', ZeroOrMore(WSP())),
					OneOrMore(WSP())
				).label(BarStaves);
	}
	/**
	 * xcom-indent ::= "indent" 1*WSP xcom-number xcom-unit
	 */
	Rule XcomIndent() {
		return Sequence(String("indent"),
				OneOrMore(WSP()).suppressNode(),
				XcomNumber(), XcomUnit())
			.label(XcomIndent);
	}
	/**
	 * xcom-measurenb ::= "measurenb" / "measurebox" / "measurefirst"
	 * / ("setbarnb" 1*WSP 1*DIGIT)
	 */
	Rule XcomMeasurenb() {
		return FirstOf(String("measurenb"),
				String("measurebox"),
				String("measurefirst"),
				Sequence(String("setbarnb"),
						OneOrMore(WSP()), OneOrMore(DIGIT())))
			.label(XcomMeasurenb);
	}
	/**
	 * xcom-text ::= xcom-textline / xcom-textcenter / xcom-textblock
	 */
	Rule XcomText() {
		return FirstOf(XcomTextline(), XcomTextcenter(), XcomTextblock())
			.label(XcomText);
	}
	/**
	 * xcom-textline ::= "text" 1*WSP tex-text
	 */
	Rule XcomTextline() {
		return Sequence(String("text"),
				OneOrMore(WSP()).suppressNode(),
				TexText())
			.label(XcomTextline);
	}
	/**
	 * xcom-textcenter ::= "center" 1*WSP tex-text
	 */
	Rule XcomTextcenter() {
		return Sequence(String("center"), OneOrMore(WSP()), TexText())
			.label(XcomTextcenter);
	}
	/**
	 * xcom-textblock ::= "begintext" [textblock-param] eol *(["%%"] tex-text) "%%endtext"
	 */
	Rule XcomTextblock() {
		return Sequence(String("begintext"), OptionalS(TextblockParam()), Eol(),
				ZeroOrMoreS(SequenceS(OptionalS(String("%%")), TexText(), Eol())),
				String("%%endtext")
				).label(XcomTextblock);
	}
	/**
	 * textblock-param ::= "obeylines" / ("fill" / "ragged") / ("align" / "justify")
	 * / "skip"
	 */
	Rule TextblockParam() {
		return FirstOf("obeylines", "fill", "ragged", "align", "justify", "skip")
			.label(TextblockParam).suppressSubnodes();
	}
	/**
	 * xcom-layout ::= xcom-sep / xcom-vskip / xcom-newpage
	 */
	Rule XcomLayout() {
		return FirstOf(XcomSep(), XcomVskip(), XcomNewpage())
			.label(XcomLayout);
	}
	/**
	 * xcom-sep ::= "sep" [3(1*WSP xcom-number xcom-unit)]
	 * <p>
	 * space-above, space-below, width
	 */
	Rule XcomSep() {
		return Sequence(String("sep"),
				ZeroOrMoreS(SequenceS(WSPS(), XcomNumber(), XcomUnit()))
			).label(XcomSep);
	}
	/**
	 * xcom-vskip ::= "vskip" 1*WSP xcom-number xcom-unit
	 */
	Rule XcomVskip() {
		return Sequence(String("vskip"), OneOrMore(WSP()), XcomNumber(), XcomUnit())
			.label(XcomVskip);
	}
	/**
	 * xcom-newpage ::= "newpage" [1*WSP 1*DIGIT]
	 * <p>
	 * optionally restart page numbering at n
	 */
	Rule XcomNewpage() {
		return Sequence(String("newpage"), Optional(Sequence(OneOrMore(WSP()), OneOrMore(DIGIT()))))
			.label(XcomNewpage);
	}
	/**
	 * xcom-margins ::= ("botmargin" / "topmargin" / "leftmargin" / "rightmargin")
	 * 1*WSP xcom-number xcom-unit
	 */
	Rule XcomMargins() {
		return Sequence(
			FirstOfS(String("botmargin"),
					String("topmargin"),
					String("leftmargin"),
					String("rightmargin")),
				//suppressSubnodes()
			OptionalS(String("=")),
			WSPS(), XcomNumber(), XcomUnit()
		).label(XcomMargins);
	}
	/**
	 * xcom-midi ::= "midi" 1*WSP (midi-channel / midi-program / midi-transpose)
	 */
	Rule XcomMidi() {
		return Sequence(String("midi"),
				OptionalS(String("=")),
				OneOrMore(WSP()),
				FirstOf(MidiChannel(), MidiProgram(), MidiTranspose()))
			.label(XcomMidi);
	}
	/**
	 * midi-channel ::= "channel" 1*WSP midi-channel-number
	 */
	Rule MidiChannel() {
		return Sequence(String("channel"), OneOrMore(WSP()), MidiChannelNumber())
			.label(MidiChannel);
	}
	/**
	 * midi-program ::= "program" 1*WSP [midi-channel-number 1*WSP] midi-program-number
	 */
	Rule MidiProgram() {
		return Sequence(String("program"), OneOrMore(WSP()),
				Optional(Sequence(MidiChannelNumber(), OneOrMore(WSP()))),
				MidiProgramNumber())
			.label(MidiProgram);
	}
	/**
	 * midi-channel-number ::= %x31-39 / (%x31 %x30-36)
	 * <p>
	 * channels 1-16
	 */
	Rule MidiChannelNumber() {
		return FirstOf(CharRange('1', '9'),
				Sequence('1', CharRange('0', '6')))
			.label(MidiChannelNumber).suppressSubnodes();
	}
	/**
	 * midi-program-number ::= %x31-39 / (%x31-39 %x30-39)
	 * / (%x31 %x30-31 %x30-39) / (%x31.32 %x30-38)
	 * <p>
	 * programs 1-128
	 */
	Rule MidiProgramNumber() {
		return FirstOf(
			CharRange('1', '9'),
			Sequence(CharRange('1', '9'), CharRange('0', '9')),
			Sequence('1', CharRange('0', '1'), CharRange('0', '9')),
			Sequence("12", CharRange('0', '8'))
		).label(MidiProgramNumber).suppressSubnodes();
	}
	/**
	 * midi-transpose ::= "transpose" 1*WSP ["-"] 1*DIGIT
	 */
	Rule MidiTranspose() {
		return Sequence(String("transpose"),
				OneOrMore(WSP()),
				Optional('-'),
				OneOrMore(DIGIT()))
			.label(MidiTranspose);
	}
	/**
	 * xcom-other ::= *(VCHAR / WSP)
	 */
	Rule XcomOther() {
		return FirstOf(
				SequenceS(ALPHAS(),
					WSPS().suppressNode(),
					FirstOfS(
						SequenceS(XcomNumber(),
							Optional(WSPS()).suppressNode(),
							OptionalS(XcomUnit())),
						Boolean()
					),
					ZeroOrMore(FirstOfS(VCHAR(), WSP()))
						.label("...").suppressSubnodes()),
				ZeroOrMore(FirstOfS(VCHAR(), WSP()))
					.label("...").suppressSubnodes()
			).label(XcomOther);
	}
	Rule Boolean() {
		return FirstOf(IgnoreCase("yes"), IgnoreCase("no"))
			.label(Boolean).suppressSubnodes();
	}
	/**
	 * xcom-number ::= 1*DIGIT ["." 1*DIGIT]
	 */
	Rule XcomNumber() {
		return Sequence(OneOrMore(DIGIT()),
						Optional(Sequence('.', OneOrMore(DIGIT())))
		).label(XcomNumber).suppressSubnodes();
	}
	/**
	 * xcom-unit ::= ["cm" / "pt" / "in"]
	 */
	Rule XcomUnit() {
		return Optional(FirstOf(IgnoreCase("cm"), IgnoreCase("pt"), IgnoreCase("in")))
			.label(XcomUnit).suppressSubnodes();
	}
	
	////////////////////////////////////////////////////////////////////
	
	/**
	 * lyrics ::= *(lyrics-char / lyrics-syllable-break / lyrics-next-bar
	 * / lyrics-hold / lyrics-skip-note / lyrics-nbsp / lyrics-dash / tex-escape)
	 */
	Rule Lyrics() {
		return ZeroOrMore(FirstOfS(LyricsSyllableBreak(), LyricsNextBar(),
				LyricsHold(), LyricsSkipNote(), LyricsNbsp(), LyricsDash(),
				LyricsSyllable(),
				SequenceS(LineContinuation(), Eol())))
			.label(Lyrics);
	}
	/**
	 * lyrics-ifield ::= *(lyrics-char-ifield / lyrics-syllable-break / lyrics-next-bar
	 * / lyrics-hold / lyrics-skip-note / lyrics-nbsp / lyrics-dash / tex-escape)
	 */
	Rule LyricsIfield() {
		return ZeroOrMore(FirstOfS(LyricsSyllableBreak(), LyricsNextBar(),
				LyricsHold(), LyricsSkipNote(), LyricsNbsp(), LyricsDash(),
				LyricsSyllableIfield()))
			.label(LyricsIfield);
	}
	Rule LyricsSyllable() {
		return OneOrMore(FirstOf(LyricsChar(), TexEscape()))
			.label(LyricsSyllable).suppressSubnodes();
	}
	Rule LyricsSyllableIfield() {
		return OneOrMore(FirstOf(LyricsCharIfield(), TexEscape()))
			.label(LyricsSyllable).suppressSubnodes();
	}
	/**
	 * lyrics-char ::= WSP / DIGIT / ALPHA / %x21-29 / %x2B-2C / %x2E-2F
	 * / %x3A-40 / %x5B / %x5D / %x5E / %x60 / %x7B-7D
	 * <p>all characters without special meaning
	 */
	Rule LyricsChar() {
		return FirstOf(DIGIT(), ALPHA(),
				AnyOf("!\"#$%&'()"+"+,"+"./"+":;<=>?@"+"[]^`{}"))
			.label(LyricsChar).suppressSubnodes();
	}
	/**
	 * lyrics-char-ifield ::= WSP / DIGIT / ALPHA / %x21-29 / %x2B-2C / %x2E-2F
	 * / %x3A-40 / %x5B / %x5E / %x60 / %x7B-7D
	 * <p>all characters without special meaning and except ]
	 */
	Rule LyricsCharIfield() {
		return FirstOf(DIGIT(), ALPHA(),
				AnyOf("!\"#$%&'()"+"+,"+"./"+":;<=>?@"+"[^`{}"))
			.label(LyricsChar).suppressSubnodes();
	}
	/**
	 * lyrics-syllable-break ::= "-"
	 * <p>break between syllables in a word
	 */
	Rule LyricsSyllableBreak() {
		return FirstOf(WSPS(), String("-"))
			.label(LyricsSyllableBreak).suppressSubnodes();
	}
	/**
	 * lyrics-next-bar ::= "|"
	 * <p>advance to next bar
	 */
	Rule LyricsNextBar() {
		return String("|").label(LyricsNextBar);
	}
	/**
	 * lyrics-hold ::= "_"
	 * <p>hold syllable for one more note
	 */
	Rule LyricsHold() {
		return String("_").label(LyricsHold);
	}
	/**
	 * lyrics-skip-note ::= "*"
	 * <p>=blank syllable
	 */
	Rule LyricsSkipNote() {
		return String("*").label(LyricsSkipNote);
	}
	/**
	 * lyrics-nbsp ::= "~"
	 * <p>non-breaking space = words on same note
	 */
	Rule LyricsNbsp() {
		return String("~").label(LyricsNbsp);
	}
	/**
	 * lyrics-dash ::= "\-"
	 * <p>printed as a -
	 */
	Rule LyricsDash() {
		return String("\\-").label(LyricsDash);
	}

	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * tex-text ::= *(WSP / %21-%5B / %5D-7E / tex-escape)
	 * <p>text that may contain TeX escapes
	 */
	Rule TexText() {
		return ZeroOrMore(
				FirstOf(WSP(),
						CharRange('!', '$'),
						//exclude % which is comment ?
						'%',
						CharRange('&', '['),
						//exclude \ which is tex escape
						CharRange(']', '~'),
						LatinExtendedAndOtherAlphabet(), TexEscape())
			).label(TexText).suppressSubnodes();
	}
	/**
	 * tex-text-ifield ::= *(WSP / %21-%5B / %5E-7E / tex-escape)
	 * <p>as above, but except ]
	 */
	Rule TexTextIfield() {
		return ZeroOrMore(FirstOf(WSP(), CharRange('!', '['),
					CharRange('^', '~'), LatinExtendedAndOtherAlphabet(), TexEscape()))
			.label(TexText)/*Ifield")*/.suppressSubnodes();
	}
	/**
	 * tex-escape ::= "\" 1*(VCHAR)
	 * <p>to be defined better
	 */
	Rule TexEscape() {
		return Sequence('\\', OneOrMore(VCHAR())
			).label(TexEscape).suppressSubnodes();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	/**
	 * tex ::= "\" *(VCHAR / WSP) eol
	 * <p>deprecated - kept only for backward compatibility with abc2mtex
	 */
	Rule Tex() {
		return Sequence('\\', ZeroOrMore(FirstOf(VCHAR(), WSP())), Eol())
			.label(Tex);
	}
	/**
	 * eol ::= CRLF / LF / CR
	 * <p>only one version should occur in the whole file - win / *nix / mac line breaks
	 */
	Rule Eol() {
		return FirstOf(CRLF(), LF(), CR()).suppressSubnodes()
			.label("Eol");
	}
	Rule Eols() {
		return FirstOf( OneOrMore(CRLF()),
						OneOrMore(LF()),
						OneOrMore(CR())).suppressSubnodes()
			.label(Eols);
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	/**
	 * ALPHA ::= %x41-5A / %x61-7A
	 * <p>A-Z / a-z
	 * @see #ALPHAS()
	 */
	Rule ALPHA() {
		return FirstOf(CharRange('A', 'Z'), CharRange('a', 'z'))
			.label(ALPHA).suppressNode();
	}
	/**
	 * chars &gt;= 0080 and &lt;= 024f
	 * It contains : Basic Latin, system, Latin-Supplement,
	 * Latin A and B extended
	 * <br>
	 * between 0080 and 00A0, this is system specific chars
	 * depending on encoding
	 */
	Rule ALPHALatinExtended() {
		return FirstOf(CharRange('\u0080', '\u024f'),
					   CharRange('\u1E00', '\u1EFF'))
			.suppressNode();
	}
	/**
	 * chars between 0080 and FFEF with exclusions.
	 * It contains various alphabets such as latin extended,
	 * hebrew, arabic, chinese, tamil... and symbols
	 */
	Rule LatinExtendedAndOtherAlphabet() {
		return FirstOf(
				CharRange('\u0080', '\u074F'), //Latin to Syriac
				CharRange('\u0780', '\u07BF'), //Thaana
				CharRange('\u0900', '\u137F'), //Devanagari to Ethiopic
				CharRange('\u13A0', '\u18AF'), //Cherokee to Mongolian
				CharRange('\u1900', '\u197F'), //Limbu to Tai Le
				CharRange('\u19E0', '\u19FF'), //Khmer
				CharRange('\u1D00', '\u1D7F'), //Phonetic ext 
				CharRange('\u1E00', '\u2BFF'), //Latin ext add to misc symbol & arrows
				CharRange('\u2E80', '\u2FDF'), //CJK radicals to Kangxi radical
				CharRange('\u2FF0', '\u31BF'), //Ideographic desc chars to Bopomofo ext
				CharRange('\u31F0', '\uA4CF'), //Katakana phonet ext to Yi radicals
				CharRange('\uAC00', '\uD7AF'), //Hangul syllable
				//exclude Surrogates
				CharRange('\uF900', '\uFE0F'), //CJK compat to Variation selectors
				CharRange('\uFE20', '\uFFEF') //combin half mark to halfwidth & fullwidth forms
			).suppressNode();
	}
	
	/**
	 * CR ::= %x0D
	 * <p>carriage return
	 */
	Rule CR() {
		return String("\r").label(CR);
	}
	/**
	 * CRLF ::= %x0D.0A
	 * <p>CR+LF
	 */
	Rule CRLF() {
		return String("\r\n").label(CRLF);
	}
	/**
	 * LF ::= %x0A
	 * <p>linefeed
	 */
	Rule LF() {
		return String("\n").label(LF);
	}
	/**
	 * DIGIT ::= %x30-39
	 * <p>0-9
	 * @see #DIGITS()
	 */
	Rule DIGIT() {
		return CharRange('0', '9').label(DIGIT);
	}
	/**
	 * DQUOTE ::= %x22
	 * <p>"
	 */
	Rule DQUOTE() {
		return String("\"").label(DQUOTE);
	}
	/**
	 * HTAB ::= %x09 ; tab
	 */
	Rule HTAB() {
		return String("\t").label(HTAB);
	}
	/**
	 * SP ::= %x20
	 * <p>space
	 */
	Rule SP() {
		return String(" ").label(SP);
	}
	/**
	 * VCHAR ::= %x21-7e
	 * <p>printing chars
	 */
	Rule VCHAR() {
		return FirstOf(CharRange('!', '~'),
				LatinExtendedAndOtherAlphabet()
			).label(VCHAR).suppressNode();
	}
	/**
	 * WSP ::= SP / HTAB
	 * <p>whitespace
	 */
	Rule WSP() {
		return FirstOf(SP(), HTAB()).label(WSP).suppressNode();
	}
	
	/** Suppress node of String(s) rule */
	Rule suppr(String s) {
		return String(s).suppressNode();
	}
	/** Suppress node of r rule */
	Rule suppr(Rule r) {
		return r.suppressNode();
	}
	/** OneOrMore(DIGIT()), suppress sub nodes */
	Rule DIGITS() {
		return OneOrMore(DIGIT()).label(DIGITS).suppressSubnodes();
	}
	/** OneOrMore(ALPHA()), suppress sub nodes */
	Rule ALPHAS() {
		return OneOrMore(ALPHA()).label(ALPHAS).suppressSubnodes();
	}
	/** Whitespaces - OneOrMore(WSP()) suppress sub nodes */
	Rule WSPS() {
		return OneOrMore(WSP()).label(WSPS).suppressSubnodes();
	}
	/** Alpha numeric value<br>
	 * OneOrMore(FirstOf(ALPHA(), DIGIT())), suppress sub nodes */
	Rule ALPHASandDIGITS() {
		return OneOrMore(FirstOf(ALPHA(), DIGIT()))
			.label(ALPHASandDIGITS).suppressSubnodes();
	}
	
	//////////////////////////
	
	Rule FirstOfS(Object rule, Object rule2, Object... moreRules) {
        return FirstOf(rule, rule2, moreRules).label("FirstOf").skipNode();
    }
	Rule OneOrMoreS(Object rule) {
		return OneOrMore(rule).label("OneOrMore").skipNode();
	}
	Rule OneOrMoreS(Object rule, Object rule2, Object... moreRules) {
		return OneOrMore(rule, rule2, moreRules).label("OneOrMore").skipNode();
	}
	Rule OptionalS(Rule r) {
		return Optional(r).label("Optional").skipNode();
	}
	Rule OptionalS(Object rule, Object rule2, Object... moreRules) {
		return Optional(rule, rule2, moreRules).label("Optional").skipNode();
	}
	Rule SequenceS(Object rule, Object rule2, Object... moreRules) {
		return Sequence(rule, rule2, moreRules).label("Sequence").skipNode();
	}
	Rule ZeroOrMoreS(Object rule) {
		return ZeroOrMore(rule).label("ZeroOrMore").skipNode();
	}
	Rule ZeroOrMoreS(Object rule, Object rule2, Object... moreRules) {
		return ZeroOrMore(rule, rule2, moreRules).label("ZeroOrMore").skipNode();
	}
	public Rule IgnoreCase(String s) {
		return super.IgnoreCase(s).label(s).suppressSubnodes();
	}
	public Rule String(String s) {
		return super.String(s).label(s).suppressSubnodes();
	}
	
}

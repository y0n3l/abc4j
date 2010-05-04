import junit.framework.TestCase;
import abc.notation.AccidentalType;
import abc.notation.Interval;
import abc.notation.KeySignature;
import abc.notation.Note;

public class IntervalTests extends TestCase {
	
	public IntervalTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1() {
		assertEquals(0, Interval.PERFECT_UNISON.getSemitones());
		assertEquals(-1, Interval.DIMINISHED_UNISON.getSemitones());
		assertEquals(1, Interval.AUGMENTED_UNISON.getSemitones());
		assertFalse(Interval.AUGMENTED_UNISON.equals(Interval.DIMINISHED_SECOND));
		assertTrue(Interval.AUGMENTED_UNISON.isSimilarTo(Interval.MINOR_SECOND));
		Interval majorNinth = new Interval((byte) (Interval.OCTAVE+Interval.SECOND),
											Interval.MAJOR);
		assertEquals(14, majorNinth.getSemitones());
		assertEquals(Interval.NINTH, majorNinth.getLabel());
		assertEquals(Interval.UPWARD, majorNinth.getDirection());
		
		Interval majorNinthDown = new Interval((byte) (Interval.OCTAVE+Interval.SECOND),
				Interval.MAJOR, Interval.DOWNWARD);
		assertEquals(14, majorNinthDown.getSemitones());
		assertEquals(Interval.NINTH, majorNinthDown.getLabel());
		assertEquals(Interval.DOWNWARD, majorNinthDown.getDirection());
	}
	
	public void test2IntervalFromNotes() {
		Note n1 = new Note(Note.E, AccidentalType.FLAT);
		Note n2 = new Note(Note.B, AccidentalType.NONE);
		Interval fifthAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthAug.getLabel());
		assertEquals(Interval.AUGMENTED, fifthAug.getQuality());
		assertEquals(8, fifthAug.getSemitones());
		assertEquals(Interval.UPWARD, fifthAug.getDirection());
		
		KeySignature Gmin = new KeySignature(Note.G, AccidentalType.NATURAL, KeySignature.MINOR);
		//G minor has B flat and E flat
		//note B has no accidental (NONE)
		//our new interval will be understood as perfect fifth
		Interval fifthPerfect = new Interval(n1, n2, Gmin);
		assertEquals(Interval.FIFTH, fifthPerfect.getLabel());
		assertEquals(Interval.PERFECT, fifthPerfect.getQuality());
		assertEquals(7, fifthPerfect.getSemitones());

		//double augmented
		n2 = new Note(Note.B, AccidentalType.SHARP);
		Interval fifthDblAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthDblAug.getLabel());
		assertEquals(Interval.DOUBLE_AUGMENTED, fifthDblAug.getQuality());
		assertEquals(9, fifthDblAug.getSemitones());
		
		//triple augmented
		n2 = new Note(Note.B, AccidentalType.DOUBLE_SHARP);
		Interval fifthTripleAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthTripleAug.getLabel());
		assertEquals(Interval.TRIPLE_AUGMENTED, fifthTripleAug.getQuality());
		assertEquals(10, fifthTripleAug.getSemitones());

		//quadruple augmented
		n1 = new Note(Note.E, AccidentalType.DOUBLE_FLAT);
		Interval fifthQuadAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthQuadAug.getLabel());
		assertEquals(Interval.QUADRUPLE_AUGMENTED, fifthQuadAug.getQuality());
		assertEquals(11, fifthQuadAug.getSemitones());

		//diminished 2nd
		Interval dim2nd = new Interval(
				new Note(Note.C, AccidentalType.DOUBLE_SHARP),
				new Note(Note.D, AccidentalType.NATURAL),
				null);
		assertEquals(Interval.SECOND, dim2nd.getLabel());
		assertEquals(Interval.DIMINISHED, dim2nd.getQuality());
		assertEquals(0, dim2nd.getSemitones());
		assertEquals(Interval.UPWARD, dim2nd.getDirection());

		//augmented 2nd
		Interval aug2nd = new Interval(
				new Note(Note.e),
				new Note(Note.f, AccidentalType.DOUBLE_SHARP),
				null);
		assertEquals(Interval.SECOND, aug2nd.getLabel());
		assertEquals(Interval.AUGMENTED, aug2nd.getQuality());
		assertEquals(3, aug2nd.getSemitones());
		assertEquals(Interval.UPWARD, aug2nd.getDirection());
		
		Interval minor6th = new Interval(
				new Note(Note.D),
				new Note(Note.B, AccidentalType.FLAT),
				null);
		assertEquals(Interval.SIXTH, minor6th.getLabel());
		assertEquals(Interval.MINOR, minor6th.getQuality());
		assertEquals(8, minor6th.getSemitones());
		
		Interval minorOct6th = new Interval(
				new Note(Note.D),
				new Note(Note.b, AccidentalType.FLAT),
				null);
		assertEquals(Interval.OCTAVE+Interval.SIXTH, minorOct6th.getLabel());
		assertEquals(Interval.MINOR, minorOct6th.getQuality());
		assertEquals(12+8, minorOct6th.getSemitones());

		//renversed fifth... is a fifth!
		Interval renversedFifth = new Interval(
				new Note(Note.c),
				new Note(Note.F),
				null);
		assertEquals(Interval.FIFTH, renversedFifth.getLabel());
		assertEquals(Interval.PERFECT, renversedFifth.getQuality());
		assertEquals(7, renversedFifth.getSemitones());
		assertEquals(Interval.DOWNWARD, renversedFifth.getDirection());
		
		//renversed octave+fifth
		Interval renversedOctFifth = new Interval(
				new Note(Note.c, AccidentalType.NATURAL, (byte) 1),
				new Note(Note.F),
				null);
		assertEquals(Interval.OCTAVE+Interval.FIFTH, renversedOctFifth.getLabel());
		assertEquals(Interval.PERFECT, renversedOctFifth.getQuality());
		assertEquals(12+7, renversedOctFifth.getSemitones());
		assertEquals(Interval.DOWNWARD, renversedOctFifth.getDirection());
		
		//3 octaves
		Interval threeOct = new Interval(
				new Note(Note.C),
				new Note(Note.c, AccidentalType.NATURAL, (byte) 2),
				null);
		assertEquals(3*Interval.OCTAVE, threeOct.getLabel());
		assertEquals(Interval.PERFECT, threeOct.getQuality());
		assertEquals(3*12, threeOct.getSemitones());
		assertEquals(Interval.UPWARD, threeOct.getDirection());

		//renversed 3 octave
		Interval renvThreeOct = new Interval(
				new Note(Note.c, AccidentalType.NATURAL, (byte) 2),
				new Note(Note.C),
				null);
		assertEquals(3*Interval.OCTAVE, renvThreeOct.getLabel());
		assertEquals(Interval.PERFECT, renvThreeOct.getQuality());
		assertEquals(3*12, renvThreeOct.getSemitones());
		assertEquals(Interval.DOWNWARD, renvThreeOct.getDirection());

		//renversed 3 octave + major second
		Interval renvThreeAugOct = new Interval(
				new Note(Note.d, AccidentalType.NATURAL, (byte) 0),
				new Note(Note.C),
				null);
		assertEquals(1*Interval.OCTAVE + Interval.SECOND,
						renvThreeAugOct.getLabel());
		assertEquals(Interval.MAJOR, renvThreeAugOct.getQuality());
		assertEquals(1*12 + 2, renvThreeAugOct.getSemitones());
		
		Interval dimOct = new Interval(
				new Note(Note.C, AccidentalType.SHARP),
				new Note(Note.c),
				null);
		assertEquals(Interval.OCTAVE, dimOct.getLabel());
		assertEquals(Interval.DIMINISHED, dimOct.getQuality());
		assertEquals(11, dimOct.getSemitones());
		
		Interval dblDimOct = new Interval(
				new Note(Note.C, AccidentalType.SHARP),
				new Note(Note.c, AccidentalType.FLAT),
				null);
		assertEquals(Interval.OCTAVE, dblDimOct.getLabel());
		assertEquals(Interval.DOUBLE_DIMINISHED, dblDimOct.getQuality());
		assertEquals(10, dblDimOct.getSemitones());
	
		Interval augOct = new Interval(
				new Note(Note.C),
				new Note(Note.c, AccidentalType.SHARP),
				null);
		assertEquals(Interval.OCTAVE, augOct.getLabel());
		assertEquals(Interval.AUGMENTED, augOct.getQuality());
		assertEquals(13, augOct.getSemitones());
		
	}
	
	public void test3renverse() {
		Interval revMinor3rd = Interval.reverseOrder(Interval.MINOR_THIRD);
		assertEquals(Interval.MINOR_THIRD.getLabel(), revMinor3rd.getLabel());
		assertEquals(Interval.MINOR_THIRD.getQuality(), revMinor3rd.getQuality());
		assertEquals(Interval.MINOR_THIRD.getSemitones(), revMinor3rd.getSemitones());
		assertEquals(Interval.DOWNWARD, revMinor3rd.getDirection());
	}
	
	public void test4invert() {
		Interval invMinor3rd = Interval.invert(Interval.MINOR_THIRD);
		assertEquals(Interval.SIXTH, invMinor3rd.getLabel());
		assertEquals(Interval.MAJOR, invMinor3rd.getQuality());
		assertEquals(Interval.MAJOR_SIXTH.getSemitones(), invMinor3rd.getSemitones());
		assertEquals(Interval.DOWNWARD, invMinor3rd.getDirection());
		
		Interval invUnison = Interval.invert(Interval.PERFECT_UNISON);
		assertEquals(Interval.OCTAVE, invUnison.getLabel());
		assertEquals(Interval.PERFECT, invUnison.getQuality());
		assertEquals(Interval.PERFECT_OCTAVE.getSemitones(), invUnison.getSemitones());
		assertEquals(Interval.DOWNWARD, invUnison.getDirection());

		//an octave + a fifth
		Interval downOctFifth = new Interval(
				new Note(Note.c, AccidentalType.NATURAL, (byte) 1),
				new Note(Note.F),
				null);
		assertTrue(downOctFifth.isCompound());
		Interval upFourth = Interval.invert(downOctFifth);
		assertEquals(Interval.FOURTH, upFourth.getLabel());
		assertEquals(Interval.PERFECT, upFourth.getQuality());
		assertEquals(Interval.UPWARD, upFourth.getDirection());
		assertFalse(upFourth.isCompound());
		
		//quadruple augmented
		Note n1 = new Note(Note.E, AccidentalType.DOUBLE_FLAT);
		Note n2 = new Note(Note.B, AccidentalType.DOUBLE_SHARP);
		Interval fifthQuadAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthQuadAug.getLabel());
		assertEquals(Interval.QUADRUPLE_AUGMENTED, fifthQuadAug.getQuality());
		assertEquals(11, fifthQuadAug.getSemitones());
		Interval invFifthQuadAug = Interval.invert(fifthQuadAug);
		assertEquals(Interval.FOURTH, invFifthQuadAug.getLabel());
		assertEquals(Interval.QUADRUPLE_DIMINISHED, invFifthQuadAug.getQuality());
		assertEquals(1, invFifthQuadAug.getSemitones());
	}
	
	public void test5compound() {
		assertFalse(Interval.PERFECT_FOURTH.isCompound());
		assertFalse(Interval.PERFECT_OCTAVE.isCompound());
		assertEquals(0, Interval.PERFECT_FOURTH.getOctaveNumber());
		assertEquals(1, Interval.PERFECT_OCTAVE.getOctaveNumber());
		
		Interval compound = new Interval((byte) (Interval.OCTAVE + Interval.THIRD),
									Interval.MINOR);
		assertTrue(compound.isCompound());
		assertEquals(1, compound.getOctaveNumber());

		compound = new Interval((byte) (3*Interval.OCTAVE + Interval.THIRD),
				Interval.MINOR, Interval.DOWNWARD);
		assertTrue(compound.isCompound());
		assertEquals(3, compound.getOctaveNumber());

	}
	
	private boolean areSimilar(Note n1, Note n2) {
		boolean ret = (n1.getStrictHeight() == n2.getStrictHeight())
			&& (n1.getOctaveTransposition() == n2.getOctaveTransposition())
			&& (n1.getAccidental() == n2.getAccidental());
		if (!ret) {
			System.err.println(n1 + " and " + n2 + " are not similar, test will fail");
		}
		return ret;
	}
	
	public void test6calculateSecondNote() {
	//	Note Cbb = new Note(Note.C, AccidentalType.DOUBLE_FLAT);
		Note Cb = new Note(Note.C, AccidentalType.FLAT);
		Note C = new Note(Note.C, AccidentalType.NATURAL);
		Note Cs = new Note(Note.C, AccidentalType.SHARP);
		Note Css = new Note(Note.C, AccidentalType.DOUBLE_SHARP);
		Note Dbb = new Note(Note.D, AccidentalType.DOUBLE_FLAT);
		Note Db = new Note(Note.D, AccidentalType.FLAT);
		Note D = new Note(Note.D, AccidentalType.NATURAL);
		Note Ds = new Note(Note.D, AccidentalType.SHARP);
		Note Dss = new Note(Note.D, AccidentalType.DOUBLE_SHARP);
		Note Ebb = new Note(Note.E, AccidentalType.DOUBLE_FLAT);
		Note Eb = new Note(Note.E, AccidentalType.FLAT);
		Note E = new Note(Note.E, AccidentalType.NATURAL);
		Note Es = new Note(Note.E, AccidentalType.SHARP);
		Note Ess = new Note(Note.E, AccidentalType.DOUBLE_SHARP);
		Note Fbb = new Note(Note.F, AccidentalType.DOUBLE_FLAT);
		Note Fb = new Note(Note.F, AccidentalType.FLAT);
		Note F = new Note(Note.F, AccidentalType.NATURAL);
		Note Fs = new Note(Note.F, AccidentalType.SHARP);
		Note Fss = new Note(Note.F, AccidentalType.DOUBLE_SHARP);
		Note Gbb = new Note(Note.G, AccidentalType.DOUBLE_FLAT);
		Note Gb = new Note(Note.G, AccidentalType.FLAT);
		Note G = new Note(Note.G, AccidentalType.NATURAL);
		Note Gs = new Note(Note.G, AccidentalType.SHARP);
		Note Gss = new Note(Note.G, AccidentalType.DOUBLE_SHARP);
		Note Abb = new Note(Note.A, AccidentalType.DOUBLE_FLAT);
		Note Ab = new Note(Note.A, AccidentalType.FLAT);
		Note A = new Note(Note.A, AccidentalType.NATURAL);
		Note As = new Note(Note.A, AccidentalType.SHARP);
		Note Ass = new Note(Note.A, AccidentalType.DOUBLE_SHARP);
		Note Bbb = new Note(Note.B, AccidentalType.DOUBLE_FLAT);
		Note Bb = new Note(Note.B, AccidentalType.FLAT);
		Note B = new Note(Note.B, AccidentalType.NATURAL);
		Note Bs = new Note(Note.B, AccidentalType.SHARP);
		Note Bss = new Note(Note.B, AccidentalType.DOUBLE_SHARP);
		Note cbb = new Note(Note.c, AccidentalType.DOUBLE_FLAT);
		Note cb = new Note(Note.c, AccidentalType.FLAT);
		Note c = new Note(Note.c, AccidentalType.NATURAL);
		Note cs = new Note(Note.c, AccidentalType.SHARP);
		Note css = new Note(Note.c, AccidentalType.DOUBLE_SHARP);
		Note dbb = new Note(Note.d, AccidentalType.DOUBLE_FLAT);
		Note db = new Note(Note.d, AccidentalType.FLAT);
		Note d = new Note(Note.d, AccidentalType.NATURAL);
		Note ds = new Note(Note.d, AccidentalType.SHARP);
		Note dss = new Note(Note.d, AccidentalType.DOUBLE_SHARP);
		Note ebb = new Note(Note.e, AccidentalType.DOUBLE_FLAT);
		Note eb = new Note(Note.e, AccidentalType.FLAT);
		Note e = new Note(Note.e, AccidentalType.NATURAL);
		Note es = new Note(Note.e, AccidentalType.SHARP);
	//	Note ess = new Note(Note.e, AccidentalType.DOUBLE_SHARP);
	//	Note fbb = new Note(Note.f, AccidentalType.DOUBLE_FLAT);
		Note fb = new Note(Note.f, AccidentalType.FLAT);
		Note f = new Note(Note.f, AccidentalType.NATURAL);
		Note fs = new Note(Note.f, AccidentalType.SHARP);
		Note fss = new Note(Note.f, AccidentalType.DOUBLE_SHARP);
	//	Note gbb = new Note(Note.g, AccidentalType.DOUBLE_FLAT);
		Note gb = new Note(Note.g, AccidentalType.FLAT);
		Note g = new Note(Note.g, AccidentalType.NATURAL);
		Note gs = new Note(Note.g, AccidentalType.SHARP);
		Note gss = new Note(Note.g, AccidentalType.DOUBLE_SHARP);
	//	Note abb = new Note(Note.a, AccidentalType.DOUBLE_FLAT);
		Note ab = new Note(Note.a, AccidentalType.FLAT);
		Note a = new Note(Note.a, AccidentalType.NATURAL);
		Note as = new Note(Note.a, AccidentalType.SHARP);
		Note ass = new Note(Note.a, AccidentalType.DOUBLE_SHARP);
	//	Note bbb = new Note(Note.b, AccidentalType.DOUBLE_FLAT);
		Note bb = new Note(Note.b, AccidentalType.FLAT);
		Note b = new Note(Note.b, AccidentalType.NATURAL);
		Note bs = new Note(Note.b, AccidentalType.SHARP);
	//	Note bss = new Note(Note.b, AccidentalType.DOUBLE_SHARP);
		Note c2 = new Note(Note.C, AccidentalType.NATURAL, (byte) 2);

		//minor
		Interval secondMin = new Interval(Interval.SECOND, Interval.MINOR);
		assertTrue(areSimilar(Db, secondMin.calculateSecondNote(C)));
		assertTrue(areSimilar(c, secondMin.calculateSecondNote(B)));
		assertTrue(areSimilar(E, secondMin.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Fb, secondMin.calculateSecondNote(Eb)));
		Interval revSecondMin = Interval.reverseOrder(secondMin);
		assertTrue(areSimilar(B, revSecondMin.calculateSecondNote(c)));
		assertTrue(areSimilar(As, revSecondMin.calculateSecondNote(B)));
		assertTrue(areSimilar(css, revSecondMin.calculateSecondNote(ds)));
		assertTrue(areSimilar(d, revSecondMin.calculateSecondNote(eb)));
		Interval thirdMin = new Interval(Interval.THIRD, Interval.MINOR);
		assertTrue(areSimilar(Eb, thirdMin.calculateSecondNote(C)));
		assertTrue(areSimilar(d, thirdMin.calculateSecondNote(B)));
		assertTrue(areSimilar(Fs, thirdMin.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Gb, thirdMin.calculateSecondNote(Eb)));
		Interval revThirdMin = Interval.reverseOrder(thirdMin);
		assertTrue(areSimilar(A, revThirdMin.calculateSecondNote(c)));
		assertTrue(areSimilar(Gs, revThirdMin.calculateSecondNote(B)));
		assertTrue(areSimilar(Bs, revThirdMin.calculateSecondNote(ds)));
		assertTrue(areSimilar(c, revThirdMin.calculateSecondNote(eb)));
		Interval sixthMin = new Interval(Interval.SIXTH, Interval.MINOR);
		assertTrue(areSimilar(Ab, sixthMin.calculateSecondNote(C)));
		assertTrue(areSimilar(g, sixthMin.calculateSecondNote(B)));
		assertTrue(areSimilar(B, sixthMin.calculateSecondNote(Ds)));
		assertTrue(areSimilar(cb, sixthMin.calculateSecondNote(Eb)));
		Interval revSixthMin = Interval.reverseOrder(sixthMin);
		assertTrue(areSimilar(E, revSixthMin.calculateSecondNote(c)));
		assertTrue(areSimilar(Ds, revSixthMin.calculateSecondNote(B)));
		assertTrue(areSimilar(Fss, revSixthMin.calculateSecondNote(ds)));
		assertTrue(areSimilar(G, revSixthMin.calculateSecondNote(eb)));
		Interval seventhMin = new Interval(Interval.SEVENTH, Interval.MINOR);
		assertTrue(areSimilar(Bb, seventhMin.calculateSecondNote(C)));
		assertTrue(areSimilar(a, seventhMin.calculateSecondNote(B)));
		assertTrue(areSimilar(cs, seventhMin.calculateSecondNote(Ds)));
		assertTrue(areSimilar(db, seventhMin.calculateSecondNote(Eb)));
		Interval revSeventhMin = Interval.reverseOrder(seventhMin);
		assertTrue(areSimilar(D, revSeventhMin.calculateSecondNote(c)));
		assertTrue(areSimilar(Cs, revSeventhMin.calculateSecondNote(B)));
		assertTrue(areSimilar(Es, revSeventhMin.calculateSecondNote(ds)));
		assertTrue(areSimilar(F, revSeventhMin.calculateSecondNote(eb)));
		Interval ninthMin = new Interval(Interval.NINTH, Interval.MINOR);
		assertTrue(areSimilar(db, ninthMin.calculateSecondNote(C)));
		assertTrue(areSimilar(c2, ninthMin.calculateSecondNote(B)));
		assertTrue(areSimilar(e, ninthMin.calculateSecondNote(Ds)));
		assertTrue(areSimilar(fb, ninthMin.calculateSecondNote(Eb)));
		Interval revNinthMin = Interval.reverseOrder(ninthMin);
		assertTrue(areSimilar(B, revNinthMin.calculateSecondNote(c2)));
		assertTrue(areSimilar(As, revNinthMin.calculateSecondNote(b)));
		assertTrue(areSimilar(Css, revNinthMin.calculateSecondNote(ds)));
		assertTrue(areSimilar(D, revNinthMin.calculateSecondNote(eb)));

		//perfect
		Interval unison = new Interval(Interval.UNISON, Interval.PERFECT);
		assertTrue(areSimilar(C, unison.calculateSecondNote(C)));
		assertTrue(areSimilar(B, unison.calculateSecondNote(B)));
		assertTrue(areSimilar(ds, unison.calculateSecondNote(ds)));
		assertTrue(areSimilar(eb, unison.calculateSecondNote(eb)));
		Interval revUnison = Interval.reverseOrder(unison);
		assertTrue(areSimilar(C, revUnison.calculateSecondNote(C)));
		assertTrue(areSimilar(B, revUnison.calculateSecondNote(B)));
		assertTrue(areSimilar(ds, revUnison.calculateSecondNote(ds)));
		assertTrue(areSimilar(eb, revUnison.calculateSecondNote(eb)));
		Interval fourth = new Interval(Interval.FOURTH, Interval.PERFECT);
		assertTrue(areSimilar(F, fourth.calculateSecondNote(C)));
		assertTrue(areSimilar(e, fourth.calculateSecondNote(B)));
		assertTrue(areSimilar(Gs, fourth.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Ab, fourth.calculateSecondNote(Eb)));
		assertTrue(areSimilar(Bb, fourth.calculateSecondNote(F)));
		assertTrue(areSimilar(As, fourth.calculateSecondNote(Es)));
		Interval revFourth = Interval.reverseOrder(fourth);
		assertTrue(areSimilar(G, revFourth.calculateSecondNote(c)));
		assertTrue(areSimilar(Fs, revFourth.calculateSecondNote(B)));
		assertTrue(areSimilar(As, revFourth.calculateSecondNote(ds)));
		assertTrue(areSimilar(Bb, revFourth.calculateSecondNote(eb)));
		assertTrue(areSimilar(F, revFourth.calculateSecondNote(Bb)));
		assertTrue(areSimilar(Es, revFourth.calculateSecondNote(As)));
		Interval fifth = new Interval(Interval.FIFTH, Interval.PERFECT);
		assertTrue(areSimilar(G, fifth.calculateSecondNote(C)));
		assertTrue(areSimilar(fs, fifth.calculateSecondNote(B)));
		assertTrue(areSimilar(As, fifth.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Bb, fifth.calculateSecondNote(Eb)));
		assertTrue(areSimilar(f, fifth.calculateSecondNote(Bb)));
		assertTrue(areSimilar(es, fifth.calculateSecondNote(As)));
		Interval revFifth = Interval.reverseOrder(fifth);
		assertTrue(areSimilar(F, revFifth.calculateSecondNote(c)));
		assertTrue(areSimilar(E, revFifth.calculateSecondNote(B)));
		assertTrue(areSimilar(Gs, revFifth.calculateSecondNote(ds)));
		assertTrue(areSimilar(Ab, revFifth.calculateSecondNote(eb)));
		assertTrue(areSimilar(Bb, revFifth.calculateSecondNote(f)));
		assertTrue(areSimilar(As, revFifth.calculateSecondNote(es)));
		Interval octave = new Interval(Interval.OCTAVE, Interval.PERFECT);
		assertTrue(areSimilar(c, octave.calculateSecondNote(C)));
		assertTrue(areSimilar(b, octave.calculateSecondNote(B)));
		assertTrue(areSimilar(ds, octave.calculateSecondNote(Ds)));
		assertTrue(areSimilar(eb, octave.calculateSecondNote(Eb)));
		Interval revOctave = Interval.reverseOrder(octave);
		assertTrue(areSimilar(C, revOctave.calculateSecondNote(c)));
		assertTrue(areSimilar(B, revOctave.calculateSecondNote(b)));
		assertTrue(areSimilar(Ds, revOctave.calculateSecondNote(ds)));
		assertTrue(areSimilar(Eb, revOctave.calculateSecondNote(eb)));

		//major
		Interval secondMaj = new Interval(Interval.SECOND, Interval.MAJOR);
		assertTrue(areSimilar(D, secondMaj.calculateSecondNote(C)));
		assertTrue(areSimilar(cs, secondMaj.calculateSecondNote(B)));
		assertTrue(areSimilar(Es, secondMaj.calculateSecondNote(Ds)));
		assertTrue(areSimilar(F, secondMaj.calculateSecondNote(Eb)));
		Interval revSecondMaj = Interval.reverseOrder(secondMaj);
		assertTrue(areSimilar(Bb, revSecondMaj.calculateSecondNote(c)));
		assertTrue(areSimilar(A, revSecondMaj.calculateSecondNote(B)));
		assertTrue(areSimilar(cs, revSecondMaj.calculateSecondNote(ds)));
		assertTrue(areSimilar(db, revSecondMaj.calculateSecondNote(eb)));
		Interval thirdMaj = new Interval(Interval.THIRD, Interval.MAJOR);
		assertTrue(areSimilar(E, thirdMaj.calculateSecondNote(C)));
		assertTrue(areSimilar(ds, thirdMaj.calculateSecondNote(B)));
		assertTrue(areSimilar(Fss, thirdMaj.calculateSecondNote(Ds)));
		assertTrue(areSimilar(G, thirdMaj.calculateSecondNote(Eb)));
		assertTrue(areSimilar(Es, thirdMaj.calculateSecondNote(Cs)));
		Interval revThirdMaj = Interval.reverseOrder(thirdMaj);
		assertTrue(areSimilar(Ab, revThirdMaj.calculateSecondNote(c)));
		assertTrue(areSimilar(G, revThirdMaj.calculateSecondNote(B)));
		assertTrue(areSimilar(B, revThirdMaj.calculateSecondNote(ds)));
		assertTrue(areSimilar(cb, revThirdMaj.calculateSecondNote(eb)));
		assertTrue(areSimilar(Cs, revThirdMaj.calculateSecondNote(Es)));
		Interval sixthMaj = new Interval(Interval.SIXTH, Interval.MAJOR);
		assertTrue(areSimilar(A, sixthMaj.calculateSecondNote(C)));
		assertTrue(areSimilar(gs, sixthMaj.calculateSecondNote(B)));
		assertTrue(areSimilar(Bs, sixthMaj.calculateSecondNote(Ds)));
		assertTrue(areSimilar(c, sixthMaj.calculateSecondNote(Eb)));
		Interval revSixthMaj = Interval.reverseOrder(sixthMaj);
		assertTrue(areSimilar(Eb, revSixthMaj.calculateSecondNote(c)));
		assertTrue(areSimilar(D, revSixthMaj.calculateSecondNote(B)));
		assertTrue(areSimilar(Fs, revSixthMaj.calculateSecondNote(ds)));
		assertTrue(areSimilar(Gb, revSixthMaj.calculateSecondNote(eb)));
		Interval seventhMaj = new Interval(Interval.SEVENTH, Interval.MAJOR);
		assertTrue(areSimilar(B, seventhMaj.calculateSecondNote(C)));
		assertTrue(areSimilar(as, seventhMaj.calculateSecondNote(B)));
		assertTrue(areSimilar(css, seventhMaj.calculateSecondNote(Ds)));
		assertTrue(areSimilar(d, seventhMaj.calculateSecondNote(Eb)));
		Interval revSeventhMaj = Interval.reverseOrder(seventhMaj);
		assertTrue(areSimilar(Db, revSeventhMaj.calculateSecondNote(c)));
		assertTrue(areSimilar(C, revSeventhMaj.calculateSecondNote(B)));
		assertTrue(areSimilar(E, revSeventhMaj.calculateSecondNote(ds)));
		assertTrue(areSimilar(Fb, revSeventhMaj.calculateSecondNote(eb)));

		//augmented
		Interval unisonAug = new Interval(Interval.UNISON, Interval.AUGMENTED);
		assertTrue(areSimilar(Cs, unisonAug.calculateSecondNote(C)));
		assertTrue(areSimilar(Bs, unisonAug.calculateSecondNote(B)));
		assertTrue(areSimilar(dss, unisonAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(e, unisonAug.calculateSecondNote(eb)));
		Interval revUnisonAug = Interval.reverseOrder(unisonAug);
		assertTrue(areSimilar(Cb, revUnisonAug.calculateSecondNote(C)));
		assertTrue(areSimilar(Bb, revUnisonAug.calculateSecondNote(B)));
		assertTrue(areSimilar(d, revUnisonAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(ebb, revUnisonAug.calculateSecondNote(eb)));
		Interval secondAug = new Interval(Interval.SECOND, Interval.AUGMENTED);
		assertTrue(areSimilar(Ds, secondAug.calculateSecondNote(C)));
		assertTrue(areSimilar(css, secondAug.calculateSecondNote(B)));
		assertTrue(areSimilar(Ess, secondAug.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Fs, secondAug.calculateSecondNote(Eb)));
		Interval revSecondAug = Interval.reverseOrder(secondAug);
		assertTrue(areSimilar(Bbb, revSecondAug.calculateSecondNote(c)));
		assertTrue(areSimilar(Ab, revSecondAug.calculateSecondNote(B)));
		assertTrue(areSimilar(c, revSecondAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(dbb, revSecondAug.calculateSecondNote(eb)));
		Interval thirdAug = new Interval(Interval.THIRD, Interval.AUGMENTED);
		assertTrue(areSimilar(Es, thirdAug.calculateSecondNote(C)));
		assertTrue(areSimilar(dss, thirdAug.calculateSecondNote(B)));
		assertTrue(areSimilar(Gs, thirdAug.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Gs, thirdAug.calculateSecondNote(Eb)));
		Interval revThirdAug = Interval.reverseOrder(thirdAug);
		assertTrue(areSimilar(Abb, revThirdAug.calculateSecondNote(c)));
		assertTrue(areSimilar(Gb, revThirdAug.calculateSecondNote(B)));
		assertTrue(areSimilar(Bb, revThirdAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(cbb, revThirdAug.calculateSecondNote(eb)));
		Interval fourthAug = new Interval(Interval.FOURTH, Interval.AUGMENTED);
		assertTrue(areSimilar(Fs, fourthAug.calculateSecondNote(C)));
		assertTrue(areSimilar(es, fourthAug.calculateSecondNote(B)));
		assertTrue(areSimilar(Gss, fourthAug.calculateSecondNote(Ds)));
		assertTrue(areSimilar(A, fourthAug.calculateSecondNote(Eb)));
		Interval revFourthAug = Interval.reverseOrder(fourthAug);
		assertTrue(areSimilar(Gb, revFourthAug.calculateSecondNote(c)));
		assertTrue(areSimilar(F, revFourthAug.calculateSecondNote(B)));
		assertTrue(areSimilar(A, revFourthAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(Bbb, revFourthAug.calculateSecondNote(eb)));
		Interval fifthAug = new Interval(Interval.FIFTH, Interval.AUGMENTED);
		assertTrue(areSimilar(Gs, fifthAug.calculateSecondNote(C)));
		assertTrue(areSimilar(fss, fifthAug.calculateSecondNote(B)));
		assertTrue(areSimilar(Ass, fifthAug.calculateSecondNote(Ds)));
		assertTrue(areSimilar(B, fifthAug.calculateSecondNote(Eb)));
		Interval revFifthAug = Interval.reverseOrder(fifthAug);
		assertTrue(areSimilar(Fb, revFifthAug.calculateSecondNote(c)));
		assertTrue(areSimilar(Eb, revFifthAug.calculateSecondNote(B)));
		assertTrue(areSimilar(G, revFifthAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(Abb, revFifthAug.calculateSecondNote(eb)));
		Interval sixthAug = new Interval(Interval.SIXTH, Interval.AUGMENTED);
		assertTrue(areSimilar(As, sixthAug.calculateSecondNote(C)));
		assertTrue(areSimilar(gss, sixthAug.calculateSecondNote(B)));
		assertTrue(areSimilar(Bss, sixthAug.calculateSecondNote(Ds)));
		assertTrue(areSimilar(cs, sixthAug.calculateSecondNote(Eb)));
		Interval revSixthAug = Interval.reverseOrder(sixthAug);
		assertTrue(areSimilar(Ebb, revSixthAug.calculateSecondNote(c)));
		assertTrue(areSimilar(Db, revSixthAug.calculateSecondNote(B)));
		assertTrue(areSimilar(F, revSixthAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(Gbb, revSixthAug.calculateSecondNote(eb)));
		Interval seventhAug = new Interval(Interval.SEVENTH, Interval.AUGMENTED);
		assertTrue(areSimilar(Bs, seventhAug.calculateSecondNote(C)));
		assertTrue(areSimilar(ass, seventhAug.calculateSecondNote(B)));
		assertTrue(areSimilar(ds, seventhAug.calculateSecondNote(Ds)));
		assertTrue(areSimilar(ds, seventhAug.calculateSecondNote(Eb)));
		Interval revSeventhAug = Interval.reverseOrder(seventhAug);
		assertTrue(areSimilar(Dbb, revSeventhAug.calculateSecondNote(c)));
		assertTrue(areSimilar(Cb, revSeventhAug.calculateSecondNote(B)));
		assertTrue(areSimilar(Eb, revSeventhAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(Fbb, revSeventhAug.calculateSecondNote(eb)));
		Interval octaveAug = new Interval(Interval.OCTAVE, Interval.AUGMENTED);
		assertTrue(areSimilar(cs, octaveAug.calculateSecondNote(C)));
		assertTrue(areSimilar(bs, octaveAug.calculateSecondNote(B)));
		assertTrue(areSimilar(dss, octaveAug.calculateSecondNote(Ds)));
		assertTrue(areSimilar(e, octaveAug.calculateSecondNote(Eb)));
		Interval revOctaveAug = Interval.reverseOrder(octaveAug);
		assertTrue(areSimilar(Cb, revOctaveAug.calculateSecondNote(c)));
		assertTrue(areSimilar(Bb, revOctaveAug.calculateSecondNote(b)));
		assertTrue(areSimilar(D, revOctaveAug.calculateSecondNote(ds)));
		assertTrue(areSimilar(Ebb, revOctaveAug.calculateSecondNote(eb)));

		//diminished
		Interval unisonDim = new Interval(Interval.UNISON, Interval.DIMINISHED);
		assertTrue(areSimilar(Cb, unisonDim.calculateSecondNote(C)));
		assertTrue(areSimilar(Bb, unisonDim.calculateSecondNote(B)));
		assertTrue(areSimilar(d, unisonDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(ebb, unisonDim.calculateSecondNote(eb)));
		Interval revUnisonDim = Interval.reverseOrder(unisonDim);
		assertTrue(areSimilar(Cs, revUnisonDim.calculateSecondNote(C)));
		assertTrue(areSimilar(Bs, revUnisonDim.calculateSecondNote(B)));
		assertTrue(areSimilar(dss, revUnisonDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(e, revUnisonDim.calculateSecondNote(eb)));
		Interval secondDim = new Interval(Interval.SECOND, Interval.DIMINISHED);
		assertTrue(areSimilar(Dbb, secondDim.calculateSecondNote(C)));
		assertTrue(areSimilar(cb, secondDim.calculateSecondNote(B)));
		assertTrue(areSimilar(Eb, secondDim.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Fbb, secondDim.calculateSecondNote(Eb)));
		Interval revSecondDim = Interval.reverseOrder(secondDim);
		assertTrue(areSimilar(Bs, revSecondDim.calculateSecondNote(c)));
		assertTrue(areSimilar(Ass, revSecondDim.calculateSecondNote(B)));
		assertTrue(areSimilar(ds, revSecondDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(ds, revSecondDim.calculateSecondNote(eb)));
		Interval thirdDim = new Interval(Interval.THIRD, Interval.DIMINISHED);
		assertTrue(areSimilar(Ebb, thirdDim.calculateSecondNote(C)));
		assertTrue(areSimilar(db, thirdDim.calculateSecondNote(B)));
		assertTrue(areSimilar(F, thirdDim.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Gbb, thirdDim.calculateSecondNote(Eb)));
		Interval revThirdDim = Interval.reverseOrder(thirdDim);
		assertTrue(areSimilar(As, revThirdDim.calculateSecondNote(c)));
		assertTrue(areSimilar(Gss, revThirdDim.calculateSecondNote(B)));
		assertTrue(areSimilar(Bss, revThirdDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(cs, revThirdDim.calculateSecondNote(eb)));
		Interval fourthDim = new Interval(Interval.FOURTH, Interval.DIMINISHED);
		assertTrue(areSimilar(Fb, fourthDim.calculateSecondNote(C)));
		assertTrue(areSimilar(eb, fourthDim.calculateSecondNote(B)));
		assertTrue(areSimilar(G, fourthDim.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Abb, fourthDim.calculateSecondNote(Eb)));
		Interval revFourthDim = Interval.reverseOrder(fourthDim);
		assertTrue(areSimilar(Gs, revFourthDim.calculateSecondNote(c)));
		assertTrue(areSimilar(Fss, revFourthDim.calculateSecondNote(B)));
		assertTrue(areSimilar(Ass, revFourthDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(B, revFourthDim.calculateSecondNote(eb)));
		Interval fifthDim = new Interval(Interval.FIFTH, Interval.DIMINISHED);
		assertTrue(areSimilar(Gb, fifthDim.calculateSecondNote(C)));
		assertTrue(areSimilar(f, fifthDim.calculateSecondNote(B)));
		assertTrue(areSimilar(A, fifthDim.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Bbb, fifthDim.calculateSecondNote(Eb)));
		Interval revFifthDim = Interval.reverseOrder(fifthDim);
		assertTrue(areSimilar(Fs, revFifthDim.calculateSecondNote(c)));
		assertTrue(areSimilar(Es, revFifthDim.calculateSecondNote(B)));
		assertTrue(areSimilar(Gss, revFifthDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(A, revFifthDim.calculateSecondNote(eb)));
		Interval sixthDim = new Interval(Interval.SIXTH, Interval.DIMINISHED);
		assertTrue(areSimilar(Abb, sixthDim.calculateSecondNote(C)));
		assertTrue(areSimilar(gb, sixthDim.calculateSecondNote(B)));
		assertTrue(areSimilar(Bb, sixthDim.calculateSecondNote(Ds)));
		assertTrue(areSimilar(cbb, sixthDim.calculateSecondNote(Eb)));
		Interval revSixthDim = Interval.reverseOrder(sixthDim);
		assertTrue(areSimilar(Es, revSixthDim.calculateSecondNote(c)));
		assertTrue(areSimilar(Dss, revSixthDim.calculateSecondNote(B)));
		assertTrue(areSimilar(Gs, revSixthDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(Gs, revSixthDim.calculateSecondNote(eb)));
		Interval seventhDim = new Interval(Interval.SEVENTH, Interval.DIMINISHED);
		assertTrue(areSimilar(Bbb, seventhDim.calculateSecondNote(C)));
		assertTrue(areSimilar(ab, seventhDim.calculateSecondNote(B)));
		assertTrue(areSimilar(c, seventhDim.calculateSecondNote(Ds)));
		assertTrue(areSimilar(dbb, seventhDim.calculateSecondNote(Eb)));
		Interval revSeventhDim = Interval.reverseOrder(seventhDim);
		assertTrue(areSimilar(Ds, revSeventhDim.calculateSecondNote(c)));
		assertTrue(areSimilar(Css, revSeventhDim.calculateSecondNote(B)));
		assertTrue(areSimilar(Ess, revSeventhDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(Fs, revSeventhDim.calculateSecondNote(eb)));
		Interval octaveDim = new Interval(Interval.OCTAVE, Interval.DIMINISHED);
		assertTrue(areSimilar(cb, octaveDim.calculateSecondNote(C)));
		assertTrue(areSimilar(bb, octaveDim.calculateSecondNote(B)));
		assertTrue(areSimilar(d, octaveDim.calculateSecondNote(Ds)));
		assertTrue(areSimilar(ebb, octaveDim.calculateSecondNote(Eb)));
		Interval revOctaveDim = Interval.reverseOrder(octaveDim);
		assertTrue(areSimilar(Cs, revOctaveDim.calculateSecondNote(c)));
		assertTrue(areSimilar(Bs, revOctaveDim.calculateSecondNote(b)));
		assertTrue(areSimilar(Dss, revOctaveDim.calculateSecondNote(ds)));
		assertTrue(areSimilar(E, revOctaveDim.calculateSecondNote(eb)));

		//double augmented
		Interval fifthAug2 = new Interval(Interval.FIFTH, Interval.DOUBLE_AUGMENTED);
		assertTrue(areSimilar(Gss, fifthAug2.calculateSecondNote(C)));
		assertTrue(areSimilar(gs, fifthAug2.calculateSecondNote(B)));
		assertTrue(areSimilar(Bs, fifthAug2.calculateSecondNote(Ds)));
		assertTrue(areSimilar(c, fifthAug2.calculateSecondNote(Eb)));
		Interval revFifthAug2 = Interval.reverseOrder(fifthAug2);
		//would prefer Fbb... but it's rare...
		assertTrue(areSimilar(Eb, revFifthAug2.calculateSecondNote(c)));
		assertTrue(areSimilar(Ebb, revFifthAug2.calculateSecondNote(B)));
		assertTrue(areSimilar(Fs, revFifthAug2.calculateSecondNote(ds)));
		assertTrue(areSimilar(Gb, revFifthAug2.calculateSecondNote(eb)));
		
		//triple augmented
		Interval fifthAug3 = new Interval(Interval.FIFTH, Interval.TRIPLE_AUGMENTED);
		assertTrue(areSimilar(As, fifthAug3.calculateSecondNote(C)));
		assertTrue(areSimilar(gss, fifthAug3.calculateSecondNote(B)));
		assertTrue(areSimilar(Bss, fifthAug3.calculateSecondNote(Ds)));
		assertTrue(areSimilar(cs, fifthAug3.calculateSecondNote(Eb)));
		Interval revFifthAug3 = Interval.reverseOrder(fifthAug3);
		assertTrue(areSimilar(Ebb, revFifthAug3.calculateSecondNote(c)));
		assertTrue(areSimilar(Db, revFifthAug3.calculateSecondNote(B)));
		assertTrue(areSimilar(F, revFifthAug3.calculateSecondNote(ds)));
		assertTrue(areSimilar(Gbb, revFifthAug3.calculateSecondNote(eb)));
	
		//quadruple augmented
		Interval fifthAug4 = new Interval(Interval.FIFTH, Interval.QUADRUPLE_AUGMENTED);
		assertTrue(areSimilar(Ass, fifthAug4.calculateSecondNote(C)));
		assertTrue(areSimilar(as, fifthAug4.calculateSecondNote(B)));
		assertTrue(areSimilar(css, fifthAug4.calculateSecondNote(Ds)));
		assertTrue(areSimilar(d, fifthAug4.calculateSecondNote(Eb)));
		Interval revFifthAug4 = Interval.reverseOrder(fifthAug4);
		assertTrue(areSimilar(Db, revFifthAug4.calculateSecondNote(c)));
		assertTrue(areSimilar(Dbb, revFifthAug4.calculateSecondNote(B)));
		assertTrue(areSimilar(E, revFifthAug4.calculateSecondNote(ds)));
		assertTrue(areSimilar(Fb, revFifthAug4.calculateSecondNote(eb)));
		
		//with key signature
		G = fifth.calculateSecondNote(C,
				new KeySignature(Note.C, KeySignature.MINOR));
		assertEquals(Note.G, G.getStrictHeight());
		assertEquals(0, G.getOctaveTransposition());
		assertEquals(AccidentalType.NONE, G.getAccidental());
		Gs = fifth.calculateSecondNote(new Note(Note.C, AccidentalType.NONE),
				new KeySignature(Note.A, KeySignature.MAJOR));
		assertEquals(Note.G, Gs.getStrictHeight());
		assertEquals(0, Gs.getOctaveTransposition());
		assertEquals(AccidentalType.NONE, Gs.getAccidental());
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

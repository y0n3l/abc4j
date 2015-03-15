import junit.framework.TestCase;
import abc.notation.Accidental;
import abc.notation.Interval;
import abc.notation.KeySignature;
import abc.notation.Note;

public class IntervalTests extends TestCase {
	
	public IntervalTests(String name) {
		super(name);
	}
	
	private boolean areSimilar(Note n1, Note n2) {
		boolean ret = (n1.getStrictHeight() == n2.getStrictHeight())
			&& (n1.getOctaveTransposition() == n2.getOctaveTransposition())
			&& (n1.getAccidental().equals(n2.getAccidental()));
		if (!ret) {
			System.err.println(n1 + " and " + n2 + " are not similar, test will fail");
		}
		return ret;
	}
	
	private void assertEquals(String s, Note n) {
		assertEquals(s, n.toString());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
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
		Note n1 = new Note(Note.E, Accidental.FLAT);
		Note n2 = new Note(Note.B, Accidental.NONE);
		Interval fifthAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthAug.getLabel());
		assertEquals(Interval.AUGMENTED, fifthAug.getQuality());
		assertEquals(8, fifthAug.getSemitones());
		assertEquals(Interval.UPWARD, fifthAug.getDirection());
		
		KeySignature Gmin = new KeySignature(Note.G, Accidental.NATURAL, KeySignature.MINOR);
		//G minor has B flat and E flat
		//note B has no accidental (NONE)
		//our new interval will be understood as perfect fifth
		Interval fifthPerfect = new Interval(n1, n2, Gmin);
		assertEquals(Interval.FIFTH, fifthPerfect.getLabel());
		assertEquals(Interval.PERFECT, fifthPerfect.getQuality());
		assertEquals(7, fifthPerfect.getSemitones());

		//double augmented
		n2 = new Note(Note.B, Accidental.SHARP);
		Interval fifthDblAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthDblAug.getLabel());
		assertEquals(Interval.DOUBLE_AUGMENTED, fifthDblAug.getQuality());
		assertEquals(9, fifthDblAug.getSemitones());
		
		//triple augmented
		n2 = new Note(Note.B, Accidental.DOUBLE_SHARP);
		Interval fifthTripleAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthTripleAug.getLabel());
		assertEquals(Interval.TRIPLE_AUGMENTED, fifthTripleAug.getQuality());
		assertEquals(10, fifthTripleAug.getSemitones());

		//quadruple augmented
		n1 = new Note(Note.E, Accidental.DOUBLE_FLAT);
		Interval fifthQuadAug = new Interval(n1, n2, null);
		assertEquals(Interval.FIFTH, fifthQuadAug.getLabel());
		assertEquals(Interval.QUADRUPLE_AUGMENTED, fifthQuadAug.getQuality());
		assertEquals(11, fifthQuadAug.getSemitones());

		//diminished 2nd
		Interval dim2nd = new Interval(
				new Note(Note.C, Accidental.DOUBLE_SHARP),
				new Note(Note.D, Accidental.NATURAL),
				null);
		assertEquals(Interval.SECOND, dim2nd.getLabel());
		assertEquals(Interval.DIMINISHED, dim2nd.getQuality());
		assertEquals(0, dim2nd.getSemitones());
		assertEquals(Interval.UPWARD, dim2nd.getDirection());

		//augmented 2nd
		Interval aug2nd = new Interval(
				new Note(Note.e),
				new Note(Note.f, Accidental.DOUBLE_SHARP),
				null);
		assertEquals(Interval.SECOND, aug2nd.getLabel());
		assertEquals(Interval.AUGMENTED, aug2nd.getQuality());
		assertEquals(3, aug2nd.getSemitones());
		assertEquals(Interval.UPWARD, aug2nd.getDirection());
		
		Interval minor6th = new Interval(
				new Note(Note.D),
				new Note(Note.B, Accidental.FLAT),
				null);
		assertEquals(Interval.SIXTH, minor6th.getLabel());
		assertEquals(Interval.MINOR, minor6th.getQuality());
		assertEquals(8, minor6th.getSemitones());
		
		Interval minorOct6th = new Interval(
				new Note(Note.D),
				new Note(Note.b, Accidental.FLAT),
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
				new Note(Note.c, Accidental.NATURAL, (byte) 1),
				new Note(Note.F),
				null);
		assertEquals(Interval.OCTAVE+Interval.FIFTH, renversedOctFifth.getLabel());
		assertEquals(Interval.PERFECT, renversedOctFifth.getQuality());
		assertEquals(12+7, renversedOctFifth.getSemitones());
		assertEquals(Interval.DOWNWARD, renversedOctFifth.getDirection());
		
		//3 octaves
		Interval threeOct = new Interval(
				new Note(Note.C),
				new Note(Note.c, Accidental.NATURAL, (byte) 2),
				null);
		assertEquals(3*Interval.OCTAVE, threeOct.getLabel());
		assertEquals(Interval.PERFECT, threeOct.getQuality());
		assertEquals(3*12, threeOct.getSemitones());
		assertEquals(Interval.UPWARD, threeOct.getDirection());

		//renversed 3 octave
		Interval renvThreeOct = new Interval(
				new Note(Note.c, Accidental.NATURAL, (byte) 2),
				new Note(Note.C),
				null);
		assertEquals(3*Interval.OCTAVE, renvThreeOct.getLabel());
		assertEquals(Interval.PERFECT, renvThreeOct.getQuality());
		assertEquals(3*12, renvThreeOct.getSemitones());
		assertEquals(Interval.DOWNWARD, renvThreeOct.getDirection());

		//renversed 3 octave + major second
		Interval renvThreeAugOct = new Interval(
				new Note(Note.d, Accidental.NATURAL, (byte) 0),
				new Note(Note.C),
				null);
		assertEquals(1*Interval.OCTAVE + Interval.SECOND,
						renvThreeAugOct.getLabel());
		assertEquals(Interval.MAJOR, renvThreeAugOct.getQuality());
		assertEquals(1*12 + 2, renvThreeAugOct.getSemitones());
		
		Interval dimOct = new Interval(
				new Note(Note.C, Accidental.SHARP),
				new Note(Note.c),
				null);
		assertEquals(Interval.OCTAVE, dimOct.getLabel());
		assertEquals(Interval.DIMINISHED, dimOct.getQuality());
		assertEquals(11, dimOct.getSemitones());
		
		Interval dblDimOct = new Interval(
				new Note(Note.C, Accidental.SHARP),
				new Note(Note.c, Accidental.FLAT),
				null);
		assertEquals(Interval.OCTAVE, dblDimOct.getLabel());
		assertEquals(Interval.DOUBLE_DIMINISHED, dblDimOct.getQuality());
		assertEquals(10, dblDimOct.getSemitones());
	
		Interval augOct = new Interval(
				new Note(Note.C),
				new Note(Note.c, Accidental.SHARP),
				null);
		assertEquals(Interval.OCTAVE, augOct.getLabel());
		assertEquals(Interval.AUGMENTED, augOct.getQuality());
		assertEquals(13, augOct.getSemitones());
		
		Interval rev2ndDim = new Interval(
				new Note(Note.B, Accidental.FLAT),
				new Note(Note.A, Accidental.SHARP),
				null);
		assertEquals(Interval.SECOND, rev2ndDim.getLabel());
		assertEquals(Interval.DIMINISHED, rev2ndDim.getQuality());
		assertTrue(rev2ndDim.isDownward());
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
				new Note(Note.c, Accidental.NATURAL, (byte) 1),
				new Note(Note.F),
				null);
		assertTrue(downOctFifth.isCompound());
		Interval upFourth = Interval.invert(downOctFifth);
		assertEquals(Interval.FOURTH, upFourth.getLabel());
		assertEquals(Interval.PERFECT, upFourth.getQuality());
		assertEquals(Interval.UPWARD, upFourth.getDirection());
		assertFalse(upFourth.isCompound());
		
		//quadruple augmented
		Note n1 = new Note(Note.E, Accidental.DOUBLE_FLAT);
		Note n2 = new Note(Note.B, Accidental.DOUBLE_SHARP);
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
	
	public void test6calculateSecondNote() {
	//	Note Cbb = new Note(Note.C, Accidental.DOUBLE_FLAT);
		Note Cb = new Note(Note.C, Accidental.FLAT);
		Note C = new Note(Note.C, Accidental.NATURAL);
		Note Cs = new Note(Note.C, Accidental.SHARP);
		Note Css = new Note(Note.C, Accidental.DOUBLE_SHARP);
		Note Dbb = new Note(Note.D, Accidental.DOUBLE_FLAT);
		Note Db = new Note(Note.D, Accidental.FLAT);
		Note D = new Note(Note.D, Accidental.NATURAL);
		Note Ds = new Note(Note.D, Accidental.SHARP);
		Note Dss = new Note(Note.D, Accidental.DOUBLE_SHARP);
		Note Ebb = new Note(Note.E, Accidental.DOUBLE_FLAT);
		Note Eb = new Note(Note.E, Accidental.FLAT);
		Note E = new Note(Note.E, Accidental.NATURAL);
		Note Es = new Note(Note.E, Accidental.SHARP);
		Note Ess = new Note(Note.E, Accidental.DOUBLE_SHARP);
		Note Fbb = new Note(Note.F, Accidental.DOUBLE_FLAT);
		Note Fb = new Note(Note.F, Accidental.FLAT);
		Note F = new Note(Note.F, Accidental.NATURAL);
		Note Fs = new Note(Note.F, Accidental.SHARP);
		Note Fss = new Note(Note.F, Accidental.DOUBLE_SHARP);
		Note Gbb = new Note(Note.G, Accidental.DOUBLE_FLAT);
		Note Gb = new Note(Note.G, Accidental.FLAT);
		Note G = new Note(Note.G, Accidental.NATURAL);
		Note Gs = new Note(Note.G, Accidental.SHARP);
		Note Gss = new Note(Note.G, Accidental.DOUBLE_SHARP);
		Note Abb = new Note(Note.A, Accidental.DOUBLE_FLAT);
		Note Ab = new Note(Note.A, Accidental.FLAT);
		Note A = new Note(Note.A, Accidental.NATURAL);
		Note As = new Note(Note.A, Accidental.SHARP);
		Note Ass = new Note(Note.A, Accidental.DOUBLE_SHARP);
		Note Bbb = new Note(Note.B, Accidental.DOUBLE_FLAT);
		Note Bb = new Note(Note.B, Accidental.FLAT);
		Note B = new Note(Note.B, Accidental.NATURAL);
		Note Bs = new Note(Note.B, Accidental.SHARP);
		Note Bss = new Note(Note.B, Accidental.DOUBLE_SHARP);
		Note cbb = new Note(Note.c, Accidental.DOUBLE_FLAT);
		Note cb = new Note(Note.c, Accidental.FLAT);
		Note c = new Note(Note.c, Accidental.NATURAL);
		Note cs = new Note(Note.c, Accidental.SHARP);
		Note css = new Note(Note.c, Accidental.DOUBLE_SHARP);
		Note dbb = new Note(Note.d, Accidental.DOUBLE_FLAT);
		Note db = new Note(Note.d, Accidental.FLAT);
		Note d = new Note(Note.d, Accidental.NATURAL);
		Note ds = new Note(Note.d, Accidental.SHARP);
		Note dss = new Note(Note.d, Accidental.DOUBLE_SHARP);
		Note ebb = new Note(Note.e, Accidental.DOUBLE_FLAT);
		Note eb = new Note(Note.e, Accidental.FLAT);
		Note e = new Note(Note.e, Accidental.NATURAL);
		Note es = new Note(Note.e, Accidental.SHARP);
	//	Note ess = new Note(Note.e, Accidental.DOUBLE_SHARP);
	//	Note fbb = new Note(Note.f, Accidental.DOUBLE_FLAT);
		Note fb = new Note(Note.f, Accidental.FLAT);
		Note f = new Note(Note.f, Accidental.NATURAL);
		Note fs = new Note(Note.f, Accidental.SHARP);
		Note fss = new Note(Note.f, Accidental.DOUBLE_SHARP);
	//	Note gbb = new Note(Note.g, Accidental.DOUBLE_FLAT);
		Note gb = new Note(Note.g, Accidental.FLAT);
		Note g = new Note(Note.g, Accidental.NATURAL);
		Note gs = new Note(Note.g, Accidental.SHARP);
		Note gss = new Note(Note.g, Accidental.DOUBLE_SHARP);
	//	Note abb = new Note(Note.a, Accidental.DOUBLE_FLAT);
		Note ab = new Note(Note.a, Accidental.FLAT);
		Note a = new Note(Note.a, Accidental.NATURAL);
		Note as = new Note(Note.a, Accidental.SHARP);
		Note ass = new Note(Note.a, Accidental.DOUBLE_SHARP);
	//	Note bbb = new Note(Note.b, Accidental.DOUBLE_FLAT);
		Note bb = new Note(Note.b, Accidental.FLAT);
		Note b = new Note(Note.b, Accidental.NATURAL);
		Note bs = new Note(Note.b, Accidental.SHARP);
	//	Note bss = new Note(Note.b, Accidental.DOUBLE_SHARP);
		Note c2 = new Note(Note.C, Accidental.NATURAL, (byte) 2);

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
		assertTrue(areSimilar(Bs, fifthAug2.calculateSecondNote(Eb)));
		Interval revFifthAug2 = Interval.reverseOrder(fifthAug2);
		//would prefer Fbb... but it's rare...
		assertTrue(areSimilar(Fbb, revFifthAug2.calculateSecondNote(c)));
		assertTrue(areSimilar(Ebb, revFifthAug2.calculateSecondNote(B)));
		assertTrue(areSimilar(Gb, revFifthAug2.calculateSecondNote(ds)));
		assertTrue(areSimilar(Gb, revFifthAug2.calculateSecondNote(eb)));
		
		//triple augmented
		Interval fifthAug3 = new Interval(Interval.FIFTH, Interval.TRIPLE_AUGMENTED);
		assertTrue(areSimilar(As, fifthAug3.calculateSecondNote(C)));
		assertTrue(areSimilar(gss, fifthAug3.calculateSecondNote(B)));
		assertTrue(areSimilar(Bss, fifthAug3.calculateSecondNote(Ds)));
		assertTrue(areSimilar(Bss, fifthAug3.calculateSecondNote(Eb)));
		Interval revFifthAug3 = Interval.reverseOrder(fifthAug3);
		assertTrue(areSimilar(Ebb, revFifthAug3.calculateSecondNote(c)));
		assertTrue(areSimilar(Db, revFifthAug3.calculateSecondNote(B)));
		assertTrue(areSimilar(Gbb, revFifthAug3.calculateSecondNote(ds)));
		assertTrue(areSimilar(Gbb, revFifthAug3.calculateSecondNote(eb)));
	
		//quadruple augmented
		Interval fifthAug4 = new Interval(Interval.FIFTH, Interval.QUADRUPLE_AUGMENTED);
		assertTrue(areSimilar(Ass, fifthAug4.calculateSecondNote(C)));
		assertTrue(areSimilar(as, fifthAug4.calculateSecondNote(B)));
		assertTrue(areSimilar(css, fifthAug4.calculateSecondNote(Ds)));
		assertTrue(areSimilar(css, fifthAug4.calculateSecondNote(Eb)));
		Interval revFifthAug4 = Interval.reverseOrder(fifthAug4);
		assertTrue(areSimilar(Db, revFifthAug4.calculateSecondNote(c)));
		assertTrue(areSimilar(Dbb, revFifthAug4.calculateSecondNote(B)));
		assertTrue(areSimilar(Fb, revFifthAug4.calculateSecondNote(ds)));
		assertTrue(areSimilar(Fb, revFifthAug4.calculateSecondNote(eb)));
		
		//with key signature
		G = fifth.calculateSecondNote(C,
				new KeySignature(Note.C, KeySignature.MINOR));
		assertEquals(Note.G, G.getStrictHeight());
		assertEquals(0, G.getOctaveTransposition());
		assertTrue(G.getAccidental().isInTheKey());
		Gs = fifth.calculateSecondNote(new Note(Note.C, Accidental.NONE),
				new KeySignature(Note.A, KeySignature.MAJOR));
		assertEquals(Note.G, Gs.getStrictHeight());
		assertEquals(0, Gs.getOctaveTransposition());
		assertTrue(Gs.getAccidental().isInTheKey());
		
	}
	
	public void test7GetSemiQuarterAndCents() {
		
		//
		//unison
		//
		test7helper(
				new Interval(Interval.UNISON, Interval.QUADRUPLE_DIMINISHED),
				-4, -8, -400);
		test7helper(new Interval(Interval.UNISON,
				Interval.SUPER_TRIPLE_DIMINISHED), -4, -7, -350);
		test7helper(new Interval(Interval.UNISON, Interval.TRIPLE_DIMINISHED),
				-3, -6, -300);
		test7helper(new Interval(Interval.UNISON,
				Interval.SUPER_DOUBLE_DIMINISHED), -3, -5, -250);
		test7helper(new Interval(Interval.UNISON, Interval.DOUBLE_DIMINISHED),
				-2, -4, -200);
		test7helper(new Interval(Interval.UNISON, Interval.SUPER_DIMINISHED),
				-2, -3, -150);
		try {
			new Interval(Interval.UNISON, Interval.SUBMINOR);
			fail("Subminor not possible for unison");
		} catch (Exception ok) {
		}
		test7helper(Interval.DIMINISHED_UNISON, -1, -2, -100);
		test7helper(new Interval(Interval.UNISON, Interval.SUB), -1, -1, -50);
		test7helper(Interval.PERFECT_UNISON, 0, 0, 0);
		test7helper(new Interval(Interval.UNISON, Interval.SUPER), 1, 1, 50);
		test7helper(Interval.AUGMENTED_UNISON, 1, 2, 100);
		test7helper(new Interval(Interval.UNISON, Interval.SUPER_AUGMENTED), 2,
				3, 150);
		test7helper(new Interval(Interval.UNISON, Interval.DOUBLE_AUGMENTED),
				2, 4, 200);
		test7helper(new Interval(Interval.UNISON,
				Interval.SUPER_DOUBLE_AUGMENTED), 3, 5, 250);
		test7helper(new Interval(Interval.UNISON, Interval.TRIPLE_AUGMENTED),
				3, 6, 300);
		test7helper(new Interval(Interval.UNISON,
				Interval.SUPER_TRIPLE_AUGMENTED), 4, 7, 350);
		test7helper(
				new Interval(Interval.UNISON, Interval.QUADRUPLE_AUGMENTED), 4,
				8, 400);

		//
		//second
		//
		try {
			new Interval(Interval.SECOND, Interval.QUADRUPLE_DIMINISHED);
		} catch (Exception ok) {
		}
		try {
			new Interval(Interval.SECOND, Interval.SUPER_TRIPLE_DIMINISHED);
		} catch (Exception ok) {
		}
		test7helper(new Interval(Interval.SECOND, Interval.TRIPLE_DIMINISHED),
				-2, -4, -200);
		test7helper(new Interval(Interval.SECOND,
				Interval.SUPER_DOUBLE_DIMINISHED), -2, -3, -150);
		test7helper(new Interval(Interval.SECOND, Interval.DOUBLE_DIMINISHED),
				-1, -2, -100);
		test7helper(new Interval(Interval.SECOND, Interval.SUPER_DIMINISHED),
				-1, -1, -50);
		test7helper(Interval.DIMINISHED_SECOND, 0, 0, 0);
		test7helper(new Interval(Interval.SECOND, Interval.SUBMINOR), 0, 1, 50);
		test7helper(Interval.MINOR_SECOND, 1, 2, 100);
		test7helper(new Interval(Interval.SECOND, Interval.NEUTRAL), 1, 3, 150);
		try {
			new Interval(Interval.SECOND, Interval.PERFECT);
		} catch (Exception ok) {
		}
		test7helper(Interval.MAJOR_SECOND, 2, 4, 200);
		test7helper(new Interval(Interval.SECOND, Interval.SUPER_MAJOR), 3, 5,
				250);
		test7helper(Interval.AUGMENTED_SECOND, 3, 6, 300);
		test7helper(new Interval(Interval.SECOND, Interval.SUPER_AUGMENTED), 4,
				7, 350);
		test7helper(new Interval(Interval.SECOND, Interval.DOUBLE_AUGMENTED),
				4, 8, 400);
		test7helper(new Interval(Interval.SECOND,
				Interval.SUPER_DOUBLE_AUGMENTED), 5, 9, 450);
		test7helper(new Interval(Interval.SECOND, Interval.TRIPLE_AUGMENTED),
				5, 10, 500);
		test7helper(new Interval(Interval.SECOND,
				Interval.SUPER_TRIPLE_AUGMENTED), 6, 11, 550);
		test7helper(
				new Interval(Interval.SECOND, Interval.QUADRUPLE_AUGMENTED), 6,
				12, 600);

		//
		//third
		//
		try {
			new Interval(Interval.THIRD, Interval.QUADRUPLE_DIMINISHED);
		} catch (Exception ok) {
		}
		try {
			new Interval(Interval.THIRD, Interval.SUPER_TRIPLE_DIMINISHED);
		} catch (Exception ok) {
		}
		test7helper(new Interval(Interval.THIRD, Interval.TRIPLE_DIMINISHED),
				0, 0, 0);
		test7helper(new Interval(Interval.THIRD,
				Interval.SUPER_DOUBLE_DIMINISHED), 0, 1, 50);
		test7helper(new Interval(Interval.THIRD, Interval.DOUBLE_DIMINISHED),
				1, 2, 100);
		test7helper(new Interval(Interval.THIRD, Interval.SUPER_DIMINISHED), 1,
				3, 150);
		test7helper(Interval.DIMINISHED_THIRD, 2, 4, 200);
		test7helper(new Interval(Interval.THIRD, Interval.SUBMINOR), 2, 5, 250);
		test7helper(Interval.MINOR_THIRD, 3, 6, 300);
		test7helper(new Interval(Interval.THIRD, Interval.NEUTRAL), 3, 7, 350);
		try {
			new Interval(Interval.THIRD, Interval.PERFECT);
		} catch (Exception ok) {
		}
		test7helper(Interval.MAJOR_THIRD, 4, 8, 400);
		test7helper(new Interval(Interval.THIRD, Interval.SUPER_MAJOR), 5, 9,
				450);
		test7helper(Interval.AUGMENTED_THIRD, 5, 10, 500);
		test7helper(new Interval(Interval.THIRD, Interval.SUPER_AUGMENTED), 6,
				11, 550);
		test7helper(new Interval(Interval.THIRD, Interval.DOUBLE_AUGMENTED), 6,
				12, 600);
		test7helper(new Interval(Interval.THIRD,
				Interval.SUPER_DOUBLE_AUGMENTED), 7, 13, 650);
		test7helper(new Interval(Interval.THIRD, Interval.TRIPLE_AUGMENTED), 7,
				14, 700);
		test7helper(new Interval(Interval.THIRD,
				Interval.SUPER_TRIPLE_AUGMENTED), 8, 15, 750);
		test7helper(new Interval(Interval.THIRD, Interval.QUADRUPLE_AUGMENTED),
				8, 16, 800);

		//
		//fourth
		//
		test7helper(
				new Interval(Interval.FOURTH, Interval.QUADRUPLE_DIMINISHED),
				1, 2, 100);
		test7helper(new Interval(Interval.FOURTH,
				Interval.SUPER_TRIPLE_DIMINISHED), 1, 3, 150);
		test7helper(new Interval(Interval.FOURTH, Interval.TRIPLE_DIMINISHED),
				2, 4, 200);
		test7helper(new Interval(Interval.FOURTH,
				Interval.SUPER_DOUBLE_DIMINISHED), 2, 5, 250);
		test7helper(new Interval(Interval.FOURTH, Interval.DOUBLE_DIMINISHED),
				3, 6, 300);
		test7helper(new Interval(Interval.FOURTH, Interval.SUPER_DIMINISHED),
				3, 7, 350);
		try {
			new Interval(Interval.FOURTH, Interval.SUBMINOR);
			fail("Subminor not possible for FOURTH");
		} catch (Exception ok) {
		}
		test7helper(Interval.DIMINISHED_FOURTH, 4, 8, 400);
		test7helper(new Interval(Interval.FOURTH, Interval.SUB), 4, 9, 450);
		test7helper(Interval.PERFECT_FOURTH, 5, 10, 500);
		test7helper(new Interval(Interval.FOURTH, Interval.SUPER), 6, 11, 550);
		test7helper(Interval.AUGMENTED_FOURTH, 6, 12, 600);
		test7helper(new Interval(Interval.FOURTH, Interval.SUPER_AUGMENTED), 7,
				13, 650);
		test7helper(new Interval(Interval.FOURTH, Interval.DOUBLE_AUGMENTED),
				7, 14, 700);
		test7helper(new Interval(Interval.FOURTH,
				Interval.SUPER_DOUBLE_AUGMENTED), 8, 15, 750);
		test7helper(new Interval(Interval.FOURTH, Interval.TRIPLE_AUGMENTED),
				8, 16, 800);
		test7helper(new Interval(Interval.FOURTH,
				Interval.SUPER_TRIPLE_AUGMENTED), 9, 17, 850);
		test7helper(
				new Interval(Interval.FOURTH, Interval.QUADRUPLE_AUGMENTED), 9,
				18, 900);

		//
		//fifth
		//
		test7helper(
				new Interval(Interval.FIFTH, Interval.QUADRUPLE_DIMINISHED), 3,
				6, 300);
		test7helper(new Interval(Interval.FIFTH,
				Interval.SUPER_TRIPLE_DIMINISHED), 3, 7, 350);
		test7helper(new Interval(Interval.FIFTH, Interval.TRIPLE_DIMINISHED),
				4, 8, 400);
		test7helper(new Interval(Interval.FIFTH,
				Interval.SUPER_DOUBLE_DIMINISHED), 4, 9, 450);
		test7helper(new Interval(Interval.FIFTH, Interval.DOUBLE_DIMINISHED),
				5, 10, 500);
		test7helper(new Interval(Interval.FIFTH, Interval.SUPER_DIMINISHED), 5,
				11, 550);
		try {
			new Interval(Interval.FIFTH, Interval.SUBMINOR);
			fail("Subminor not possible for FOURTH");
		} catch (Exception ok) {
		}
		test7helper(Interval.DIMINISHED_FIFTH, 6, 12, 600);
		test7helper(new Interval(Interval.FIFTH, Interval.SUB), 6, 13, 650);
		test7helper(Interval.PERFECT_FIFTH, 7, 14, 700);
		test7helper(new Interval(Interval.FIFTH, Interval.SUPER), 8, 15, 750);
		test7helper(Interval.AUGMENTED_FIFTH, 8, 16, 800);
		test7helper(new Interval(Interval.FIFTH, Interval.SUPER_AUGMENTED), 9,
				17, 850);
		test7helper(new Interval(Interval.FIFTH, Interval.DOUBLE_AUGMENTED), 9,
				18, 900);
		test7helper(new Interval(Interval.FIFTH,
				Interval.SUPER_DOUBLE_AUGMENTED), 10, 19, 950);
		test7helper(new Interval(Interval.FIFTH, Interval.TRIPLE_AUGMENTED),
				10, 20, 1000);
		test7helper(new Interval(Interval.FIFTH,
				Interval.SUPER_TRIPLE_AUGMENTED), 11, 21, 1050);
		test7helper(new Interval(Interval.FIFTH, Interval.QUADRUPLE_AUGMENTED),
				11, 22, 1100);

		//
		//sixth
		//
		try {
			new Interval(Interval.SIXTH, Interval.QUADRUPLE_DIMINISHED);
		} catch (Exception ok) {
		}
		try {
			new Interval(Interval.SIXTH, Interval.SUPER_TRIPLE_DIMINISHED);
		} catch (Exception ok) {
		}
		test7helper(new Interval(Interval.SIXTH, Interval.TRIPLE_DIMINISHED),
				5, 10, 500);
		test7helper(new Interval(Interval.SIXTH,
				Interval.SUPER_DOUBLE_DIMINISHED), 5, 11, 550);
		test7helper(new Interval(Interval.SIXTH, Interval.DOUBLE_DIMINISHED),
				6, 12, 600);
		test7helper(new Interval(Interval.SIXTH, Interval.SUPER_DIMINISHED), 6,
				13, 650);
		test7helper(Interval.DIMINISHED_SIXTH, 7, 14, 700);
		test7helper(new Interval(Interval.SIXTH, Interval.SUBMINOR), 7, 15, 750);
		test7helper(Interval.MINOR_SIXTH, 8, 16, 800);
		test7helper(new Interval(Interval.SIXTH, Interval.NEUTRAL), 8, 17, 850);
		try {
			new Interval(Interval.SIXTH, Interval.PERFECT);
		} catch (Exception ok) {
		}
		test7helper(Interval.MAJOR_SIXTH, 9, 18, 900);
		test7helper(new Interval(Interval.SIXTH, Interval.SUPER_MAJOR), 10, 19,
				950);
		test7helper(Interval.AUGMENTED_SIXTH, 10, 20, 1000);
		test7helper(new Interval(Interval.SIXTH, Interval.SUPER_AUGMENTED), 11,
				21, 1050);
		test7helper(new Interval(Interval.SIXTH, Interval.DOUBLE_AUGMENTED),
				11, 22, 1100);
		test7helper(new Interval(Interval.SIXTH,
				Interval.SUPER_DOUBLE_AUGMENTED), 12, 23, 1150);
		test7helper(new Interval(Interval.SIXTH, Interval.TRIPLE_AUGMENTED),
				12, 24, 1200);
		test7helper(new Interval(Interval.SIXTH,
				Interval.SUPER_TRIPLE_AUGMENTED), 13, 25, 1250);
		test7helper(new Interval(Interval.SIXTH, Interval.QUADRUPLE_AUGMENTED),
				13, 26, 1300);

		//
		//seventh
		//
		try {
			new Interval(Interval.SEVENTH, Interval.QUADRUPLE_DIMINISHED);
		} catch (Exception ok) {
		}
		try {
			new Interval(Interval.SEVENTH, Interval.SUPER_TRIPLE_DIMINISHED);
		} catch (Exception ok) {
		}
		test7helper(new Interval(Interval.SEVENTH, Interval.TRIPLE_DIMINISHED),
				7, 14, 700);
		test7helper(new Interval(Interval.SEVENTH,
				Interval.SUPER_DOUBLE_DIMINISHED), 7, 15, 750);
		test7helper(new Interval(Interval.SEVENTH, Interval.DOUBLE_DIMINISHED),
				8, 16, 800);
		test7helper(new Interval(Interval.SEVENTH, Interval.SUPER_DIMINISHED),
				8, 17, 850);
		test7helper(Interval.DIMINISHED_SEVENTH, 9, 18, 900);
		test7helper(new Interval(Interval.SEVENTH, Interval.SUBMINOR), 9, 19,
				950);
		test7helper(Interval.MINOR_SEVENTH, 10, 20, 1000);
		test7helper(new Interval(Interval.SEVENTH, Interval.NEUTRAL), 10, 21,
				1050);
		try {
			new Interval(Interval.SEVENTH, Interval.PERFECT);
		} catch (Exception ok) {
		}
		test7helper(Interval.MAJOR_SEVENTH, 11, 22, 1100);
		test7helper(new Interval(Interval.SEVENTH, Interval.SUPER_MAJOR), 12,
				23, 1150);
		test7helper(Interval.AUGMENTED_SEVENTH, 12, 24, 1200);
		test7helper(new Interval(Interval.SEVENTH, Interval.SUPER_AUGMENTED),
				13, 25, 1250);
		test7helper(new Interval(Interval.SEVENTH, Interval.DOUBLE_AUGMENTED),
				13, 26, 1300);
		test7helper(new Interval(Interval.SEVENTH,
				Interval.SUPER_DOUBLE_AUGMENTED), 14, 27, 1350);
		test7helper(new Interval(Interval.SEVENTH, Interval.TRIPLE_AUGMENTED),
				14, 28, 1400);
		test7helper(new Interval(Interval.SEVENTH,
				Interval.SUPER_TRIPLE_AUGMENTED), 15, 29, 1450);
		test7helper(
				new Interval(Interval.SEVENTH, Interval.QUADRUPLE_AUGMENTED),
				15, 30, 1500);

		//
		//octave
		//
		test7helper(
				new Interval(Interval.OCTAVE, Interval.QUADRUPLE_DIMINISHED),
				8, 16, 800);
		test7helper(new Interval(Interval.OCTAVE,
				Interval.SUPER_TRIPLE_DIMINISHED), 8, 17, 850);
		test7helper(new Interval(Interval.OCTAVE, Interval.TRIPLE_DIMINISHED),
				9, 18, 900);
		test7helper(new Interval(Interval.OCTAVE,
				Interval.SUPER_DOUBLE_DIMINISHED), 9, 19, 950);
		test7helper(new Interval(Interval.OCTAVE, Interval.DOUBLE_DIMINISHED),
				10, 20, 1000);
		test7helper(new Interval(Interval.OCTAVE, Interval.SUPER_DIMINISHED),
				10, 21, 1050);
		try {
			new Interval(Interval.OCTAVE, Interval.SUBMINOR);
			fail("Subminor not possible for OCTAVE");
		} catch (Exception ok) {
		}
		test7helper(Interval.DIMINISHED_OCTAVE, 11, 22, 1100);
		test7helper(new Interval(Interval.OCTAVE, Interval.SUB), 11, 23, 1150);
		test7helper(Interval.PERFECT_OCTAVE, 12, 24, 1200);
		test7helper(new Interval(Interval.OCTAVE, Interval.SUPER), 13, 25, 1250);
		test7helper(Interval.AUGMENTED_OCTAVE, 13, 26, 1300);
		test7helper(new Interval(Interval.OCTAVE, Interval.SUPER_AUGMENTED),
				14, 27, 1350);
		test7helper(new Interval(Interval.OCTAVE, Interval.DOUBLE_AUGMENTED),
				14, 28, 1400);
		test7helper(new Interval(Interval.OCTAVE,
				Interval.SUPER_DOUBLE_AUGMENTED), 15, 29, 1450);
		test7helper(new Interval(Interval.OCTAVE, Interval.TRIPLE_AUGMENTED),
				15, 30, 1500);
		test7helper(new Interval(Interval.OCTAVE,
				Interval.SUPER_TRIPLE_AUGMENTED), 16, 31, 1550);
		test7helper(
				new Interval(Interval.OCTAVE, Interval.QUADRUPLE_AUGMENTED),
				16, 32, 1600);

	}
	
	private void test7helper(Interval i, int semi, int quarter, int cents) {
		assertEquals(semi+" semitones", semi, i.getSemitones());
		assertEquals(quarter+" quarter", quarter, i.getQuartertones());
		assertEquals(cents+" cents", cents, i.getCents());
	}
	
	public void test8QuarterTone() {
		//
		//Create note and check their properties
		//
		Note C2b = new Note(Note.C, Accidental.DOUBLE_FLAT);
		assertEquals(-2, C2b.getMidiLikeHeight());
		assertEquals(-2f, C2b.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, C2b.getAccidental().getMicrotonalOffset());
		Note C1_5b = new Note(Note.C, Accidental.FLAT_AND_A_HALF);
		assertEquals(-1, C1_5b.getMidiLikeHeight());
		assertEquals(-1.5f, C1_5b.getMidiLikeMicrotonalHeight());
		assertEquals(-0.5f, C1_5b.getAccidental().getMicrotonalOffset());
		Note Cb = new Note(Note.C, Accidental.FLAT);
		assertEquals(-1, Cb.getMidiLikeHeight());
		assertEquals(-1f, Cb.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, Cb.getAccidental().getMicrotonalOffset());
		Note C0_5b = new Note(Note.C, Accidental.HALF_FLAT);
		assertEquals(0, C0_5b.getMidiLikeHeight());
		assertEquals(-0.5f, C0_5b.getMidiLikeMicrotonalHeight());
		assertEquals(-0.5f, C0_5b.getAccidental().getMicrotonalOffset());
		Note C = new Note(Note.C, Accidental.NATURAL);
		assertEquals(0, C.getMidiLikeHeight());
		assertEquals(0.0f, C.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, C.getAccidental().getMicrotonalOffset());
		Note C0_5s = new Note(Note.C, Accidental.HALF_SHARP);
		assertEquals(0, C0_5s.getMidiLikeHeight());
		assertEquals(0.5f, C0_5s.getMidiLikeMicrotonalHeight());
		assertEquals(0.5f, C0_5s.getAccidental().getMicrotonalOffset());
		Note Cs = new Note(Note.C, Accidental.SHARP);
		assertEquals(1, Cs.getMidiLikeHeight());
		assertEquals(1.0f, Cs.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, Cs.getAccidental().getMicrotonalOffset());
		Note C1_5s = new Note(Note.C, Accidental.SHARP_AND_A_HALF);
		assertEquals(1, C1_5s.getMidiLikeHeight());
		assertEquals(1.5f, C1_5s.getMidiLikeMicrotonalHeight());
		assertEquals(0.5f, C1_5s.getAccidental().getMicrotonalOffset());
		Note C2s = new Note(Note.C, Accidental.DOUBLE_SHARP);
		assertEquals(2, C2s.getMidiLikeHeight());
		assertEquals(2f, C2s.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, C2s.getAccidental().getMicrotonalOffset());
		
		Note D = new Note(Note.D, Accidental.NATURAL);
		assertEquals(2, D.getMidiLikeHeight());
		assertEquals(2.0f, D.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, D.getAccidental().getMicrotonalOffset());
		Note E2b = new Note(Note.E, Accidental.DOUBLE_FLAT);
		assertEquals(2, E2b.getMidiLikeHeight());
		assertEquals(2.0f, E2b.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, E2b.getAccidental().getMicrotonalOffset());
		Note E1_5b = new Note(Note.E, Accidental.FLAT_AND_A_HALF);
		assertEquals(3, E1_5b.getMidiLikeHeight());
		assertEquals(2.5f, E1_5b.getMidiLikeMicrotonalHeight());
		assertEquals(-0.5f, E1_5b.getAccidental().getMicrotonalOffset());
		Note Eb = new Note(Note.E, Accidental.FLAT);
		assertEquals(3, Eb.getMidiLikeHeight());
		assertEquals(3.0f, Eb.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, Eb.getAccidental().getMicrotonalOffset());
		Note E0_5b = new Note(Note.E, Accidental.HALF_FLAT);
		assertEquals(4, E0_5b.getMidiLikeHeight());
		assertEquals(3.5f, E0_5b.getMidiLikeMicrotonalHeight());
		assertEquals(-0.5f, E0_5b.getAccidental().getMicrotonalOffset());
		Note E = new Note(Note.E, Accidental.NATURAL);
		assertEquals(4, E.getMidiLikeHeight());
		assertEquals(4.0f, E.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, E.getAccidental().getMicrotonalOffset());
		Note E0_5s = new Note(Note.E, Accidental.HALF_SHARP);
		assertEquals(4, E0_5s.getMidiLikeHeight());
		assertEquals(4.5f, E0_5s.getMidiLikeMicrotonalHeight());
		assertEquals(0.5f, E0_5s.getAccidental().getMicrotonalOffset());
		Note Es = new Note(Note.E, Accidental.SHARP);
		assertEquals(5, Es.getMidiLikeHeight());
		assertEquals(5.0f, Es.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, Es.getAccidental().getMicrotonalOffset());
		Note E1_5s = new Note(Note.E, Accidental.SHARP_AND_A_HALF);
		assertEquals(5, E1_5s.getMidiLikeHeight());
		assertEquals(5.5f, E1_5s.getMidiLikeMicrotonalHeight());
		assertEquals(0.5f, E1_5s.getAccidental().getMicrotonalOffset());
		Note E2s = new Note(Note.E, Accidental.DOUBLE_SHARP);
		assertEquals(6, E2s.getMidiLikeHeight());
		assertEquals(6.0f, E2s.getMidiLikeMicrotonalHeight());
		assertEquals(0.0f, E2s.getAccidental().getMicrotonalOffset());

		//
		//Create intervals and check their properties
		//
		//Ebb = D
		Interval intervC_E2b = new Interval(C, E2b, null);
		assertEquals(2, intervC_E2b.getSemitones());
		assertEquals(4, intervC_E2b.getQuartertones());
		assertEquals(Interval.THIRD, intervC_E2b.getLabel());
		assertEquals(0.0f, intervC_E2b.getMicrotonalOffset());
		//E flat and half stay in flat, not D half flat
		Interval intervC_E1_5b = new Interval(C, E1_5b, null);
		assertEquals(2, intervC_E1_5b.getSemitones());
		assertEquals(5, intervC_E1_5b.getQuartertones());
		assertEquals(Interval.THIRD, intervC_E1_5b.getLabel());
		assertEquals(-0.5f, intervC_E1_5b.getMicrotonalOffset());
		//E flat
		Interval intervC_Eb = new Interval(C, Eb, null);
		assertEquals(3, intervC_Eb.getSemitones());
		assertEquals(6, intervC_Eb.getQuartertones());
		assertEquals(Interval.THIRD, intervC_Eb.getLabel());
		assertEquals(0.0f, intervC_Eb.getMicrotonalOffset());
		//E half flat
		Interval intervC_E0_5b = new Interval(C, E0_5b, null);
		assertEquals(3, intervC_E0_5b.getSemitones());
		assertEquals(7, intervC_E0_5b.getQuartertones());
		assertEquals(Interval.THIRD, intervC_E0_5b.getLabel());
		assertEquals(-0.5f, intervC_E0_5b.getMicrotonalOffset());
		
		Interval intervC_E = new Interval(C, E, null);
		assertEquals(4, intervC_E.getSemitones());
		assertEquals(8, intervC_E.getQuartertones());
		assertEquals(Interval.THIRD, intervC_E.getLabel());
		assertEquals(0.0f, intervC_E.getMicrotonalOffset());
		
		//E half sharp stay a sharp, not F half flat
		Interval intervC_E0_5s = new Interval(C, E0_5s, null);
		assertEquals(5, intervC_E0_5s.getSemitones());
		assertEquals(9, intervC_E0_5s.getQuartertones());
		assertEquals(Interval.THIRD, intervC_E0_5s.getLabel());
		assertEquals(0.5f, intervC_E0_5s.getMicrotonalOffset());
		//E sharp = F
		Interval intervC_Es = new Interval(C, Es, null);
		assertEquals(5, intervC_Es.getSemitones());
		assertEquals(10, intervC_Es.getQuartertones());
		assertEquals(Interval.THIRD, intervC_Es.getLabel());
		assertEquals(0.0f, intervC_Es.getMicrotonalOffset());
		//E sharp and half = F half sharp, not F#
		Interval intervC_E1_5s = new Interval(C, E1_5s, null);
		assertEquals(6, intervC_E1_5s.getSemitones());
		assertEquals(11, intervC_E1_5s.getQuartertones());
		assertEquals(Interval.THIRD, intervC_E1_5s.getLabel());
		assertEquals(0.5f, intervC_E1_5s.getMicrotonalOffset());
		//E## = F#
		Interval intervC_E2s = new Interval(C, E2s, null);
		assertEquals(6, intervC_E2s.getSemitones());
		assertEquals(12, intervC_E2s.getQuartertones());
		assertEquals(Interval.THIRD, intervC_E2s.getLabel());
		assertEquals(0.0f, intervC_E2s.getMicrotonalOffset());
		
		//
		//Create downward intervals and check their properties
		//
		//Ebb = D
		Interval intervE2b_C = new Interval(E2b, C, null);
		assertEquals(2, intervE2b_C.getSemitones());
		assertEquals(4, intervE2b_C.getQuartertones());
		assertEquals(Interval.THIRD, intervE2b_C.getLabel());
		assertEquals(0.0f, intervE2b_C.getMicrotonalOffset());
		//E flat and half stay in flat, not D half flat
		Interval intervE1_5b_C = new Interval(E1_5b, C, null);
		assertEquals(2, intervE1_5b_C.getSemitones());
		assertEquals(5, intervE1_5b_C.getQuartertones());
		assertEquals(Interval.THIRD, intervE1_5b_C.getLabel());
		assertEquals(-0.5f, intervE1_5b_C.getMicrotonalOffset());
		//E flat
		Interval intervEb_C = new Interval(Eb, C, null);
		assertEquals(3, intervEb_C.getSemitones());
		assertEquals(6, intervEb_C.getQuartertones());
		assertEquals(Interval.THIRD, intervEb_C.getLabel());
		assertEquals(0.0f, intervEb_C.getMicrotonalOffset());
		//E half flat
		Interval intervE0_5b_C = new Interval(E0_5b, C, null);
		assertEquals(3, intervE0_5b_C.getSemitones());
		assertEquals(7, intervE0_5b_C.getQuartertones());
		assertEquals(Interval.THIRD, intervE0_5b_C.getLabel());
		assertEquals(-0.5f, intervE0_5b_C.getMicrotonalOffset());
		
		Interval intervE_C = new Interval(E, C, null);
		assertEquals(4, intervE_C.getSemitones());
		assertEquals(8, intervE_C.getQuartertones());
		assertEquals(Interval.THIRD, intervE_C.getLabel());
		assertEquals(0.0f, intervE_C.getMicrotonalOffset());
		
		//E half sharp stay a sharp, not F half flat
		Interval intervE0_5s_C = new Interval(E0_5s, C, null);
		assertEquals(5, intervE0_5s_C.getSemitones());
		assertEquals(9, intervE0_5s_C.getQuartertones());
		assertEquals(Interval.THIRD, intervE0_5s_C.getLabel());
		assertEquals(0.5f, intervE0_5s_C.getMicrotonalOffset());
		//E sharp = F
		Interval intervEs_C = new Interval(Es, C, null);
		assertEquals(5, intervEs_C.getSemitones());
		assertEquals(10, intervEs_C.getQuartertones());
		assertEquals(Interval.THIRD, intervEs_C.getLabel());
		assertEquals(0.0f, intervEs_C.getMicrotonalOffset());
		//E sharp and half = F half sharp, not F#
		Interval intervE1_5s_C = new Interval(E1_5s, C, null);
		assertEquals(6, intervE1_5s_C.getSemitones());
		assertEquals(11, intervE1_5s_C.getQuartertones());
		assertEquals(Interval.THIRD, intervE1_5s_C.getLabel());
		assertEquals(0.5f, intervE1_5s_C.getMicrotonalOffset());
		//E## = F#
		Interval intervE2s_C = new Interval(E2s, C, null);
		assertEquals(6, intervE2s_C.getSemitones());
		assertEquals(12, intervE2s_C.getQuartertones());
		assertEquals(Interval.THIRD, intervE2s_C.getLabel());
		assertEquals(0.0f, intervE2s_C.getMicrotonalOffset());
		
		
		//
		//Test interval transposition
		//
		//diminished
		assertEquals("Dbb", intervC_E2b.calculateSecondNote(C2b));
		assertEquals("D1.5b", intervC_E2b.calculateSecondNote(C1_5b));
		assertEquals("Db", intervC_E2b.calculateSecondNote(Cb));
		assertEquals("D0.5b", intervC_E2b.calculateSecondNote(C0_5b));
		assertEquals("Ebb", intervC_E2b.calculateSecondNote(C));
		assertEquals("E1.5b", intervC_E2b.calculateSecondNote(C0_5s));
		assertEquals("Eb", intervC_E2b.calculateSecondNote(Cs));
		assertEquals("E0.5b", intervC_E2b.calculateSecondNote(C1_5s));
		assertEquals("E=", intervC_E2b.calculateSecondNote(C2s));
		
		//subminor
		assertEquals("D1.5b", intervC_E1_5b.calculateSecondNote(C2b));
		assertEquals("Db", intervC_E1_5b.calculateSecondNote(C1_5b));
		assertEquals("D0.5b", intervC_E1_5b.calculateSecondNote(Cb));
		assertEquals("Ebb", intervC_E1_5b.calculateSecondNote(C0_5b));
		assertEquals("E1.5b", intervC_E1_5b.calculateSecondNote(C));
		assertEquals("Eb", intervC_E1_5b.calculateSecondNote(C0_5s));
		assertEquals("E0.5b", intervC_E1_5b.calculateSecondNote(Cs));
		assertEquals("E=", intervC_E1_5b.calculateSecondNote(C1_5s));
		assertEquals("E0.5#", intervC_E1_5b.calculateSecondNote(C2s));

		//minor
		assertEquals("Db", intervC_Eb.calculateSecondNote(C2b));
		assertEquals("D0.5b", intervC_Eb.calculateSecondNote(C1_5b));
		assertEquals("Ebb", intervC_Eb.calculateSecondNote(Cb));
		assertEquals("E1.5b", intervC_Eb.calculateSecondNote(C0_5b));
		assertEquals("Eb", intervC_Eb.calculateSecondNote(C));
		assertEquals("E0.5b", intervC_Eb.calculateSecondNote(C0_5s));
		assertEquals("E=", intervC_Eb.calculateSecondNote(Cs));
		assertEquals("E0.5#", intervC_Eb.calculateSecondNote(C1_5s));
		assertEquals("E#", intervC_Eb.calculateSecondNote(C2s));
		
		//neutral
		assertEquals("D0.5b", intervC_E0_5b.calculateSecondNote(C2b));
		assertEquals("Ebb", intervC_E0_5b.calculateSecondNote(C1_5b));
		assertEquals("E1.5b", intervC_E0_5b.calculateSecondNote(Cb));
		assertEquals("Eb", intervC_E0_5b.calculateSecondNote(C0_5b));
		assertEquals("E0.5b", intervC_E0_5b.calculateSecondNote(C));
		assertEquals("E=", intervC_E0_5b.calculateSecondNote(C0_5s));
		assertEquals("E0.5#", intervC_E0_5b.calculateSecondNote(Cs));
		assertEquals("E#", intervC_E0_5b.calculateSecondNote(C1_5s));
		assertEquals("E1.5#", intervC_E0_5b.calculateSecondNote(C2s));

		//major
		assertEquals("Ebb", intervC_E.calculateSecondNote(C2b));
		assertEquals("E1.5b", intervC_E.calculateSecondNote(C1_5b));
		assertEquals("Eb", intervC_E.calculateSecondNote(Cb));
		assertEquals("E0.5b", intervC_E.calculateSecondNote(C0_5b));
		assertEquals("E=", intervC_E.calculateSecondNote(C));
		assertEquals("E0.5#", intervC_E.calculateSecondNote(C0_5s));
		assertEquals("E#", intervC_E.calculateSecondNote(Cs));
		assertEquals("E1.5#", intervC_E.calculateSecondNote(C1_5s));
		assertEquals("E##", intervC_E.calculateSecondNote(C2s));

		//super major
		assertEquals("E1.5b", intervC_E0_5s.calculateSecondNote(C2b));
		assertEquals("Eb", intervC_E0_5s.calculateSecondNote(C1_5b));
		assertEquals("E0.5b", intervC_E0_5s.calculateSecondNote(Cb));
		assertEquals("E=", intervC_E0_5s.calculateSecondNote(C0_5b));
		assertEquals("E0.5#", intervC_E0_5s.calculateSecondNote(C));
		assertEquals("E#", intervC_E0_5s.calculateSecondNote(C0_5s));
		assertEquals("E1.5#", intervC_E0_5s.calculateSecondNote(Cs));
		assertEquals("E##", intervC_E0_5s.calculateSecondNote(C1_5s));
		assertEquals("F1.5#", intervC_E0_5s.calculateSecondNote(C2s));

		//augmented
		assertEquals("Eb", intervC_Es.calculateSecondNote(C2b));
		assertEquals("E0.5b", intervC_Es.calculateSecondNote(C1_5b));
		assertEquals("E=", intervC_Es.calculateSecondNote(Cb));
		assertEquals("E0.5#", intervC_Es.calculateSecondNote(C0_5b));
		assertEquals("E#", intervC_Es.calculateSecondNote(C));
		assertEquals("E1.5#", intervC_Es.calculateSecondNote(C0_5s));
		assertEquals("E##", intervC_Es.calculateSecondNote(Cs));
		assertEquals("F1.5#", intervC_Es.calculateSecondNote(C1_5s));
		assertEquals("F##", intervC_Es.calculateSecondNote(C2s));

		//super augmented
		assertEquals("E0.5b", intervC_E1_5s.calculateSecondNote(C2b));
		assertEquals("E=", intervC_E1_5s.calculateSecondNote(C1_5b));
		assertEquals("E0.5#", intervC_E1_5s.calculateSecondNote(Cb));
		assertEquals("E#", intervC_E1_5s.calculateSecondNote(C0_5b));
		assertEquals("E1.5#", intervC_E1_5s.calculateSecondNote(C));
		assertEquals("E##", intervC_E1_5s.calculateSecondNote(C0_5s));
		assertEquals("F1.5#", intervC_E1_5s.calculateSecondNote(Cs));
		assertEquals("F##", intervC_E1_5s.calculateSecondNote(C1_5s));
		assertEquals("G0.5#", intervC_E1_5s.calculateSecondNote(C2s));

		//double augmented
		assertEquals("E=", intervC_E2s.calculateSecondNote(C2b));
		assertEquals("E0.5#", intervC_E2s.calculateSecondNote(C1_5b));
		assertEquals("E#", intervC_E2s.calculateSecondNote(Cb));
		assertEquals("E1.5#", intervC_E2s.calculateSecondNote(C0_5b));
		assertEquals("E##", intervC_E2s.calculateSecondNote(C));
		assertEquals("F1.5#", intervC_E2s.calculateSecondNote(C0_5s));
		assertEquals("F##", intervC_E2s.calculateSecondNote(Cs));
		assertEquals("G0.5#", intervC_E2s.calculateSecondNote(C1_5s));
		assertEquals("G#", intervC_E2s.calculateSecondNote(C2s));
		
		//Downward
		Note c2b = new Note(Note.c, Accidental.DOUBLE_FLAT);
		Note c1_5b = new Note(Note.c, Accidental.FLAT_AND_A_HALF);
		Note cb = new Note(Note.c, Accidental.FLAT);
		Note c0_5b = new Note(Note.c, Accidental.HALF_FLAT);
		Note c = new Note(Note.c, Accidental.NATURAL);
		Note c0_5s = new Note(Note.c, Accidental.HALF_SHARP);
		Note cs = new Note(Note.c, Accidental.SHARP);
		Note c1_5s = new Note(Note.c, Accidental.SHARP_AND_A_HALF);
		Note c2s = new Note(Note.c, Accidental.DOUBLE_SHARP);

		//diminished
		assertEquals("Ab", intervE2b_C.calculateSecondNote(c2b));
		assertEquals("A0.5b", intervE2b_C.calculateSecondNote(c1_5b));
		assertEquals("A=", intervE2b_C.calculateSecondNote(cb));
		assertEquals("A0.5#", intervE2b_C.calculateSecondNote(c0_5b));
		assertEquals("A#", intervE2b_C.calculateSecondNote(c));
		assertEquals("A1.5#", intervE2b_C.calculateSecondNote(c0_5s));
		assertEquals("A##", intervE2b_C.calculateSecondNote(cs));
		assertEquals("B0.5#", intervE2b_C.calculateSecondNote(c1_5s));
		assertEquals("B#", intervE2b_C.calculateSecondNote(c2s));
		
		//subminor
		assertEquals("A1.5b", intervE1_5b_C.calculateSecondNote(c2b));
		assertEquals("Ab", intervE1_5b_C.calculateSecondNote(c1_5b));
		assertEquals("A0.5b", intervE1_5b_C.calculateSecondNote(cb));
		assertEquals("A=", intervE1_5b_C.calculateSecondNote(c0_5b));
		assertEquals("A0.5#", intervE1_5b_C.calculateSecondNote(c));
		assertEquals("A#", intervE1_5b_C.calculateSecondNote(c0_5s));
		assertEquals("A1.5#", intervE1_5b_C.calculateSecondNote(cs));
		assertEquals("A##", intervE1_5b_C.calculateSecondNote(c1_5s));
		assertEquals("B0.5#", intervE1_5b_C.calculateSecondNote(c2s));

		//minor
		assertEquals("Abb", intervEb_C.calculateSecondNote(c2b));
		assertEquals("A1.5b", intervEb_C.calculateSecondNote(c1_5b));
		assertEquals("Ab", intervEb_C.calculateSecondNote(cb));
		assertEquals("A0.5b", intervEb_C.calculateSecondNote(c0_5b));
		assertEquals("A=", intervEb_C.calculateSecondNote(c));
		assertEquals("A0.5#", intervEb_C.calculateSecondNote(c0_5s));
		assertEquals("A#", intervEb_C.calculateSecondNote(cs));
		assertEquals("A1.5#", intervEb_C.calculateSecondNote(c1_5s));
		assertEquals("A##", intervEb_C.calculateSecondNote(c2s));
		
		//neutral
		assertEquals("G0.5b", intervE0_5b_C.calculateSecondNote(c2b));
		assertEquals("Abb", intervE0_5b_C.calculateSecondNote(c1_5b));
		assertEquals("A1.5b", intervE0_5b_C.calculateSecondNote(cb));
		assertEquals("Ab", intervE0_5b_C.calculateSecondNote(c0_5b));
		assertEquals("A0.5b", intervE0_5b_C.calculateSecondNote(c));
		assertEquals("A=", intervE0_5b_C.calculateSecondNote(c0_5s));
		assertEquals("A0.5#", intervE0_5b_C.calculateSecondNote(cs));
		assertEquals("A#", intervE0_5b_C.calculateSecondNote(c1_5s));
		assertEquals("A1.5#", intervE0_5b_C.calculateSecondNote(c2s));
		
		//major
		assertEquals("Gb", intervE_C.calculateSecondNote(c2b));
		assertEquals("G0.5b", intervE_C.calculateSecondNote(c1_5b));
		assertEquals("Abb", intervE_C.calculateSecondNote(cb));
		assertEquals("A1.5b", intervE_C.calculateSecondNote(c0_5b));
		assertEquals("Ab", intervE_C.calculateSecondNote(c));
		assertEquals("A0.5b", intervE_C.calculateSecondNote(c0_5s));
		assertEquals("A=", intervE_C.calculateSecondNote(cs));
		assertEquals("A0.5#", intervE_C.calculateSecondNote(c1_5s));
		assertEquals("A#", intervE_C.calculateSecondNote(c2s));
		
		//super major
		assertEquals("G1.5b", intervE0_5s_C.calculateSecondNote(c2b));
		assertEquals("Gb", intervE0_5s_C.calculateSecondNote(c1_5b));
		assertEquals("G0.5b", intervE0_5s_C.calculateSecondNote(cb));
		assertEquals("Abb", intervE0_5s_C.calculateSecondNote(c0_5b));
		assertEquals("A1.5b", intervE0_5s_C.calculateSecondNote(c));
		assertEquals("Ab", intervE0_5s_C.calculateSecondNote(c0_5s));
		assertEquals("A0.5b", intervE0_5s_C.calculateSecondNote(cs));
		assertEquals("A=", intervE0_5s_C.calculateSecondNote(c1_5s));
		assertEquals("A0.5#", intervE0_5s_C.calculateSecondNote(c2s));
		
		//augmented
		assertEquals("Gbb", intervEs_C.calculateSecondNote(c2b));
		assertEquals("G1.5b", intervEs_C.calculateSecondNote(c1_5b));
		assertEquals("Gb", intervEs_C.calculateSecondNote(cb));
		assertEquals("G0.5b", intervEs_C.calculateSecondNote(c0_5b));
		assertEquals("Abb", intervEs_C.calculateSecondNote(c));
		assertEquals("A1.5b", intervEs_C.calculateSecondNote(c0_5s));
		assertEquals("Ab", intervEs_C.calculateSecondNote(cs));
		assertEquals("A0.5b", intervEs_C.calculateSecondNote(c1_5s));
		assertEquals("A=", intervEs_C.calculateSecondNote(c2s));
		
		//super augmented
		assertEquals("F0.5b", intervE1_5s_C.calculateSecondNote(c2b));
		assertEquals("Gbb", intervE1_5s_C.calculateSecondNote(c1_5b));
		assertEquals("G1.5b", intervE1_5s_C.calculateSecondNote(cb));
		assertEquals("Gb", intervE1_5s_C.calculateSecondNote(c0_5b));
		assertEquals("G0.5b", intervE1_5s_C.calculateSecondNote(c));
		assertEquals("Abb", intervE1_5s_C.calculateSecondNote(c0_5s));
		assertEquals("A1.5b", intervE1_5s_C.calculateSecondNote(cs));
		assertEquals("Ab", intervE1_5s_C.calculateSecondNote(c1_5s));
		assertEquals("A0.5b", intervE1_5s_C.calculateSecondNote(c2s));
		
		//double augmented
		assertEquals("Fb", intervE2s_C.calculateSecondNote(c2b));
		assertEquals("F0.5b", intervE2s_C.calculateSecondNote(c1_5b));
		assertEquals("Gbb", intervE2s_C.calculateSecondNote(cb));
		assertEquals("G1.5b", intervE2s_C.calculateSecondNote(c0_5b));
		assertEquals("Gb", intervE2s_C.calculateSecondNote(c));
		assertEquals("G0.5b", intervE2s_C.calculateSecondNote(c0_5s));
		assertEquals("Abb", intervE2s_C.calculateSecondNote(cs));
		assertEquals("A1.5b", intervE2s_C.calculateSecondNote(c1_5s));
		assertEquals("Ab", intervE2s_C.calculateSecondNote(c2s));
		
		
		Note rEb = intervC_Eb.calculateSecondNote(C);
		assertTrue(Accidental.FLAT.equals(rEb.getAccidental()));
		Note rE0_5b = intervC_E0_5b.calculateSecondNote(C);
		assertTrue(Accidental.HALF_FLAT.equals(rE0_5b.getAccidental()));
		Note rE = intervC_E.calculateSecondNote(C);
		assertTrue(Accidental.NATURAL.equals(rE.getAccidental()));
		Note rE0_5s = intervC_E0_5s.calculateSecondNote(C);
		assertTrue(Accidental.HALF_SHARP.equals(rE0_5s.getAccidental()));
		Note rEs = intervC_Es.calculateSecondNote(C);
		assertTrue(Accidental.SHARP.equals(rEs.getAccidental()));
		Note rE1_5s = intervC_E1_5s.calculateSecondNote(C);
		assertTrue(Accidental.SHARP_AND_A_HALF.equals(rE1_5s.getAccidental()));
		Note rE2s = intervC_E2s.calculateSecondNote(C);
		assertTrue(Accidental.DOUBLE_SHARP.equals(rE2s.getAccidental()));
		
		//...
		
		Interval intervC0_5sE = new Interval(C0_5s, E, null);
		assertEquals(3, intervC0_5sE.getSemitones());
		assertEquals(7, intervC0_5sE.getQuartertones());
		assertEquals(Interval.THIRD, intervC0_5sE.getLabel());
		assertEquals(-0.5f, intervC0_5sE.getMicrotonalOffset());
		rE0_5b = intervC0_5sE.calculateSecondNote(C);
		assertEquals(Note.E, rE0_5b.getHeight());
		assertTrue(Accidental.HALF_FLAT.equals(rE0_5b.getAccidental()));
		rE = intervC0_5sE.calculateSecondNote(C0_5s);
		assertEquals(Note.E, rE.getHeight());
		assertTrue(Accidental.NATURAL.equals(rE.getAccidental()));
		rE0_5s = intervC0_5sE.calculateSecondNote(Cs);
		assertEquals(Note.E, rE0_5s.getHeight());
		assertTrue(Accidental.HALF_SHARP.equals(rE0_5s.getAccidental()));
		
	}


}

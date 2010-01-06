import java.util.Vector;

import junit.framework.TestCase;
import abc.midi.MidiConverterAbstract;
import abc.notation.AccidentalType;
import abc.notation.KeySignature;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.NoteHeightException;
import abc.notation.Tune;
import abc.parser.TuneParser;


public class HeightTest extends TestCase {
	public static void main(String[] args) {
	}

	public HeightTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1(){
		Note note = new Note(Note.C, AccidentalType.NONE);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(0, note.getOctaveTransposition());
		
		note = new Note(Note.c, AccidentalType.NONE);
		assertEquals(Note.C, note.getStrictHeight());
		note.setAccidental(AccidentalType.SHARP);
		assertEquals(Note.C, note.getStrictHeight());
		
		note = new Note(Note.C, AccidentalType.NONE, (byte)1);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(Note.c, note.getHeight());
		assertEquals(1, note.getOctaveTransposition());
		
		note = new Note(Note.c, AccidentalType.NONE, (byte)1);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(2, note.getOctaveTransposition());
		
		note = new Note(Note.c, AccidentalType.NONE);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(Note.c, note.getHeight());
		assertEquals(1, note.getOctaveTransposition());
		
		note = new Note(Note.C, AccidentalType.NONE, (byte)2);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(2, note.getOctaveTransposition());
		assertEquals(Note.c*2, note.getHeight());
		
		note = new Note(Note.REST, AccidentalType.NONE);
		assertEquals(Note.REST, note.getStrictHeight());
		assertEquals(0, note.getOctaveTransposition());
	}
	
	public void testMidiHeight() {
		Note note = new Note(Note.C, AccidentalType.NONE);
		KeySignature ks = new KeySignature(Note.C, KeySignature.MAJOR);
		//60 corresponds to the midi note number for the C note.
		assertEquals(60, MidiConverterAbstract.getMidiNoteNumber(note, ks));
		note.setAccidental(AccidentalType.SHARP);
		assertEquals(61, MidiConverterAbstract.getMidiNoteNumber(note, ks));
		note.setAccidental(AccidentalType.NONE);
		note.setOctaveTransposition((byte)1);
		assertEquals(72, MidiConverterAbstract.getMidiNoteNumber(note, ks));
		
	}
	
	public void testNoteHeightComparison() {
		String tuneAsString = "X:1\nT:test\nK:c\nabcdef\n";
		TuneParser tuneParser = new TuneParser();
		Tune tune = tuneParser.parse(tuneAsString);
		Note firstNote = (Note)tune.getMusic().elementAt(1);
		Note lastNote = (Note)tune.getMusic().elementAt(6);
		//System.out.println("highest note : " + tune.getMusic().getHighestNoteBewteen(0, tune.getMusic().size()-1));
		assertEquals(Note.b,((Note) tune.getMusic().getHighestNoteBewteen(firstNote, lastNote)).getHeight());
	}
	
	/** */
	public void test2(){
		String tuneAsString = "X:1\nT:test\nK:c\nb/2b2bb4b8\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getMusic().elementAt(1);
		Note secondNote = (Note)tune.getMusic().elementAt(2);
		Note thirdNote = (Note)tune.getMusic().elementAt(3);
		Note fourthNote = (Note)tune.getMusic().elementAt(4);
		assertEquals(firstNote.getHeight(), Note.b);
		assertEquals(secondNote.getHeight(), Note.b);
		assertEquals(thirdNote.getHeight(), Note.b);
		assertEquals(fourthNote.getHeight(), Note.b);
	}
	
	/** */
	public void test3(){
		Tune tune = new TuneParser().parse("X:1\nT:test\nK:c\naFc\n");
		Note firstNote = (Note)tune.getMusic().elementAt(1);
		Note secondNote = (Note)tune.getMusic().elementAt(2);
		Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertEquals(tune.getMusic().getLowestNoteBewteen(firstNote, thirdNote), secondNote);
		assertEquals(tune.getMusic().getHighestNoteBewteen(firstNote, thirdNote), firstNote);
	}
	
	public void test4(){
		Tune tune = new TuneParser().parse("X:1\nT:test\nK:c\n[aFc]\n");
		MultiNote firstNote = (MultiNote)tune.getMusic().elementAt(1);
		//Note secondNote = (Note)tune.getMusic().elementAt(2);
		//Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertEquals(firstNote.getHighestNote().getStrictHeight(), Note.A);
		assertEquals(firstNote.getLowestNote().getStrictHeight(), Note.F);
	}
	
	public void testNoteVariousHeightComparison() {
		String tuneAsString = "X:1\nT:test\nK:c\nA,B,CDEFa,b,cdef\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		for (int i=0; i<tune.getMusic().size()-1; i++)
			if (tune.getMusic().elementAt(i) instanceof Note && tune.getMusic().elementAt(i+1) instanceof Note)
				assertFalse(((Note)tune.getMusic().elementAt(i)).isHigherThan((Note)tune.getMusic().elementAt(i+1)));
	}
	
	public void test5() {
		String tuneAsString = "X:1\nT:test\nM:4/4\nL:1/4\nK:c\n[A2a]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote n = (MultiNote)tune.getMusic().elementAt(2);
		assertEquals(n.getLowestNote().getHeight(), Note.A);
		assertEquals(n.getHighestNote().getHeight(), Note.a);
		Note[] shorterNotes = MultiNote.getNotesShorterThan(n.toArray(), Note.HALF);
		assertEquals(shorterNotes.length, 1);
		assertEquals(shorterNotes[0].getHeight(), Note.a);
	}
	
	public void test6() {
		String tuneAsString = "X:1\nT:test\nM:4/4\nL:1/4\nK:c\n[aAa']\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote n = (MultiNote)tune.getMusic().elementAt(2);
		Vector v = n.getNotesAsVector();
		assertEquals(((Note)v.elementAt(0)).getHeight(), Note.A);
		assertEquals(((Note)v.elementAt(1)).getHeight(), Note.a);
		assertEquals(((Note)v.elementAt(2)).getStrictHeight(), Note.A);
		assertEquals(((Note)v.elementAt(2)).getOctaveTransposition(), 2);
	}
	
	public void test7() {
		String tuneAsString = "X:1\nT:test\nM:4/4\nL:1/4\nK:c\n[a'AaA,]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote n = (MultiNote)tune.getMusic().elementAt(2);
		Vector v = n.getNotesAsVector();
		assertEquals(((Note)v.elementAt(0)).getStrictHeight(), Note.A);
		assertEquals(((Note)v.elementAt(0)).getOctaveTransposition(), -1);
		assertEquals(((Note)v.elementAt(1)).getHeight(), Note.A);
		assertEquals(((Note)v.elementAt(2)).getHeight(), Note.a);
		assertEquals(((Note)v.elementAt(3)).getStrictHeight(), Note.A);
		assertEquals(((Note)v.elementAt(3)).getOctaveTransposition(), 2);
	}
	
	public void test8transposeAndEnharmonics() {
		//C
		Note C = new Note(Note.C, AccidentalType.NATURAL);
		C.setDuration(Note.DOTTED_HALF);
		Note Cs = Note.transpose(C, 1);
		assertTrue(Cs != C); //cloned OK
		assertEquals(C.getMidiLikeHeight(), 0);
		assertEquals(Cs.getMidiLikeHeight(), 1);
		assertEquals(Cs.getStrictHeight(), Note.C);
		assertEquals(Cs.getAccidental(), AccidentalType.SHARP);
		assertEquals(Cs.getOctaveTransposition(), 0);
		assertEquals(Cs.getDuration(), Note.DOTTED_HALF);
		// ^A, / _B,
		Note Bb_1 = Note.transpose(Cs, -3);
		assertEquals(Bb_1.getMidiLikeHeight(), -2);
		assertEquals(Bb_1.getStrictHeight(), Note.A);
		assertEquals(Bb_1.getAccidental(), AccidentalType.SHARP);
		assertEquals(Bb_1.getOctaveTransposition(), -1);
		assertEquals(Bb_1.getDuration(), Note.DOTTED_HALF);
		// G,
		Note G_1 = Note.transpose(C, -5);
		assertEquals(G_1.getMidiLikeHeight(), -5);
		assertEquals(G_1.getStrictHeight(), Note.G);
		assertEquals(G_1.getAccidental(), AccidentalType.NATURAL);
		assertEquals(G_1.getOctaveTransposition(), -1);
		//too high, too low
		try {
			Note.transpose(C, 128);
			fail();
		} catch (NoteHeightException OK) {}
		try {
			Note.transpose(C, -129);
			fail();
		} catch (NoteHeightException OK) {}
		
		//enharmonics

		Note eb1 = new Note(Note.E, AccidentalType.FLAT, (byte) 1);
		assertEquals(eb1.getMidiLikeHeight(), 15);
		Note ds1 = Note.createEnharmonic(eb1, new byte[] {AccidentalType.SHARP});
		assertTrue(eb1 != ds1); //cloned OK
		assertEquals(ds1.getMidiLikeHeight(), 15);
		assertEquals(ds1.getStrictHeight(), Note.D);
		assertEquals(ds1.getAccidental(), AccidentalType.SHARP);
		Note fbb1 = Note.createEnharmonic(eb1, new byte[] { AccidentalType.DOUBLE_FLAT });
		assertEquals(fbb1.getMidiLikeHeight(), 15);
		assertEquals(fbb1.getStrictHeight(), Note.F);
		assertEquals(fbb1.getAccidental(), AccidentalType.DOUBLE_FLAT);
		
		Note Bs_1 = Note.createEnharmonic(C, new byte[] { AccidentalType.SHARP });
		assertEquals(Bs_1.getMidiLikeHeight(), 0);
		assertEquals(Bs_1.getStrictHeight(), Note.B);
		assertEquals(Bs_1.getAccidental(), AccidentalType.SHARP);
		assertEquals(Bs_1.getOctaveTransposition(), -1);
		
		Note unchanged = Note.createEnharmonic(C, new byte[] { AccidentalType.DOUBLE_SHARP });
		assertEquals(unchanged.getMidiLikeHeight(), 0);
		assertEquals(unchanged.getStrictHeight(), Note.C);
		assertEquals(unchanged.getAccidental(), AccidentalType.NATURAL);
		
		//enharmonics based on key
		KeySignature Dmajor = new KeySignature(Note.D, KeySignature.MAJOR);
		//=D => D (acc none)
		Note DmajorI = new Note(Note.D, AccidentalType.NATURAL);
		Note DmajorIa = Note.createEnharmonic(DmajorI, Dmajor);
		assertEquals(DmajorIa.getStrictHeight(), Note.D);
		assertEquals(DmajorIa.getAccidental(), AccidentalType.NONE);
		//^D => _E or ^D ? avoid to have D and ^D so will return _E
		Note DmajorDs = new Note(Note.D, AccidentalType.SHARP);
		Note DmajorDsa = Note.createEnharmonic(DmajorDs, Dmajor);
		assertEquals(DmajorDsa.getStrictHeight(), Note.D);
		assertEquals(DmajorDsa.getAccidental(), AccidentalType.SHARP);
		//_E => _E or ^D ? would like to avoid D and ^D so will return _E
		//but for now, returns the dominant accidental in the scale
		//D major contains F# and C#, so returns D#
		Note DmajorEb = new Note(Note.E, AccidentalType.FLAT);
		Note DmajorEba = Note.createEnharmonic(DmajorEb, Dmajor);
		assertEquals(DmajorEba.getStrictHeight(), Note.D);
		assertEquals(DmajorEba.getAccidental(), AccidentalType.SHARP);
		//E => E
		Note DmajorII = new Note(Note.E);
		Note DmajorIIa = Note.createEnharmonic(DmajorII, Dmajor);
		assertEquals(DmajorIIa.getStrictHeight(), Note.E);
		assertEquals(DmajorIIa.getAccidental(), AccidentalType.NONE);
		//^E => unchanged
		Note DmajorEs = new Note(Note.E, AccidentalType.SHARP);
		Note DmajorEsa = Note.createEnharmonic(DmajorEs, Dmajor);
		assertEquals(DmajorEsa.getStrictHeight(), Note.E);
		assertEquals(DmajorEsa.getAccidental(), AccidentalType.SHARP);
		//^^E => ^F => F (acc none)
		Note DmajorIII = new Note(Note.E, AccidentalType.DOUBLE_SHARP);
		Note DmajorIIIa = Note.createEnharmonic(DmajorIII, Dmajor);
		assertEquals(DmajorIIIa.getStrictHeight(), Note.F);
		assertEquals(DmajorIIIa.getAccidental(), AccidentalType.NONE);
		//__A => G (acc none)
		Note DmajorIV = new Note(Note.A, AccidentalType.DOUBLE_FLAT);
		Note DmajorIVa = Note.createEnharmonic(DmajorIV, Dmajor);
		assertEquals(DmajorIVa.getStrictHeight(), Note.G);
		assertEquals(DmajorIVa.getAccidental(), AccidentalType.NONE);
		//_A => unchanged or ^G ? ^G because scale has only sharps
		Note DmajorAb = new Note(Note.A, AccidentalType.FLAT);
		Note DmajorAba = Note.createEnharmonic(DmajorAb, Dmajor);
		assertEquals(DmajorAba.getStrictHeight(), Note.G);
		assertEquals(DmajorAba.getAccidental(), AccidentalType.SHARP);
		//A => unchanged (acc none)
		Note DmajorV = new Note(Note.A);
		Note DmajorVa = Note.createEnharmonic(DmajorV, Dmajor);
		assertEquals(DmajorVa.getStrictHeight(), Note.A);
		assertEquals(DmajorVa.getAccidental(), AccidentalType.NONE);
		//^A => unchanged (key has only flat, will not returns _B)
		Note DmajorAs = new Note(Note.A, AccidentalType.SHARP);
		Note DmajorAsa = Note.createEnharmonic(DmajorAs, Dmajor);
		assertEquals(DmajorAsa.getStrictHeight(), Note.A);
		assertEquals(DmajorAsa.getAccidental(), AccidentalType.SHARP);
		//__C => ^A (key has only flat)
		Note DmajorCbb = new Note(Note.C, AccidentalType.DOUBLE_FLAT);
		Note DmajorCbba = Note.createEnharmonic(DmajorCbb, Dmajor);
		assertEquals(DmajorCbba.getStrictHeight(), Note.A);
		assertEquals(DmajorCbba.getAccidental(), AccidentalType.SHARP);
		//_C => B (acc none)
		Note DmajorVI = new Note(Note.C, AccidentalType.FLAT);
		Note DmajorVIa = Note.createEnharmonic(DmajorVI, Dmajor);
		assertEquals(DmajorVIa.getStrictHeight(), Note.B);
		assertEquals(DmajorVIa.getAccidental(), AccidentalType.NONE);
		//^B => =C (acc natural)
		Note DmajorBs = new Note(Note.B, AccidentalType.SHARP);
		Note DmajorBsa = Note.createEnharmonic(DmajorBs, Dmajor);
		assertEquals(DmajorBsa.getStrictHeight(), Note.C);
		assertEquals(DmajorBsa.getAccidental(), AccidentalType.NATURAL);
		//C => unchanged (acc none)
		Note DmajorVII = new Note(Note.C, AccidentalType.NONE);
		Note DmajorVIIa = Note.createEnharmonic(DmajorVII, Dmajor);
		assertEquals(DmajorVIIa.getStrictHeight(), Note.C);
		assertEquals(DmajorVIIa.getAccidental(), AccidentalType.NONE);
		//^C => C (acc none)
		DmajorVII = new Note(Note.C, AccidentalType.SHARP);
		Note DmajorVIIb = Note.createEnharmonic(DmajorVII, Dmajor);
		assertEquals(DmajorVIIb.getStrictHeight(), Note.C);
		assertEquals(DmajorVIIb.getAccidental(), AccidentalType.NONE);
		//^^C => D (acc none)
		Note DmajorCss = new Note(Note.C, AccidentalType.DOUBLE_SHARP);
		Note DmajorCssa = Note.createEnharmonic(DmajorCss, Dmajor);
		assertEquals(DmajorCssa.getStrictHeight(), Note.D);
		assertEquals(DmajorCssa.getAccidental(), AccidentalType.NONE);
		
		//transposition & enharmonic
		C = new Note(Note.C);
		KeySignature EbMaj = new KeySignature(Note.E, AccidentalType.FLAT, KeySignature.MAJOR);
		Note Eb = Note.transpose(C, 3, EbMaj);
		assertEquals(Eb.getStrictHeight(), Note.E);
		assertEquals(Eb.getAccidental(), AccidentalType.NONE); //Eb is include in the key
		
		//transposition of C D E F G A B c in C scale.
		//all notes are natural, need no accidental (NONE)
		//in each transposition, all notes should have no accidental
		KeySignature key = new KeySignature(Note.C, AccidentalType.NATURAL, KeySignature.MAJOR);
		Note[] scale = new Note[] {
			new Note(Note.C, AccidentalType.NATURAL),
			new Note(Note.D, AccidentalType.NATURAL),
			new Note(Note.E, AccidentalType.NATURAL),
			new Note(Note.F, AccidentalType.NATURAL),
			new Note(Note.G, AccidentalType.NATURAL),
			new Note(Note.A, AccidentalType.NATURAL),
			new Note(Note.B, AccidentalType.NATURAL),
		};
		for (int i = 0; i <= 12; i++) {
			KeySignature transpKey = KeySignature.transpose(key, i);
			for (int j = 0; j < scale.length; j++) {
				assertEquals(Note.transpose(scale[j], i, transpKey)
							.getAccidental(), AccidentalType.NONE);
			}
		}
	}
	
	public void test9createFromMidiHeight() {
		//C
		Note C = Note.createFromMidiLikeHeight(0);
		assertEquals(C.getMidiLikeHeight(), 0);
		assertEquals(C.getStrictHeight(), Note.C);
		assertEquals(C.getAccidental(), AccidentalType.NATURAL);
		assertEquals(C.getOctaveTransposition(), 0);
	
		//B,
		Note B_1 = Note.createFromMidiLikeHeight(-1);
		assertEquals(B_1.getMidiLikeHeight(), -1);
		assertEquals(B_1.getStrictHeight(), Note.B);
		assertEquals(B_1.getAccidental(), AccidentalType.NATURAL);

		//^A, / _B,
		Note Bb_1 = Note.createFromMidiLikeHeight(-2);
		assertEquals(Bb_1.getMidiLikeHeight(), -2);
		assertEquals(Bb_1.getStrictHeight(), Note.A);
		assertEquals(Bb_1.getAccidental(), AccidentalType.SHARP);
		
		//^d / _e
		Note eb1 = Note.createFromMidiLikeHeight(15);
		assertEquals(eb1.getMidiLikeHeight(), 15);
		assertEquals(eb1.getStrictHeight(), Note.D);
		assertEquals(eb1.getAccidental(), AccidentalType.SHARP);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

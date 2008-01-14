import java.util.Vector;

import junit.framework.TestCase;
import abc.midi.MidiConverterAbstract;
import abc.notation.AccidentalType;
import abc.notation.KeySignature;
import abc.notation.MultiNote;
import abc.notation.Note;
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
		assertEquals(Note.b, tune.getMusic().getHighestNoteBewteen(firstNote, lastNote).getHeight());
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
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

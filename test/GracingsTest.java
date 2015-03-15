import junit.framework.TestCase;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.Tune;
import abc.parser.TuneParser;

public class GracingsTest extends TestCase {

	public GracingsTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1(){
		String tuneAsString = "X:1\nT:test\nK:C\n{G}A{GG}uBv{G}C{^G_F}_D\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getMusic().getFirstVoice().elementAt(1);
		Note secondNote = (Note)tune.getMusic().getFirstVoice().elementAt(2);
		Note thirdNote = (Note)tune.getMusic().getFirstVoice().elementAt(3);
		//why 5? I can't explain, 5 gets the _D (4th) note
		Note fourthNote = (Note)tune.getMusic().getFirstVoice().elementAt(4);
		assertTrue(firstNote.hasGracingNotes());
		assertEquals(firstNote.getGracingNotes().length, 1);
		//2nd note has 2 gracings and one decoration
		assertTrue(secondNote.hasGracingNotes());
		assertEquals(secondNote.getGracingNotes().length, 2);
		//3rd note is bad writted (gracings must be before decorations)
		//assertFalse with old version, assertTrue now :)
		assertTrue(thirdNote.hasGracingNotes());
		
		//Make sure accidentals works
		assertTrue(fourthNote.hasGracingNotes());
		assertEquals(fourthNote.getGracingNotes().length, 2);
	}
	
	public void test2(){
		String tuneAsString = "X:1\nT:test\nK:C\n{G}[Ac] ({G}A{F}c) (A{G}c)\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		//gracing before chord (multi note)
		MultiNote firstNote = (MultiNote)tune.getMusic().getFirstVoice().elementAt(1);
		assertTrue(firstNote.hasGracingNotes());
		assertEquals(firstNote.getGracingNotes().length, 1);
		
		//gracing before each notes of a group of note (slured)
		Note secondNote = (Note)tune.getMusic().getFirstVoice().elementAt(3);
		Note thirdNote = (Note)tune.getMusic().getFirstVoice().elementAt(4);
		assertEquals(secondNote.getHeight(), Note.A);
		assertTrue(secondNote.hasGracingNotes());
		assertEquals(thirdNote.getHeight(), Note.c);
		assertTrue(thirdNote.hasGracingNotes());
		
		//gracing in the middle of a group of note (slured)
		secondNote = (Note)tune.getMusic().getFirstVoice().elementAt(6);
		thirdNote = (Note)tune.getMusic().getFirstVoice().elementAt(7);
		assertEquals(secondNote.getHeight(), Note.A);
		assertFalse(secondNote.hasGracingNotes());
		assertEquals(thirdNote.getHeight(), Note.c);
		assertTrue(thirdNote.hasGracingNotes());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

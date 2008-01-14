import junit.framework.TestCase;
import abc.notation.MultiNote;
import abc.notation.Tune;
import abc.parser.TuneParser;

public class ChordsTest extends TestCase {
	
	public ChordsTest(String name) {
		super(name);
	}
	
	/** */
	public void test1(){
		String tuneAsString = "X:1\nT:test\nK:c\n[e^fa]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote firstNote = (MultiNote)tune.getMusic().elementAt(1);
		//Note secondNote = (Note)tune.getMusic().elementAt(2);
		//Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertTrue(firstNote.hasUniqueStrictDuration());
		assertTrue(firstNote.hasAccidental());
		assertEquals(firstNote.getLowestNote(), firstNote.getNotesAsVector().firstElement());
		assertEquals(firstNote.getHighestNote(), firstNote.getNotesAsVector().lastElement());
	}
	
	public void test2(){
		String tuneAsString = "X:1\nT:test\nK:c\n[e4^fa]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote firstNote = (MultiNote)tune.getMusic().elementAt(1);
		//Note secondNote = (Note)tune.getMusic().elementAt(2);
		//Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertFalse(firstNote.hasUniqueStrictDuration());
		assertEquals(firstNote.getLowestNote(), firstNote.getNotesAsVector().firstElement());
		assertEquals(firstNote.getHighestNote(), firstNote.getNotesAsVector().lastElement());
		assertEquals(firstNote.getLongestNote(), firstNote.getNotesAsVector().firstElement());
		assertEquals(firstNote.getHighestNote(), firstNote.getNotesAsVector().lastElement());
	}
	
}

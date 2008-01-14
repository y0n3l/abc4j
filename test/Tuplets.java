import junit.framework.TestCase;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.Tune;
import abc.parser.TuneParser;

public class Tuplets extends TestCase {

	public Tuplets(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/** */
	public void test1(){
		String tuneAsString = "X:1\nT:test\nK:c\n(3abc\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getMusic().elementAt(1);
		Note secondNote = (Note)tune.getMusic().elementAt(2);
		Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertTrue(firstNote.isPartOfTuplet());
		assertTrue(secondNote.isPartOfTuplet());
		assertTrue(thirdNote.isPartOfTuplet());
		assertNotNull(firstNote.getTuplet());
		assertEquals(firstNote.getTuplet(), secondNote.getTuplet());
		assertEquals(secondNote.getTuplet(), thirdNote.getTuplet());
	}
	
	public void test2(){
		String tuneAsString = "X:1\nT:test\nK:c\n(3[ae]bc\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote firstNote = (MultiNote)tune.getMusic().elementAt(1);
		Note secondNote = (Note)tune.getMusic().elementAt(2);
		Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertTrue(firstNote.isPartOfTuplet());
		assertTrue(secondNote.isPartOfTuplet());
		assertTrue(thirdNote.isPartOfTuplet());
		assertNotNull(firstNote.getTuplet());
		assertEquals(firstNote.getTuplet(), secondNote.getTuplet());
		assertEquals(secondNote.getTuplet(), thirdNote.getTuplet());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

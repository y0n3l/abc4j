import junit.framework.TestCase;
import abc.notation.AccidentalType;
import abc.notation.Note;
import abc.notation.NoteAbstract;
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
		String tuneAsString = "X:1\nT:test\nK:c\nvAuBC\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getMusic().elementAt(1);
		Note secondNote = (Note)tune.getMusic().elementAt(2);
		Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertTrue(firstNote.getBow()==NoteAbstract.DOWN);
		assertTrue(secondNote.getBow()==NoteAbstract.UP);
		assertTrue(thirdNote.getBow()==NoteAbstract.NONE);
	}
	
	public void test2(){
		String tuneAsString = "X:1\nT:test\nK:c\nv_Au=Bv^c\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getMusic().elementAt(1);
		Note secondNote = (Note)tune.getMusic().elementAt(2);
		Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertEquals(firstNote.getBow(), NoteAbstract.DOWN);
		assertEquals(firstNote.getAccidental(), AccidentalType.FLAT);
		assertEquals(secondNote.getBow(), NoteAbstract.UP);
		assertEquals(secondNote.getAccidental(), AccidentalType.NATURAL);
		assertEquals(thirdNote.getBow(), NoteAbstract.DOWN);
		assertEquals(thirdNote.getAccidental(), AccidentalType.SHARP);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

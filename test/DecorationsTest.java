import junit.framework.TestCase;
import abc.notation.Decoration;
import abc.notation.Note;
import abc.notation.Tune;
import abc.parser.TuneParser;

public class DecorationsTest extends TestCase {

	public DecorationsTest(String name) {
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
		assertTrue(firstNote.hasDecoration(Decoration.DOWNBOW));
		assertTrue(secondNote.hasDecoration(Decoration.UPBOW));
		assertFalse(thirdNote.hasDecorations());
		assertFalse(thirdNote.hasDecoration(Decoration.UPBOW));
		assertFalse(thirdNote.hasDecoration(Decoration.DOWNBOW));
	}
	
	public void test2(){
		String tuneAsString = "X:1\nT:test\nK:c\nv_Au=Bv^c\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getMusic().elementAt(1);
		Note secondNote = (Note)tune.getMusic().elementAt(2);
		Note thirdNote = (Note)tune.getMusic().elementAt(3);
		assertTrue(firstNote.hasDecoration(Decoration.DOWNBOW));
		assertTrue(firstNote.getAccidental().isFlat());
		assertTrue(secondNote.hasDecoration(Decoration.UPBOW));
		assertTrue(secondNote.getAccidental().isNatural());
		assertTrue(thirdNote.hasDecoration(Decoration.DOWNBOW));
		assertTrue(thirdNote.getAccidental().isSharp());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

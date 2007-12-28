import java.io.File;

import junit.framework.TestCase;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.NoteAbstract;
import abc.notation.Tune;
import abc.parser.TuneBook;
import abc.parser.TuneParser;

public class TieTest extends TestCase {
	
	private static final String FILE_NAME = "../ressources/crash.abc";
	
	public TieTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * c-c
	 */
	public void test1(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			//checkSlursInScore(tb.getTune(25).getScore());
			Tune.Score score = tb.getTune(25).getScore(); 
			Note firstNote = (Note)score.elementAt(1);
			Note secondNote = (Note)score.elementAt(2);
			assertTrue(firstNote.isTied());
			assertTrue(secondNote.isTied());
			assertFalse(firstNote.isPartOfSlur());
			assertFalse(secondNote.isPartOfSlur());
			assertNull(firstNote.getSlurDefinition());
			assertNull(secondNote.getSlurDefinition());
			assertNotNull(firstNote.getTieDefinition());
			assertNotNull(secondNote.getTieDefinition());
			assertEquals(secondNote.getTieDefinition().getStart(), firstNote);
			assertEquals(firstNote.getTieDefinition().getEnd(), secondNote);
			assertEquals(firstNote.getTieDefinition(), secondNote.getTieDefinition());
		}
		catch (Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * a-|b
	 */
	public void test2(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			Tune.Score score = tb.getTune(28).getScore();
			Note firstNote = (Note)score.elementAt(1);
			Note secondNote = (Note)score.elementAt(3);
			assertTrue(firstNote.isTied());
			assertFalse(secondNote.isTied());
			assertFalse(firstNote.isPartOfSlur());
			assertFalse(secondNote.isPartOfSlur());
			assertNull(firstNote.getSlurDefinition());
			assertNull(secondNote.getSlurDefinition());
			assertNotNull(firstNote.getTieDefinition());
			assertNull(secondNote.getTieDefinition());
		}
		catch (Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * a-|a
	 */
	public void test3(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			Tune.Score score = tb.getTune(30).getScore();
			Note firstNote = (Note)score.elementAt(1);
			Note secondNote = (Note)score.elementAt(3);
			assertTrue(firstNote.isTied());
			assertTrue(secondNote.isTied());
			assertFalse(firstNote.isPartOfSlur());
			assertFalse(secondNote.isPartOfSlur());
			assertNull(firstNote.getSlurDefinition());
			assertNull(secondNote.getSlurDefinition());
			assertNotNull(firstNote.getTieDefinition());
			assertNotNull(secondNote.getTieDefinition());
			assertEquals(secondNote.getTieDefinition().getStart(), firstNote);
			assertEquals(firstNote.getTieDefinition().getEnd(), secondNote);
			assertEquals(firstNote.getTieDefinition(), secondNote.getTieDefinition());
		}
		catch (Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	public void test4(){
		String tuneAsString = "X:1\nT:test\nK:c\n[A-a]A\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote firstNote = (MultiNote)tune.getScore().elementAt(1);
		Note secondNote = (Note)tune.getScore().elementAt(2);
		assertFalse(firstNote.isBeginingSlur());
		Note bassNote = firstNote.getLowestNote();
		assertTrue(bassNote.isBeginningTie());
		assertTrue(secondNote.isEndingTie());
		assertNotNull(bassNote.getTieDefinition());
		assertEquals(bassNote.getTieDefinition().getStart(), bassNote);
		assertEquals(bassNote.getTieDefinition().getEnd(), secondNote);
		assertEquals(bassNote.getTieDefinition(), secondNote.getTieDefinition());
	}
	
	public void test5(){
		String tuneAsString = "X:1\nT:test\nK:c\n[A-a][Aa]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote firstMNote = (MultiNote)tune.getScore().elementAt(1);
		MultiNote secondMNote = (MultiNote)tune.getScore().elementAt(2);
		assertTrue(firstMNote.getLowestNote().isBeginningTie());
		assertTrue(secondMNote.getLowestNote().isEndingTie());
	}
	
	public void test6(){
		String tuneAsString = "X:1\nT:test\nK:c\n[A-a-][Aa]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote firstMNote = (MultiNote)tune.getScore().elementAt(1);
		MultiNote secondMNote = (MultiNote)tune.getScore().elementAt(2);
		assertTrue(firstMNote.getLowestNote().isBeginningTie());
		assertTrue(secondMNote.getLowestNote().isEndingTie());
		assertTrue(firstMNote.getHighestNote().isBeginningTie());
		assertTrue(secondMNote.getHighestNote().isEndingTie());
	}
	
	public void test7(){
		String tuneAsString = "X:1\nT:test\nK:c\nA-[Aa]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getScore().elementAt(1);
		MultiNote secondMNote = (MultiNote)tune.getScore().elementAt(2);
		assertTrue(firstNote.isBeginningTie());
		assertTrue(secondMNote.getLowestNote().isEndingTie());
		assertEquals(firstNote.getTieDefinition(), secondMNote.getLowestNote().getTieDefinition());
	}
	
	public void test8(){
		String tuneAsString = "X:0\nT:test\nK:C\na[A-a-][A2a2]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getScore().elementAt(1);
		MultiNote secondMNote = (MultiNote)tune.getScore().elementAt(2);
		MultiNote thirdMNote = (MultiNote)tune.getScore().elementAt(3);
		assertTrue(secondMNote.getLowestNote().isBeginningTie());
		assertTrue(secondMNote.getHighestNote().isBeginningTie());
		assertTrue(thirdMNote.getLowestNote().isEndingTie());
		assertTrue(thirdMNote.getHighestNote().isEndingTie());
		assertEquals(secondMNote.getLowestNote().getTieDefinition(), thirdMNote.getLowestNote().getTieDefinition());
		assertEquals(secondMNote.getHighestNote().getTieDefinition(), thirdMNote.getHighestNote().getTieDefinition());
	}
	
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

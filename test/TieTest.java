import java.io.File;

import junit.framework.TestCase;
import abc.notation.Note;
import abc.notation.Tune;
import abc.parser.TuneBook;

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
	
	/*private void checkTieInScore(Score score) {
		Note firstNote = null;
		Note lastNote = null;
		for (int i=0; i<score.size(); i++)
			if (score.elementAt(i) instanceof Note) {
				Note note = ((Note)score.elementAt(i));
				if (firstNote==null)
					firstNote = note;
				lastNote = note;
				assertTrue(note.isPartOfSlur());
			}
		assertNotNull(firstNote.getSlurDefinition());
		assertTrue(firstNote.isTied());
		assertFalse(lastNote.isTied());
		assertTrue(firstNote.isBeginingSlur());
		assertTrue(lastNote.isEndingSlur());
		assertEquals(firstNote.getSlurDefinition(), lastNote.getSlurDefinition());
		
	}*/
	
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

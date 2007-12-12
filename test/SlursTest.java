import java.io.File;

import junit.framework.TestCase;
import abc.notation.Note;
import abc.notation.Tune.Score;
import abc.parser.TuneBook;

public class SlursTest extends TestCase {
	
	private static final String FILE_NAME = "../ressources/crash.abc";
	
	public SlursTest(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * (cd)
	 *
	 */
	public void test1(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			checkSlursInScore(tb.getTune(26).getScore());
/*			Note firstNote = (Note)score.elementAt(1);
			Note secondNote = (Note)score.elementAt(2);
			assertTrue(firstNote.isPartOfSlur());
			assertTrue(secondNote.isPartOfSlur());
			assertNotNull(firstNote.getSlurDefinition());
			assertNotNull(secondNote.getSlurDefinition());
			assertEquals(firstNote.getSlurDefinition().getStart(), firstNote);
			assertEquals(firstNote.getSlurDefinition().getEnd(), secondNote);
			assertEquals(firstNote.getSlurDefinition(), secondNote.getSlurDefinition());*/
		}
		catch (Exception e ) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * (abcdefg)
	 */
	public void test2(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			checkSlursInScore(tb.getTune(27).getScore());
		}
		catch (Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * (a|b)
	 */
	public void test3(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			checkSlursInScore(tb.getTune(29).getScore());
		}
		catch (Exception e ) {
			throw new RuntimeException(e);
		}
	}
	
	private void checkSlursInScore(Score score) {
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
		assertFalse(firstNote.isTied());
		assertFalse(lastNote.isTied());
		assertTrue(firstNote.isBeginingSlur());
		assertTrue(lastNote.isEndingSlur());
		assertEquals(firstNote.getSlurDefinition(), lastNote.getSlurDefinition());
		
	}
	
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

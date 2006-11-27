import java.io.File;

import junit.framework.TestCase;
import abc.notation.BarLine;
import abc.notation.Tune;
import abc.notation.Note;
import abc.parser.TuneBook;

public class TuneBookTest extends TestCase {
	
	public TuneBookTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1(){
		File f = new File("D:/Perso/abc/testPlan.abc");
		//AbcHeadersParser hp = new AbcHeadersParser();
		try {
			/*hp.addListener(new AbcFileParserAdapter(){
				
				public void lineProcessed(String line) {
					System.out.println(line);
				}
			});*/
			//hp.parseFile(f);
			TuneBook tb = new TuneBook(f);
			Tune t = tb.getTune(0);
			//Checks the empty score that contains only the time signature and the key
			assertEquals(2, t.getScore().size());
			assertNotNull(tb.getTuneHeader(0));
			//Checks the 2nd tune : simple scale exercise.
			t = tb.getTune(1);
			System.out.println(t.getScore());
			assertEquals(Note.C, ((Note)t.getScore().elementAt(2)).getHeight());
			assertEquals(Note.D, ((Note)t.getScore().elementAt(3)).getHeight());
			assertEquals(Note.E, ((Note)t.getScore().elementAt(4)).getHeight());
			assertEquals(Note.F, ((Note)t.getScore().elementAt(5)).getHeight());
			assertEquals(BarLine.SIMPLE, ((BarLine)t.getScore().elementAt(6)).getType());
			assertEquals(Note.G, ((Note)t.getScore().elementAt(7)).getHeight());
			assertEquals(Note.A, ((Note)t.getScore().elementAt(8)).getHeight());
			assertEquals(Note.B, ((Note)t.getScore().elementAt(9)).getHeight());
			assertEquals(Note.C, ((Note)t.getScore().elementAt(10)).getHeight());
			assertEquals(BarLine.SIMPLE, ((BarLine)t.getScore().elementAt(6)).getType());
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

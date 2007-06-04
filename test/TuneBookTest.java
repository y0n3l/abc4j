import java.io.File;

import junit.framework.TestCase;
import abc.notation.BarLine;
import abc.notation.Tune;
import abc.notation.Note;
import abc.parser.PositionableNote;
import abc.parser.TuneBook;

public class TuneBookTest extends TestCase {
	
	public TuneBookTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1(){
		File f = new File("./testPlan.abc");
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
			//Checks if in-file tune header are properly parsed.
			assertNotNull(tb.getTuneHeader(0));

			//Checks the 2nd tune : simple scale exercise.
			t = tb.getTune(1);
			//Checks if in-file tune header are properly parsed.
			assertNotNull(tb.getTuneHeader(0));
			PositionableNote n = null;
			System.out.println(t.getScore());
			n = (PositionableNote)t.getScore().elementAt(2);
			assertEquals(Note.C, n.getHeight());
			assertEquals(1, n.getPosition().getColumn());
			assertEquals(5, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getScore().elementAt(3);
			assertEquals(Note.D, n.getHeight());
			assertEquals(3, n.getPosition().getColumn());
			assertEquals(5, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getScore().elementAt(4);
			assertEquals(Note.E, n.getHeight());
			assertEquals(5, n.getPosition().getColumn());
			assertEquals(5, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getScore().elementAt(5);
			assertEquals(Note.F, n.getHeight());
			assertEquals(7, n.getPosition().getColumn());
			assertEquals(5, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			assertEquals(BarLine.SIMPLE, ((BarLine)t.getScore().elementAt(6)).getType());
			
			
			n = (PositionableNote)t.getScore().elementAt(7);
			assertEquals(Note.G, n.getHeight());
			assertEquals(1, n.getPosition().getColumn());
			assertEquals(6, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getScore().elementAt(8);
			assertEquals(Note.A, n.getHeight());
			assertEquals(3, n.getPosition().getColumn());
			assertEquals(6, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getScore().elementAt(9);
			assertEquals(Note.B, n.getHeight());
			assertEquals(5, n.getPosition().getColumn());
			assertEquals(6, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getScore().elementAt(10);
			assertEquals(Note.C, n.getHeight());
			assertEquals(7, n.getPosition().getColumn());
			assertEquals(6, n.getPosition().getLine());
			assertEquals(1, n.getLength());
			
			
			assertEquals(BarLine.SIMPLE, ((BarLine)t.getScore().elementAt(11)).getType());
			
			
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

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
		File f = new File("../ressources/testPlan.abc");
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
			assertEquals(2, t.getMusic().getVoice(1).size());
			//Checks if in-file tune header are properly parsed.
			assertNotNull(tb.getTuneHeader(0));

			//Checks the 2nd tune : simple scale exercise.
			t = tb.getTune(1);
			//Checks if in-file tune header are properly parsed.
			assertNotNull(tb.getTuneHeader(1));
			PositionableNote n = null;
			System.out.println(t.getMusic());
			n = (PositionableNote)t.getMusic().getVoice(1).elementAt(2);
			assertEquals(Note.C, n.getStrictHeight());
			assertEquals(1, n.getPosition().getColumn());
			assertEquals(5, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getMusic().getVoice(1).elementAt(3);
			assertEquals(Note.D, n.getStrictHeight());
			assertEquals(3, n.getPosition().getColumn());
			assertEquals(5, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getMusic().getVoice(1).elementAt(4);
			assertEquals(Note.E, n.getStrictHeight());
			assertEquals(5, n.getPosition().getColumn());
			assertEquals(5, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getMusic().getVoice(1).elementAt(5);
			assertEquals(Note.F, n.getStrictHeight());
			assertEquals(7, n.getPosition().getColumn());
			assertEquals(5, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			assertEquals(BarLine.SIMPLE, ((BarLine)t.getMusic().getVoice(1).elementAt(6)).getType());
			
			
			n = (PositionableNote)t.getMusic().getVoice(1).elementAt(7);
			assertEquals(Note.G, n.getStrictHeight());
			assertEquals(1, n.getPosition().getColumn());
			assertEquals(6, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getMusic().getVoice(1).elementAt(8);
			assertEquals(Note.A, n.getStrictHeight());
			assertEquals(3, n.getPosition().getColumn());
			assertEquals(6, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getMusic().getVoice(1).elementAt(9);
			assertEquals(Note.B, n.getStrictHeight());
			assertEquals(5, n.getPosition().getColumn());
			assertEquals(6, n.getPosition().getLine());
			assertEquals(2, n.getLength());
			
			n = (PositionableNote)t.getMusic().getVoice(1).elementAt(10);
			assertEquals(Note.C, n.getStrictHeight());
			assertEquals(7, n.getPosition().getColumn());
			assertEquals(6, n.getPosition().getLine());
			assertEquals(1, n.getLength());
			
			
			assertEquals(BarLine.SIMPLE, ((BarLine)t.getMusic().getVoice(1).elementAt(11)).getType());
			
			
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

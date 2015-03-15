import java.io.File;

import junit.framework.TestCase;
import abc.notation.BarLine;
import abc.notation.Tune;
import abc.notation.Note;
//import abc.parser2.PositionableNote;
import abc.notation.TuneBook;
import abc.parser.TuneBookParser;

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
			TuneBook tb = new TuneBookParser().parse(f);
			Tune t = tb.getTune(0);
			//Checks the empty score that contains only the time signature and the key
			assertEquals(2, t.getMusic().getFirstVoice().size());
			//Checks if in-file tune header are properly parsed.
			assertNotNull(tb.getTune(0));

			//Checks the 2nd tune : simple scale exercise.
			t = tb.getTune(1);
			//Checks if in-file tune header are properly parsed.
			assertNotNull(tb.getTune(1));
			Note n = null;
			n = (Note)t.getMusic().getFirstVoice().elementAt(2);
			System.out.println(n.getCharStreamPosition());
			assertEquals(Note.C, n.getStrictHeight());
			assertEquals(1, n.getCharStreamPosition().getColumn());
			assertEquals(12, n.getCharStreamPosition().getLine());
			assertEquals(2, n.getCharStreamPosition().getLength());
			
			n = (Note)t.getMusic().getFirstVoice().elementAt(3);
			assertEquals(Note.D, n.getStrictHeight());
			assertEquals(3, n.getCharStreamPosition().getColumn());
			assertEquals(12, n.getCharStreamPosition().getLine());
			assertEquals(2, n.getCharStreamPosition().getLength());
			
			n = (Note)t.getMusic().getFirstVoice().elementAt(4);
			assertEquals(Note.E, n.getStrictHeight());
			assertEquals(5, n.getCharStreamPosition().getColumn());
			assertEquals(12, n.getCharStreamPosition().getLine());
			assertEquals(2, n.getCharStreamPosition().getLength());
			
			n = (Note)t.getMusic().getFirstVoice().elementAt(5);
			assertEquals(Note.F, n.getStrictHeight());
			assertEquals(7, n.getCharStreamPosition().getColumn());
			assertEquals(12, n.getCharStreamPosition().getLine());
			assertEquals(2, n.getCharStreamPosition().getLength());
			
			assertEquals(BarLine.SIMPLE, ((BarLine)t.getMusic().getFirstVoice().elementAt(6)).getType());
			
			
			n = (Note)t.getMusic().getFirstVoice().elementAt(7);
			assertEquals(Note.G, n.getStrictHeight());
			assertEquals(1, n.getCharStreamPosition().getColumn());
			assertEquals(13, n.getCharStreamPosition().getLine());
			assertEquals(2, n.getCharStreamPosition().getLength());
			
			n = (Note)t.getMusic().getFirstVoice().elementAt(8);
			assertEquals(Note.A, n.getStrictHeight());
			assertEquals(3, n.getCharStreamPosition().getColumn());
			assertEquals(13, n.getCharStreamPosition().getLine());
			assertEquals(2, n.getCharStreamPosition().getLength());
			
			n = (Note)t.getMusic().getFirstVoice().elementAt(9);
			assertEquals(Note.B, n.getStrictHeight());
			assertEquals(5, n.getCharStreamPosition().getColumn());
			assertEquals(13, n.getCharStreamPosition().getLine());
			assertEquals(2, n.getCharStreamPosition().getLength());
			
			n = (Note)t.getMusic().getFirstVoice().elementAt(10);
			assertEquals(Note.C, n.getStrictHeight());
			assertEquals(7, n.getCharStreamPosition().getColumn());
			assertEquals(13, n.getCharStreamPosition().getLine());
			assertEquals(1, n.getCharStreamPosition().getLength());
			
			
			assertEquals(BarLine.SIMPLE, ((BarLine)t.getMusic().getFirstVoice().elementAt(11)).getType());
			
			
		}
		catch (Exception e) {
			//e.printStackTrace();
		}
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

import java.io.File;
import java.io.StringReader;

import junit.framework.TestCase;
import abc.notation.AccidentalType;
import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.Tune;
import abc.parser.AbcHeadersParser;
import abc.parser.TuneBook;
import abc.parser.TuneParser;

public class KeySignatureTests extends TestCase {
	
	private static final String FILE_NAME = "../ressources/crash.abc";
	
	public KeySignatureTests(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
X:21
T:Test Gflat
K:Gb
G	 
	 */
	public void test1(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			KeySignature key = tb.getTune(21).getKey();
			// Gb {b, b, b, nat, b, b, b}
			assertEquals(key.getAccidentalFor(Note.C), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.D), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.E), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.F), AccidentalType.NATURAL );
			assertEquals(key.getAccidentalFor(Note.G), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.A), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.B), AccidentalType.FLAT );
			assertTrue(key.hasOnlyFlats());
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	/**
X:22
T:Test Fsharp
K:F#
F
	 */
	public void test2(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			KeySignature key = tb.getTune(22).getKey();
			// F# {#, #, #, #, #, #, nat},
			assertEquals(key.getAccidentalFor(Note.C), AccidentalType.SHARP);
			assertEquals(key.getAccidentalFor(Note.D), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.E), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.F), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.G), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.A), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.B), AccidentalType.NATURAL );
			assertTrue(key.hasOnlySharps());
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	/**
X:23
T:Test Cflat
K:Cb
C
	 */
	public void test3(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			KeySignature key = tb.getTune(23).getKey();
			// Cb {b, b, b, b, b, b, b}
			assertEquals(key.getAccidentalFor(Note.C), AccidentalType.FLAT);
			assertEquals(key.getAccidentalFor(Note.D), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.E), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.F), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.G), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.A), AccidentalType.FLAT );
			assertEquals(key.getAccidentalFor(Note.B), AccidentalType.FLAT );
			assertTrue(key.hasOnlyFlats());
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	/**
X:24
T:Test Csharp
K:C#
C
	 */
	public void test4(){
		try {
			TuneBook tb = new TuneBook(new File(FILE_NAME));
			KeySignature key = tb.getTune(24).getKey();
			// C# {#, #, #, #, #, #, #},
			assertEquals(key.getAccidentalFor(Note.C), AccidentalType.SHARP);
			assertEquals(key.getAccidentalFor(Note.D), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.E), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.F), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.G), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.A), AccidentalType.SHARP );
			assertEquals(key.getAccidentalFor(Note.B), AccidentalType.SHARP );
			assertTrue(key.hasOnlySharps());
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	public void test5(){
		KeySignature key = new KeySignature(Note.D, KeySignature.MAJOR);
		assertTrue(key.hasOnlySharps());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

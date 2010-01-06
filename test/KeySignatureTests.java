import java.io.File;

import junit.framework.TestCase;
import abc.notation.AccidentalType;
import abc.notation.KeySignature;
import abc.notation.Note;
import abc.parser.TuneBook;

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
			assertTrue(key.isFlatDominant());
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
			assertTrue(key.isSharpDominant());
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
			assertTrue(key.isFlatDominant());
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
			assertTrue(key.isSharpDominant());
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	public void test5(){
		KeySignature key = new KeySignature(Note.D, KeySignature.MAJOR);
		assertTrue(key.isSharpDominant());
	}
	
	public void test6getDegree() {
		KeySignature key = new KeySignature(Note.C, KeySignature.MAJOR);
		assertEquals(Note.C, key.getNote());
		assertEquals(Note.C, key.getDegree(1));
		assertEquals(Note.D, key.getDegree(2));
		assertEquals(Note.B, key.getDegree(7));
		
		//mode and accidental have no importance, only key note
		key = new KeySignature(Note.E, AccidentalType.FLAT, KeySignature.MIXOLYDIAN);
		assertEquals(Note.E, key.getNote());
		assertEquals(Note.E, key.getDegree(1));
		assertEquals(Note.F, key.getDegree(2));
		assertEquals(Note.G, key.getDegree(3));
		assertEquals(Note.A, key.getDegree(4));
		assertEquals(Note.B, key.getDegree(5));
		assertEquals(Note.C, key.getDegree(6));
		assertEquals(Note.D, key.getDegree(7));
	}
	
	public void test7trnspose() {
		KeySignature CMaj = new KeySignature(Note.C, KeySignature.MAJOR);
		KeySignature EMaj = KeySignature.transpose(CMaj, 4);
		assertEquals(EMaj.getNote(), Note.E);
		assertEquals(EMaj.getAccidental(), AccidentalType.NATURAL);
		assertEquals(EMaj.getAccidentalFor(Note.E), AccidentalType.NATURAL);
		assertEquals(EMaj.getAccidentalFor(Note.F), AccidentalType.SHARP);
		assertEquals(EMaj.getAccidentalFor(Note.G), AccidentalType.SHARP);
		assertEquals(EMaj.getAccidentalFor(Note.A), AccidentalType.NATURAL);
		assertEquals(EMaj.getAccidentalFor(Note.B), AccidentalType.NATURAL);
		assertEquals(EMaj.getAccidentalFor(Note.C), AccidentalType.SHARP);
		assertEquals(EMaj.getAccidentalFor(Note.D), AccidentalType.SHARP);
		//Eb
		KeySignature EbMaj = KeySignature.transpose(CMaj, 3);
		assertEquals(EbMaj.getNote(), Note.E);
		assertEquals(EbMaj.getAccidental(), AccidentalType.FLAT);
		assertEquals(EbMaj.getAccidentalFor(Note.E), AccidentalType.FLAT);
		assertEquals(EbMaj.getAccidentalFor(Note.F), AccidentalType.NATURAL);
		assertEquals(EbMaj.getAccidentalFor(Note.G), AccidentalType.NATURAL);
		assertEquals(EbMaj.getAccidentalFor(Note.A), AccidentalType.FLAT);
		assertEquals(EbMaj.getAccidentalFor(Note.B), AccidentalType.FLAT);
		assertEquals(EbMaj.getAccidentalFor(Note.C), AccidentalType.NATURAL);
		assertEquals(EbMaj.getAccidentalFor(Note.D), AccidentalType.NATURAL);
		//F#Maj
		KeySignature FsMaj = KeySignature.transpose(EbMaj, -9);
		assertEquals(FsMaj.getNote(), Note.F);
		assertEquals(FsMaj.getAccidental(), AccidentalType.SHARP);
		
		//C _e ^f _a (arabic scale nawa atar)
		KeySignature CnawaAtar = new KeySignature(Note.C, AccidentalType.NATURAL, KeySignature.MAJOR);
		CnawaAtar.setAccidental(Note.E, AccidentalType.FLAT);
		CnawaAtar.setAccidental(Note.F, AccidentalType.SHARP);
		CnawaAtar.setAccidental(Note.A, AccidentalType.FLAT);
		KeySignature DnawaAtar = KeySignature.transpose(CnawaAtar, 2);
		//C D Eb F# G Ab B (C minor harmonic with F#)
		//becomes
		//D E F G# A Bb C# (D minor harmonic with G#)
		assertEquals(DnawaAtar.getNote(), Note.D);
		assertEquals(DnawaAtar.getAccidental(), AccidentalType.NATURAL);
		assertEquals(DnawaAtar.getMode(), KeySignature.MAJOR);
		assertEquals(DnawaAtar.getAccidentalFor(Note.D), AccidentalType.NATURAL);
		assertEquals(DnawaAtar.getAccidentalFor(Note.E), AccidentalType.NATURAL);
		assertEquals(DnawaAtar.getAccidentalFor(Note.F), AccidentalType.NATURAL);
		assertEquals(DnawaAtar.getAccidentalFor(Note.G), AccidentalType.SHARP);
		assertEquals(DnawaAtar.getAccidentalFor(Note.A), AccidentalType.NATURAL);
		assertEquals(DnawaAtar.getAccidentalFor(Note.B), AccidentalType.FLAT);
		assertEquals(DnawaAtar.getAccidentalFor(Note.C), AccidentalType.SHARP);
		//C D Eb F# G Ab B (C minor harmonic with F#)
		//D E F G# A Bb C# (D minor harmonic with G#)
		//becomes
		//Bb C Db E F Gb A (Bb minor harmonic with E=)
		KeySignature BbnawaAtar = KeySignature.transpose(DnawaAtar, -4);
		assertEquals(BbnawaAtar.getNote(), Note.B);
		assertEquals(BbnawaAtar.getAccidental(), AccidentalType.FLAT);
		assertEquals(BbnawaAtar.getAccidentalFor(Note.B), AccidentalType.FLAT);
		assertEquals(BbnawaAtar.getAccidentalFor(Note.C), AccidentalType.NATURAL);
		assertEquals(BbnawaAtar.getAccidentalFor(Note.D), AccidentalType.FLAT);
		assertEquals(BbnawaAtar.getAccidentalFor(Note.E), AccidentalType.NATURAL);
		assertEquals(BbnawaAtar.getAccidentalFor(Note.F), AccidentalType.NATURAL);
		assertEquals(BbnawaAtar.getAccidentalFor(Note.G), AccidentalType.FLAT);
		assertEquals(BbnawaAtar.getAccidentalFor(Note.A), AccidentalType.NATURAL);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

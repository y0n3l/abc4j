import java.io.File;

import junit.framework.TestCase;
import abc.notation.Accidental;
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
			assertTrue(key.getAccidentalFor(Note.C).isFlat());
			assertTrue(key.getAccidentalFor(Note.D).isFlat());
			assertTrue(key.getAccidentalFor(Note.E).isFlat());
			assertTrue(key.getAccidentalFor(Note.F).isNatural());
			assertTrue(key.getAccidentalFor(Note.G).isFlat());
			assertTrue(key.getAccidentalFor(Note.A).isFlat());
			assertTrue(key.getAccidentalFor(Note.B).isFlat());
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
			assertTrue(key.getAccidentalFor(Note.C).isSharp());
			assertTrue(key.getAccidentalFor(Note.D).isSharp());
			assertTrue(key.getAccidentalFor(Note.E).isSharp());
			assertTrue(key.getAccidentalFor(Note.F).isSharp());
			assertTrue(key.getAccidentalFor(Note.G).isSharp());
			assertTrue(key.getAccidentalFor(Note.A).isSharp());
			assertTrue(key.getAccidentalFor(Note.B).isNatural());
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
			assertTrue(key.getAccidentalFor(Note.C).isFlat());
			assertTrue(key.getAccidentalFor(Note.D).isFlat());
			assertTrue(key.getAccidentalFor(Note.E).isFlat());
			assertTrue(key.getAccidentalFor(Note.F).isFlat());
			assertTrue(key.getAccidentalFor(Note.G).isFlat());
			assertTrue(key.getAccidentalFor(Note.A).isFlat());
			assertTrue(key.getAccidentalFor(Note.B).isFlat());
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
			assertTrue(key.getAccidentalFor(Note.C).isSharp());
			assertTrue(key.getAccidentalFor(Note.D).isSharp());
			assertTrue(key.getAccidentalFor(Note.E).isSharp());
			assertTrue(key.getAccidentalFor(Note.F).isSharp());
			assertTrue(key.getAccidentalFor(Note.G).isSharp());
			assertTrue(key.getAccidentalFor(Note.A).isSharp());
			assertTrue(key.getAccidentalFor(Note.B).isSharp());
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
		key = new KeySignature(Note.E, Accidental.FLAT, KeySignature.MIXOLYDIAN);
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
		assertTrue(EMaj.getAccidental().isNatural());
		assertTrue(EMaj.getAccidentalFor(Note.E).isNatural());
		assertTrue(EMaj.getAccidentalFor(Note.F).isSharp());
		assertTrue(EMaj.getAccidentalFor(Note.G).isSharp());
		assertTrue(EMaj.getAccidentalFor(Note.A).isNatural());
		assertTrue(EMaj.getAccidentalFor(Note.B).isNatural());
		assertTrue(EMaj.getAccidentalFor(Note.C).isSharp());
		assertTrue(EMaj.getAccidentalFor(Note.D).isSharp());
		//Eb
		KeySignature EbMaj = KeySignature.transpose(CMaj, 3);
		assertEquals(EbMaj.getNote(), Note.E);
		assertTrue(EbMaj.getAccidental().isFlat());
		assertTrue(EbMaj.getAccidentalFor(Note.E).isFlat());
		assertTrue(EbMaj.getAccidentalFor(Note.F).isNatural());
		assertTrue(EbMaj.getAccidentalFor(Note.G).isNatural());
		assertTrue(EbMaj.getAccidentalFor(Note.A).isFlat());
		assertTrue(EbMaj.getAccidentalFor(Note.B).isFlat());
		assertTrue(EbMaj.getAccidentalFor(Note.C).isNatural());
		assertTrue(EbMaj.getAccidentalFor(Note.D).isNatural());
		//F#Maj
		KeySignature FsMaj = KeySignature.transpose(EbMaj, -9);
		assertEquals(FsMaj.getNote(), Note.F);
		assertTrue(FsMaj.getAccidental().isSharp());
		
		//C _e ^f _a (arabic scale nawa atar)
		KeySignature CnawaAtar = new KeySignature(Note.C, Accidental.NATURAL, KeySignature.MAJOR);
		CnawaAtar.setAccidental(Note.E, Accidental.FLAT);
		CnawaAtar.setAccidental(Note.F, Accidental.SHARP);
		CnawaAtar.setAccidental(Note.A, Accidental.FLAT);
		KeySignature DnawaAtar = KeySignature.transpose(CnawaAtar, 2);
		//C D Eb F# G Ab B (C minor harmonic with F#)
		//becomes
		//D E F G# A Bb C# (D minor harmonic with G#)
		assertEquals(DnawaAtar.getNote(), Note.D);
		assertTrue(DnawaAtar.getAccidental().isNatural());
		assertEquals(DnawaAtar.getMode(), KeySignature.MAJOR);
		assertTrue(DnawaAtar.getAccidentalFor(Note.D).isNatural());
		assertTrue(DnawaAtar.getAccidentalFor(Note.E).isNatural());
		assertTrue(DnawaAtar.getAccidentalFor(Note.F).isNatural());
		assertTrue(DnawaAtar.getAccidentalFor(Note.G).isSharp());
		assertTrue(DnawaAtar.getAccidentalFor(Note.A).isNatural());
		assertTrue(DnawaAtar.getAccidentalFor(Note.B).isFlat());
		assertTrue(DnawaAtar.getAccidentalFor(Note.C).isSharp());
		//C D Eb F# G Ab B (C minor harmonic with F#)
		//D E F G# A Bb C# (D minor harmonic with G#)
		//becomes
		//Bb C Db E F Gb A (Bb minor harmonic with E=)
		KeySignature BbnawaAtar = KeySignature.transpose(DnawaAtar, -4);
		assertEquals(BbnawaAtar.getNote(), Note.B);
		assertTrue(BbnawaAtar.getAccidental().isFlat());
		assertTrue(BbnawaAtar.getAccidentalFor(Note.B).isFlat());
		assertTrue(BbnawaAtar.getAccidentalFor(Note.C).isNatural());
		assertTrue(BbnawaAtar.getAccidentalFor(Note.D).isFlat());
		assertTrue(BbnawaAtar.getAccidentalFor(Note.E).isNatural());
		assertTrue(BbnawaAtar.getAccidentalFor(Note.F).isNatural());
		assertTrue(BbnawaAtar.getAccidentalFor(Note.G).isFlat());
		assertTrue(BbnawaAtar.getAccidentalFor(Note.A).isNatural());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

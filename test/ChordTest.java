import abc.notation.AccidentalType;
import abc.notation.Chord;
import abc.notation.Note;
import junit.framework.TestCase;

public class ChordTest extends TestCase {
	
	public ChordTest(String name) {
		super(name);
	}
	
	public void test1isChord() {
		Chord chord = new Chord("C");
		assertTrue("C", chord.isChord());
		assertEquals(Note.C, chord.getNote().getHeight());
		assertEquals(AccidentalType.NONE, chord.getNote().getAccidental());
		assertNull(chord.getBass());
		assertEquals("", chord.getQuality());
		assertFalse(chord.isOptional());
		
		chord = new Chord("C#");
		assertTrue("C#", chord.isChord());
		assertEquals(AccidentalType.SHARP, chord.getNote().getAccidental());
		
		chord = new Chord("C##");
		assertFalse("C##", chord.isChord());
		assertNull(chord.getNote());
		assertNull(chord.getBass());
		assertEquals("C##", chord.getText());
		
		chord = new Chord("Cm");
		assertTrue("Cm", chord.isChord());
		assertEquals("m", chord.getQuality());
		//lowercase is not understood as minor...
		assertFalse("c", Chord.isChord("c"));
		assertTrue("Cmin", Chord.isChord("Cmin"));
		assertTrue("Cm6", Chord.isChord("Cm6"));
		assertTrue("Cmin6", Chord.isChord("Cmin6"));
		assertFalse("CMIN", Chord.isChord("CMIN"));
		assertTrue("CMaj", Chord.isChord("CMaj"));
		assertTrue("C7M", Chord.isChord("C7M"));
		assertTrue("C7M", Chord.isChord("C7M"));
		//parenthesis and spaces
		chord = new Chord("(E7)");
		assertTrue("(E7)", chord.isChord());
		assertTrue(chord.isOptional());
		assertEquals("7", chord.getQuality());
		chord = new Chord(" (E7) ");
		assertTrue(" (E7) ", chord.isChord());
		assertTrue(chord.isOptional());
		assertNull(chord.getBass());
		assertFalse(" ( E7 ) ", Chord.isChord(" ( E7 ) "));
		//bass
		chord = new Chord("Ab/C");
		assertTrue("Ab/C", chord.isChord());
		assertEquals(Note.A, chord.getNote().getHeight());
		assertEquals(AccidentalType.FLAT, chord.getNote().getAccidental());
		assertEquals(Note.C, chord.getBass().getHeight());
		assertEquals(AccidentalType.NONE, chord.getBass().getAccidental());
		assertFalse(chord.isOptional());
		chord = new Chord("/Eb");
		assertTrue("/Eb", chord.isChord());
		assertNull(chord.getNote());
		assertEquals(Note.E, chord.getBass().getHeight());
		
		//other chars
		chord = new Chord("EØ");
		assertTrue("EØ", chord.isChord());
		assertEquals("Ø", chord.getQuality());
		chord = new Chord("%");
		assertFalse("%", chord.isChord());
		chord = new Chord("Bbo");
		assertTrue("Bbo", chord.isChord());
		assertEquals("o", chord.getQuality());
		chord = new Chord("C79-");
		assertTrue("C79-", chord.isChord());
		assertEquals("79-", chord.getQuality());
		//accidentals in unicode
		String flat = "\u266D";
		chord = new Chord("E"+flat);
		assertTrue("E"+flat, chord.isChord());
		assertEquals(AccidentalType.FLAT, chord.getNote().getAccidental());
		String sharp = "\u266F";
		chord = new Chord("F"+sharp);
		assertTrue("F"+sharp, chord.isChord());
		assertEquals(AccidentalType.SHARP, chord.getNote().getAccidental());
		//We never write natural in the chord name
		String natural = "\u266E";
		assertFalse("B"+natural, Chord.isChord("B"+natural));
		
		//complete
		chord = new Chord(" (EbMaj7/Bb) ");
		assertTrue("(EbMaj7/Bb)", chord.isChord());
		assertEquals(Note.E, chord.getNote().getHeight());
		assertEquals(AccidentalType.FLAT, chord.getNote().getAccidental());
		assertTrue(chord.isOptional());
		assertEquals("Maj7", chord.getQuality());
		assertEquals(Note.B, chord.getBass().getHeight());
		assertEquals(AccidentalType.FLAT, chord.getBass().getAccidental());
		assertEquals("(E"+flat+"Maj7/B"+flat+")", chord.getText(true));
	}
	
}

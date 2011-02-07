import junit.framework.TestCase;
import abc.notation.Accidental;
import abc.notation.EndOfStaffLine;
import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.PartLabel;
import abc.notation.Tune;
import abc.notation.Voice;
import abc.parser.TuneParser;

public class BeforeAudioRenditionTest extends TestCase {
	
	public BeforeAudioRenditionTest(String name) {
		super(name);
	}
	
	/** */
	public void test1correctPartsKeys() {
		String tuneAsString = "X:1\n" +
				"T:test parts keys\n" +
				"P:ABCBAC" +
				"K:A\n" +
				"P:A\nA\n" +
				"K:Ab\n_A" +
				"P:B\nK:B\nB\n" +
				"P:C\nc\nK:C\nC\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Voice voice = tune.getMusicForAudioRendition().getFirstVoice();
		//for (int i = 0, j = voice.size(); i < j; i++) {
		//	System.out.println(i+" : "+voice.elementAt(i).getClass()+" "+voice.elementAt(i));
		//}
		//should be expanded as
		//K:A, P:A, A, K:Ab, _A
		//P:B, K:B, B
		//P:C, C', K:C, C
		//return to B which is in K:B
		//P:B, K:B, B
		//return to A...
		//P:A, K:A, A, K:Ab, _A
		//go to C... c is starting with B key (from part B)
		//K:B, P:C, C', K:C, C
//		0 : class abc.notation.KeySignature KeySignature: Amaj (#, =, =, #, #, =, =}
		assertTrue(voice.elementAt(0) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(0)).equals(new KeySignature(Note.A, KeySignature.MAJOR)));
//		1 : class abc.notation.PartLabel abc.notation.PartLabel@aa9835
		assertTrue(voice.elementAt(1) instanceof PartLabel
				&& ((PartLabel)voice.elementAt(1)).getLabel().equals("A"));
//		2 : class abc.parser.PositionableNote A...
		assertTrue(voice.elementAt(2) instanceof Note);
//		3 : class abc.notation.EndOfStaffLine abc.notation.EndOfStaffLine@1eec612
		assertTrue(voice.elementAt(3) instanceof EndOfStaffLine);
//		4 : class abc.notation.KeySignature KeySignature: Abmaj (=, b, b, =, =, b, b}
		assertTrue(voice.elementAt(4) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(4)).equals(new KeySignature(Note.A, Accidental.FLAT, KeySignature.MAJOR)));
//		5 : class abc.parser.PositionableNote Ab...
		assertTrue(voice.elementAt(5) instanceof Note);
//		6 : class abc.notation.PartLabel abc.notation.PartLabel@10dd1f7
		assertTrue(voice.elementAt(6) instanceof PartLabel
				&& ((PartLabel)voice.elementAt(6)).getLabel().equals("B"));
//		7 : class abc.notation.KeySignature KeySignature: Bmaj (#, #, =, #, #, #, =}
		assertTrue(voice.elementAt(7) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(7)).equals(new KeySignature(Note.B, KeySignature.MAJOR)));
//		8 : class abc.parser.PositionableNote B...
		assertTrue(voice.elementAt(8) instanceof Note);
//		9 : class abc.notation.EndOfStaffLine abc.notation.EndOfStaffLine@53c015
		assertTrue(voice.elementAt(9) instanceof EndOfStaffLine);
//		10 : class abc.notation.PartLabel abc.notation.PartLabel@67ac19
		assertTrue(voice.elementAt(10) instanceof PartLabel
				&& ((PartLabel)voice.elementAt(10)).getLabel().equals("C"));
//		11 : class abc.parser.PositionableNote C'...
		assertTrue(voice.elementAt(11) instanceof Note);
//		12 : class abc.notation.EndOfStaffLine abc.notation.EndOfStaffLine@53ba3d
		assertTrue(voice.elementAt(12) instanceof EndOfStaffLine);
//		13 : class abc.notation.KeySignature KeySignature: Cmaj (=, =, =, =, =, =, =}
		assertTrue(voice.elementAt(13) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(13)).equals(new KeySignature(Note.C, KeySignature.MAJOR)));
//		14 : class abc.parser.PositionableNote C...
		assertTrue(voice.elementAt(14) instanceof Note);
//		15 : class abc.notation.EndOfStaffLine abc.notation.EndOfStaffLine@53ba3d
		assertTrue(voice.elementAt(15) instanceof EndOfStaffLine);
//		16 : class abc.notation.PartLabel abc.notation.PartLabel@e80a59
		assertTrue(voice.elementAt(16) instanceof PartLabel
				&& ((PartLabel)voice.elementAt(16)).getLabel().equals("B"));
//		17 : class abc.notation.KeySignature KeySignature: Bmaj (#, #, =, #, #, #, =}
		assertTrue(voice.elementAt(17) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(17)).equals(new KeySignature(Note.B, KeySignature.MAJOR)));
//		18 : class abc.parser.PositionableNote B....
		assertTrue(voice.elementAt(18) instanceof Note);
//		19 : class abc.notation.EndOfStaffLine abc.notation.EndOfStaffLine@53c015
		assertTrue(voice.elementAt(19) instanceof EndOfStaffLine);
//		20 : class abc.notation.PartLabel abc.notation.PartLabel@1ff5ea7
		assertTrue(voice.elementAt(20) instanceof PartLabel
				&& ((PartLabel)voice.elementAt(20)).getLabel().equals("A"));
//		21 : class abc.notation.KeySignature KeySignature: Amaj (#, =, =, #, #, =, =}
		assertTrue(voice.elementAt(21) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(21)).equals(new KeySignature(Note.A, KeySignature.MAJOR)));
//		22 : class abc.parser.PositionableNote A...
		assertTrue(voice.elementAt(22) instanceof Note);
//		23 : class abc.notation.EndOfStaffLine abc.notation.EndOfStaffLine@1eec612
		assertTrue(voice.elementAt(23) instanceof EndOfStaffLine);
//		24 : class abc.notation.KeySignature KeySignature: Abmaj (=, b, b, =, =, b, b}
		assertTrue(voice.elementAt(24) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(24)).equals(new KeySignature(Note.A, Accidental.FLAT, KeySignature.MAJOR)));
//		25 : class abc.parser.PositionableNote Ab...
		assertTrue(voice.elementAt(25) instanceof Note);
//		26 : class abc.notation.PartLabel abc.notation.PartLabel@1ff5ea7
		assertTrue(voice.elementAt(26) instanceof PartLabel
				&& ((PartLabel)voice.elementAt(26)).getLabel().equals("C"));
//		27 : class abc.notation.KeySignature KeySignature: Bmaj (#, #, =, #, #, #, =}
		assertTrue(voice.elementAt(27) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(27)).equals(new KeySignature(Note.B, KeySignature.MAJOR)));
//		28 : class abc.parser.PositionableNote C'...
		assertTrue(voice.elementAt(28) instanceof Note);
//		29 : class abc.notation.EndOfStaffLine abc.notation.EndOfStaffLine@53ba3d
		assertTrue(voice.elementAt(29) instanceof EndOfStaffLine);
//		30 : class abc.notation.KeySignature KeySignature: Cmaj (=, =, =, =, =, =, =}
		assertTrue(voice.elementAt(30) instanceof KeySignature
				&& ((KeySignature)voice.elementAt(30)).equals(new KeySignature(Note.C, KeySignature.MAJOR)));
//		31 : class abc.parser.PositionableNote C...
		assertTrue(voice.elementAt(31) instanceof Note);
//		32 : class abc.notation.EndOfStaffLine abc.notation.EndOfStaffLine@53ba3d
		assertTrue(voice.elementAt(32) instanceof EndOfStaffLine);

	}
	
}

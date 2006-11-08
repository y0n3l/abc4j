import junit.framework.TestCase;
import abc.notation.Note;
import abc.notation.Tune;
import abc.parser.AbcHeadersParser;
import abc.parser.TuneParser;

public class VariousTests extends TestCase {
	
	private static final String TUNE_TITLE = "test";
	private static final String line01 = "X:1\n";
	private static final String line02 = "T:" + TUNE_TITLE + "\n";
	private static final String line03 = "L:1/8\n"; //default length is the "crotchet"
	private static final String line04 = "K:D\n";
	private static final String line05 = "d>A FA/d/\n";
	
	public VariousTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/**
	 * L:1/8
	 * d>A FA/d/
	 * 
I get the durations (with the last constants update): 36 12 12 6 6
(DOTTED_EIGTH, SIXTEENTH, SIXTEENTH, THIRTY_SECOND, THIRTY_SECOND)
I have used some other abc software, where I get DOTTED_EIGTH,
SIXTEENTH, EIGTH, SIXTEENTH, SIXTEENTH.

	 *
	 */
	public void test1(){
		String abcTune = line01.concat(line02).concat(line03).concat(line04).concat(line05);
		TuneParser tparser = new TuneParser();
		Tune tuneResult = tparser.parse(abcTune);
		Note firstNote = (Note)tuneResult.getScore().elementAt(1);
		assertEquals(Note.EIGHTH, firstNote.getStrictDuration());
		assertEquals(Note.DOTTED_EIGHTH, firstNote.getDuration());
		assertEquals(1, firstNote.countDots());
		
		Note secondNote = (Note)tuneResult.getScore().elementAt(2);
		assertEquals(Note.SIXTEENTH, secondNote.getStrictDuration());
		assertEquals(0, secondNote.countDots());
		
		Note thirdNote = (Note)tuneResult.getScore().elementAt(3);
		assertEquals(0, thirdNote.countDots());
		assertEquals(Note.EIGHTH, thirdNote.getStrictDuration());
		assertEquals(Note.EIGHTH, thirdNote.getDuration());
		
		Note fourthNote = (Note)tuneResult.getScore().elementAt(4);
		assertEquals(0, fourthNote.countDots());
		assertEquals(Note.SIXTEENTH, fourthNote.getStrictDuration());
		assertEquals(Note.SIXTEENTH, fourthNote.getDuration());
		
		Note fifthNote = (Note)tuneResult.getScore().elementAt(5);
		assertEquals(0, fifthNote.countDots());
		assertEquals(Note.SIXTEENTH, fifthNote.getStrictDuration());
		assertEquals(Note.SIXTEENTH, fifthNote.getDuration());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

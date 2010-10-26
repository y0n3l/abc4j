import junit.framework.TestCase;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.Tune;
import abc.parser.TuneParser;

public class RhythmsTest extends TestCase {
	private static final String TUNE_TITLE = "test";
	private static final String line01 = "X:1\n";
	private static final String line02 = "T:" + TUNE_TITLE + "\n";
	private static final String line03 = "L:1/8\n"; //default length is the "crotchet"
	private static final String line04 = "K:D\n";
	private static final String line05 = "c>c\n";
	private static final String line051 = "c>>c\n";
	private static final String line052 = "c2>c2\n";
	private static final String line053 = "c2>>c2\n";

	public RhythmsTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/** Checks c>c */
	public void test1(){
		String abcTune = line01.concat(line02).concat(line03).concat(line04).concat(line05);
		TuneParser myParser = new TuneParser();
		Tune tuneResult = myParser.parse(abcTune);
		assertEquals(TUNE_TITLE, tuneResult.getTitles()[0]);
		Note firstNote = (Note)tuneResult.getMusic().elementAt(1);
		assertEquals(Note.EIGHTH, firstNote.getStrictDuration());
		assertEquals(1, firstNote.countDots());
		Note secondNote = (Note)tuneResult.getMusic().elementAt(2);
		assertEquals(Note.SIXTEENTH, secondNote.getStrictDuration());
		assertEquals(0, secondNote.countDots());
	}
	
	/** Checks c>>c */
	public void test2(){
		String abcTune = line01.concat(line02).concat(line03).concat(line04).concat(line051);
		TuneParser myParser = new TuneParser();
		Tune tuneResult = myParser.parse(abcTune);
		assertEquals(TUNE_TITLE, tuneResult.getTitles()[0]);
		Note firstNote = (Note)tuneResult.getMusic().elementAt(1);
		assertEquals(Note.EIGHTH, firstNote.getStrictDuration());
		assertEquals(2, firstNote.countDots());
		Note secondNote = (Note)tuneResult.getMusic().elementAt(2);
		assertEquals(Note.THIRTY_SECOND, secondNote.getStrictDuration());
		assertEquals(0, secondNote.countDots());
	}
	
	/** checks c2>c2 */
	public void test3(){
		String abcTune = line01.concat(line02).concat(line03).concat(line04).concat(line052);
		TuneParser myParser = new TuneParser();
		Tune tuneResult = myParser.parse(abcTune);
		assertEquals(TUNE_TITLE, tuneResult.getTitles()[0]);
		Note firstNote = (Note)tuneResult.getMusic().elementAt(1);
		assertEquals(Note.QUARTER, firstNote.getStrictDuration());
		assertEquals(1, firstNote.countDots());
		Note secondNote = (Note)tuneResult.getMusic().elementAt(2);
		assertEquals(Note.EIGHTH, secondNote.getStrictDuration());
		assertEquals(0, secondNote.countDots());
	}
	
	/** checks c2>>c2 */
	public void test4(){
		String abcTune = line01.concat(line02).concat(line03).concat(line04).concat(line053);
		TuneParser myParser = new TuneParser();
		Tune tuneResult = myParser.parse(abcTune);
		assertEquals(TUNE_TITLE, tuneResult.getTitles()[0]);
		Note firstNote = (Note)tuneResult.getMusic().elementAt(1);
		assertEquals(Note.QUARTER, firstNote.getStrictDuration());
		assertEquals(2, firstNote.countDots());
		Note secondNote = (Note)tuneResult.getMusic().elementAt(2);
		assertEquals(Note.SIXTEENTH, secondNote.getStrictDuration());
		assertEquals(0, secondNote.countDots());
	}
	
	public void test5(){
		String tuneAsString = "X:1\nT:test\nM:2/4\nL:1/8\nK:C\na\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getMusic().elementAt(2);
		assertEquals(firstNote.getStrictDuration(), Note.EIGHTH);
	}
	
	public void test6(){
		String tuneAsString = "X:1\nT:test\nM:2/4\nL:1/8\nK:C\n[A2a]\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		MultiNote firstNote = (MultiNote)tune.getMusic().elementAt(2);
		assertEquals(firstNote.getStrictDurations()[0], Note.EIGHTH);
		assertEquals(firstNote.getStrictDurations()[1], Note.QUARTER);
		assertEquals(firstNote.getShortestNote().getOctaveTransposition(), 1);
		assertEquals(firstNote.getLongestNote().getOctaveTransposition(), 0);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

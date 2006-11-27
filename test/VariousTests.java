import java.io.StringReader;

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
	private static final String line051 = "C2->Aa\n";
	private static final String line052 = "abcd\n";
	private static final String line06 = "efg\n";
	private static final String line07 = "edcba\n";
	
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
	
	/**
	 * X:0
	 * T:test
	 * L:1/8
	 * K:D
	 * C2->A a
	 * Not really recommanded because 
	 * Warning: This dotted-note notation is only defined between two 
	 * notes of equal length. If you use it between notes with different 
	 * lengths, the result is not defined, and ABC programs may misinterpret your intent.
	 */
	public void test2(){
		String abcTune = line01.concat(line02).concat(line03).concat(line04).concat(line051);
		TuneParser tparser = new TuneParser();
		Tune tuneResult = tparser.parse(abcTune);
		Note firstNote = (Note)tuneResult.getScore().elementAt(1);
		assertEquals(Note.QUARTER, firstNote.getStrictDuration());
		assertEquals(Note.DOTTED_QUARTER, firstNote.getDuration());
		assertEquals(1, firstNote.countDots());
		assertEquals(true, firstNote.isTied());
		
		Note secondNote = (Note)tuneResult.getScore().elementAt(2);
		assertEquals(Note.SIXTEENTH, secondNote.getStrictDuration());
		assertEquals(0, secondNote.countDots());
		assertEquals(true, firstNote.isTied());
		
		Note thirdNote = (Note)tuneResult.getScore().elementAt(3);
		assertEquals(Note.EIGHTH, thirdNote.getStrictDuration());
		assertEquals(0, thirdNote.countDots());
		assertEquals(false, thirdNote.isTied());
		
	}
	
	public void test3(){
		String abcTune = line01.concat(line02).concat(line03).concat(line04)
			.concat(line052).concat(line06).concat(line07);
		AbcHeadersParser tparser = new AbcHeadersParser();
		/*tparser.addListener(new AbcFileParserAdapter(){
			public void lineProcessed(String line) {
				System.out.println(line);
			}
			
			public void tuneBegin() {
				System.out.println("==begin");
			}
			public void tuneEnd(Tune t) {
				System.out.println("==end");
			}
		}
		);*/
		tparser.parseFile(new StringReader(abcTune));
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

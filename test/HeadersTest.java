import java.io.File;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;
import abc.notation.Tune;
import abc.parser.AbcFileParser;
import abc.parser.AbcHeadersParser;
import abc.parser.TuneParser;

public class HeadersTest extends TestCase {
	
	private static final String TUNE_TITLE = "test";
	private static final String line01 = "X:1\n";
	private static final String line02 = "T:" + TUNE_TITLE + "\n";
	private static final String line03 = "L:1/8\n"; //default length is the "crotchet"
	private static final String line04 = "K:D\n";
	private static final String line05 = "abcdef\n";
	private static final String line06 = "abcdef\n";
	private static final String line07 = "abcdef\n";
	private static final String line08 = "\n";
	
	
	public HeadersTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1(){
		String abcTune = line01.concat(line02).concat(line03).concat(line04).concat(line05)
		.concat(line06).concat(line07).concat(line08);
		TuneParser hparser = new TuneParser();
		Reader r = new StringReader(abcTune);
		Tune tune = hparser.parse(r);
		//System.out.println(tune.getScore());
		assertEquals(TUNE_TITLE, tune.getTitles()[0]);
	}
	
	public void test2(){
		File f = new File("D:/Perso/abc/oNeill4Test.abc");
		AbcHeadersParser hparser = new AbcHeadersParser();
		/*hparser.addListener(new AbcFileParserAdapter(){
			int tuneNb = 0;
			public void tuneEnd(Tune tune) {
				System.out.println((tuneNb++) + " " + tune.getTitles()[0]);
			}
		});*/
		long headersParsingTime = 0;
		try { 
			long start = System.currentTimeMillis();
			hparser.parseFile(f);
			long end = System.currentTimeMillis();
			headersParsingTime = end - start;
			System.out.println("Headers only : " + headersParsingTime); 
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		
		AbcFileParser fparser = new AbcFileParser();
		long fileParsingTime = 0;
		try { 
			long start = System.currentTimeMillis();
			/*fparser.addListener(new AbcFileParserAdapter(){
				int tuneNb = 0;
				public void tuneEnd(Tune tune) {
					System.out.println((tuneNb++) + " " + tune.getTitles()[0]);
				}
			});*/
			fparser.parseFile(f);
			long end = System.currentTimeMillis();
			fileParsingTime = end - start;
			System.out.println("Headers only : " + fileParsingTime);
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		System.out.println("Headers parsing is about " + fileParsingTime / headersParsingTime + " times faster");
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

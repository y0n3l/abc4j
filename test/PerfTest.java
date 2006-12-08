import java.io.File;

import junit.framework.TestCase;
import abc.parser.AbcFileParser;
import abc.parser.AbcHeadersParser;
import abc.parser.TuneBook;

public class PerfTest extends TestCase {
	
	private static final String ABC_FILE_REFERENCE = "D:/Perso/abc/OneillDos.abc";
	
	public PerfTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void headersVswholeFileTest(){
		File f = new File(ABC_FILE_REFERENCE);
		System.out.println("Reference file for the test is " + ABC_FILE_REFERENCE);
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
			System.out.println("There's an exception here " + e.getMessage());
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
	
	public void headersVsTunebookTest(){
		try {
			File f = new File(ABC_FILE_REFERENCE);
			AbcHeadersParser hparser = new AbcHeadersParser();
			long headersParsingTime = 0;
			long start = System.currentTimeMillis();
			hparser.parseFile(f);
			long end = System.currentTimeMillis();
			headersParsingTime = end - start;
			System.out.println("Headers only : " + headersParsingTime); 
		
			start = System.currentTimeMillis();
			TuneBook t = new TuneBook(f);
			end = System.currentTimeMillis();
			long tuneBookCreationTime = end - start;
			System.out.println("Tune Book creation time : "+ tuneBookCreationTime);
			System.out.println("TuneBook introduces an overhead of " 
					+ ((tuneBookCreationTime - headersParsingTime)*100) / headersParsingTime 
					+ "% compared to pure headers parsing");
			System.out.println("tunebook size " + t.size());
		}
		catch (Exception e ) {
			System.out.println("There's an exception here " + e.getMessage());
			e.printStackTrace();
		}

		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

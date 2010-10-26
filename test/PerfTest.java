import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.TestCase;
import abc.parser.AbcFileParser;
import abc.parser.AbcHeadersParser;
import abc.parser.TuneBook;

public class PerfTest extends TestCase {
	
	private static final String ABC_FILE_REFERENCE = "../ressources/OneillDos.abc";
	
	public PerfTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testHeadersVswholeFile(){
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
		//try { 
			long start = System.currentTimeMillis();
			try {
				hparser.parseFile(f);
				}
				catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			long end = System.currentTimeMillis();
			headersParsingTime = end - start;
			System.out.println("Headers only : " + headersParsingTime); 
		/*}
		catch (Exception e ) {
			System.out.println("There's an exception here " + e.getMessage());
			e.printStackTrace();
		}*/
		
		AbcFileParser fparser = new AbcFileParser();
		long fileParsingTime = 0;
		//try { 
			start = System.currentTimeMillis();
			/*fparser.addListener(new AbcFileParserAdapter(){
				int tuneNb = 0;
				public void tuneEnd(Tune tune) {
					System.out.println((tuneNb++) + " " + tune.getTitles()[0]);
				}
			});*/
			try {
			fparser.parseFile(f);
			}
			catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			end = System.currentTimeMillis();
			fileParsingTime = end - start;
			System.out.println("Headers only : " + fileParsingTime);
		/*}
		catch (Exception e ) {
			e.printStackTrace();
		}*/
		System.out.println("=====> Headers parsing is about " + fileParsingTime / headersParsingTime + " times faster");
		
	}
	
	public void testHeadersVsTunebook(){
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
			System.out.println("=====> TuneBook introduces an overhead of " 
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

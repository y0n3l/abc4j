import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;
import scanner.InvalidCharacterEvent;
import scanner.TokenEvent;
import abc.notation.Tune;
import abc.parser.AbcFileParserAdapter;
import abc.parser.InvalidTokenEvent;
import abc.parser.TuneParser;

public class CrashTest extends TestCase {
	
	private static final String FILE_NAME = "D:/Perso/abc/crash2.abc";
	
	public CrashTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	

	public void test1(){
		File f = new File(FILE_NAME);
		/*AbcHeadersParser hparser = new AbcHeadersParser();
		hparser.addListener(new AbcFileParserAdapter(){
			public void tuneEnd(Tune tune) {
				System.out.println(tune.getReferenceNumber() + " : " + tune.getTitles()[0] + "(" + tune.getKey() + ")");
			}
			
			public void validToken(TokenEvent event) {
				System.out.println("tok : " + event);
			}
		});
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
		}*/
		//==================================================================
		
		TuneParser fparser = new TuneParser();
		long fileParsingTime = 0;
		try { 
			long start = System.currentTimeMillis();
			fparser.addListener(new AbcFileParserAdapter(){
				public void tuneEnd(Tune tune) {
					System.out.println(tune.getReferenceNumber() + " : " + tune.getTitles()[0]);
				}
				public void validToken(TokenEvent event) {
					System.out.println("tok : " + event);
				}
				
				public void invalidCharacter(InvalidCharacterEvent evt) {
					System.out.println("invalid char : " + evt);
				}
				
				public void invalidToken(InvalidTokenEvent evt) {
					System.out.println("invalid token : " + evt);
				}
				
				 public void lineProcessed(String line) {
					 System.out.println("line processed : " + line);
				 }
			});
			fparser.parse(new BufferedReader(new FileReader(f)));
			long end = System.currentTimeMillis();
			fileParsingTime = end - start;
			System.out.println("Headers only : " + fileParsingTime);
		}
		catch (Exception e ) {
			e.printStackTrace();
		}
		//System.out.println("Headers parsing is about " + fileParsingTime / headersParsingTime + " times faster");
		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}


}

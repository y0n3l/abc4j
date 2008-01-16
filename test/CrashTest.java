import java.io.File;

import junit.framework.TestCase;
import scanner.InvalidCharacterEvent;
import scanner.TokenEvent;
import abc.notation.Tune;
import abc.parser.AbcFileParserAdapter;
import abc.parser.AbcHeadersParser;
import abc.parser.InvalidTokenEvent;

public class CrashTest extends TestCase {
	
	private static final String FILE_NAME = "../ressources/crash.abc";
	
	public CrashTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	

	public void test1(){
		try {
		File f = new File(FILE_NAME);
		System.out.println((new File(".")).getAbsolutePath());
		AbcHeadersParser hparser = new AbcHeadersParser();
		//hparser.addListener(new ParsingDumper());
		long headersParsingTime = 0;
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
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public class ParsingDumper extends AbcFileParserAdapter {
		public void tuneEnd(Tune tune) {
			System.out.println(tune.getReferenceNumber() + " : " + tune.getTitles()[0]);
		}
		public void validToken(TokenEvent event) {
			System.out.println("tok : " + event);
		}
		
		public void invalidCharacter(InvalidCharacterEvent evt) {
			System.err.println("invalid char : " + evt);
		}
		
		public void invalidToken(InvalidTokenEvent evt) {
			System.err.println("invalid token : " + evt);
		}
		
		 public void lineProcessed(String line) {
			 System.out.println("line processed : " + line);
		 }
	}

}

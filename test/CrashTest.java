import java.io.File;

import junit.framework.TestCase;
import abc.notation.Tune;
import abc.notation.TuneInfos;
import abc.parser.AbcFileParserListenerInterface;
import abc.parser.AbcNode;
import abc.parser.AbcParserAbstract;
import abc.parser.TuneBookParser;

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
		TuneBookParser hparser = new TuneBookParser();
		//hparser.addListener(new ParsingDumper());
		long headersParsingTime = 0;
			long start = System.currentTimeMillis();
			hparser.parseHeaders(f);
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
	
	
	public class ParsingDumper implements AbcFileParserListenerInterface {
/*		public void tuneEnd(Tune tune) {
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
		 }*/
		public void fileBegin() {
		}
		public void fileEnd() {
		}
		public void noTune() {
		}
		public void tuneBegin() {
		}
		public void tuneEnd(Tune tune, AbcNode abcRoot) {
			System.out.println(tune.getReferenceNumber() + " : " + tune.getTuneInfos().get(TuneInfos.TITLE));
			if (abcRoot.hasError()) {
				AbcParserAbstract.debugTree(abcRoot);
			}
		}
		public boolean isBusy() {
			return false;
		}
	}

}

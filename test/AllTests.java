import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for default package");
		//$JUnit-BEGIN$
		suite.addTestSuite(BeforeAudioRenditionTest.class);
		suite.addTestSuite(ChordsTest.class);
		suite.addTestSuite(ChordTest.class);
		suite.addTestSuite(CrashTest.class);
		suite.addTestSuite(DecorationsTest.class);
		suite.addTestSuite(GracingsTest.class);
		suite.addTestSuite(HeightTest.class);
		suite.addTestSuite(IntervalTests.class);
		suite.addTestSuite(KeySignatureTests.class);
		suite.addTestSuite(PerfTest.class);
		suite.addTestSuite(RhythmsTest.class);
		suite.addTestSuite(SlursTest.class);
		suite.addTestSuite(TieTest.class);
		suite.addTestSuite(TuneBookTest.class);
		suite.addTestSuite(Tuplets.class);
		suite.addTestSuite(VariousTests.class);
		//$JUnit-END$
		return suite;
	}

}

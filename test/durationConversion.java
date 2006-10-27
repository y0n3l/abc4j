import junit.framework.TestCase;
import abc.notation.Fraction;
import abc.notation.Note;
import abc.parser.AbcToolkit;

public class durationConversion extends TestCase {

	public static void main(String[] args) {
	}

	public durationConversion(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1(){
		Fraction relativeDuration = new Fraction(1,1);
		assertEquals(Note.QUARTER, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).getStrictDuration());
		assertEquals(0, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).countDots());
		relativeDuration = new Fraction(2,1);
		assertEquals(Note.HALF, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).getStrictDuration());
		assertEquals(0, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).countDots());
		relativeDuration = new Fraction(3,1);
		assertEquals(Note.HALF, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).getStrictDuration());
		assertEquals(1, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).countDots());
		relativeDuration = new Fraction(4,1);
		assertEquals(Note.WHOLE, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).getStrictDuration());
		assertEquals(0, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).countDots());
		//relativeDuration = new Fraction(5,1);
		//assertEquals(Note.WHOLE, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER));
		
		relativeDuration = new Fraction(1,2);
		assertEquals(Note.EIGHTH, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).getStrictDuration());
		boolean exceptionThrown = false;
		try{
			relativeDuration = new Fraction(1,3);
			AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER);
		}
		catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		relativeDuration = new Fraction(1,4);
		assertEquals(Note.SIXTEENTH, AbcToolkit.getAbsoluteDurationFor(relativeDuration, Note.QUARTER).getStrictDuration());
		
	}
	
	public void test2(){
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

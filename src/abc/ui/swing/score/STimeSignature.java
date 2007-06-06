package abc.ui.swing.score;

import java.awt.geom.Point2D;

import abc.notation.RepeatBarLine;
import abc.notation.TimeSignature;

public class STimeSignature {
	
	private TimeSignature m_ts = null;
	
	public STimeSignature(TimeSignature ts, Point2D base, ScoreRenditionContext c) {
		m_ts = ts;
	}
	
	public double render(ScoreRenditionContext context, Point2D base){
		//System.out.println("base for time : " + base);
		try {
			char[] numChars = getChar(m_ts.getNumerator());
			char[] denomChars = getChar(m_ts.getDenominator());
			context.getGraphics().drawChars(numChars, 0, 1, 
				(int)base.getX(), (int)(base.getY()-context.getNoteHeigth()*3.1));
			context.getGraphics().drawChars(denomChars, 0, 1, 
				(int)base.getX(), (int)(base.getY()-context.getNoteHeigth()*0.9));
			return context.getNoteWidth();
		}
		catch (IllegalArgumentException e) {
			return 0;
		}
	}
	
	public static char[] getChar(int number) {
		char[] chars = null;
		switch (number) {
			case 1 : chars = ScoreRenditionContext.NUMBER_1; break;
			case 2 : chars = ScoreRenditionContext.NUMBER_2; break;
			case 3 : chars = ScoreRenditionContext.NUMBER_3; break;
			case 4 : chars = ScoreRenditionContext.NUMBER_4; break;
			case 5 : chars = ScoreRenditionContext.NUMBER_5; break;
			case 6 : chars = ScoreRenditionContext.NUMBER_6; break;
			case 7 : chars = ScoreRenditionContext.NUMBER_7; break;
			case 8 : chars = ScoreRenditionContext.NUMBER_8; break;
			case 9 : chars = ScoreRenditionContext.NUMBER_9; break;
			// to do signature with 12.
			default : throw new IllegalArgumentException("no number matching " + number);
		}
		return chars;
	}
}

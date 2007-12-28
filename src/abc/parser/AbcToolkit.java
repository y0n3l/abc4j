package abc.parser;

import abc.notation.Fraction;
import abc.notation.Note;

/** A class that provides convenience methods to manipulate abc related 
 * strings and their representation using objects from the <TT>abc.notation</TT>
 * package. */
public class AbcToolkit {
	public static byte convertToRepeatBarLine(String barLine) {
      if (barLine.equals("[1")) return 1;
      else if (barLine.equals("[2")) return 2;
      else if (barLine.equals("|1")) return 1;
      else if (barLine.equals(":|2")) return 2;
      else return -1;
    }

    public static byte convertBrokenRhythm(String brokenRhythm) {
      byte br = (byte)brokenRhythm.length();
      if (brokenRhythm.equals("<")) return (byte)-br;
      else if (brokenRhythm.equals(">")) return (byte)br;
      else return 0;
    }

    public static byte convertToOctaveTransposition(String octave) {
      if (octave.charAt(0)=='\'')
        return (byte)octave.length();
      else if (octave.charAt(0)==',')
        return (byte)(-octave.length());
        else return 0;
    }
    
    /** Returns the absolute note duration for the specified relative note
     * with taking into account the default note length. 
     * @return The absolute note duration for the specified relative note
     * with taking into account the default note length. 
     * ONLY Possible values are <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT>, 
     * <TT>Note.QUARTER</TT>, <TT>Note.EIGHTH</TT>, <TT>Note.SIXTEENTH</TT>, 
     * <TT>Note.THIRTY_SECOND</TT>, <TT>Note.SIXTY_FOURTH</TT>. 
     * Usefull to convert notes such as : 
     * <IMG src="./images/dottedcrotchet.gif"/> 
     * @exception IllegalArgumentException Thrown if the computing of the 
     * absolute note duration is impossible. */ 
    public static DurationDescription getAbsoluteDurationFor(Fraction relativeDuration, short defaultDuration)
    	throws IllegalArgumentException {
    	// This algorithm is closely linked to the way constants are defined
    	// in the Note class !!!
    	int absoluteDuration = -1; 
    	byte dotsNumber = 0;
    	if ( (defaultDuration!=Note.HALF)&&(defaultDuration!=Note.QUARTER)&&
    			(defaultDuration!=Note.EIGHTH)&&(defaultDuration!=Note.SIXTEENTH))
    			throw new IllegalArgumentException("Invalid default note duration " + defaultDuration);
    			
    	/*if (relativeDuration.getNumerator()==1) {
    		// default note duration is divided by x
    		absoluteDuration = (short)(defaultDuration / relativeDuration.getDenominator());
    		if (absoluteDuration<Note.SIXTY_FOURTH)
    			throw new IllegalArgumentException("The relative duration " + relativeDuration + 
    					" has an absolute duration that is less than SIXTY_FOURTH " +
    					" with a default duration equals to " + defaultDuration);
    	}
    	else {*/
    		absoluteDuration = defaultDuration * relativeDuration.getNumerator();
    		absoluteDuration = absoluteDuration / relativeDuration.getDenominator();
    		int remainingDurTmp = 0;
    		if (absoluteDuration>=2*Note.WHOLE)
	    		throw new IllegalArgumentException ("Cannot calculate the dots for " + relativeDuration +
	    				" with a default duration equals to " + defaultDuration + 
	    				" : absolute note length was equal to " + absoluteDuration);
    		else
    		if (absoluteDuration>=Note.WHOLE) {
    			remainingDurTmp = absoluteDuration - Note.WHOLE; absoluteDuration = Note.WHOLE;
    		} else
    			if (absoluteDuration>=Note.HALF) {
    				remainingDurTmp = absoluteDuration - Note.HALF; absoluteDuration = Note.HALF;
    			}
    			else
    				if (absoluteDuration>=Note.QUARTER) {
        				remainingDurTmp = absoluteDuration - Note.QUARTER; absoluteDuration = Note.QUARTER;
        			}
        			else
        				if (absoluteDuration>=Note.EIGHTH) {
            				remainingDurTmp = absoluteDuration - Note.EIGHTH; absoluteDuration = Note.EIGHTH;
            			}
            			else
            				if (absoluteDuration>=Note.SIXTEENTH) {
                				remainingDurTmp = absoluteDuration - Note.SIXTEENTH; absoluteDuration = Note.SIXTEENTH;
                			}
            				else
            				if (absoluteDuration>=Note.THIRTY_SECOND) {
                				remainingDurTmp = absoluteDuration - Note.THIRTY_SECOND; absoluteDuration = Note.THIRTY_SECOND;
                			}
            				else
                				if (absoluteDuration>=Note.SIXTY_FOURTH) {
                    				remainingDurTmp = absoluteDuration -Note.SIXTY_FOURTH; absoluteDuration = Note.SIXTY_FOURTH;
                    			}
    		//from here, absDurTemp contains the *real* note duration (without the dots)
    		//and remainingDurTemp contains the part that should be expressed using dots.
    		if (remainingDurTmp!=0) {
    			//valuates the number of dots.
    			int durationRepresentedByDots = 0;
    			int currentDur = absoluteDuration;
    		    while ( durationRepresentedByDots!=remainingDurTmp) {
    		    	dotsNumber++;
    		    	currentDur = currentDur/2;
    		    	durationRepresentedByDots = durationRepresentedByDots + currentDur;
    		    	if (durationRepresentedByDots>remainingDurTmp)
    		    		throw new IllegalArgumentException ("Cannot calculate the dots for " + relativeDuration +
    		    				" with a default duration equals to " + defaultDuration + 
    		    				" : absolute note length was equal to " + absoluteDuration);
    		    }
    		}
    	//le }
    	return new DurationDescription((short)absoluteDuration, dotsNumber);
    }
    
    public static class DurationDescription {
    	private short m_strictDuration;
    	private byte m_dotsNb;
    	DurationDescription(short noteDuration, byte dotsNb){
    		m_strictDuration = noteDuration;
    		m_dotsNb = dotsNb;
    	}
    	
    	public short getStrictDuration() {
    		return m_strictDuration;
    	}
    	
    	public byte countDots() {
    		return m_dotsNb;
    	}
    	
    }
	
}
package abc.notation;

/**
 * This class defines a (single) Note : height, rhythm, part of tuplet, rest etc...
 * There can be some tricky representation of a duration for a note.
 * For instance :<BR/>
 * <B>Tuplets</B><BR/>
 * <IMG src="../../images/tuplets.gif"/><BR/>
 * The first tuplet describes 3 quarter notes in the time of 2. So in that case, 
 * the representation of each note of tuplet as a <TT>Note</TT> object is :
 * <UL>
 * <LI><TT>getStrictDuration()</TT> returns <TT>Note.QUARTER</TT></LI>
 * <LI><TT>isPartOfTuplet()</TT> returns <TT>true</TT></LI>
 * <LI><TT>getTuplet()</TT> returns a <TT>Tuplet</TT> instance that enacapsulates
 * the three instances of notes that are part of the tuplet.</LI>
 * </UL>
 * The same applies to the second tuplet except that the strict duration of the
 * notes composing the tuplet is <TT>Note.EIGHTH</TT>. Same for the third tuplet
 * with a strict duration equals to <TT>Note.SIXTEENTH</TT>.
 * <B>Dots</B><BR/>
 * <IMG src="../../images/multiDots.jpg"/><BR/>
 * When a note is dotted, its strict duration remains unchanged. The only difference
 * between a non-dotted note and a dotted one can be retrieved from the method 
 * <TT>countDots()</TT> that returns the number of dots for a given <TT>Note</TT> instance.
 * So in the example above :
 * <UL>
 * <LI>For the first example</LI>
 * 		<UL>
 * 			<LI><TT>getStrictDuration()</TT> returns <TT>Note.WHOLE</TT></LI>
 * 			<LI><TT>countDots()</TT> returns <TT>2</TT></LI>
 *		</UL>
 * <LI>For the second example</LI>
 * 		<UL>
 * 			<LI><TT>getStrictDuration()</TT> returns <TT>Note.HALF</TT></LI>
 * 			<LI><TT>countDots()</TT> returns <TT>2</TT></LI>
 *		</UL>
 * <LI>For the third example</LI>
 * 		<UL>
 * 			<LI><TT>getStrictDuration()</TT> returns <TT>Note.HALF</TT></LI>
 * 			<LI><TT>countDots()</TT> returns <TT>3</TT></LI>
 *		</UL>
 * </UL>
 */
public class Note extends NoteAbstract
{
  /** The <TT>C</TT> note height type. */
  public static final byte C		= 0;
  /** The <TT>D</TT> note height type. */
  public static final byte D		= 2;
  /** The <TT>E</TT> note height type. */
  public static final byte E		= 4;
  /** The <TT>F</TT> note height type. */
  public static final byte F		= 5;
  /** The <TT>G</TT> note height type. */
  public static final byte G		= 7;
  /** The <TT>A</TT> note height type : A440 */
  public static final byte A		= 9;
  /** The <TT>B</TT> note height type. */
  public static final byte B		= 11;
  /** The <TT>c</TT> note height type. */
  public static final byte c		= 12;
  /** The <TT>d</TT> note height type. */
  public static final byte d		= 14;
  /** The <TT>e</TT> note height type. */
  public static final byte e		= 16;
  /** The <TT>f</TT> note height type. */
  public static final byte f		= 17;
  /** The <TT>g</TT> note height type. */
  public static final byte g		= 19;
  /** The <TT>a</TT> note height type. */
  public static final byte a		= 21;
  /** The <TT>b</TT> note height type. */
  public static final byte b		= 23;

  /** The <TT>REST</TT> height type. */
  public static final byte REST		= -128;

  private static final short LENGTH_RESOLUTION = 3;
  /** The <TT>DOTTED_WHOLE</TT> length type. */
  public static final short DOTTED_WHOLE	= LENGTH_RESOLUTION * 96;
  /** The <TT>WHOLE</TT> length type. <IMG src="../../images/whole.jpg"/>*/
  public static final short WHOLE		= LENGTH_RESOLUTION * 64; //ronde
  /** The <TT>DOTTED_HALF</TT> length type. */
  public static final short DOTTED_HALF	 	= LENGTH_RESOLUTION * 48;
  /** The <TT>HALF</TT> length type. <IMG src="../../images/half.jpg"/>*/
  public static final short HALF		= LENGTH_RESOLUTION * 32; //blanche
  /** The <TT>DOTTED_QUARTER</TT> length type. */
  public static final short DOTTED_QUARTER    	= LENGTH_RESOLUTION * 24;
  /** The <TT>QUARTER</TT> length type. <IMG src="../../images/quarter.jpg"/>*/
  public static final short QUARTER	    	= LENGTH_RESOLUTION * 16; // noire
  /** The <TT>DOTTED_EIGHTH</TT> length type. */
  public static final short DOTTED_EIGHTH	= LENGTH_RESOLUTION * 12;
  /** The <TT>EIGHTH</TT> length type. <IMG src="../../images/eighth.jpg"/>*/
  public static final short EIGHTH		= LENGTH_RESOLUTION * 8;  // croche
  /** The <TT>DOTTED_SIXTEENTH</TT> length type. */
  public static final short DOTTED_SIXTEENTH    = LENGTH_RESOLUTION * 6;
  /** The <TT>SIXTEENTH</TT> length type. <IMG src="../../images/sixteenth.jpg"/>*/
  public static final short SIXTEENTH	    	= LENGTH_RESOLUTION * 4;  // double croche
  /** The <TT>DOTTED_THIRTY_SECOND</TT> length type. */
  public static final short DOTTED_THIRTY_SECOND= LENGTH_RESOLUTION * 3 ;
  /** The <TT>THIRTY_SECOND</TT> length type. <IMG src="../../images/thirtySecond.jpg"/>*/
  public static final short THIRTY_SECOND 	= LENGTH_RESOLUTION * 2 ; // triple croche
  /** The <TT>DOTTED_SIXTY_FOURTH</TT> length type. */
  public static final short DOTTED_SIXTY_FOURTH	= (short)(LENGTH_RESOLUTION * 1.5);
  /** The <TT>SIXTY_FOURTH</TT> length type. */
  public static final short SIXTY_FOURTH	= LENGTH_RESOLUTION;      // quadruple croche

  private byte height = REST;
  private byte octaveTransposition = 0;
  /** Accidental for this note. */
  private byte accidental = AccidentalType.NONE;
  /** The full whole duration that takes into account the dots. (why not
   * the tuplet stuff ? :/ ) */
  private short m_duration = -1;
  /** The strict duration (that does not take into account the dots, 
   * the tuplet or whatever : this is the pure note type definition. */
  private short m_strictDuration = -1;
  /** <TT>true</TT> if this note is tied, <TT>false</TT> otherwise. */
  private boolean m_isTied = false;

  /** Creates an abc note with the specified heigth and accidental.
   * @param heigthValue The heigth of the note.
   * @param accidentalValue The accidental of the note. */
  public Note (byte heigthValue, byte accidentalValue)
  {
    super();
    height = heigthValue;
    accidental = accidentalValue;
  }

  /** Creates an abc note with the specified heigth, accidental and octave
   * transposition.
   * @param heigthValue
   * @param accidentalValue */
  public Note (byte heigthValue, byte accidentalValue, byte octaveTranspositionValue)
  {
    super();
    height = heigthValue;
    accidental = accidentalValue;
    octaveTransposition = octaveTranspositionValue;
  }

  /** Sets the heigth of this note.
   * @param heigthValue The heigth of this note. The heigth is <TT>REST</TT> if
   * this note is a rest.
   * @deprecated use setHeight(byte heigthValue) instead. sorry for the typo... 
   * @see #setHeight(byte) */
  public void setHeigth(byte heigthValue)
  { height = heigthValue; }
  
  /** Sets the heigth of this note.
   * @param heigthValue The heigth of this note. The heigth is <TT>REST</TT> if
   * this note is a rest. */
  public void setHeight(byte heigthValue)
  { height = heigthValue; }

  /** Returns this note absolute height. This height doesn't take in account
   * octave transposition.
   * @return This note height. <TT>REST</TT> is returned if this note is a rest.
   * Possible other values are <TT>C</TT>, <TT>D</TT>, <TT>E</TT>, <TT>F</TT>, <TT>G</TT>,
   * <TT>A</TT>, <TT>B</TT>, <TT>c</TT>, <TT>d</TT>, <TT>e</TT>, <TT>f</TT>, <TT>g</TT>,
   * <TT>a</TT> or <TT>b</TT>. 
   * @deprecated use getHeight() instead. Sorry for the typo.... 
   * @see #getHeight() */
  public byte getHeigth ()
  { return height; }
  
  /** Returns this note absolute height. This height doesn't take in account
   * octave transposition.
   * @return This note height. <TT>REST</TT> is returned if this note is a rest.
   * Possible other values are <TT>C</TT>, <TT>D</TT>, <TT>E</TT>, <TT>F</TT>, <TT>G</TT>,
   * <TT>A</TT>, <TT>B</TT>, <TT>c</TT>, <TT>d</TT>, <TT>e</TT>, <TT>f</TT>, <TT>g</TT>,
   * <TT>a</TT> or <TT>b</TT>. */
  public byte getHeight() {
	  return height;
  }

  /** Returns the heigth of this note on the first octave.
   * @return the heigth of this note on the first octave. Possible values are
   * <TT>C</TT>, <TT>D</TT>, <TT>E</TT>, <TT>F</TT>, <TT>G</TT>, <TT>A</TT>(404)
   * or <TT>B</TT>. */
  public byte toRootOctaveHeigth()
  { return (byte)Math.abs(height%12); }

  /** Sets the octave transposition for this note.
   * @param octaveTranspositionValue The octave transposition for this note :
   * 1, 2 or 3 means "1, 2 or 3 octave(s) higher than the reference octave" and
   * -1, -2 or -3 means "1, 2 or 3 octave(s) less than the reference octave". */
  public void setOctaveTransposition (byte octaveTranspositionValue)
  { octaveTransposition = octaveTranspositionValue; }

  /** Returns the octave transposition for this note.
   * @return The octave transposition for this note. Default is 0.*/
  public byte getOctaveTransposition()
  { return octaveTransposition; }

  /** Sets the length of this note. 
   * @deprecated use setDuration(short duration) instead.
   * @param length The length of this note as a value adjusted to 
   * the scale of constants such as <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT> etc etc ... 
   * @see #setDuration(short)*/
  public void setLength(short length) { 
	  m_duration = length;
  }
  
  /** Sets the length of this note. However, it is recommanded to represent
   * the note duration using methods such as setStrictDuration(short strictDuration), 
   * setDotted(byte dotted) etc etc as explained at the begining of this class description.
   * @param duration The length of this note as a value adjusted to 
   * the scale of constants such as <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT> etc etc ... 
   * @see #getDuration()*/
  public void setDuration(short duration) { 
	  m_duration = duration;
	  System.err.println("[warning]duration of " + this + 
			  " set in an absolute manner with " + duration + "(not recommanded but supported)");
	  //Thread.dumpStack();
  }
  
  /** Sets the strict duration of this note.
   * @param strictDuration This note strict duration. Possible values are ONLY 
   * <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT>,
   * <TT>Note.QUARTER</TT>, <TT>Note.EIGHTH</TT>, <TT>Note.SIXTEENTH</TT>, 
   * <TT>Note.THIRTY_SECOND</TT>, <TT>Note.SIXTY_FOURTH</TT>.
   * @exception IllegalArgumentException Thrown if the given duration does not 
   * match the excepted ones. */
  public void setStrictDuration(short strictDuration) throws IllegalArgumentException {
	  if (isStrictDuration(strictDuration))
		  m_strictDuration = strictDuration;
	  else
		  throw new IllegalArgumentException("The note duration " + strictDuration + " is not equals to " +
				  "Note.WHOLE, Note.HALF, Note.QUARTER, Note.EIGHTH, Note.SIXTEENTH, " + 
				  "Note.THIRTY_SECOND or Note.SIXTY_FOURTH");
	  // Re init the whole duration => will be computed later on request only.
	  m_duration = -1;
  }
  
  /** Returns the strict duration of this note. 
   * @return The strict duration of this note. The dot, tuplet whatever... 
   * are taken NOT into account for the duration returned by this function.
   * The possible returned values are :
   * <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT>,
   * <TT>Note.QUARTER</TT>, <TT>Note.EIGHTH</TT>, <TT>Note.SIXTEENTH</TT>, 
   * <TT>Note.THIRTY_SECOND</TT>, <TT>Note.SIXTY_FOURTH</TT> or -1 if this
   * note duration is not expressed using (strict duration + dots + tuplet)
   * but with an exotic duration (that can be retrieved using getDuration()
   * in that case).
   * @see #getDuration() */
  public short getStrictDuration() {
    return m_strictDuration;
  }

  /** Returns the duration of this note. The duration returned here takes into 
   * account if the note is dotted, part of a tuplet and so on ... (as opposed
   * to <TT>getStrictDuration()</TT> that only refers to the "pure" note) 
   * @return The duration of this note as a value adjusted to 
   * the scale of constants such as <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT> etc etc ...
   * @see #setLength(short)
   * @see #getStrictDuration() */
  public short getDuration() {
	  if (m_duration!=-1)
		  //The duration has been set in an absolute manner (not recommanded, but 
		  //can be usefull
		  return m_duration;
	  else {
		  //Compute the absolute duration from the strict duration and dots
		  //Store it and return it.
		  return m_duration = computeDuration(m_strictDuration, countDots());
	  }
  }
  
  /** Sets the accidental for this note.
   * @param accidentalValue Accidental for this note. Possible values are
   * <TT>NATURAL</TT>, <TT>SHARP</TT> (#) or <TT>FLAT</TT> (b). */
  public void setAccidental(byte accidentalValue)
  { accidental = accidentalValue; }

  /** Returns accidental for this note if any.
   * @return Accidental for this note if any. Possible values are
   * <TT>NATURAL</TT>, <TT>FLAT</TT> or <TT>SHARP</TT>.
   * @see #setAccidental(byte) */
  public byte getAccidental()
  { return accidental; }

  /** Sets if this note is tied, wheter or not.
   * @param isTied <TT>true</TT> if this note is tied, <TT>false</TT> otherwise. */
  public void setIsTied(boolean isTied)
  { m_isTied = isTied; }

  /** Returns <TT>true</TT> if this note is tied.
   * @return <TT>true</TT> if this note is tied, <TT>false</TT> otherwise. */
  public boolean isTied()
  { return m_isTied; }

  /** Returns <TT>true</TT> if this note is a rest
   * @return <TT>true</TT> if this note is a rest, <TT>false</TT> otherwise. */
  public boolean isRest()
  { return (height == REST); }
  
  	public void setDotted(byte dotted) { 
  		super.setDotted(dotted);
  		// Re init the whole duration => will be computed later on request only.
  		m_duration = -1; 
  	}

  public static byte convertToNoteType(String note)
  {
    if (note.equals("A")) return Note.A;
    else if (note.equals("B")) return Note.B;
    else if (note.equals("C")) return Note.C;
    else if (note.equals("D")) return Note.D;
    else if (note.equals("E")) return Note.E;
    else if (note.equals("F")) return Note.F;
    else if (note.equals("G")) return Note.G;
    else if (note.equals("a")) return Note.a;
    else if (note.equals("b")) return Note.b;
    else if (note.equals("c")) return Note.c;
    else if (note.equals("d")) return Note.d;
    else if (note.equals("e")) return Note.e;
    else if (note.equals("f")) return Note.f;
    else if (note.equals("g")) return Note.g;
    else if (note.equals("z")) return Note.REST;
    else return -1;
  }

  public static short convertToNoteLengthStrict(int num, int denom) throws IllegalArgumentException
  {
    if (num==1 && denom==1) return Note.WHOLE;
    else if (num==1 && denom==2) return Note.HALF;
    else if (num==1 && denom==4) return Note.QUARTER;
    else if (num==1 && denom==8) return Note.EIGHTH;
    else if (num==1 && denom==16) return Note.SIXTEENTH;
    else if (num==1 && denom==32) return Note.THIRTY_SECOND;
    else if (num==1 && denom==64) return Note.SIXTY_FOURTH;
    else throw new IllegalArgumentException(num + "/" + denom + " doesn't correspond to any strict note length");
  }

  public static byte convertToAccidentalType(String accidental) throws IllegalArgumentException
  {
    if (accidental==null) return AccidentalType.NONE;
    else if (accidental.equals("^")) return AccidentalType.SHARP;
    else if (accidental.equals("^^")) return AccidentalType.SHARP;
    else if (accidental.equals("_")) return AccidentalType.FLAT;
    else if (accidental.equals("__")) return AccidentalType.FLAT;
    else if (accidental.equals("=")) return AccidentalType.NATURAL;
    else throw new IllegalArgumentException(accidental + " is not a valid accidental");
  }

  public String toString()
  {
    String string2Return = super.toString();
    if (height == REST) 	string2Return = string2Return.concat("z"); else
    if (height == C) 	string2Return = string2Return.concat("C"); else
    if (height == D) 	string2Return = string2Return.concat("D"); else
    if (height == E) 	string2Return = string2Return.concat("E"); else
    if (height == F) 	string2Return = string2Return.concat("F"); else
    if (height == G) 	string2Return = string2Return.concat("G"); else
    if (height == A) 	string2Return = string2Return.concat("A"); else
    if (height == B) 	string2Return = string2Return.concat("B"); else
    if (height == c) 	string2Return = string2Return.concat("c"); else
    if (height == d) 	string2Return = string2Return.concat("d"); else
    if (height == e) 	string2Return = string2Return.concat("e"); else
    if (height == f) 	string2Return = string2Return.concat("f"); else
    if (height == g) 	string2Return = string2Return.concat("g"); else
    if (height == a) 	string2Return = string2Return.concat("a"); else
    if (height == b) 	string2Return = string2Return.concat("b");
    if (octaveTransposition == 1) 	string2Return = string2Return.concat("'"); else
    if (octaveTransposition == -1) 	string2Return = string2Return.concat(",");
    if (accidental == AccidentalType.FLAT)	string2Return = string2Return.concat("b");
    if (accidental == AccidentalType.SHARP)	string2Return = string2Return.concat("#");
    //string2Return = string2Return.concat(relativeLength.toString());
    return string2Return;
  }
  
  /** Returns <TT>true</TT> if the duration of the note is one of the 
   * following : <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT>,
   * <TT>Note.QUARTER</TT>, <TT>Note.EIGHTH</TT>, <TT>Note.SIXTEENTH</TT>, 
   * <TT>Note.THIRTY_SECOND</TT>, <TT>Note.SIXTY_FOURTH</TT>.
   * @param noteDuration The note duration to be checked
   * @return <TT>true</TT> if the duration of the note is one of the 
   * following : <TT>Note.WHOLE</TT>, <TT>Note.HALF</TT>,
   * <TT>Note.QUARTER</TT>, <TT>Note.EIGHTH</TT>, <TT>Note.SIXTEENTH</TT>, 
   * <TT>Note.THIRTY_SECOND</TT>, <TT>Note.SIXTY_FOURTH</TT>.
   * <TT>false</TT> otherwise. */
  public static boolean isStrictDuration(short noteDuration) {
	  return (
			  (noteDuration==Note.WHOLE)|| (noteDuration==Note.HALF) ||
			  (noteDuration==Note.QUARTER) || (noteDuration==Note.EIGHTH) ||
			  (noteDuration==Note.SIXTEENTH) || (noteDuration==Note.THIRTY_SECOND) ||
			  (noteDuration==Note.SIXTY_FOURTH));
  }

  	/** Compute a duration that takes strict duration as a reference plus
  	 * the duration of the optional dots and the tuplet if any. */ 
  	private short computeDuration(short strictDuration, int dotsNumber){
  		short duration = strictDuration;
  	    if (isPartOfTuplet()) {
  	    	//if the note is part of tuplet then, for now,
  	    	//ignore the strict duration of the note. => more simple
  	    	//but may need to ne improved.
  	    	Tuplet tuplet = getTuplet();
  	    	int notesNb = tuplet.getNotesAsVector().size();
  	    	float totalTupletDuration = tuplet.getTotalDuration();
  	    	//The correction for the note duration because that's a tuplet.
  	    	duration = (short)(totalTupletDuration / notesNb);
  	    }
  	    else {
  	  		int dotsNb = countDots();
  	  		for (int i=1; i<=dotsNb; i++) {
  	  			short dottedDuration = (short)(strictDuration / (i+1)); //at the first dot (i=1), we should divide by 2
  	  			duration = (short)(duration + dottedDuration);
  	  		}
  	    }
  	    return duration;
  	}
}


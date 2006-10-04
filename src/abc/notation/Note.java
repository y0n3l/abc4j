package abc.notation;

/**
 * This class defines a (single) Note : heigth, rhythm, part of tuplet, rest etc...
 */
public class Note extends NoteAbstract
{
  /** The <TT>C</TT> note heigth type. */
  public static final byte C		= 0;
  /** The <TT>D</TT> note heigth type. */
  public static final byte D		= 2;
  /** The <TT>E</TT> note heigth type. */
  public static final byte E		= 4;
  /** The <TT>F</TT> note heigth type. */
  public static final byte F		= 5;
  /** The <TT>G</TT> note heigth type. */
  public static final byte G		= 7;
  /** The <TT>A</TT> note heigth type : A404 */
  public static final byte A		= 9;
  /** The <TT>B</TT> note heigth type. */
  public static final byte B		= 11;
  /** The <TT>c</TT> note heigth type. */
  public static final byte c		= 12;
  /** The <TT>d</TT> note heigth type. */
  public static final byte d		= 14;
  /** The <TT>e</TT> note heigth type. */
  public static final byte e		= 16;
  /** The <TT>f</TT> note heigth type. */
  public static final byte f		= 17;
  /** The <TT>g</TT> note heigth type. */
  public static final byte g		= 19;
  /** The <TT>a</TT> note heigth type. */
  public static final byte a		= 21;
  /** The <TT>b</TT> note heigth type. */
  public static final byte b		= 23;

  /** The <TT>REST</TT> heigth type. */
  public static final byte REST		= -128;

  private static final short LENGTH_RESOLUTION = 2;
  /** The <TT>DOTTED_WHOLE</TT> length type. */
  public static final short DOTTED_WHOLE	= LENGTH_RESOLUTION * 96;
  /** The <TT>WHOLE</TT> length type. */
  public static final short WHOLE		= LENGTH_RESOLUTION * 64; //ronde
  /** The <TT>DOTTED_HALF</TT> length type. */
  public static final short DOTTED_HALF	 	= LENGTH_RESOLUTION * 48;
  /** The <TT>HALF</TT> length type. */
  public static final short HALF		= LENGTH_RESOLUTION * 32; //blanche
  /** The <TT>DOTTED_QUARTER</TT> length type. */
  public static final short DOTTED_QUARTER    	= LENGTH_RESOLUTION * 24;
  /** The <TT>QUARTER</TT> length type. */
  public static final short QUARTER	    	= LENGTH_RESOLUTION * 16; // noire
  /** The <TT>DOTTED_EIGHTH</TT> length type. */
  public static final short DOTTED_EIGHTH	= LENGTH_RESOLUTION * 12;
  /** The <TT>EIGHTH</TT> length type. */
  public static final short EIGHTH		= LENGTH_RESOLUTION * 8;  // croche
  /** The <TT>DOTTED_SIXTEENTH</TT> length type. */
  public static final short DOTTED_SIXTEENTH    = LENGTH_RESOLUTION * 6;
  /** The <TT>SIXTEENTH</TT> length type. */
  public static final short SIXTEENTH	    	= LENGTH_RESOLUTION * 4;  // double croche
  /** The <TT>DOTTED_THIRTY_SECOND</TT> length type. */
  public static final short DOTTED_THIRTY_SECOND= LENGTH_RESOLUTION * 3 ;
  /** The <TT>THIRTY_SECOND</TT> length type. */
  public static final short THIRTY_SECOND 	= LENGTH_RESOLUTION * 2 ; // triple croche
  /** The <TT>DOTTED_SIXTY_FOURTH</TT> length type. */
  public static final short DOTTED_SIXTY_FOURTH	= (short)(LENGTH_RESOLUTION * 1.5);
  /** The <TT>SIXTY_FOURTH</TT> length type. */
  public static final short SIXTY_FOURTH	= LENGTH_RESOLUTION;      // quadruple croche

  private byte heigth = REST;
  private byte octaveTransposition = 0;
  private byte accidental = AccidentalType.NONE;
  private short m_length = QUARTER;
  private boolean m_isTied = false;

  /** Creates an abc note with the specified heigth and accidental.
   * @param heigthValue The heigth of the note.
   * @param accidentalValue The accidental of the note. */
  public Note (byte heigthValue, byte accidentalValue)
  {
    super();
    heigth = heigthValue;
    accidental = accidentalValue;
  }

  /** Creates an abc note with the specified heigth, accidental and octave
   * transposition.
   * @param heigthValue
   * @param accidentalValue */
  public Note (byte heigthValue, byte accidentalValue, byte octaveTranspositionValue)
  {
    super();
    heigth = heigthValue;
    accidental = accidentalValue;
    octaveTransposition = octaveTranspositionValue;
  }

  /** Sets the heigth of this note.
   * @param heigthValue The heigth of this note. The heigth is <TT>REST</TT> if
   * this note is a rest.
   * @see #setHeigth(byte) */
  public void setHeigth(byte heigthValue)
  { heigth = heigthValue; }

  /** Returns this note absolute height. This height doesn't take in account
   * octave transposition.
   * @return This note height. <TT>REST</TT> is returned if this note is a rest.
   * Possible other values are <TT>C</TT>, <TT>D</TT>, <TT>E</TT>, <TT>F</TT>, <TT>G</TT>,
   * <TT>A</TT>, <TT>B</TT>, <TT>c</TT>, <TT>d</TT>, <TT>e</TT>, <TT>f</TT>, <TT>g</TT>,
   * <TT>a</TT> or <TT>b</TT>. */
  public byte getHeigth ()
  { return heigth; }

  /** Returns the heigth of this note on the first octave.
   * @return the heigth of this note on the first octave. Possible values are
   * <TT>C</TT>, <TT>D</TT>, <TT>E</TT>, <TT>F</TT>, <TT>G</TT>, <TT>A</TT>(404)
   * or <TT>B</TT>. */
  public byte toRootOctaveHeigth()
  { return (byte)Math.abs(heigth%12); }

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

  public void setLength(short length)
  { m_length = length; }

  /**Returns the relative length of this note.
   * @return The relative length of this note. The relative length can be
   * converted into absolute length (the real length of the note) by using
   * the default note length value defines in the score this note belongs to. */
  public short getDuration()
  {
    int length = m_length;
    if (getDotted()!=0)
    {
      //int dot = getDotted();
      int dottedLength = length / (getDotted()*2);
      length = length + dottedLength;
    }
    return (short)length;
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
  { return (heigth == REST); }

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
    if (heigth == REST) 	string2Return = string2Return.concat("z"); else
    if (heigth == C) 	string2Return = string2Return.concat("C"); else
    if (heigth == D) 	string2Return = string2Return.concat("D"); else
    if (heigth == E) 	string2Return = string2Return.concat("E"); else
    if (heigth == F) 	string2Return = string2Return.concat("F"); else
    if (heigth == G) 	string2Return = string2Return.concat("G"); else
    if (heigth == A) 	string2Return = string2Return.concat("A"); else
    if (heigth == B) 	string2Return = string2Return.concat("B"); else
    if (heigth == c) 	string2Return = string2Return.concat("c"); else
    if (heigth == d) 	string2Return = string2Return.concat("d"); else
    if (heigth == e) 	string2Return = string2Return.concat("e"); else
    if (heigth == f) 	string2Return = string2Return.concat("f"); else
    if (heigth == g) 	string2Return = string2Return.concat("g"); else
    if (heigth == a) 	string2Return = string2Return.concat("a"); else
    if (heigth == b) 	string2Return = string2Return.concat("b");
    if (octaveTransposition == 1) 	string2Return = string2Return.concat("'"); else
    if (octaveTransposition == -1) 	string2Return = string2Return.concat(",");
    if (accidental == AccidentalType.FLAT)	string2Return = string2Return.concat("b");
    if (accidental == AccidentalType.SHARP)	string2Return = string2Return.concat("#");
    //string2Return = string2Return.concat(relativeLength.toString());
    return string2Return;
  }
}


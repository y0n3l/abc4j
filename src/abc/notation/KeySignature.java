// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
package abc.notation;

/** This class defines key signatures using modes definition like E major, G minor etc etc...
 * <PRE>
 *                           1   2   3   4   5   6   7
 * Major (Ionian, mode 1)    D   E   F#  G   A   B   C#
 * Dorian (mode 2)               E   F#  G   A   B   C#   D
 * Mixolydian (mode 5)                       A   B   C#   D   E   F#  G
 * Aeolian (mode 6)                              B   C#   D   E   F#  G   A
 * </PRE>
 * If we consider the key namned "Ab aeolian", "A" is the note of this
 * key, "b" is the key accidental and "aeolian" is the mode. */
public class KeySignature implements MusicElement, Cloneable
{
    private final byte[][] accidentalsRules = {
//	Flyd C Cmaj Cion Gmix Ddor Amin Am Aeol  Ephr Bloc
//						C    					D    					E    					F    					G    					A    					B
/*C*/ {AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL },	// C
/*Db*/{AccidentalType.NATURAL, 	AccidentalType.FLAT,	AccidentalType.FLAT,	AccidentalType.NATURAL,	AccidentalType.FLAT,	AccidentalType.FLAT,	AccidentalType.FLAT},
/*D*/ {AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL },	// D
/*Db*/{AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.FLAT, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.FLAT, 	AccidentalType.FLAT },
/*E*/ {AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.NATURAL },	// E
/*F*/ {AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.FLAT },		// F
      {},	// This one would contain E#(=F) Not really OK 
/*G*/ {AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL },	// G
/*Ab*/{AccidentalType.NATURAL, 	AccidentalType.FLAT, 	AccidentalType.FLAT, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.FLAT, 	AccidentalType.FLAT },
/*A*/ {AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.NATURAL },	// A
/*Bb*/{AccidentalType.NATURAL, 	AccidentalType.NATURAL, AccidentalType.FLAT, 	AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.NATURAL, AccidentalType.FLAT },
/*B*/ {AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.NATURAL, AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.NATURAL }	// B
    };
    
    private final byte[][] accidentalRulesFlat = {
/*C*/ {},
/* */ {},
/*D*/ {},
/* */ {},
/*E*/ {},
/*F*/ {},
/*Gb*/{AccidentalType.FLAT, 		AccidentalType.FLAT, 	AccidentalType.FLAT, 	AccidentalType.NATURAL, AccidentalType.FLAT, 	AccidentalType.FLAT, 	AccidentalType.FLAT },
/*G*/ {},
/* */ {},
/*A*/ {},
/* */ {},
/*Cb*/{AccidentalType.FLAT, 		AccidentalType.FLAT, 	AccidentalType.FLAT, 	AccidentalType.FLAT, 	AccidentalType.FLAT, 	AccidentalType.FLAT, 	AccidentalType.FLAT }
    };
    
    private final byte[][] accidentalRulesSharp = {
/*C*/ {},
/*C#*/{AccidentalType.SHARP,		AccidentalType.SHARP,	AccidentalType.SHARP, 	AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.SHARP },
/*D*/ {},
/* */ {},
/*E*/ {},
/*F*/ {},
/*F#*/{AccidentalType.SHARP,		AccidentalType.SHARP,	AccidentalType.SHARP, 	AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.SHARP,	AccidentalType.NATURAL },
/*G*/ {},
/* */ {},
/*A*/ {},
/* */ {},
/*B*/ {}
    };

    /** The aeolian mode type. */
    public static final byte AEOLIAN	= 0;
    /** The dorian mode type. */
    public static final byte DORIAN	= 1;
    /** The ionian mode type. */
    public static final byte IONIAN	= 2;
    /** The locrian mode type. */
    public static final byte LOCRIAN	= 3;
    /** The lydian mode type. */
    public static final byte LYDIAN	= 4;
    /** The major mode type. */
    public static final byte MAJOR	= 5;
    /** The minor mode type. */
    public static final byte MINOR	= 6;
    /** The mixolydian mode type. */
    public static final byte MIXOLYDIAN= 7;
    /** The phrygian mode type. */
    public static final byte PHRYGIAN	= 8;
    /** The "not standard" mode type. */
    public static final byte OTHER	= -1;

    /** Highland Bagpipe notation is also catered for :
     * K:HP
     * puts no key signature on the music but implies the bagpipe scale, while
     * K:Hp
     * puts F sharp, C sharp and G natural. */
    private byte key = Note.C;
    private byte m_keyAccidental = AccidentalType.NATURAL;
    private byte mode = OTHER;
    private byte[] accidentals = accidentalsRules[0];
    private int keyIndex=0;

    /** Creates a new signature with the specified parameters.
     * @param keyNoteType The note of the mode. Possible values are
     * <TT>Note.A</TT>, <TT>Note.B</TT>, <TT>Note.C</TT>, <TT>Note.D</TT>,
     * <TT>Note.E</TT>, <TT>Note.F</TT> or <TT>Note.G</TT>.
     * @param modeType The type of the mode. Possible values are
     * <TT>AEOLIAN</TT>, <TT>DORIAN</TT>, <TT>IONIAN</TT>, <TT>LOCRIAN</TT>, <TT>LYDIAN</TT>
     * <TT>MAJOR</TT>, <TT>MINOR</TT>, <TT>MIXOLYDIAN</TT>, <TT>PHRYGIAN</TT> or <TT>OTHER</TT>. */
    public KeySignature (byte keyNoteType, byte modeType)
    {
      this(keyNoteType, AccidentalType.NONE, modeType);
    }

    /** Creates a new signature with the specified parameters.
     * @param keyNoteType The note of the mode. Possible values are
     * <TT>Note.A</TT>, <TT>Note.B</TT>, <TT>Note.C</TT>, <TT>Note.D</TT>,
     * <TT>Note.E</TT>, <TT>Note.F</TT> or <TT>Note.G</TT>.
     * @param keyAccidental Accidental for the note mode. Possible values are
     * <TT>AccidentalType.SHARP</TT>, <TT>AccidentalType.NATURAL</TT>?
     * <TT>AccidentalType.NONE</TT>, or <TT>AccidentalType.FLAT</TT>.
     * @param modeType The type of the mode. Possible values are
     * <TT>AEOLIAN</TT>, <TT>DORIAN</TT>, <TT>IONIAN</TT>, <TT>LOCRIAN</TT>,
     * <TT>LYDIAN</TT>, <TT>MAJOR</TT>, <TT>MINOR</TT>, <TT>MIXOLYDIAN</TT>,
     * <TT>PHRYGIAN</TT> or <TT>OTHER</TT>.
     * @exception IllegalArgumentException Thrown if keyAccidental or modeType
     * are out of the allowed values. */
    public KeySignature (byte keyNoteType, byte keyAccidental, byte modeType)
    {
      if (!(keyAccidental==AccidentalType.FLAT ||
          keyAccidental==AccidentalType.NATURAL ||
          keyAccidental==AccidentalType.SHARP ||
          keyAccidental==AccidentalType.NONE))
         throw new IllegalArgumentException ("Key accidental type should be AccidentalType.FLAT, AccidentalType.NATURAL, AccidentalType.SHARP or AccidentalType.NONE");
       if (!(modeType==AEOLIAN	|| modeType==DORIAN || modeType==IONIAN
             ||	modeType==LOCRIAN || modeType==LYDIAN || modeType==MAJOR
             || modeType==MINOR	|| modeType==MIXOLYDIAN || modeType==PHRYGIAN
             || modeType==OTHER	))
         throw new IllegalArgumentException ("Mode type must be choose among AEOLIAN, DORIAN, IONIAN, LOCRIAN, LYDIAN, MAJOR, MINOR, MIXOLYDIAN, PHRYGIAN or OTHER");
      key = keyNoteType;
      mode = modeType;
      m_keyAccidental = keyAccidental;
      keyIndex=0;
      if (key == Note.D) keyIndex = keyIndex + 2;
      else if (key == Note.E) keyIndex = keyIndex + 4;
      else if (key == Note.F) keyIndex = keyIndex + 5;
      else if (key == Note.G) keyIndex = keyIndex + 7;
      else if (key == Note.A) keyIndex = keyIndex + 9;
      else if (key == Note.B) keyIndex = keyIndex + 11;
      if (m_keyAccidental == AccidentalType.SHARP) keyIndex = keyIndex + 1;
      else if (m_keyAccidental == AccidentalType.FLAT) keyIndex = keyIndex - 1;
      if (modeType == MINOR) keyIndex = keyIndex + 3;
      else if (modeType == LYDIAN) keyIndex = keyIndex + 7;
      else if (modeType == MIXOLYDIAN) keyIndex = keyIndex + 5;
      else if (modeType == DORIAN) keyIndex = keyIndex + 10;
      else if (modeType == AEOLIAN)keyIndex = keyIndex + 3;
      else if (modeType == PHRYGIAN) keyIndex = keyIndex + 8;
      else if (modeType == LOCRIAN) keyIndex = keyIndex + 1;
      // this + 12 % 12 is a workaound to express key signature 
      // that are expressed with notes such as Cb (=B) or E# (=F)
      // Before this fix, keys such as Cb was causing crash (because
      // keyIndex was equals to -1 in that case.
      keyIndex = (keyIndex+12) % 12;
      if (keyIndex==6 && keyAccidental==AccidentalType.FLAT) //Gb (The C is flat)
    		  accidentals = accidentalRulesFlat[keyIndex];
    	  else
    		  if (keyIndex==6 &&  keyAccidental==AccidentalType.SHARP) //F# (The E is sharp)
    			  accidentals = accidentalRulesSharp[keyIndex];
    		  else
    			  if (keyIndex==6 &&  keyAccidental==AccidentalType.NATURAL)
    				  throw new RuntimeException("Cannot map " + keyNoteType + "/" + keyAccidental + "/" + modeType + " to a key signature");
    			  else
    				  if (keyIndex==11 && keyAccidental==AccidentalType.FLAT) // for Cb
    					  accidentals = accidentalRulesFlat[keyIndex];
    				  else
    					  if (keyIndex==1 && keyAccidental==AccidentalType.SHARP) // C#
    						  accidentals = accidentalRulesSharp[keyIndex];
    					  else
    						  accidentals = accidentalsRules [keyIndex]; //apply normal rule.
    }

    /** Creates a key signature with the specified accidentals.
     * @param accidentalsDefinition Accidental definition from note
     * C to B. Possible values for accidentals are :
     * <TT>AccidentalType.SHARP</TT>, <TT>AccidentalType.NATURAL</TT>
     * or <TT>AccidentalType.FLAT</TT>. */
    public KeySignature (byte[] accidentalsDefinition)
    {
      byte[] newArray = new byte[7];
      System.arraycopy(accidentalsDefinition, 0, newArray, 0, 7);
      accidentals = newArray;
    }

    /** Returns the note of the mode.
     * @return The note of the mode. Possible values are
     * <TT>Note.A</TT>, <TT>Note.B</TT>, <TT>Note.C</TT>, <TT>Note.D</TT>,
     * <TT>Note.E</TT>, <TT>Note.F</TT> or <TT>Note.G</TT>. */
    public byte getNote()
    { return key; }

    /** Returns the mode of this key.
     * @return The mode of this key. Possible values are <TT>AEOLIAN</TT>,
     * <TT>DORIAN</TT>, <TT>IONIAN</TT>, <TT>LOCRIAN</TT>, <TT>LYDIAN</TT>,
     * <TT>MAJOR</TT>, <TT>MINOR</TT>, <TT>MIXOLYDIAN</TT>, <TT>PHRYGIAN</TT>
     * or <TT>OTHER</TT>. */
    public byte getMode()
    { return mode; }

    /** Returns key accidental for this Key.
     * @return This key's key accidental. Ex: for "Ab aeolian", the key
     * accidental is "b" (flat) */
    public byte getAccidental ()
    { return m_keyAccidental; }

    /** Returns accidentals of this key signature.
     * @return accidentals of this key signature. Index 0 correspond to 
     * accidental for C, 1 to accidental for D and so on up to B.*/
    public byte[] getAccidentals ()
    { return accidentals; }

    /** Sets the accidental for the specified note.
     * @param noteHeigth The note heigth. Possible values are,
     * <TT>Note.A</TT>, <TT>Note.B</TT>, <TT>Note.C</TT>, <TT>Note.D</TT>,
     * <TT>Note.E</TT>, <TT>Note.F</TT> or <TT>Note.G</TT>.
     * @param accidental The accidental to be set to the note. Possible values are :
     * <TT>AccidentalType.SHARP</TT>, <TT>AccidentalType.NATURAL</TT>
     * or <TT>AccidentalType.FLAT</TT>.
     * @exception IllegalArgumentException Thrown if an invalid note heigth or
     * accidental has been given. */
    public void setAccidental(byte noteHeigth, byte accidental) throws IllegalArgumentException
    {
      int index = 0;
      noteHeigth = (byte)(noteHeigth%12);
      if (noteHeigth==Note.C) index=0;
      else if (noteHeigth==Note.D) index=1;
      else if (noteHeigth==Note.E) index=2;
      else if (noteHeigth==Note.F) index=3;
      else if (noteHeigth==Note.G) index=4;
      else if (noteHeigth==Note.A) index=5;
      else if (noteHeigth==Note.B) index=6;
      else
        throw new IllegalArgumentException("Invalid note heigth : " + noteHeigth);

      if (accidental==AccidentalType.NATURAL || accidental==AccidentalType.SHARP || accidental==AccidentalType.FLAT)
        accidentals[index] = accidental;
      else
        throw new IllegalArgumentException("Accidental type should be SHARP, FLAT or NATURAL");
    }

    /** Returns accidental for the specified note heigth for this key.
     * @param noteHeigth A note heigth among <TT>Note.C</TT>, <TT>Note.D</TT>,
     * <TT>Note.E</TT>, <TT>Note.F</TT>, <TT>Note.G</TT>, <TT>Note.A</TT>,
     * <TT>Note.B</TT>.
     * @return Accidental value for the specified note heigth. This value can be
     * <TT>NATURAL</TT>, <TT>FLAT</TT> or <TT>SHARP</TT>.
     * @exception IllegalArgumentException Thrown if the specified note heigth
     * is invalid. */
    public byte getAccidentalFor (byte noteHeigth)
    {
      int index = 0;
      if (noteHeigth==Note.C) index=0;
      else if (noteHeigth==Note.D) index=1;
      else if (noteHeigth==Note.E) index=2;
      else if (noteHeigth==Note.F) index=3;
      else if (noteHeigth==Note.G) index=4;
      else if (noteHeigth==Note.A) index=5;
      else if (noteHeigth==Note.B) index=6;
        else throw new IllegalArgumentException("Invalid note heigth : " + noteHeigth);
      return accidentals[index];
    }
    
    /* FIXME: rename to hasSharpsDominant or something like that.
     * Doesn't work for K:Dm ^c */
    public boolean hasOnlySharps(){
    	return ((keyIndex==1 && m_keyAccidental==AccidentalType.SHARP) || keyIndex==2 || keyIndex==4 
    			|| (keyIndex==6 && m_keyAccidental==AccidentalType.SHARP) 
    			|| keyIndex==7 || keyIndex== 9 || (keyIndex==11 && m_keyAccidental==AccidentalType.NATURAL));     
    }
    
    /* FIXME: rename to hasFlatsDominant or something like that.
     * Doesn't work for K:Dm ^c */
    public boolean hasOnlyFlats(){
    	return ((keyIndex==1 && m_keyAccidental==AccidentalType.FLAT) || keyIndex==3 || keyIndex==5 
    			|| (keyIndex==6 && m_keyAccidental==AccidentalType.FLAT) || keyIndex==8 || keyIndex== 10
    			|| (keyIndex==11 && m_keyAccidental==AccidentalType.FLAT));     
    }
    
    /**
     * Returns <TT>true</TT> if the key contains sharps <B>and</B>
     * flats (e.g.: oriental scale <TT>K:Dm ^c</TT> in ABC format).
     * Returns <TT>false</TT> otherwise.
     * @return
     */
    public boolean hasSharpsAndFlats() {
    	boolean hasSharp = false, hasFlat = false;
    	for (int i = 0; i < accidentals.length; i++) {
			hasSharp = hasSharp || accidentals[i] == AccidentalType.SHARP;
			hasFlat = hasFlat || accidentals[i] == AccidentalType.FLAT;
		}
    	return hasSharp && hasFlat;
    }
    
    public String toLitteralNotation()
    {
      String notation = "";
      if (key==Note.A) notation = notation.concat("A");
      else if (key==Note.B) notation = notation.concat("B");
      else if (key==Note.C) notation = notation.concat("C");
      else if (key==Note.D) notation = notation.concat("D");
      else if (key==Note.E) notation = notation.concat("E");
      else if (key==Note.F) notation = notation.concat("F");
      else if (key==Note.G) notation = notation.concat("G");
      if (m_keyAccidental==AccidentalType.FLAT) notation = notation.concat("b");
      else if (m_keyAccidental==AccidentalType.SHARP) notation = notation.concat("#");
      if (mode==AEOLIAN) notation = notation.concat("aeo");
      else if (mode==DORIAN) notation = notation.concat("dor");
      else if (mode==IONIAN) notation = notation.concat("ion");
      else if (mode==LOCRIAN) notation = notation.concat("loc");
      else if (mode==LYDIAN) notation = notation.concat("lyd");
      else if (mode==MAJOR) notation = notation.concat("maj");
      else if (mode==MINOR) notation = notation.concat("min");
      else if (mode==MIXOLYDIAN) notation = notation.concat("mix");
      else if (mode==PHRYGIAN) notation = notation.concat("phr");
      return notation;
    }

    public static byte convertToModeType(String mode)
    {
      if ("AEO".equalsIgnoreCase(mode)) return KeySignature.AEOLIAN;
      else if ("DOR".equalsIgnoreCase(mode)) return KeySignature.DORIAN;
      else if ("ION".equalsIgnoreCase(mode)) return KeySignature.IONIAN;
      else if ("LOC".equalsIgnoreCase(mode)) return KeySignature.LOCRIAN;
      else if ("LYD".equalsIgnoreCase(mode)) return KeySignature.LYDIAN;
      else if ("MAJ".equalsIgnoreCase(mode)) return KeySignature.MAJOR;
      else if ("MIN".equalsIgnoreCase(mode) || ("M".equalsIgnoreCase(mode))) return KeySignature.MINOR;
      else if ("MIX".equalsIgnoreCase(mode)) return KeySignature.MIXOLYDIAN;
      else if ("PHR".equalsIgnoreCase(mode)) return KeySignature.PHRYGIAN;
      else return -1;
    }

  public static byte convertToAccidentalType(String accidental) throws IllegalArgumentException
  {
    if (accidental==null) return AccidentalType.NATURAL;
    else if (accidental.equals("#")) return AccidentalType.SHARP;
    else if (accidental.equals("b")) return AccidentalType.FLAT;
    else throw new IllegalArgumentException(accidental + " is not a valid accidental");
  }

  /** Returns a String representation of this key.
   * @return A String representation of this key. */
  public String toString()
  {
    String string2Return = "{";
    for (int i= 0; i<7; i++)
    {
      switch (accidentals[i])
      {
          case AccidentalType.NATURAL : string2Return = string2Return.concat("AccidentalType.NATURAL"); break;
      case AccidentalType.FLAT : string2Return = string2Return.concat("AccidentalType.FLAT"); break;
      case AccidentalType.SHARP : string2Return = string2Return.concat("AccidentalType.SHARP"); break;
      default : break;
      }
      if(i!=6)
        string2Return = string2Return.concat(", ");
    }
    string2Return = string2Return.concat("}");
    return string2Return;
  }
  

	public boolean equals(Object o) {
		if (o instanceof KeySignature){
			KeySignature oKey = (KeySignature) o;
			if ((this.key != oKey.key)
					|| (this.keyIndex != oKey.keyIndex)
					|| (this.m_keyAccidental != oKey.m_keyAccidental)
					|| (this.mode != oKey.mode)
				) {
				return false;
			}
			for (int i = 0; i < accidentals.length; i++) {
				if (accidentals[i] != oKey.accidentals[i])
					return false;
			}
			return true;
		}
		else
			return super.equals(o);
	}

  	public Object clone() {
  		KeySignature k = new KeySignature(this.getNote(), this.getAccidental(), this.getMode());
  		k.accidentals = accidentals;
  		return k;
  	}

/*    public void display ()
    {
        System.out.println (key +  " " + m_keyAccidental + " " + mode);
    }*/
}

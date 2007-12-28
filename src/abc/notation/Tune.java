package abc.notation;

import java.util.Hashtable;
import java.util.Vector;

import scanner.CharStreamPosition;
import scanner.PositionableInCharStream;

/** This class encapsulates all information retrieved from a tune transcribed
 * using abc notation : header and music. */
public class Tune implements Cloneable
{
  //    Field name                     header this elsewhere Used by Examples and notes
  private String m_area = null;           //yes                           A:Donegal, A:Bampton
  private String m_book = null;           //yes         yes       archive B:O'Neills
  private String m_composer = null;       //yes                           C:Trad.
  private String m_discography = null;    //yes                   archive D:Chieftans IV
  //private String m_fileName = null;       //            yes               see index.tex
  private String m_group = null;          //yes         yes       archive G:flute
  private String m_history = null;        //yes         yes       archive H:This this said to ...
  private String m_information = null;    //yes         yes       playabc
  private KeySignature m_key = null;    //yes         yes       playabc
  private String m_notes = null;          //yes                           N:see also O'Neills - 234
  private String m_origin = null;         //yes         yes       index   O:I, O:Irish, O:English
  private String m_rhythm = null;         //yes         yes       index   R:R, R:reel
  private String m_source = null;         //yes                           S:collected in Brittany
  private int m_referenceNumber = -1;    //first                         X:1, X:2
  private String m_transcriptionNotes = null;//yes                         Z:from photocopy
  private int m_elemskip = 0;            //yes    yes                    see Line Breaking
  private Vector m_titles;              //second yes                    T:Paddy O'Rafferty
  //private AbcMultiPartsDefinition abcMultiPartsDefinition = null;  //yes    yes                    P:ABAC, P:A, P:B
  //private AbcScore m_abcScore = null;
  private Part m_defaultPart = null;
  /** the multi parts definition of this tune if composed of several parts.
   * If this tune is a one-part tune, this attribtue is <TT>null</TT> */
  private MultiPartsDefinition m_multiPartsDef = null;
  private Hashtable m_parts = null;

  /** Creates a new empty tune. */
  public Tune() {
    super();
    m_titles = new Vector();
    m_defaultPart = new Part(this, ' ');
  }
  
  /** Copy constructor
   * @param tune The tune to be copied in depth. */
  public Tune(Tune tune) {
	  this.m_area = tune.m_area;
	  this.m_book = tune.m_book;
	  this.m_composer = tune.m_composer;
	  this.m_discography = tune.m_discography;
	  this.m_group = tune.m_group;
	  this.m_history = tune.m_history;
	  this.m_information = tune.m_information;
	  this.m_key = (KeySignature)tune.m_key.clone();
	  this.m_notes = tune.m_notes;
	  this.m_origin = tune.m_origin;
	  this.m_rhythm = tune.m_rhythm;
	  this.m_source = tune.m_source;
	  this.m_referenceNumber = tune.m_referenceNumber;
	  this.m_transcriptionNotes = tune.m_transcriptionNotes;
	  this.m_elemskip = tune.m_elemskip;
	  this.m_titles = (Vector)tune.m_titles.clone();
	  this.m_parts = (Hashtable)tune.m_parts.clone(); 
	  this.m_defaultPart = (Part)tune.m_defaultPart.clone();
	  this.m_multiPartsDef = (MultiPartsDefinition)tune.m_multiPartsDef.clone();
  }

  /** Sets the geographic area where this tune comes from.
   * Corresponds to the "A:" abc field.
   * Ex: A:Donegal, A:Bampton
   * @param area The area where this tune comes from. */
  public void setArea(String area)
  {m_area = area; }

  /** Returns the area where this tune comes from.
   * @return The area where this tune comes from.
   * <TT>null</TT> if the area hasn't been specified. */
  public String getArea()
  { return m_area; }

  /** Sets the list of publications where
   * this tune can be found.
   * Corresponds to the "B:" abc field.
   * Ex: B:O'Neills
   * @param book The book where this tune comes from. */
  public void setBook(String book)
  { m_book = book; }

  /** Returns the list of publications where this
   * tune can be found.
   * @return Returns the list of publications where
   * this tune can be found,
   * <TT>null</TT> if the book hasn't been specified. */
  public String getBook()
  { return m_book; }

  /** Sets the composer of this tune.
   * Corresponds to the "C:" abc field.
   * Ex: C:Paddy Fahey
   * @param composer The composer who wrotes this tune.
   * For tunes known as traditional, you can use "traditional"
   * as parameter so that that people don't think the composer
   * has just been ignored. */
  public void setComposer(String composer)
  { m_composer = composer; }

  /** Returns the composer of this tune.
   * @return The composer of this tune,
   * <TT>null</TT> if the composer hasn't been specified. */
  public String getComposer()
  { return m_composer; }

  /** Sets recordings where this tune appears.
   * Corresponds to the "D:" abc field.
   * Ex: D:Gwenojenn
   * @param discography Recordings where this tune appears. */
  public void setDiscography(String discography)
  { m_discography = discography; }

  /** Returns recordings where this tune appears.
   * @return recordings where this tune appears,
   * <TT>null</TT> if the discography hasn't been specified. */
  public String getDiscography()
  { return m_discography; }

  public void setElemskip(int value)
  { m_elemskip = value; }

  public int getElemskip()
  { return m_elemskip; }

  public void setGroup(String value)
  { m_group = value; }

  public String getGroup()
  { return m_group; }

  /** Adds historical information about the tune.
   * Corresponds to the "H:" abc field.
   * Ex: H:Composed in 1930
   * @param history Historical information about
   * the tune to be added. */
  public void addHistory(String history)
  {
    if (m_history==null)
      m_history = new String(history);
    else
      m_history = m_history.concat(history + "\n");
  }

  /** Returns historical information about the tune.
   * @return Historical information about the tune,
   * <TT>null</TT> if no historical information about
   * the tune is provided. */
  public String getHistory()
  { return m_history; }

  /**Sets the key signature of this tune.
   * @param key The key signature of this tune. */
  void setKey(KeySignature key)
  { m_key = key; }

  /** Returns the key signature of this tune.
   * @return The key signature of this tune. */
  public KeySignature getKey()
  { return m_key; }

  /** Sets additional information about the tune.
   * @param information Additional information about the tune. */
  public void setInformation(String information)
  { m_information = information; }

  /** Returns additional information about the tune.
   * @return Additional information about the tune,
   * <TT>null</TT> if no additional information about
   * the tune is provided. */
  public String getInformation()
  { return m_information; }

  /** Sets notes concerning the transcription of this tune.
   * Corresponds to the "N:" abc field.
   * Ex: N:see also O'Neills - 234
   * @param notes Notes concerning the transcription of this tune. */
  public void setNotes(String notes)
  { m_notes = notes; }

  /** Returns notes concerning the transcription of this tune.
   * @return Notes concerning the transcription of this tune,
   * <TT>null</TT> if no transcription notes about
   * the tune is provided. */
  public String getNotes()
  { return m_notes; }

  /** Sets the origin of this tune.
   * Corresponds to the "O:" abc field.
   * Ex: O:Irish, O:English
   * @param origin Origin of this tune : place or a person
   * that the music came from. N.B: For a person, setSource
   * is probably better.
   * @see #setSource(java.lang.String)*/
  public void setOrigin(String origin)
  { m_origin = origin; }

  /** Returns the origin of this tune.
   * @return The origin of this tune.
   * <TT>null</TT> if no origin about
   * the tune is provided. */
  public String getOrigin()
  { return m_origin; }

  /** Returns the part of the tune identified by the given label.
   * @param partLabel A part label.
   * @return The part of the tune identified by the given label, <TT>null</TT>
   * if no part with the specified label exists in this tune. */
  public Part getPart(char partLabel)
  {
    if (m_parts!=null)
    {
      Part p = (Part)m_parts.get(new Character(partLabel));
      return p;
    }
    else
      return null;
  }

  /** Creates a new part in this tune and returns it.
   * @param partLabel The label defining this new tune part.
   * @return The new part properly labeled. */
  public Part createPart(char partLabel) {
	  // check should be requiered to see if the label is not 
	  // empty or blank character because the blank character is
	  // used as flag for the default part.
	  Part part = new Part(this, partLabel);
	  if (m_parts==null) m_parts = new Hashtable();
	  m_parts.put(new Character(partLabel), part);
	  return part;
  }

  /** Sets the multi parts definition of this tune.
   * @param multiPartsDef The multi parts definition of this tune : defines
   * how parts should be played. */
  public void setMultiPartsDefinition(MultiPartsDefinition multiPartsDef)
  { m_multiPartsDef= multiPartsDef; }

  /** Returns the multi parts definition of this tune.
   * @return The multi parts definition of this tune. <TT>null</TT> is returned
   * if this tuned isn't composed of several parts. */
  public MultiPartsDefinition getMultiPartsDefinition()
  { return m_multiPartsDef; }

  /** Sets the rhythm of this tune.
   * Corresponds to the "R:" abc field.
   * Ex: R:hornpipe
   * @param rhythm Type of rhythm of this tune.
   * @see #getRhythm() */
  public void setRhythm(String rhythm)
  { m_rhythm = rhythm; }

  /** Returns the rhythm of this tune.
   * @return The rhythm of this tune,
   * <TT>null</TT> if no rhythm about
   * the tune is provided.
   * @see #setRhythm(java.lang.String)*/
  public String getRhythm()
  {return m_rhythm; }

  /** Sets the source of this tune.
   * Corresponds to the "S:" abc field.
   * Ex: S:collected in Brittany
   * @param source The source of this tune (place where
   * it has been collected for ex). */
  public void setSource(String source)
  { m_source = source; }

  /** Returns the source of this tune.
   * @return The source of this tune. <TT>null</TT> if no source is provided. */
  public String getSource()
  { return m_source; }

  /** Adds a title to this tune.
   * Corresponds to the "T:" abc field.
   * Ex: T:Dansaone
   * @param title A title for this tune. */
  public void addTitle(String title)
  { m_titles.addElement(title); }

  /** Removes one the titles of this tune.
   * @param title The title to be removed of this tune. */
  public void removeTitle(String title)
  { m_titles.removeElement(title); }

  /** Returns the titles of this tune.
   * @return An array containing the titles of this tune. If this tune has no
   * title, <TT>null</TT> is returned. */
  public String[] getTitles()
  {
    String[] titles = null;
    if (m_titles.size()!=0)
    {
      titles = new String[m_titles.size()];
      for (int i=0; i<m_titles.size(); i++)
        titles[i]=(String)m_titles.elementAt(i);
    }
    return titles;
  }

  /** Sets the reference number of this tune.
   * @param id The reference number of this tune. */
  public void setReferenceNumber(int id)
  { m_referenceNumber = id; }

  /** Returns the reference number of this tune.
   * @return The reference number of this tune. */
  public int getReferenceNumber()
  { return m_referenceNumber; }

  /** Adds notes about transcription of this tune.
   * Corresponds to the "Z:" abc field.
   * Ex: Z:collected in Brittany
   * @param transciptionNotes notes about about who did the ABC
   * transcription : email addresses and URLs are appropriate here,
   * and other contact information such as phone numbers or postal
   * addresses may be included. */
  public void addTranscriptionNotes(String transciptionNotes)
  {
    if (m_transcriptionNotes==null)
      m_transcriptionNotes = new String(transciptionNotes);
    else
      m_transcriptionNotes = m_transcriptionNotes.concat(transciptionNotes + "\n");
  }

  /** Returns transcription notes of this tune.
   * @return Transcription notes of this tune. */
  public String getTranscriptionNotes()
  { return m_transcriptionNotes; }

  /** Returns the score of this tune.
   * @return The score of this tune. If this tune isn't composed of several parts
   * this method returns the "normal" score. If this tune is composed of several
   * parts the returned is generated so that the tune looks like a "single-part"
   * one. If you want to retrieve the score related to each part separatly just
   * do <TT>getPart(char partLabel).getScore()</TT>.
   * @see #getPart(char) */
  public Score getScore()
  {
    if (m_multiPartsDef==null)
      return (m_defaultPart.getScore());
    else
    {
      Score globalScore = new Score();
      Score defaultScore = m_defaultPart.getScore();
      for (int i=0; i<defaultScore.size(); i++)
        globalScore.addElement(defaultScore.elementAt(i));
      Part[] parts = m_multiPartsDef.toPartsArray();
      for (int i=0; i<parts.length; i++)
      {
        Score score = parts[i].getScore();
        for (int j=0; j<score.size(); j++)
          globalScore.addElement(score.elementAt(j));
      }
      return globalScore;
    }
  }
  
  /** Returns a string representation of this tune.
   * @return A string representation of this tune. */
  public String toString()
  {
    String string2return = "";
    if (m_titles.size()!=0)
      string2return = m_titles + "(" + m_referenceNumber + ")@" + hashCode();
    else
      string2return = "(" + m_referenceNumber + ")@" + hashCode();
    return string2return;
  }
  
  	public Object clone() {
  		return new Tune(this);
  	}

  /** Creates a new score. 
   * NB : bullshit pattern, why going through a tune to create a score???
   * The reference to the tune is not kept in the score. */
  Score createScore()
  { return new Score(); }

  public class Score extends Vector {
	  
	  protected NoteAbstract lastNote = null; 
    public Score ()
    { super (); }

    public void addElement(KeySignature key) {
      if (Tune.this.getKey()==null)
        Tune.this.setKey(key);
      super.addElement(key);
    }
    
    public void addElement(NoteAbstract note) {
    	//System.out.println("adding note " + note + " to " + this);
        lastNote = note;
        super.addElement(note);
    }
    
    /* Returns the last note that has been added to this score.
     * @return The last note that has been added to this score. <TT>null</TT>
     * if no note in this score. */
    public NoteAbstract getLastNote() {
    	return lastNote;
    }
    
    /** Returns the score element location at the specified offset.
     * @param offset An offset in a char stream.  
     * @return The score element location at the specified offset.
     */  
    public ScoreElementInterface getElementAt(int offset) {
    	ScoreElementInterface foundElement = null;
    	ScoreElementInterface current = null;
    	for (int i=0; i<size(); i++) {
    		current = (ScoreElementInterface)elementAt(i);
    		if (current instanceof PositionableInCharStream) {
    			PositionableInCharStream pos = (PositionableInCharStream)current; 
    			if (pos.getPosition().getCharactersOffset()<=offset && 
    					(pos.getPosition().getCharactersOffset()+ pos.getLength())>=offset 
    					)
    				foundElement=current;
    		}
    	}
    	return foundElement;
    }
    
    public int indexOf(ScoreElementInterface elmnt) {
    	Object elmntIt = null;
    	boolean isLooking4Note = elmnt instanceof Note;
    	for (int i=0; i<size(); i++){
    		elmntIt = elementAt(i);
    		if (elementAt(i) instanceof MultiNote && isLooking4Note) {
    			if (((MultiNote)elmntIt).contains((Note)elmnt))
    					return i;
    		}
    		else
    			if (elementAt(i).equals(elmnt))
    				return i;
    	}
    	return -1;
    }
    
    /*public Note getHighestNoteBewteen(int scoreElmntIndexBegin, int ScoreElmtIndexEnd) throws IllegalArgumentException {
    	if (scoreElmntIndexBegin>ScoreElmtIndexEnd)
    		throw new IllegalArgumentException("First parameter " + scoreElmntIndexBegin + " must be located before " + ScoreElmtIndexEnd + " in the score");
    	Note highestNote = null;
    	ScoreElementInterface currentScoreEl;
    	for (int i=scoreElmntIndexBegin; i<=ScoreElmtIndexEnd; i++) {
    		currentScoreEl=(ScoreElementInterface)elementAt(i);
    		if (currentScoreEl instanceof Note && (highestNote==null || ((Note)currentScoreEl).isHigherThan(highestNote)))
    			highestNote = (Note)currentScoreEl;
    	}
    	return highestNote;
    }*/
    
    /** Returns the highest note between two score elements. <TT>MultiNote</TT> instances
     * are ignored.  
     * @param elmtBegin The score element where to start (included) the search
     * of the highest note.
     * @param elmtEnd The score element where to end (included) the search
     * of the highest note.
     * @return The highest note between two score elements. <TT>null</TT> if
     * no note has been found between the two score elements.
     * @throws IllegalArgumentException Thrown if one of the score elements hasn't been found 
     * in the score or if the <TT>elmtEnd</TT> param is located before the <TT>elmntBegin</TT> 
     * param in the score. */
    public Note getHighestNoteBewteen(ScoreElementInterface elmtBegin, ScoreElementInterface elmtEnd)
    	throws IllegalArgumentException {
    	Note highestNote = null;
    	//init
    	if (elmtBegin instanceof Note)
    		highestNote=(Note)elmtBegin;
    	else
    		if (elmtBegin instanceof MultiNote)
    			highestNote = ((MultiNote)elmtBegin).getHighestNote();
    	int idxBegin = indexOf(elmtBegin);
    	int idxEnd = this.indexOf(elmtEnd);
    	if (idxBegin==-1)
    		throw new IllegalArgumentException("Note " + elmtBegin + " hasn't been found in tune");
    	if (idxEnd==-1)
        	throw new IllegalArgumentException("Note " + elmtEnd + " hasn't been found in tune");
    	if (idxBegin>idxEnd)
    		throw new IllegalArgumentException("Note " + elmtBegin + " is located after " + elmtEnd + " in the score");
    	ScoreElementInterface currentScoreEl;
    	for (int i=idxBegin+1; i<=idxEnd; i++) {
    		currentScoreEl=(ScoreElementInterface)elementAt(i);
    		if (currentScoreEl instanceof Note && ((Note)currentScoreEl).isHigherThan(highestNote))
    			highestNote = (Note)currentScoreEl;
    		/*else
    			if (currentScoreEl instanceof MultiNote && ((MultiNote)currentScoreEl).getLowestNote().isHigherThan(highestNote))
    					highestNote = ((MultiNote)currentScoreEl).getLowestNote();*/
    	}
    	return highestNote;
    }

    /** <TT>MultiNote</TT> instances are ignored.  
     * @param noteBegin (included)
     * @param noteEnd (included)
     * @return The lowest note between the two given score elements if found.
     * @throws IllegalArgumentException
     */
    public Note getLowestNoteBewteen(ScoreElementInterface noteBegin, ScoreElementInterface noteEnd)
		throws IllegalArgumentException {
    	Note lowestNote = null;
    	//init
    	if (noteBegin instanceof Note)
    		lowestNote=(Note)noteBegin;
    	else
    		if (noteBegin instanceof MultiNote)
    			lowestNote = ((MultiNote)noteBegin).getLowestNote();
    	int idxBegin = indexOf(noteBegin);
    	int idxEnd = this.indexOf(noteEnd);
    	if (idxBegin==-1)
    		throw new IllegalArgumentException("Note " + noteBegin + " hasn't been found in tune");
    	if (idxEnd==-1)
        	throw new IllegalArgumentException("Note " + noteEnd + " hasn't been found in tune");
    	if (idxBegin>idxEnd)
    		throw new IllegalArgumentException("Note " + noteBegin + " is located after " + noteEnd + " in the score");
    	ScoreElementInterface currentScoreEl;
    	for (int i=idxBegin+1; i<=idxEnd; i++) {
    		currentScoreEl=(ScoreElementInterface)elementAt(i);
    		if (currentScoreEl instanceof Note && ((Note)currentScoreEl).isLowerThan(lowestNote))
    			lowestNote = (Note)currentScoreEl;
    		/* else
    			if (currentScoreEl instanceof MultiNote && ((MultiNote)currentScoreEl).getLowestNote().isLowerThan(lowestNote))
    					lowestNote = ((MultiNote)currentScoreEl).getLowestNote();*/
    	}
    	return lowestNote;

	}
    
    /*public Note getLowestNoteBewteen(int scoreElmntIndexBegin, int ScoreElmtIndexEnd) throws IllegalArgumentException {
    	if (scoreElmntIndexBegin>ScoreElmtIndexEnd)
    		throw new IllegalArgumentException("First parameter " + scoreElmntIndexBegin + " must be located before " + ScoreElmtIndexEnd + " in the score");
    	Note lowestNote = null;
    	ScoreElementInterface currentScoreEl;
    	for (int i=scoreElmntIndexBegin; i<=ScoreElmtIndexEnd; i++) {
    		currentScoreEl=(ScoreElementInterface)elementAt(i);
    		if (currentScoreEl instanceof Note && (lowestNote==null || ((Note)currentScoreEl).isLowerThan(lowestNote)))
    			lowestNote = (Note)currentScoreEl;
    		else
    			if (currentScoreEl instanceof MultiNote 
    					&& (lowestNote==null || ((MultiNote)currentScoreEl).getLowestNote().isLowerThan(lowestNote)))
    					lowestNote = ((MultiNote)currentScoreEl).getLowestNote();
    	}
    	return lowestNote;
    }*/
  }
}

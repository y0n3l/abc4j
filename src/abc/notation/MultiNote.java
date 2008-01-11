package abc.notation;

import java.util.Vector;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
/** A multi note is a group of notes that should be played together. */
public class MultiNote extends NoteAbstract
{
  /** Notes contained in this multinote. */
  private Vector m_notes;

	/** Creates a new <TT>MultiNote</TT> from given notes.
	 * @param notes A Vector containing the <TT>NoteAbstract</TT> of this
	 * <TT>MultiNote</TT>. */
  	public MultiNote (Vector notes) {
		super();
		m_notes = fromLowestToHighest(notes);
  	}
  	
  	public boolean contains(Note aNote) {
  		return (m_notes.indexOf(aNote)!=-1);
  	}

  /** Returns the longest note of this multi note, based on its duration.
   * @return The longest note of this multi note. If several notes have the
   * same longest length, the first one is returned.
   * @see Note#getDuration() */
  public Note getLongestNote() {
	  /*float length = 0;
	  float currentNoteLength = 0;
	  Note maxNote = null;
	  for (int i=0; i<m_notes.size() && maxNote==null; i++) {
		  currentNoteLength = ((Note)(m_notes.elementAt(i))).getDuration();
		  if (currentNoteLength > length)
			  maxNote = (Note)m_notes.elementAt(i);
	  }
	  return maxNote;*/return getLongestNote(this.toArray());
  }
  
  public static Note getLongestNote(Note[] notes) {
	  float length = 0;
	  float currentNoteLength = 0;
	  Note maxNote = null;
	  for (int i=0; i<notes.length && maxNote==null; i++) {
		  currentNoteLength = notes[i].getDuration();
		  if (currentNoteLength > length)
			  maxNote = notes[i];
	  }
	  return maxNote;
  }
  
  /*public static Note[] getNotesLongerThan(int aNoteDuration) {
	  return null;
  }*/
  
  /**
   * strictly Shorter than ! and uses strict duration
   */
  public static Note[] getNotesShorterThan(Note[] notes, int aNoteDuration) {
	  Vector shorterNotes = new Vector();
	  Note[] notesArray =null;
	  for (int i=0; i<notes.length; i++) {
		  if (notes[i].getStrictDuration()<aNoteDuration)
			  shorterNotes.addElement(notes[i]);
	  }
	  if (shorterNotes.size()>0) {
		  notesArray = new Note[shorterNotes.size()];
		  shorterNotes.toArray(notesArray);
	  }
	  return notesArray;
  }
  
  public static Note getLowestNote(Note[] notes) {
	  Note lowestNote = notes[0];
	  for (int i=1; i<notes.length; i++)
		  if (notes[i].isLowerThan(lowestNote))
			  lowestNote = notes[i];
	  return lowestNote;
  }
  
  public static Note getHighestNote(Note[] notes) {
	  Note highestNote = notes[0];
	  for (int i=1; i<notes.length; i++)
		  if (notes[i].isHigherThan(highestNote))
			  highestNote = notes[i];
	  return highestNote;
  }
  
  /** Returns the shortest note of this multi note, based on its duration.
   * @return The shortest note of this multi note. If several notes have the
   * same shortest length, the first one is returned.
   * @see Note#getDuration() */
  public Note getShortestNote() {
	  Note shortNote = (Note)m_notes.elementAt(0);
	  float length = shortNote.getDuration();
	  float currentNoteLength = 0;
	  for (int i=0; i<m_notes.size() && shortNote==null; i++) {
		  currentNoteLength = ((Note)(m_notes.elementAt(i))).getDuration();
		  if (currentNoteLength < length)
			  shortNote = (Note)m_notes.elementAt(i);
	  }
	  return shortNote;
  }
  
  /** Returns the highest note among the notes composing this multi note.
   * @return The highest note among the notes composing this multi note.
   * @see #getLowestNote()
   * @see Note#isHigherThan(Note) */
  public Note getHighestNote() {
	  Note highestNote = (Note)m_notes.elementAt(0);
	  //short highestHeight = highestNote.getHeight();
	  //short currentNoteLength = 0;
	  for (int i=1; i<m_notes.size(); i++)
		  if (((Note)(m_notes.elementAt(i))).isHigherThan(highestNote))
			  highestNote = (Note)m_notes.elementAt(i);
	  return highestNote;
  }
  
  public Note[] getNotesBeginningTie() {
	  Vector notesBT = new Vector();
	  for (int i=0; i<m_notes.size(); i++) 
		  if (((Note)m_notes.elementAt(i)).isBeginningTie())
			  notesBT.addElement(m_notes.elementAt(i));
	  if (notesBT.size()>0) {
		  Note[] notesBTasArray = new Note[notesBT.size()];
		  notesBT.toArray(notesBTasArray);
		  return notesBTasArray;
	  }
	  else
		  return null;
  }
  
  public static Note[] excludeTiesEndings(Note[] notes) {
	  Vector withoutTiesEnding = new Vector();
	  Note[] withoutTiesEndingArray = null;
	  for (int i=0; i<notes.length; i++) {
		  if (!notes[i].isEndingTie())
			  withoutTiesEnding.addElement(notes[i]);
	  }
	  if (withoutTiesEnding.size()>0) {
		  withoutTiesEndingArray = new Note[withoutTiesEnding.size()];
		  withoutTiesEnding.toArray(withoutTiesEndingArray);
	  }
	  return withoutTiesEndingArray;
  }
  
  /** 
   * @see #getHighestNote()
   * @see Note#isLowerThan(Note) */
  public Note getLowestNote() {
	  /*Note lowestNote = (Note)m_notes.elementAt(0);
	  for (int i=1; i<m_notes.size(); i++)
		  if (((Note)(m_notes.elementAt(i))).isLowerThan(lowestNote))
			  lowestNote = (Note)m_notes.elementAt(i);
	  return lowestNote;*/
	  return getLowestNote(this.toArray());
  }
  
  /** Returns <TT>true</TT> if the strict durations of all notes composing this 
   * multi note have the same value.
   * @return <TT>true</TT> if the strict durations of all notes composing this 
   * multi note have the same value, <TT>false</TT> otherwise.
   * @see Note#getStrictDuration() */
  public boolean hasUniqueStrictDuration() {
	  short strictDuration = ((Note)m_notes.elementAt(0)).getStrictDuration();
	  short currentDuration = 0;
	  for (int i=1; i<m_notes.size(); i++) {
		  currentDuration = ((Note)(m_notes.elementAt(i))).getStrictDuration();
		  if (currentDuration!=strictDuration)
			  return false;
	  }
	  return true;
  }
  
  //TODO rework this interface method. 
  public Hashtable splitWithSameStrictDuration() {
	  Hashtable splitter = new Hashtable();
	  for (int i=0; i<m_notes.size(); i++) {
		  Note note = (Note)m_notes.elementAt(i);
		  Short key = new Short(note.getStrictDuration()); 
		  if (splitter.containsKey(key))
			  ((Vector)splitter.get(key)).addElement(note);
		  else {
			  Vector v = new Vector();
			  v.addElement(note);
			  splitter.put(key, v);
		  }
	  }
	  return splitter;
  }
  
  private static Vector fromLowestToHighest(Vector original){
	  //TODO quick sort could be improved..
	  Vector orderedNotes = new Vector();
	  for (int i=0; i<original.size(); i++) {
		  int j=0;
		  while (j<orderedNotes.size() 
				  && ((Note)orderedNotes.elementAt(j)).isLowerThan((Note)original.elementAt(i)))
			  j++;
		  orderedNotes.insertElementAt(original.elementAt(i),j);
	  }
	  return orderedNotes;
  }
  
  /** Returns <TT>true</TT> if this multi note some accidentals (for at least one
   * of its note).
   * @return <TT>true</TT> if this multi note some accidentals (for at least one
   * of its note), <TT>false</TT> otherwise.
   * @see Note#hasAccidental() */
  public boolean hasAccidental() {
	  for (int i=1; i<m_notes.size(); i++)
		  if (((Note)(m_notes.elementAt(i))).hasAccidental())
			  return true;
	  return false;
  }
  
  /** Returns a new vector containing all <TT>Note</TT> objects contained in
   * this multi note.
   * @return a new vector containing all <TT>Note</TT> objects contained in
   * this multi note. */
  public Vector getNotesAsVector()
  { return (Vector)m_notes.clone(); }
  
  public Note[] toArray() {
	  if (m_notes.size()>0) {
		  Note[] notes = new Note[m_notes.size()];
		  return (Note[])m_notes.toArray(notes);
	  }
	  else
		  return null;
  }
}
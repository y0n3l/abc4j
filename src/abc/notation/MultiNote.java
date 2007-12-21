package abc.notation;

import java.util.Vector;
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
		m_notes = notes;
  	}
  	
  	public boolean contains(Note aNote) {
  		return (m_notes.indexOf(aNote)!=-1);
  	}

  /** Returns the longest note of this multi note, based on its duration.
   * @return The longest note of this multi note. If several notes have the
   * same longest length, the first one is returned.
   * @see Note#getDuration() */
  public Note getLongestNote() {
	  float length = 0;
	  float currentNoteLength = 0;
	  Note maxNote = null;
	  for (int i=0; i<m_notes.size() && maxNote==null; i++) {
		  currentNoteLength = ((Note)(m_notes.elementAt(i))).getDuration();
		  if (currentNoteLength > length)
			  maxNote = (Note)m_notes.elementAt(i);
	  }
	  return maxNote;
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
  
  /** 
   * @see #getHighestNote()
   * @see Note#isLowerThan(Note) */
  public Note getLowestNote() {
	  Note lowestNote = (Note)m_notes.elementAt(0);
	  for (int i=1; i<m_notes.size(); i++)
		  if (((Note)(m_notes.elementAt(i))).isLowerThan(lowestNote))
			  lowestNote = (Note)m_notes.elementAt(i);
	  return lowestNote;
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
}
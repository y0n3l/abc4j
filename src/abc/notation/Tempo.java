package abc.notation;

/** The tempo class enables you to define tempo values from a reference note
 * length. */
public class Tempo implements MusicElement
{
  /** The length taken as reference to define the tempo value. */
  private short m_referenceLength = Note.QUARTER;
  /** The tempo value in notes per minutes. */
  private int m_value;

  /** Creates a tempo object with the specified tempo value and quarter as
   * length reference.
   * @param notesNbPerMinute The number of quarter notes per minutes. */
  public Tempo (int notesNbPerMinute)
  { m_value = notesNbPerMinute; }

  /** Creates a tempo object with the specified tempo value and the specified
   * length reference.
   * @param referenceLength The reference length.
   * @param value The number of reference lengths per minutes. */
  public Tempo (short referenceLength, int value)
  {
    m_referenceLength = referenceLength;
    m_value = value;
  }

  /** Returns the reference length used to express this tempo.
   * @return The reference length used to express this tempo. Possible
   * values are <TT>Note.SIXTY_FOURTH</TT>, <TT>Note.THIRTY_SECOND</TT> ...
   * or <TT>Note.WHOLE</TT>. */
  public short getReference()
  { return m_referenceLength; }

  /** Returns the number of note per minutes considering that those notes' length
   * is the reference length.
   * @return the number of note per minutes considering that those notes' length
   * is the reference length.  */
  public int getNotesNumberPerMinute()
  { return m_value; }

  /** Returns the tempo for the given length as reference.
   * @param refLength The note length in which this tempo should be expressed.
   * @return The number of notes of the specified length per minutes. */
  public int getNotesNumberPerMinute(short refLength)
  { return (int)(m_value * (((float)m_referenceLength)/((float)refLength))); }
}

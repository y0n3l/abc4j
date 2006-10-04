package abc.notation;

import java.util.Vector;

/** A class for defining tuplets in a score. */
public class Tuplet
{
  /** Notes composing the tuplet. */
  private Vector m_notes = null;
  private int m_totalRelativeLength = -1;

  /** Creates a new tuplet composed of the specified notes. The total length
   * of this tuplet will be equals to the totalRelativeLength * defaultLength.
   * @param notes The <TT>NoteAbstract</TT> obejcts composing this tuplet,
   * encapsulated inside a <TT>Vector</TT>.
   * @param totalRelativeLength The total relative length of this tuplet
   * multiplied by the delfault relative length gives the total absolute length
   * of this tuplet. */
  public Tuplet (Vector notes, int totalRelativeLength)
  {
    m_notes = notes;
    m_totalRelativeLength = totalRelativeLength;
    for (int i=0; i<notes.size(); i++)
      ((NoteAbstract)notes.elementAt(i)).setTuplet(this);
  }

  /** Returns the total relative length of this tuplet.
   * @return The total relative length of this tuplet. The total relative length
   * of this tuplet multiplied by the delfault relative length gives the total
   * absolute length of this tuplet. */
  public int getTotalRelativeLength()
  { return m_totalRelativeLength; }

  /** Returns a new vector containing all notes of this multi note.
   * @return A new vector containing all notes of this multi note. */
  public Vector getNotesAsVector()
  { return (Vector)m_notes.clone(); }

}

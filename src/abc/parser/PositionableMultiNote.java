package abc.parser;

import java.util.Vector;
import scanner.CharStreamPosition;
import scanner.PositionableInCharStream;
import abc.notation.MultiNote;

/**
 * A multinote that encapsulates the information needed to locate where the abc 
 * information describing this multinote was positioned in the parsed stream.
 */
public class PositionableMultiNote extends MultiNote implements PositionableInCharStream
{
  private CharStreamPosition m_position = null;
  private int m_length = -1;

  public PositionableMultiNote(Vector notes)
  {
    super(notes);
    PositionableNote firstNote = (PositionableNote)notes.elementAt(0);
    PositionableNote lastNote = (PositionableNote)notes.elementAt(notes.size()-1);
    m_position = firstNote.getPosition();
    m_length = lastNote.getPosition().getCharactersOffset() - firstNote.getPosition().getCharactersOffset()
        + lastNote.getLength();
  }

  public CharStreamPosition getPosition()
  { return m_position; }

  public void setBeginPosition(CharStreamPosition position)
  { m_position = position; }

  public void setLength(int length)
  { m_length = length; }

  public int getLength()
  { return m_length; }

  public String toString()
  { return super.toString()+" "+m_position+"(l="+m_length+")"; }
}

package abc.parser;

import abc.notation.Note;
import scanner.CharStreamPosition;
import scanner.PositionableInCharStream;

/**
 * A note that encapsulates the information needed to locate where the abc 
 * information describing this note was positioned in the parsed stream.
 */
public class PositionableNote extends Note implements PositionableInCharStream
{
  private CharStreamPosition m_position = null;
  private int m_length = -1;

  public PositionableNote(byte heigthValue, byte accidentalValue)
  { super(heigthValue, accidentalValue); }

  public PositionableNote(byte heigthValue, byte accidentalValue, byte octaveTranspositionValue)
  { super(heigthValue, accidentalValue, octaveTranspositionValue); }

  public CharStreamPosition getPosition()
  { return m_position; }

  public void setBeginPosition(CharStreamPosition position)
  { m_position = position; }

  public void setLength(int length)
  { m_length = length; }

  public int getLength()
  { return m_length; }

  public String toString()
  { return super.toString()+" "+m_position+"(l=" + m_length+")"; }
}

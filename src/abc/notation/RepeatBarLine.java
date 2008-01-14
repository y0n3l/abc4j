package abc.notation;

/** A special bar line that enables you to repeat part of music from a tune. */
public class RepeatBarLine extends BarLine implements MusicElement
{
  private byte m_repeatNumber=0;

  /** Creates a new repeat bar line.
   * @param repeatsNumber The number of times the repeat should occur. */
  public RepeatBarLine(byte repeatsNumber)
  {
    super((repeatsNumber==1)?BarLine.SIMPLE:BarLine.REPEAT_CLOSE);
    m_repeatNumber = repeatsNumber;
  }

  /** Returns the number of times the repeat should occur.
   * @return the number of times the repeat should occur. */
  public byte getRepeatNumber()
  { return m_repeatNumber; }

  /** Returns a string representation of this repeat barline.
   * @return A string representation of this repeat barline. */
  public String toString()
  {
    if (m_repeatNumber==1)
      return "|1";
    else
    if (m_repeatNumber==2)
      return ":|2";
    else
      return "?";
  }

}


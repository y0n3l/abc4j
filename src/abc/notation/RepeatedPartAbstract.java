package abc.notation;

/** Abstract class that defines the number of times a part in a tune's music should
 * be repeated. */
public abstract class RepeatedPartAbstract
{
  private int m_repeatNumber = 1;
  /** Creates a new repeated part. By default this part is repeated
   * only once. */
  public RepeatedPartAbstract()
  { super(); }

  /** Returns the number of times this part should be repeated.
   * @return The number of times this part should be repeated. */
  public int getNumberOfRepeats()
  { return m_repeatNumber; }

  /** Sets the number of times this part should be repeated.
   * @param repeatNumber The number of times this part should be repeated. */
  public void setNumberOfRepeats(int repeatNumber)
  { m_repeatNumber = repeatNumber; }

  /** Returns this repeated part as an array of singles parts. The playing of
   * this repeated part would sound the same as the playing of the array of parts.
   * @return An array of singles parts that would sound the same as the
   * playing of this repeated part. */
  public abstract Part[] toPartsArray();
}

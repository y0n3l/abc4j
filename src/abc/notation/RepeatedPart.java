package abc.notation;

/** A simple <TT>Part</TT> repeated several times. */
public class RepeatedPart extends RepeatedPartAbstract
{
  private Part m_part = null;

  /** Creates a new repeated part that will repeat the given part.
   * @param part A part of a tune. */
  public RepeatedPart (Part part)
  { m_part = part; }

  public Part[] toPartsArray()
  {
    int repeatNumber = getNumberOfRepeats();
    Part[] parts = new Part[repeatNumber];
    for (int i=0; i<repeatNumber; i++)
      parts[i] = m_part;
    return parts;
  }
}

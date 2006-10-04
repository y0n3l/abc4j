package abc.notation;

import java.util.Vector;

/** This class describes the way a multipart score is defined. */
public class MultiPartsDefinition extends RepeatedPartAbstract
{
  private Vector m_parts = new Vector();

  /** Creates a new multi part definition. */
  public MultiPartsDefinition()
  { new Vector();}

  /** Adds a new part to this multi part.
   * @param p The part that has to be added to the multi part. This part can
   * be a simple <TT>Part</TT> or another </TT>MultiPart</TT> (composite
   * definition). */
  public void addPart(RepeatedPartAbstract p)
  { m_parts.addElement(p); }

  /** Returns this multipart as an array of singles parts. The playing of the
   * multi part would sound the same as the playing of the array of parts.
   * @return An array of singles parts that would sound the same as the
   * playing of this multipart. */
  public Part[] toPartsArray()
  {
    Vector parts = getPartsAsRepeatedOnceVector();
    Part[] partsArray = new Part[parts.size()];
    for (int i=0; i<parts.size(); i++)
      partsArray[i] = (Part)parts.elementAt(i);
    return partsArray;
  }

  public  Vector getPartsAsRepeatedOnceVector()
  {
    //int repeatNumber = getNumberOfRepeats();
    Vector parts = new Vector();
    //Part currentPart = null;
    RepeatedPartAbstract c = null;
    for (int i=0; i<getNumberOfRepeats(); i++)
    {
      for (int j=0; j<m_parts.size();j++)
      {
        c = (RepeatedPartAbstract)m_parts.elementAt(j);
        Part[] p = c.toPartsArray();
        for (int k=0; k<p.length; k++)
          parts.addElement(p[k]);
      }
    }
    return parts;
  }
}

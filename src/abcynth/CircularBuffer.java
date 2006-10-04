package abcynth;

import java.util.Vector;
public class CircularBuffer extends Vector
{
  private int m_sizeLimite = 0;

  public CircularBuffer(int sizeLimit)
  {m_sizeLimite = sizeLimit;}

  public void setSizeLimit(int sizeLimit)
  {m_sizeLimite = sizeLimit;}

  public void addElement(Object o)
  {
    if (size()>=m_sizeLimite)
      removeElementAt(0);
    super.addElement(o);
    //return true;
  }
}
package abc.midi;

import java.util.EventObject;

/** */
public class TempoChangeEvent extends EventObject
{
  private int m_newTempoValue = 0;
  public TempoChangeEvent(Object source, int newTempoValue)
  {
    super(source);
    m_newTempoValue = newTempoValue;
  }

  public int getNewTempoValue()
  { return m_newTempoValue; }
}

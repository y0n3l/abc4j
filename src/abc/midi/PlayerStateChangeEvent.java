package abc.midi;

import java.util.EventObject;

/** */
public class PlayerStateChangeEvent extends EventObject
{
  private boolean m_isPlaying = false;
  public PlayerStateChangeEvent(Object source, boolean isPlaying)
  { super(source); }

  public boolean isPlayerPlaying()
  { return m_isPlaying; }
}

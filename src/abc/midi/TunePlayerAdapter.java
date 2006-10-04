package abc.midi;

import abc.notation.NoteAbstract;

/** A convenient class to listen to TunePlayer. This listener does nothing. Just
* extend from it to override your behaviours. */
public class TunePlayerAdapter implements TunePlayerListenerInterface
{
  /** Invoked when the playing of a tune has started. */
  public void playBegin(PlayerStateChangeEvent e)
  {}

  /** Invoked when the playing of a tune is ended. */
  public void playEnd(PlayerStateChangeEvent e)
  {}

  /** Invoked when the playing tempo has changed. */
  public void tempoChanged(TempoChangeEvent e)
  {}

  /** Invoked when a part of the tune notation is played. */
  public void partPlayed(int begin, int end)
  {}

  public void notePlayed(NoteAbstract note)
  {}
}

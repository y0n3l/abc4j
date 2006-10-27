package abc.midi;

import java.util.EventListener;

import abc.notation.NoteAbstract;

/** This interface defines methods that should be implemented by any object that
 * wants to listen to a tune player. */
public interface TunePlayerListenerInterface extends EventListener
{
  /** Invoked when the playing of a tune has started. */
  public void playBegin(PlayerStateChangeEvent e);

  /** Invoked when the playing of a tune is ended. */
  public void playEnd(PlayerStateChangeEvent e);

  /** Invoked when the playing tempo has changed. */
  public void tempoChanged(TempoChangeEvent e);

  /** Invoked when a part of the tune notation is played.
   * @deprecated use notePlayed() instead. */
  public void partPlayed(int begin, int end);

  public void notePlayed(NoteAbstract note);
}

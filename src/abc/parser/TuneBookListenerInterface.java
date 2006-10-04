package abc.parser;

import java.util.EventListener;

/** This is the interface that should be implemented by any object that wants
 * to listens to changes occured in a tunebook. */
public interface TuneBookListenerInterface extends EventListener
{
  /** Invoked when a change occured in the tunebook.
   * @param e An event describing the change that occured in the tunebook. */
  public void tuneChanged(TuneChangeEvent e);
}
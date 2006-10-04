package scanner;

import java.util.EventListener;

/** This interface should be implemented by any object that wants to listen
 * to scanner events thrown during characters stream scanning. */
public interface ScannerListenerInterface extends EventListener
{
  /** Invoked when a new token has been generated.
   * @param event Event containing all information about the token generated. */
  public void tokenGenerated(TokenEvent event);

  /** Invoked when an invalid character has been found.
   * @param evt Event containing all information about the invalid character
   * found. */
  public void invalidCharacter(InvalidCharacterEvent evt);

  /** Invoked when a line has been processed.
   * @param line The line that has just been processed. */
  public void lineProcessed(String line);
}

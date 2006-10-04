package abc.parser;

import java.util.EventListener;

import abc.notation.Tune;
import scanner.InvalidCharacterEvent;
import scanner.TokenEvent;

/** Interface that should be implemented by any object that listens to tunes
 * parsing. */
public interface TuneParserListenerInterface extends EventListener
{
  /** Invoked when the parsing of the tune begins. */
  public void tuneBegin();

  /** Invoked when an invalid token has been encountered.
   * @param event An event describing the problem encountered by the parser. */
  public void invalidToken(InvalidTokenEvent event);

  /** Invoked when a valid token has been encountered by the parser.
   * @param event An event describing the valid token parsed. */
  public void validToken(TokenEvent event);

  /** Invoked when an invalid character has been found by the parser.
   * @param event An event describing the invalid character found by the parser. */
  public void invalidCharacter(InvalidCharacterEvent event);

  /** Invoked when the parsing of a tune has ended.
   * @param tune The tune that has just been parsed. */
  public void tuneEnd(Tune tune);
}

package abc.parser;

import abc.notation.Tune;
import scanner.InvalidCharacterEvent;
import scanner.TokenEvent;

/** A default implementation of a file parser listener that does nothing. */
public class AbcFileParserAdapter implements AbcFileParserListenerInterface
{
  /** Invoked when the parser reaches the beginning of a tune. */
  public void tuneBegin()
  {}

  /** Invoked when an invalid token has been parsed.
   * @param evt An event describing the invalid token. */
  public void invalidToken(InvalidTokenEvent evt)
  {}

  /** Invoked when a valid token has been parsed.
   * @param evt An event describing the valid token. */
  public void validToken(TokenEvent evt)
  {}

  /** Invoked when an invalid character has been parsed.
   * @param evt An event describing the invalid character. */
  public void invalidCharacter(InvalidCharacterEvent evt)
  {}

  /** Invoked when the parser reaches the end of a tune.
   * @param tune The tune that has just been parsed. */
  public void tuneEnd(Tune tune)
  {}

  /** Invoked when the parsing of the file begins. */
  public void fileBegin()
  {}

  /** Invoked when the parsing of the file end. */
  public void fileEnd()
  {}

  public void lineProcessed(String line)
  {}
}

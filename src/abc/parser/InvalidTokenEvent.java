package abc.parser;

import scanner.CharStreamPosition;
import scanner.Token;
import scanner.TokenEvent;
import scanner.TokenType;

/** Event generated when an invalid token is encountered by a parser. */
public class InvalidTokenEvent extends TokenEvent
{
  /** The expected token type. */
  private TokenType m_expectedTokenType = TokenType.UNKNOWN;
  private CharStreamPosition m_position = null;

  /** Creates a new event
   * @param source The source that generates the event.
   * @param token The encountered invalid token.
   * @param expectedTokenType The type of token that was expected. */
  public InvalidTokenEvent(Object source, Token token, TokenType expectedTokenType)
  {
    super(source, token);
    m_expectedTokenType = expectedTokenType;
    m_position = token.getPosition();
  }

  /** Creates a new event.
   * @param source The source that generates the event.
   * @param position The place where the event occured.
   * @param expectedTokenType The type of token that was expected. */
  public InvalidTokenEvent(Object source, CharStreamPosition position, TokenType expectedTokenType)
  {
    super(source, null);
    m_expectedTokenType = expectedTokenType;
    m_position = position;
  }

  /** Returns the type of the token that was expected.
   * @return The type of the token that was expected. The place where this token
   * was expected in the position returned by getPosition().
   * @see #getPosition() */
  public TokenType getExpectedTokenType()
  { return m_expectedTokenType; }

  /** Returns the place where this event occured.
   * @return The place where this event occured. */
  public CharStreamPosition getPosition()
  { return m_position; }

  public int getLength()
  {
    if (m_token==null)
      return 0;
    else
      return m_token.getLength();
  }

  /** Returns a String representation of this event.
   * @return a String representation of this event. */
  public String toString()
  {
    if (getToken()!=null)
      return  ("Expecting " + m_expectedTokenType.toString() + " instead of "
               + getToken().getType().toString() + "(" + getToken() + ")"
               + getPosition());
    else
      return  ("Expecting " + m_expectedTokenType.toString() + " " + getPosition());
  }
}

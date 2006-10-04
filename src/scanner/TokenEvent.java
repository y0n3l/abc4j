package scanner;

import java.util.EventObject;
import scanner.PositionableInCharStream;
import scanner.CharStreamPosition;
import scanner.Token;

/** This event is thrown when a new token has been detected. */
public class TokenEvent extends EventObject implements PositionableInCharStream
{
  /** The token found. */
  protected Token m_token = null;

  /** Creates a new token event.
   * @param source The source that has detected the new token.
   * @param token The detected token. */
  public TokenEvent(Object source, Token token)
  {
    super(source);
    m_token = token;
  }

  public CharStreamPosition getPosition()
  { return m_token.getPosition();}

  public int getLength()
  { return m_token.getLength();}

  /** Returns the found token.
   * @return The found token. */
  public Token getToken()
  { return m_token; }

  /** Returns a string representation of this token event.
   * @return A string representation of this token event. */
  public String toString()
  { return (m_token + "(" + m_token.getType() + ")"); }
}
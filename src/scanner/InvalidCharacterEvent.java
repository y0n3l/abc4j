package scanner;

import java.util.EventObject;
import scanner.CharStreamPosition;

/** This event is thrown when an unexpected character is encountered. */
public class InvalidCharacterEvent extends EventObject implements PositionableInCharStream
{
  /** The invalid character. */
  private char m_character;
  /** The position of the invalid character. */
  private CharStreamPosition m_position = null;

  /** Creates a new event with the following parameters.
   * @param source The source that has detected the invalid character.
   * @param character The invalid character.
   * @param position The position where the invalid character has been detected. */
  public InvalidCharacterEvent(Object source, char character, CharStreamPosition position)
  {
    super(source);
    m_position = position;
    m_character = character;
  }

  /** Returns the invalid character.
   * @return The invalid character. */
  public char getCharacter()
  { return m_character; }

  public int getLength()
  { return 1; }

  /** Returns the position where the invalid character has been detected.
   * @return The position where the invalid character has been detected. */
  public CharStreamPosition getPosition()
  { return m_position; }

  public String toString()
  { return ("'" + m_character + "'" + m_position); }
}

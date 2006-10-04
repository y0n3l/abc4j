package scanner;

/** Interface that should be implemented by any object that has a position
 * in a char stream. */
public interface PositionableInCharStream
{
  /** Returns the start position.
   * @return The start position. */
  public CharStreamPosition getPosition();

  /** Returns the length of this positionable object.
   * @return The length of this positionable object. */
  public int getLength();

}

package abc.notation;

/** This class defines bar lines. */
public class BarLine implements MusicElement
{
  /** The simple bar line type. Ex: | */
  public static final byte SIMPLE = 0;
  /** The repeat open bar line type. Ex: |: */
  public static final byte REPEAT_OPEN = 1;
  /** The repeat close bar line type. Ex: :| */
  public static final byte REPEAT_CLOSE = 2;
  /** The type of this bar line. */
  private byte m_type = SIMPLE;

  /** Default constructor.
   * Constructs a simple bar line. */
  public BarLine()
  {}

  /** Creates a new bar line with the corresponding type.
   * @param type The type of bar line to be created : <TT>SIMPLE</TT>,
   * <TT>REPEAT_OPEN</TT> or <TT>REPEAT_CLOSE</TT>. */
  public BarLine(byte type)
  { m_type = type; }

  /** Returns the type of this bar line.
   * @return The type of this bar line. */
  public byte getType()
  { return m_type; }

  /** Converts the specified string to a bar line type.
   * @param barLine The string to be converted as a bar line.
   * Possible values are <TT>|</TT>, <TT>||</TT>, <TT>[|</TT>,
   * <TT>|]</TT>, <TT>:|</TT>, <TT>|:</TT>, <TT>::</TT>.
   * @return The bar line type corresponding to the given string.
   * <TT>null</TT> is returned if no type matches the string. */
  public static byte[] convertToBarLine(String barLine)
    {
      byte[] barlineTypes = null;
      if (barLine.equals("::"))
      {
        barlineTypes = new byte[2];
        barlineTypes[0] = BarLine.REPEAT_CLOSE;
        barlineTypes[1] = BarLine.REPEAT_OPEN;
        return barlineTypes;
      }
      else
      {
        if (barLine.equals("|"))
        {
          barlineTypes = new byte[1];
          barlineTypes[0] = BarLine.SIMPLE;
        }
        else
        if (barLine.equals("||"))
        {
          barlineTypes = new byte[1];
          barlineTypes[0] = BarLine.SIMPLE;
        }
        else
        if (barLine.equals("[|"))
        {
          barlineTypes = new byte[1];
          barlineTypes[0] = BarLine.SIMPLE;
        }
        else
        if (barLine.equals("|]"))
        {
          barlineTypes = new byte[1];
          barlineTypes[0] = BarLine.SIMPLE;
        }
        else
        if (barLine.equals(":|"))
        {
          barlineTypes = new byte[1];
          barlineTypes[0] = BarLine.REPEAT_CLOSE;
        }
        else
        if (barLine.equals("|:"))
        {
          barlineTypes = new byte[1];
          barlineTypes[0] = BarLine.REPEAT_OPEN;
        }
        return barlineTypes;
      }
    }

  /** Returns a string representation of this object.
   * @return A string representation of this object. */
  public String toString()
  {
    if (m_type==SIMPLE) return new String("|");
    if (m_type==REPEAT_OPEN) return new String("|:");
    if (m_type==REPEAT_CLOSE) return new String(":|");
    return null;
  }
}

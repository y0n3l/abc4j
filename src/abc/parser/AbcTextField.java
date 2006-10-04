package abc.parser;

class AbcTextField
{
  public static final byte AREA = 1;
  public static final byte BOOK = 2;
  public static final byte COMPOSER = 3;
  public static final byte DISCOGRAPHY = 4;
  public static final byte ELEMSKIP = 5;
  public static final byte GROUP = 6;
  public static final byte HISTORY = 7;
  public static final byte INFORMATION = 8;
  public static final byte NOTES = 9;
  public static final byte ORIGIN = 10;
  public static final byte RHYTHM = 11;
  public static final byte SOURCE = 12;
  public static final byte TITLE = 15;
  public static final byte TRANSCRNOTES = 13;
  public static final byte WORDS = 14;

  private byte m_type = 0;
  private String m_text = null;
  private String m_comment = null;

  public AbcTextField(byte type, String text)
  {
    m_type = type;
    m_text = text;
  }

  public AbcTextField(byte type, String text, String comment)
  {
    m_type = type;
    m_text = text;
    m_comment = comment;
  }

  public String getText()
  {return m_text;}

  public byte getType()
  {return m_type;}

  public String getComment()
  {return m_comment;}

  public void display ()
  {
  }
}

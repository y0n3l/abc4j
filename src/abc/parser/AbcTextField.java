// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
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

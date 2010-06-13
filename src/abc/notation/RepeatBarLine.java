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
package abc.notation;

/** A special bar line that enables you to repeat part of music from a tune. */
public class RepeatBarLine extends BarLine implements Cloneable
{

  private static final long serialVersionUID = 6130499407941371335L;
  
  private byte m_repeatNumber=0;

  /** Creates a new repeat bar line.
   * @param repeatsNumber The number of times the repeat should occur. */
  public RepeatBarLine(byte repeatsNumber)
  {
    super((repeatsNumber==1)?BarLine.SIMPLE:BarLine.REPEAT_CLOSE);
    m_repeatNumber = repeatsNumber;
  }

  /** Returns the number of times the repeat should occur.
   * @return the number of times the repeat should occur. */
  public byte getRepeatNumber()
  { return m_repeatNumber; }

  /** Returns a string representation of this repeat barline.
   * @return A string representation of this repeat barline. */
  public String toString()
  {
    if (m_repeatNumber==1)
      return "|1";
    else
    if (m_repeatNumber==2)
      return ":|2";
    else
      return "?";
  }

  public Object clone() throws CloneNotSupportedException {
	  return super.clone();
  }
}


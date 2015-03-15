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
package abcynth;

import java.util.Vector;
public class CircularBuffer extends Vector<Object>
{
  private static final long serialVersionUID = -3820743244359848069L;
  private int m_sizeLimite = 0;

  public CircularBuffer(int sizeLimit)
  {m_sizeLimite = sizeLimit;}

  public void setSizeLimit(int sizeLimit)
  {m_sizeLimite = sizeLimit;}

  public void addElement(Object o)
  {
    if (size()>=m_sizeLimite)
      removeElementAt(0);
    super.addElement(o);
    //return true;
  }
}
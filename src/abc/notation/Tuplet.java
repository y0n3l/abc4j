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

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

/** A class to define tuplets. */
public class Tuplet extends MusicElement implements Cloneable, Serializable
{
  private static final long serialVersionUID = -800634088496917971L;
  
  /** Notes composing the tuplet. */
  private Vector<NoteAbstract> m_notes = new Vector<NoteAbstract>(4, 2);
  private short m_totalRelativeLength = -1;
  private short m_totalDuration = -1;
  private short m_tupletNumber = -1;
  private short m_defaultNoteLength = -1;

  /** Creates a new tuplet composed of the specified notes. The total length
   * of this tuplet will be equals to the totalRelativeLength * defaultLength.
   * @param notes The <TT>NoteAbstract</TT> obejcts composing this tuplet,
   * encapsulated inside a <TT>Vector</TT>.
   * @param totalRelativeLength The total relative length of this tuplet
   * multiplied by the default relative length gives the total absolute length
   * of this tuplet.
   * @deprecated
   */
  public Tuplet(Vector<NoteAbstract> notes, short totalRelativeLength, short defaultNoteLength) {
  	this((short)notes.size(), notes, totalRelativeLength, defaultNoteLength);
  }

  /** Creates a new tuplet composed of the specified notes. The total length
   * of this tuplet will be equals to the totalRelativeLength * defaultLength.
   * @param tupletNumber The <TT>number</TT> that will be displayed
   * on the tuplet. A 3-uplet of 16th can have only 2 notes : one 8th and one 16th
   * @param notes The <TT>NoteAbstract</TT> obejcts composing this tuplet,
   * encapsulated inside a <TT>Vector</TT>.
   * @param totalRelativeLength The total relative length of this tuplet
   * multiplied by the default relative length gives the total absolute length
   * of this tuplet. */
  public Tuplet(int tupletNumber, Collection<NoteAbstract> notes, short totalRelativeLength, short defaultNoteLength)
  {
	m_tupletNumber = (short)tupletNumber;
    m_notes.addAll(notes);
    m_totalRelativeLength = totalRelativeLength;
    m_defaultNoteLength = defaultNoteLength;
    m_totalDuration = (short)(m_totalRelativeLength * m_defaultNoteLength);
    for (int i=0; i<notes.size(); i++) {
      ((NoteAbstract)m_notes.elementAt(i)).setTuplet(this);
    }
  }

  /** Returns the total relative length of this tuplet.
   * @return The total relative length of this tuplet. The total relative length
   * of this tuplet multiplied by the default relative length gives the total
   * absolute length of this tuplet.
   * @deprecated use totalDuration() instead. Reference to relative length should
   * be avoided in the API bacause this is only related to the "abc world". 
   * @see #getTotalDuration() */
  public short getTotalRelativeLength()
  { return m_totalRelativeLength; }
  
  /** Returns the total duration of this tuplet.
   * @return The total duration of this tuplet. */  
  public short getTotalDuration() {
	  return m_totalDuration;
  }
  
  /** Default note length is the length of all notes
   * of the tuplet if tupletNumber==numberOfNotes
   * and each note has the same duration.
   * @return
   */
  public short getDefaultNoteLength() {
	  return m_defaultNoteLength;
  }
  
  public int getNumberOfNotes() {
	  return m_notes.size();
  }
  
  public int getTupletNumber() {
	  return m_tupletNumber;
  }

  /** Returns a new vector containing all notes of this multi note.
   * @return A new vector containing all notes of this multi note. */
  @SuppressWarnings("unchecked")
public Vector<NoteAbstract> getNotesAsVector()
  { return (Vector<NoteAbstract>) m_notes.clone(); }

  @SuppressWarnings("unchecked")
  public Object clone() throws CloneNotSupportedException {
		Object o = super.clone();
		if (m_notes != null)
			((Tuplet) o).m_notes = (Vector<NoteAbstract>) m_notes.clone();
		return o;
	}
}

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

/** This class abstracts any kind of relationship  between two notes. */
public class TwoNotesLink {
	
	/** The note starting the link between the two notes. */	
	private NoteAbstract start = null;
	/** The ending the link between the two notes. */
	private NoteAbstract end = null;
	
	/** Default constructor. */
	protected TwoNotesLink(){
	}
	
	/** Returns the end of the two notes link.
	 * @return Returns the end of the two notes link. 
	 * <TT>null</TT> if not specified.
	 * @see #getStart() */
	public NoteAbstract getEnd() {
		return end;
	}
	
	/** Sets the end of the two notes link.
	 * @param end The note ending the relation between the two notes. 
	 * @see #getEnd() 
	 * @see #setStart(NoteAbstract) */
	public void setEnd(NoteAbstract end) {
		this.end = end;
	}
	
	/** Returns the start of the two notes link.
	 * @return Returns the start of the two notes link. 
	 * <TT>null</TT> if not specified. 
	 * @see #getEnd() */
	public NoteAbstract getStart() {
		return start;
	}
	
	/** Sets the start of the two notes link.
	 * @param start The note starting the relation between the two notes. 
	 * @see #getStart() 
	 * @see #setEnd(NoteAbstract) */
	public void setStart(NoteAbstract start) {
		this.start = start;
	}
}
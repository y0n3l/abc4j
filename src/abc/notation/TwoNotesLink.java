package abc.notation;

/** This class abstracts any kind of relationship  between two notes. */
public class TwoNotesLink {
	
	/** The note starting the link between the two notes. */	
	protected NoteAbstract start = null;
	/** The ending the link between the two notes. */
	protected NoteAbstract end = null;
	
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

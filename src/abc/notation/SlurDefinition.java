package abc.notation;


/**
 */
public class SlurDefinition implements ScoreElementInterface {

	protected NoteAbstract start = null;
	
	protected NoteAbstract end = null;
	
	//protected Vector m_content = null;
	
	public SlurDefinition(){
		//m_content = new Vector();
		System.out.println("test");
	}
	
	/*public void add(NoteAbstract groupStart) {
		m_content start = groupStart;
	}*/
	

	/**
	 * @return Returns the end.
	 */
	public NoteAbstract getEnd() {
		return end;
	}
	/**
	 * @param end The end to set.
	 */
	public void setEnd(NoteAbstract end) {
		this.end = end;
	}
	/**
	 * @return Returns the start.
	 */
	public NoteAbstract getStart() {
		return start;
	}
	/**
	 * @param start The start to set.
	 */
	public void setStart(NoteAbstract start) {
		this.start = start;
	}
}

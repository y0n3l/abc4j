package abc.notation;

/**
 * A class to describe words (lyrics) in a score. 
 */
public class Words implements ScoreElementInterface {
	/** the content of the words. */
	private String content = null;
	
	/** Creates a new word element.
	 * @param theContent The words to be included in the score. */
	public Words(String theContent){
		content = theContent;
	}
	
	/** Returns the content of this words element.
	 * @return The content of this words element. */
	public String getContent() {
		return content;
	}
	
}
package abc.notation;

public class Words implements ScoreElementInterface {

	private String content = null;
	
	public Words(String theContent){
		content = theContent;
	}
	
	/** Returns the content of this words element. */
	public String getContent() {
		return content;
	}
	
}
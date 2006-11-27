package abc.parser;

import scanner.Set;

/** 
 * This class provides parser instances that reduce the parsing 
 * scope to tunes headers (music is excluded). This results in a much 
 * faster parsing than parsing the whole tunes and enables you to 
 * build up tunes indexes fastly.<BR/>
 * Just invoke the usual methods #parseFile(File abcFile) or 
 * #parseFile(Reader abcCharStream) and you'll get in return instances of 
 * Tune without any Score part. */
public class AbcHeadersParser extends AbcFileParser {

	/** Default constructor. */
	public AbcHeadersParser() {
		// Override the definition of an abc line 
		FIRST_ABC_LINE = new Set(AbcTokenType.TEXT);
	}
	
	/** abc-line ::= (1*element line-ender) / mid-tune-field / tex-command */
    protected void parseAbcLine(Set follow) {
    	//System.out.println(this.getClass().getName() + "parsing line !");
    	Set current = new Set(AbcTokenType.LINE_FEED);
    	accept(AbcTokenType.TEXT, current, follow);
    	current.remove(AbcTokenType.LINE_FEED);
    	accept(AbcTokenType.LINE_FEED, current, follow);
    }

}

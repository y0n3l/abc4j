package abc.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import scanner.InvalidCharacterEvent;
import scanner.NoSuchTokenException;
import scanner.ScannerListenerInterface;
import scanner.Set;
import scanner.TokenEvent;
import abc.notation.Tune;

/** An abc parser to parse abc files written using abc notation.
 * This parser has the flexibility to 
 * <UL>
 * 		<LI>Parse entierely the content of the tunes described in the 
 * file : header + music score content. </LI>
 * 		<LI>Restrict the parsing to the headers of the tunes : in that
 * case, the parser fully parses the content of the header, and then
 * skips the next lines until it gets again a tune again. This accelerates
 * the parsing phasis and is quite usefull when you are only interested in
 * a global view from the content of an ABC file (= you don't get about the
 * scores for the tunes). </LI>
 * </UL> 
 * */
public class AbcFileParser extends AbcParserAbstract {
	/** Creates a new abc file parser. */
	public AbcFileParser() {
		m_scanner.removeListener(m_scannerListener);
		m_scannerListener = new ScannerListenerInterface() {
			//=======================================SCANNER LISTENER BEGIN
			public void tokenGenerated(TokenEvent evt){
			}
			public void invalidCharacter(InvalidCharacterEvent evt){
				notifyListenersForInvalidCharacter(evt);
			}
			public void lineProcessed(String line) {
				notifyListenersForLineProcessed(line);
			}
			//=======================================SCANNER LISTENER END
		};
		m_scanner.addListener(m_scannerListener);
	}

	/** Adds a listener to catch events thrwon by the parser durin tune parsing.
	 * @param listener Object that implements the TuneParserListenerInterface.
	 * @see #removeListener(abc.parser.AbcFileParserListenerInterface)*/
	public void addListener(AbcFileParserListenerInterface listener) { 
		super.addListener(listener);
	}

  /** Removes a listener from this parser.
   * @param listener The listener to be removed.
   * @see #addListener(abc.parser.AbcFileParserListenerInterface) */
  public void removeListener(AbcFileParserListenerInterface listener)
  { super.addListener(listener); }

  /** Parses the specified file.
   * @param abcFile The file to be parsed.
   * @exception FileNotFoundException Thrown if the specified file isn't found. */
  public void parseFile(File abcFile) throws FileNotFoundException
  { parseFile(new BufferedReader(new InputStreamReader(new FileInputStream (abcFile)))); }

  /** Parses only tunes header of the the specified file.
   * @param abcFile A text file using ABC notation.
   * @exception FileNotFoundException Thrown if the specified file isn't found. */
  public void parseFileHeaders(File abcFile) throws FileNotFoundException
  { parseFileHeaders(new BufferedReader(new InputStreamReader(new FileInputStream (abcFile)))); }

	/** Parses the specified stream in abc notation.
	 * @param abcCharStream The abc stream to be parsed. */
	public void parseFile(Reader abcCharStream) {
		try {
			Set current = new Set();
			m_scanner.init(abcCharStream);
			notifyListenersForFileBegin();
			parseAbcFile(current);
			notifyListenersForFileEnd();
		}
		catch (NoSuchTokenException e) {
			//e.printStackTrace();
			//Occurs when the last parts of the tune is just invalid characters.
		}
	}

	/** abc-file ::= *(abc-tune / comment / linefeed / tex-command / file-fields) */
	protected void parseAbcFile(Set follow) {
		Set current = initParsing();
		while (m_token!=null) {
			if (FIRST_ABCTUNE.contains(m_token.getType())) {
				notifyListenersForTuneBegin();
				Tune tune = parseAbcTune(current.createUnion(follow));
				notifyListenersForTuneEnd(tune);
			}
			else if (FIRST_COMMENT.contains(m_token.getType()))
				parseComment(current.createUnion(follow));
			else if (FIRST_LINE_FEED.contains(m_token.getType()))
				accept(AbcTokenType.LINE_FEED, current, (current.createUnion(follow)));
        //else if (Syntax.FIRST_TEX_COMMAND.contains(token.getType())) parseAbcTune(current.createUnion(follow));
        //else if (Syntax.FIRST_FILE_FIELDS.contains(token.getType())) parseAbcTune(current.createUnion(follow));
		}
	}
	/** Inits the parsing : sets the starting state of the current Set +
	 * sets the finale state automata of the scanner + retrieves the first 
	 * token and its type.
	 * @return The starting state of the current Set as it should
	 * be at the begining of the parsing. */
	private Set initParsing() {
		Set startingSet = FIRST_ABCTUNE.createUnion(FIRST_COMMENT).createUnion(FIRST_LINE_FEED);
		// are missing TEX COMMAND and FILE FIELDS
		//.createUnion(FIRST_TEX_COMMAND);//.createUnion(FIRST_FILE_FIELDS);
		m_scanner.setFinaleStateAutomata(AutomataFactory.getAutomata(startingSet.getTypes()));
		m_token = m_scanner.nextToken();
		m_tokenType = m_token.getType();
		return startingSet;
	}
	
	/** */
	public void parseFileHeaders(BufferedReader charStream) {
		Tune tune = null;
		try {
			m_scanner.init(charStream);
			notifyListenersForFileBegin();
			Set current = initParsing();
			while (m_token!=null) {
				if (FIRST_ABCTUNE.contains(m_token.getType())) {
					notifyListenersForTuneBegin();
					tune = parseAbcHeader(current);//.createUnion(FIRST_ABC_MUSIC));//.createUnion(FIRST_LINE_FEED));
					m_scanner.removeListener(m_scannerListener);
					StringBuffer line = null;
					//from here, the current token can be anything !
					// if it's a '\n' where at the end of a tune.
					while (m_token.getType()!=AbcTokenType.LINE_FEED) {
						line = new StringBuffer(m_scanner.getCurrentLine());
						//line.append() = line.connew StringBuffer(token.getValue());
						if (!FIRST_LINE_FEED.contains(m_token.getType())) {
							// if this is not an empty line, we process it, and reach the first next token.
							try {
								line.append(charStream.readLine());
								line.append("\n");
								notifyListenersForLineProcessed(new String(line));
							}
							catch(IOException e)
							{ e.printStackTrace(); }
							m_scanner.init(charStream);
							// Init the current to whatever we can find in an Abc File. ("abc-file" in BNF definition)
							Set newCurrent = FIRST_ABCTUNE.createUnion(FIRST_COMMENT).createUnion(FIRST_LINE_FEED)./*union(FIRST_TEX_COMMAND).*/createUnion(FIRST_ABC_MUSIC);
							m_scanner.setFinaleStateAutomata(AutomataFactory.getAutomata(newCurrent.getTypes()));
							// this next token is the first of the new line.
							m_token = m_scanner.nextToken();
							m_tokenType = m_token.getType();
							if (m_tokenType.equals(AbcTokenType.LINE_FEED))
								// we notify for an empty line.
								notifyListenersForLineProcessed(new String("\n"));
							//System.out.println("an empty line has been retrieved line : *" + line +"*");
						}
						// if the line is a line feed, the notification commes from the tokens scanner.
					}
					//System.out.println("token type for tune end : " + m_token.getType());
					notifyListenersForTuneEnd(tune);
					m_scanner.addListener(m_scannerListener);
				}
				else if (FIRST_COMMENT.contains(m_token.getType()))
					parseComment(current);
				else if (FIRST_LINE_FEED.contains(m_token.getType()))
					accept(AbcTokenType.LINE_FEED, current, (current));
				//else if (Syntax.FIRST_TEX_COMMAND.contains(token.getType())) parseAbcTune(current.createUnion(follow));
				//else if (Syntax.FIRST_FILE_FIELDS.contains(token.getType())) parseAbcTune(current.createUnion(follow));
			}
		}
		catch (NoSuchTokenException e) {
			notifyListenersForTuneEnd(tune);
			//System.out.println("CATCHING NO SUCH ELEMENT EXCEPTION");
			//e.printStackTrace();
			//Occurs when the last parts of the tune is just invalid characters.
		}
	}

  /** Notifies listeners that the parsing of the file has begun. */
  protected void notifyListenersForFileBegin()
  {
    for (int i=0; i<m_listeners.size();i++)
      ((AbcFileParserListenerInterface)m_listeners.elementAt(i)).fileBegin();
  }

  /** Notifies listeners that the parsing of the file is ended. */
  protected void notifyListenersForFileEnd()
  {
    for (int i=0; i<m_listeners.size();i++)
      ((AbcFileParserListenerInterface)m_listeners.elementAt(i)).fileEnd();
  }

  /** Notifies listeners that a line has been processed.
   * @param line The line that has been processed. */
  protected void notifyListenersForLineProcessed(String line)
  {
    for (int i=0; i<m_listeners.size();i++)
      ((AbcFileParserListenerInterface)m_listeners.elementAt(i)).lineProcessed(line);
  }

}

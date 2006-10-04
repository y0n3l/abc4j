package scanner;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;

/** A scanner is able to separate tokens from an input stream, following states
 * defined in a finale state automata. */
public class Scanner
{
    private Reader charStream = null;
    private FinaleStateAutomata FSA = null;
    private Vector m_listeners = null;
    private CharStreamPosition m_previousPosition = null;
    private CharStreamPosition m_position = null;
    private StringBuffer m_currentLine = null;
    //private boolean m_lastTokenWasContainingEndOfLine = false;
    private char[] m_currentChar = null;

    /** Creates a new scanner to scan the specified string.
     * @param charStreamValue A string to be scanned. */
    public Scanner(String charStreamValue)
    {  this(new StringReader(charStreamValue)); }

    /** Creates a new scanner to scan the specified stream
     * @param stream The stream to be scanned. */
    public Scanner(Reader stream)
    {
       this();
       m_listeners = new Vector();
       init(stream);
    }

    /** Creates a new scanner. */
    public Scanner()
    {
      m_listeners = new Vector();
      m_currentChar = new char[1];
    }

    /** Inits this scanner to be able to perform a scan on the given string
     * @param charStreamValue A string to be scanned. */
    public void init(String charStreamValue)
    { init(new StringReader(charStreamValue)); }

    /** Inits this scanner to be able to perform a scan on the given stream
     * @param readerStream The stream to be scanned. */
    public void init(Reader readerStream)
    {
      charStream = readerStream;
      FSA =  null;
      m_currentLine = new StringBuffer();
      m_previousPosition = new CharStreamPosition();
      //the position is before the first character
      //column=0, line =1, offset=-1
      m_position = new CharStreamPosition(0,1,-1);
    }

    /** Adds a listener to this scanner.
     * @param listener The listener to be added to this scanner. */
    public void addListener(ScannerListenerInterface listener)
    { m_listeners.addElement(listener); }

    /** Removes a listener from this scanner.
     * @param listener The listener to be removed from this scanner. */
    public void removeListener(ScannerListenerInterface listener)
    { m_listeners.removeElement(listener); }

    /** Returns the next token encountered.
     * @return The next token encountered.
     * @exception Thrown if there's no next valid token. */
    public Token nextToken() throws NoSuchTokenException
    {
      String token = null;
      boolean endOfStreamReached = false;
      //char[] currentChar = new char[1];
      while (token==null && !endOfStreamReached)
      {
        try
        {
          charStream.mark(1);
          if (charStream.read(m_currentChar)==-1)
            endOfStreamReached = true;
          else
          {
            m_previousPosition.setPosition(m_position);
            m_position.setColumn(m_position.getColumn()+1);
            m_position.setCharactersOffset(m_position.getCharactersOffset()+1);
            //if (currentChar[0]=='\n')

            if (FSA.getTransitionFor(m_currentChar[0])!=null)
            {
              FSA.sendChar(m_currentChar[0]);
              m_currentLine.append(m_currentChar);
              if (m_currentChar[0]=='\n')
              {
                notifyListenersForLineScanned(new String(m_currentLine));
                m_currentLine = new StringBuffer();
              }
            }
            else
            {
              if (FSA.getCurrentState().isTokenState())
              {
                //==================a valid token has been found.
                try
                {
                  //the last character will be reinjected => we go back from one character.
                  charStream.reset();
                  m_position.setPosition(m_previousPosition);
                }
                catch (IOException ex)
                {ex.printStackTrace();}
                token = FSA.getReceivedCharacters();
              }
              else
              {
                //=================an invalid character has been found.
                m_currentLine.append(m_currentChar);
                notifyListenersForInvalidCharacter(m_currentChar[0], (CharStreamPosition)m_position.clone());
                FSA.initialize();
                //===== line changed in case on invalid character
                if (m_currentChar[0]=='\n')
                {
                  notifyListenersForLineScanned(new String(m_currentLine));
                  m_currentLine = new StringBuffer();
                  m_position.setColumn(0);
                  m_position.setLine(m_position.getLine()+1);
                }
                //===== end of line changed in case on invalid character
              }
            }
          }
        }
        catch (java.io.IOException e)
        { e.printStackTrace(); }
      }
      if (endOfStreamReached)
      {
        if (FSA.getCurrentState().isTokenState())
          token = FSA.getReceivedCharacters();
        else
        {
          FSA.initialize();
          throw new NoSuchTokenException();
        }
      }
      TokenType currentState = FSA.getCurrentState().getType();
      FSA.initialize();
      Token tok = new Token(token,
                            currentState,
                            new CharStreamPosition(m_position.getColumn()-(token.length()-1)
                                                   , m_position.getLine()
                                                   , m_position.getCharactersOffset()-(token.length()-1)));
      notifyListenersForToken(tok);
      if (token.indexOf('\n')!=-1)
      {
        m_position.setColumn(0);
        m_position.setLine(m_position.getLine()+1);
      }
      return tok;
    }

    /** Returns <TT>true</TT> if there's any character left.
     * @return <TT>true</TT> if there's any character left, <TT>false</TT>
     * otherwise. */
    public boolean hasNext()
    {
      int hasNext = -1;
      try
      {
        charStream.mark(0);
        hasNext = charStream.read(new char[1]);
        charStream.reset();
      }
      catch (IOException e)
      { e.printStackTrace(); }
      return (hasNext!=-1);
    }

    /** Returns the current finale state automata currently used to separate
     * tokens.
     * @return the current finale state automata currently used to separate
     * tokens. */
    public FinaleStateAutomata getFinaleStateAutomata()
    { return FSA; }

    public void setFinaleStateAutomata(FinaleStateAutomata fsa)
    { FSA = fsa; }

    /** Returns the position of the scanner if the input stream.
     * @return The position of the scanner if the input stream. The last processed
     * character position. */
    public CharStreamPosition getPosition()
    { return m_position; }

    public String getCurrentLine()
    { return new String(m_currentLine); }

    protected void notifyListenersForToken(Token token)
    {
      TokenEvent evt = new TokenEvent(this, token);
      for (int i=0; i<m_listeners.size();i++)
        ((ScannerListenerInterface)m_listeners.elementAt(i)).tokenGenerated(evt);
    }

    protected void notifyListenersForInvalidCharacter(char character, CharStreamPosition pos)
    {
      InvalidCharacterEvent evt = new InvalidCharacterEvent(this, character, pos);
      for (int i=0; i<m_listeners.size();i++)
        ((ScannerListenerInterface)m_listeners.elementAt(i)).invalidCharacter(evt);
    }

    protected void notifyListenersForLineScanned(String line)
    {
      for (int i=0; i<m_listeners.size();i++)
        ((ScannerListenerInterface)m_listeners.elementAt(i)).lineProcessed(line);
    }
}

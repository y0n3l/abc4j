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
package abc.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import abc.notation.Tune;
import abc.parser.AbcNode;
import abc.parser.AbcTokens;
import abc.parser.CharStreamPosition;
import abc.parser.PositionableInCharStream;
import abc.parser.TuneParser;
import abc.parser.TuneParserListenerInterface;

/** A JTextPane to display and edit abc tunes. This pane handles copy/paste.
 * actions. */
public class TuneEditorPane extends JTextPane implements AbcTokens//, ActionListener
{

  private static final long serialVersionUID = 2835190367574934182L;
  private static Color BACKGROUND_COLOR = new Color(249,234,202);
  private static Color FIELDS_COLOR = new Color (0,128,0);
  private static Color GRACING_FOREGROUND_COLOR = Color.green;
  private static Color GRACING_BACKGROUND_COLOR = new Color(220,255,220);
  private static Color SYMBOL_FOREGROUND_COLOR = new Color(128, 128, 0);
  private static Color TUPLET_FOREGROUND_COLOR = new Color(128,0,255);
  private static Color SELECTION_FOREGROUND_COLOR = Color.white;//new Color(10,36,106);
  private static Color SELECTION_BACKGROUND_COLOR = new Color(10,36,106);
  private static Color BAR_COLOR = new Color (0,0,128);
  private static final String COPY_ACTION = "Copy";
  private static final String PASTE_ACTION = "Paste";
  private static final String ERROR_STYLE = "error";
  private static final String BARS_STYLE = "bars";
  private static final String TEXT_STYLE = "text";
  private static final String COMMENT_STYLE = "comment";
  private static final String XCOMMAND_STYLE = "xcommand";
  private static final String NOTE_STYLE = "note";
  private static final String NOTE_ATTR_STYLE = "noteAttr";
  private static final String REST_STYLE = "rest";
  private static final String GRACING_STYLE = "note";
  private static final String SYMBOL_STYLE = "symbol";
  private static final String FIELD_STYLE = "field";
  private static final String RHYTHM_STYLE = "rhythm";
  private static final String DEFAULT_STYLE = "rhythm";
  public static final String REFRESHER_THREAD_NAME = "ABC-TunePaneRefresh";

  //private static final boolean ENABLE_COLORING = true;

  //private int nbApply =0;
  private boolean m_forceRefresh = false;
  /** */
  private Style m_barStyle, m_textStyle, m_errorStyle,
  	m_fieldStyle, m_rhythmStyle, m_defaultStyle, m_baseNoteStyle,
  	m_commentStyle, m_xcommandStyle, m_gracingStyle, m_restStyle,
  	m_noteAttrStyle, m_symbolStyle = null;
  private static final int IDLE_TIME_BEFORE_REFRESH = 200;
  /** The thread in charge of refreshing the tune representation of this editor pane. */
  private ParsingRefresh m_refresher = null;
  /** The tune currently represented in this editor pane. */
  private Tune m_tune = null;
  //private TuneParser m_tuneParser = null;
  private int m_idleTimeBeforeRefresh = IDLE_TIME_BEFORE_REFRESH;
  private boolean m_enableColoring = false;

  /** Default constructor. */
  public TuneEditorPane()
  { this(new TuneParser()); }

  /** Creates a new TuneEditorPane with the given parser.
   * @param parser The parser to be used to parse the abc notation
   * written in the TuneEditorPane and to give the resulting {@link abc.notation.Tune tune} */
  public TuneEditorPane(TuneParser parser)
  { this(parser, IDLE_TIME_BEFORE_REFRESH); }

  /** Creates a new TuneEditorPane with the given idle time before refresh.
   * @param idleTimeBeforeRefresh The idle time after any text update
   * before triggering the parsing of the abc text written in this pane.
   * This is used to avoid perpetual parsing of the text at each text update */
  public TuneEditorPane(int idleTimeBeforeRefresh)
  { this(new TuneParser(), idleTimeBeforeRefresh); }

  /** Creates a new TuneEditorPane with the given parser and
   * idle time before refresh.
   * @param parser The parser to be used to parse the abc notation
   * written in the TuneEditorPane and to give the resulting {@link abc.notation.Tune tune}
   * @param idleTimeBeforeRefresh The idle time after any text update
   * before triggering the parsing of the abc text written in this pane.
   * This is used to avoid perpetual parsing of the text at each text update */
  public TuneEditorPane(TuneParser parser, int idleTimeBeforeRefresh) {
    setBackground(BACKGROUND_COLOR);
    setSelectedTextColor(SELECTION_FOREGROUND_COLOR);
    setSelectionColor(SELECTION_BACKGROUND_COLOR);
    setFont(new Font("Courier", Font.PLAIN, 12));
    //m_tuneParser = parser;
    m_refresher = new ParsingRefresh((DefaultStyledDocument)getDocument(), parser);
    KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);
    // Identifying the copy KeyStroke user can modify this
    // to copy on some other Key combination.
    KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false);
    // Identifying the Paste KeyStroke user can modify this
    //to copy on some other Key combination.
    MyActionListener myAL = new MyActionListener();
    registerKeyboardAction(myAL,COPY_ACTION,copy,JComponent.WHEN_FOCUSED);
    registerKeyboardAction(myAL,PASTE_ACTION,paste,JComponent.WHEN_FOCUSED);
    createStyles();
    setCharacterAttributes(m_defaultStyle, true);
  }

  public boolean getScrollableTracksViewportWidth()
  { return false;  }

  	/** Returns the parser used to build up the tune from the
  	 * abc text written in this pane.
  	 * @return the parser used to build up the tune from the
  	 * abc text written in this pane. */
  	public TuneParser getParser() {
  		return m_refresher.getParser();
  	}

  /** Returns <TT>true</TT> if text coloring has been enabled, <TT>false</TT>
   * otherwise.
   * @return <TT>true</TT> if text coloring has been enabled, <TT>false</TT>
   * otherwise. Text coloring enables user to distinguish abc tokens detected in
   * the text of this pane.
   * @see #setColoringEnable(boolean) */
  public boolean isColoringEnabled()
  { return m_enableColoring; }

  /** Enables/disables coloring of the text.
   * @param coloring <TT>true</TT> if text coloring should be enabled,
   * <TT>false</TT> otherwise.
   * @see #isColoringEnabled() */
  public void setColoringEnable(boolean coloring)
  {
    m_enableColoring = coloring;
    if (m_enableColoring)
      m_refresher.redrawTune();
    else
    {
        ((DefaultStyledDocument)getDocument()).setCharacterAttributes(
    0,getDocument().getEndPosition().getOffset(),m_defaultStyle, true);
      //setSelectedTextColor(SELECTION_FOREGROUND_COLOR);
      //setSelectionColor(SELECTION_BACKGROUND_COLOR);
    }
  }

  	/** Highlights the specified element in the abc tune notation.
  	 * @param elmnt The element to be highlighted in the abc tune notation. */
  	public void setSelectedItem(PositionableInCharStream elmnt) {
  		CharStreamPosition pos = null;
  		if (elmnt != null)
  			pos = elmnt.getCharStreamPosition();
  		if (pos != null) {
	  		int begin = pos.getStartIndex();
	  		int end = pos.getEndIndex();
	  		try	{
	  			setCaretPosition(end);
				moveCaretPosition(begin);
				getCaret().setSelectionVisible(true);
				repaint();
	  		}
	  		catch (IllegalArgumentException excpt)
	  		{}
  		}
  	}

  public void setSize(Dimension d)
  {
    if(d.width < getParent().getSize().width)
      d.width = getParent().getSize().width;
    super.setSize(d);
  }

  	/** Returns the tune that is currently described in this tune editor.
  	 * @return The tune that is currently described in this tune editor. */
  	public Tune getTune() {
  		return m_tune;
  	}

  /*public void setDocument(Document doc)
  {
    //System.out.println("TuneEditorPane - setDocument(" + doc  + ")");
    //m_forceRefresh = true;
    super.setDocument(doc);
    //if (m_refresher!=null)
    //{
    //  setCharacterAttributes(m_defaultStyle, true);
    //  m_refresher.setDocument((DefaultStyledDocument)doc);
    //}
  }*/

  public void setText(String text)
  {
/*  	try
  	{

    //System.out.println("TuneEditorPane - setText(" + text  + ")");
    DefaultStyledDocument doc = new DefaultStyledDocument();
    doc.insertString(0,text,m_defaultStyle);
    m_refresher.setDocument(doc);
    this.setDocument(doc);
    }
      catch (Exception e)
      { e.printStackTrace();
      }*/

    try
    {
    	m_refresher.stopIt();
       	getDocument().remove(0,getDocument().getLength()/*-1*/);
    	getDocument().insertString(0,text,m_defaultStyle);
    	m_refresher.startIt();
    }
    catch (Exception e)
    {e.printStackTrace();
    }
    //super.setText(text);
    //m_tuneParser.parse(text);
  }

  private void createStyles()
  {
    m_defaultStyle = addStyle(DEFAULT_STYLE, null);
    StyleConstants.setFontFamily(m_defaultStyle, "Courier");
    m_errorStyle = addStyle(ERROR_STYLE, m_defaultStyle);
    StyleConstants.setBackground(m_errorStyle, Color.red);
    StyleConstants.setForeground(m_errorStyle, Color.yellow);
    StyleConstants.setBold(m_errorStyle, true);
    m_baseNoteStyle = addStyle(NOTE_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_baseNoteStyle, Color.red);
    m_noteAttrStyle = addStyle(NOTE_ATTR_STYLE, m_defaultStyle);
    //StyleConstants.setForeground(m_noteAttrStyle, new Color(255,128,255));
    m_restStyle = addStyle(REST_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_restStyle, new Color(128,0,0));
    m_gracingStyle = addStyle(GRACING_STYLE, m_defaultStyle);
    StyleConstants.setBackground(m_gracingStyle, GRACING_BACKGROUND_COLOR);
    StyleConstants.setForeground(m_gracingStyle, GRACING_FOREGROUND_COLOR);
    m_symbolStyle = addStyle(SYMBOL_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_symbolStyle, SYMBOL_FOREGROUND_COLOR);
    m_textStyle = addStyle(TEXT_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_textStyle, Color.blue);
    m_commentStyle = addStyle(COMMENT_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_commentStyle, Color.white);
    StyleConstants.setBackground(m_commentStyle, Color.gray);
    m_xcommandStyle = addStyle(XCOMMAND_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_xcommandStyle, Color.white);
    StyleConstants.setBackground(m_xcommandStyle, Color.gray);
    StyleConstants.setBold(m_xcommandStyle, true);
    m_barStyle = addStyle(BARS_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_barStyle, BAR_COLOR);
    StyleConstants.setBold(m_barStyle, true);
    m_fieldStyle = addStyle(FIELD_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_fieldStyle, FIELDS_COLOR);
    StyleConstants.setBold(m_fieldStyle, true);
    StyleConstants.setBold(m_fieldStyle, true);
    m_rhythmStyle = addStyle(RHYTHM_STYLE, m_defaultStyle);
    StyleConstants.setForeground(m_rhythmStyle, TUPLET_FOREGROUND_COLOR);
    StyleConstants.setBold(m_rhythmStyle, true);
  }

  private class MyActionListener implements ActionListener {

  /** This method is activated on the Keystrokes we are listening to
   * in this implementation. Here it listens for Copy and Paste ActionCommands. */
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().compareTo(COPY_ACTION)==0)
    {
      //StringBuffer sbf=new StringBuffer();
      //System.out.println("COPY ! : " + getSelectedText());
      StringSelection stsel  = new StringSelection(getSelectedText());
      Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
      system.setContents(stsel,stsel);
    }
    if (e.getActionCommand().compareTo(PASTE_ACTION)==0)
    {
      Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
      try
      {
        String selectedText = getSelectedText();
        String trstring= (String)(system.getContents(this).getTransferData(DataFlavor.stringFlavor));
        //System.out.println("PASTE ! : " + trstring);
        if (selectedText!=null)
          getDocument().remove(getCaretPosition(), selectedText.length());
        getDocument().insertString(getCaretPosition(), trstring, m_defaultStyle);
      }
      catch(Exception ex)
      {ex.printStackTrace();}
    }
  }
  }

  private class ParsingRefresh extends Thread implements DocumentListener, TuneParserListenerInterface
  {
    //private int m_elapsedTimeSinceLastParsing = 0;
    private DefaultStyledDocument m_document = null;
    private TuneParser m_parser = null;
    private int m_idleTime = 0;
    private Object m_mutex = new Object();
    private AbcNode m_abcRoot = null;
    private boolean isBusy = false;

    public ParsingRefresh(DefaultStyledDocument document, TuneParser parser)
    {
      super(REFRESHER_THREAD_NAME);
      start();
      m_parser = parser;
      m_parser.addListener(this);
      m_document = document;
      startIt();
    }

    public void startIt()
    {
    	m_document.addDocumentListener(this);
    	m_forceRefresh=true;
    	synchronized(m_mutex)
      	{
        	m_mutex.notify();
      	}

    }

    public void stopIt()
    {
    	m_document.removeDocumentListener(this);
    }

    public TuneParser getParser()
    { return m_parser; }

    public void setDocument(DefaultStyledDocument doc)
    {
    	try
    	{
      // System.out.println(this.getClass().getName() + " - setDocument(" + doc + ")");
      m_document.removeDocumentListener(this);
      m_document = doc;
      m_tune = m_parser.parse(doc.getText(0, doc.getLength()));
      m_document.addDocumentListener(this);
      }
      catch (Exception e)
      { e.printStackTrace();
      }
    }

    public void run()
    {
      while (true)
      {
        try
        {
          synchronized(m_mutex)
          {
            //System.out.println("Area - wait()");
            m_mutex.wait();
            if (!m_forceRefresh)
            {
              do
              {
                //System.out.println("Area - compting before parsing : " + m_idleTime);
                m_mutex.wait(10);
                m_idleTime+=10;
              }
              while (m_idleTime<=m_idleTimeBeforeRefresh);
            }
            try
            {

            	String tuneNotation = TuneEditorPane.this.getDocument().getText(0, TuneEditorPane.this.getDocument().getLength());
            	if (!tuneNotation.equals(""))
            	{
            		if (m_forceRefresh==true)
	            	{
    	          		m_forceRefresh = false;
        	      		//System.out.println("Area - Forcing refresh");
            		}
            		//System.out.println("Area - parsing("+ tuneNotation +")");
              		m_tune = m_parser.parse(tuneNotation);
            	}
            }
            catch (Exception e)
            { e.printStackTrace(); }
           }
        }
        catch (InterruptedException e)
        { e.printStackTrace(); }
      }
    }

    public void changedUpdate(DocumentEvent e)
    {
    }

    public void insertUpdate(DocumentEvent e)
    {
      synchronized(m_mutex)
      {
        m_mutex.notify();
        m_idleTime=0;
      }
    }
    
    public boolean isBusy() {
    	return isBusy;
    }

    public void removeUpdate(DocumentEvent e)
    {
      synchronized(m_mutex)
      {
        m_mutex.notify();
        m_idleTime=0;
      }
    }
    public void tuneBegin()
    {
    	m_abcRoot = null;
    	isBusy = true;
    }
    public void noTune()
    {
    	m_abcRoot = null;
    	isBusy = false;
    }

    public void tuneEnd(Tune tune, AbcNode abcNodeRoot)
    {
    	isBusy = false;
    	m_tune = tune;
    	m_abcRoot = abcNodeRoot;
      if (m_enableColoring)
      {
        //===========================================================THOSE CALLBACK CREATE A DEADLOCK WHEN SETTING TEXT ATTRIBUTES.
        try
        {
          javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              redrawTune();
            }
          });
        }
        catch (Exception e)
        { e.printStackTrace(); }
      }
    }

    private void redrawTune() {
    	if (m_abcRoot == null)
    		return; //Nothing to do
		m_document.setCharacterAttributes(0, m_document.getLength(), m_defaultStyle, true);
		
		int maxStart = 0;
    	Iterator it = m_abcRoot.getDeepestChilds().iterator();
    	while (it.hasNext()) {
    		AbcNode node = (AbcNode) it.next();
    		String value = node.getValue();
    		int length = value.length();
    		int start = node.getCharStreamPosition().getStartIndex();
    		if (start < maxStart) {
    			length -= (maxStart - start);
    			start = maxStart;
    		}
    		maxStart += length;
    		
    		Style att = m_defaultStyle;
    		if (node.hasError()) {
    			att = m_errorStyle;
    			if (length == 0) {
    				length = 1;
    				maxStart++;
    			}
    		}
    		else if (node.isChildOf_or_is(Pitch))
   				att = m_baseNoteStyle;
    		else if (node.isChildOf_or_is(NoteLength)
    				|| node.isChildOf_or_is(_Accidental)
    				|| node.isChildOf_or_is(Tie))
				att = m_noteAttrStyle;
    		else if (node.isChildOf_or_is(Barline)
    				|| node.isChildOf_or_is(NthRepeat)
    				|| node.isChildOf_or_is(EndNthRepeat)
    				|| node.isChildOf_or_is(MeasureRepeat)
    				|| node.isChildOf_or_is(LineContinuation)
    				|| node.isChildOf_or_is(HardLineBreak))
    			att = m_barStyle;
    		else if (node.isChildOf_or_is(Rest)
    				|| node.isChildOf_or_is(MultiMeasureRest))
    			att = m_restStyle;
    		else if (node.isChildOf_or_is(TexText)
    				|| node.isChildOf_or_is(ChordOrText))
    			att = m_textStyle;
    		else if (node.isChildOf_or_is(Comment))
    			att = m_commentStyle;
    		else if (node.isChildOf_or_is(Xcommand))
    			att = m_xcommandStyle;
    		else if (node.isChildOf_or_is(TuneField)
    				|| node.isChildOf_or_is(InlineField)
    				|| node.isChildOf_or_is(AbcHeader))
    			att = m_fieldStyle;
    		else if (node.isChildOf_or_is(Tuplet)
    				|| node.isChildOf_or_is(BrokenRhythm))
    			att = m_rhythmStyle;
    		else if (node.isChildOf_or_is(Gracing))
				att = m_symbolStyle;
    		else if (node.isChildOf_or_is(GraceNotes))
    			att = m_gracingStyle;
    		m_document.setCharacterAttributes(start, length, att, true);
		}
	}
  }

}

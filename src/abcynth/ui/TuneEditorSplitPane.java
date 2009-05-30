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
package abcynth.ui;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import abc.ui.swing.TuneEditorPane;

/** A pane for displaying tunes. */
public class TuneEditorSplitPane extends JSplitPane// implements TuneParserListenerInterface
{
  private static final long serialVersionUID = -7079256875832517566L;
  private TuneEditorPane m_tunePane = null;
  //private TuneParser m_parser = null;
  private JList m_errorsList = null;

  public TuneEditorSplitPane()
  {
    super(JSplitPane.VERTICAL_SPLIT);
    setOneTouchExpandable(true);
    //setContinuousLayout(true);
    m_tunePane = new TuneEditorPane();
    m_errorsList = new ErrorsList(m_tunePane.getParser());
    //m_parser = m_tunePane.getParser();
    //m_parser.addListener(this);
    setTopComponent(new JScrollPane(m_tunePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    //m_errorsList = new JList(new ErrorsListModel());
    m_errorsList.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(ListSelectionEvent e)
      {
        //if (!e.getValueIsAdjusting())
        //{
        ErrorsList.Error err = (ErrorsList.Error)(m_errorsList.getModel()).getElementAt(m_errorsList.getSelectedIndex());
        //System.out.println("Selected error :"  + err);
        try
        {
          m_tunePane.setCaretPosition(err.getBeginOffset());
          m_tunePane.moveCaretPosition(err.getEndoffset());
          m_tunePane.getCaret().setSelectionVisible(true);
          m_tunePane.repaint();
        }
        catch (IllegalArgumentException excpt)
        {}
        //}
      }});
    setBottomComponent(new JScrollPane(m_errorsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
  }

/*  public void invalidToken(InvalidTokenEvent evt)
  {
    try
    {
    String message = "Expecting "
        + evt.getExpectedTokenType().toString()
        +  " at line "  + evt.getPosition().getLine()
        + ", column " + evt.getPosition().getColumn();
    if (evt.getToken()!=null)
    {
      message = message.concat(" instead of " + evt.getToken().toString());
      ((ErrorsListModel)m_errorsList.getModel()).addError(new Error(message, evt.getToken().getPosition().getCharactersOffset(),
          evt.getToken().getPosition().getCharactersOffset()+evt.getToken().getValue().length()));
    }
    else
      ((ErrorsListModel)m_errorsList.getModel()).addError(new Error(message, evt.getPosition().getCharactersOffset(),
          evt.getPosition().getCharactersOffset()+1));
    }
    catch (NullPointerException e)
    {
      e.printStackTrace();
    }
  }*/

  public TuneEditorPane getTuneEditorPane()
  { return m_tunePane; }

  /*public void tuneBegin()
  { ((ErrorsListModel)m_errorsList.getModel()).removeAllErrors(); }

  public void tuneEnd(Tune tune)
  {
    //m_errorsList.setSelectedIndex(-1);
  }

  public void validToken(TokenEvent evt)
  { }

  public void invalidCharacter(InvalidCharacterEvent evt)
  {
    String message = "Invalid character '" + evt.getCharacter() + "' "
        +  "at line " + evt.getPosition().getLine() + ", column " + evt.getPosition().getColumn();
    ((ErrorsListModel)m_errorsList.getModel()).addError(new Error(message, evt.getPosition().getCharactersOffset(), evt.getPosition().getCharactersOffset()+1));
  }

  private class ErrorsListModel extends AbstractListModel
  {
    private Vector m_errors = null;

    public ErrorsListModel()
    {
      super();
      m_errors = new Vector();
    }

    public Object getElementAt(int index)
    { return m_errors.elementAt(index); }

    public int getSize()
    { return m_errors.size(); }

    public void removeAllErrors()
    {
      m_errors.removeAllElements();
      fireContentsChanged(this,0,0);
    }

    public void addError(Object o)
    {
      m_errors.addElement(o);
      fireContentsChanged(this,m_errors.size()-1,m_errors.size()-1);
    }
  }

  private class Error
  {
    private String m_message = null;
    private int m_beginOffset = 0;
    private int m_endOffset = 0;

    public Error(String message, int beginOffset, int endOffset)
    {
      m_message = message;
      m_beginOffset = beginOffset;
      m_endOffset = endOffset;
    }

    public int getBeginOffset()
    { return m_beginOffset; }

    public int getEndoffset()
    { return m_endOffset; }

    public String getMessage()
    { return m_message; }

    public String toString()
    { return m_message; }
  }*/
}
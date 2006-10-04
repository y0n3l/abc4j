package abc.ui.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.SystemColor;
import scanner.*;
import abc.notation.*;
import abc.parser.*;
import abc.ui.swing.*;
import java.util.*;
import java.io.*;

/** */
public class ErrorsList extends JList implements TuneParserListenerInterface
{
  //private TuneEditorPane m_tunePane = null;
  private TuneParser m_parser = null;
  //private JList m_errorsList = null;

  public ErrorsList(TuneParser parser)
  {
    //setContinuousLayout(true);
    super(new ErrorsListModel());
    m_parser = parser;
    m_parser.addListener(this);
    //setTopComponent(new JScrollPane(m_tunePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    //m_errorsList = new JList(new ErrorsListModel());
    /*m_errorsList.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(ListSelectionEvent e)
      {
        //if (!e.getValueIsAdjusting())
        //{
        Error err = (Error)((ErrorsListModel)m_errorsList.getModel()).getElementAt(m_errorsList.getSelectedIndex());
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
    setBottomComponent(new JScrollPane(m_errorsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));*/
  }

  public void setParser(TuneParser parser)
  {
    m_parser.removeListener(this);
    m_parser = parser;
    m_parser.addListener(this);
  }

  public void invalidToken(InvalidTokenEvent evt)
  {
    try
    {
    String message = "Expecting "
        + evt.getExpectedTokenType().toString()
        +  " at line "  + evt.getPosition().getLine()
        + ", column " + evt.getPosition().getColumn();
    if (evt.getToken()!=null)
    {
      message = message.concat(" instead of " + evt.getToken().getType());
      ((ErrorsListModel)getModel()).addError(new Error(message, evt.getToken().getPosition().getCharactersOffset(),
          evt.getToken().getPosition().getCharactersOffset()+evt.getToken().getValue().length()));
    }
    else
      ((ErrorsListModel)getModel()).addError(new Error(message, evt.getPosition().getCharactersOffset(),
          evt.getPosition().getCharactersOffset()+1));
    }
    catch (NullPointerException e)
    {
      e.printStackTrace();
    }
  }

  public void tuneBegin()
  {
    ((ErrorsListModel)getModel()).removeAllErrors();
  }

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
    ((ErrorsListModel)getModel()).addError(new Error(message, evt.getPosition().getCharactersOffset(), evt.getPosition().getCharactersOffset()+1));
  }

  private static class ErrorsListModel extends AbstractListModel
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

  public class Error
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
  }
}
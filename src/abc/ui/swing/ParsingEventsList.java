package abc.ui.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.table.*;
import java.awt.SystemColor;
import scanner.*;
import abc.notation.*;
import abc.parser.*;
import abc.ui.swing.*;
import java.util.*;
import java.io.*;

/** */
public class ParsingEventsList extends JTable implements TuneParserListenerInterface
{
  private TuneParser m_parser = null;
  private ParsingEventsTableModel m_model = null;

  public ParsingEventsList(TuneParser parser)
  {
    //setContinuousLayout(true);
    super();
    m_model= new ParsingEventsTableModel(parser);
    setModel(m_model);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    m_parser = parser;
    m_parser.addListener(this);
  }

  public void setParser(TuneParser parser)
  {
    m_parser.removeListener(this);
    m_parser = parser;
    m_parser.addListener(this);
  }

  public void tuneBegin()
  {
    //getSelectionModel().setLeadSelectionIndex(0);
    //getSelectionModel().setValueIsAdjusting(false);
    //System.out.println("The selection is now : " + getSelectionModel().getLeadSelectionIndex());
    ((ParsingEventsTableModel)getModel()).removeAllErrors();
  }

  public void tuneEnd(Tune tune)
  {
    m_model.fireTableDataChanged();
    //setSelectedIndex(-1);
  }

  public void invalidToken(InvalidTokenEvent evt)
  {
    ((ParsingEventsTableModel)getModel()).addEvent(evt);
  }

  public void validToken(TokenEvent evt)
  {
    ((ParsingEventsTableModel)getModel()).addEvent(evt);
  }

  public void invalidCharacter(InvalidCharacterEvent evt)
  {
    ((ParsingEventsTableModel)getModel()).addEvent(evt);  }

/*  private static class EventsListModel extends AbstractListModel
  {
    private Vector m_events = null;

    public EventsListModel()
    {
      super();
      m_events = new Vector();
    }

    public Object getElementAt(int index)
    { return m_events.elementAt(index); }

    public int getSize()
    { return m_events.size(); }

    public void removeAllErrors()
    {
      m_events.removeAllElements();
      fireContentsChanged(this,0,0);
    }

    public void addEvent(Object o)
    {
      m_events.addElement(o);
      fireContentsChanged(this,m_events.size()-1,m_events.size()-1);
    }
  }*/

  public class ParsingEventsTableModel extends AbstractTableModel// implements TuneBookListenerInterface
  {
    private TuneParser m_tuneParser = null;
    private Vector m_events = null;


    public ParsingEventsTableModel(TuneParser parser)
    {
      m_tuneParser = parser;
      m_events = new Vector();
    }

    public int getSize()
    { return m_events.size(); }

    public void removeAllErrors()
    {

      m_events.removeAllElements();
      //fireTableDataChanged();
    }

    public EventObject getEvent(int index)
    { return (EventObject)m_events.elementAt(index); }

    public void addEvent(Object o)
    {
      m_events.addElement(o);
      //fireTableDataChanged();
    }

    public int getColumnCount()
    {
      return 4;
    }
    public int getRowCount()
    { return m_events.size();}

    public Object getValueAt(int row, int col)
    {
      try
      {
        if (m_events.elementAt(row)!=null &&
            (m_events.elementAt(row) instanceof TokenEvent
            ||m_events.elementAt(row) instanceof InvalidCharacterEvent))
        {
          if (m_events.elementAt(row) instanceof InvalidTokenEvent)
          {
            InvalidTokenEvent evt = (InvalidTokenEvent)m_events.elementAt(row);
            if (col==0)
              if (evt.getToken()==null)
                return "NO TYPE";
              else
                return evt.getToken().getType();
            else
            if (col==1)
              if (evt.getToken()==null)
                return "NO VALUE";
              else
                return evt.getToken().getValue();
            else
            if (col==2)
              return evt.getPosition();
            else
            if (col==3)
              return evt.getExpectedTokenType();
          }
          else
          if (m_events.elementAt(row) instanceof TokenEvent)
          {
            TokenEvent evt = (TokenEvent)m_events.elementAt(row);
            if (col==0)
              return evt.getToken().getType();
            else
            if (col==1)
              return evt.getToken().getValue();
            else
            if (col==2)
              return evt.getToken().getPosition();
            else
            if (col==3)
              return "";
          }
          else
          if (m_events.elementAt(row) instanceof InvalidCharacterEvent)
          {
            InvalidCharacterEvent evt = (InvalidCharacterEvent)m_events.elementAt(row);
            if (col==0)
              return "Invalid Char";
            else
            if (col==1)
              return new Character(evt.getCharacter());
            else
            if (col==2)
              return evt.getPosition();
            else
            if (col==3)
              return "";
          }
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();
        System.out.println("CRASHING FOR "  + m_events.elementAt(row));
      }
      return "";
      /*try
      {
      //System.out.println(row + " " + col);
      TuneBookTableColumnModel model = (TuneBookTableColumnModel)TuneBookTable.this.getColumnModel();
      TuneColumn column = (TuneColumn)model.getColumnFromModelIndex(col);
      Object obj = column.getValueFor((Tune)m_tunes.elementAt(row));
      return obj;
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        return null;
      }*/
    };

  }
}
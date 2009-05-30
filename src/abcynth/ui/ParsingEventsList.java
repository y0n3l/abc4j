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

import java.util.EventObject;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import scanner.InvalidCharacterEvent;
import scanner.TokenEvent;
import abc.notation.Tune;
import abc.parser.InvalidTokenEvent;
import abc.parser.TuneParser;
import abc.parser.TuneParserListenerInterface;

/** */
public class ParsingEventsList extends JTable implements TuneParserListenerInterface
{
  private static final long serialVersionUID = -5448619364935299044L;
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
    private static final long serialVersionUID = -4695151725324770777L;
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
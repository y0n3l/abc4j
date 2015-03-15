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

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import abc.notation.Tune;
import abc.parser.AbcNode;
import abc.parser.AbcParseError;
import abc.parser.TuneParser;
import abc.parser.TuneParserListenerInterface;

/** */
public class ParsingEventsList extends JTable implements TuneParserListenerInterface
{
  private static final long serialVersionUID = -5448619364935299044L;
  private TuneParser m_parser = null;
  private ParsingEventsTableModel m_model = null;
  private boolean isBusy = false;

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
	  isBusy = true;
    //getSelectionModel().setLeadSelectionIndex(0);
    //getSelectionModel().setValueIsAdjusting(false);
    //System.out.println("The selection is now : " + getSelectionModel().getLeadSelectionIndex());
    ((ParsingEventsTableModel)getModel()).removeAllErrors();
  }
  
  public void noTune() { isBusy = false; }
  
  public boolean isBusy() { return isBusy; }

  public void tuneEnd(Tune tune, AbcNode abcRoot)
  {
	isBusy = false;
    m_model.fireTableDataChanged();
    if (abcRoot != null) {
	    Iterator it = abcRoot.getDeepestChilds().iterator();
	    while (it.hasNext()) {
	        ((ParsingEventsTableModel)getModel()).addEvent((AbcNode) it.next());
	    }
    }
    //setSelectedIndex(-1);
  }

  public class ParsingEventsTableModel extends AbstractTableModel// implements TuneBookListenerInterface
  {
    private static final long serialVersionUID = -4695151725324770777L;
	//private TuneParser m_tuneParser = null;
    private Vector<Object> m_events = null;


    public ParsingEventsTableModel(TuneParser parser)
    {
      //m_tuneParser = parser;
      m_events = new Vector<Object>();
    }

    public int getSize()
    { return m_events.size(); }

    public void removeAllErrors()
    {

      m_events.removeAllElements();
      //fireTableDataChanged();
    }

    public AbcNode getEvent(int index)
    { return (AbcNode)m_events.elementAt(index); }

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

    public Object getValueAt(int row, int col) {
			try {
				if (m_events.elementAt(row) != null) {
					AbcNode evt = (AbcNode) m_events.elementAt(row);
					if (col == 0)
						return evt.getLabel();
					else if (col == 1)
						return evt.getValue();
					else if (col == 2)
						return evt.getCharStreamPosition();
					else if (col == 3) {
						if (evt.hasError())
							return ((AbcParseError) evt.getErrors().get(0))
									.getErrorMessage();
						else
							return "";
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("CRASHING FOR " + m_events.elementAt(row));
			}
			return "";
		};

  }
}
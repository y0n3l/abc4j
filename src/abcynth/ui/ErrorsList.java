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

import javax.swing.AbstractListModel;
import javax.swing.JList;

import abc.notation.Tune;
import abc.parser.AbcNode;
import abc.parser.AbcParseError;
import abc.parser.TuneParser;
import abc.parser.TuneParserListenerInterface;

/** */
public class ErrorsList extends JList implements TuneParserListenerInterface
{
  private static final long serialVersionUID = -3624031033400760255L;
  //private TuneEditorPane m_tunePane = null;
  private TuneParser m_parser = null;
  //private JList m_errorsList = null;
  private boolean isBusy = false;

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

  public void tuneBegin()
  {
	  isBusy = true;
    ((ErrorsListModel)getModel()).removeAllErrors();
  }
  
  public boolean isBusy() { return isBusy; }
  
  public void noTune() { isBusy = false; }

  public void tuneEnd(Tune tune, AbcNode abcRoot) {
	  isBusy = false;
	  if (abcRoot != null) {
		Iterator it = abcRoot.getErrors().iterator();
		while (it.hasNext()) {
			AbcParseError ape = (AbcParseError) it.next();
			String message = ape.getErrorMessage() + " at line "
					+ ape.getCharStreamPosition().getLine() + ", column "
					+ ape.getCharStreamPosition().getColumn();
			((ErrorsListModel) getModel()).addError(new Error(message, ape
					.getCharStreamPosition().getStartIndex(), ape
					.getCharStreamPosition().getEndIndex()));
		}
		// m_errorsList.setSelectedIndex(-1);
	  }
	}

  private static class ErrorsListModel extends AbstractListModel
  {
	private static final long serialVersionUID = 8460605907411650180L;
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
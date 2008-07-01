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
package abcynth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.EventObject;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import scanner.PositionableInCharStream;
import abc.notation.Tune;
import abc.parser.TuneParserAdapter;
import abc.ui.swing.TuneEditorPane;
import abc.ui.swing.JScoreComponent;
import abcynth.ui.ErrorsList;
import abcynth.ui.ParsingEventsList;

/** A pane for displaying tunes. */
public class TuneEditorSplitPane extends JSplitPane// implements TuneParserListenerInterface
{
  private TuneEditorPane m_tunePane = null;
  //private TuneParser m_parser = null;
  private JList m_errorsList = null;
  private ParsingEventsList m_tokensList = null;
  private JScoreComponent m_score = null;
  private JTextArea m_tuneHeaderComments = null;
  private JTabbedPane m_tabbedPane = null;

  private static final String HEADER_TAB_NAME = "Header";
  private static final String ERRORS_TAB_NAME = "Errors";
  private static final String TOKENS_TAB_NAME = "Tokens";
  private static final String SCORE_TAB_NAME = "Score";
  private static final boolean DISPLAY_TOKEN_TAB = false;
  
  private static final int SCORE_SIZE = 38;

  public TuneEditorSplitPane()
  {
    super(JSplitPane.VERTICAL_SPLIT);
    setOneTouchExpandable(true);
    m_tunePane = new TuneEditorPane();
    m_errorsList = new ErrorsList(m_tunePane.getParser());
    m_score = new JScoreComponent();
    m_score.setJustification(true);
    m_score.setSize(SCORE_SIZE);
    m_tunePane.getParser().addListener(new TuneParserAdapter(){
    	public void tuneEnd(Tune tune){
    		m_score.setTune(tune);
    	}
    });
    if (DISPLAY_TOKEN_TAB)
      m_tokensList = new ParsingEventsList(m_tunePane.getParser());
    setTopComponent(new JScrollPane(m_tunePane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
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
    //JPanel upperPanel = new JPanel(new BorderLayout());
    //upperPanel.add(tablePane, BorderLayout.CENTER);
    //setTopComponent(upperPanel);

    if(DISPLAY_TOKEN_TAB)
    m_tokensList.getSelectionModel().addListSelectionListener(
        new ListSelectionListener()
        {
          public void valueChanged(ListSelectionEvent e)
          {
            //System.out.println("token item selected :  " + e.getFirstIndex() + " LAST : " +  e.getLastIndex() + " adjusting : " + e.getValueIsAdjusting());
            int selectedIndex = -1;
            int viewColumnNumber = -1;
            try
            {
              //System.out.println(e);
              selectedIndex = m_tokensList.getSelectionModel().getLeadSelectionIndex();
              ParsingEventsList.ParsingEventsTableModel model = (ParsingEventsList.ParsingEventsTableModel)m_tokensList.getModel();
              EventObject evt = model.getEvent(selectedIndex);
              //viewColumnNumber = selectedIndex.convertColumnIndexToView(TuneBookTable.REFERENCE_NUMBER_COLUMN);
              //if (m_tokensList.getValueAt(selectedIndex, viewColumnNumber)!=null)
              if (selectedIndex!=-1 && selectedIndex<model.getSize())
              {
                //System.out.println("Displaying :" + evt);
                //PositionableInCharStream pos = (PositionableInCharStream)((ParsingEventsList.ParsingEventsTableModel)m_tokensList.getModel()).getEvent(m_tokensList.getSelectedRow());
                PositionableInCharStream pos = (PositionableInCharStream)((ParsingEventsList.ParsingEventsTableModel)m_tokensList.getModel()).getEvent(selectedIndex);
                try
                {
                  m_tunePane.setCaretPosition(pos.getPosition().getCharactersOffset());
                  m_tunePane.moveCaretPosition(pos.getPosition().getCharactersOffset()+pos.getLength());
                  m_tunePane.getCaret().setSelectionVisible(true);
                  m_tunePane.repaint();
                }
                catch (IllegalArgumentException excpt)
                {}
              }
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
              System.err.println("INDEX :" + selectedIndex + " " + viewColumnNumber );
              System.err.println("=" + m_tokensList.getValueAt(selectedIndex, viewColumnNumber));
            }
          }
        }
    );

    m_tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
    //m_tabbedPane.addTab(FIRST_TAB_NAME, panel1);

    m_tuneHeaderComments = new JTextArea();
    m_tuneHeaderComments.setBackground(Color.lightGray);
    m_tuneHeaderComments.setEditable(false);
    m_tabbedPane.addTab(SCORE_TAB_NAME, new JScrollPane(m_score, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    m_tabbedPane.addTab(ERRORS_TAB_NAME, new JScrollPane(m_errorsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    m_tabbedPane.addTab(HEADER_TAB_NAME, new JScrollPane(m_tuneHeaderComments, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    if(DISPLAY_TOKEN_TAB)
      m_tabbedPane.addTab(TOKENS_TAB_NAME, new JScrollPane(m_tokensList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

    JPanel downerPanel = new JPanel(new BorderLayout());
    downerPanel.add(m_tabbedPane, BorderLayout.CENTER);
    setBottomComponent(downerPanel);
    setPreferredSize(new Dimension(200,100));
  }

  public TuneEditorPane getTuneEditorPane()
  { return m_tunePane; }
  
  public JScoreComponent getScore()
  { return m_score; }


}

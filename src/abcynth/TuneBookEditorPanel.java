package abcynth;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import abc.midi.TunePlayer;
import abc.notation.Tune;
import abc.parser.TuneBook;
import abc.ui.swing.TuneBookTable;
import abc.ui.swing.TuneEditorPane;

/** A simple user interface to display abc files content and play
 * tunes. */
public class TuneBookEditorPanel extends JSplitPane
{
  private static final String FIRST_TAB_NAME = "Header";
  private static final String SECOND_TAB_NAME = "Errors";
  private static final String THIRD_TAB_NAME = "Tokens";
  private TuneBookTable m_tuneBookTable = null;
  private int m_selectedTuneIndex = -1;
  private TuneEditorSplitPane m_tuneEditPane = null;
  //private JTextArea m_tuneHeaderComments = null;
  //private abc.ui.jmusic.ScorePanel m_scorePanel = null;
  private boolean m_tuneHasChangedInEditor = false;
  //private JTabbedPane m_tabbedPane = null;
  private OwnParser m_parser = new OwnParser();
  private TunePlayer player = null;
  private DocumentListener m_documentListener = null;
  private Object m_mutex = new Object();

  public TuneBookEditorPanel()
  {
    super(JSplitPane.VERTICAL_SPLIT);
    m_parser.start();
    m_tuneEditPane = new TuneEditorSplitPane();
    m_documentListener = new DocumentListener(){
      public void changedUpdate(DocumentEvent e)
      {}
      public void insertUpdate(DocumentEvent e)
      {
        //System.out.println("tune insert " + e.getType());
        m_tuneHasChangedInEditor=true;
      }
      public void removeUpdate(DocumentEvent e)
      {
        //System.out.println("tune remove " + e.getType());
        m_tuneHasChangedInEditor=true;
      }};
    m_tuneEditPane.getTuneEditorPane().getDocument().addDocumentListener(m_documentListener);
    player = new TunePlayer();
    player.start();
    m_tuneBookTable = new TuneBookTable();

    setDividerSize(10);
    setOneTouchExpandable(true);
    JScrollPane tablePane = new JScrollPane(m_tuneBookTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    JPanel upperPanel = new JPanel(new BorderLayout());
    upperPanel.add(tablePane, BorderLayout.CENTER);
    setTopComponent(upperPanel);


    //m_tabbedPane = new JTabbedPane();
    //m_tabbedPane.addTab(FIRST_TAB_NAME, panel1);

    //m_tabbedPane.addTab(FIRST_TAB_NAME, new JScrollPane(m_tuneHeaderComments, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

    JPanel panel1 = new JPanel(new BorderLayout());
    //m_tuneEditPane.getTuneEditorPane().setBackground(Color.LIGHT_GRAY);
    panel1.add(m_tuneEditPane,BorderLayout.CENTER);
    //m_tabbedPane.addTab(FIRST_TAB_NAME, panel1);

    //m_tuneHeaderComments = new JTextArea();
    //m_tuneHeaderComments.setBackground(Color.lightGray);
    //m_tuneHeaderComments.setEditable(false);
    //m_tabbedPane.addTab(SECOND_TAB_NAME, new JScrollPane(m_tuneHeaderComments, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    //m_tabbedPane.setEnabledAt(1,false);

    //m_scorePanel = new abc.ui.jmusic.ScorePanel();
    //m_tabbedPane.addTab(THIRD_TAB_NAME, new JScrollPane(m_scorePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

    //m_tabbedPane.setSelectedIndex(0);

    JPanel downerPanel = new JPanel(new BorderLayout());
    //downerPanel.add(m_tabbedPane, BorderLayout.CENTER);
    downerPanel.add(m_tuneEditPane, BorderLayout.CENTER);

    setBottomComponent(downerPanel);

    m_tuneBookTable.getSelectionModel().addListSelectionListener(
        new ListSelectionListener()
        {
          public void valueChanged(ListSelectionEvent e)
          {
            //System.out.println("FIRST index  " + e.getFirstIndex() + " LAST : " +  e.getLastIndex() + " adjusting : " + e.getValueIsAdjusting());
            int selectedIndex = -1;
            int viewColumnNumber = -1;
            try
            {
              //System.out.println(e);
              selectedIndex = m_tuneBookTable.getSelectionModel().getLeadSelectionIndex();
              viewColumnNumber = m_tuneBookTable.convertColumnIndexToView(TuneBookTable.REFERENCE_NUMBER_COLUMN);
              if (m_tuneBookTable.getValueAt(selectedIndex, viewColumnNumber)!=null)
              {
                int selectedTuneIndex = ( (Integer) m_tuneBookTable.getValueAt(selectedIndex, viewColumnNumber)).intValue();
                if (m_selectedTuneIndex != selectedTuneIndex)
                {
                  //System.out.println("Moving from " + m_selectedTuneIndex + " to " +  selectedTuneIndex);
                  m_parser.parse(e);
                }
              }
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
              System.out.println("INDEX :" + selectedIndex + " " + viewColumnNumber );
              System.out.println("=" + m_tuneBookTable.getValueAt(selectedIndex, viewColumnNumber));
            }
          }
        }
        );

      }

  public boolean isEditingTune()
  { return m_tuneHasChangedInEditor; }

  public TuneEditorPane getTuneEditArea()
  { return m_tuneEditPane.getTuneEditorPane(); }

  public TuneBookTable getTuneBookTable()
  { return m_tuneBookTable; }

  public TuneEditorSplitPane getTuneEditSplitPane()
  { return m_tuneEditPane; }

  public void setTuneBook(TuneBook tuneBook)
  { m_tuneBookTable.setTuneBook(tuneBook); }

  public void onTuneSelectedChange(int newSelectedTuneReferenceNumber)
  {
    // m_selectedTuneIndex still has the old selected tune Reference number
    try
    {
      if (m_tuneHasChangedInEditor)
      {
        // update changes in notation in the tune book.
        String newNotation = m_tuneEditPane.getTuneEditorPane().getDocument().getText(0, m_tuneEditPane.getTuneEditorPane().getDocument().getLength());
        Tune newTune = m_tuneBookTable.getTuneBook().putTune(newNotation);
        if (newTune.getReferenceNumber()!=newSelectedTuneReferenceNumber)
          System.out.println("Changing reference number from " + m_selectedTuneIndex + " to " + newTune.getReferenceNumber());
        else
          System.out.println("Saving new tune notation for tune " + m_selectedTuneIndex);
      }
      // put the notation of the new selected tune in the editor area.
      m_tuneEditPane.getTuneEditorPane().getDocument().removeDocumentListener(m_documentListener);
      m_tuneEditPane.getTuneEditorPane().setText(m_tuneBookTable.getTuneBook().getTuneNotation(newSelectedTuneReferenceNumber));
      //m_scorePanel.setTune(m_tuneBookTable.getTuneBook().getTune(newSelectedTuneReferenceNumber));
      //String tuneheader = m_tuneBookTable.getTuneBook().getTuneHeader(newSelectedTuneReferenceNumber);
      /*if (tuneheader!=null)
      {
        m_tuneHeaderComments.setText(tuneheader);
        m_tabbedPane.setEnabledAt(1, true);
      }
      else
        m_tabbedPane.setEnabledAt(1, false);*/
      m_tuneHasChangedInEditor=false;
      m_tuneEditPane.getTuneEditorPane().getDocument().addDocumentListener(m_documentListener);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private class OwnParser extends Thread
  {
    private ListSelectionEvent m_selectionEvent = null;
    //private TuneParser m_parser = null;

    public OwnParser()
    {
      super("ABC-TuneBookEditor");
      //m_parser = new TuneParser();
    }

    //public TuneParser getParser()
    //{ return m_parser; }

    public void parse(ListSelectionEvent event)
    {
      m_selectionEvent = event;
      synchronized(m_mutex)
      { m_mutex.notify(); }
    }

    public void run()
    {
      try
      {
        while (true)
        {
          synchronized (m_mutex)
          {
            m_mutex.wait();
            int selectedIndex = m_tuneBookTable.getSelectionModel().getLeadSelectionIndex();
            int viewColumnNumber = m_tuneBookTable.convertColumnIndexToView(TuneBookTable.REFERENCE_NUMBER_COLUMN);
            int selectedTuneReferenceNumber = ( (Integer) m_tuneBookTable.getValueAt(selectedIndex, viewColumnNumber)).intValue();
            onTuneSelectedChange(selectedTuneReferenceNumber);
            m_selectedTuneIndex = selectedTuneReferenceNumber;
          }
        }
      }
      catch (InterruptedException e)
      { e.printStackTrace(); }
    }
  }
}


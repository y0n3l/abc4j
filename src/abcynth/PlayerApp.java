package abcynth;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import scanner.CharStreamPosition;
import scanner.PositionableInCharStream;
import abc.midi.BasicMidiConverter;
import abc.midi.MidiConverterAbstract;
import abc.midi.PlayerStateChangeEvent;
import abc.midi.TempoChangeEvent;
import abc.midi.TunePlayer;
import abc.midi.TunePlayerListenerInterface;
import abc.notation.NoteAbstract;
import abc.notation.ScoreElementInterface;
import abc.notation.Tune;
import abc.parser.TuneBook;
import abc.ui.swing.JScoreElement;
import abcynth.ui.AddTuneAction;
import abcynth.ui.RemoveTuneAction;
import abcynth.ui.TuneBookActionAbstract;

//import com.birosoft.liquid.LiquidLookAndFeel;

//import jm.music.data.*;
//import jm.gui.show.*;
//import jm.gui.cpn.*;


/** A simple user interface to display abc files content and play
 * tunes. */
public class PlayerApp extends JFrame implements TunePlayerListenerInterface, WindowListener
{
  private File m_file = null;
  private File lastDirectory = null;
  private TuneBook m_tuneBook = null;
  private TuneBookEditorPanel m_tuneBookEditorPanel = null;
  private TunePlayer m_player = null;
  private CircularBuffer m_lastOpenedFiles = null;
  private TuneBookActionAbstract m_addTuneAction, m_saveAction, m_saveAsAction = null;
  private Action m_tune2MidiExport, m_showHideLogAction, m_enableColoringAction = null;
  private JMenu m_fileMenu, m_tunebookMenu, m_lastOpenedFilesMenu, m_windowsMenu, m_viewMenu = null;
  private PlayerToolBar m_playerToolBar = null;
  private JPopupMenu m_tunePopMenu = null;
  private LogFrame m_logFrame = null;
  //private int tunesNb = 0;

  public PlayerApp()
  {
    super("ABCynth");
    /*try {
    	//QuaquaManager.setProperty("Quaqua.tabLayoutPolicy","wrap" );
        //UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
    	LiquidLookAndFeel.setLiquidDecorations(true, "mac");
        UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
   } catch (Exception e) {
       e.printStackTrace();
   }*/
    m_logFrame = new LogFrame();
    //m_logFrame.setVisible(true);
    m_tuneBookEditorPanel = new TuneBookEditorPanel();
    m_lastOpenedFiles = new CircularBuffer(5);
    setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
    File f = new File("config.dat");
    try
    {
      if (f.exists())
      {
        ObjectInputStream oos = new ObjectInputStream(new FileInputStream(f));
        Object o = oos.readObject();
        m_lastOpenedFiles = (CircularBuffer)o;
        if (m_lastOpenedFiles.size()!=0)
          lastDirectory = (File)m_lastOpenedFiles.lastElement();
        oos.close();
      }
      else
        lastDirectory = new File(".");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    JMenuBar menuBar = new JMenuBar();
    JMenuItem menuItem = null;
    m_fileMenu = new JMenu("File");
    menuItem = new JMenuItem(new OpenAbcFileAction("Open...", "Opens a new ABC file", KeyEvent.VK_O, this));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    m_fileMenu.add(menuItem);
    m_lastOpenedFilesMenu = new JMenu("Reopen");
    m_fileMenu.add(m_lastOpenedFilesMenu);
    for (int i=0; i<m_lastOpenedFiles.size();i++)
    {
      String name = ((File)m_lastOpenedFiles.elementAt(i)).getAbsolutePath();
      m_lastOpenedFilesMenu.add(new OpenLastAction(name, "Opens the file " + name));
    }
    m_fileMenu.addSeparator();

    m_saveAction = new SaveAction("Save", "Saves tunebook updates", this);
    menuItem = new JMenuItem(m_saveAction);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    m_fileMenu.add(menuItem);
    m_saveAsAction = new SaveToAbcFileAction("Save as...", "Saves the current tunebook to a file", KeyEvent.VK_S, this);
    m_fileMenu.add(m_saveAsAction);
    m_tune2MidiExport = new Tune2MidiExport("Export to midi...", "Saves the current selected tune to a midi file", PlayerApp.this);
    m_fileMenu.add(m_tune2MidiExport);

    m_tunebookMenu = new JMenu("Tunebook");
    menuItem = new JMenuItem(new NewTuneBookAction("New", "Creates a new emty tunebook"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
    m_tunebookMenu.add(menuItem);
    m_addTuneAction = new AddTuneAction("Add Tune", "Adds a new tune to the opened tunebook" , KeyEvent.VK_A);
    m_tunebookMenu.add(m_addTuneAction);
    m_tunebookMenu.add(new RemoveTuneAction("Remove tune", "Removes the selected tune" , KeyEvent.VK_D, m_tuneBookEditorPanel.getTuneBookTable()));

//    m_windowsMenu = new JMenu("Windows");
//    m_showHideLogAction = new ShowHideLogAction("Show log", "Shows the log window");
//    m_windowsMenu.add(new JCheckBoxMenuItem(m_showHideLogAction));
    m_viewMenu = new JMenu("View");
    m_enableColoringAction = new EnableColoringAction("Enable tune coloring", "Differenciate tune parts with colors");
    m_viewMenu.add(new JCheckBoxMenuItem(m_enableColoringAction));

    m_fileMenu.addSeparator();
    menuItem = new JMenuItem(new ExitAction("Exit", "Exit ABCynth"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    m_fileMenu.add(menuItem);

    JMenu helpMenu = new JMenu("Help");
    helpMenu.add(new HelpAction("About ABCynth...", "About ABCynth", KeyEvent.VK_H, PlayerApp.this));

    menuBar.add(m_fileMenu);
    menuBar.add(m_tunebookMenu);
    //menuBar.add(m_windowsMenu);
    menuBar.add(m_viewMenu);
    menuBar.add(helpMenu);
    setJMenuBar(menuBar);

    m_player = new TunePlayer();
    m_player.addListener(this);
    m_player.start();
    m_playerToolBar = new PlayerToolBar(m_player);
    m_playerToolBar.setFloatable(false);

    m_playerToolBar.getPlayButton().addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        Tune tune2play = m_tuneBookEditorPanel.getTuneEditArea().getTune();
        if (tune2play!=null)
          if (!m_player.isPlaying())
          { m_player.play(tune2play); }
          else
          { m_player.stopPlaying(); }
      }
    });

    m_tunePopMenu = new JPopupMenu("Tune");
    m_tunePopMenu.add("Play");
    m_tunePopMenu.add("Export to midi...");

    getContentPane().add(m_playerToolBar, BorderLayout.NORTH);
    getContentPane().add(m_tuneBookEditorPanel, BorderLayout.CENTER);
    //JTabbedPane tab = new JTabbedPane(JTabbedPane.BOTTOM);
    //tab.addTab("Score", new JTextField());
    //tab.addTab("Log", new JTextField());
    //getContentPane().add(tab, BorderLayout.SOUTH);
    addWindowListener(this);
    pack();
    setSize(600,600);
    m_tuneBookEditorPanel.setDividerLocation((double)0.4);
    //m_tuneBookEditorPanel.setPreferredSize(new Dimension(100,50));
    //m_tuneBookEditorPanel.getTuneEditArea().setPreferredSize(new Dimension(100,250));
    m_tuneBookEditorPanel.getTuneEditSplitPane().setDividerLocation(200);
    
    m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().addMouseListener( new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			
			JScoreElement sel = m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().getScoreElementAt(e.getPoint());
			//if (sel!=null)
			//	System.out.println("Score element at " + e.getX() + " / " + e.getY() + " : " + sel + "@" + sel.getBase());
			//SRenderer sel = getScoreElementAt(e.getPoint());
			if (sel!=null) {
				ScoreElementInterface elmnt = sel.getScoreElement();
				m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().setSelectedItem(sel);
				if (elmnt!=null && elmnt instanceof PositionableInCharStream){
					m_tuneBookEditorPanel.getTuneEditArea().setSelectedItem((PositionableInCharStream)elmnt);
	    			// Dumping element properties
					if (elmnt instanceof NoteAbstract) {
						NoteAbstract note = (NoteAbstract)elmnt;
						System.out.println("properties for " + elmnt + " : slur?="+ note.isPartOfSlur() + " isLastOfGroup?=" + note.isLastOfGroup());
						String test = (note.getSlurDefinition()==null)?"no slur":"start:"+note.getSlurDefinition().getStart()+" end:"+note.getSlurDefinition().getEnd();
						System.out.println(test);
					}
	    		}
			}
		}
	});
    m_tuneBookEditorPanel.getTuneEditSplitPane().getTuneEditorPane().addCaretListener(new CaretListener() {
    	public void caretUpdate(CaretEvent e) {
    		//System.out.println("The caret has moved : " + e);
    		ScoreElementInterface elmnt = null;
    		Tune tune = m_tuneBookEditorPanel.getTuneEditSplitPane().getTuneEditorPane().getTune();
    		if (tune!=null) {
    			elmnt = tune.getScore().getElementAt(e.getDot());
    		}
    		m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().setSelectedItem(elmnt);
    	}
    });
    
    setTuneBook(new TuneBook());
    addMouseListenerToHeaderInTable();
    //m_tuneBookEditorPanel.getTuneBookTable().set
  }

  public void setFile(File file)
  {
    try
    {
      m_file = file;
      TuneBook tuneBook = new TuneBook(file, m_logFrame);
      setTuneBook(tuneBook);
      setTitle("ABCynth - " + file.getAbsolutePath() + " (" + m_tuneBook.size() + " tunes)");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void setTuneBook(TuneBook tunebook)
  {
    m_tuneBook = tunebook;
    m_tuneBookEditorPanel.setTuneBook(m_tuneBook);
    m_addTuneAction.setTuneBook(m_tuneBook);
    m_saveAction.setTuneBook(m_tuneBook);
    m_saveAsAction.setTuneBook(m_tuneBook);
  }

  public void onExit()
  {
    Object[] options = {"Yes", "No"};
    int answer = JOptionPane.showOptionDialog(PlayerApp.this,"Do you really want to exit ?","Exit ?",
                                              JOptionPane.YES_NO_CANCEL_OPTION,
                                              JOptionPane.QUESTION_MESSAGE,
                                              null,
                                              options,
                                              options[1]);
    if (answer==0)
      try
      {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("config.dat"));
        oos.writeObject(m_lastOpenedFiles);
        oos.close();
        System.exit(0);
      }
      catch (Exception exc)
      {
        exc.printStackTrace();
      }
  }

  private void addMouseListenerToHeaderInTable()
  {
    MouseAdapter listMouseListener = new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e)
      {
        //System.out.println("CLICKED " + m_popMenu.isVisible());
        if (!m_tunePopMenu.isVisible())
        {
          JTable table = m_tuneBookEditorPanel.getTuneBookTable();
          /*int columnIndex = table.getRgetColumnModel().getColumnIndexAtX(e.getX());
          TuneColumn column = (TuneColumn)getColumnModel().getColumn(columnIndex);
          if (!column.isAscendingOrdered() && !column.isDescendingOrdered())
            column.sort(true);
          else
          if (column.isAscendingOrdered())
            column.sort(false);
          else
          if (column.isDescendingOrdered())
            column.sort(true);
          for (int i=0; i<getColumnModel().getColumnCount(); i++)
          {
            if(!getColumnModel().getColumn(i).equals(column))
            {
              ((TuneColumn)getColumnModel().getColumn(i)).setIsAscendingOrdered(false);
              ((TuneColumn)getColumnModel().getColumn(i)).setIsDescendingOrdered(false);
            }
          }*/
        }
      }

      public void mousePressed(MouseEvent e)
      {
        //System.out.println("PRESSED" + e.isPopupTrigger() + " " + e);
      }

      public void mouseReleased(MouseEvent e)
      {

        //System.out.println("RELEASED " + e.isPopupTrigger() + " " + e);
        if (e.isPopupTrigger())
          m_tunePopMenu.show(m_tuneBookEditorPanel, (int)e.getX(), (int)e.getY());
      }
    };
    m_tuneBookEditorPanel.getTuneBookTable().addMouseListener(listMouseListener);
  }


  public void windowActivated(WindowEvent e) {}
  public void windowClosed(WindowEvent e)  {}
  public void windowClosing (WindowEvent e)
  { onExit(); }

  public void windowDeactivated(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowOpened(WindowEvent e) {}

  TuneBookEditorPanel getTuneBookEditor()
  { return m_tuneBookEditorPanel; }

  TunePlayer getPlayer()
  { return m_player; }

  public void tempoChanged(TempoChangeEvent e)
  { }

  public void playBegin(PlayerStateChangeEvent e)
  { }

  public void playEnd(PlayerStateChangeEvent e)
  { m_tuneBookEditorPanel.getTuneEditArea().setCaretPosition(0); }

  public void notePlayed(NoteAbstract note)
  {
    if(m_player.getTune().equals(m_tuneBookEditorPanel.getTuneEditArea().getTune()))
    {
      int begin = ((PositionableInCharStream)note).getPosition().getCharactersOffset();
      int end = begin + ((PositionableInCharStream)note).getLength();
       try
       {
         m_tuneBookEditorPanel.getTuneEditArea().setCaretPosition(begin);
         m_tuneBookEditorPanel.getTuneEditArea().moveCaretPosition(end);
         m_tuneBookEditorPanel.getTuneEditArea().getCaret().setSelectionVisible(true);
         m_tuneBookEditorPanel.getTuneEditArea().repaint();
       }
       catch (IllegalArgumentException e)
       {}
       m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().setSelectedItem(note);
    }
  }

  public void partPlayed(int begin, int end)
  {
    /*if(m_player.getTune().equals(m_tuneBookEditorPanel.getTuneEditArea().getTune()))
    {
       try
       {
         m_tuneBookEditorPanel.getTuneEditArea().setCaretPosition(begin);
         m_tuneBookEditorPanel.getTuneEditArea().moveCaretPosition(end+1);
         m_tuneBookEditorPanel.getTuneEditArea().getCaret().setSelectionVisible(true);
         m_tuneBookEditorPanel.getTuneEditArea().repaint();
       }
       catch (IllegalArgumentException e)
       {}
    }*/
  }

  public class Tune2MidiExport extends AbstractAction
  {
    private Component m_parent = null;
    public Tune2MidiExport(String name, String description, Component parent)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
      //putValue(MNEMONIC_KEY, new Integer(shortcut));
      m_parent = parent;
    }

    public void actionPerformed(ActionEvent e)
    {
      try
      {
        Tune t = m_tuneBookEditorPanel.getTuneEditArea().getTune();
        if (t!=null)
        {
          JFileChooser chooser = new JFileChooser(lastDirectory);
          int returnVal = chooser.showSaveDialog(m_parent);
          File file = chooser.getSelectedFile();
          if (file!=null)
          {
            MidiConverterAbstract conv = new BasicMidiConverter();
            Sequence s = conv.toMidiSequence(t);
            int[] types = MidiSystem.getMidiFileTypes(s);
            if (types.length>0)
              MidiSystem.write(s,types[0],file);
          }
        }
      }
      catch (IOException excpt)
      { excpt.printStackTrace(); }
    }
  }

  public class NewTuneBookAction extends AbstractAction
  {
    public NewTuneBookAction(String name, String description)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
      //putValue(MNEMONIC_KEY, new Integer(shortcut));
    }

    public void actionPerformed(ActionEvent e)
    {
      setTuneBook(new TuneBook());
      setTitle("ABCynth");
    }
  }

  public class ExitAction extends AbstractAction
  {
    public ExitAction(String name, String description)//, int shortcut)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
      //putValue(ACCELERATOR_KEY, new Integer(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK).getKeyCode()));
      //putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_A));
    }

    public void actionPerformed(ActionEvent e)
    { onExit(); }
  }

  public class ShowHideLogAction extends AbstractAction
  {
    public ShowHideLogAction(String name, String description)//, int shortcut)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
    }

    public void actionPerformed(ActionEvent e)
    {
      if (m_logFrame.isVisible())
        m_logFrame.setVisible(false);
      else
        m_logFrame.setVisible(true);
    }
  }

  public class EnableColoringAction extends AbstractAction
  {
    public EnableColoringAction(String name, String description)//, int shortcut)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
    }

    public void actionPerformed(ActionEvent e)
    {
      if (m_tuneBookEditorPanel.getTuneEditArea().isColoringEnabled()==true)
        m_tuneBookEditorPanel.getTuneEditArea().setColoringEnable(false);
      else
        m_tuneBookEditorPanel.getTuneEditArea().setColoringEnable(true);
      /*if (m_logFrame.isVisible())
        m_logFrame.setVisible(false);
      else
        m_logFrame.setVisible(true);*/
    }
  }


  public class OpenAbcFileAction extends AbstractAction
  {
    private Component m_parent = null;
    public OpenAbcFileAction(String name, String description, int shortcurt, Component parent)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
      putValue(MNEMONIC_KEY, new Integer(shortcurt));
      m_parent = parent;
    }

    public void actionPerformed(ActionEvent e)
    {
      try
      {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        int returnVal = chooser.showOpenDialog(m_parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          lastDirectory=file;
          File abcFile = new File(file.getPath());
          if (!m_lastOpenedFiles.contains(abcFile))
          {
            m_lastOpenedFiles.insertElementAt(abcFile,0);
            Action a = new OpenLastAction(abcFile.getAbsolutePath(), "Last Opened file");
            JMenuItem m = new JMenuItem(a);
            m_lastOpenedFilesMenu.add(m,0);
            //m_fileMenu.remove(
          }
          setFile(abcFile);
        }
        chooser.setVisible(true);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("config.dat"));
        oos.writeObject(lastDirectory);
        oos.close();
      }
      catch (Exception ex)
      {
        //ex.printStackTrace();
      }
    }
  }

  public class SaveAction extends TuneBookActionAbstract
  {
    private Component m_parent = null;
    public SaveAction(String name, String description, Component parent)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
      //putValue(MNEMONIC_KEY, new Integer(shortcurt));
      m_parent = parent;
    }

    public void actionPerformed(ActionEvent e)
    {
      try
      {
        if (m_tuneBookEditorPanel.isEditingTune())
        {
          String newNotation = m_tuneBookEditorPanel.getTuneEditArea().getDocument().getText(0, m_tuneBookEditorPanel.getTuneEditArea().getDocument().getLength());
          m_tuneBook.putTune(newNotation);
        }
        System.out.println("Saving changes for " + getTuneBook());
        getTuneBook().save();
        setTitle("ABCynth - " + m_tuneBook.getFile().getAbsolutePath() + "\\" + m_tuneBook.getFile().getName() + " (" + m_tuneBook.size() + " tunes)");
      }
      catch (Exception ex)
      { ex.printStackTrace(); }
    }
  }

  public class SaveToAbcFileAction extends TuneBookActionAbstract
  {
    private Component m_parent = null;
    public SaveToAbcFileAction(String name, String description, int shortcurt, Component parent)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
      putValue(MNEMONIC_KEY, new Integer(shortcurt));
      m_parent = parent;
    }

    public void actionPerformed(ActionEvent e)
    {
      try
      {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        int returnVal = chooser.showSaveDialog(m_parent);
        File abcFile = chooser.getSelectedFile();
        if (abcFile!=null)
        {
          lastDirectory=abcFile;
          chooser.setVisible(true);
          getTuneBook().saveTo(abcFile);
          if (!m_lastOpenedFiles.contains(abcFile))
          {
            m_lastOpenedFiles.insertElementAt(abcFile,0);
            Action a = new OpenLastAction(abcFile.getAbsolutePath(), "Opens the file " + abcFile.getAbsolutePath());
            JMenuItem m = new JMenuItem(a);
            m_lastOpenedFilesMenu.add(m,0);
            //m_fileMenu.remove(
          }
        }
        setTitle("ABCynth - " + abcFile.getAbsolutePath() + "\\" + abcFile.getName() + " (" + m_tuneBook.size() + " tunes)");
      }
      catch (Exception ex)
      {
        //ex.printStackTrace();
      }
    }
  }

  public class OpenLastAction extends AbstractAction
  {
    public OpenLastAction(String name, String description)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
    }

    public void actionPerformed(ActionEvent e)
    {
      //System.out.println(e.getSource());
      File selectedFile = new File(((JMenuItem)e.getSource()).getText());
      setFile(selectedFile);
      m_lastOpenedFilesMenu.remove((JMenuItem)e.getSource());
      m_lastOpenedFilesMenu.add((JMenuItem)e.getSource(),0);
    }
  }
  
  class MyScoreSelectionListener {
	  public MyScoreSelectionListener() {
		  
	  }
  }

  public static void main (String[] arg)
  {
/*    TuneBook book = null;
    try
    {
      book = new TuneBook(new File("D:/Perso/abc/test.abc"));
      System.out.println(book.size());
      Tune t = book.getTune(1);
      System.out.println(t);
      System.out.println(t.getTitles()[0]);
      TunePlayer player = new TunePlayer();
      player.addListener(new TunePlayerAdapter(){
        public void notePlayed(NoteAbstract note)
        {
          System.out.println("note played : " + note + "position : " +
          ((PositionableInCharStream)note).getBeginPosition() + " " +
          ((PositionableInCharStream)note).getEndPosition());
        }

        public void partPlayed(int begin, int end)
        {
          System.out.println("part played : " + begin + " " + end);
        }
      });
      player.start();
      player.play(t);
    }
    catch (Exception e)
    {e.printStackTrace(); }*/

/*    Phrase phrase = new Phrase();
    phrase.add(new jm.music.data.Note(jm.constants.Pitches.B4, jm.constants.Durations.HALF_NOTE));
    phrase.add(new jm.music.data.Note(jm.constants.Pitches.B5, jm.constants.Durations.QUARTER_NOTE));
    phrase.add(new jm.music.data.Note(jm.constants.Pitches.B6, jm.constants.Durations.THIRTYSECOND_NOTE));
    jm.music.data.Part p1 = new jm.music.data.Part();
    p1.add(phrase);
    Score score = new Score();
    score.setTimeSignature(4,4);
    score.add(p1);
    Frame fr = new Frame("essai");
    Tune tune = book.getTune(book.getReferenceNumbers()[0]);
    //Part part = abc.ui.jmusic.ScoreToJMusicConverter.convert(tune.getScore()).getPart(0);
    Stave panel = new TrebleStave(abc.ui.jmusic.ScoreToJMusicConverter.convert(tune.getScore()).getPart(0).getPhrase(0));
    fr.add(panel);
    fr.setVisible(true);
    //jm.util.View.notate(phrase);
    //ShowScore showScore = new ShowScore(score);
    //showScore.setVisible(true);
*/

//=================================================================REAL MAIN
    PlayerApp ui = new PlayerApp();
    if (arg.length!=0 && (new File(arg[0])).exists())
      ui.setFile(new File(arg[0]));
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    ui.setLocation((int)(d.getWidth()-ui.getWidth())/2, (int)(d.getHeight()-ui.getHeight())/2);
    ui.setVisible(true);
//=================================================================REAL MAIN
/*    AbcFileParser p = new AbcFileParser();
    try
    {
      p.addListener(new AbcFileParserListenerInterface()
        {
          public void tuneBegin()
          {
            //System.out.println("parser : parsing new tune");
            //System.out.print(".");
          }

          public void invalidToken(InvalidTokenEvent event)
          {
            //System.out.println("INVALID TOKEN : " + event);
          }
          public void validToken(TokenEvent event)
          {
            //System.out.println("VALID TOKEN : " + event);
          }
          public void invalidCharacter(InvalidCharacterEvent event)
          {
            //System.out.println("invalid char " + event);
          }
          public void lineProcessed(String line)
          {
            //System.out.println("invalid char " + event);
          }

          public void tuneEnd(Tune tune)
          {
            tunesNb++;

          }
          public void fileBegin()
          {
            //System.out.println("file begin");

          }
          public void fileEnd()
          {
            //System.out.println("file ended");
            }
        });

      File path = new File("D:/Perso/abc/");
      File[] list = path.listFiles();
      for (int i=0;i<list.length; i++)
      {
        if (!list[i].isDirectory())
        {
          System.out.print("Parsing "+ list[i].toString());
          long start = System.currentTimeMillis();
          p.parseFileHeaders(list[i]);
          long end = System.currentTimeMillis();
          long totalTime = end - start;
          System.out.println(" : " + tunesNb + " tunes parsed in " + totalTime + "ms (" + list[i].toString() + ")");
          tunesNb=0;
        }
      }
      File file = new File("D:/Perso/abc/ceili.abc");
      TuneBook book = new TuneBook();
      book.addListener(new TuneBookListenerInterface()
      {
        public void tuneChanged(TuneChangeEvent e)
        {
          Tune tune = e.getTune();
          System.out.println(tune.getReferenceNumber()+"\t- "+tune.getTitles()[0] + " (" + tune.getKey().toLitteralNotation() +")");
        }
      });
      book.setFile(file);
      /*int[] nb = book.getReferenceNumbers();
      for (int i=0; i<nb.length; i++)
      {
        Tune tune = book.getTune(nb[i]);
        System.out.println(nb[i]+"\t- "+tune.getTitles()[0] + " (" + tune.getKey().toLitteralNotation() +")");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }*/

  }

}

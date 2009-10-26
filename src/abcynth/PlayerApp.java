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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
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

import scanner.PositionableInCharStream;
import abc.midi.BasicMidiConverter;
import abc.midi.MidiConverterAbstract;
import abc.midi.PlayerStateChangeEvent;
import abc.midi.TempoChangeEvent;
import abc.midi.TunePlayer;
import abc.midi.TunePlayerListenerInterface;
import abc.notation.MusicElement;
import abc.notation.NoteAbstract;
import abc.notation.Tune;
import abc.parser.TuneBook;
import abc.util.PropertyManager;
import abc.ui.swing.Engraver;
import abc.ui.swing.JScoreElement;
import abc.xml.Abc2xml;
import abcynth.ui.AddTuneAction;
import abcynth.ui.RemoveTuneAction;
import abcynth.ui.TuneBookActionAbstract;


/** A simple user interface to display abc files content and play
 * tunes. */
public class PlayerApp extends JFrame implements TunePlayerListenerInterface, WindowListener
{
  private static final long serialVersionUID = -8475230184183870538L;
  private File m_file = null;
  private File lastDirectory = null;
  private TuneBook m_tuneBook = null;
  private TuneBookEditorPanel m_tuneBookEditorPanel = null;
  private TunePlayer m_player = null;
  private CircularBuffer m_lastOpenedFiles = null;
  private TuneBookActionAbstract m_addTuneAction, m_saveAction, m_saveAsAction = null;
  private Action m_tune2MidiExport, m_tune2pngExport, m_abc2xmlExport,m_showHideLogAction, m_enableColoringAction = null;
  private JMenu m_fileMenu, m_tunebookMenu, m_lastOpenedFilesMenu, m_windowsMenu, m_viewMenu = null;
  private PlayerToolBar m_playerToolBar = null;
  private JPopupMenu m_tunePopMenu = null;
  private LogFrame m_logFrame = null;
  //private int tunesNb = 0;

  // properties and preference related attributes
  private PrefsDialog m_prefsDialog = null;
  private JPropertyChangeHandler m_propsHandler = null;
  private float m_notationFontSize = 0;

  private int DEFAULT_WIDTH, DEFAULT_HEIGHT = 600;

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

    setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);

    m_logFrame = new LogFrame();
    //m_logFrame.setVisible(true);
    m_tuneBookEditorPanel = new TuneBookEditorPanel();

    // load and apply preferences AFTER TuneBookEditorPanel has been
    //  created because many of the preferences are applied against
    //  the JScoreComponent instance contained within the editing panels
    try {
	  loadPreferences();
      applyPreferences();
    } catch (IOException ex) {
      System.out.println("Could not load and apply preferences.");
    }

    m_lastOpenedFiles = new CircularBuffer(5);

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
    m_tune2MidiExport = new Tune2MidiExport("Export to midi...", "Saves the selected tune to a midi file", PlayerApp.this);
    m_fileMenu.add(m_tune2MidiExport);
    m_tune2pngExport = new Tune2PNGExport("Export to png...", "Saves the selected tune to a png image file", PlayerApp.this);
    m_fileMenu.add(m_tune2pngExport);
    m_abc2xmlExport = new Tune2XMLExport("Export to MusicXML...", "Saves the selected tune to a musicXML file", PlayerApp.this);
    m_fileMenu.add(m_abc2xmlExport);

    m_fileMenu.addSeparator();
    menuItem = new JMenuItem(new PrefsAction("Preferences", "ABCynth Preferences"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
    m_fileMenu.add(menuItem);

    m_fileMenu.addSeparator();
    menuItem = new JMenuItem(new ExitAction("Exit", "Exit ABCynth"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    m_fileMenu.add(menuItem);



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

    JMenu helpMenu = new JMenu("Help");
    helpMenu.add(new HelpAction("About ABCynth...", "About ABCynth", KeyEvent.VK_H, PlayerApp.this));

    menuBar.add(m_fileMenu);
    menuBar.add(m_tunebookMenu);
    //menuBar.add(m_windowsMenu);
    menuBar.add(m_viewMenu);
    menuBar.add(helpMenu);
    setJMenuBar(menuBar);

    m_player = new TunePlayer();
    try {
    	//Retrieves all instruments
    	Instrument[] allInstruments = MidiSystem.getSynthesizer().getAvailableInstruments();
    	Instrument accordion = null;
    	//Find the accordion among them
    	for (int i=0; i<allInstruments.length && accordion ==null; i++)
    		if (allInstruments[i].getName().equalsIgnoreCase("accordion"))
    			accordion= allInstruments[i];
    	m_player.setInstrument(accordion);
    }
    catch(MidiUnavailableException e) {
    	e.printStackTrace();
    }

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
    // apply size/location preferences again as they don't "take"
    //   until component has been packed
    applyPreferenceSizeAndLocation();

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
				MusicElement elmnt = sel.getMusicElement();
				m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().setSelectedItem(sel);
				if (elmnt!=null && elmnt instanceof PositionableInCharStream){
					m_tuneBookEditorPanel.getTuneEditArea().setSelectedItem((PositionableInCharStream)elmnt);
	    			// Dumping element properties
					if (elmnt instanceof NoteAbstract) {
						NoteAbstract note = (NoteAbstract)elmnt;
						System.out.println("properties for " + elmnt + " : slur?="+ note.isPartOfSlur() + " isLastOfGroup?=");
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
    		MusicElement elmnt = null;
    		Tune tune = m_tuneBookEditorPanel.getTuneEditSplitPane().getTuneEditorPane().getTune();
    		if (tune!=null) {
    			elmnt = tune.getMusic().getElementAt(e.getDot());
    		}
    		m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().setSelectedItem(elmnt);
    	}
    });

    setTuneBook(new TuneBook());
    addMouseListenerToHeaderInTable();
    //m_tuneBookEditorPanel.getTuneBookTable().set

	// add application property change listener
	m_propsHandler = new JPropertyChangeHandler();
	m_propsHandler.listen();

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

		savePreferences();

        System.exit(0);
      }
      catch (Exception exc)
      {
        exc.printStackTrace();
      }
  }

  public void savePreferences() {

	// store properties and preferences
	//   GUI size, GUI location, preferences
    try {

  	  PropertyManager pm = PropertyManager.getInstance();
	  String prop = null;
	  prop = pm.getProperty(StringConstants.PROPS_KEY_UI_SAVEDIMENSIONS);
	  if (prop != null && prop.equals(StringConstants.YES)) {
	    pm.setProperty(StringConstants.PROPS_KEY_UI_WIDTH, "" + getWidth());
	    pm.setProperty(StringConstants.PROPS_KEY_UI_HEIGHT, "" + getHeight());
	  }
	  prop = pm.getProperty(StringConstants.PROPS_KEY_UI_SAVELOCATION);
	  if (prop != null && prop.equals(StringConstants.YES)) {
	    pm.setProperty(StringConstants.PROPS_KEY_UI_X, "" + getX());
	    pm.setProperty(StringConstants.PROPS_KEY_UI_Y, "" + getY());
	  }

	  pm.storeProperties();

    } catch (IOException ex) {
	  ex.printStackTrace();
	}
  }


  protected void loadPreferences() throws IOException {
    PropertyManager pm = PropertyManager.getInstance();
  }


  protected void applyPreferences() {
	applyPreferenceSizeAndLocation();

	applyPreferenceLanguageChoice(null);

	// score preferences
    applyPreferenceScoreSize(null);
	applyPreferenceTitlesDisplay(null);
	applyPreferenceStemmingPolicy(null);
	applyPreferenceEngravingStyle(null);

	// instrument specific preferences
//	applyPreferenceInstrumentProfile();

  }

  protected void applyPreferenceLanguageChoice(String val) {
    // FIXME: implement something!!

  }

  protected void applyPreferenceScoreSize(String val) {
	if (val == null) {
		try {
			val = PropertyManager.getInstance().getProperty(StringConstants.PROPS_KEY_SCORESIZE);
			if (val == null) {
				throw new IOException("Property value NULL for KEY ("+StringConstants.PROPS_KEY_SCORESIZE+")");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}

    // FIXME: only works if score is already loaded
	try {
	  m_notationFontSize = m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().getScoreMetrics().getNotationFontSize();
    } catch (NullPointerException ex) {
		// FIXME: log something?
    }

	// strip " %" from end
	val = val.substring(0, (val.length()-2));
	float newSize = m_notationFontSize;
	try {
	  newSize=Float.valueOf(val).floatValue();
	  newSize = newSize/100;
	  newSize = m_notationFontSize * newSize;
	} catch (NumberFormatException nfe) {
	  nfe.printStackTrace();
	}

    // FIXME: only works if score is already loaded
	try {
	  m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().setSize(newSize);
    } catch (NullPointerException ex) {
		// FIXME: log something?
    }

  }

  protected void applyPreferenceTitlesDisplay(String val) {
	if (val == null) {
		try {
			val = PropertyManager.getInstance().getProperty(StringConstants.PROPS_KEY_DISPLAYTITLES);
			if (val == null) {
				throw new IOException("Property value NULL for KEY ("+StringConstants.PROPS_KEY_DISPLAYTITLES+")");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}

	boolean showTitles = (val.equals(StringConstants.YES)) ? true : false;
    // FIXME: only works if score is already loaded
	try {
	  m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().showTitles(showTitles);
	  m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().refresh();
    } catch (NullPointerException ex) {
		// FIXME: log something?
    }

  }

  protected void applyPreferenceStemmingPolicy(String val) {
	if (val == null) {
		try {
			val = PropertyManager.getInstance().getProperty(StringConstants.PROPS_KEY_STEMMINGPOLICY);
			if (val == null) {
				throw new IOException("Property value NULL for KEY ("+StringConstants.PROPS_KEY_STEMMINGPOLICY+")");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}
	byte policy = 0;  // 0=auto 1=up 2=down
	if (val.equals(StringConstants.UP))
	  policy = 1;
	else if (val.equals(StringConstants.DOWN))
	  policy = 2;
	else
	  policy = 0;

    // FIXME: only works if score is already loaded
	try {
   	  m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().setStemmingPolicy(policy);
	  m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().refresh();
    } catch (NullPointerException ex) {
		// FIXME: log something?
    }

  }

  protected void applyPreferenceEngravingStyle(String val) {
	if (val == null) {
		try {
			val = PropertyManager.getInstance().getProperty(StringConstants.PROPS_KEY_ENGRAVINGSTYLE);
			if (val == null) {
				throw new IOException("Property value NULL for KEY ("+StringConstants.PROPS_KEY_ENGRAVINGSTYLE+")");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}

    // FIXME: only works if score is already loaded
	try {
	  if (val.equals(StringConstants.JUSTIFIED)) {
	    // FIXME: just what does justified do to score rendering??
	    m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().setJustified(true);
	  } else if (val.equals(StringConstants.FIXED)) {
	    m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().getEngraver().setMode(Engraver.NONE);
	  } else {
	    m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().getEngraver().setMode(Engraver.DEFAULT);
  	  }
    } catch (NullPointerException ex) {
		// FIXME: log something?
    }

  }

  protected void applyPreferenceSizeAndLocation() {
    try {
      PropertyManager pm = PropertyManager.getInstance();
	  String prop = null;
	  String valStr = null;
	  int val1 = -1;
	  int val2 = -1;
	  prop = pm.getProperty(StringConstants.PROPS_KEY_UI_SAVEDIMENSIONS);
	  if (prop != null && prop.equals(StringConstants.YES)) {
	    try {
		  valStr = pm.getProperty(StringConstants.PROPS_KEY_UI_WIDTH);
		  if (valStr != null) {
			  val1 = Integer.parseInt(valStr);

		      valStr = pm.getProperty(StringConstants.PROPS_KEY_UI_HEIGHT);
		      if (valStr != null) {
			    val2 = Integer.parseInt(valStr);

			    setPreferredSize(new Dimension(val1, val2));
			}
		  }
		} catch (NumberFormatException nfe) {
		  nfe.printStackTrace();
		  setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		}
	  }

	  prop = pm.getProperty(StringConstants.PROPS_KEY_UI_SAVELOCATION);
	  if (prop != null && prop.equals(StringConstants.YES)) {
	    try {
		  valStr = pm.getProperty(StringConstants.PROPS_KEY_UI_X);
		  if (valStr != null) {
			val1 = Integer.parseInt(valStr);

		    valStr = pm.getProperty(StringConstants.PROPS_KEY_UI_Y);
		    if (valStr != null) {
			  val2 = Integer.parseInt(valStr);

			  setLocation(val1, val2);
			}
	      } else {
			// default action: center on screen
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		    setLocation((int)(d.getWidth()-getWidth())/2, (int)(d.getHeight()-getHeight())/2);
		  }
		} catch (NumberFormatException nfe) {
		  nfe.printStackTrace();
		}
	  } else {
		// default action: center on screen
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int)(d.getWidth()-getWidth())/2, (int)(d.getHeight()-getHeight())/2);
	  }

    } catch (IOException ex) {
	  ex.printStackTrace();
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
    private static final long serialVersionUID = -7024807010515214434L;
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

  public class Tune2PNGExport extends AbstractAction
  {
    private static final long serialVersionUID = 7275802044821451364L;
	private Component m_parent = null;
    public Tune2PNGExport(String name, String description, Component parent)
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
          if (file!=null) {
        	  m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().writeScoreTo(file);
          }
        }
      }
      catch (IOException excpt)
      { excpt.printStackTrace(); }
    }
  }

  public class Tune2XMLExport extends AbstractAction
  {
    private static final long serialVersionUID = 9024213350494736598L;
	private Component m_parent = null;
    public Tune2XMLExport(String name, String description, Component parent)
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
          if (file!=null) {
        	  Abc2xml abc2xml = new Abc2xml();
          	  abc2xml.writeAsMusicXML(t, file);
        	  //m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().writeScoreTo(file);
          }
        }
      }
      catch (IOException excpt)
      { excpt.printStackTrace(); }
    }
  }

  public class NewTuneBookAction extends AbstractAction
  {
    private static final long serialVersionUID = 2153921560101531857L;

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

  public class PrefsAction extends AbstractAction
  {
	// FIXME: needs unique UID
    private static final long serialVersionUID = -7329820982747359966L;

	public PrefsAction(String name, String description)//, int shortcut)
    {
      putValue(NAME, name);
      putValue(SHORT_DESCRIPTION, description);
      //putValue(ACCELERATOR_KEY, new Integer(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK).getKeyCode()));
      //putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_A));
    }

    public void actionPerformed(ActionEvent e)
    {
	  if (m_prefsDialog == null) {
		  // create a modal dialog
		  m_prefsDialog = new PrefsDialog(PlayerApp.this, (String)getValue(SHORT_DESCRIPTION), true);
	  }
	  m_prefsDialog.setVisible(true);

    }
  }

  public class ExitAction extends AbstractAction
  {
    private static final long serialVersionUID = -7329820982747359966L;

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
    private static final long serialVersionUID = -144917309332425131L;

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
    private static final long serialVersionUID = -3781525526370269971L;

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
    private static final long serialVersionUID = -1120909116485245557L;
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
    private static final long serialVersionUID = -4911261075834413942L;
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
    private static final long serialVersionUID = 28351013505659963L;
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
    private static final long serialVersionUID = 2657812375962296142L;

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


  class JPropertyChangeHandler implements PropertyChangeListener {
  	protected ArrayList keys = null;

  	public JPropertyChangeHandler () {
	  keys = new ArrayList();
	  keys.add(StringConstants.PROPS_KEY_LANG);
	  keys.add(StringConstants.PROPS_KEY_SCORESIZE);
  	  keys.add(StringConstants.PROPS_KEY_DISPLAYTITLES);
  	  keys.add(StringConstants.PROPS_KEY_STEMMINGPOLICY);
  	  keys.add(StringConstants.PROPS_KEY_ENGRAVINGSTYLE);
	}

	public void listen() {
	  try {
	    PropertyManager pm = PropertyManager.getInstance();
	    int len = keys.size();
	    for (int i=0; i<len; i++) {
		  pm.addListener((String)keys.get(i), this);
	    }
	  } catch (IOException th) {
	    // TODO: logging or UI message
	  }
	}

    public void propertyChange(PropertyChangeEvent evt) {

	  String key = null;
	  key = evt.getPropertyName();

	  if (!keys.contains(key)) {
		return;
	  }

	  String val = (String)evt.getNewValue();

	  if (key.equals(StringConstants.PROPS_KEY_LANG)) {
		applyPreferenceLanguageChoice(val);

	  } else if (key.equals(StringConstants.PROPS_KEY_SCORESIZE)) {
        applyPreferenceScoreSize(val);

	  } else if (key.equals(StringConstants.PROPS_KEY_DISPLAYTITLES)) {
		applyPreferenceTitlesDisplay(val);

	  } else if (key.equals(StringConstants.PROPS_KEY_STEMMINGPOLICY)) {
		applyPreferenceStemmingPolicy(val);

	  } else if (key.equals(StringConstants.PROPS_KEY_ENGRAVINGSTYLE)) {
		applyPreferenceEngravingStyle(val);
	  }

 	  // FIXME: currently refreshes JScoreComponent for each property
 	  //        needs a different refresh strategy
 	  m_tuneBookEditorPanel.getTuneEditSplitPane().getScore().refresh();

    }
  }

  public static void main (String[] arg)
  {

//=================================================================REAL MAIN
    PlayerApp ui = new PlayerApp();
    if (arg.length!=0 && (new File(arg[0])).exists())
      ui.setFile(new File(arg[0]));
    ui.setVisible(true);
//=================================================================REAL MAIN

  }

}

package abcynth;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JApplet;
import javax.swing.JButton;

import abc.midi.TunePlayer;
import abc.parser.TuneBook;

/** A simple user interface to display abc files content and play
 * tunes. */
public class PlayerApplet extends JApplet
{
  private static final String DEMO_FILE_NAME = "LGtunes.abc";
  private PlayerApp m_app = null;
  //private TunePlayer m_player = null;

  public PlayerApplet()
  {
    m_app = new PlayerApp();
    JButton launchDemoButton = new JButton("Click here to run demo!");
    launchDemoButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e)
      {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        m_app.setLocation((screenSize.width  - m_app.getWidth())  / 2,
                          (screenSize.height - m_app.getHeight()) / 2);
        m_app.setSize(700,550);
        m_app.setVisible(true);
      }
    }
    );
    try
    {
      InputStream is = getClass().getResourceAsStream(DEMO_FILE_NAME);
      System.out.println("get ressource " + is);
      if (is!=null)
      {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        m_app.getTuneBookEditor().setTuneBook(new TuneBook(reader));
      }
    }
    catch (Exception e)
    { }
    getContentPane().add(launchDemoButton);
  }

  public void destroy()
  {m_app.getPlayer().stop(); }
}

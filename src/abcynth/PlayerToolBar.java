package abcynth;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.SystemColor;
import abc.notation.*;
import abc.parser.*;
import abc.midi.*;
import scanner.*;
import java.util.*;
import java.io.*;

/** A simple user interface to display abc files content and play
 * tunes. */
public class PlayerToolBar extends JToolBar implements TunePlayerListenerInterface
{
  private JLabel m_tempoField = new JLabel("180");
  private JSlider m_tempoSlider = new JSlider(0, 300, 180);
  private TunePlayer m_player = null;
  private JButton m_playButton = null;
  private Tune m_tune = null;

  public PlayerToolBar(TunePlayer player)
  {
    super("Tune Player");
    m_player = player;
    setLayout(new BorderLayout());
    m_playButton = new JButton("PLAY | STOP");
    add(m_playButton, BorderLayout.EAST);
    add(m_tempoSlider, BorderLayout.CENTER);
    add(m_tempoField, BorderLayout.WEST);

    ChangeListener tempoListener = new ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        m_player.setTempo(m_tempoSlider.getValue());
        m_tempoField.setText((new Integer(m_tempoSlider.getValue())).toString());
      }
    };
    m_tempoSlider.addChangeListener(tempoListener);

    player.addListener(this);
  }

  public void tempoChanged(TempoChangeEvent e)
  { m_tempoSlider.setValue(e.getNewTempoValue()); }

  public void playBegin(PlayerStateChangeEvent e)
  { }

  public void notePlayed(NoteAbstract e)
  { }

  public void playEnd(PlayerStateChangeEvent e)
  { }

  public void partPlayed(int begin, int end)
  {}

  public void setTune(Tune tune)
  { m_tune = tune; }

  public JButton getPlayButton()
  { return m_playButton; }

/*  public JSlider getSlider()
  { return m_tempoSlider; }

  public JLabel getTempoField()
  { return m_tempoField; }*/

}

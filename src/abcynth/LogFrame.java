package abcynth;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.sound.midi.*;
import java.awt.SystemColor;
import abc.notation.*;
import abc.parser.*;
import abc.midi.*;
import scanner.*;
import java.util.*;
import java.io.*;
import abc.ui.swing.*;


/** */
public class LogFrame extends JFrame implements AbcFileParserListenerInterface
{
  private JTextArea m_errorsArea = null;

  public LogFrame()
  {
    super("Logs...");
    m_errorsArea = new JTextArea();
    JScrollPane logPane = new JScrollPane(m_errorsArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    getContentPane().add(logPane);
  }

  public JTextArea getErrorsArea()
  { return m_errorsArea; }

  public void tuneBegin()
  {
  }

  public void invalidToken(InvalidTokenEvent event)
  {
    m_errorsArea.append("Invalid token " + event.getToken().getValue() + " at " + event.getPosition().toString() + "\n");
  }

  public void validToken(TokenEvent event)
  {}

  public void invalidCharacter(InvalidCharacterEvent event)
  {
    m_errorsArea.append("Invalid character " + event.getCharacter() + " at " + event.getPosition().toString()+ "\n");
  }

  public void tuneEnd(Tune tune)
  { }

  public void fileBegin()
  {
    m_errorsArea.setText("");
  }

  public void lineProcessed(String line)
  {}

  public void fileEnd()
  {
  }
}
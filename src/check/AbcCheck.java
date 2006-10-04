package check;

import java.awt.*;
import java.awt.event.*;
import java.awt.SystemColor;
import java.applet.Applet;
import abc.notation.*;
import abc.parser.*;
import abc.midi.*;
import abc.ui.awt.TuneEditorArea;
import scanner.*;
import java.util.*;
import java.io.*;

/** */
public class AbcCheck extends Applet implements TuneParserListenerInterface
{
  private TuneEditorArea m_textArea = null;
  private Button m_checkButton = null;
  private java.awt.List m_errorList = null;
  //private TuneParser m_parser = null;
  private Vector m_errors = null;

  public AbcCheck()
  {
    setLayout(new BorderLayout());
    m_textArea = new TuneEditorArea();
    m_textArea.getParser().addListener(this);
    m_textArea.setFont(new Font("Courier", Font.PLAIN, 15));
    m_errorList = new java.awt.List(10);
    m_errorList.setForeground(Color.red);
    m_errorList.setFont(new Font("Courier", Font.BOLD, 15));
    m_errorList.add("Errors are displayed here.");

    m_errorList.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent e)
      {
        if (e.getStateChange()==ItemEvent.SELECTED)
        {
          if (e.getItem() instanceof Integer)
          {
            EventObject evt = (EventObject)m_errors.elementAt(((Integer)e.getItem()).intValue());
            int beginOffset = 0;
            int length  = 0;
            if (evt instanceof InvalidCharacterEvent)
            {
              beginOffset = ((InvalidCharacterEvent)evt).getPosition().getCharactersOffset();
              length = 1;
            }
            else
            if (evt instanceof InvalidTokenEvent)
            {
              beginOffset = ((InvalidTokenEvent)evt).getPosition().getCharactersOffset();
              if (((InvalidTokenEvent)evt).getToken()==null)
                length = 1;
              else
                length = ((InvalidTokenEvent)evt).getToken().getValue().length();
            }
            m_textArea.select(beginOffset, beginOffset+ length);
          }
        }
      }
    }
    );
    m_errors = new Vector();
    add(m_textArea, BorderLayout.CENTER);
    add(m_errorList, BorderLayout.SOUTH);
  }

  public void tuneBegin()
  {
    System.out.println("Beginning to parse tune");
    m_errors.removeAllElements();
  }

  public void invalidToken(InvalidTokenEvent evt)
  { m_errors.addElement(evt); }

  public void validToken(TokenEvent event)
  { }

  public void invalidCharacter(InvalidCharacterEvent event)
  { m_errors.addElement(event); }

  public void tuneEnd(Tune tune)
  {
    m_errorList.removeAll();
    if (m_errors.isEmpty())
      m_errorList.add("No error detected !");
    else
      for (int i=0; i<m_errors.size(); i++)
      {
        EventObject ev = (EventObject)m_errors.elementAt(i);
        String message = null;
        if (ev instanceof InvalidCharacterEvent)
        {
          InvalidCharacterEvent evt = (InvalidCharacterEvent)ev;
          message = "Invalid Character at line " + evt.getPosition().getLine() + ", column "+
                        evt.getPosition().getColumn();
            }
            else
            if (ev instanceof InvalidTokenEvent)
            {
              InvalidTokenEvent evt = (InvalidTokenEvent)ev;
              message = ev.toString();
              if (evt.getToken()!=null)
                message = "Expecting " + evt.getExpectedTokenType().toString()
                  +  " at line " + evt.getToken().getPosition().getLine() + ", column " + evt.getToken().getPosition().getColumn()
                  + " instead of "
                  + evt.getToken().getType().toString();
              else
                message = "Expecting " + evt.getExpectedTokenType().toString();
            }
            m_errorList.add(message);
          }
        m_errorList.repaint();
        AbcCheck.this.repaint();
  }
}

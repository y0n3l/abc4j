package abcynth;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class HelpAction extends AbstractAction
{
  private Component m_parent = null;

  public HelpAction(String name, String description, int shortcurt, Component parent)
  {
    putValue(NAME, name);
    putValue(SHORT_DESCRIPTION, description);
    putValue(MNEMONIC_KEY, new Integer(shortcurt));
    m_parent = parent;
  }

  public void actionPerformed(ActionEvent e)
  { JOptionPane.showMessageDialog(m_parent, "ABCynth - The abc4j demonstrator\n2003-2007 by Lionel GUEGANTON\nFor feedback, "
                                  + "help or whatever...\nlionel.gueganton@libertysurf.fr\n"
                                  + "http://abc4j.googlecode.com\n" 
                                  + "http://groups.google.com/group/abc4j" , "About ABCynth", JOptionPane.INFORMATION_MESSAGE); }
}

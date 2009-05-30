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

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class HelpAction extends AbstractAction
{
  private static final long serialVersionUID = 3645150876472254238L;
  private Component m_parent = null;

  public HelpAction(String name, String description, int shortcurt, Component parent)
  {
    putValue(NAME, name);
    putValue(SHORT_DESCRIPTION, description);
    putValue(MNEMONIC_KEY, new Integer(shortcurt));
    m_parent = parent;
  }

  public void actionPerformed(ActionEvent e)
  { JOptionPane.showMessageDialog(m_parent, "ABCynth - The abc4j demonstrator\n2003-2008 by Lionel GUEGANTON\nFor feedback, "
                                  + "help or whatever...\nlionel.gueganton@libertysurf.fr\n"
                                  + "http://abc4j.googlecode.com\n" 
                                  + "http://groups.google.com/group/abc4j" , "About ABCynth", JOptionPane.INFORMATION_MESSAGE); }
}

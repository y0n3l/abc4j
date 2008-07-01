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

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scanner.InvalidCharacterEvent;
import scanner.TokenEvent;
import abc.notation.Tune;
import abc.parser.AbcFileParserListenerInterface;
import abc.parser.InvalidTokenEvent;


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

  public void tuneBegin() {
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
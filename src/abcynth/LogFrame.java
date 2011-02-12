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

import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import abc.notation.Tune;
import abc.parser.AbcFileParserListenerInterface;
import abc.parser.AbcNode;
import abc.parser.AbcParseError;
import abc.parser.CharStreamPosition;


/** */
public class LogFrame extends JFrame implements AbcFileParserListenerInterface
{
  private static final long serialVersionUID = -1909417662191372084L;
  private JTextArea m_errorsArea = null;
  private boolean isBusy = false;

  public LogFrame()
  {
    super("Logs...");
    m_errorsArea = new JTextArea();
    JScrollPane logPane = new JScrollPane(m_errorsArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    getContentPane().add(logPane);
  }

  public JTextArea getErrorsArea()
  { return m_errorsArea; }

  public boolean isBusy() {
	  return isBusy;
  }
  
  public void tuneBegin() {
	  isBusy = true;
  }
  
  public void noTune() {
	  isBusy = false;
  }

  public void tuneEnd(Tune tune, AbcNode abcRoot)
  {
	  isBusy = false;
	  if (abcRoot != null) {
		  Iterator it = abcRoot.getErrors().iterator();
		  while (it.hasNext()) {
			  AbcParseError ape = (AbcParseError) it.next();
			  CharStreamPosition csp = ape.getCharStreamPosition();
			  m_errorsArea.append(ape.getErrorMessage() + " at line " + csp.getLine()+", column "+ csp.getColumn()+"\n");
		  }
	  }
  }

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
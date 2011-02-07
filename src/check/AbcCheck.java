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
package check;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import abc.notation.Tune;
import abc.parser.AbcNode;
import abc.parser.AbcParseError;
import abc.parser.CharStreamPosition;
import abc.parser.TuneParserListenerInterface;
import abc.ui.awt.TuneEditorArea;

/** */
public class AbcCheck extends Applet implements TuneParserListenerInterface {
	
	private static final long serialVersionUID = -1212835148227194802L;

	private TuneEditorArea m_textArea = null;

	// private Button m_checkButton = null;
	private java.awt.List m_errorList = null;

	// private TuneParser m_parser = null;
	private java.util.List m_errors = null;

	public AbcCheck() {
		setLayout(new BorderLayout());
		m_textArea = new TuneEditorArea();
		m_textArea.getParser().addListener(this);
		m_textArea.setFont(new Font("Courier", Font.PLAIN, 15));
		m_errorList = new java.awt.List(10);
		m_errorList.setForeground(Color.red);
		m_errorList.setFont(new Font("Courier", Font.BOLD, 15));
		m_errorList.add("Errors are displayed here.");

		m_errorList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (e.getItem() instanceof Integer) {
						AbcParseError err = (AbcParseError) m_errors
								.get(((Integer) e.getItem()).intValue());
						int beginOffset = err.getCharStreamPosition().getStartIndex();
						int endOffset = err.getCharStreamPosition().getEndIndex();
						m_textArea.select(beginOffset, endOffset);
					}
				}
			}
		});
		m_errors = new Vector();
		add(m_textArea, BorderLayout.CENTER);
		add(m_errorList, BorderLayout.SOUTH);
	}

	public void tuneBegin() {
		System.out.println("Beginning to parse tune");
		m_errors.clear();
	}

	public void noTune() {
		System.out.println("No tune found!");
	}

	public void tuneEnd(Tune tune, AbcNode abcNode) {
		m_errorList.removeAll();
		m_errors = abcNode.getErrors();
		if (m_errors.isEmpty())
			m_errorList.add("No error detected.");
		else {
			for (int i = 0; i < m_errors.size(); i++) {
				AbcParseError err = (AbcParseError) m_errors.get(i);
				CharStreamPosition pos = err.getCharStreamPosition();
				String message = err.getErrorMessage()
					+ " at line " + pos.getLine() + ", col "
					+ pos.getColumn() + ", length " + pos.getLength();
				m_errorList.add(message);
			}
		}
		m_errorList.repaint();
		AbcCheck.this.repaint();
	}
}

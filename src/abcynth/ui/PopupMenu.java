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
package abcynth.ui;

/*
 * a tutorial reader.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

public class PopupMenu extends JPopupMenu implements ActionListener
{
  private static final long serialVersionUID = 7057939431750016753L;
  private JTable m_table = null;
  private Vector m_columns = null;
  private Vector m_buttons =null;

    public PopupMenu(JTable table)
    {
      m_table = table;
      m_columns = new Vector();
      m_buttons = new Vector();
      for (int i=0; i<table.getColumnModel().getColumnCount(); i++)
        m_columns.addElement(table.getColumnModel().getColumn(i));

      for (int i=0; i<m_columns.size(); i++)
      {
        JCheckBoxMenuItem button = new JCheckBoxMenuItem(((TableColumn)m_columns.elementAt(i)).getHeaderValue().toString(), true);
        m_buttons.addElement(button);
        add(button);
        button.addActionListener(this);
      }
    }

    public void actionPerformed(ActionEvent e)
    {
      for (int i=0; i<m_buttons.size(); i++)
      {
        JCheckBoxMenuItem button = (JCheckBoxMenuItem)m_buttons.elementAt(i);
        if (e.getSource().equals(button))
        {
          if (button.isSelected())
            m_table.addColumn((TableColumn)m_columns.elementAt(i));
          else
            m_table.removeColumn((TableColumn)m_columns.elementAt(i));
        }
      }
    }

}

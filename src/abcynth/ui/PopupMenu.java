package abcynth.ui;

/*
 * This code is based on an example provided by Richard Stanford,
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

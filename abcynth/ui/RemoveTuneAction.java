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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class RemoveTuneAction extends AbstractAction
{
  private static final long serialVersionUID = -6658735429197072061L;
  private TuneBookTable m_tunebookTable = null;
  public RemoveTuneAction(String name, String description, int shortcut, TuneBookTable tuneBookTable)
  {
    m_tunebookTable = tuneBookTable;
    putValue(NAME, name);
    putValue(SHORT_DESCRIPTION, description);
    putValue(MNEMONIC_KEY, new Integer(shortcut));
  }

  public void actionPerformed(ActionEvent e)
  {
    int selectedIndex = m_tunebookTable.getSelectionModel().getLeadSelectionIndex();
    if (selectedIndex!=-1)
    {
      int viewColumnNumber = m_tunebookTable.convertColumnIndexToView(TuneBookTable.REFERENCE_NUMBER_COLUMN);
      int selectedTuneIndex = ( (Integer) m_tunebookTable.getValueAt(selectedIndex, viewColumnNumber)).intValue();
      m_tunebookTable.getTuneBook().removeTune(selectedTuneIndex);
    }
  }
}
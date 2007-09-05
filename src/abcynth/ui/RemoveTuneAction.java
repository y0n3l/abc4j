package abcynth.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class RemoveTuneAction extends AbstractAction
{
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
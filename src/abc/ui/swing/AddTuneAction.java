package abc.ui.swing;

import java.awt.event.ActionEvent;

public class AddTuneAction extends TuneBookActionAbstract
{
  public AddTuneAction(String name, String description, int shortcut)
  {
    putValue(NAME, name);
    putValue(SHORT_DESCRIPTION, description);
    putValue(MNEMONIC_KEY, new Integer(shortcut));
  }

  public void actionPerformed(ActionEvent e)
  {
    int hi = getTuneBook().getHighestReferenceNumber()+1;
    getTuneBook().putTune("X:" + hi + "\n" + "T:\n" + "K:\n");
  }
}
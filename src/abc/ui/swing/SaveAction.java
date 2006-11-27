package abc.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class SaveAction extends TuneBookActionAbstract
{
  public SaveAction()
  {
    putValue(NAME, "Save");
    putValue(SHORT_DESCRIPTION, "Saves tunebook updates");
    putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
  }

  public void actionPerformed(ActionEvent e)
  {
    try
    { getTuneBook().save(); }
    catch(IOException ex)
    { ex.printStackTrace(); }
  }
}
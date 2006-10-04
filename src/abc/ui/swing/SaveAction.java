package abc.ui.swing;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;
import java.awt.event.KeyEvent;

import abc.parser.TuneBook;

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
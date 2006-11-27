package abc.ui.swing;

import javax.swing.AbstractAction;

import abc.parser.TuneBook;

public abstract class TuneBookActionAbstract extends AbstractAction
{
  private TuneBook m_tuneBook = null;

  public TuneBookActionAbstract(TuneBook tuneBook)
  { m_tuneBook = tuneBook; }

  public TuneBookActionAbstract()
  { }

  public TuneBook getTuneBook()
  { return m_tuneBook; }

  public void setTuneBook(TuneBook t)
  { m_tuneBook = t; }

}
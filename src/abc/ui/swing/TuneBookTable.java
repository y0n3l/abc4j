package abc.ui.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.SystemColor;
import scanner.*;
import abc.notation.*;
import abc.parser.*;
import java.util.*;
import java.io.*;

/** A scecial JTable that has integrated features for displaying tunebooks. */
public class TuneBookTable extends JTable// implements TuneBookListenerInterface
{
  /* Reference number column model identifier. */
  public static final int REFERENCE_NUMBER_COLUMN = 10;
  /* Title column model identifier. */
  public static final int TITLE_COLUMN = 1;
  /* Key column model identifier. */
  public static final int KEY_COLUMN = 2;
  /* Composer column model identifier. */
  public static final int COMPOSER_COLUMN = 3;
  /* Information column model identifier. */
  public static final int INFORMATION_COLUMN = 4;
  /* Discography column model identifier. */
  public static final int DISCOGRAPHY_COLUMN = 5;
  /* Origin column model identifier. */
  public static final int ORIGIN_COLUMN = 6;
  /* Rhythm column model identifier. */
  public static final int RHYTHM_COLUMN = 7;
  /* Book column model identifier. */
  public static final int BOOK_COLUMN = 8;
  /* Source column model identifier. */
  public static final int SOURCE_COLUMN = 9;

  private Vector m_tunes = null;
  private Vector m_columns = null;
  private PopupMenu m_popMenu = null;
  private TuneBookTableModel m_model = null;

  /** Creates a new TuneBookTable. */
  public TuneBookTable()
  {
    super();
    m_model = new TuneBookTableModel();
    setModel(m_model);
    setColumnModel(new TuneBookTableColumnModel());
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_popMenu = new PopupMenu(this);
    addMouseListenerToHeaderInTable();
  }

  private class TuneBookTableModel extends AbstractTableModel implements TuneBookListenerInterface
  {
    private TuneBook m_bookModel = null;
    public TuneBookTableModel(TuneBook tuneBook)
    {
      this();
      setTuneBook(tuneBook);
    }

    public TuneBookTableModel()
    { m_tunes = new Vector(); }

    public void setTuneBook(TuneBook tuneBook)
    {
      if (m_bookModel!=null)
        m_bookModel.removeListener(this);
      m_bookModel = tuneBook;
      m_bookModel.addListener(this);
      m_tunes = m_bookModel.toVector();
      fireTableDataChanged();
    }

    public int getColumnCount()
    {
      int col = TuneBookTable.this.getColumnModel().getColumnCount();
      return col;
    }
    public int getRowCount()
    { return m_tunes.size();}

    public Object getValueAt(int row, int col)
    {
      try
      {
      //System.out.println(row + " " + col);
      TuneBookTableColumnModel model = (TuneBookTableColumnModel)TuneBookTable.this.getColumnModel();
      TuneColumn column = (TuneColumn)model.getColumnFromModelIndex(col);
      Object obj = column.getValueFor((Tune)m_tunes.elementAt(row));
      return obj;
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
        return null;
      }
    };

    public void tuneChanged(TuneChangeEvent e)
    {
      System.out.println(getClass().getName() + " tune change detection " + e);
      if (e.getType()==TuneChangeEvent.TUNE_ADDED)
      {
        m_tunes.addElement(e.getTune());
        fireTableDataChanged();
      }
      else
      {
        int ref =e.getTune().getReferenceNumber();
        for (int i=0; i<m_tunes.size();i++)
        {
          if (((Tune)m_tunes.elementAt(i)).getReferenceNumber()==ref)
          {
            if (e.getType()==TuneChangeEvent.TUNE_REMOVED)
            {
              m_tunes.removeElementAt(i);
            }
            else
            {
              Tune tune = m_bookModel.getTune(ref);
              m_tunes.setElementAt(tune, i);
            }
            fireTableDataChanged();
            break;
          }
        }
      }
    }
  }
  private class TuneBookTableColumnModel extends DefaultTableColumnModel
  {
    public TuneBookTableColumnModel()
    {
      TableColumn col = new ReferenceNumberColumn();
      col.setModelIndex(REFERENCE_NUMBER_COLUMN);
      addColumn(col);

      col = new TitleColumn();
      col.setModelIndex(TITLE_COLUMN);
      addColumn(col);

      col = new KeyColumn();
      col.setModelIndex(KEY_COLUMN);
      addColumn(col);

      col = new ComposerColumn();
      col.setModelIndex(COMPOSER_COLUMN);
      addColumn(col);

/*      col = new InformationColumn();
      col.setModelIndex(INFORMATION_COLUMN);
      addColumn(col);

      col = new DiscographyColumn();
      col.setModelIndex(5);
      addColumn(col);

      col = new OriginColumn();
      col.setModelIndex(6);
      addColumn(col);*/

      col = new RhythmColumn();
      col.setModelIndex(RHYTHM_COLUMN);
      addColumn(col);

/*      col = new BookColumn();
      col.setModelIndex(BOOK_COLUMN);
      addColumn(col);

      col = new SourceColumn();
      col.setModelIndex(SOURCE_COLUMN);
      addColumn(col);*/
    }

    private TableColumn getColumnFromModelIndex(int index)
    {
      for (int i=0; i<getColumnCount(); i++)
      {
        if (getColumn(i).getModelIndex()==index)
          return getColumn(i);
      }
      return null;
    }
  }

  private void addMouseListenerToHeaderInTable()
  {
    MouseAdapter listMouseListener = new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e)
      {
        //System.out.println("CLICKED " + m_popMenu.isVisible());
        if (!m_popMenu.isVisible())
        {
          int columnIndex = getColumnModel().getColumnIndexAtX(e.getX());
          TuneColumn column = (TuneColumn)getColumnModel().getColumn(columnIndex);
          if (!column.isAscendingOrdered() && !column.isDescendingOrdered())
            column.sort(true);
          else
          if (column.isAscendingOrdered())
            column.sort(false);
          else
          if (column.isDescendingOrdered())
            column.sort(true);
          for (int i=0; i<getColumnModel().getColumnCount(); i++)
          {
            if(!getColumnModel().getColumn(i).equals(column))
            {
              ((TuneColumn)getColumnModel().getColumn(i)).setIsAscendingOrdered(false);
              ((TuneColumn)getColumnModel().getColumn(i)).setIsDescendingOrdered(false);
            }
          }
        }
      }

      public void mousePressed(MouseEvent e)
      {
        //System.out.println("PRESSED" + e.isPopupTrigger() + " " + e);
      }

      public void mouseReleased(MouseEvent e)
      {

        //System.out.println("RELEASED " + e.isPopupTrigger() + " " + e);
        if (e.isPopupTrigger())
          m_popMenu.show(TuneBookTable.this, (int)e.getX(), (int)e.getY());
      }
    };
    getTableHeader().addMouseListener(listMouseListener);
  }

  /** Sets the tunebook to be displayed in this table.
   * @param book The tunebook to be displayed in this table. */
  public void setTuneBook(TuneBook book)
  {
    //System.out.println(getClass().getName() + "setTuneBook(" + book + ")");
    //if (m_book!=null)
    //  m_book.removeListener(this);
    m_model.setTuneBook(book);
    try
    {
      int[] numbers = {0};// = m_book.getReferenceNumbers();
      for (int i=0; i<numbers.length; i++)
      {
        //System.out.println(getClass().getName() + "retrieving " + (i+1) + "/" + numbers.length );
        //m_parser.parseHeader(book.getTuneNotation(numbers[i]));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /** */
  public TuneBook getTuneBook()
  { return m_model.m_bookModel; }

  public Tune getSelectedTune()
  {
    int selectedIndex = -1;
    int viewColumnNumber = -1;
    selectedIndex = getSelectionModel().getLeadSelectionIndex();
    if (selectedIndex!=-1)
    {
      viewColumnNumber = convertColumnIndexToView(TuneBookTable.REFERENCE_NUMBER_COLUMN);
      int selectedTuneReferenceNumber = ( (Integer)getValueAt(selectedIndex, viewColumnNumber)).intValue();
      return m_model.m_bookModel.getTune(selectedTuneReferenceNumber);
    }
    else
      return null;
  }

  public void mouseClicked(MouseEvent e)
  {}

  public void mouseEntered(MouseEvent e)
  {}

  public void mouseExited(MouseEvent e)
  { }

  public void mousePressed(MouseEvent e)
  { }

  public void mouseReleased(MouseEvent e)
  { }

  public class ReferenceNumberColumn extends TuneColumn
  {
    public ReferenceNumberColumn()
    { setHeaderValue("Number"); }

    public Object getValueFor(Tune tune)
    { return new Integer(tune.getReferenceNumber()); }
  }

  public class AreaColumn extends TuneColumn
  {
    public AreaColumn()
    { setHeaderValue("Area"); }

    public Object getValueFor(Tune tune)
    { return tune.getArea(); }
  }

  public class BookColumn extends TuneColumn
  {
    public BookColumn()
    { setHeaderValue("Book"); }

    public Object getValueFor(Tune tune)
    { return tune.getBook(); }
  }

  public class ComposerColumn extends TuneColumn
  {
    public ComposerColumn()
    { setHeaderValue("Composer"); }

    public Object getValueFor(Tune tune)
    { return tune.getComposer(); }
  }

  public class DiscographyColumn extends TuneColumn
  {
    public DiscographyColumn()
    { setHeaderValue("Discography"); }

    public Object getValueFor(Tune tune)
    { return tune.getDiscography(); }
  }

  public class TitleColumn extends TuneColumn
  {
    public TitleColumn()
    { setHeaderValue("Title"); }

    public Object getValueFor(Tune tune)
    {
      if (tune.getTitles()!=null)
        return tune.getTitles()[0];
      else
        return null;
    }
  }

  public class KeyColumn extends TuneColumn
  {
    public KeyColumn()
    { setHeaderValue("Key"); }

    public Object getValueFor(Tune tune)
    {
      if (tune.getKey()!=null)
        return tune.getKey().toLitteralNotation();
      else
        return null;
    }
  }

  public class OriginColumn extends TuneColumn
  {
    public OriginColumn()
    { setHeaderValue("Origin"); }

    public Object getValueFor(Tune tune)
    { return tune.getOrigin(); }
  }

  public class InformationColumn extends TuneColumn
  {
    public InformationColumn()
    { setHeaderValue("Information"); }

    public Object getValueFor(Tune tune)
    { return tune.getInformation(); }
  }

  public class RhythmColumn extends TuneColumn
  {
    public RhythmColumn()
    { setHeaderValue("Rhythm"); }

    public Object getValueFor(Tune tune)
    { return tune.getRhythm(); }
  }

  public class SourceColumn extends TuneColumn
  {
    public SourceColumn()
    { setHeaderValue("Source"); }

    public Object getValueFor(Tune tune)
    { return tune.getSource(); }
  }



  public abstract class TuneColumn extends TableColumn
  {
    private boolean isAscendingOrdered = false;
    private boolean isDescendingOrdered = false;

    public abstract Object getValueFor(Tune tune);

    public void sort(boolean ascendingOrder)
    {
      Comparator comp = null;
      if (ascendingOrder)
      {
        comp = new Comparator()
        {
          public int compare(Object obj1, Object obj2)
          {
            Comparable o1 =  (Comparable)getValueFor((Tune)obj1);
            Comparable o2 =  (Comparable)getValueFor((Tune)obj2);
            if (o1==null && o2==null) return 0;
            else if (o1==null) return -1;
            else if (o2==null) return 1;
            else return o1.compareTo(o2);
          }
        };
        isAscendingOrdered = true;
        isDescendingOrdered = false;
      }
      else
      {
        comp = new Comparator()
        {
          public int compare(Object obj1, Object obj2)
          {
            Comparable o1 =  (Comparable)getValueFor((Tune)obj1);
            Comparable o2 =  (Comparable)getValueFor((Tune)obj2);
            if (o1==null && o2==null) return 0;
            else if (o1==null) return 1;
            else if (o2==null) return -1;
            else return o2.compareTo(o1);
          }
        };
        isAscendingOrdered = false;
        isDescendingOrdered = true;
      }
      Collections.sort(m_tunes, comp);
      updateUI();
    }

    public boolean isAscendingOrdered()
    { return isAscendingOrdered; }

    public boolean isDescendingOrdered()
    { return isDescendingOrdered; }

    void setIsAscendingOrdered(boolean order)
    { isAscendingOrdered = order; }

    void setIsDescendingOrdered(boolean order)
    { isDescendingOrdered = order; }

  }
}
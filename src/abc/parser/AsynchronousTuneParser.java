package abc.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;

import abc.notation.Tune;

/** A parser that parses abc gammar into its own thread context. */
public class AsynchronousTuneParser extends TuneParser
{
  private Object m_mutex = new Object();
  private Thread m_parsingThread = null;
  private Vector m_queue = null;
  private boolean m_isQueueManagementEnabled = true;

  /** Constructs a new tune parser. */
  public AsynchronousTuneParser()
  {
    super();
    m_queue = new Vector();
    m_parsingThread = new Thread(new ParsingRunnable());
    m_parsingThread.start();
  }

  public AsynchronousTuneParser(boolean isQueueManagementEnabled)
  {
    super();
    m_isQueueManagementEnabled = isQueueManagementEnabled;
    m_queue = new Vector();
    m_parsingThread = new Thread(new ParsingRunnable());
    m_parsingThread.start();
  }

  /** Parse the given string and creates a <TT>Tune</TT> object as parsing result.
   * @param tune The abc tune, as a String, to be parsed.
   * @return An object representation of the abc notation string. */
  public Tune parse(String tune)
  {
    synchronized(m_mutex)
    {
      if (!m_isQueueManagementEnabled)
        m_queue.removeAllElements();
      m_queue.addElement(new QueueElement(new StringReader(tune), false));
      m_mutex.notify();
    }
    return null;
  }

  public Tune parse(Reader charStream)
  {
    synchronized(m_mutex)
    {
      if (!m_isQueueManagementEnabled)
        m_queue.removeAllElements();
      m_queue.addElement(new QueueElement(charStream, false));
      m_mutex.notify();
    }
    return null;
  }

  public Tune parseHeader(String tune)
  {
    synchronized(m_mutex)
    {
      if (!m_isQueueManagementEnabled)
        m_queue.removeAllElements();
      m_queue.addElement(new QueueElement(new StringReader(tune), true));
      m_mutex.notify();
    }
    return null;
  }

  public Tune parseHeader(Reader charStream)
  {
    synchronized(m_mutex)
    {
      if (!m_isQueueManagementEnabled)
        m_queue.removeAllElements();
      m_queue.addElement(new QueueElement(charStream, true));
      m_mutex.notify();
    }
    return null;
  }

  private Vector getQueue()
  { return m_queue; }

  public void superParse(Reader r)
  { super.parse(r); }

  public void superParseHeader(Reader r)
  { super.parseHeader(r); }

  private class ParsingRunnable implements Runnable
  {
    public void run()
    {
      try
      {
        synchronized(m_mutex)
        {
          while(true)
          {
            while (!m_queue.isEmpty())
            {
              QueueElement q = (QueueElement)getQueue().elementAt(0);
              getQueue().removeElementAt(0);
              //System.out.println(" parsing " + q);
              if (q.headerOnly())
                AsynchronousTuneParser.this.superParseHeader(q.getNotation());
              else
                AsynchronousTuneParser.this.superParse(q.getNotation());
            }
            //System.out.println("waiting");
            m_mutex.wait();
          }
        }
      }
      catch (InterruptedException e)
      { e.printStackTrace(); }
    }
  }

  private class QueueElement
  {
    private Reader m_notation = null;
    private boolean m_headerOnly = false;
    public QueueElement(Reader notation, boolean headerOnly)
    {
      m_notation = notation;
      m_headerOnly = headerOnly;
    }

    public Reader getNotation()
    { return m_notation; }

    public boolean headerOnly()
    { return m_headerOnly; }

  }
}


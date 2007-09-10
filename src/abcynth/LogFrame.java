package abcynth;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import scanner.InvalidCharacterEvent;
import scanner.TokenEvent;
import abc.notation.Tune;
import abc.parser.AbcFileParserListenerInterface;
import abc.parser.InvalidTokenEvent;


/** */
public class LogFrame extends JFrame implements AbcFileParserListenerInterface
{
  private JTextArea m_errorsArea = null;

  public LogFrame()
  {
    super("Logs...");
    m_errorsArea = new JTextArea();
    JScrollPane logPane = new JScrollPane(m_errorsArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    getContentPane().add(logPane);
  }

  public JTextArea getErrorsArea()
  { return m_errorsArea; }

  public void tuneBegin() {
  }

  public void invalidToken(InvalidTokenEvent event)
  {
    m_errorsArea.append("Invalid token " + event.getToken().getValue() + " at " + event.getPosition().toString() + "\n");
  }

  public void validToken(TokenEvent event)
  {}

  public void invalidCharacter(InvalidCharacterEvent event)
  {
    m_errorsArea.append("Invalid character " + event.getCharacter() + " at " + event.getPosition().toString()+ "\n");
  }

  public void tuneEnd(Tune tune)
  { }

  public void fileBegin()
  {
    m_errorsArea.setText("");
  }

  public void lineProcessed(String line)
  {}

  public void fileEnd()
  {
  }
}
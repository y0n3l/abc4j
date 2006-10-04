package abc.parser;

/** The interface to be implemented when parsing abc files. */
public interface AbcFileParserListenerInterface extends TuneParserListenerInterface
{
  /** Invoked when the parsing of the file begins. */
  public void fileBegin();

  /** Invoked when a line has been processed.
   * @param line The line that has just been processed. */
  public void lineProcessed(String line);

  /** Invoked when the parsing of the file end. */
  public void fileEnd();
}

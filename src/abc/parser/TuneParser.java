package abc.parser;

import java.io.Reader;

import abc.notation.Tune;

/** This class provides String parsing for tunes in abc notation. */
public class TuneParser extends AbcParserAbstract
{
  /** Constructs a new tune parser. */
  public TuneParser()
  {}

  /** Parses the given string and returns the tune corresponding to the notation.
   * @param tuneNotation A tune written using ABC notation.
   * @return The tune corresponding to the given notation. */
  public Tune parse(String tuneNotation)
  { return super.parse(tuneNotation); }

  /** Parses the abc stream and returns the tune corresponding to the notation.
   * @param abcCharStream An abc stream.
   * @return The tune corresponding to the given notation. */
  public Tune parse(Reader abcCharStream)
  { return super.parse(abcCharStream); }

  /** Parses the tune notation and returns only header information.
   * @param tuneNotation A tune written using ABC notation.
   * @return Header information corresponding to the given notation. */
  public Tune parseHeader(String tuneNotation)
  { return super.parseHeader(tuneNotation); }

  /** Parse the given stream and creates a <TT>Tune</TT> object with no score
   * as parsing result. This purpose of this method method is to provide a
   * faster parsing when just abc header fields are needed.
   * @param abcCharStream A stream in abc Notation.
   * @return An object representation with no score of the abc stream. */
  public Tune parseHeader(Reader abcCharStream)
  { return super.parseHeader(abcCharStream); }

  /** Adds a listener to catch events thrwon by the parser durin tune parsing.
   * @param listener Object that implements the TuneParserListenerInterface.
   * @see #removeListener(abc.parser.TuneParserListenerInterface) */
  public void addListener(TuneParserListenerInterface listener)
  { super.addListener(listener); }

  /** Removes a listener from this parser.
   * @param listener The listener to be removed.
   * @see #addListener(abc.parser.TuneParserListenerInterface) */
  public void removeListener(TuneParserListenerInterface listener)
  { super.removeListener(listener); }
}


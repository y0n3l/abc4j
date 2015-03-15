// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
package abc.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A convenient class to ease the parsing of a tune book. The result of the
 * parsing is returned as a TuneBook object synchronously. You don't have to
 * attach yourself as a listener or whatever to get the parsing result.
 */
public class TuneBookParser extends AbcParserAbstract {

	private AbcTuneBook m_tuneBook = null;

	/**
	 * Constructs a new tunebook parser.
	 */
	public TuneBookParser() {
		super();
	}

	/**
	 * Return last fully-parsed tunebook, <code>null</code> if not yet parsed
	 * or only headers
	 */
	public AbcTuneBook getTuneBook() {
		return m_tuneBook;
	}

	/**
	 * Parses the specified file in ABC notation.
	 * 
	 * @param file
	 *            Tune book file in ABC notation.
	 * @return A tune book representing the ABC notation stream.
	 */
	public AbcTuneBook parse(File file) throws IOException {
		return parse0(getParseTree(new FileReader(file)));
	}

	/**
	 * Parses the specified stream in ABC notation.
	 * 
	 * @param reader
	 *            Tune book stream in ABC notation.
	 * @return A tune book representing the ABC notation stream.
	 */
	public AbcTuneBook parse(Reader reader) throws IOException {
		return parse0(getParseTree(reader));
	}

	/**
	 * Parse the given string and creates a <TT>Tune</TT> object as parsing
	 * result.
	 * 
	 * @param tune
	 *            The abc tune book, as a String, to be parsed.
	 * @return An object representation of the abc notation string.
	 */
	public AbcTuneBook parse(String tune) {
		return parse0(getParseTree(tune));
	}

	private AbcTuneBook parse0(AbcNode abcRoot) {
		notifyListenersForTuneBookBegin();
		if (abcRoot != null) {
			AbcNode tuneBookHeader = abcRoot.getChild(AbcTuneBookHeader);
			m_tuneBook = parseTuneBookHeader(tuneBookHeader);
			for (AbcNode abcTuneNode : abcRoot.getChilds(AbcTune)) {
				m_tuneBook.putTune(parseAbcTune(abcTuneNode));
			}
		} else {
			m_tuneBook = newAbcTuneBook();
			notifyListenersForEmptyTuneBook();
		}
		notifyListenersForTuneBookEnd(m_tuneBook, abcRoot);
		return m_tuneBook;
	}

	/**
	 * Parse the given file and creates a <TT>TuneBook</TT> object with no music
	 * as parsing result. This purpose of this method method is to provide a
	 * faster parsing when just tune book and its tunes header fields are needed.
	 * 
	 * @param file
	 *            The file to be parsed.
	 * @return An object representation with no score of the abc notation
	 *         string.
	 */
	public AbcTuneBook parseHeaders(File file) throws IOException {
		return parseHeaders0(getParseTree(new FileReader(file)));
	}

	/**
	 * Parse the given stream and creates a <TT>TuneBook</TT> object with no music
	 * as parsing result. This purpose of this method method is to provide a
	 * faster parsing when just tune book and its tunes header fields are needed.
	 * 
	 * @param reader
	 *            The stream to be parsed.
	 * @return An object representation with no score of the abc notation
	 *         string.
	 */
	public AbcTuneBook parseHeaders(Reader reader) throws IOException {
		return parseHeaders0(getParseTree(reader));
	}

	/**
	 * Parse the given String and creates a <TT>TuneBook</TT> object with no music
	 * as parsing result. This purpose of this method method is to provide a
	 * faster parsing when just tune book and its tunes header fields are needed.
	 * 
	 * @param tune
	 *            A tune notation in ABC.
	 * @return A tune representing the ABC notation with header values only.
	 */
	public AbcTuneBook parseHeaders(String tune) {
		return parseHeaders0(getParseTree(tune));
	}

	private AbcTuneBook parseHeaders0(AbcNode abcRoot) {
		notifyListenersForTuneBookBegin();
		if (abcRoot != null) {
			AbcNode tuneBookHeader = abcRoot.getChild(AbcTuneBookHeader);
			AbcTuneBook tuneBook = parseTuneBookHeader(tuneBookHeader);
			for (AbcNode abcTuneNode : abcRoot.getChilds(AbcTune)) {
				initNewTune();
				notifyListenersForTuneBegin();
				AbcNode abcHeaderNode = null;
				if (abcTuneNode != null)
					abcHeaderNode = abcTuneNode.getChild("AbcHeader");
				AbcTune tune;
				if (abcHeaderNode == null) {
					tune = new AbcTune();
					notifyListenersForNoTune();
				} else {
					tune = parseAbcHeader(abcHeaderNode);
					tune.setAbcString(abcHeaderNode.getValue());
				}
				notifyListenersForTuneEnd(tune, abcHeaderNode);
				tuneBook.putTune(tune);
			}
		} else {
			m_tuneBook = newAbcTuneBook();
			notifyListenersForEmptyTuneBook();
		}
		notifyListenersForTuneBookEnd(m_tuneBook, abcRoot);
		return m_tuneBook;
	}

}

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
 * A convenient class to ease the parsing of ONE tune. The result of the parsing
 * is directly returned as a Tune object synchronously. You don't have to attach
 * yourself as a listener or whatever to get the parsing result.
 */
public class TuneParser extends AbcParserAbstract {

	private AbcTune m_tune = null;
	
	/**
	 * Constructs a new tune parser.
	 */
	public TuneParser() {
		super();
	}
	
	/**
	 * Return last completly parsed tune, <code>null</code> if not yet parsed
	 */
	public AbcTune getTune() {
		return m_tune;
	}
	
	/**
	 * Parses the specified file in ABC notation.
	 * 
	 * @param file
	 *            Tune file in ABC notation.
	 * @return A tune representing the ABC notation stream.
	 */
	public AbcTune parse(File file) throws IOException {
		return parse0(getParseTree(new FileReader(file)));
	}
	
	/**
	 * Parses the specified stream in ABC notation.
	 * 
	 * @param reader
	 *            Tune stream in ABC notation.
	 * @return A tune representing the ABC notation stream.
	 */
	public AbcTune parse(Reader reader) throws IOException {
		return parse0(getParseTree(reader));
	}

	/**
	 * Parse the given string and creates a <TT>Tune</TT> object as parsing
	 * result.
	 * 
	 * @param tune
	 *            The abc tune, as a String, to be parsed.
	 * @return An object representation of the abc notation string.
	 */
	public AbcTune parse(String tune) {
		return parse0(getParseTree(tune));
	}

	private AbcTune parse0(AbcNode abcRoot) {
		AbcNode abcTuneNode = abcRoot.getChild("AbcTune");
		m_tune = parseAbcTune(abcTuneNode);
		return m_tune;
	}

	/**
	 * Parse the given file and creates a <TT>Tune</TT> object with no music
	 * as parsing result. This purpose of this method method is to provide a
	 * faster parsing when just abc header fields are needed.
	 * 
	 * @param file
	 *            The file to be parsed.
	 * @return An object representation with no score of the abc notation
	 *         string.
	 */
	public AbcTune parseHeader(File file) throws IOException {
		return parseHeader0(getParseTree(new FileReader(file)));
	}

	/**
	 * Parse the given stream and creates a <TT>Tune</TT> object with no music
	 * as parsing result. This purpose of this method method is to provide a
	 * faster parsing when just abc header fields are needed.
	 * 
	 * @param reader
	 *            The stream to be parsed.
	 * @return An object representation with no score of the abc notation
	 *         string.
	 */
	public AbcTune parseHeader(Reader reader) throws IOException {
		return parseHeader0(getParseTree(reader));
	}

	/**
	 * Parses the header of the specified tune notation.
	 * 
	 * @param tune
	 *            A tune notation in ABC.
	 * @return A tune representing the ABC notation with header values only.
	 */
	public AbcTune parseHeader(String tune) {
		return parseHeader0(getParseTree(tune));
	}

	private AbcTune parseHeader0(AbcNode abcRoot) {
		initNewTune();
		notifyListenersForTuneBegin();
		AbcNode abcHeaderNode = null;
		if (abcRoot != null) {
			AbcNode abcTuneNode = abcRoot.getChild("AbcTune");
			if (abcTuneNode != null) {
				abcHeaderNode = abcTuneNode.getChild("AbcHeader");
			}
		}
		AbcTune tune;
		if (abcHeaderNode == null) {
			tune = new AbcTune();
			notifyListenersForNoTune();
		} else {
			tune = parseAbcHeader(abcHeaderNode);
			tune.setAbcString(abcHeaderNode.getValue());
		}
		notifyListenersForTuneEnd(tune, abcHeaderNode);
		return tune;
	}

}

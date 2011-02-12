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

import abc.notation.TuneBook;

/**
 * Interface that should be implemented by any object that listens to tune book
 * parsing.
 * 
 * It listen too for tune parser event such as
 * {@link abc.parser.TuneParserListenerInterface#tuneBegin()}, noTune(),
 * tuneEnd().
 * 
 * @see abc.parser.TuneBookParserAdapter for a simple implementation
 * skeleton.
 */
public interface TuneBookParserListenerInterface extends
		TuneParserListenerInterface {

	/** Invoked when the tune book contains nothing: no header, no tune */
	public void emptyTuneBook();

	/** Invoked when the parsing of the tune book begins. */
	public void tuneBookBegin();

	/**
	 * Invoked when the parsing of a tune book has ended.
	 * 
	 * @param tunebook
	 *            The tune book that has just been parsed.
	 * @param abcRoot
	 *            The parsing tree which could be browsed to find errors, node
	 *            types.
	 */
	public void tuneBookEnd(TuneBook tuneBook, AbcNode abcRoot);

}

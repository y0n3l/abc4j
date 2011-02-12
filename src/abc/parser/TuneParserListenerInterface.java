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

import java.util.EventListener;

import abc.notation.Tune;

/** Interface that should be implemented by any object that listens to tunes
 * parsing.
 * 
 * @see abc.parser.TuneParserAdapter for a simple implementation
 * skeleton. */
public interface TuneParserListenerInterface extends EventListener
{
	
	/** Returns true if a tune is currently into parsing process */
	public boolean isBusy();

	/** Invoked when no tune was found in the stream. */
	public void noTune();

	/** Invoked when the parsing of the tune begins. */
	public void tuneBegin();

	/** Invoked when the parsing of a tune has ended.
	 * @param tune The tune that has just been parsed.
	 * @param abcRoot The parsing tree which could be browsed
	 * to find errors, node types. */
	public void tuneEnd(Tune tune, AbcNode abcRoot);

}

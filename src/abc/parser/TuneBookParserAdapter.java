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

import abc.notation.Tune;
import abc.notation.TuneBook;

/** An empty implementation of a tune book parser listener that does nothing. */
public class TuneBookParserAdapter implements TuneBookParserListenerInterface {
	
	private boolean isBusy = false;

	public void emptyTuneBook() {
		isBusy = false;
	}
	
	public boolean isBusy() {
		return isBusy;
	}

	public void tuneBookBegin() {
		isBusy = true;
	}

	public void tuneBookEnd(TuneBook tuneBook, AbcNode abcRoot) {
		isBusy = false;
	}

	public void noTune() {
		isBusy = false;
	}

	public void tuneBegin() {
	}

	public void tuneEnd(Tune tune, AbcNode abcRoot) {
	}

}

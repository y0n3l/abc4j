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
import java.io.FileWriter;
import java.io.IOException;

import abc.notation.Tune;

/**
 * An AbcTune is a {@link abc.notation.Tune} writted in ABC language. <br>
 * In addition to Tune content (infos, parts, voices, music, notes...), it
 * contains the ABC string source.
 * <p>
 * Even if the tune content is modified, the {@link #saveAs(File)} method save
 * the non-modified source. <b>This is not yet a Tune to ABC converter!</b>
 */
public class AbcTune extends Tune {

	private static final long serialVersionUID = 3677824899152119849L;

	private String m_abcString = null;

	public AbcTune() {
		super();
	}
	
	public AbcTune(Tune tune) {
		super(tune);
	}

	protected AbcTune(Tune tune, String abcString) {
		super(tune);
		m_abcString = abcString;
	}

	/**
	 * Returns the ABC source String. Even if the tune content is modified, this
	 * is <b>non-modified</b> from the original constructor
	 * {@link #AbcTune(Tune, String)}
	 * 
	 * @return may return <code>null</code>
	 */
	public String getAbcString() {
		return m_abcString;
	}

	/**
	 * Saves the ABC source String to file. Even if the tune content is
	 * modified, this is <b>non-modified</b> from the original constructor
	 * {@link #AbcTune(Tune, String)}
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void saveAs(File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			writer.write(getAbcString());
		} finally {
			writer.close();
		}
	}
	
	protected void setAbcString(String abcString) {
		m_abcString = abcString;
	}

}

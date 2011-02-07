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
import java.util.ArrayList;
import java.util.Iterator;

import abc.notation.Tune;
import abc.notation.TuneBook;
import abc.notation.TuneInfos;

/**
 * An AbcTuneBook is a {@link abc.notation.TuneBook} writted in ABC language.
 * <br>
 * In addition to TuneBook and Tune contents (infos, parts, voices, music,
 * notes...), it contains the ABC string source.
 * <p>
 * This class supports add, delete and save methods, which allow to create and
 * modify a tune book, by handling {@link abc.parser.AbcTune} objects. <br>
 * As described in AbcTune, even if the tunes' content is modified, the
 * {@link #saveAs(File)} method save the non-modified source for each tune.
 */
public class AbcTuneBook extends TuneBook {

	private static final long serialVersionUID = -641380916972326741L;

	private String m_abcHeaderString = "";

	private ArrayList m_listeners = new ArrayList(2);

	public AbcTuneBook() {
		super();
	}

	public AbcTuneBook(TuneBook tuneBook) {
		super(tuneBook);
	}

	protected AbcTuneBook(TuneBook tuneBook, String abcString) {
		super(tuneBook);
		m_abcHeaderString = abcString;
	}

	/**
	 * Adds a listener to this tunebook to be aware of tunes changes.
	 * 
	 * @param l
	 *            The listener to be added.
	 */
	public void addListener(TuneBookListenerInterface l) {
		m_listeners.add(l);
	}

	public String getAbcHeaderString() {
		return m_abcHeaderString;
	}

	public String getAbcString() {
		StringBuffer sb = new StringBuffer(getAbcHeaderString());
		if (sb.length() > 0)
			sb.append("\n\n");
		Iterator it = toVector().iterator();
		while (it.hasNext()) {
			Tune tune = (Tune) it.next();
			if (tune instanceof AbcTune) {
				sb.append(((AbcTune) tune).getAbcString().trim());
				sb.append("\n\n");
			}
		}
		return sb.toString();
	}

	protected void notifyListenersForTuneChange(TuneChangeEvent e) {
		for (int i = 0; i < m_listeners.size(); i++)
			((TuneBookListenerInterface) m_listeners.get(i)).tuneChanged(e);
	}
	
	public boolean putTune(String abcString) {
		TuneParser tp = new TuneParser();
		return putTune(tp.parse(abcString));
	}

	/**
	 * Put a tune into the book. If exists, replace tune with the same reference
	 * number and returns true. If doesn't exist, add it and return false.
	 * 
	 * @param tune
	 * @return true if it replaced an existing tune, false otherwise
	 */
	public boolean putTune(AbcTune tune) {
		boolean ret = containsTune(tune);
		super.putTune(tune);
		TuneChangeEvent tce = new TuneChangeEvent(this,
				TuneChangeEvent.TUNE_ADDED, tune);
		if (ret) // update
			tce.setType(TuneChangeEvent.TUNE_UPDATED);
		notifyListenersForTuneChange(tce);
		return ret;
	}

	/**
	 * Removes a listener from this tunebook.
	 * 
	 * @param l
	 *            The listener to be removed.
	 */
	public void removeListener(TuneBookListenerInterface l) {
		m_listeners.remove(l);
	}

	/**
	 * Removes the tune having the requested reference number from the book
	 * 
	 * @param referenceNumber
	 * @return the removed tune, null if didn't exist.
	 */
	public Tune removeTune(int referenceNumber) {
		Tune ret = super.removeTune(referenceNumber);
		if (ret != null) {
			notifyListenersForTuneChange(new TuneChangeEvent(this,
					TuneChangeEvent.TUNE_REMOVED, ret));
		}
		return ret;
	}
	
	/**
	 * Saves the ABC source String to file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void saveTo(File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			writer.write(getAbcString());
		} finally {
			writer.close();
		}
	}

	protected void setAbcHeaderString(String abcString) {
		m_abcHeaderString = abcString;
	}

	protected void setBookInfos(TuneInfos bookInfos) {
		super.setBookInfos(bookInfos);
	}

}

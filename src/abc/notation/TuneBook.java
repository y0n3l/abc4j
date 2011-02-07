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
package abc.notation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * A tune book is a collection of {@link abc.notation.Tune}s with optionally
 * some common headers information (e.g. author, discography, book) and
 * {@link abc.notation.Instruction}s.
 */
public class TuneBook implements Cloneable, Serializable {

	private static final long serialVersionUID = -4434875253102494298L;

	/** Textual informations for all tunes in the book */
	private TuneInfos m_bookInfos = null;

	/** Collection of Instruction object (Xcommand, user defined symbols) */
	private ArrayList m_instructions = null;

	/**
	 * The structure used to store the tunes. Key = Integer(ReferenceNumber)
	 * Value = TranscribedTune instance
	 */
	private TreeMap m_tunes = null;

	public TuneBook() {
		m_tunes = new TreeMap();
		m_bookInfos = new TuneInfos();
	}
	
	public TuneBook(TuneBook tuneBook) {
		m_tunes = (TreeMap) tuneBook.m_tunes.clone();
		m_bookInfos = (TuneInfos) tuneBook.m_bookInfos.clone();
		m_instructions = (ArrayList) tuneBook.getInstructions().clone();
	}

	/**
	 * Add an Instruction to all tune in this book (Xcommand, user defined
	 * symbols)
	 */
	public void addInstruction(Instruction i) {
		if (i != null)
			getInstructions().add(i);
	}
	
	public Object clone() {
		return new TuneBook(this);
	}

	/**
	 * Returns true if tune book already contains a tune with the same reference
	 * number.
	 * 
	 * @param referenceNumber
	 */
	public boolean containsTune(int referenceNumber) {
		return getTune(new Integer(referenceNumber)) != null;
	}

	/**
	 * Returns true if tune book already contains a tune with the same reference
	 * number.
	 * 
	 * @param tune
	 */
	public boolean containsTune(Tune tune) {
		return containsTune(tune.getReferenceNumber());
	}

	/**
	 * Returns TuneInfos object which contains textual informations concerning
	 * all tunes in this book
	 */
	public TuneInfos getBookInfos() {
		return m_bookInfos;
	}
	
	/**
	 * Returns the highest reference number from all the reference numbers this
	 * tunebook contains.
	 * 
	 * @return The highest reference number from all the reference numbers this
	 *         tunebook contains. -1 is returned if no tune is stored in this
	 *         tunebook
	 */
	public int getHighestReferenceNumber() {
		int max = -1;
		Iterator it = m_tunes.keySet().iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			if (i > max)
				max = i;
		}
		return max;
	}

	/**
	 * Returns a collection of Instruction object (Xcommand, user defined
	 * symbols)
	 */
	public ArrayList getInstructions() {
		if (m_instructions == null)
			m_instructions = new ArrayList();
		return m_instructions;
	}

	/**
	 * Returns the reference numbers of tunes contained in this tunebook.
	 * 
	 * @return An array containing the reference numbers of tunes contained in
	 *         this tunebook, ordered in the way they were added in this
	 *         tunebook.
	 */
	public int[] getReferenceNumbers() {
		Iterator it = m_tunes.keySet().iterator();
		int[] refNb = new int[m_tunes.size()];
		int index = 0;
		while (it.hasNext()) {
			refNb[index] = ((Integer) it.next()).intValue();
			index++;
		}
		return refNb;
	}

	/**
	 * Returns the tune requested by its reference number. <br>
	 * Returns null if ref number is not found
	 * 
	 * @param referenceNumber
	 * @return a Tune or null
	 */
	public Tune getTune(int referenceNumber) {
		Object o = m_tunes.get(new Integer(referenceNumber));
		if (o != null)
			return (Tune) o;
		else
			return null;
	}

	/**
	 * Returns a map of reference number => Tune
	 */
	public Map getTunes() {
		return m_tunes;
	}

	/**
	 * Put a tune into the book. If exists, replace tune with the same reference
	 * number and returns true. If doesn't exist, add it and return false.
	 * 
	 * @param tune
	 * @return true if it replaced an existing tune, false otherwise
	 */
	public boolean putTune(Tune tune) {
		boolean ret = containsTune(tune);
		tune.getTuneInfos().setBookInfos(getBookInfos());
		tune.getInstructions().addAll(0, getInstructions());
		m_tunes.put(new Integer(tune.getReferenceNumber()), tune);
		return ret;
	}

	/**
	 * Remove the tune having the requested reference number from the book
	 * 
	 * @param referenceNumber
	 * @return the removed tune, null if didn't exist.
	 */
	public Tune removeTune(int referenceNumber) {
		Tune ret = getTune(referenceNumber);
		if (ret != null) {
			ret.getTuneInfos().setBookInfos(null);
			ret.getInstructions().removeAll(getInstructions());
			m_tunes.remove(new Integer(referenceNumber));
		}
		return ret;
	}

	/**
	 * Remove the tune having the same reference number from the book
	 * 
	 * @param tune
	 * @return the removed tune, null if didn't exist.
	 */
	public Tune removeTune(Tune tune) {
		return removeTune(tune.getReferenceNumber());
	}

	protected void setBookInfos(TuneInfos bookInfos) {
		m_bookInfos = bookInfos;
	}

	/** Returns the number of tunes contained in this tunebook. */
	public int size() {
		return m_tunes.size();
	}

	public Vector toVector() {
		Vector v = new Vector(size(), 5);
		Iterator it = m_tunes.keySet().iterator();
		while (it.hasNext()) {
			Integer i = (Integer) it.next();
			v.addElement(m_tunes.get(i));
		}
		return v;
	}
}

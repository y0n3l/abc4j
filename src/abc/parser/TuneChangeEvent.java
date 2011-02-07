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

import java.util.EventObject;

import abc.notation.Tune;

/** Event used when a state changed occured on a tune. */
public class TuneChangeEvent extends EventObject {

	private static final long serialVersionUID = 8455472810254418509L;

	/** The tune added event type. */
	public static final byte TUNE_ADDED = 2;

	/** The tune removed event type. */
	public static final byte TUNE_REMOVED = 1;

	/** The tune updated event type. */
	public static final byte TUNE_UPDATED = 0;

	/** The type of event. */
	private byte m_eventType = 0;

	/** The tune that changed. */
	private Tune m_tune = null;

	/**
	 * Creates a new event describing a tune change.
	 * 
	 * @param source
	 *            The source that generated this event.
	 * @param eventType
	 *            The type of this event, {@link #TUNE_ADDED},
	 *            {@link #TUNE_REMOVED}, {@link #TUNE_UPDATED}
	 * @param newTune
	 *            The tune that has changed (new, been removed or updated)
	 */
	public TuneChangeEvent(Object source, byte eventType, Tune newTune) {
		super(source);
		m_eventType = eventType;
		m_tune = newTune;
	}

	/**
	 * Returns the tune that has changed.
	 * 
	 * @return The tune that has changed.
	 */
	public Tune getTune() {
		return m_tune;
	}

	/**
	 * Returns the tune notation in ABC format.
	 * 
	 * @return the tune notation in ABC format.
	 */
	public String getTuneNotation() {
		if (m_tune instanceof AbcTune)
			return ((AbcTune)m_tune).getAbcString();
		else
			return null;
	}

	/**
	 * Returns the type of this event.
	 * 
	 * @return The type of this event.
	 */
	public int getType() {
		return m_eventType;
	}

	/**
	 * Sets the type of this event.
	 * 
	 * @param eventType
	 *            The type of this event, {@link #TUNE_ADDED},
	 *            {@link #TUNE_REMOVED}, {@link #TUNE_UPDATED}
	 */
	protected void setType(byte eventType) {
		m_eventType = eventType;
	}

	/**
	 * Returns a string representation of this event.
	 * 
	 * @return A string representation of this event.
	 */
	public String toString() {
		if (m_eventType == TUNE_REMOVED)
			return "Tune n°" + m_tune.getReferenceNumber() + " REMOVED";
		else if (m_eventType == TUNE_ADDED)
			return "Tune n°" + m_tune.getReferenceNumber() + " ADDED";
		else
			return "Tune n°" + m_tune.getReferenceNumber() + " UPDATED";
	}
}

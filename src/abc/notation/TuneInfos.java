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
import java.util.Collection;
import java.util.HashMap;

/**
 * TuneInfos is a convenient way to store textual informations about a tune or a
 * tune book like author, title, source, discography...
 * <p>
 * TuneInfos stores author(s), title(s), source(s)... and also another TuneInfos
 * object which is related to the TuneBook in which the tune is stored.
 * <br>Methods as {@link #get(byte)} or {@link #getAsCollection(byte)}
 * returns requested infos from the tune and the book if available.
 * <p> 
 * This is more flexible than all these field in Tune object.
 */
public class TuneInfos implements Cloneable, Serializable {

	/**
	 * Area, e.g. A:Donegal, A:Bampton
	 * @see #ORIGIN
	 */
	public static final byte AREA = 1;

	/** Book, e.g. B:O'Neills */
	public static final byte BOOK = 2;

	/**
	 * Music composer, e.g. C:Trad.
	 * @see #LYRICIST
	 */
	public static final byte COMPOSER = 3;

	/** Discography, e.g. D:Chieftans IV */
	public static final byte DISCOGRAPHY = 4;

	/** File URL, e.g. F:http://www.url.com/thisfile.abc */
	public static final byte FILEURL = 5;

	/** Instrument group, e.g. G:flute */
	public static final byte GROUP = 6;

	/** Tune history, e.g. H:This this said to ... */
	public static final byte HISTORY = 7;

	/**
	 * Information is ABC 1.6 standard, in 2.0 it's replaced by instructions
	 */
	public static final byte INFORMATIONS = 8;

	/** Author of lyrics, not ABC standard, or maybe an attempt with "A:"
	 * in v1.76 ? */
	public static final byte LYRICIST = 9;

	/** Notes about tune, e.g. N:see also O'Neills - 234 */
	public static final byte NOTES = 10;

	/**
	 * Origin, e.g. O::Irish, O:English
	 * @see #AREA
	 */
	public static final byte ORIGIN = 11;

	/** Rhythm, e.g. R:reel */
	public static final byte RHYTHM = 12;

	private static final long serialVersionUID = 3755742386020527700L;

	/** Source, e.g. S:collected in Brittany */
	public static final byte SOURCE = 13;

	/** Title and sub-title(s), e.g. T:Paddy O'Rafferty */
	public static final byte TITLE = 14;

	/**
	 * Transcriber notes, e.g. Z:from photocopy Z:Transcriber &lt;email&gt;
	 * &lt;website&gt;...
	 */
	public static final byte TRANSCRIPTION = 15;

	/** Lyrics put under the score, W: field */
	public static final byte WORDS = 16;
	
	private static final char lineSeparator = '\n';

	private static Collection<String> arrayToCollection(String[] s) {
		if (s == null)
			return new ArrayList<String>(1);
		else {
			ArrayList<String> list = new ArrayList<String>(s.length);
			for (int i = 0; i < s.length; i++) {
				list.add(s[i]);
			}
			return list;
		}
	}
	
	/** TuneBook infos
	 * @see #get(byte) */
	private TuneInfos m_bookInfos = null;

	private HashMap<Byte, String> m_infos = null;
	
	protected TuneInfos() {
		m_infos = new HashMap<Byte, String>();
	}

	/** Add each element of collection c as new lines in field b */
	public void add(byte b, Collection<String> c) {
		if ((c != null) && (c.size() > 0)) {
			String s2 = get(b);
			for (String s : c) {
				if (s2 == null)
					s2 = s;
				else
					s2 += lineSeparator + s;
			}
			set(b, s2);
		}
	}

	/** Add the string s as new line to field b */
	public void add(byte b, String s) {
		if (s != null) {
			String s2 = get(b);
			if (s2 == null)
				s2 = s;
			else
				s2 += lineSeparator + s;
			set(b, s2);
		}
	}

	/** Add each element of string array as new lines in field b */
	public void add(byte b, String[] s) {
		add(b, arrayToCollection(s));
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			Object o = super.clone();
			((TuneInfos)o).m_infos = (HashMap<Byte, String>) m_infos.clone();
			return o;
		} catch (CloneNotSupportedException cnse) {
			throw new InternalError();
		}
	}

	/**
	 * Returns the whole content of field b, null if not defined
	 * 
	 * @param b
	 * @return
	 */
	public String get(byte b) {
		Object o = m_infos.get(key(b));
		String o2 = null;
		if (m_bookInfos != null)
			o2 = m_bookInfos.get(b);
		if ((o == null) && (o2 == null))
			return null;
		else {
			String ret = "";
			if (o != null) {
				ret += (String)o;
				if (o2 != null)
					ret += lineSeparator;
			}
			if (o2 != null)
				ret += o2;
			return ret;
		}
	}

	/**
	 * Returns the content of field b, as a collection, each line is an element
	 * of the collection
	 * 
	 * @param b
	 * @return
	 */
	public Collection<String> getAsCollection(byte b) {
		String[] lines = getAsStringArray(b);
		if (lines == null)
			return new ArrayList<String>();
		else
			return arrayToCollection(lines);
	}
	
	/**
	 * Returns the content of field b, as a string array, each
	 * line is an element in the array
	 */
	public String[] getAsStringArray(byte b) {
		String s = get(b);
		if (s == null)
			return null;
		else
			return s.split(lineSeparator+"");
	}

	/** Is the field b filled? */
	public boolean has(byte b) {
		return get(b) != null;
	}

	private Byte key(byte b) {
		return new Byte(b);
	}

	/** Remove the whole content of field b */
	public void remove(byte b) {
		m_infos.remove(key(b));
	}

	/**
	 * Remove the line s from the field b
	 * 
	 * @param b
	 * @param s
	 */
	public void remove(byte b, String s) {
		Collection<String> c = getAsCollection(b);
		c.remove(s);
		set(b, c);
	}

	/**
	 * Set the whole content of field b <br>
	 * Create one line per collection element
	 * 
	 * @param b
	 * @param c
	 */
	public void set(byte b, Collection<String> c) {
		if ((c != null) && (c.size() > 0)) {
			StringBuffer sb = new StringBuffer();
			for (String s : c) {
				if (sb.length() > 0)
					sb.append(lineSeparator);
				sb.append(s);
			}
			set(b, sb.toString());
		} else
			remove(b);
	}

	/** Set the whole content of field b */
	public void set(byte b, String s) {
		if ((s != null) && (s.length() > 0))
			m_infos.put(key(b), s);
		else
			remove(b);
	}

	/**
	 * Set the whole content of field b from a string array <br>
	 * Create one line per collection element
	 * 
	 * @param b
	 * @param s
	 */
	public void set(byte b, String[] s) {
		if (s != null) {
			set(b, arrayToCollection(s));
		} else {
			remove(b);
		}
	}
	
	/** Sets the infos for TuneBook containing the Tune.
	 * <br>
	 * Set it to null when you remove the tune from the book.
	 * @param bookInfos
	 */
	protected void setBookInfos(TuneInfos bookInfos) {
		m_bookInfos = bookInfos;
	}
}

/**
 * 
 */
package abc.notation;

import java.util.Comparator;

/**
 * Compare the notes by their height
 */
public class NoteHeightComparator implements Comparator<Note> {

	public static final int ORDER_ASCENDING = 1, ORDER_DESCENDING = -1;

	private KeySignature keySig = null;

	private int order = ORDER_ASCENDING;

	/**
	 * Compare the notes by their height, in ascending order, without taking
	 * into account the key signature (if accidental is NONE).
	 */
	public NoteHeightComparator() {
		this(ORDER_ASCENDING, null);
	}

	/**
	 * Compare the notes by their height, without taking into account the key
	 * signature (if accidental is NONE).
	 * 
	 * @param order
	 *            {@link #ORDER_ASCENDING} or {@link #ORDER_DESCENDING}
	 */
	public NoteHeightComparator(int order) {
		this(order, null);
	}

	/**
	 * Compare the notes by their height, taking into account the key signature
	 * (if accidental is NONE).
	 * 
	 * @param order
	 *            {@link #ORDER_ASCENDING} or {@link #ORDER_DESCENDING}
	 * @param key
	 */
	public NoteHeightComparator(int order, KeySignature key) {
		this.order = order == ORDER_DESCENDING ? ORDER_DESCENDING
				: ORDER_ASCENDING;
		this.keySig = key;
	}

	/**
	 * Compare the notes by their height in ascending order, taking into account
	 * the key signature (if accidental is NONE).
	 * 
	 * @param key
	 */
	public NoteHeightComparator(KeySignature key) {
		this(ORDER_ASCENDING, key);
	}

	public int compare(Note n1, Note n2) {
		if (n1.isHigherThan(n2, keySig))
			return order;
		else if (n1.isLowerThan(n2, keySig))
			return -order;
		//Distinguish G# and Ab
		if (n1.getHeight() > n2.getHeight())
			return order;
		else if (n1.getHeight() < n2.getHeight())
			return -order;
		else
			return 0;
	}

}

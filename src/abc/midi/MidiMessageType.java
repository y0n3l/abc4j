package abc.midi;

/** This interface exposes the types of midi messages 
 * created by the <TT>abc.midi</TT> package */ 
public interface MidiMessageType {
	
  //public static final byte TEXT_EVENT = 0x01;
	/** The message type for an end of a tune playback. */
	public static final byte END_OF_TRACK = 0x2F;
	/** The message type for a tempo change. */ 
	public static final byte TEMPO_CHANGE = 0x51;
	/** The message type to flag which part of the abc notation
	 * is played during a tune playback.
	 * @deprecated use <TT>NOTE_INDEX_MARKER</TT>
	 * @see #NOTE_INDEX_MARKER */
	public static final byte NOTATION_MARKER = 0x30;
	/** The message type to give a reference to the note index in the
	 * score that is being played during a tune playback. */
	public static final byte NOTE_INDEX_MARKER = 0x40;
	
	public static final byte MARKER = 0x06;

}
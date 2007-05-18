package abc.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
/**
 * As the jdk 1.4 sequencer does not seem to detect all kind of meta
 * messages during sequence playback, a workaround has been introduced
 * to rely on the only type of meta message the sequencer detects : 
 * the one with type 0x06 (represented by the constant
 * <PRE>MidiMessageType.MARKER</PRE>.
 */
public class MetaMessageWA extends MetaMessage {
	byte[] m_realData = null;
	
	public MetaMessageWA(MetaMessage rootMessage) throws InvalidMidiDataException {
		m_realData = rootMessage.getData();
		byte[] newData = new byte[m_realData.length+1];
		System.arraycopy(m_realData, 0, newData, 1, m_realData.length);
		newData[0] = (byte)rootMessage.getType();
		super.setMessage(MidiMessageType.MARKER, newData, newData.length);
	}
	
	public int getType(){
		return super.getData()[0];
	}
	
	public byte[] getData(){
		return m_realData;
	}
	
	/* Sets the content of this meta message.
	 * @
	 * @see javax.sound.midi.MetaMessage#setMessage(int, byte[], int)
	 */
	/*public void setMessage(int type, byte[] data, int length) throws InvalidMidiDataException {
		byte[] newData = new byte[data.length+1];
		newData[0] = (byte)type;
		for (int i=0; i<data.length; i++)
			newData[i+1] = data[i];
		super.setMessage(MidiMessageType.MARKER, newData, newData.length);
	}*/

	/** Checks the first byte of the data part of the message to check 
	 * if it is a tempo message or not.
	 * @param message
	 * @return <TT>true</TT> if the given message is a tempo message, 
	 * <TT>false</TT> otherwise. */
	public static boolean isTempoMessage(MetaMessage message) {
		return
        	(message.getType()==MidiMessageType.MARKER &&
        			message.getData()[0]==MidiMessageType.TEMPO_CHANGE);
	}

	/** Checks the first byte of the data part of the message to check 
	 * if it is a notation marker message or not.
	 * @param message
	 * @return <TT>true</TT> if the given message is a marker notation message, 
	 * <TT>false</TT> otherwise. */
	public static boolean isNotationMarker(MetaMessage message) {
		return
        	(message.getType()==MidiMessageType.MARKER &&
        			message.getData()[0]==MidiMessageType.NOTATION_MARKER);
	}

	public static boolean isNoteIndexMessage(MetaMessage message) {
		return
        	(message.getType()==MidiMessageType.MARKER &&
        			message.getData()[0]==MidiMessageType.NOTE_INDEX_MARKER);
	}
}

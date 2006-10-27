package abc.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
/**
 * This class describes midi meta messages with an additional
 * type that is expressed as the first byte of the data...
 * Not the straight way to use meta message but the standard 
 * way does not seem to work properly (message not detected 
 * while play back)
 */
public class MetaMessageWA extends MetaMessage {
	
	/* Sets the content of this meta message.
	 * @
	 * @see javax.sound.midi.MetaMessage#setMessage(int, byte[], int)
	 */
	public void setMessage(int type, byte[] data, int length) throws InvalidMidiDataException {
		byte[] newData = new byte[data.length+1];
		newData[0] = (byte)type;
		for (int i=0; i<data.length; i++)
			newData[i+1] = data[i];
		super.setMessage(MidiMessageType.MARKER, newData, newData.length);
	}

	/** Checks the first byte of the data part of the message to check 
	 * if it is a tempo message or not.
	 * @param message
	 * @return */
	public static boolean isTempoMessage(MetaMessage message) {
		return
        	(message.getType()==MidiMessageType.MARKER &&
        			message.getData()[0]==MidiMessageType.TEMPO_CHANGE);
	}

	/** Checks the first byte of the data part of the message to check 
	 * if it is a notation marker message or not.
	 * @param message
	 * @return */
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

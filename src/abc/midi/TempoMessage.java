package abc.midi;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.InvalidMidiDataException;
import abc.notation.Tempo;
import abc.notation.Note;

/** A midi message to set tempo. */
public class TempoMessage extends MetaMessage {
	private static final int MICRO_SECOND_NB_IN_ONE_MINUTE = 60 * 1000000;
	
	/** Creates a midi message to change tempo to the specified tempo.
	 * @param tempo A tempo coming from an abc notation. */
	public TempoMessage(Tempo tempo) {
		super();
		int nbOfQuarterNotesPerMinute = tempo.getNotesNumberPerMinute(Note.QUARTER);
		int microsecondsPerQuarterNote = MICRO_SECOND_NB_IN_ONE_MINUTE / nbOfQuarterNotesPerMinute;
		//System.out.println("encoding " + microsecondsPerQuarterNote);
		byte[] buffer = new byte[3];
		buffer[0] = (byte)(( microsecondsPerQuarterNote & 0x00ff0000 ) >> 16 );
		buffer[1] = (byte)(( microsecondsPerQuarterNote & 0x0000ff00 ) >> 8 );
		buffer[2] = (byte)( microsecondsPerQuarterNote &  0x000000ff );
		try	{
			setMessage(MidiMessageType.TEMPO_CHANGE ,buffer, buffer.length);
		}
		catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
	public static Tempo getTempo(byte[] bytes) {
		Tempo tempo = null;
		try {
			//System.out.println("converting" + bytes.length);
			int a = ((int)bytes[0]&0xFF)<<16;
			int b = ((int)bytes[1]&0xFF)<<8;
			int c = ((int)bytes[2]&0xFF);
			int microsecondsPerQuarterNote = a+b+c;
			int nbOfQuarterNotesPerMinute = MICRO_SECOND_NB_IN_ONE_MINUTE / microsecondsPerQuarterNote;
			tempo = new Tempo(nbOfQuarterNotesPerMinute);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return tempo;
	}
}

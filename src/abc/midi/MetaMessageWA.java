package abc.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;

public class MetaMessageWA extends MetaMessage
{
  public void setMessage(int type, byte[] data, int length) throws InvalidMidiDataException
  {
    byte[] newData = new byte[data.length+1];
    newData[0] = (byte)type;
    for (int i=0; i<data.length; i++)
      newData[i+1] = data[i];
    super.setMessage(MidiMessageType.MARKER, newData, newData.length);
  }

  public static boolean isTempoMessage(MetaMessage message)
  {
    return
        (message.getType()==MidiMessageType.MARKER &&
         message.getData()[0]==MidiMessageType.TEMPO_CHANGE);
  }

  public static boolean isNotationMarker(MetaMessage message)
  {
    return
        (message.getType()==MidiMessageType.MARKER &&
         message.getData()[0]==MidiMessageType.NOTATION_MARKER);
  }

  public static boolean isNoteIndexMessage(MetaMessage message)
  {
    return
        (message.getType()==MidiMessageType.MARKER &&
         message.getData()[0]==MidiMessageType.NOTE_INDEX_MARKER);
  }
}

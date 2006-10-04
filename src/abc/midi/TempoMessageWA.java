package abc.midi;

import javax.sound.midi.InvalidMidiDataException;

public class TempoMessageWA extends MetaMessageWA
{
  public TempoMessageWA()
  {
    byte[] buffer = new byte[1];
    try
    { setMessage(MidiMessageType.TEMPO_CHANGE ,buffer, buffer.length); }
    catch (InvalidMidiDataException e)
    { e.printStackTrace(); }
  }
}

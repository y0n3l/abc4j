package abc.midi;

import javax.sound.midi.InvalidMidiDataException;

public class NoteIndexMessage extends MetaMessageWA
{
  //private PositionableInCharStream m_pos = null;

  public NoteIndexMessage(int indexInScore)
  {
    //System.out.println("new NoteIndexMessage(" + indexInScore +")");
    //FF 7F <len> <id> <data>  Sequencer-Specific Meta-event
    //m_pos = pos;
    byte[] buffer = new byte[3];

    buffer[0] = (byte)(( indexInScore & 0x00ff0000 ) >> 16 );
    buffer[1] = (byte)(( indexInScore & 0x0000ff00 ) >> 8 );
    buffer[2] = (byte)( indexInScore &  0x000000ff );

    try
    { setMessage(MidiMessageType.NOTE_INDEX_MARKER ,buffer, buffer.length); }
    catch (InvalidMidiDataException e)
    { e.printStackTrace(); }
  }

  public static int getIndex(byte[] bytes)
  {
    int a = ((int)bytes[1]&0xFF)<<16;
    int b = ((int)bytes[2]&0xFF)<<8;
    int c = ((int)bytes[3]&0xFF);
    if (a+b+c<0)
      System.out.println("ca va péter !");
    return a+b+c;

  }
}

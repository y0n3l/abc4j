package abc.midi;

import javax.sound.midi.InvalidMidiDataException;

import scanner.PositionableInCharStream;

/** A midi meta event to flag a positionable object in a midi stream. */
public class NotationMarkerMessage extends MetaMessageWA
{
  //private PositionableInCharStream m_pos = null;

  public NotationMarkerMessage(PositionableInCharStream pos)
  {
    //FF 7F <len> <id> <data>  Sequencer-Specific Meta-event
    //m_pos = pos;
    int offsetBegin = pos.getPosition().getCharactersOffset();
    //int offsetEnd = pos.getEndPosition().getCharactersOffset()-1;
    // replaced by when migrating ot getSize() in positionableInStream:
    int offsetEnd = offsetBegin+pos.getLength()-1;


    byte[] buffer = new byte[6];

    buffer[0] = (byte)(( offsetBegin & 0x00ff0000 ) >> 16 );
    buffer[1] = (byte)(( offsetBegin & 0x0000ff00 ) >> 8 );
    buffer[2] = (byte)( offsetBegin &  0x000000ff );

    buffer[3] = (byte)(( offsetEnd & 0x00ff0000 ) >> 16 );
    buffer[4] = (byte)(( offsetEnd & 0x0000ff00 ) >> 8 );
    buffer[5] = (byte)( offsetEnd &  0x000000ff );
    try
    { setMessage(MidiMessageType.NOTATION_MARKER ,buffer, buffer.length); }
    catch (InvalidMidiDataException e)
    { e.printStackTrace(); }
  }

  public static int getBeginOffset(byte[] bytes)
  {
    int a = ((int)bytes[1]&0xFF)<<16;
    int b = ((int)bytes[2]&0xFF)<<8;
    int c = ((int)bytes[3]&0xFF);
    if (a+b+c<0)
      System.out.println("ca va péter !");
    return a+b+c;

  }

  public static int getEndOffset(byte[] bytes)
  {
    int a = ((int)bytes[4]&0xFF)<<16;
    int b = ((int)bytes[5]&0xFF)<<8;
    int c = ((int)bytes[6]&0xFF);
    return a+b+c;
  }
}

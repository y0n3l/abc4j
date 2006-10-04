package abc.midi;

public interface MidiMessageType
{
  public static final byte TEXT_EVENT = 0x01;

  public static final byte END_OF_TRACK = 0x2F;

  public static final byte TEMPO_CHANGE = 0x51;

  public static final byte NOTATION_MARKER = 0x30;

  public static final byte NOTE_INDEX_MARKER = 0x40;

  public static final byte MARKER = 0x06;

  //public static final int TEMPO_CHANGE = 0x51;

}
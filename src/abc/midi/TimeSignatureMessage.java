package abc.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import abc.notation.TimeSignature;

/** A midi message to set time signature DOES NOT WORK !!!!. */
public class TimeSignatureMessage extends MetaMessage
{
  /** Creates a midi message to change time signature from an ABC time
   * signature.
   * @param meter  */
  public TimeSignatureMessage(TimeSignature meter)
  {
    super();
    byte nn = (byte)meter.getNumerator();
    byte dd = (byte)(Math.log(meter.getDenominator()) / Math.log(2));
    byte midiClocksPerMetronomeTick = 50;
    byte thirySecondNotesPer24MidiClocks = 50;

    byte[] buffer = new byte[4];
    buffer[0] = nn;
    buffer[1] = dd;
    buffer[2] = midiClocksPerMetronomeTick;
    buffer[3] = thirySecondNotesPer24MidiClocks;
    try
    { setMessage(0x58 ,buffer, 4); }
    catch (InvalidMidiDataException e)
    { e.printStackTrace(); }
/*    FF 58 04 nn dd cc bb  Time Signature
 Time signature of the form:
nn/2^dd
eg: 6/8 would be specified using nn=6, dd=3
The parameter cc is the number of MIDI Clocks per metronome tick.

Normally, there are 24 MIDI Clocks per quarter note. However, some software allows this to be set by the user. The parameter bb defines this in terms of the number of 1/32 notes which make up the usual 24 MIDI Clocks (the 'standard' quarter note).

 nn  Time signature, numerator
 dd  Time signature, denominator expressed as a power of 2.
eg a denominator of 4 is expressed as dd=2
 cc  MIDI Clocks per metronome tick
 bb  Number of 1/32 notes per 24 MIDI clocks (8 is standard)  */

  }
}

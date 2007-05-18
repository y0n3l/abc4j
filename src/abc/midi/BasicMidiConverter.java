package abc.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.Tempo;

/** A basic midi converter that just plays melody, ignores ornaments and chords. */
public class BasicMidiConverter extends MidiConverterAbstract
{
  public MidiMessage[] getNoteOneMessageFor(Note note, KeySignature key) throws InvalidMidiDataException
  {
    MidiMessage[] events = new MidiMessage[1];
    ShortMessage myNoteOn = new ShortMessage();
    myNoteOn.setMessage(ShortMessage.NOTE_ON, getMidiNoteNumber(note, key), 50);
    events[0] = myNoteOn;
    return events;
  }

  public MidiMessage[] getNoteOffMessageFor(Note note, KeySignature key) throws InvalidMidiDataException
  {
    ShortMessage[] events = new ShortMessage[1];
    ShortMessage myNoteOff = new ShortMessage();
    myNoteOff.setMessage(ShortMessage.NOTE_OFF , getMidiNoteNumber(note, key), 50);
    events[0]=myNoteOff;
    return events;
  }

  public MidiMessage[] getMidiMessagesFor(Tempo tempo) throws InvalidMidiDataException {
	  TempoMessage tempoM = new TempoMessage(tempo);
	  MidiMessage[] messages = {tempoM, new MetaMessageWA(tempoM)};
	  return messages;
  }

}

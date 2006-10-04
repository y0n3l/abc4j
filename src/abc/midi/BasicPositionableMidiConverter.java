package abc.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;
import javax.sound.midi.MidiEvent;
import scanner.PositionableInCharStream;
import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.MultiNote;

/** A basic midi converter that just plays melody, ignores ornaments and chords. */
public class BasicPositionableMidiConverter extends BasicMidiConverter
{
  protected void playNote(Note note, int indexInScore, KeySignature currentKey, long reference, long duration, Track track) throws InvalidMidiDataException
  {
    //System.out.println("play note " + note);
    //MidiEvent[] array = {new MidiEvent(new NotationMarkerMessage((PositionableInCharStream)note), reference)};
    MidiEvent[] array = {new MidiEvent(new NoteIndexMessage(indexInScore), reference)};
    addEventsToTrack(track, array);
    super.playNote(note, indexInScore,  currentKey, reference, duration, track);
  }

  protected void playMultiNote(MultiNote multiNote, int indexInScore, KeySignature currentKey, long reference, Track track) throws InvalidMidiDataException
  {
    //System.out.println("play multiNote " + multiNote);
    //MidiEvent[] array = {new MidiEvent(new NotationMarkerMessage((PositionableInCharStream)multiNote), reference)};
    MidiEvent[] array = {new MidiEvent(new NoteIndexMessage(indexInScore), reference)};
    addEventsToTrack(track, array);
    super.playMultiNote(multiNote, indexInScore, currentKey, reference, track);

  }

}

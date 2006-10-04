package abc.midi;

import java.util.Vector;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import abc.notation.AccidentalType;
import abc.notation.BarLine;
import abc.notation.KeySignature;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.RepeatBarLine;
import abc.notation.Tempo;
import abc.notation.Tune;
import abc.notation.Tuplet;
import abc.parser.PositionableMultiNote;
import abc.parser.PositionableNote;

/** MidiConverter class defines various static methods to convert abc related stuff
 * to midi : notes, tunes etc... */
public abstract class OldMidiConverterAbstract implements MidiConverterInterface
{
  /** The resolution of the sequence : this will correspond to a quarter note. */
  private static final int SEQUENCE_RESOLUTION = Note.QUARTER;

  /** Converts the given tune to a midi sequence.
   * @param tune The tune to be converted.
   * @return The midi sequence of the tune. */
  public Sequence toMidiSequence(Tune tune)
  {
    Sequence sequence = null;
    try
    {
      //Tune.AbcScore score = tune.getAbcScore();
      // Sequence in ticks per quarter note.
      sequence = new Sequence (Sequence.PPQ, SEQUENCE_RESOLUTION, 1);
      Track track = sequence.createTrack();
      long trackLengthInTicks = track.ticks();
      int lastRepeatOpen = -1;
      int repeatNumber = 1;
      boolean inWrongEnding = false;
      int i = 0;// StaffItem iterator
      KeySignature tuneKey = null;
      KeySignature currentKey = null;

      long elapsedTime = 0;
      Tune.Score staff = tune.getScore();
      while (i < staff.size())
      {
        if (!inWrongEnding)
        {
          //==================================================================== TEMPO
          if (staff.elementAt(i) instanceof abc.notation.Tempo)
            trackLengthInTicks = addEventsToTrack(track, getMidiEventsFor((Tempo)staff.elementAt(i), trackLengthInTicks));
          else
          //==================================================================== KEY SIGNATURE
          if (staff.elementAt(i) instanceof abc.notation.KeySignature)
          {
            tuneKey = (KeySignature)(staff.elementAt(i));
            currentKey = new KeySignature(tuneKey.getAccidentals());
          }
          else
          //==================================================================== TUPLET
          if (staff.elementAt(i) instanceof abc.notation.Tuplet)
          {
            Tuplet tuplet = (Tuplet)staff.elementAt(i);
            trackLengthInTicks = addEventsToTrack(track, getMidiEventsFor(tuplet, currentKey, trackLengthInTicks));
            Vector notes = tuplet.getNotesAsVector();
            for (int j=0; j<notes.size(); j++)
              updateKey(currentKey, (Note)notes.elementAt(j));
          }
          else
          //==================================================================== NOTE
          if (staff.elementAt(i) instanceof abc.notation.Note)
          {
            PositionableNote note = (PositionableNote)staff.elementAt(i);
            if (!note.isRest())
            {
              MidiEvent[] array = {new MidiEvent(new NoteIndexMessage(i), trackLengthInTicks)};
              addEventsToTrack(track, array);
              trackLengthInTicks = addEventsToTrack(track, getMidiEventsFor(note, currentKey, trackLengthInTicks));
              updateKey(currentKey, note);
            }
            else
              trackLengthInTicks+=getNoteLengthInTicks(note);
          }
          else
          //==================================================================== MULTI NOTE
          if ((staff.elementAt(i) instanceof abc.notation.MultiNote))
          {
            PositionableMultiNote multiNote = (PositionableMultiNote)staff.elementAt(i);
            trackLengthInTicks = addEventsToTrack(track, getMidiEventsFor(multiNote, currentKey, trackLengthInTicks));
            Vector notes = multiNote.getNotesAsVector();
            for (int j=0; j<notes.size(); j++)
              updateKey(currentKey, (Note)notes.elementAt(j));
          }
        }
        //====================================================================== REPEAT BAR LINE
        if (staff.elementAt(i) instanceof abc.notation.RepeatBarLine)
        {
          RepeatBarLine bar = (RepeatBarLine)staff.elementAt(i);
          if (repeatNumber<bar.getRepeatNumber() && lastRepeatOpen!=-1)
          {
            repeatNumber++;
            i=lastRepeatOpen;
          }
          else
          if (repeatNumber>bar.getRepeatNumber())
            inWrongEnding = true;
          else
            inWrongEnding = false;
        }
        else
        //====================================================================== BAR LINE
        if (staff.elementAt(i) instanceof abc.notation.BarLine)
        {
          currentKey = new KeySignature(tuneKey.getAccidentals());
          switch ( ((BarLine)(staff.elementAt(i))).getType())
          {
              case BarLine.SIMPLE : break;
              case BarLine.REPEAT_OPEN : lastRepeatOpen=i; repeatNumber=1; break;
              case BarLine.REPEAT_CLOSE :
                if (repeatNumber<2 && lastRepeatOpen!=-1)
                { repeatNumber++; i=lastRepeatOpen; }
                else
                {repeatNumber=1; lastRepeatOpen=-1; }
                  break;
          }
        }
        i++;
      }
    }
    catch (InvalidMidiDataException e)
    {
      e.printStackTrace();
    }
    return sequence;
  }

  private void updateKey(KeySignature key, Note note)
  {
    if (note.getAccidental()!=AccidentalType.NONE)
      key.setAccidental(note.toRootOctaveHeigth(), note.getAccidental());
  }

  /**
   * @return The length of the track in ticks, once events have been added to it.
   */
  private long addEventsToTrack(Track track, MidiEvent[] events)
  {
    if (events!=null)
      for (int i=0; i<events.length; i++)
        track.add(events[i]);
    return track.ticks();
  }

  /** Returns the corresponding midi events for a note. */
  public abstract MidiEvent[] getMidiEventsFor(Note note, KeySignature key, long lastPosInTicks) throws InvalidMidiDataException;

  /** Returns the corresponding midi events for a tuplet. */
  public abstract MidiEvent[] getMidiEventsFor(Tuplet tuplet, KeySignature key, long lastPosInTicks) throws InvalidMidiDataException;

  /** Returns the corresponding midi events for a tempo change. */
  public abstract MidiEvent[] getMidiEventsFor(Tempo tempo, long lastPosInTicks) throws InvalidMidiDataException;

  /** Returns the corresponding midi events for a multi note. */
  public abstract MidiEvent[] getMidiEventsFor(MultiNote notes, KeySignature key, long lastPosInTicks) throws InvalidMidiDataException;

  /** Returns the absolute note length of a note, thanks to the sequence
   * resolution and the default note length. */
  protected long getNoteLengthInTicks(Note note)//, DefaultNoteLength defaultNoteLength)
  {
    short noteLength = note.getDuration();//defaultNoteLength.getDefaultNoteLength());
    float numberOfQuarterNotesInThisNote = (float)noteLength / Note.QUARTER;
    float lengthInTicks = (float)SEQUENCE_RESOLUTION * numberOfQuarterNotesInThisNote;
    return (long)lengthInTicks;
  }

  /** Returns the length of the multi note in ticks.
   * This length is calculated from the resolution of the midi sequence
   * manipulated internally.
   * @return The length of the multi note in ticks : this is equal to the length
   * of the longest note of the multi note. */
  protected long getNoteLengthInTicks(MultiNote note)//, DefaultNoteLength defaultNoteLength)
  {
    short longestLength = note.getLongestNote().getDuration();//defaultNoteLength.getDefaultNoteLength());
    float numberOfQuarterNotesInThisNote =  (float)longestLength / Note.QUARTER;
    float lengthInTicks = (float)SEQUENCE_RESOLUTION * numberOfQuarterNotesInThisNote;
    return (long)lengthInTicks;
  }

  /** Returns the midi note number corresponding a note in the given key.
   * @param note The note.
   * @param key The key.
   * @return The midi heigth of the note in the given key. */
  protected byte getMidiNoteNumber (Note note, KeySignature key)
  {
    byte heigth = note.getHeigth();
    byte accidental = note.getAccidental();
    byte midiNoteNumber = (byte)(heigth+(69-Note.A));
    midiNoteNumber = (byte)(midiNoteNumber + note.getOctaveTransposition()*12);
    if (accidental == AccidentalType.NONE)
    {
      byte absoluteAccidental = AccidentalType.NATURAL;
      byte heightOnOneOctave = (byte)(heigth % 12);
      absoluteAccidental = key.getAccidentalFor(heightOnOneOctave);
      switch (absoluteAccidental)
      {
          case AccidentalType.FLAT :
            midiNoteNumber --; break;
          case AccidentalType.NATURAL	:
            break;
          case AccidentalType.SHARP :
            midiNoteNumber ++; break;
      }
    }
    else
    {
      switch (accidental)
      {
          case AccidentalType.FLAT : midiNoteNumber --; break;
          case AccidentalType.NATURAL	: break;
          case AccidentalType.SHARP : midiNoteNumber ++; break;
      }
    }
    return midiNoteNumber;
  }
}


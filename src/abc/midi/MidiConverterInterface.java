package abc.midi;

import javax.sound.midi.Sequence;

import abc.notation.Tune;

/** This interface defines methods that should be implemented for any "tune to
 * midi" converter.
 * Improved converters could generate midi sequences depending on the type of tune
 * rhythm, have nice ornaments features etc etc... */
public interface MidiConverterInterface
{
  /** Returns the midi sequence corresponding to the given tune.
   * @param tune A tune with a score.
   * @return A midi sequence corresponding to the given tune. */
  public Sequence toMidiSequence(Tune tune);
}


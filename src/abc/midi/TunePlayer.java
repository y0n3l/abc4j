package abc.midi;

import java.util.Vector;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import abc.notation.NoteAbstract;
import abc.notation.Tune;

/** TunePlayer objects enable you to play tunes using default MIDI sequencer. */
public class TunePlayer implements MetaEventListener
{
  //private static final int SEQUENCE_RESOLUTION = 96;
  //private static final boolean DEBUG = true;
  private Tune m_tune = null;
  private Sequencer seq = null;
  //private Synthesizer synth = null;
  private boolean m_isStarted = false;
  private Vector m_listeners = null;
  private int m_tempo = 180;
  private MidiConverterInterface m_converter = null;

  //private static final int VOLUME_CONTROLLER_NUMBER = 7;

  /** Constructs a tune player with default midi converter.
   * @see BasicMidiConverter */
  public TunePlayer()
  {
    m_converter = new BasicPositionableMidiConverter();
    m_listeners = new Vector();
  }

  /** Constructs a tune player with the specified midi converter. */
  public TunePlayer(MidiConverterInterface converter)
  {
    m_converter = converter;
    m_listeners = new Vector();
  }

  /** Adds a listener to this tune player.
   * @param listener The listener to be added to this tune player. */
  public void addListener(TunePlayerListenerInterface listener)
  { m_listeners.addElement(listener); }

  /** Removes a listener from this tune player.
   * @param listener The listener to be removed from this tune player. */
  public void removeListener(TunePlayerListenerInterface listener)
  { m_listeners.removeElement(listener); }

  /** Sets the tempo used to play the tune. N.B: This is real time tempo change :
   * the tempo will change even if a tune is playing.
   * @param tempo the tempo used to play the tune. */
  public void setTempo(int tempo)
  {
    m_tempo = tempo;
    seq.setTempoInBPM(m_tempo);
    notifyForTempoChange(tempo);
  }

  /** Returns the tempo currently used to play tunes.
   * @return The tempo currently used to play tunes. */
  public int getTempo()
  { return m_tempo; }

/*  public int getVolume()
  {
    try
    {
      MidiChannel[] channels =((Synthesizer)seq).getChannels();
      for (int i=0; i<channels.length; i++)
        System.out.println(channels[i].getController(VOLUME_CONTROLLER_NUMBER));
      return ((Synthesizer)seq).getChannels()[0].getController(VOLUME_CONTROLLER_NUMBER);
    }
    catch (Exception e)
    { e.printStackTrace();
      return -1;
    }
  }

  public void setVolume(int volume)
  {
    try
    {
      MidiChannel[] channels =((Synthesizer)seq).getChannels();
      for (int i=0; i<channels.length; i++)
        channels[i].controlChange(VOLUME_CONTROLLER_NUMBER, volume);
    }
    catch (Exception e)
    { e.printStackTrace();
    }
  }*/


  /** The tune that is currently played, */
  public Tune getTune()
  { return m_tune; }

  /** Plays the given tune.
   * @param tune The tune to be played.
   * @exception IllegalStateException Thrown if the player hasn't been started
   * before. */
  public void play(Tune tune) throws IllegalStateException
  {
    if (m_isStarted)
    {
      try
      {
        m_tune = tune;
        Sequence sequence = m_converter.toMidiSequence(m_tune);
        seq.setSequence(sequence);
        seq.setTempoInBPM(m_tempo);
        seq.start();
      }
      catch (Exception e)
      { e.printStackTrace(); }
    }
    else
      throw new IllegalStateException("The player hasn't been started yet !");
  }

  /** Returns <TT>true</TT> if this player is currently playing a tune.
   * @return <TT>true</TT> if this player is currently playing a tune, <TT>false</TT>
   * otheriwse. */
  public boolean isPlaying()
  {
    if (seq==null)
      return false;
    else
      return seq.isRunning();
  }

//
//  public void controlChange(ShortMessage event)
//  { System.out.println("control  : "  + event); }

  //========================================================================
  //======================= Meta events  Listener =========================
  //========================================================================
  public void meta(MetaMessage meta)
  {
    if (MetaMessageWA.isTempoMessage(meta))
    {
      //System.out.println("TEMPO CHANGE DETECTED");
      notifyForTempoChange((int)seq.getTempoInBPM());
    }
    else
    if (MetaMessageWA.isNoteIndexMessage(meta))
    {
      notifyNotePlayedChanged((NoteAbstract)m_tune.getScore().elementAt(NoteIndexMessage.getIndex(meta.getData())));
      //System.out.println("Note Index detected : " + NoteIndexMessage.getIndex(meta.getData()));
      //notifyForTempoChange((int)seq.getTempoInBPM());
    }
    else
    if (MetaMessageWA.isNotationMarker(meta))
    {
      //System.out.println("NOTATION MARKER");
      int begin = NotationMarkerMessage.getBeginOffset(meta.getData());
      int end = NotationMarkerMessage.getEndOffset(meta.getData());
      notifyForPartPlayedChanged (begin, end);
    }
    else
    if (meta.getType()==MidiMessageType.END_OF_TRACK)
    { notifyForPlayEnd(); }
  }

  /** Starts the player so that it can play tunes.
   *  (Retrieves the default sequencer during this phasis). */
  public void start()
  {
    try
    {
      seq = MidiSystem.getSequencer();
      seq.open();
      seq.addMetaEventListener(this);
      //int[] val = {81};
      //seq.addControllerEventListener(this, val);
      m_isStarted = true;
    }
    catch (IllegalStateException e)
    { e.printStackTrace(); }
    catch (Exception e)
    { e.printStackTrace(); }
  }

  /** Stops the playing of the current tune if any. */
  public void stopPlaying()
  { seq.stop(); }


  /** Stops this player. */
  public void stop()
  {
    seq.stop();
    seq.close();
    m_isStarted = false;
  }

  protected void notifyForTempoChange(int newTempoValue)
  {
    TempoChangeEvent event = new TempoChangeEvent(this, newTempoValue);
    for (int i=0; i<m_listeners.size(); i++)
      ((TunePlayerListenerInterface)m_listeners.elementAt(i)).tempoChanged(event);
  }

  protected void notifyForPartPlayedChanged (int begin, int end)
  {
    for (int i=0; i<m_listeners.size(); i++)
      ((TunePlayerListenerInterface)m_listeners.elementAt(i)).partPlayed(begin, end);
  }

  protected void notifyNotePlayedChanged (NoteAbstract note)
  {
    for (int i=0; i<m_listeners.size(); i++)
      ((TunePlayerListenerInterface)m_listeners.elementAt(i)).notePlayed(note);
  }

  protected void notifyForPlayBegin()
  {
    PlayerStateChangeEvent e = new PlayerStateChangeEvent(this, true);
    for (int i=0; i<m_listeners.size(); i++)
      ((TunePlayerListenerInterface)m_listeners.elementAt(i)).playBegin(e);
  }

  protected void notifyForPlayEnd()
  {
    PlayerStateChangeEvent e = new PlayerStateChangeEvent(this, false);
    for (int i=0; i<m_listeners.size(); i++)
      ((TunePlayerListenerInterface)m_listeners.elementAt(i)).playEnd(e);
  }
}

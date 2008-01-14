package abc.notation;

/** <TT>Part</TT> objects are used to define parts in tunes. */
public class Part implements Cloneable {
  private char m_label;
  //private Tune m_tune = null;
  private Tune.Music m_music = null;

  Part (Tune tune, char labelValue) {
    //m_tune = tune;
    m_label = labelValue;
    m_music = tune.createMusic();
  }
  
  Part (Part root) {
	  m_label = root.m_label;
	  m_music = (Tune.Music)root.m_music.clone();
  }

  /** Sets the label that identifies this part.
   * @param labelValue The label that identifies this part. */
  public void setLabel(char labelValue)
  { m_label = labelValue; }

  /** Returns the label that identifies this part.
   * @return The label that identifies this part. */
  public char getLabel()
  { return m_label; }

  /** Returns the music to this part.
   * @return The music associated to this part. */
  public Tune.Music getMusic()
  {return m_music;}

  void setMusic(Tune.Music score)
  {m_music = score;}
  	
  	public Object clone(){
  		return new Part(this);
  	}
}

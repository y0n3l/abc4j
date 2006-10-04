package abc.midi;

import javax.sound.midi.MetaMessage;
import abc.notation.KeySignature;

/** A midi message to set key signature DOES NOT WORK !!!. */
public class KeySignatureMessage extends MetaMessage
{
  /** Creates a midi message to change key from an ABC key.
   * @param key */
  public KeySignatureMessage(KeySignature key)
  {
    super();

/*    FF 59 02 sf mi  Key Signature
 Key Signature, expressed as the number of sharps or flats, and a major/minor flag.
0 represents a key of C, negative numbers represent 'flats', while positive numbers represent 'sharps'.

 sf  number of sharps or flats
-7 = 7 flats
0 = key of C
+7 = 7 sharps
 mi  0 = major key
1 = minor key
  */

  }
}

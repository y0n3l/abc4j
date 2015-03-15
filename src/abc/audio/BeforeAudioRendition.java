// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
package abc.audio;

import java.util.Hashtable;
import java.util.Iterator;

import abc.notation.KeySignature;
import abc.notation.Music;
import abc.notation.MusicElement;
import abc.notation.PartLabel;
import abc.notation.Voice;

/**
 * Utility class to prepare a {@link abc.notation.Tune.Music} object
 * to be rendered in audio output (MIDI or other)
 */
public class BeforeAudioRendition {

	public static Music transformAll(Music source) {
		Music dest = correctPartsKeys(source);
		//dest = transformDecorations(dest);
		dest = applyAccidentals(dest);
		dest = applyDynamics(dest);
		dest = generateBassAndChords(dest);
		dest = transformRepeatsAndBreaks(dest);
		return dest;
	}
	
	/**
	 * Be sure each part has it's proper key signature.
	 * 
	 * Imagine... you have
	 * <code>K:Bb
	 * P:A
	 * ...
	 * P:B
	 * ...
	 * P:C
	 * K:G
	 * ...</code>
	 * in a <tt>ABCA</tt> structure, the last part A will have
	 * the G key of part C.
	 * 
	 * This method corrects this, the key was Bb before the
	 * first part A, so before the second part A we add a Bb
	 * key.
	 * @param music
	 * @return
	 */
	public static Music correctPartsKeys(Music music) {
		//we may have key/clef changes
		//repeat operation for each voice. Key is for all voices
		//but clef can change from one voice to another
		for (Voice voice : music.getVoices()) {
			if (voice.hasObject(KeySignature.class)
					&& voice.hasObject(PartLabel.class)) {
				Hashtable<String, KeySignature> partsKey = new Hashtable<String, KeySignature>();
				KeySignature tuneKey = null;
				int size = voice.size();
				int i = 0;
				while (i < size) {
					MusicElement me = (MusicElement) voice.elementAt(i);
					if (me instanceof KeySignature) {
						tuneKey = (KeySignature) me;
					} else if (me instanceof PartLabel) {
						PartLabel pl = (PartLabel) me;
						if ((tuneKey != null) && (partsKey.get(pl.getLabel()+"") == null)) {
							//first time we see this part, store the key
							partsKey.put(pl.getLabel()+"", tuneKey);
						} else {//not the first time we see this part
							//add the key
							tuneKey = (KeySignature) partsKey.get(pl.getLabel() + "");
							if (i < (size - 1)) {
								if (!(voice.elementAt(i+1) instanceof KeySignature)) {
									//if next element is a key, no need to insert one
									//just before, next while step it'll be defined as
									//tuneKey
									voice.insertElementAt(tuneKey, i+1);
									i++;
									size++;
								}
							}
						}
					}
					i++;
				}//end for each element of voice
			}//end voice has key(s) and part(s)
		}//end for each voice of music
		//else nothing to do
		return music;
	}
	
	/**
	 * Propagate key accidentals to notes. And non-key
	 * accidental to the same note of a bar.
	 * 
	 * TODO use the rule ??? to apply or not to octaves
	 * @param music
	 * @return
	 */
	public static Music applyAccidentals(Music music) {
		return music;
	}
	
	/**
	 * Transform tills, mordants, grupetto, arpeggio, staccato
	 * into multiple notes (and rests).
	 * 
	 * e.g. +trill+C2 will be transformed into DCDC
	 * @param source
	 * @return the Music object which contents has been changed
	 */
	public static Music transformDecorations(Music music) {
		Music ret = new Music();
		KeySignature tuneKey = null;
		KeySignature currentKey = null;
		Hashtable partsKey = new Hashtable();
		for (Voice voice : music.getVoices()) {
			for (MusicElement element : voice) {
				//TODO
			}
		}
		return music;
	}
	
	/**
	 * Transforms <code>|: ... :|</code> into <code>2 * (...)</code>,
	 * repeat bars.
	 * @param music
	 * @return
	 */
	public static Music transformRepeatsAndBreaks(Music music) {
		return music;
	}
	
	/**
	 * Apply dynamics by modifying notes' velocities by a
	 * percentage, e.g. +ff+ apply 120%.
	 * @param music
	 * @return
	 */
	public static Music applyDynamics(Music music) {
		return music;
	}
	
	/**
	 * If <code>%%MIDI gchord ...</code> instruction(s) is(are)
	 * declared, create new voices for bass and chord
	 * accompaniments
	 * @param music
	 * @return
	 */
	public static Music generateBassAndChords(Music music) {
		return music;
	}
}

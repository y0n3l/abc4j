# Introduction #
The purpose of abc4j is to ease the the manipulation and the extract of information from
abc tunes in general and abc files in particular.


# Getting information from an abc file #
Let's consider the following abc file `tutorial.abc`:
```
X:10
T:Simple scale exercise
M:4/4
C:noOne
K:C
|| C,D,E,F, | G,A,B,C | DEFG | ABcd | efga | bc'd'e' | f'g'a'b' ||

X:11
T:A multiple parts tune
M:4/4
C:anyone
P:(AB)2CCC
K:C
P:A
aaaa
P:B
bbbb
P:C
cccc
```
There are many ways to get information from an abc file with abc4j. The simpliest one is
to create a `TuneBook` from this file. Then you get access to all the information contained
in this abc file like if it was parsed. Parsing phasis are hidden inside the `TuneBook`
object. Once you get the instance of `TuneBook` that represents the "tutorial.abc" file,
it's easy to know the number of tunes inside the `TuneBook`, titles of tunes contained in it
or who composed the tune :
```
import java.io.File;
import abc.notation.Tune;
import abc.parser.TuneBook;

public void main(String[] arg)
{
    File abcFile = new File ("tutorial.abc");
    TuneBook book = new TuneBook(abcfile);
    int tunesNb = book.size();
    System.out.println("Nb of tunes in tutorial.abc : " + tunesNb);
    // now retrieve the tune with reference number "10"
    Tune aTune = book.getTune(10);
    // display its title
    System.out.print("Title n°10 is " + aTune.getTitles[0]);
    // and the name of its composer.
    System.out.println(" and has been composed by " + aTune.getComposer());
    .
    .
```

The execution of this main class gives :
```
Nb of tunes in tutorial.abc : 2
Title n°10 is Simple scale exercise and has been composed by noOne
```

# Getting the score from a tune #
The score (class Tune.Score) is the place where information like key, notes, barlines,
single or multiparts definition is stored. A score contains two types of elements:

  * `ScoreElementInterface` : Those elements contain only "pure" score information without
any information about how the score should be displayed (layout).
  * `ScorePresentationElementInterface` : Those elements describe the way the score should
be displayed (or printed) to the user.

Once we have a reference to a `Tune`, it's easy to retrieve its corresponding score and
navigate into it to retrieve its content (notes, barlines...). This code retrieves the
score of tune n°10 and displays all elements composing its score:
```
.
.
Tune.Score score = aTune.getScore();
Iterator iter = score.iterator();
while (iter.hasNext())
System.out.println(iter.next());
.
.
```

# How to play a tune #
Here's the code to play a tune contained in an abc file `ceili.abc` using midi:
```
import java.io.File;
import abc.notation.Tune;
import abc.parser.TuneParser;
import abc.parser.TuneBook;
import abc.midi.TunePlayer;
.
.
//creates a file from the path to the abc file containing the tune to be played
File abcFile = new File ("ceili.abc");
//creates a tunebook from the previous file
TuneBook book = new TuneBook(abcfile);
//retrieves the first tune notation of the ceili.abc file
Tune tune = book.getTune(0);
//creates a midi player to play tunes
TunePlayer player = new TunePlayer();
//starts the player and play the tune
player.start();
player.play(tune);
.
..
```

# How to convert a tune into a midi file #
The conversion of a tune object into a midi file is achieved using the abc4j midiConverter concept and some features of javaSound :
```
//The midi file result
File file = new File("test.mid");
//Create a converter to convert a tune into midi sequence
MidiConverterAbstract conv = new BasicMidiConverter();
//convert it !
Sequence s = conv.toMidiSequence(t);
//All available midi file type for the tune's sequence
int[] types = MidiSystem.getMidiFileTypes(s);
//Write the sequence as a midi file.
MidiSystem.write(s,types[0],file);
```
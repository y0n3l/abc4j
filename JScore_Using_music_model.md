#This tutorial explains how to display music scores by using the music model provided by abc4j.

# Introduction #
This tutorial explains how to display music scores by using the music model provided by abc4j.
We'll expose here, step by step, how to get the following score display using abc4j music objects abstractions:

![http://u1.ipernity.com/u/3/1D/F2/1176093.bbce890f1.l.jpg](http://u1.ipernity.com/u/3/1D/F2/1176093.bbce890f1.l.jpg)

The purpose here is to get the same result as what is achieved in http://code.google.com/p/abc4j/wiki/JScore_Using_abc_notation but using another way : use of the object oriented music model instead of the abc ascii music notation.

# Step 1 #
First, let's create a new `abc.notation.Tune` instance, and add a time signature and the notes to its music.
This can be achieved by the following code :
```
import javax.swing.JFrame;

import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.Tune;
import abc.ui.swing.JScoreComponent;

public static void main (String[] arg) {
	Tune tune = new Tune();
	KeySignature key = new KeySignature(Note.D, KeySignature.MAJOR);
	tune.getMusic().addElement(key);
	Tune.Music music = tune.getMusic();
	music.addElement(new Note(Note.C));
	music.addElement(new Note(Note.D));
	music.addElement(new Note(Note.E));
	music.addElement(new Note(Note.F));
	music.addElement(new Note(Note.G));
	music.addElement(new Note(Note.A));
	music.addElement(new Note(Note.B));
	music.addElement(new Note(Note.c));
	music.addElement(new Note(Note.d));
	music.addElement(new Note(Note.e));
	music.addElement(new Note(Note.f));
	music.addElement(new Note(Note.g));
	music.addElement(new Note(Note.g));
	music.addElement(new Note(Note.f));
	music.addElement(new Note(Note.e));
	music.addElement(new Note(Note.d));
	music.addElement(new Note(Note.c));
	music.addElement(new Note(Note.B));
	music.addElement(new Note(Note.A));
	music.addElement(new Note(Note.G));
	music.addElement(new Note(Note.F));
	music.addElement(new Note(Note.E));
	music.addElement(new Note(Note.D));
	music.addElement(new Note(Note.C));
	JScoreComponent scoreUI =new JScoreComponent();
	scoreUI.setTune(tune);
	JFrame j = new JFrame();
	j.add(scoreUI);
	j.pack();
	j.setVisible(true);
}
```

The execution of this code will display :

![http://u1.ipernity.com/u/3/1E/F2/1176094.65f4112d1.l.jpg](http://u1.ipernity.com/u/3/1E/F2/1176094.65f4112d1.l.jpg)

## Step 2 ##
Now, let's add some bar lines to the previous tune. The main becomes :
```
public static void main (String[] arg) {
	Tune tune = new Tune();
	KeySignature key = new KeySignature(Note.D, KeySignature.MAJOR);
	tune.getMusic().addElement(key);
	Tune.Music music = tune.getMusic();
	music.addElement(new Note(Note.C));
	music.addElement(new Note(Note.D));
	music.addElement(new Note(Note.E));
	music.addElement(new Note(Note.F));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.G));
	music.addElement(new Note(Note.A));
	music.addElement(new Note(Note.B));
	music.addElement(new Note(Note.c));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.d));
	music.addElement(new Note(Note.e));
	music.addElement(new Note(Note.f));
	music.addElement(new Note(Note.g));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.g));
	music.addElement(new Note(Note.f));
	music.addElement(new Note(Note.e));
	music.addElement(new Note(Note.d));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.c));
	music.addElement(new Note(Note.B));
	music.addElement(new Note(Note.A));
	music.addElement(new Note(Note.G));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.F));
	music.addElement(new Note(Note.E));
	music.addElement(new Note(Note.D));
	music.addElement(new Note(Note.C));
	JScoreComponent scoreUI =new JScoreComponent();
	scoreUI.setTune(tune);
	JFrame j = new JFrame();
	j.add(scoreUI);
	j.pack();
	j.setVisible(true);
}
```
and the resulting score is :

![http://u1.ipernity.com/u/3/4C/F2/1176140.a5391a401.l.jpg](http://u1.ipernity.com/u/3/4C/F2/1176140.a5391a401.l.jpg)

## Step 3 ##
Now, let's add the time signature 4/4 and let's group the notes two by two.
The code is :
```
public static void main (String[] arg) {
	Tune tune = new Tune();
	KeySignature key = new KeySignature(Note.D, KeySignature.MAJOR);
	tune.getMusic().addElement(key);
	Tune.Music music = tune.getMusic();
	music.addElement(TimeSignature.SIGNATURE_4_4);
	music.addElement(new Note(Note.C));
	music.addElement(new Note(Note.D));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.E));
	music.addElement(new Note(Note.F));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.G));
	music.addElement(new Note(Note.A));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.B));
	music.addElement(new Note(Note.c));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.d));
	music.addElement(new Note(Note.e));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.f));
	music.addElement(new Note(Note.g));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.g));
	music.addElement(new Note(Note.f));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.e));
	music.addElement(new Note(Note.d));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.c));
	music.addElement(new Note(Note.B));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.A));
	music.addElement(new Note(Note.G));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.F));
	music.addElement(new Note(Note.E));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.D));
	music.addElement(new Note(Note.C));
	JScoreComponent scoreUI =new JScoreComponent();
	scoreUI.setTune(tune);
	JFrame j = new JFrame();
	j.add(scoreUI);
	j.pack();
	j.setVisible(true);
}
```
and its execution gives :

![http://u1.ipernity.com/u/3/53/F2/1176147.05efbe7d1.l.jpg](http://u1.ipernity.com/u/3/53/F2/1176147.05efbe7d1.l.jpg)

## Step 4 ##
We just need to add the slurs and ties definitions now. This can be achieved by improving the code to :
```
public static void main (String[] arg) {
	Tune tune = new Tune();
	KeySignature key = new KeySignature(Note.D, KeySignature.MAJOR);
	tune.getMusic().addElement(key);
	Tune.Music music = tune.getMusic();
	music.addElement(TimeSignature.SIGNATURE_4_4);
	Note noteFirstSlurBegin = new Note(Note.C); 
	music.addElement(noteFirstSlurBegin);
	music.addElement(new Note(Note.D));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.E));
	music.addElement(new Note(Note.F));
	music.addElement(new BarLine());
	Note noteFirstSlurEnd =  new Note(Note.G);
	music.addElement(noteFirstSlurEnd);
	music.addElement(new Note(Note.A));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.B));
	music.addElement(new Note(Note.c));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.d));
	music.addElement(new Note(Note.e));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.f));
	Note noteStartingTie = new Note(Note.g); 
	music.addElement(noteStartingTie);
	music.addElement(new BarLine());
	Note noteEndingTie = new Note(Note.g);
	music.addElement(noteEndingTie);
	music.addElement(new Note(Note.f));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.e));
	music.addElement(new Note(Note.d));
	music.addElement(new BarLine());
	music.addElement(new Note(Note.c));
	music.addElement(new Note(Note.B));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.A));
	Note noteSecondSlurBegin = new Note(Note.G); 
	music.addElement(noteSecondSlurBegin);
	music.addElement(new BarLine());
	music.addElement(new Note(Note.F));
	music.addElement(new Note(Note.E));
	music.addElement(new NotesSeparator());
	music.addElement(new Note(Note.D));
	Note noteSecondSlurEnd = new Note(Note.C);
	music.addElement(noteSecondSlurEnd);
	//first slur definition
	SlurDefinition firstSlur = new SlurDefinition();
	firstSlur.setStart(noteFirstSlurBegin);
	firstSlur.setEnd(noteFirstSlurEnd);
	noteFirstSlurBegin.setSlurDefinition(firstSlur);
	noteFirstSlurEnd.setSlurDefinition(firstSlur);
	//second slur definition
	SlurDefinition secondSlur = new SlurDefinition();
	secondSlur.setStart(noteSecondSlurBegin);
	secondSlur.setEnd(noteSecondSlurEnd);
	noteSecondSlurBegin.setSlurDefinition(secondSlur);
	noteSecondSlurEnd.setSlurDefinition(secondSlur);
	//tie between the two g notes.
	TieDefinition tie = new TieDefinition();
	tie.setStart(noteStartingTie);
	tie.setEnd(noteEndingTie);
	noteStartingTie.setTieDefinition(tie);
	noteEndingTie.setTieDefinition(tie);
	JScoreComponent scoreUI =new JScoreComponent();
	scoreUI.setTune(tune);
	JFrame j = new JFrame();
	j.add(scoreUI);
	j.pack();
	j.setVisible(true);
}
```
This code, once executed, gives:

![http://u1.ipernity.com/u/3/59/F2/1176153.b2cce7621.l.jpg](http://u1.ipernity.com/u/3/59/F2/1176153.b2cce7621.l.jpg)

As described here, the music model enables you to describe music directly by instantiating objects, without the use of abc notation. However, in its current state, the model is quite verbose and needs the same information to be described several times to different abstractions. I consider this as bad points and will improve this in the coming releases... promised ! ;)
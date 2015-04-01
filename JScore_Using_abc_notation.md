# Introduction #
You will find exposed step by step, how to get the following score displayed thanks to abc4j :

![http://u1.ipernity.com/u/3/1D/F2/1176093.bbce890f1.l.jpg](http://u1.ipernity.com/u/3/1D/F2/1176093.bbce890f1.l.jpg)
# Details #
The abc notation is only one way to display scores, the other one can be achieved using the music model directly that is object oriented based instead of being ascii notation based.

## Step 1 ##
First, let's simply define the tune notes, its reference number, a title, and its key. These are the basics requiered to get a valid tune. Such tune would look like this:
```
X:0
T:A simple scale exercise
K:D
CDEFGABcdefggfedcBAGFEDC
```
To get it displayed, let's use the `abc.parser.TuneParser` class that is the most appropriated class to parse only 1 tune from its notation as a string.
The code to achieve this is :
```
import javax.swing.JFrame;

import abc.notation.Tune;
import abc.parser.TuneParser;
import abc.ui.swing.JScoreComponent;

public static void main (String[] arg) {
	String tuneAsString = "X:0\nT:A simple scale exercise\nK:D\nCDEFGABcdefggfedcBAGFEDC\n";
	Tune tune = new TuneParser().parse(tuneAsString);
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

As you may notice, the abc notation does not implicitly separate notes.

## Step 2 ##
Then we need to add the time signature and the measure bars to the tune. This will result into :
```
X:0
T:A simple scale exercise
K:D
CDEF|GABc|defg|gfed|cBAG|FEDC
```
Just replace the `tuneAsString` declaration with :
```
String tuneAsString = "X:0\nT:A simple scale exercise\nK:D\nCDEF|GABc|defg|gfed|cBAG|FEDC\n";
```
and you'll get:

![http://u1.ipernity.com/u/3/4C/F2/1176140.a5391a401.l.jpg](http://u1.ipernity.com/u/3/4C/F2/1176140.a5391a401.l.jpg)

## Step 3 ##
Now we'll set the time signature using `M:4/4` and group the notes by two using spaces. The tune now looks like :
```
X:0
T:A simple scale exercise
M:4/4
K:D
CD EF|GA Bc|de fg|gf ed|cB AG|FE DC
```
`tuneAsString` declaration is now :
```
String tuneAsString = "X:0\nT:A simple scale exercise\nM:4/4\nK:D\nCD EF|GA Bc|de fg|gf ed|cB AG|FE DC\n";
```
and you'll get:

![http://u1.ipernity.com/u/3/53/F2/1176147.05efbe7d1.l.jpg](http://u1.ipernity.com/u/3/53/F2/1176147.05efbe7d1.l.jpg)

## Step 4 ##
We just need to add the tie and the slurs to get the score fully corresponding to what we were expecting.
Let's correct the `tuneAsString` to :
```
String tuneAsString = "X:0\nT:A simple scale exercise\nM:4/4\nK:D\n(CD EF|G)A Bc|de fg-|gf ed|cB A(G|FE DC)\n";
```

The corresponding abc tune is :
```
X:0
T:A simple scale exercise
M:4/4
K:D
(CD EF|G)A Bc|de fg-|gf ed|cB A(G|FE DC)
```
and the resulting score is

![http://u1.ipernity.com/u/3/59/F2/1176153.b2cce7621.l.jpg](http://u1.ipernity.com/u/3/59/F2/1176153.b2cce7621.l.jpg)

and voilà !!
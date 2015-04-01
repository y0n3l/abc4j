# Introduction #
You will learn how to display and manipulate music scores with abc4j.

# Display the score of a tune retrieved from an abc file #
The `abc.notation.Tune` instances that are retrieved from the parsing of an abc file can be directly set to a `abc.ui.swing.JScoreComponent`. Then, you just have to use this component as a normal Swing component to get your score rendered.

ex:
```
import javax.swing.JFrame;
import abc.notation.*;
import abc.ui.swing.score.JScoreComponent;

....
public static void main (String[] arg) {
File file = new File("D:/path2UrAbcFile/myFileabc");
TuneBook tb = new TuneBook(file);
Tune tune = tb.getTune(60); //retrieve the tune to display from the tunebook using its reference number.
JScoreComponent jscore = new JScoreComponent();
jscore.setTune(tune);
scoreUI.setTune(tune);
JFrame j = new JFrame();
j.add(scoreUI);
j.pack();
j.setVisible(true);
}
```

will give :

![http://u1.ipernity.com/u/3/E3/F6/1177315.55c07b831.l.jpg](http://u1.ipernity.com/u/3/E3/F6/1177315.55c07b831.l.jpg)

## Justified alignment ##
abc4j supports music score justified alignment. You can get a nicer music score rendition by using the method `setJustification(true);` on `JScoreComponent` instances.

Just update one line in the the previous example
```
...
JScoreComponent jscore = new JScoreComponent();
jscore.setJustification(true);
jscore.setTune(tune);
...
```
to get :

![http://u1.ipernity.com/u/3/E2/F6/1177314.c5c3b7121.l.jpg](http://u1.ipernity.com/u/3/E2/F6/1177314.c5c3b7121.l.jpg)

As you can noticed, bar lines are aligned at the end of each staff line.

# Write a music score to a file #
Scores rendered using the `JScoreComponent` can also be written to disk (as a .PNG file with transparent background color).

This can be executed easily by invoking :
```
jscore.writeScoreTo(new File("D:/path2UrPngFile/myScore.png"));
```


















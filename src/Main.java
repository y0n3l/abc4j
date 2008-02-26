import java.io.File;

import javax.swing.JFrame;

import abc.notation.Tune;
import abc.parser.TuneBook;
import abc.ui.swing.JScoreComponent;

//import jm.music.data.*;
//import jm.gui.show.*;
//import jm.gui.cpn.*;


/** A simple user interface to display abc files content and play
 * tunes.  
 * Main entry point to execute some perf tests or any other 
 * operation. */
public class Main  {

  //static int PARSING_TIMES_NB = 1;
  //static int tunesNb = 0;
  /*public static void main (String[] arg) {
	  boolean endOfStreamReached = false;
	  char[] currentChar = new char[1];
	  long start  = 0;
	  long end = 0;
  	  try {
  		  
  		  
  		  File f = new File("D:/Perso/abc/LGTunes.abc");
  		  BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream (f)));
  		  
  		  int k=1;
  		  Scanner2 s = new Scanner2(r);
  		  while (k<1000) {
  		  	System.out.print(s.nextChar());
  		  	s.nextChar();
  		  	s.previousChar();
  		  }
  		  
  		  System.out.println(r);
  		  start = System.currentTimeMillis();
  		  while (!endOfStreamReached) {
  			  r.mark(1);
  			  if (r.read(currentChar)==-1)
  				  endOfStreamReached = true;
  		  }
  		  end = System.currentTimeMillis();
  		  r.close();
  		  System.out.println("Done in " + (end-start));
  		  
  		  endOfStreamReached = false;
		  r = new BufferedReader(new InputStreamReader(new FileInputStream (f)));
		  System.out.println(r);
		  String line ="";
		  start = System.currentTimeMillis();
		  while (!endOfStreamReached) {
			  line = r.readLine();
			  if (line==null)
				  endOfStreamReached = true;
			  else {
				  int i = 0;
				  while (i<line.length()) {
					  char c = line.charAt(i);
					  i++;
				  }
			  }
		  }
		  end = System.currentTimeMillis();
		  r.close();
		  System.out.println("Done in " + (end-start));
		  
		  endOfStreamReached = false;
		  r = new BufferedReader(new FileReader(f));
		  System.out.println(r);
  		  start = System.currentTimeMillis();
  		  while (!endOfStreamReached) {
  			  r.mark(1);
  			  if (r.read(currentChar)==-1)
  				  endOfStreamReached = true;
  		  }
  		  end = System.currentTimeMillis();
  		  r.close();
  		  System.out.println("Done in " + (end-start));
  		  
  		  endOfStreamReached = false;
		  r = new BufferedReader(new FileReader(f));
		  System.out.println(r);
		  line ="";
		  start = System.currentTimeMillis();
		  while (!endOfStreamReached) {
			  line = r.readLine();
			  if (line==null)
				  endOfStreamReached = true;
		  }
		  end = System.currentTimeMillis();
		  r.close();
		  System.out.println("Done in " + (end-start));
  		  
  		  
  	  }	catch (Exception e) {
  		  e.printStackTrace();
  	  }
  }*/
	
	/*public static void main (String[] arg) {
		String tuneAsString = "X:0\nT:A simple scale exercise\nM:4/4\nK:D\n(CD EF|G)A Bc|de fg-|gf ed|cB A(G|FE DC)\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		JScoreComponent scoreUI =new JScoreComponent();
		scoreUI.setTune(tune);
		JFrame j = new JFrame();
		j.add(scoreUI);
		j.pack();
		//System.out.println(sp.getSize());
		j.setVisible(true);
		}
	}*/
	
	
	public static void main (String[] arg) {
		try {
		TuneBook tb = new TuneBook(new File("D:/Perso/abc/Book4.abc"));
		Tune tune = tb.getTune(7); 
		JScoreComponent scoreUI =new JScoreComponent();
		scoreUI.setTune(tune);
		scoreUI.writeScoreTo(new File("C:/Documents and Settings/uidd0558/Desktop/kittysGoneMilkingNotjustified.jpg"));
		scoreUI.setJustification(true);
		scoreUI.writeScoreTo(new File("C:/Documents and Settings/uidd0558/Desktop/kittysGoneMilkingJustified.jpg"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public static void main (String[] arg) {
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
	}*/
	
}

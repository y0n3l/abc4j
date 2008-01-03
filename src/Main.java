import java.io.File;

import abc.notation.KeySignature;
import abc.notation.Note;
import abc.notation.TimeSignature;
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
	
	public static void main2 (String[] arg) {
		try {
		File file =new File("D:/Perso/abc/Book2.abc");
		TuneBook tb = new TuneBook(file);
		Tune tune = tb.getTune(60);
		System.out.println(tune);
		JScoreComponent jscore = new JScoreComponent();
		jscore.setTune(tune);
		jscore.setJustification(false);
		jscore.writeScoreTo(new File("C:/Documents and Settings/uidd0558/Desktop/"+ tune.getTitles()[0]+".png"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main (String[] arg) {
		Tune tune = new Tune();
		KeySignature key = new KeySignature(Note.D, KeySignature.MAJOR);
		tune.getScore().addElement(key);
		Tune.Music score = tune.getScore();
		score.addElement(TimeSignature.SIGNATURE_4_4);
		score.addElement(new Note(Note.C));
		score.addElement(new Note(Note.D));
		score.addElement(new Note(Note.E));
		score.addElement(new Note(Note.F));
		score.addElement(new Note(Note.G));
		score.addElement(new Note(Note.A));
		score.addElement(new Note(Note.B));
		score.addElement(new Note(Note.c));
		JScoreComponent scoreUI =new JScoreComponent();
		/*scoreUI.setSize(43);
		scoreUI.setTune(tune);
		JFrame j = new JFrame();
		j.setSize(500, 200);
		j.add(scoreUI);
		//System.out.println(sp.getSize());
		j.setVisible(true);*/
		try {
			scoreUI.setTune(tune);
			scoreUI.writeScoreTo(new File("C:/Documents and Settings/uidd0558/Desktop/abc4j.jpg"));
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
}

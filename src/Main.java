import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import abc.notation.Tune;
import abc.parser.TuneBook;
import abc.ui.swing.score.JScoreComponent;

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
	
	public static void main (String[] arg) {
		try {
		//MusicDisplaySample sample= new MusicDisplaySample();
		JScoreComponent sample =new JScoreComponent();
		File file =new File("D:/Perso/abc/test.abc");
		TuneBook tb = new TuneBook(file);
		Tune tune = tb.getTune(1);
		System.out.println(tune);
		sample.setTune(tune);
		JFrame j = new JFrame();
		j.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.out.println("on exit");
				System.exit(0);
			}
		});
		j.setSize(1000, 400);
		j.add(sample);
		j.setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import scanner.Scanner2;

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
  public static void main (String[] arg) {
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
  

	  
	 /*TuneEditorPane area = new TuneEditorPane(new TuneParser());
    Frame frame = new Frame();
    frame.setSize(200,100);
    frame.add(area);
    frame.setVisible(true);*/
  }
}

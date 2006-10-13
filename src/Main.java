import java.io.File;
import java.util.Vector;

import abc.notation.Tune;
import abc.parser.AbcFileParser;
import abc.parser.AbcFileParserAdapter;
import abc.parser.TuneBook;
import abc.parser.*;
import scanner.*;

//import jm.music.data.*;
//import jm.gui.show.*;
//import jm.gui.cpn.*;


/** A simple user interface to display abc files content and play
 * tunes.  
 * Main entry point to execute some perf tests or any other 
 * operation. */
public class Main //implements TunePlayerListenerInterface
{

  static int PARSING_TIMES_NB = 1;
  static int tunesNb = 0;
  public static void main (String[] arg)
  {
    // ==================================================== PERF MESUREMENTS
    AbcFileParser parser = new AbcFileParser();
    parser.addListener(new AbcFileParserAdapter()
    {
      public void validToken(TokenEvent evt)
      {
        //System.out.print(evt.getToken().getType() + " | ");
      }

      public void tuneBegin()
      {
        //System.out.println("==================================== tune begin ");
      }

      public void tuneEnd(Tune tune)
      {
        //System.out.println("==================================== tune end ");
      }

      public void lineProcessed(String line)
      {
        //System.out.print("\nline processed : " + line);
      }
    });
    try
    {
      /*System.out.println("Parsing....");
      long ref = System.currentTimeMillis();
      long intermediateRef = ref;
      System.out.println("Ref : " + ref);
      for (int i=0; i<PARSING_TIMES_NB; i++)
      {
        parser.parseFile(new File("D:/Perso/abc/reels.ABC"));
        long newRef = System.currentTimeMillis();
        System.out.println((i+1) + " time(s) in " + (newRef-intermediateRef) + " millisecs");
        intermediateRef = newRef;
      }
      long end = System.currentTimeMillis();
      System.out.println("end : " + end);
      System.out.println(tunesNb + " tunes parsed in " + (end-ref)/PARSING_TIMES_NB + " millisecs");
      Vector v = abc.parser.def.DefinitionFactory.m_allPreviouslyCreatedDefinitions;
      System.out.println("Cache size  : " + v.size());

      TuneBook tb = new TuneBook(new File("D:/Perso/abc/crash.ABC"));
      System.out.println("tunes nb " + tb.getReferenceNumbers().length);
      System.out.println(tb.getTuneNotation(3));*/
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    try
    {
    	System.out.println('\u00E0');
    	System.out.println('\340');
      TuneBook t = new TuneBook(new File("D:/Perso/abc/crash.ABC"));
      System.out.println(t.getReferenceNumbers().length);
      for (int i=0; i<t.getReferenceNumbers().length; i++) {
    	  System.out.println("=============tune X:"+ t.getReferenceNumbers()[i]);
    	  System.out.println(t.getTuneHeader(t.getReferenceNumbers()[i]));
      }
    }
    catch (Exception e)
    {e.printStackTrace(); }
    /*TuneEditorPane area = new TuneEditorPane(new TuneParser());
    Frame frame = new Frame();
    frame.setSize(200,100);
    frame.add(area);
    frame.setVisible(true);*/
  }

}

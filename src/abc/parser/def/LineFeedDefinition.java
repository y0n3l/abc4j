package abc.parser.def;

import abc.parser.AbcTokenType;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;
import scanner.IsDigitTransition;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig.
 *               \n
 *  start -----------------------> LINE_FEED
 *    |                              ^
 *    |  \r                    \n    |
 *    |-------> LINE_FEED-------------
 **/
public class LineFeedDefinition extends AutomataDefinition
{

    public LineFeedDefinition()
    {
      State state = new State(AbcTokenType.LINE_FEED, true);
      Transition trans = new Transition(state,'\n');
      getStartingState().addTransition(trans);

      State state1 = new State(AbcTokenType.LINE_FEED, true);
      trans = new Transition(state1,'\r');
      getStartingState().addTransition(trans);

      trans = new Transition(state,'\n');
      state1.addTransition(trans);
    }
}


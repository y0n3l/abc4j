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
 *               \\                 \n
 *  start -----------> UNKNOWN ------------> NO LINE BREAK
 *                       |                              ^
 *                       |  \r                    \n    |
 *                       |----> NO LINE BREAK------------
 **/
public class NoLineBreakDefinition extends AutomataDefinition
{

    public NoLineBreakDefinition()
    {
      State state = new State(AbcTokenType.UNKNOWN, false);
      getStartingState().addTransition(new Transition(state, '\\'));
      State state1 = new State(AbcTokenType.NO_LINE_BREAK, true);
      state.addTransition(new Transition(state1, '\n'));
    }
}


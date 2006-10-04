package abc.parser.def;

import abc.parser.AbcTokenType;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;
import scanner.IsDigitTransition;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class GracingEndDefinition extends AutomataDefinition
{

    public GracingEndDefinition()
    {
      State state = new State(AbcTokenType.GRACING_END, true);
      getStartingState().addTransition(new Transition(state, '}'));
    }
}


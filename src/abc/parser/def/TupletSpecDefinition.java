package abc.parser.def;

import abc.parser.AbcTokenType;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class TupletSpecDefinition extends AutomataDefinition
{

    public TupletSpecDefinition()
    {
      State state = new State(AbcTokenType.UNKNOWN, false);
      getStartingState().addTransition(new Transition(state, '('));
      State state1 = new State(AbcTokenType.TUPLET_SPEC, true);
      state.addTransition(new IsDigitTransition(state1));

      /*State state2 = new State(AbcTokenType.UNKNOWN, false);
      state1.addTransition(new Transition(state2, ':'));
      State state3 = new State(AbcTokenType.TUPLET_SPEC, true);
      state2.addTransition(new IsDigitTransition(state3));

      State state4 = new State(AbcTokenType.UNKNOWN, false);
      state3.addTransition(new Transition(state4, ':'));
      State state5 = new State(AbcTokenType.TUPLET_SPEC, true);
      state4.addTransition(new IsDigitTransition(state5));*/

    }
}


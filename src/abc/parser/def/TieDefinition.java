package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class TieDefinition extends AutomataDefinition
{

    public TieDefinition()
    {
      State state = new State(AbcTokenType.TIE, true);
      getStartingState().addTransition(new Transition(state, '-'));
    }
}


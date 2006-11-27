package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class SlurEndDefinition extends AutomataDefinition
{

    public SlurEndDefinition()
    {
      State state = new State(AbcTokenType.END_SLUR, true);
      getStartingState().addTransition(new Transition(state, ')'));
    }
}


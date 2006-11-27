package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class SlurBeginDefinition extends AutomataDefinition
{

    public SlurBeginDefinition()
    {
      State state = new State(AbcTokenType.BEGIN_SLUR, true);
      getStartingState().addTransition(new Transition(state, '('));
    }
}


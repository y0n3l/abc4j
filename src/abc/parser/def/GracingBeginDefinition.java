package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class GracingBeginDefinition extends AutomataDefinition
{

    public GracingBeginDefinition()
    {
      State state = new State(AbcTokenType.GRACING_BEGIN, true);
      getStartingState().addTransition(new Transition(state, '{'));
    }
}


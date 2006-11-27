package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class LineBreakDefinition extends AutomataDefinition
{

    public LineBreakDefinition()
    {
      State state = new State(AbcTokenType.UNKNOWN, false);
      getStartingState().addTransition(new Transition(state, '!'));
      State state1 = new State(AbcTokenType.LINE_BREAK, true);
      state.addTransition(new Transition(state1, '\n'));
    }
}


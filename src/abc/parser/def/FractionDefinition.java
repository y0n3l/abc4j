package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class FractionDefinition extends AutomataDefinition
{
    public FractionDefinition()
    {
        State state = new State(AbcTokenType.FRACTION, true);
        char[] chars = {'/'};
        Transition trans = new Transition(state,chars);
        getStartingState().addTransition(trans);
    }

}
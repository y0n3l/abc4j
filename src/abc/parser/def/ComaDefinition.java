package abc.parser.def;

import abc.parser.AbcTokenType;
import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;

/** **/
public class ComaDefinition extends AutomataDefinition
{
    public ComaDefinition()
    {
        State state = new State(AbcTokenType.COMA, true);
        char[] chars = {':'};
        Transition trans = new Transition(state,chars);
        getStartingState().addTransition(trans);
    }

}

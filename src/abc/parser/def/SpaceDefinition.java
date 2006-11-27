package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class SpaceDefinition extends AutomataDefinition
{
    public SpaceDefinition()
    {
        State state = new State(AbcTokenType.SPACE, true);
        char[] chars = {' ', '\t'};
        Transition trans = new Transition(state,chars);
        getStartingState().addTransition(trans);
    }

}

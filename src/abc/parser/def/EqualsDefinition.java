package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class EqualsDefinition extends AutomataDefinition
{
    public EqualsDefinition()
    {
        State state = new State(AbcTokenType.EQUALS, true);
        getStartingState().addTransition(new Transition(state, '='));
    }
}


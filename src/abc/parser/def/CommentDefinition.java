package abc.parser.def;

import abc.parser.AbcTokenType;
import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;

/** **/
public class CommentDefinition extends AutomataDefinition
{
    public CommentDefinition()
    {
        State state = new State(AbcTokenType.COMMENT, true);
        char[] chars = {'%'};
        Transition trans = new Transition(state,chars);
        getStartingState().addTransition(trans);
    }

}

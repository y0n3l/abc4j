package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

public class UserDefinedDefinition extends AutomataDefinition
{
    private static char[] chars = {'H','I','J','K','L','M','N','O',
        'P','Q','R','S','T','U','V','W','X','Y','Z'};

    public UserDefinedDefinition()
    {
        State state = new State(AbcTokenType.USER_DEFINED, true);
        Transition trans = new Transition(state,chars);
        getStartingState().addTransition(trans);
    }
}


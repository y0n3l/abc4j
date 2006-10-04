package abc.parser.def;

import scanner.Transition;
import scanner.AutomataDefinition;
import scanner.State;
import abc.parser.AbcTokenType;
/*
// header - edit "Data/yourJavaHeader" to customize
// contents - edit "EventHandlers/Java file/onCreate" to customize
//
*/

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


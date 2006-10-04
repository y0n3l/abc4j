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

public class BaseNoteDefinition extends AutomataDefinition
{
    private static char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'a', 'b', 'c', 'd', 'e', 'f', 'g'};

    public BaseNoteDefinition()
    { buildDefinition(); }

    protected void buildDefinition()
    {
        State state = new State(AbcTokenType.BASE_NOTE, true);
        Transition trans = new Transition(state,chars);
        getStartingState().addTransition(trans);
    }
}


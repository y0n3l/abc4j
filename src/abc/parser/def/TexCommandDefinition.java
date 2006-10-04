package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class TexCommandDefinition extends AutomataDefinition
{
    public TexCommandDefinition()
    {
        //State state = new State(AbcTokenType.TEX_COMMAND, true);
        //getStartingState().addTransition(new Transition(state, '\\'));

    }
}


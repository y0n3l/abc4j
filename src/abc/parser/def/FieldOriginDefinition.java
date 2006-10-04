package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class FieldOriginDefinition extends AutomataDefinition
{
    public FieldOriginDefinition()
    {
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state,'O');
        getStartingState().addTransition(trans);
        state.addTransition(new IsColonTransition(new State(AbcTokenType.FIELD_ORIGIN, true)));
    }

}


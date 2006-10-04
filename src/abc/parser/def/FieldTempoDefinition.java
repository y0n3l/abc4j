package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class FieldTempoDefinition extends AutomataDefinition
{
    public FieldTempoDefinition()
    {
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state,'Q');
        getStartingState().addTransition(trans);
        state.addTransition(new IsColonTransition(new State(AbcTokenType.FIELD_TEMPO, true)));
    }

}


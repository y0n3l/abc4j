package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class MeterCDefinition extends AutomataDefinition
{
    public MeterCDefinition()
    {
        State state = new State(AbcTokenType.C_METER, true);
        Transition trans = new Transition(state,'C');
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.C_METER, true);
        trans = new Transition(state1,'|');
        state.addTransition(trans);
    }
}


package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class TempoCDefinition extends AutomataDefinition
{
    public TempoCDefinition()
    {
        State state = new State(AbcTokenType.C_TEMPO, true);
        Transition trans = new Transition(state,'C');
        getStartingState().addTransition(trans);
    }
}


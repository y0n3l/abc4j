package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.IsDigitTransition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class NumberDefinition extends AutomataDefinition
{
    public NumberDefinition()
    { buildDefinition(); }

    protected void buildDefinition()
    {
        //===================== FIELD
        State stateNUMBER = new State(AbcTokenType.NUMBER, true);
        Transition trans = new IsDigitTransition(stateNUMBER);
        getStartingState().addTransition(trans);
        trans = new IsDigitTransition(stateNUMBER);
        stateNUMBER.addTransition(trans);
    }
}


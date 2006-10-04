package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class FieldHistoryDefinition extends AutomataDefinition
{
    public FieldHistoryDefinition()
    { buildDefinition(); }

    protected void buildDefinition()
    {
        State startingState = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(startingState,'H');
        getStartingState().addTransition(trans);
        startingState.addTransition(new IsColonTransition(new State(AbcTokenType.FIELD_HISTORY, true)));
    }

}


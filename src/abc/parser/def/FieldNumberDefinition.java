package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class FieldNumberDefinition extends AutomataDefinition
{
    public FieldNumberDefinition()
    { buildDefinition(); }

    protected void buildDefinition()
    {
        State startingState = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(startingState,'X');
        getStartingState().addTransition(trans);
        startingState.addTransition(new IsColonTransition(new State(AbcTokenType.FIELD_NUMBER, true)));
    }

}
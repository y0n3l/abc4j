package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class KeyAccidentalDefinition extends AutomataDefinition
{
    public KeyAccidentalDefinition()
    {
        State startingState = new State(AbcTokenType.KEY_ACCIDENTAL, true);
        char[] chars = {'#','b'};
        Transition trans = new Transition(startingState,chars);
        getStartingState().addTransition(trans);
    }

}


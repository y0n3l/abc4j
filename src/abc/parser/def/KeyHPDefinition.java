package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class KeyHPDefinition extends AutomataDefinition
{
    public KeyHPDefinition()
    {
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state,'H');
        getStartingState().addTransition(trans);
        State state1 = new State(AbcTokenType.KEY_HP, true);

        char[] chars = {'p', 'P'};
        trans = new Transition(state1,chars);
        state.addTransition(trans);
    }
}


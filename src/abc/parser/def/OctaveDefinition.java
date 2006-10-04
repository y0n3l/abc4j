package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class OctaveDefinition extends AutomataDefinition
{
    public OctaveDefinition()
    {
        State state = new State(AbcTokenType.OCTAVE, true);
        getStartingState().addTransition(new Transition(state, ','));
        state.addTransition(new Transition(state, ','));

        state = new State(AbcTokenType.OCTAVE, true);
        getStartingState().addTransition(new Transition(state, '\''));
        state.addTransition(new Transition(state, '\''));

    }
}


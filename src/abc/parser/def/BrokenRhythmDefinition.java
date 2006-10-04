package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class BrokenRhythmDefinition extends AutomataDefinition
{
    public BrokenRhythmDefinition()
    {
        State state = new State(AbcTokenType.BROKEN_RHYTHM, true);
        getStartingState().addTransition(new Transition(state, '<'));
        state.addTransition(new Transition(state, '<'));

        state = new State(AbcTokenType.BROKEN_RHYTHM, true);
        getStartingState().addTransition(new Transition(state, '>'));
        state.addTransition(new Transition(state, '>'));

    }
}


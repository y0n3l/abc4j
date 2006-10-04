package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class AccidentalDefinition extends AutomataDefinition
{
    public AccidentalDefinition()
    {
        State state = new State(AbcTokenType.ACCIDENTAL, true);
        Transition trans = new Transition(state,'^');
        getStartingState().addTransition(trans);
        State state1 = new State(AbcTokenType.ACCIDENTAL, true);
        trans = new Transition(state1,'^');
        state.addTransition(trans);

        state = new State(AbcTokenType.ACCIDENTAL, true);
        trans = new Transition(state,'_');
        getStartingState().addTransition(trans);
        state1 = new State(AbcTokenType.ACCIDENTAL, true);
        trans = new Transition(state1,'^');
        state.addTransition(trans);

        state = new State(AbcTokenType.ACCIDENTAL, true);
        trans = new Transition(state,'=');
        getStartingState().addTransition(trans);
    }
}


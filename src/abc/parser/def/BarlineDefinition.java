package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class BarlineDefinition extends AutomataDefinition
{
    public BarlineDefinition()
    {
        State state = new State(AbcTokenType.BARLINE, true);
        getStartingState().addTransition(new Transition(state,'|'));

        State state1 = new State(AbcTokenType.BARLINE, true);
        char[] chars = {'|', ']', ':' };
        Transition trans = new Transition(state1,chars);
        state.addTransition(trans);

        State state2 = new State(AbcTokenType.UNKNOWN, false);
        getStartingState().addTransition(new Transition(state2, ':'));
        State state3 = new State(AbcTokenType.BARLINE, true);
        char[] chars1 = {':', '|'};
        trans = new Transition(state3,chars1);
        state2.addTransition(trans);

        State state4 = new State(AbcTokenType.UNKNOWN, false);
        getStartingState().addTransition(new Transition(state4,'['));
        State state5 = new State(AbcTokenType.BARLINE, true);
        trans = new Transition(state5, '|');
        state4.addTransition(trans);
    }
}


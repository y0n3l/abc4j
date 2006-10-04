package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class FieldWordsDefinition extends AutomataDefinition {
	
    public FieldWordsDefinition() {
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state,'W');
        getStartingState().addTransition(trans);
        state.addTransition(new IsColonTransition(new State(AbcTokenType.FIELD_WORDS, true)));
    }
}

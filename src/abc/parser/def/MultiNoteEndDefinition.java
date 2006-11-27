package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class MultiNoteEndDefinition extends AutomataDefinition
{

    public MultiNoteEndDefinition()
    {
      State state = new State(AbcTokenType.MULTI_NOTE_END, true);
      getStartingState().addTransition(new Transition(state, ']'));
    }
}


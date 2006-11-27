package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class MultiNoteBeginDefinition extends AutomataDefinition
{

    public MultiNoteBeginDefinition()
    {
      State state = new State(AbcTokenType.MULTI_NOTE_BEGIN, true);
      getStartingState().addTransition(new Transition(state, '['));
    }
}


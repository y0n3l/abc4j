package abc.parser.def;

import abc.parser.AbcTokenType;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;
import scanner.IsDigitTransition;

/** **/
public class MultiNoteBeginDefinition extends AutomataDefinition
{

    public MultiNoteBeginDefinition()
    {
      State state = new State(AbcTokenType.MULTI_NOTE_BEGIN, true);
      getStartingState().addTransition(new Transition(state, '['));
    }
}


package abc.parser.def;

import abc.parser.AbcTokenType;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;
import scanner.IsDigitTransition;

/** **/
public class MultiNoteEndDefinition extends AutomataDefinition
{

    public MultiNoteEndDefinition()
    {
      State state = new State(AbcTokenType.MULTI_NOTE_END, true);
      getStartingState().addTransition(new Transition(state, ']'));
    }
}


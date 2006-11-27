package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class GuitarChordDefinition extends AutomataDefinition
{

    public GuitarChordDefinition()
    {
      State state = new State(AbcTokenType.GUITAR_CHORD, true);
      getStartingState().addTransition(new Transition(state, '"'));
    }
}

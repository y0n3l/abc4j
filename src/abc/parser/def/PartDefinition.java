package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** This scanner extends the capabilities of the default scanner to implement
 *  abc tokens scannig. **/
public class PartDefinition extends AutomataDefinition
{

    public PartDefinition()
    {
      State state = new State(AbcTokenType.PART, true);
      char[] chars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
      getStartingState().addTransition(new Transition(state, chars));
    }
}


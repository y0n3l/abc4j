package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.IsDigitTransition;
import scanner.State;
import abc.parser.AbcTokenType;

/** **/
public class DigitDefinition extends AutomataDefinition
{
    public DigitDefinition()
    {
        State state = new State(AbcTokenType.DIGIT, true);
        getStartingState().addTransition(new IsDigitTransition(state));
    }
}


package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class FieldDiscographyDefinition extends AutomataDefinition
{
    public FieldDiscographyDefinition()
    { buildDefinition(); }

    protected void buildDefinition()
    {
        State areaState = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(areaState,'D');
        getStartingState().addTransition(trans);
        areaState.addTransition(new IsColonTransition(new State(AbcTokenType.FIELD_DISCOGRAPHY, true)));
    }

}


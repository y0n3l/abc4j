package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class FieldAreaDefinition extends AutomataDefinition
{
    public FieldAreaDefinition()
    { buildDefinition(); }

    protected void buildDefinition()
    {
        State areaState = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(areaState,'A');
        getStartingState().addTransition(trans);
        areaState.addTransition(new IsColonTransition(new State(AbcTokenType.FIELD_AREA, true)));
    }

}


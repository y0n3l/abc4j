package abc.parser.def;

import scanner.Transition;
import scanner.State;
/*
// header - edit "Data/yourJavaHeader" to customize
// contents - edit "EventHandlers/Java file/onCreate" to customize
//
*/
public class IsColonTransition extends Transition
{
  private static char[] chars = {':'};

    public IsColonTransition(State state)
    { super(state, chars); }

}

package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Scanner;
import scanner.Transition;
import scanner.IsDigitTransition;

import abc.parser.AbcTokenType;

/** **/
public class ModeDefinition extends AutomataDefinition
{
  public ModeDefinition()
  {
    super();
    copyFrom(
        (new ModeMinorDefinition()).union(new ModeMajorDefinition()).union(new ModeLydianDefinition())
    .union(new ModeIonianDefinition()).union(new ModeMixolydianDefinition()).union(new ModeDorianDefinition())
    .union(new ModeAeolianDefinition()).union(new ModePhrygianDefinition()).union(new ModeLocrianDefinition())
    );
  }

  private class ModeAeolianDefinition extends AutomataDefinition
{

    public ModeAeolianDefinition()
    {
        char[] chars = {'a','A' };
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'e', 'E'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE, true);
        char[] chars2 = {'o', 'O'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
private class ModeDorianDefinition extends AutomataDefinition
{

    public ModeDorianDefinition()
    {
        char[] chars = {'d','D' };
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'o', 'O'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE, true);
        char[] chars2 = {'r', 'R'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
private class ModeIonianDefinition extends AutomataDefinition
{

    public ModeIonianDefinition()
    {
        char[] chars = {'i','I' };
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'o', 'O'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE , true);
        char[] chars2 = {'n', 'N'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
private class ModeLocrianDefinition extends AutomataDefinition
{

    public ModeLocrianDefinition()
    {
        char[] chars = {'l','L' };
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'o', 'O'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE , true);
        char[] chars2 = {'c', 'C'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
private class ModeLydianDefinition extends AutomataDefinition
{

    public ModeLydianDefinition()
    {
        char[] chars = {'l','L' };
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'y', 'Y'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE , true);
        char[] chars2 = {'d', 'D'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
private class ModeMajorDefinition extends AutomataDefinition
{

    public ModeMajorDefinition()
    {
        char[] chars = {'m','M' };
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'a', 'A'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE , true);
        char[] chars2 = {'j', 'J'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
private class ModeMinorDefinition extends AutomataDefinition
{

    public ModeMinorDefinition()
    {
        char[] chars = {'m','M' };
        State state = new State(AbcTokenType.MODE , true);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'i', 'I'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE , true);
        char[] chars2 = {'n', 'N'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
private class ModeMixolydianDefinition extends AutomataDefinition
{

    public ModeMixolydianDefinition()
    {
        char[] chars = {'m','M' };
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'i', 'I'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE , true);
        char[] chars2 = {'x', 'X'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
private class ModePhrygianDefinition extends AutomataDefinition
{

    public ModePhrygianDefinition()
    {
        char[] chars = {'p','P' };
        State state = new State(AbcTokenType.UNKNOWN, false);
        Transition trans = new Transition(state, chars);
        getStartingState().addTransition(trans);

        State state1 = new State(AbcTokenType.UNKNOWN, false);
        char[] chars1 = {'h', 'H'};
        state.addTransition(new Transition(state1, chars1));

        State state2 = new State(AbcTokenType.MODE , true);
        char[] chars2 = {'r', 'R'};
        state1.addTransition(new Transition(state2, chars2));
    }

}
}
package abc.parser.def;

import abc.parser.AbcTokenType;
import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
/** **/
public class ChordNameDefinition extends AutomataDefinition
{
  private static char[] chars = {
    '0','1','2','3','4','5','6','7','8','9',
    ' ',
    '\t','!','#','$','&','\'','(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_',
    '`','{','|','}','~',
    'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
    'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    public ChordNameDefinition()
    {   //===================== FIELD
        State stateTEXT_CHAR = new State(AbcTokenType.CHORD_NAME, true);
        Transition trans = new Transition(stateTEXT_CHAR, chars);
        getStartingState().addTransition(trans);
        stateTEXT_CHAR.addTransition(new Transition(stateTEXT_CHAR, chars));
    }

}


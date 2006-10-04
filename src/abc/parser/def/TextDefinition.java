package abc.parser.def;

import scanner.AutomataDefinition;
import scanner.State;
import scanner.Transition;
import abc.parser.AbcTokenType;

/** **/
public class TextDefinition extends AutomataDefinition {

  private static char[] chars = {
    '0','1','2','3','4','5','6','7','8','9',
    ' ',
    '\t','"','!','#','$','&','\'','(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_',
    '`','{','|','}','~',
    'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
    'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
    //==== not really part of v1.6
    , 'á', 'â', 'ã', 'é', 'ê', 'í', 'ó', 'ô', 'õ', 'ú'		// as requiered by Hugo
    , 'è', 'à', 'ù'											// french characters
    };

    public TextDefinition() {   
    	//===================== FIELD
        State stateTEXT_CHAR = new State(AbcTokenType.TEXT, true);
        Transition trans = new Transition(stateTEXT_CHAR, chars);
        getStartingState().addTransition(trans);
        stateTEXT_CHAR.addTransition(new Transition(stateTEXT_CHAR, chars));
    }

}


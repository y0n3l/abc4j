package abc.parser.def;

import scanner.*;
import scanner.State;
/*
// header - edit "Data/yourJavaHeader" to customize
// contents - edit "EventHandlers/Java file/onCreate" to customize
//
*/
public class IsTextCharTransition extends Transition
{
  private static char[] chars = {'0','1','2','3','4','5','6','7','8','9',
  '\t','"','!','#','$','&','\'','(',')','*','+',',','-','.','/',':',';','<','=','>','?','@','[','\\',']','^','_',
  'A', 'B', 'C', 'D','E','F','G','H','I','J','K',
      'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
      'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s',
      'y','u','v','w','x','y','z'};

    public IsTextCharTransition(State state)
    { super(state, chars); }

}


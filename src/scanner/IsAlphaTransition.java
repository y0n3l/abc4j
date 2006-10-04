package scanner;

/** This class defines a transition that corresponds to any alpha character :
 * a letter, upper or lower case. */
public class IsAlphaTransition extends Transition
{
  private static char[] chars = {'A', 'B', 'C', 'D','E','F','G','H','I','J','K',
      'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
      'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s',
      'y','u','v','w','x','y','z'};
  /** Default constructor. */
  public IsAlphaTransition(State state)
  { super(state, chars); }
}



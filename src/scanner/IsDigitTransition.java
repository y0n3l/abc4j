package scanner;

/** This class defines a transition that corresponds to a digit character. */
public class IsDigitTransition extends Transition
{
   private static char[] chars = {'0','1','2','3','4','5','6','7','8','9'};

   /** Default constructor. */
   public IsDigitTransition(State state)
   { super(state, chars); }

}


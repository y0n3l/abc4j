package scanner;

/** This class enables the description of automatas. Those definitions are described
 * using states and transitions between those states.
 * @see Transition
 * @see State */
public class AutomataDefinition
{
  /** The starting state. */
  private State m_startingState = null;

  /** Constructs a new definition. */
  public AutomataDefinition()
  { m_startingState = new State(TokenType.UNKNOWN, false); }

  /** Creates a new definition with the given starting state.
   * @param startingState The starting state of this definition. */
  public AutomataDefinition(State startingState)
  { m_startingState = startingState; }

  /** Returns the starting state of this automata.
   * @return The starting state of this automata. */
  public State getStartingState()
  { return m_startingState; }

  /** Realize an union between this definition and the given one.
   * @param def The definition to be unioned with this one.
   * @return A reference on this once unioned. */
  public AutomataDefinition union(AutomataDefinition def)
  { return new AutomataDefinition(m_startingState.union(def.getStartingState())); }

  /** Copy the given definition to this one. This is equivalent to set the
   * starting state of this definition to the starting state of the specified
   * one.
   * @param definition The definition of an automata. */
  public void copyFrom(AutomataDefinition definition)
  { m_startingState = definition.getStartingState(); }

  /** Returns a string representation of this object.
   * @return A string representation of this object. */
  public String toString()
  { return "FSA : [" + m_startingState.toString()+"]"; }

}


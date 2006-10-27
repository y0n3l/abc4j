package abc.parser;

import java.util.Vector;
import scanner.TokenType;
import scanner.FinaleStateAutomata;
import abc.parser.def.DefinitionFactory;

class AutomataFactory
{

  public static Vector m_allPreviouslyCreatedAutomatas = new Vector();

  public static FinaleStateAutomata getAutomata(TokenType abcTokenType)
  {
    return new FinaleStateAutomata(DefinitionFactory.getDefinition(abcTokenType));
  }

  public static FinaleStateAutomata getAutomata(TokenType[] tokenTypes)
  {
    return new FinaleStateAutomata(DefinitionFactory.getDefinition(tokenTypes));
  }


}
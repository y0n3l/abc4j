package scanner;

/** Tokens types add semantic to tokens to diferenciate them. */
public interface TokenType
{
  /** The unknown token type. */
  public static final TokenType UNKNOWN = new TokenType()
  {
    private static final String UNKNOWN_TYPE = "UNKNOWN";
    public String toString()
    { return UNKNOWN_TYPE;  }
  };
}


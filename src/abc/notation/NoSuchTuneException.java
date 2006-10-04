package abc.notation;

/** This exception is thrown when a requested tune hasn't been found. */
public class NoSuchTuneException extends RuntimeException
{
  public NoSuchTuneException(String e)
  {super(e);}
}

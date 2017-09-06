import java.util.Comparator;

/**
 * StringComparator, comparator for two strings using Java's String.compareTo method.
 * @author Nagoshi, Vincent
 * @version 1.02.00
 */

public class StringComparator implements Comparator<String> {
  @Override
  public int compare(String st1, String st2) {
    return st1.compareTo(st2);
  }   
}

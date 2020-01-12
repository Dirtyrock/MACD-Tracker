/* 
    Copyright (C) 2019  Nagoshi, Vincent

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

import java.util.Comparator;

/**
 * StringComparator, comparator for two strings using Java's String.compareTo method.
 * @author Nagoshi, Vincent
 * @version 1.03.01
 */

public class StringComparator implements Comparator<String> {
  @Override
  public int compare(String st1, String st2) {
    return st1.compareTo(st2);
  }   
}

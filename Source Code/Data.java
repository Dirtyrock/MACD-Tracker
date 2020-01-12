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

import java.math.BigDecimal;

/**
 * Data, Contains an Asset's information for one day.
 * @author Nagoshi, Vincent
 * @version 1.03.01
 */

public class Data {
  private String date, perChange;
  private BigDecimal open, low, high, close, volume, change;
  
  public Data(String date, BigDecimal open, BigDecimal low, BigDecimal high, BigDecimal close, BigDecimal volume) {
    this.date = date;
    this.open = open;
    this.low = low;
    this.high = high;
    this.close = close;
    this.volume = volume;
    this.change = new BigDecimal(-1);
    this.perChange = "nil";
  }
  
  public void setOpen(BigDecimal open) {
    this.open = open;
  }
  public void setHigh(BigDecimal high) {
    this.high = high;
  }
  public void setLow(BigDecimal low) {
    this.low = low;
  }
  public void setClose(BigDecimal close) {
    this.close = close;
  }
  
  public String getDate() {
    return date;
  }
  public BigDecimal getOpen() {
    return open;
  }
  public BigDecimal getLow() {
    return low;
  }
  public BigDecimal getHigh() {
    return high;
  }
  public BigDecimal getClose() {
    return close;
  }
  public BigDecimal getVolume() {
    return volume;
  }
  public BigDecimal getChange() {
    return change;
  }
  public String getPercentChange() {
    return perChange;
  }
}

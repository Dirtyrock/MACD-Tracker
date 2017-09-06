import java.math.BigDecimal;

/**
 * Data, Contains an Asset's information for one day.
 * @author Nagoshi, Vincent
 * @version 1.02.00
 */

public class Data {
  private String date, perChange;
  private BigDecimal open, low, high, close, volume, change;
  
  public Data(String date, BigDecimal open, BigDecimal low, BigDecimal high, BigDecimal close, BigDecimal volume, BigDecimal change, String perChange) {
    this.date = date;
    this.open = open;
    this.low = low;
    this.high = high;
    this.close = close;
    this.volume = volume;
    this.change = change;
    this.perChange = perChange;
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

import java.math.BigDecimal;

/**
 * Data, Contains an Asset's information for one day.
 * @author Nagoshi, Vincent
 * @version 1.01.01
 */

public class Data {
  private String date;
  private BigDecimal open, low, high, close, volume;
  
  public Data(String date, BigDecimal open, BigDecimal low, BigDecimal high, BigDecimal close, BigDecimal volume) {
    this.date = date;
    this.open = open;
    this.low = low;
    this.high = high;
    this.close = close;
    this.volume = volume;
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
}

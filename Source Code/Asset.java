import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Asset, Contains all data for an asset and its indicators.
 * @author Nagoshi, Vincent
 * @version 1.01.01
 */

public class Asset {
  private String assetName;
  private ArrayList<Data> data;
  private int action;
  private BigDecimal[] macd1Data;
  private BigDecimal[] signalData;
  
  public Asset(String assetName) throws IOException {
    this.assetName = assetName;
    this.data = new ArrayList<Data>();
    URL link = new URL("http://www.google.com/finance/historical?q=" + assetName.replaceAll(":", "%3A") + "&output=csv");
    BufferedReader br = new BufferedReader(new InputStreamReader(link.openStream()));
    String in;
    br.readLine();
    while((in = br.readLine()) != null) {
      StringTokenizer st = new StringTokenizer(in, ",");
      String date = st.nextToken();
      BigDecimal open = new BigDecimal(st.nextToken());
      BigDecimal high = new BigDecimal(st.nextToken());
      BigDecimal low = new BigDecimal(st.nextToken());
      BigDecimal close = new BigDecimal(st.nextToken());
      BigDecimal volume = new BigDecimal(st.nextToken());
      data.add(new Data(date, open, low, high, close, volume));
    }
    action = buySellSignal(data.toArray(new Data[1]));
  }
  
  //-1 sell
  //0 wait
  //1 buy
  //2 not enough data
  private int buySellSignal(Data[] data) {
    int rtn = 0;
    try {
      macd1Data = new BigDecimal[data.length - 50];
      for(int i = 0; i < macd1Data.length; i++) {
        macd1Data[i] = estimatedMovingAverage(i, 12, data).subtract(estimatedMovingAverage(i, 26, data));
      }
      signalData= new BigDecimal[macd1Data.length - 16];
      for(int i = 0; i < signalData.length; i++) {
        signalData[i] = estimatedMovingAverage(i, 9, macd1Data);
      }    
      if(macd1Data[0].compareTo(signalData[0]) < 0 && macd1Data[1].compareTo(signalData[1]) > 0) {
        rtn = -1;
      }
      else if (macd1Data[0].compareTo(signalData[0]) > 0 && macd1Data[1].compareTo(signalData[1]) < 0) {
        rtn = 1;
      }
    } catch (NegativeArraySizeException e) {
      rtn = 2;
    } catch (ArrayIndexOutOfBoundsException e) {
      rtn = 2;
    }
    return rtn;
  }
  
  private BigDecimal simpleMovingAverage(int start, int periods, Data[] data) {
    if(data.length < start + periods) {
      throw new ArrayIndexOutOfBoundsException();
    }
    BigDecimal rtn = data[start].getClose();
    for(int i = start + 1; i < start + periods; i++) {
      rtn = rtn.add(data[i].getClose());
    }
    rtn = rtn.divide(new BigDecimal(periods), BigDecimal.ROUND_HALF_UP);
    return rtn;
  }
  
  private BigDecimal simpleMovingAverage(int start, int periods, BigDecimal[] data) {
    if(data.length < start + periods) {
      throw new ArrayIndexOutOfBoundsException();
    }
    BigDecimal rtn = data[start];
    for(int i = start + 1; i < start + periods; i++) {
      rtn = rtn.add(data[i]);
    }
    rtn = rtn.divide(new BigDecimal(periods), BigDecimal.ROUND_HALF_UP);
    return rtn;
  }
  
  private BigDecimal estimatedMovingAverage(int start, int periods, Data[] data) {
    BigDecimal[] ema = new BigDecimal[periods];
    BigDecimal startingAverage;
    BigDecimal multiplier;
    if(data.length < (periods * 2) - 1 + start) {
      throw new ArrayIndexOutOfBoundsException();
    }
    startingAverage = simpleMovingAverage(start + periods - 1, periods, data);
    multiplier = new BigDecimal(2);
    multiplier = multiplier.setScale(100, BigDecimal.ROUND_HALF_UP);
    multiplier = multiplier.divide(new BigDecimal(periods + 1), BigDecimal.ROUND_HALF_UP);
    ema[periods - 1] = startingAverage;
    for (int i = ema.length - 2; i >= 0; i--) {
      ema[i] = data[start + i].getClose();
      ema[i] = ema[i].subtract(ema[i + 1]);
      ema[i] = ema[i].multiply(multiplier);
      ema[i] = ema[i].add(ema[i + 1]);
    }
    return ema[0];
  }
  
  private BigDecimal estimatedMovingAverage(int start, int periods, BigDecimal[] data) {
    BigDecimal[] ema = new BigDecimal[periods];
    BigDecimal startingAverage;
    BigDecimal multiplier;
    if(data.length < (periods * 2) - 1 + start) {
      throw new ArrayIndexOutOfBoundsException();
    }
    startingAverage = simpleMovingAverage(start + periods - 1, periods, data);
    multiplier = new BigDecimal(2);
    multiplier = multiplier.setScale(100, BigDecimal.ROUND_HALF_UP);
    multiplier = multiplier.divide(new BigDecimal(periods + 1), BigDecimal.ROUND_HALF_UP);
    ema[periods - 1] = startingAverage;
    for (int i = ema.length - 2; i >= 0; i--) {
      ema[i] = data[start + i];
      ema[i] = ema[i].subtract(ema[i + 1]);
      ema[i] = ema[i].multiply(multiplier);
      ema[i] = ema[i].add(ema[i + 1]);
    }
    return ema[0];
  }
  
  public String getAssetName() {
    return assetName;
  }
  
  public ArrayList<Data> getData() {
    return data;
  }
  
  public int getAction() {
    return action;
  }
  
  public BigDecimal[] getMacd1Data() {
    return macd1Data;
  }
  
  public BigDecimal[] getSignalData() {
    return signalData;
  }
}

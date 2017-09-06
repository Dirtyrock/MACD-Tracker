import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;

/**
 * Asset, Contains all data for an asset and its indicators.
 * @author Nagoshi, Vincent
 * @version 1.02.00
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
    URL link = new URL("https://markets.financialcontent.com/stocks/action/gethistoricaldata?Symbol=" + assetName + "&Range=13");//TODO
    BufferedReader br = new BufferedReader(new InputStreamReader(link.openStream()));
    String in;
    //Test to make sure there is data for the stock. FinancialContent will always send a csv regardless of assetName
    String test = br.readLine();
    if(test == null) {
      throw new IOException();
    }
    //Read data from FinancialContent
    while((in = br.readLine()) != null) {
      String[] stk = in.split(",");
      String date = stk[1] == null || stk[8].equals("") ? "NA" : stk[1];
      BigDecimal open = new BigDecimal(stk[2] == null || stk[2].equals("") ? "-1" : stk[2]);
      BigDecimal high = new BigDecimal(stk[3] == null || stk[3].equals("") ? "-1" : stk[3]);
      BigDecimal low = new BigDecimal(stk[4] == null || stk[4].equals("") ? "-1" : stk[4]);
      BigDecimal close = new BigDecimal(stk[5] == null || stk[5].equals("") ? "-1" : stk[5]);
      BigDecimal volume = new BigDecimal(stk[6] == null || stk[6].equals("") ? "-1" : stk[6]);
      BigDecimal change = new BigDecimal(stk[7] == null || stk[7].equals("") ? "-1" : stk[7]);
      String perChange = stk[8] == null || stk[8].equals("") ? "NA" : stk[8];
      data.add(new Data(date, open, low, high, close, volume, change, perChange));
      
    }
    patchData();
    action = buySellSignal(data.toArray(new Data[1]));
  }
  
  //replaces any missing data with the average of the before and after prices
  //defaults to closest known data when neighbor data is missing
  private void patchData() {
    BigDecimal neg = new BigDecimal(-1);
    for(int i = 0; i < data.size(); i++) {
      if(data.get(i).getOpen().equals(neg)) {
        if(i == 0) {//start of data
          boolean foundData = false;
          for(int j = 1; j < data.size(); j++) {
            if(!data.get(j).getOpen().equals(neg)) {
              data.get(i).setOpen(new BigDecimal(data.get(j).getOpen().toString()));
              foundData = true;
              break;
            }
          }
          if(!foundData) {
            data.get(i).setOpen(new BigDecimal(0));
          }
        }
        else if(i == data.size() - 1) {//end of data
          data.get(i).setOpen(new BigDecimal(data.get(i - 1).getOpen().toString()));
        }
        else {
          if(data.get(i + 1).getOpen().equals(neg)) {
            data.get(i).setOpen(new BigDecimal(data.get(i - 1).getOpen().toString()));
          }
          else {
            data.get(i).setOpen(new BigDecimal((data.get(i - 1).getOpen().add(data.get(i + 1).getOpen())).divide(new BigDecimal(2)).toString()));
          
          }
        }
      }
      if(data.get(i).getHigh().equals(neg)) {
        if(i == 0) {//start of data
          boolean foundData = false;
          for(int j = 1; j < data.size(); j++) {
            if(!data.get(j).getHigh().equals(neg)) {
              data.get(i).setHigh(new BigDecimal(data.get(j).getHigh().toString()));
              foundData = true;
              break;
            }
          }
          if(!foundData) {
            data.get(i).setHigh(new BigDecimal(0));
          }
        }
        else if(i == data.size() - 1) {//end of data
          data.get(i).setHigh(new BigDecimal(data.get(i - 1).getHigh().toString()));
        }
        else {
          if(data.get(i + 1).getOpen().equals(neg)) {
            data.get(i).setHigh(new BigDecimal(data.get(i - 1).getHigh().toString()));
          }
          else {
            data.get(i).setHigh(new BigDecimal((data.get(i - 1).getHigh().add(data.get(i + 1).getHigh())).divide(new BigDecimal(2)).toString()));
          
          }
        }
      }
      if(data.get(i).getLow().equals(neg)) {
        if(i == 0) {//start of data
          boolean foundData = false;
          for(int j = 1; j < data.size(); j++) {
            if(!data.get(j).getLow().equals(neg)) {
              data.get(i).setLow(new BigDecimal(data.get(j).getLow().toString()));
              foundData = true;
              break;
            }
          }
          if(!foundData) {
            data.get(i).setLow(new BigDecimal(0));
          }
        }
        else if(i == data.size() - 1) {//end of data
          data.get(i).setLow(new BigDecimal(data.get(i - 1).getLow().toString()));
        }
        else {
          if(data.get(i + 1).getOpen().equals(neg)) {
            data.get(i).setLow(new BigDecimal(data.get(i - 1).getLow().toString()));
          }
          else {
            data.get(i).setLow(new BigDecimal((data.get(i - 1).getLow().add(data.get(i + 1).getLow())).divide(new BigDecimal(2)).toString()));
          
          }
        }
      }
      if(data.get(i).getClose().equals(neg)) {
        if(i == 0) {//start of data
          boolean foundData = false;
          for(int j = 1; j < data.size(); j++) {
            if(!data.get(j).getClose().equals(neg)) {
              data.get(i).setClose(new BigDecimal(data.get(j).getClose().toString()));
              foundData = true;
              break;
            }
          }
          if(!foundData) {
            data.get(i).setClose(new BigDecimal(0));
          }
        }
        else if(i == data.size() - 1) {//end of data
          data.get(i).setClose(new BigDecimal(data.get(i - 1).getClose().toString()));
        }
        else {
          if(data.get(i + 1).getOpen().equals(neg)) {
            data.get(i).setClose(new BigDecimal(data.get(i - 1).getClose().toString()));
          }
          else {
            data.get(i).setClose(new BigDecimal((data.get(i - 1).getClose().add(data.get(i + 1).getClose())).divide(new BigDecimal(2)).toString()));
          
          }
        }
      }
    }
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

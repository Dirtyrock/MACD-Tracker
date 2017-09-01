import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * LoadingWindow, Window that loads in tracked assets and starts main window.
 * @author Nagoshi, Vincent
 * @version 1.01.01
 */

@SuppressWarnings("serial")
public class LoadingWindow extends JFrame {
  private JPanel panel, loadingBar;
  private JLabel progressLabel;
  private GridBagConstraints panelConstraints;
  
  private ArrayList<Asset> assetData; 
  private ArrayList<String> trackedAssets;
  private Color buyColor, sellColor;
  
  public LoadingWindow() {
    trackedAssets = new ArrayList<String>();
    assetData = new ArrayList<Asset>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File("MACDTrackerSettings.txt")));
      String line;
      try {
        line = br.readLine();
        StringTokenizer st = new StringTokenizer(line, ",");
        buyColor = new Color(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
        sellColor = new Color(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
      } catch (NumberFormatException e) {
        buyColor = new Color(122, 200, 122);
        sellColor = new Color(230, 90, 90);
      }
      br.close();
    } catch (IOException e) {
      buyColor = new Color(122, 200, 122);
      sellColor = new Color(230, 90, 90);
    }
    this.setTitle("MACD Tracker: Loading");
    this.setSize(new Dimension(400, 100));
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    panel = new JPanel(new GridBagLayout());
    panelConstraints = new GridBagConstraints();
    this.add(panel);
    this.setLocationRelativeTo(null);
    loadingBar = new JPanel();
    loadingBar.setPreferredSize(new Dimension(1, 25));
    loadingBar.setLocation(0, 0);
    loadingBar.setBackground(buyColor);
    progressLabel = new JLabel();
    panelConstraints.anchor = GridBagConstraints.LINE_START;
    panelConstraints.weightx = 0.9;
    panelConstraints.weighty = 1;
    panelConstraints.fill = GridBagConstraints.VERTICAL;
    panelConstraints.insets = new Insets(5,5,5,5);
    panelConstraints.gridx = 0;
    panelConstraints.gridy = 0;
    panel.add(new JLabel("Loading, please wait..."), panelConstraints);
    panelConstraints.anchor = GridBagConstraints.LINE_END;
    panelConstraints.gridx = 1;
    panel.add(progressLabel, panelConstraints);
    panelConstraints.insets = new Insets(0,0,0,0);
    panelConstraints.anchor = GridBagConstraints.LINE_START;
    panelConstraints.gridwidth = 2;
    panelConstraints.weighty = 0;
    panelConstraints.gridx = 0;
    panelConstraints.gridy = 1;
    panel.add(loadingBar, panelConstraints);
    this.pack();
    this.setSize(new Dimension(400, this.getHeight()));
    this.setVisible(true);
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File("trackedAssets.txt")));
      String line;
      while ((line = br.readLine()) != null) {
        trackedAssets.add(line);
      }
      br.close();
    } catch (IOException e) {}
    trackedAssets.sort(new StringComparator());
    progressLabel.setText("Loading: 0/" + trackedAssets.size());
    for(int i = 0; i < trackedAssets.size(); i++) {
      try {
        assetData.add(new Asset(trackedAssets.get(i)));
      } catch (IOException e) {}
      loadingBar.setPreferredSize(new Dimension((((i+1) * panel.getWidth())/trackedAssets.size()), loadingBar.getHeight()));
      progressLabel.setText("Loading: " + (i + 1) + "/" + trackedAssets.size());
    }
    new MainWindow(assetData, trackedAssets, buyColor, sellColor);
    this.dispose();
  }
}

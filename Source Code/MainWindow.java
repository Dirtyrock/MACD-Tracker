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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * MainWindow, primary window of the program, contains all buttons and sub menus.
 * @author Nagoshi, Vincent
 * @version 1.03.01
 */

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
  private JSplitPane mainPane, leftPane, rightPane;
  private JScrollPane dataTableScrollPane, trackingScrollPane;
  private JPanel optionsPanel, infoPanel;
  private JTable dataTable;
  private JList<String> trackedAssetsList;
  private DefaultListModel<String> trackedAssetsListModel;
  private JButton addButton, removeButton, infoButton, settingsButton;
  private boolean addWindowOpen, removeWindowOpen, infoWindowOpen, settingsWindowOpen;
  private JFrame addWindow, removeWindow, infoWindow, settingsWindow;
  private JLabel infoStockNameLabelField, infoDateLabelField, infoLastCloseLabelField, infoActionLabelField;
  private JLabel infoMacd1LabelField, infoSignalLabelField, infoPrevMacd1LabelField, infoPrevSignalLabelField;
  
  private ArrayList<Asset> assetData;
  private ArrayList<String> trackedAssets;
  private Color buyColor, sellColor;

  private class TableModel extends AbstractTableModel {
    private String[] columnNames = {"Date", "Open", "Low", "High", "Close", "Volume", "MACD 1", "Signal", "Action"};
    private ArrayList<Object[]> data = new ArrayList<Object[]>();
    
    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
      return columnNames[col];
    }
    
    @Override
    public int getRowCount() {
      return data.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
      return data.get(row)[col];
    }
    
    private void clear() {
      data = new ArrayList<Object[]>();
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
      if (row == -1 && col == -1) {
        clear();
      }
      else {
        if(data.size() <= row) {
          while(data.size() <= row) {
            data.add(new Object[10]);
          }
        }
        data.get(row)[col] = value;
        fireTableCellUpdated(row, col);
      }
    }
  }
  
  private class AssetListCellRenderer implements ListCellRenderer<String> {
    private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if(assetData.size() > 0 ? assetData.get(index).getAction() == 1 : false) {
        renderer.setBackground(buyColor);
      }
      else if (assetData.size() > 0 ? assetData.get(index).getAction() == -1 : false) {
        renderer.setBackground(sellColor);
      }
      else {
        renderer.setBackground(Color.WHITE);
      }
      if(isSelected) {
        renderer.setBackground(renderer.getBackground().brighter());//change/update later
      }
      return renderer;
    }
  }
  
  public MainWindow(ArrayList<Asset> assetData, ArrayList<String> trackedAssets, Color inputBuyColor, Color inputSellColor) {
    this.assetData = assetData;
    this.trackedAssets = trackedAssets;
    this.buyColor = inputBuyColor;
    this.sellColor = inputSellColor;
    this.addWindowOpen = false;
    this.removeWindowOpen = false;
    this.infoWindowOpen = false;
    this.settingsWindowOpen = false;
    trackedAssetsListModel = new DefaultListModel<String>();
    for(int i = 0; i < trackedAssets.size(); i++) {
      trackedAssetsListModel.addElement(trackedAssets.get(i));
    }
    trackedAssetsList = new JList<String>(trackedAssetsListModel);
    trackedAssetsList.setSelectedIndex(0);
    trackedAssetsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    trackedAssetsList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent arg0) {
        updateTable(trackedAssetsList.getSelectedIndex());
      }
    });
    trackedAssetsList.setCellRenderer(new AssetListCellRenderer());
    trackingScrollPane = new JScrollPane(trackedAssetsList);
    trackingScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    optionsPanel = new JPanel();
    optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
    addButton = makeAddButton();
    optionsPanel.add(addButton);
    removeButton = makeRemoveButton();
    optionsPanel.add(removeButton);
    infoButton = makeInfoButton();
    optionsPanel.add(infoButton);
    settingsButton = makeSettingsButton();
    optionsPanel.add(settingsButton);
    leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, optionsPanel, trackingScrollPane);
    leftPane.setDividerLocation(optionsPanel.getMinimumSize().height +1);
    leftPane.setEnabled(false);
    infoPanel = new JPanel(new GridLayout(2, 8, 5, 5));
    JLabel infoStockNameLabel = new JLabel("Stock Name:");
    JLabel infoDateLabel = new JLabel("Date:");
    JLabel infoLastCloseLabel = new JLabel("Close:");
    JLabel infoActionLabel = new JLabel("Action:");
    JLabel infoMacd1Label = new JLabel("MACD 1:");
    JLabel infoSignalLabel = new JLabel("Signal:");
    JLabel infoPrevMacd1Label = new JLabel("Prev. MACD 1:");
    JLabel infoPrevSignalLabel = new JLabel("Prev. Signal:");
    infoStockNameLabelField = new JLabel("Stock Name:");
    infoDateLabelField = new JLabel("Date:");
    infoLastCloseLabelField = new JLabel("Adj Close:");
    infoActionLabelField = new JLabel("Action:");
    infoMacd1LabelField = new JLabel("MACD1:");
    infoSignalLabelField = new JLabel("Signal:");
    infoPrevMacd1LabelField = new JLabel("Prev. MACD1:");
    infoPrevSignalLabelField = new JLabel("Prev. Signal:");
    infoPanel.add(infoStockNameLabel);
    infoPanel.add(infoStockNameLabelField);
    infoPanel.add(infoDateLabel);
    infoPanel.add(infoDateLabelField);
    infoPanel.add(infoLastCloseLabel);
    infoPanel.add(infoLastCloseLabelField);
    infoPanel.add(infoActionLabel);
    infoPanel.add(infoActionLabelField);
    infoPanel.add(infoMacd1Label);
    infoPanel.add(infoMacd1LabelField);
    infoPanel.add(infoSignalLabel);
    infoPanel.add(infoSignalLabelField);
    infoPanel.add(infoPrevMacd1Label);
    infoPanel.add(infoPrevMacd1LabelField);
    infoPanel.add(infoPrevSignalLabel);
    infoPanel.add(infoPrevSignalLabelField);
    dataTable = new JTable(new TableModel());
    if(trackedAssets.size() > 0) {
      updateTable(trackedAssetsList.getSelectedIndex());
    }
    dataTable.setRowSelectionAllowed(false);
    dataTable.setColumnSelectionAllowed(false);
    dataTable.getTableHeader().setReorderingAllowed(false);
    dataTableScrollPane = new JScrollPane(dataTable);
    dataTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel, dataTableScrollPane);
    rightPane.setDividerLocation(50);
    rightPane.setEnabled(false);
    mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
    mainPane.setDividerLocation(optionsPanel.getMinimumSize().width +3);
    mainPane.setEnabled(false);
    this.setTitle("MACD Tracker");
    this.setSize(new Dimension(800, 400));
    this.setMinimumSize(new Dimension(600, 400));
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.addWindowListener(new WindowListener() {
      @Override
      public void windowActivated(WindowEvent e) {}
      @Override
      public void windowClosed(WindowEvent e) {}
      @Override
      public void windowClosing(WindowEvent e) {
        try {
          BufferedWriter bw = new BufferedWriter(new FileWriter(new File("trackedAssets.txt")));
          for(int i = 0; i < trackedAssets.size(); i++) {
            bw.write(trackedAssets.get(i));
            if(i != trackedAssets.size() - 1) {
              bw.newLine();
            }
          }
          bw.close();
        } catch (IOException e1) {}
        try {
          BufferedWriter bw = new BufferedWriter(new FileWriter(new File("MACDTrackerSettings.txt")));
          bw.write(buyColor.getRed() + ",");
          bw.write(buyColor.getGreen() + ",");
          bw.write(buyColor.getBlue() + ",");
          bw.write(sellColor.getRed() + ",");
          bw.write(sellColor.getGreen() + ",");
          bw.write(sellColor.getBlue() + "");
          bw.close();
        } catch (IOException e1) {}
      }
      @Override
      public void windowDeactivated(WindowEvent arg0) {}
      @Override
      public void windowDeiconified(WindowEvent arg0) {}
      @Override
      public void windowIconified(WindowEvent arg0) {}
      @Override
      public void windowOpened(WindowEvent arg0) {}    
    });
    this.setLocationRelativeTo(null);
    this.add(mainPane);
    this.setVisible(true);
  }
  
  private JButton makeAddButton() {
    JButton button = new JButton("Add an Asset");
    button.setFocusPainted(false);
    button.setBorderPainted(true);
    button.setContentAreaFilled(false);
    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getMinimumSize().height));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(!addWindowOpen) {
          addWindow = new AddWindow();
          addWindowOpen = true;
        }
        else {
          addWindow.setVisible(true);
        }
      }
    });
    return button;
  }
  
  private class AddWindow extends JFrame {
    private JPanel addPanel;
    private JButton closeButton, inputButton;
    private JTextField textField;
    private JLabel statusLabel, fieldLabel;
    private GridBagConstraints addPanelConstraints;
    private JTextPane outputLog;
    private StyledDocument outputLogDoc;
    private Style outputLogStyle;
    private JScrollPane outputLogScrollPane;
    
    AddWindow() {
      this.setTitle("MACD Tracker: Add Asset");
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.addWindowListener(new WindowListener() {
        @Override
        public void windowActivated(WindowEvent arg0) {}
        @Override
        public void windowClosed(WindowEvent arg0) {}
        @Override
        public void windowClosing(WindowEvent arg0) {
          addWindowOpen = false;
        }
        @Override
        public void windowDeactivated(WindowEvent arg0) {}
        @Override
        public void windowDeiconified(WindowEvent arg0) {}
        @Override
        public void windowIconified(WindowEvent arg0) {}
        @Override
        public void windowOpened(WindowEvent arg0) {}
      });
      this.setSize(new Dimension(400, 300));
      this.setLocationRelativeTo(null);
      this.setResizable(false);
      addPanel = new JPanel(new GridBagLayout());
      addPanel.setMinimumSize(new Dimension(400,400));
      this.add(addPanel);
      textField = new JTextField(10);
      textField.setEditable(true);
      textField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JTextField source = (JTextField)e.getSource();
          String text = source.getText().trim().toUpperCase();
          if(!text.equals("")) {
            try {
              outputLogDoc.insertString(outputLogDoc.getLength(), "Searching for '" + text + "'...\n", null);
            } catch (BadLocationException e1) {}
            try {
              int i;
              for(i = 0; i < trackedAssets.size(); i++) {
                if(trackedAssets.get(i).compareTo(text) == 0) {
                  i = -1;
                  break;
                }
                else if (trackedAssets.get(i).compareTo(text) > 0) {
                  break;
                }
              }
              if(i != -1) {
                Asset asset = new Asset(text);
                trackedAssets.add(i, text);
                assetData.add(i, asset);
                trackedAssetsListModel.insertElementAt(text, i);
                trackedAssetsList.setSelectedIndex(i);
                StyleConstants.setForeground(outputLogStyle, new Color(122, 200, 122));
                try {
                  outputLogDoc.insertString(outputLogDoc.getLength(), "'" + text + "' successfully added to tracking.\n\n", outputLogStyle);
                } catch (BadLocationException e1) {}
              }
              else {
                StyleConstants.setForeground(outputLogStyle, new Color(230, 90, 90));
                try {
                  outputLogDoc.insertString(outputLogDoc.getLength(), "Error: The input stock is already being tracked.\n\n", outputLogStyle);
                } catch (BadLocationException e1) {}
              }
            } catch (IOException j) {
              StyleConstants.setForeground(outputLogStyle, new Color(230, 90, 90));
              try {
                outputLogDoc.insertString(outputLogDoc.getLength(), "Error: Could not locate data for '" + text + "'. Please check the entered code, formating, and your internet connection.\n\n", outputLogStyle);
              } catch (BadLocationException e1) {}
            }
          }
          textField.setText(null);
        }
      });
      inputButton = new JButton("Track Asset");
      inputButton.setFocusPainted(false);
      inputButton.setContentAreaFilled(false);
      inputButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          textField.getText();
          textField.postActionEvent();
        }
      });
      closeButton = new JButton("Close");
      closeButton.setFocusPainted(false);
      closeButton.setContentAreaFilled(false);
      closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          dispose();
          addWindowOpen = false;
        }
      });
      addPanelConstraints = new GridBagConstraints();
      fieldLabel = new JLabel("Stock Code: ");
      statusLabel = new JLabel();
      statusLabel.setText("Null");
      outputLog = new JTextPane();
      outputLog.setEditable(false);
      outputLogDoc = outputLog.getStyledDocument();
      outputLogStyle = outputLog.addStyle("removeStyle", null);
      try {
        outputLogDoc.insertString(0, "Please enter the ticker symbol of the stock you would like to track in the field above, then press the 'Track Asset' button to add it to tracking. ", null);
        outputLogDoc.insertString(outputLogDoc.getLength(), "Please double check The Wall Street Journal (www.wsj.com/market-data/quotes) for the proper code if the stock you wish to track is not found.\n\n", null);
      } catch (BadLocationException e1) {}
      outputLogScrollPane = new JScrollPane(outputLog);
      addPanelConstraints.anchor = GridBagConstraints.CENTER;
      addPanelConstraints.fill = GridBagConstraints.NONE;
      addPanelConstraints.insets = new Insets(5,5,5,5);
      addPanelConstraints.weightx = .2;
      addPanelConstraints.weighty = 0;
      addPanelConstraints.gridwidth = 1;
      addPanelConstraints.gridheight = 1;
      addPanelConstraints.gridx = 0;
      addPanelConstraints.gridy = 0;
      addPanel.add(fieldLabel, addPanelConstraints);
      addPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
      addPanelConstraints.weightx = 1;
      addPanelConstraints.gridx = 1;
      addPanel.add(textField, addPanelConstraints);
      addPanelConstraints.insets = new Insets(0,5,5,5);
      addPanelConstraints.gridx = 0;
      addPanelConstraints.gridy = 1;
      addPanelConstraints.gridwidth = 2;
      addPanel.add(inputButton, addPanelConstraints);
      addPanelConstraints.fill = GridBagConstraints.BOTH;
      addPanelConstraints.weighty = .6;
      addPanelConstraints.gridx = 0;
      addPanelConstraints.gridy = 2;
      addPanel.add(outputLogScrollPane, addPanelConstraints);
      addPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
      addPanelConstraints.weighty = 0;
      addPanelConstraints.gridy = 3;
      addPanel.add(closeButton, addPanelConstraints);
      this.setVisible(true);
    }
  }
  
  private JButton makeRemoveButton() {
    JButton button = new JButton("Remove an Asset");
    button.setFocusPainted(false);
    button.setBorderPainted(true);
    button.setContentAreaFilled(false);
    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getMinimumSize().height));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(!removeWindowOpen) {
          removeWindow = new RemoveWindow();
          removeWindowOpen = true;
        }
        else {
          removeWindow.setVisible(true);
        }
      }
    });
    return button;
  }
  
  private class RemoveWindow extends JFrame {
    private JPanel removePanel;
    private GridBagConstraints removePanelConstraints;
    private JLabel titleLabel;
    private JButton closeButton, removeAssetButton;
    private JList<String> removeAssetsList;
    private JScrollPane removeAssetsScrollPane;
    
    RemoveWindow() {
      this.setTitle("MACD Tracker: Remove Asset");
      this.setSize(new Dimension(400, 300));
      this.setResizable(false);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.addWindowListener(new WindowListener() {
        @Override
        public void windowActivated(WindowEvent arg0) {}
        @Override
        public void windowClosed(WindowEvent arg0) {}
        @Override
        public void windowClosing(WindowEvent arg0) {
          removeWindowOpen = false;
        }
        @Override
        public void windowDeactivated(WindowEvent arg0) {}
        @Override
        public void windowDeiconified(WindowEvent arg0) {}
        @Override
        public void windowIconified(WindowEvent arg0) {}
        @Override
        public void windowOpened(WindowEvent arg0) {}
      });
      removeAssetsList = new JList<String>(trackedAssetsListModel);
      removeAssetsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      removeAssetsScrollPane = new JScrollPane(removeAssetsList);
      removeAssetsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      titleLabel = new JLabel("Tracked Assets:");
      removeAssetButton = new JButton("Remove Selected Asset");
      removeAssetButton.setFocusPainted(false);
      removeAssetButton.setContentAreaFilled(false);
      removeAssetButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          if(removeAssetsList.getSelectedIndex() != -1) {
            trackedAssets.remove(removeAssetsList.getSelectedIndex());
            assetData.remove(removeAssetsList.getSelectedIndex());
            trackedAssetsListModel.remove(removeAssetsList.getSelectedIndex());
            removeAssetsList.setSelectedIndex(-1);
          }
        }
      });
      closeButton = new JButton("Close");
      closeButton.setFocusPainted(false);
      closeButton.setContentAreaFilled(false);
      closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          dispose();
          removeWindowOpen = false;
        }
      });
      removePanel = new JPanel(new GridBagLayout());
      removePanelConstraints = new GridBagConstraints();
      removePanelConstraints.insets = new Insets(5,5,5,5);
      removePanelConstraints.fill = GridBagConstraints.BOTH;
      removePanelConstraints.gridwidth = 2;
      removePanelConstraints.weightx = 1;
      removePanelConstraints.gridx = 0;
      removePanelConstraints.gridy = 0;
      removePanel.add(titleLabel, removePanelConstraints);
      removePanelConstraints.insets = new Insets(0,5,5,5);
      removePanelConstraints.weighty = 1;
      removePanelConstraints.gridy = 1;
      removePanel.add(removeAssetsScrollPane, removePanelConstraints);
      removePanelConstraints.gridwidth = 1;
      removePanelConstraints.weighty = 0;
      removePanelConstraints.gridy = 2;
      removePanel.add(removeAssetButton, removePanelConstraints);
      removePanelConstraints.gridx = 1;
      removePanel.add(closeButton, removePanelConstraints);
      this.add(removePanel);
      this.setVisible(true);
    }
  }
  
  private JButton makeInfoButton() {
    JButton button = new JButton("Program Information");
    button.setFocusPainted(false);
    button.setBorderPainted(true);
    button.setContentAreaFilled(false);
    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getMinimumSize().height));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(!infoWindowOpen) {
          infoWindow = new InfoWindow();
          infoWindowOpen = true;
        }
        else {
          infoWindow.setVisible(true);
        }
      }
    });
    return button;
  }

  private class InfoWindow extends JFrame {
    private JPanel infoPanel;
    private GridBagConstraints infoPanelConstraints;
    private JTextPane infoTextPane;
    private StyledDocument infoDoc;
    private Style infoTextPaneStyle;
    private JScrollPane infoScrollPane;
    private JButton closeButton;
    
    public InfoWindow() {
      this.setTitle("MACD Tracker: Program Information");
      this.setSize(new Dimension(400, 300));
      this.setResizable(false);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.addWindowListener(new WindowListener() {
        @Override
        public void windowActivated(WindowEvent arg0) {}
        @Override
        public void windowClosed(WindowEvent arg0) {}
        @Override
        public void windowClosing(WindowEvent arg0) {
          infoWindowOpen = false;
        }
        @Override
        public void windowDeactivated(WindowEvent arg0) {}
        @Override
        public void windowDeiconified(WindowEvent arg0) {}
        @Override
        public void windowIconified(WindowEvent arg0) {}
        @Override
        public void windowOpened(WindowEvent arg0) {}
      });
      infoTextPane = new JTextPane();
      infoTextPane.setEditable(false);
      infoTextPaneStyle = infoTextPane.addStyle("infoStyle", null);
      infoDoc = infoTextPane.getStyledDocument();
      try {
        infoDoc.insertString(0, "Welcome to MACD Tracker. ", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "This program calculates and tracks the MACD indicator for stocks and gives 'buy' and 'sell' signals accordingly.\n\n", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "When there is a buy signal for the last day of available financial data, the stock's name will be highlighted within the asset list in the buy signal color.\n", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "        The current buy signal color is ", infoTextPaneStyle);
        StyleConstants.setForeground(infoTextPaneStyle, buyColor);
        infoDoc.insertString(infoDoc.getLength(), "BUY COLOR\n", infoTextPaneStyle);
        StyleConstants.setForeground(infoTextPaneStyle, Color.BLACK);
        infoDoc.insertString(infoDoc.getLength(), "The same is true for a sell signal and the sell signal color.\n", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "        The current sell signal color is ", infoTextPaneStyle);
        StyleConstants.setForeground(infoTextPaneStyle, sellColor);
        infoDoc.insertString(infoDoc.getLength(), "SELL COLOR\n", infoTextPaneStyle);
        StyleConstants.setForeground(infoTextPaneStyle, Color.BLACK);
        infoDoc.insertString(infoDoc.getLength(), "These colors can be changed in the settings menu.\n\n", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "Historical data, including past buy and sell signals can be viewed by selecting a stock from the asset list. ", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "MACD Tracker shows up to a year worth of historical data.\n\n", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "Buy and Sell signals made by this program are purely recommendations based off of the MACD indicator. ", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "No program can with 100% certainty predict the future of the stock market. ", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "Please invest responsibly.\n\n", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "DO NOT directly edit the 'trackedAssets.txt' and 'MACDTrackerSettings.txt' files. ", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "Doing so may cause the program to behave unintendedly.\n\n", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "All financial data used by MACD Tracker is obtained from from The Wall Street Journal (www.wsj.com/market-data/quotes).", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), " If you are unable to load an asset, please double check its stock code at The Wall Street Journal's page.\n\n", infoTextPaneStyle);
        infoDoc.insertString(infoDoc.getLength(), "Any missing data is filled in by averageing the values directly before and after the missing data.", infoTextPaneStyle);
      } catch (BadLocationException e) {}
      infoTextPane.setCaretPosition(0);
      infoScrollPane = new JScrollPane(infoTextPane);
      closeButton = new JButton("Close");
      closeButton.setFocusPainted(false);
      closeButton.setContentAreaFilled(false);
      closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          dispose();
          infoWindowOpen = false;
        }
      });
      infoPanel = new JPanel(new GridBagLayout());
      infoPanelConstraints = new GridBagConstraints();
      infoPanelConstraints.weightx = 1;
      infoPanelConstraints.weighty = 1;
      infoPanelConstraints.insets = new Insets(5,5,5,5);
      infoPanelConstraints.fill = GridBagConstraints.BOTH;
      infoPanelConstraints.gridx = 0;
      infoPanelConstraints.gridy = 0;
      infoPanel.add(infoScrollPane, infoPanelConstraints);
      infoPanelConstraints.insets = new Insets(0,5,5,5);
      infoPanelConstraints.gridy = 1;
      infoPanelConstraints.weighty = 0;
      infoPanel.add(closeButton, infoPanelConstraints);
      this.add(infoPanel);
      this.setVisible(true);
    }
  }
  
  private JButton makeSettingsButton() {
    JButton button = new JButton("Settings");
    button.setFocusPainted(false);
    button.setBorderPainted(true);
    button.setContentAreaFilled(false);
    button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getMinimumSize().height));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(!settingsWindowOpen) {
          settingsWindow = new SettingsWindow();
          settingsWindowOpen = true;
        }
        else {
          settingsWindow.setVisible(true);
        }
      }
    });
    return button;
  }
  
  private class SettingsWindow extends JFrame {
    private class DocFilter extends DocumentFilter {
      private int maxCharacters = 3;
      @Override
      public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
        String text = fb.getDocument().getText(0, fb.getDocument().getLength());
        text += str;
        if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters && text.matches("[0-9]+")) {
          super.replace(fb, offs, length, str, a);
        } else {}
      }
      
      @Override
      public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
        String text = fb.getDocument().getText(0, fb.getDocument().getLength());
        text += str;
        if ((fb.getDocument().getLength() + str.length()) <= maxCharacters && text.matches("[0-9]+")) {
          super.insertString(fb, offs, str, a);
        } else {}
      }
    }
    
    private JPanel settingsPanel, buyTest, sellTest;
    private GridBagConstraints settingsPanelConstraints;
    private JTextField rb, bb, gb, rs, bs, gs;
    private AbstractDocument rbDoc, bbDoc, gbDoc, rsDoc, bsDoc, gsDoc;
    private JButton closeButton, buyButton, sellButton, buyTestButton, sellTestButton;
    private JLabel buyLabel, sellLabel, rbLabel, bbLabel, gbLabel, rsLabel, bsLabel, gsLabel;
    
    public SettingsWindow() {
      this.setTitle("MACD Tracker: Settings");
      this.setSize(new Dimension(400, 300));
      this.setResizable(false);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      this.addWindowListener(new WindowListener() {
        @Override
        public void windowActivated(WindowEvent arg0) {}
        @Override
        public void windowClosed(WindowEvent arg0) {}
        @Override
        public void windowClosing(WindowEvent arg0) {
          settingsWindowOpen = false;
        }
        @Override
        public void windowDeactivated(WindowEvent arg0) {}
        @Override
        public void windowDeiconified(WindowEvent arg0) {}
        @Override
        public void windowIconified(WindowEvent arg0) {}
        @Override
        public void windowOpened(WindowEvent arg0) {}
      });
      
      buyLabel = new JLabel("Buy Signal Color:");
      rbLabel = new JLabel("R:");
      gbLabel = new JLabel("G:");
      bbLabel = new JLabel("B:");
      rb = new JTextField(3);
      rb.setText("" + buyColor.getRed());
      rbDoc = (AbstractDocument)rb.getDocument();
      rbDoc.setDocumentFilter(new DocFilter());
      gb = new JTextField(3);
      gb.setText("" + buyColor.getGreen());
      gbDoc = (AbstractDocument)gb.getDocument();
      gbDoc.setDocumentFilter(new DocFilter());
      bb = new JTextField(3);
      bb.setText("" + buyColor.getBlue());
      bbDoc = (AbstractDocument)bb.getDocument();
      bbDoc.setDocumentFilter(new DocFilter());
      buyTest = new JPanel();
      buyTest.setBackground(buyColor);
      buyTestButton = new JButton("Test Color");
      buyTestButton.setFocusPainted(false);
      buyTestButton.setContentAreaFilled(false);
      buyTestButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          int r, b, g;
          r = Integer.parseInt(rb.getText());
          if(r < 0) {
            r = 0;
          }
          else if (r > 255) {
            r = 255;
          }
          g = Integer.parseInt(gb.getText());
          if(g < 0) {
            g = 0;
          }
          else if (g > 255) {
            g = 255;
          }
          b = Integer.parseInt(bb.getText());
          if(b < 0) {
            b = 0;
          }
          else if (b > 255) {
            b = 255;
          }
          buyTest.setBackground(new Color(r, g, b));
        }
      });
      buyButton = new JButton("Change Color");
      buyButton.setFocusPainted(false);
      buyButton.setContentAreaFilled(false);
      buyButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          int r, b, g;
          r = Integer.parseInt(rb.getText());
          if(r < 0) {
            r = 0;
          }
          else if (r > 255) {
            r = 255;
          }
          g = Integer.parseInt(gb.getText());
          if(g < 0) {
            g = 0;
          }
          else if (g > 255) {
            g = 255;
          }
          b = Integer.parseInt(bb.getText());
          if(b < 0) {
            b = 0;
          }
          else if (b > 255) {
            b = 255;
          }
          buyColor = new Color(r, g, b);
          buyTest.setBackground(buyColor);
          trackedAssetsList.setCellRenderer(new AssetListCellRenderer());
        }
      });
      sellLabel = new JLabel("Sell Signal Color:");
      rsLabel = new JLabel("R:");
      gsLabel = new JLabel("G:");
      bsLabel = new JLabel("B:");
      rs = new JTextField(3);
      rs.setText("" + sellColor.getRed());
      rsDoc = (AbstractDocument)rs.getDocument();
      rsDoc.setDocumentFilter(new DocFilter());
      gs = new JTextField(3);
      gs.setText("" + sellColor.getGreen());
      gsDoc = (AbstractDocument)gs.getDocument();
      gsDoc.setDocumentFilter(new DocFilter());
      bs = new JTextField(3);
      bs.setText("" + sellColor.getBlue());
      bsDoc = (AbstractDocument)bs.getDocument();
      bsDoc.setDocumentFilter(new DocFilter());
      sellTest = new JPanel();
      sellTest.setBackground(sellColor);
      sellTestButton = new JButton("Test Color");
      sellTestButton.setFocusPainted(false);
      sellTestButton.setContentAreaFilled(false);
      sellTestButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          int r, b, g;
          r = Integer.parseInt(rs.getText());
          if(r < 0) {
            r = 0;
          }
          else if (r > 255) {
            r = 255;
          }
          g = Integer.parseInt(gs.getText());
          if(g < 0) {
            g = 0;
          }
          else if (g > 255) {
            g = 255;
          }
          b = Integer.parseInt(bs.getText());
          if(b < 0) {
            b = 0;
          }
          else if (b > 255) {
            b = 255;
          }
          sellTest.setBackground(new Color(r, g, b));
        }
      });
      sellButton = new JButton("Change Color");
      sellButton.setFocusPainted(false);
      sellButton.setContentAreaFilled(false);
      sellButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          int r, b, g;
          r = Integer.parseInt(rs.getText());
          if(r < 0) {
            r = 0;
          }
          else if (r > 255) {
            r = 255;
          }
          g = Integer.parseInt(gs.getText());
          if(g < 0) {
            g = 0;
          }
          else if (g > 255) {
            g = 255;
          }
          b = Integer.parseInt(bs.getText());
          if(b < 0) {
            b = 0;
          }
          else if (b > 255) {
            b = 255;
          }
          sellColor = new Color(r, g, b);
          sellTest.setBackground(sellColor);
          trackedAssetsList.setCellRenderer(new AssetListCellRenderer());
        }
      });
      closeButton = new JButton("Close");
      closeButton.setFocusPainted(false);
      closeButton.setContentAreaFilled(false);
      closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          dispose();
          settingsWindowOpen = false;
        }
      });
      settingsPanel = new JPanel(new GridBagLayout());
      settingsPanelConstraints = new GridBagConstraints();
      settingsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
      settingsPanelConstraints.weightx = 1;
      settingsPanelConstraints.insets = new Insets(5,5,5,5);
      settingsPanelConstraints.gridwidth = 6;
      settingsPanelConstraints.gridx = 0;
      settingsPanelConstraints.gridy = 0;
      settingsPanel.add(buyLabel, settingsPanelConstraints);
      settingsPanelConstraints.insets = new Insets(0,5,5,5);
      settingsPanelConstraints.gridwidth = 1;
      settingsPanelConstraints.gridy = 1;
      settingsPanel.add(rbLabel, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 1;
      settingsPanel.add(rb, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 2;
      settingsPanel.add(gbLabel, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 3;
      settingsPanel.add(gb, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 4;
      settingsPanel.add(bbLabel, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 5;
      settingsPanel.add(bb, settingsPanelConstraints);
      settingsPanelConstraints.gridwidth = 2;
      settingsPanelConstraints.gridy = 2;
      settingsPanelConstraints.gridx = 0;
      settingsPanel.add(buyTestButton, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 2;
      settingsPanel.add(buyTest, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 4;
      settingsPanel.add(buyButton, settingsPanelConstraints);
      settingsPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
      settingsPanelConstraints.gridx = 0;
      settingsPanelConstraints.gridy = 3;
      settingsPanel.add(sellLabel, settingsPanelConstraints);
      settingsPanelConstraints.gridwidth = 1;
      settingsPanelConstraints.gridy = 4;
      settingsPanel.add(rsLabel, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 1;
      settingsPanel.add(rs, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 2;
      settingsPanel.add(gsLabel, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 3;
      settingsPanel.add(gs, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 4;
      settingsPanel.add(bsLabel, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 5;
      settingsPanel.add(bs, settingsPanelConstraints);
      settingsPanelConstraints.gridwidth = 2;
      settingsPanelConstraints.gridy = 5;
      settingsPanelConstraints.gridx = 0;
      settingsPanel.add(sellTestButton, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 2;
      settingsPanel.add(sellTest, settingsPanelConstraints);
      settingsPanelConstraints.gridx = 4;
      settingsPanel.add(sellButton, settingsPanelConstraints);
      settingsPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
      settingsPanelConstraints.gridx = 0;
      settingsPanelConstraints.gridy = 6;
      settingsPanel.add(closeButton, settingsPanelConstraints);
      this.add(settingsPanel);
      this.pack();
      this.setSize(new Dimension(400, (int)this.getSize().getHeight()));
      this.setVisible(true);
    }
  }
  
  private void updateTable(int index) {
    dataTable.setValueAt(null, -1, -1);
    infoStockNameLabelField.setText("");
    infoDateLabelField.setText("");
    infoLastCloseLabelField.setText("");
    infoActionLabelField.setText("---");
    infoMacd1LabelField.setText("");
    infoSignalLabelField.setText("");
    infoPrevMacd1LabelField.setText("");
    infoPrevSignalLabelField.setText("");
    if (index >= 0) {
      infoStockNameLabelField.setText(trackedAssets.get(index));
      infoDateLabelField.setText(assetData.size() > 0 ? assetData.get(index).getData().size() > 0 ? assetData.get(index).getData().get(0).getDate() : null : null);
      infoLastCloseLabelField.setText(assetData.size() > 0 ? assetData.get(index).getData().size() > 0 ? assetData.get(index).getData().get(0).getClose().setScale(2, BigDecimal.ROUND_HALF_UP).toString() : null : null);
      infoActionLabelField.setText(assetData.size() > 0 ? (assetData.get(index).getAction() != 2 ? (assetData.get(index).getAction() == 1 ? "Buy" : (assetData.get(index).getAction() == -1 ? "Sell" : "---")) : "Not enough data") : null);
      infoMacd1LabelField.setText(assetData.size() > 0 ? (assetData.get(index).getAction() != 2 ? assetData.get(index).getMacd1Data()[0].setScale(3, BigDecimal.ROUND_HALF_UP).toString() : "Not enough data") : null);
      infoSignalLabelField.setText(assetData.size() > 0 ? (assetData.get(index).getAction() != 2 ? assetData.get(index).getSignalData()[0].setScale(3, BigDecimal.ROUND_HALF_UP).toString() : "Not enough data") : null);
      infoPrevMacd1LabelField.setText(assetData.size() > 0 ? (assetData.get(index).getAction() != 2 ? assetData.get(index).getMacd1Data()[1].setScale(3, BigDecimal.ROUND_HALF_UP).toString() : "Not enough data") : null);
      infoPrevSignalLabelField.setText(assetData.size() > 0 ? (assetData.get(index).getAction() != 2 ? assetData.get(index).getSignalData()[1].setScale(3, BigDecimal.ROUND_HALF_UP).toString() : "Not enough data") : null);
      for (int i = 0; i < (assetData.size() > 0 ? assetData.get(index).getData().size() : 0); i++) {
        for (int j = 0; j < 9; j++) {
          switch (j) {
            case 0: 
              dataTable.setValueAt(assetData.get(index).getData().get(i).getDate(), i, j);
              break;
            case 1:
              dataTable.setValueAt(assetData.get(index).getData().get(i).getOpen().setScale(2, BigDecimal.ROUND_HALF_UP), i, j);
              break;
            case 2:
              dataTable.setValueAt(assetData.get(index).getData().get(i).getLow().setScale(2, BigDecimal.ROUND_HALF_UP), i, j);
              break;
            case 3:
              dataTable.setValueAt(assetData.get(index).getData().get(i).getHigh().setScale(2, BigDecimal.ROUND_HALF_UP), i, j);
              break;
            case 4:
              dataTable.setValueAt(assetData.get(index).getData().get(i).getClose().setScale(2, BigDecimal.ROUND_HALF_UP), i, j);
              break;
            case 5:
              dataTable.setValueAt(assetData.get(index).getData().get(i).getVolume(), i, j);
              break;
            case 6:
              try {
                dataTable.setValueAt(assetData.get(index).getMacd1Data()[i].setScale(3, BigDecimal.ROUND_HALF_UP), i, j);
              } catch (ArrayIndexOutOfBoundsException e) {
                dataTable.setValueAt("---", i, j);
              } catch (NullPointerException e) {
                dataTable.setValueAt("---", i, j);
              }
              break;
            case 7:
              try {
                dataTable.setValueAt(assetData.get(index).getSignalData()[i].setScale(3, BigDecimal.ROUND_HALF_UP), i, j);
              } catch (ArrayIndexOutOfBoundsException e) {
                dataTable.setValueAt("---", i, j);
              } catch (NullPointerException e) {
                dataTable.setValueAt("---", i, j);
              }
              break;
            case 8:
              try {
                dataTable.setValueAt((assetData.get(index).getMacd1Data()[i].compareTo(assetData.get(index).getSignalData()[i]) == -1 && 
                assetData.get(index).getMacd1Data()[i+1].compareTo(assetData.get(index).getSignalData()[i + 1]) == 1) ? "Sell" : 
                  ((assetData.get(index).getMacd1Data()[i].compareTo(assetData.get(index).getSignalData()[i]) == 1 && 
                      assetData.get(index).getMacd1Data()[i+1].compareTo(assetData.get(index).getSignalData()[i + 1]) == -1) ? "Buy" : "---"), i, j);
              }
              catch (ArrayIndexOutOfBoundsException e) {
                dataTable.setValueAt("---", i, j);
              }
              catch (NullPointerException e) {
                dataTable.setValueAt("---", i, j);
              }
              break;
          }
        }
      }
    }
    if(dataTableScrollPane != null) {
      int temp = rightPane.getDividerLocation();
      dataTableScrollPane = new JScrollPane(dataTable);
      rightPane.setBottomComponent(dataTableScrollPane);
      rightPane.setDividerLocation(temp);
    }    
  }
}

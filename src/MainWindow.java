import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableModel;

import org.json.*;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTable;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JEditorPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;


public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	// DATABASE VARIABLES
	private String[] columnNames = {"Miasto",
							"Temperatura",
							"Typ pogody",
							"Opis",
							"Wilgoæ",
							"Prêdkoœæ wiatru",
							"Ciœnienie"};
	private String data[][] = new String[1024][7];
	private int dbSize = WeatherDBClass.countRowsDb();
	
	private boolean firstInternetError = true;
	
	private JTextField miastoField;
	private JTable table = new JTable(data, columnNames);
	private JLabel netLbl = new JLabel("STATUS PO\u0141\u0104CZENIA Z INTERNETEM: OK");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	
	public class WeatherClass{
		public String getTemp() {
			return temp;
		}
		public String getWeatherType() {
			return weatherType;
		}
		public String getDescription() {
			return description;
		}
		public String getHumidity() {
			return humidity;
		}
		public String getWind() {
			return wind;
		}
		public String getPressure() {
			return pressure;
		}
		private String jsonStr;
		private String temp;
		private String weatherType;
		private String description;
		private String humidity;
		private String wind;
		private String pressure;
		public WeatherClass(String url){
			try {
				URL testUrl = new URL(url);
				BufferedReader in = new BufferedReader(new InputStreamReader(testUrl.openStream()));
				jsonStr = in.readLine();
				in.close();
				JSONObject obj = new JSONObject(jsonStr);
				
				
				weatherType = obj.getJSONArray("weather").getJSONObject(0).getString("main");
				description = obj.getJSONArray("weather").getJSONObject(0).getString("description");
				temp = String.valueOf(Double.parseDouble(obj.getJSONObject("main").getString("temp")) - 273.15);
				humidity = obj.getJSONObject("main").getString("humidity");
				wind = obj.getJSONObject("wind").getString("speed");
				pressure = obj.getJSONObject("main").getString("pressure");
				
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		public String print(){
			
			return temp+" "+weatherType+" "+description+" "+humidity+" "+wind+" "+pressure;
		}
	}
	
	public MainWindow() throws IOException {
		fillTable();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(450, 100, 1000, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		// AUTO WEATHER THREAD
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		exec.scheduleWithFixedDelay(new Runnable() {
		  @Override
		  public void run() {
			  	String APPID = "014b5d4258bc8726f9bda9865f5a7a66";
				WeatherClass wth = new WeatherClass("http://api.openweathermap.org/data/2.5/weather?q="+"Wroclaw"+",pl&APPID="+APPID);
				
				WeatherDBClass.addToDb("Wroclaw", wth.getTemp(), wth.getWeatherType(), wth.getDescription(), wth.getHumidity(), wth.getWind(), wth.getPressure());
				dbSize++;
				data[dbSize-1][0] = "Wroclaw";
				data[dbSize-1][1] = wth.getTemp();
				data[dbSize-1][2] = wth.getWeatherType();
				data[dbSize-1][3] = wth.getDescription();
				data[dbSize-1][4] = wth.getHumidity();
				data[dbSize-1][5] = wth.getWind();
				data[dbSize-1][6] = wth.getPressure();
				table.repaint();
		  }
		}, 0, 25, TimeUnit.SECONDS);
		

		
		netLbl.setForeground(Color.GREEN);
		netLbl.setBounds(624, 325, 259, 14);
		contentPane.add(netLbl);
		
		// INTERNET CONNECTION THREAD
		ScheduledExecutorService exec2 = Executors.newScheduledThreadPool(2);
		exec2.scheduleWithFixedDelay(new Runnable() {
		  @Override
		  public void run() {
			  	if(!netIsAvailable()) {
			  		if(firstInternetError) {
				  		JFrame frame = new JFrame();
				  		JOptionPane.showMessageDialog(frame,
				  			    "Nie uda³o siê po³¹czyæ z openweathermap.",
				  			    "Sprawdz po³¹czenie z internetem.",
				  			    JOptionPane.WARNING_MESSAGE);
				  				netLbl.setForeground(Color.RED);
				  				netLbl.setText("STATUS PO\u0141\u0104CZENIA Z INTERNETEM: B£¥D");
			  		}
			  	}else {
	  				netLbl.setForeground(Color.GREEN);
	  				netLbl.setText("STATUS PO\u0141\u0104CZENIA Z INTERNETEM: OK");
			  	}
		  }
		}, 0, 5, TimeUnit.SECONDS);
		
		
		
		JButton btnDodaj = new JButton("Dodaj");
		btnDodaj.setBounds(266, 355, 97, 25);
		contentPane.add(btnDodaj);
		new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(12, 13, 958, 300);
		
		contentPane.add(scrollPane);
		
		miastoField = new JTextField();
		miastoField.setBounds(32, 353, 222, 28);
		contentPane.add(miastoField);
		miastoField.setColumns(10);
		
		JLabel lblMiasto = new JLabel("Miasto:");
		lblMiasto.setBounds(22, 324, 56, 16);
		contentPane.add(lblMiasto);
		
		JButton btnUsu = new JButton("Usu\u0144");
		btnUsu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WeatherDBClass.removeFromDb(table.getSelectedRow());
				for(int i = 0; i < 7; i++) {
					data[table.getSelectedRow()][i] = "USUNIETO";
				}
				table.repaint();
			}
		});
		btnUsu.setBounds(881, 356, 89, 23);
		contentPane.add(btnUsu);
		
		JButton btnUsuWszystkie = new JButton("Usu\u0144 wszystkie");
		btnUsuWszystkie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < dbSize; i++) {
					for(int j = 0; j < 7; j++) {
						data[i][j] = "";
					}
				}
				WeatherDBClass.removeAllFromDb();
				dbSize = WeatherDBClass.countRowsDb();
				table.repaint();
				
			}
		});
		btnUsuWszystkie.setBackground(Color.RED);
		btnUsuWszystkie.setBounds(851, 384, 119, 23);
		contentPane.add(btnUsuWszystkie);
		
		

		btnDodaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String APPID = "014b5d4258bc8726f9bda9865f5a7a66";
				WeatherClass wth = new WeatherClass("http://api.openweathermap.org/data/2.5/weather?q="+miastoField.getText()+",pl&APPID="+APPID);
				
				if(wth.getTemp() == null) {
					JFrame frame = new JFrame();
			  		JOptionPane.showMessageDialog(frame,
			  			    "Blednie wpisana nazwa miasta.",
			  			    "Sprawdz nazwe wpisanego miasta.",
			  			    JOptionPane.WARNING_MESSAGE);
			  		return;
				}
				
				WeatherDBClass.addToDb(miastoField.getText(), wth.getTemp(), wth.getWeatherType(), wth.getDescription(), wth.getHumidity(), wth.getWind(), wth.getPressure());
				dbSize++;
				data[dbSize-1][0] = miastoField.getText();
				data[dbSize-1][1] = wth.getTemp();
				data[dbSize-1][2] = wth.getWeatherType();
				data[dbSize-1][3] = wth.getDescription();
				data[dbSize-1][4] = wth.getHumidity();
				data[dbSize-1][5] = wth.getWind();
				data[dbSize-1][6] = wth.getPressure();
				table.repaint();
			}
		});
		
		
		//contentPane.add(table);
		
		//String city = "Wroclaw";
		//WeatherClass wth = new WeatherClass("http://api.openweathermap.org/data/2.5/weather?q="+city+",pl&APPID="+APPID);
		
		//WeatherDBClass.addToDb(city, wth.getTemp(), wth.getWeatherType(), wth.getDescription(), wth.getHumidity(), wth.getWind(), wth.getPressure());
		
		
		//
		// skladnia http://api.openweathermap.org/data/2.5/weather?q=Miasto,pl&APPID=XXXXXXXX
		
		URL u= new URL("http://api.openweathermap.org/data/2.5/weather?q=Wroclaw&mode=html&apikey=014b5d4258bc8726f9bda9865f5a7a66");
		JEditorPane ed2=new JEditorPane(u);
		ed2.setBounds(12, 433, 958, 300);
		contentPane.add(ed2);
		
	}
	private static boolean netIsAvailable() {
	    try {
	    	String APPID = "014b5d4258bc8726f9bda9865f5a7a66";
	        final URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+"Wroclaw"+",pl&APPID="+APPID);
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
	        return true;
	    } catch (MalformedURLException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	        return false;
	    }
	}
	
	private void fillTable() {

		for(int i = 0; i < dbSize; i++) {
			data[i][0] = WeatherDBClass.getRow("city", i+1); // city
			data[i][1] = WeatherDBClass.getRow("temp", i+1); // temp
			data[i][2] = WeatherDBClass.getRow("weatherType", i+1); // weathertype
			data[i][3] = WeatherDBClass.getRow("description", i+1); // description
			data[i][4] = WeatherDBClass.getRow("humidity", i+1);; // humidity
			data[i][5] = WeatherDBClass.getRow("wind", i+1);; // wind
			data[i][6] = WeatherDBClass.getRow("pressure", i+1);; // pressure
			for(int j = 0; j < 7; j++) {
				if(data[i][j] == "") data[i][j] = "USUNIETO";
			}
		}
		
	}
}

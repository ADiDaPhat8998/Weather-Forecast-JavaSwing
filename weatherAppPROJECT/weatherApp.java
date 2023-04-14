package weatherAppPROJECT;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

//helper method to retrieve information from API call and add it to a string
class HttpUtil {
    public static String sendGet(String url) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

public class weatherApp {
    private JTextField locationField;
    private JButton searchButton;
    private JLabel location;
    private JPanel myPanel;

    public weatherApp() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //retrieve weather status from the API call using keywords to filter out targeted information
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String location = locationField.getText();
                String apiKey = "3502aea47d579bce282a48789765a691";
                String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey;
                ArrayList<String> temp = new ArrayList<>();
                DecimalFormat dfTwo = new DecimalFormat("0.00");
                DecimalFormat df = new DecimalFormat("0");

                try {
                    String response = HttpUtil.sendGet(apiUrl);
                    JSONObject json = new JSONObject(response);
                    JSONObject main = json.getJSONObject("main");
                    JSONArray weather = json.getJSONArray("weather");
                    JSONObject weatherObj = weather.getJSONObject(0);
                    String description = weatherObj.getString("description");
                    int pressure = main.getInt("pressure");
                    int humidity = main.getInt("humidity");
                    int visibility = json.getInt("visibility");
                    double visibilityM = (visibility/100)/1.609;

                    double tempK = main.getDouble("temp");
                    double feels_likeK = main.getDouble("feels_like");
                    double minK = main.getDouble("temp_min");
                    double maxK = main.getDouble("temp_max");

                    double tempC = Double.parseDouble(dfTwo.format(tempK - 273.15));
                    double feels_likeC = Double.parseDouble(dfTwo.format(feels_likeK - 273.15));
                    double minC = Double.parseDouble(dfTwo.format(minK - 273.15));
                    double maxC = Double.parseDouble(dfTwo.format(maxK - 273.15));

                    double tempF = tempC * 9/5 + 32;
                    double feels_likeF = feels_likeC * 9/5 + 32;
                    double minF = minC * 9/5 + 32;
                    double maxF = maxC * 9/5 + 32;

                    temp.add("Description: "+description+"\n\n");
                    temp.add("Temperature: "+tempC+"°C/"+dfTwo.format(tempF)+"°F\n\n");
                    temp.add("Feels Like: "+feels_likeC+"°C/"+dfTwo.format(feels_likeF)+"°F\n\n");
                    temp.add("Min Temperature: "+minC+"°C/"+dfTwo.format(minF)+"°F\n\n");
                    temp.add("Max Temperature: "+maxC+"°C/"+dfTwo.format(maxF)+"°F\n\n");
                    temp.add("Pressure: "+pressure+"hPa\n\n");
                    temp.add("Humidity: "+humidity+"%\n\n");
                    temp.add("Visibility: "+visibility/100+"km/"+df.format(visibilityM)+"miles\n");
                } catch (Exception ex) {
                    System.out.print("Error: " + ex.getMessage());
                }

                String text = "";
                for(String in : temp){
                    text+=in;
                }
                //produce message dialog showing the weather condition of the location input from user
                JTextArea textArea = new JTextArea(10, 35);
                textArea.setText(text);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                String title = locationField.getText()+" Forecast";
                JOptionPane.showMessageDialog(scrollPane, scrollPane, title,JOptionPane.PLAIN_MESSAGE);
            }
        });
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Weather Forecast");
        frame.setSize(300,100);
        frame.setResizable(false);
        frame.setContentPane(new weatherApp().myPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

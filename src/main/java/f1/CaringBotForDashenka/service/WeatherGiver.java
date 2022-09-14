package f1.CaringBotForDashenka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

    public class WeatherGiver {
        public static void main(String[] args) throws IOException {
            File f = new File("D:\\progg\\Learning\\java\\APICALL1\\src\\text");

            String buf = Give5DaysWeatherString();
            for(String s:CleanUpWeatherString(buf)){
                System.out.println(s);
            }
            HashMap<String,String> currentWeatherMap = new HashMap<>();
            currentWeatherMap = ClearCurrentWeatherString(GiveCurrentWeatherString());
            System.out.println("----------------------");
            System.out.println(GiveCurrentWeatherString());
            Date date = new Date();
            System.out.println(date.toString());
            System.out.println("++++++++++++++++");
            System.out.println(currentWeatherMap.get("temp"));
            System.out.println(currentWeatherMap.get("feels_like"));
            System.out.println(currentWeatherMap.get("speed"));
            System.out.println(currentWeatherMap.get("all"));
            System.out.println("++++++++++++++++");
        }


        public static String giveStringTimeFromData(Date date){
            String dateTimeString = String.valueOf(date);
            String[] dats = dateTimeString.split(" ");
            return dats[3];
        }
        public static HashMap<String,String> Clear5DaysWeatherString(String data) throws JsonProcessingException {
            HashMap<String,String> usfulData = new HashMap<>();
            Date date = new Date();
            String time = giveStringTimeFromData(date);



            return usfulData;
        }
        public static HashMap<String,String> ClearCurrentWeatherString(String data) throws JsonProcessingException {
            HashMap<String,String> usfulData = new HashMap<>();
            ObjectMapper mainMapper = new ObjectMapper();
            JsonNode mainNode = mainMapper.readTree(data).get("main");
            usfulData.put("temp",mainNode.get("temp").toString());
            usfulData.put("feels_like",mainNode.get("feels_like").toString());
            //usfulData.put("",mainNode.get("").toString());

            ObjectMapper windMapper = new ObjectMapper();
            JsonNode windNode = windMapper.readTree(data).get("wind");
            usfulData.put("speed",windNode.get("speed").toString());

            ObjectMapper cloudMapper = new ObjectMapper();
            JsonNode cloudsNode = cloudMapper.readTree(data).get("clouds");
            usfulData.put("all", String.valueOf(cloudsNode.get("all")));

            return usfulData;
        }

        public static String GiveCurrentWeatherString() throws IOException {
            //https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=43.5854823&lon=39.723109&appid=5a5fd7be31dd9215a4cfd17288ef9652&units=metric";

            File file = new File("D:\\progg\\Learning\\java\\APICALL1\\src\\text");
            URL urlObject = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent","Mozila/5.0");
            int responseCode = connection.getResponseCode();
            if(responseCode == 404){
                throw new IllegalArgumentException();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine())!=null){
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        public static String Give5DaysWeatherString() throws IOException {
            String urlString = "http://api.openweathermap.org/data/2.5/forecast?lat=43.5854823&lon=39.723109&appid=5a5fd7be31dd9215a4cfd17288ef9652&units=metric";

            File file = new File("D:\\progg\\Learning\\java\\APICALL1\\src\\text");
            URL urlObject = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent","Mozila/5.0");
            int responseCode = connection.getResponseCode();
            if(responseCode == 404){
                throw new IllegalArgumentException();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine())!=null){
                response.append(inputLine);
            }
            in.close();
            return response.toString();

        }

        public static List<String> CleanUpWeatherString(String data) throws JsonProcessingException {
            List<String> weatherList = new ArrayList<>();
            JsonNode arrNode = new ObjectMapper().readTree(data).get("list");
            if(arrNode.isArray()){
                for(final JsonNode objNode:arrNode ){
                    String forecastTime = objNode.get("dt_txt").toString();
                    if(forecastTime.contains("9:00")){
                        weatherList.add(objNode.toString());
                    }
                }
            }

            return weatherList;
        }
    }

package f1.CaringBotForDashenka.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

    public class WeatherGiver {

        private static String giveStringDayFromData(Date date){
            String dateTimeString = String.valueOf(date);
            String[] dats = dateTimeString.split(" ");
            return dats[2];
        }

        private static String giveStringTimeFromData(Date date){
            String dateTimeString = String.valueOf(date);
            String[] dats = dateTimeString.split(" ");
            return dats[3];
        }
        public static HashMap<String,String> GiveClear5DaysWeatherString() throws IOException {
            List<String> data = CleanUpWeatherString();
            HashMap<String,String> usfulData = new HashMap<>();
            ObjectMapper mainMapper = new ObjectMapper();

            for (int i=0;i<data.size();i++) {
                JsonNode timeNode = mainMapper.readTree(data.get(i)).get("dt_txt");
                String dateTime = timeNode.toString().substring(12,14);
                JsonNode mainNode = mainMapper.readTree(data.get(i)).get("main");
                usfulData.put("temp"+dateTime, mainNode.get("temp").toString());
                usfulData.put("feels_like"+dateTime, mainNode.get("feels_like").toString());

                JsonNode windNode = mainMapper.readTree(data.get(i)).get("wind");
                usfulData.put("speed"+dateTime, windNode.get("speed").toString());

                JsonNode cloudsNode = mainMapper.readTree(data.get(i)).get("clouds");
                usfulData.put("all"+dateTime, String.valueOf(cloudsNode.get("all")));
                var a = 10;
                JsonNode statusNode = mainMapper.readTree(data.get(i)).get("weather").get(0);
                usfulData.put("main"+dateTime, String.valueOf(statusNode.get("main")));
                usfulData.put("description"+dateTime, String.valueOf(statusNode.get("description")));
            }

            return usfulData;
        }
        public static HashMap<String,String> GiveClearCurrentWeatherString() throws IOException {
            String data = GiveCurrentWeatherString();
            HashMap<String,String> usfulData = new HashMap<>();
            ObjectMapper mainMapper = new ObjectMapper();
            JsonNode mainNode = mainMapper.readTree(data).get("main");
            //----------------------------------------------------------
            usfulData.put("temp",mainNode.get("temp").toString());
            usfulData.put("feels_like",mainNode.get("feels_like").toString());

            JsonNode windNode = mainMapper.readTree(data).get("wind");
            usfulData.put("speed",windNode.get("speed").toString());

            JsonNode cloudsNode = mainMapper.readTree(data).get("clouds");
            usfulData.put("all", String.valueOf(cloudsNode.get("all")));

            JsonNode statusNode = mainMapper.readTree(data).get("weather").get(0);
            usfulData.put("main", String.valueOf(statusNode.get("main")));
            usfulData.put("description", String.valueOf(statusNode.get("description")));

            return usfulData;
        }

        private static String GiveCurrentWeatherString() throws IOException {
            //https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
            String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=43.5854823&lon=39.723109&appid=5a5fd7be31dd9215a4cfd17288ef9652&units=metric";

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
        private static String Give5DaysWeatherString() throws IOException {
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

        private static List<String> CleanUpWeatherString() throws IOException {
            String data = Give5DaysWeatherString();
            List<String> weatherList = new ArrayList<>();
            JsonNode arrNode = new ObjectMapper().readTree(data).get("list");
            if(arrNode.isArray()){
                for(final JsonNode objNode:arrNode ){
                    String forecastTime = objNode.get("dt_txt").toString();
                    if(forecastTime.substring(9,11).contains(giveStringDayFromData(new Date()))){
                        weatherList.add(objNode.toString());
                    }
                }
            }

            return weatherList;
        }
    }

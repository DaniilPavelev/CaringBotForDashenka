package f1.CaringBotForDashenka.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnimalPhotoIMG {
    @SneakyThrows
    public static void main(String[] args) {
        System.out.println(giveURLforCats());
    }

    @SneakyThrows
    public static String giveURLforCats(){
        String string = giveCatsJSON();
        ObjectMapper mainMapper = new ObjectMapper();
        JsonNode mainNode = mainMapper.readTree(string).get(0).get("url");
        String str = mainNode.toString();
        str = str.substring(1,str.length()-1);
        return str;

    }

    @SneakyThrows
    public static String giveURLforDogs(){
        String string = giveDogsJSON();
        ObjectMapper mainMapper = new ObjectMapper();
        JsonNode mainNode = mainMapper.readTree(string).get(0).get("url");
        String str = mainNode.toString();
        str = str.substring(1,str.length()-1);
        return str;

    }

    private static String giveCatsJSON() throws IOException {
        String urlString = "https://api.thecatapi.com/v1/images/search";

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
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine())!=null){
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private static String giveDogsJSON() throws IOException {
        String urlString = "https://api.thedogapi.com/v1/images/search";

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
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine())!=null){
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

}
//https://api.thedogapi.com/v1/images/search

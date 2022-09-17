package f1.CaringBotForDashenka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CatsIMGGiver {
    @SneakyThrows
    public static void main(String[] args) {
        System.out.println(giveURLforCats());
    }

    @SneakyThrows
    private static String giveURLforCats() throws JsonProcessingException {
        String string = giveCatsJSON();
        ObjectMapper mainMapper = new ObjectMapper();
        JsonNode mainNode = mainMapper.readTree(string).get(0).get("url");
        return mainNode.toString();

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
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine())!=null){
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

}


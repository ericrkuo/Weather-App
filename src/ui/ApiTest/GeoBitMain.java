package ui.ApiTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.GeoBitData.City;
import model.GeoBitData.GeoCities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GeoBitMain {

    public static void main(String[] args) {

        String prefix = "Richmond";
        String weatherquery = "http://geodb-free-service.wirefreethought.com/v1/geo/cities?limit=5&offset=0&namePrefix=" + prefix + "&sort=name";

        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(weatherquery);
            BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();

            String weatherString = toPrettyFormat(result.toString());
            System.out.println(weatherString);

            Gson gson = new Gson();
            GeoCities citiesObject = gson.fromJson(weatherString, GeoCities.class);

            for (City c : citiesObject.getCities()) {
                if (c.getName().contains(" ")) {
                    System.out.println("City Name: " + c.getName().replace(" ", "+") + "," + c.getCountryCode());
                } else {
                    System.out.println("City Name: " + c.getName() + "," + c.getCountry());
                }
                System.out.println("       Latitude: "+c.getLatitude());
                System.out.println("       Longitude: "+c.getLongitude());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String toPrettyFormat(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }
}

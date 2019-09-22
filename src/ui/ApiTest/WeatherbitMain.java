package ui.ApiTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.ForecastData.ForecastData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class WeatherbitMain {

    public static void main(String[] args) {

        String apikey = "YOUR API KEY";
        String city = "Richmond,CA";
        String weatherquery = "https://api.weatherbit.io/v2.0/current?city=" + city + "&units=M&key=";

        Double lat = 49.166592;
        Double lon = -123.133568;
        String coordinates ="lat="+lat+"&lon="+lon;
        //weatherquery = "https://api.weatherbit.io/v2.0/current?" + coordinates + "&units=M&key=";
        weatherquery = "https://api.weatherbit.io/v2.0/forecast/daily?" + coordinates + "&days=16&units=M&key=";


        String theURL = weatherquery + apikey;

        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(theURL);
            BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();

            String weatherString = toPrettyFormat(result.toString());
            System.out.println(weatherString);

            Gson gson = new Gson();
            ForecastData weatherObject = gson.fromJson(weatherString, ForecastData.class);

            ForecastData.Datum data =weatherObject.getData().get(0);

//            System.out.println("\nCountry: " +data.getCityName()+","+data.getCountryCode());
//            System.out.println("Current temperature: "+data.getTemp()+" Celsius with "+data.getWeather().getDescription().toLowerCase());
//            System.out.println("Feels like temperature: "+data.getAppTemp()+" Celsius");
//            System.out.println("Precipitation: "+data.getPrecip()+" mm/hr");

            System.out.println(data.getValidDate());
            System.out.println("     Max Temp: "+data.getMaxTemp());
            System.out.println("     Min Temp: "+data.getMinTemp());
            System.out.println("     Temp: "+data.getTemp());
            System.out.println("     Precip: "+data.getPrecip());


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String toPrettyFormat(String jsonString)
    {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }


}

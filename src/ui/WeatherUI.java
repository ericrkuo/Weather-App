package ui;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import model.GeoBitData.City;
import model.GeoBitData.GeoCities;
import model.WeatherData.Data;
import model.WeatherData.WeatherData;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class WeatherUI {

    private Scanner input;
    private boolean runProgram;
    private ArrayList<City> favoriteCities = new ArrayList<>();


    public WeatherUI() {
        input = new Scanner(System.in);
        runProgram = true;

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader("favoriteCities.json"));
            City[] fav = gson.fromJson(reader, City[].class);

            for(City city : fav){
                favoriteCities.add(city);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void beginUserInput() {

        while (runProgram) {
            parseInput();
        }
    }

    private void parseInput() {

        System.out.println("Enter the name of a city you would like to find the weather of: ");

        String str = getUserInputString();
        if (str.length() > 0) {
            printOutCities(str.replace(" ", "+"));
        }
    }

    private void printOutCities(String str) {

        String prefix = str;
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

            String citiesString = result.toString();
            System.out.println(citiesString);

            Gson gson = new Gson();
            GeoCities geoCities = gson.fromJson(citiesString, GeoCities.class);

            int counter = 1;
            for (City c : geoCities.getCities()) {
                if (c.getRegion() != null) {
                    System.out.println(counter + ". City Name: " + c.getName() + ", " + c.getRegion() + ", " + c.getCountry());
                } else {
                    System.out.println(counter + ". City Name: " + c.getName() + ", " + c.getCountry());
                }
                counter++;
            }

            if (geoCities.getCities().size() == 0) {
                System.out.println("Sorry no cities were found, please try again.");
                parseInput();
            } else {
                System.out.println("Enter a number (1-5) for any city you would like to know the weather of: ");
                selectCityToDisplayWeather(geoCities);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectCityToDisplayWeather(GeoCities geoCities) {
        String str = getUserInputString();

        if (geoCities.getCities().size() == 0) {
            System.out.println("Sorry no cities were found, please try again.");
            parseInput();
        }

        if (str.length() > 0) {
            City city = geoCities.getCities().get(Integer.parseInt(str) - 1);

            if(!favoriteCities.contains(city)){
                favoriteCities.add(city);
            }

            displayWeatherInfo(city.getLatitude(), city.getLongitude());
        }
    }

    private void displayWeatherInfo(Double latitude, Double longitude) {
        String apikey = "bceeb7cc0b1f48749deb6c60773fed72";
        String coordinates = "lat=" + latitude + "&lon=" + longitude;
        String weatherquery = "https://api.weatherbit.io/v2.0/current?" + coordinates + "&units=M&key=";

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

            String weatherString = result.toString();

            Gson gson = new Gson();
            WeatherData weatherObject = gson.fromJson(weatherString, WeatherData.class);

            Data data = weatherObject.getData().get(0);

            System.out.println("\nCountry: " + data.getCityName() + "," + data.getCountryCode());
            System.out.println("Current temperature: " + data.getTemp() + " Celsius with " + data.getWeather().getDescription().toLowerCase());
            System.out.println("Feels like temperature: " + data.getAppTemp() + " Celsius");
            System.out.println("Precipitation: " + data.getPrecip() + " mm/hr");
            System.out.println("Snowfall: : " + data.getSnow() + " mm/hr");

            System.out.println(data.getTimezone());
            TimeZone timeZone = TimeZone.getTimeZone(data.getTimezone().replace("\\", ""));
            Date now = new Date();
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
            format.setTimeZone(timeZone);

            System.out.println(format.format(now));
            System.out.println(data.getObTime());
            System.out.println(data.getDatetime());

            offerToTryAgain();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void offerToTryAgain() {
        System.out.println();
        System.out.println("Would you like to find the weather of another city? Enter yes or no");

        String str = getUserInputString();
        if (str.length() > 0) {
            if (str.equalsIgnoreCase("yes")) {
                parseInput();
            } else {
                endProgram();
            }
        }
    }

    public void endProgram() {
        try {
            Gson gson1 = new Gson();
            String json = gson1.toJson(favoriteCities);
            json = json.replace("},", "},\n");
            FileWriter writer = new FileWriter("favoriteCities.json");
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Quitting...");
        System.exit(0);
        input.close();
    }

    public String getUserInputString() {
        String str = "";
        if (input.hasNext()) {
            str = input.nextLine();
        }
        return str;
    }
}

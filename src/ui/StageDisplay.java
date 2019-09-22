package ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ForecastData.ForecastData;
import model.GeoBitData.City;
import model.GeoBitData.GeoCities;
import model.WeatherData.Data;
import model.WeatherData.WeatherData;
import org.controlsfx.control.Notifications;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class StageDisplay extends Application {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 1000;
    private static final int IMAGE_WIDTH = 150;
    private static final int IMAGE_HEIGHT = 100;
    private static final int BUTTON_ICON_WIDTH = 32;
    private static final int BUTTON_ICON_HEIGHT = 32;
    private static final String BACKGROUND_COLOUR = "#00366B";
    private static final String LIGHTER_BACKGROUND_COLOUR = "#E5F0FB";
    private static final String GEO_IDLE_TEXTAREA = "-fx-background-color: #a9b3bc;";
    private static final String GEO_HOVERED_TEXTAREA = "-fx-background-color: #c6ccd3;";
    private static final double forecastWidth = WIDTH + WIDTH / 1.5;
    private static final double forecastHeight = HEIGHT - 80;

    private static final String IDLE_BUTTON_STYLE = "-fx-background-color:"
            + "linear-gradient(#0000ff, #0000ff),"
            + "radial-gradient(center 50% -40%, radius 200%, #9999ff 45%, #7f7fff 50%);"
            + "-fx-background-radius: 6, 5;"
            + "-fx-background-insets: 0, 1;"
            + "-fx-hover: 0, 1;"
            + "-fx-font-size: 20px;"
            + "-fx-font-family: Lucida Sans Unicode;"
            + "-fx-font-weight: bold;"
            + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 );"
            + "-fx-text-fill: white;";
    private static final String HOVERED_BUTTON_STYLE = "-fx-background-color:"
            + "linear-gradient(#0000ff, #0000ff),"
            + "radial-gradient(center 50% -40%, radius 200%, #ccccff 45%, #b2b2ff 50%);"
            + "-fx-background-radius: 6, 5;"
            + "-fx-background-insets: 0, 1;"
            + "-fx-hover: 0, 1;"
            + "-fx-font-size: 20px;"
            + "-fx-font-family: Lucida Sans Unicode;"
            + "-fx-font-weight: bold;"
            + "-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.4) , 5, 0.0 , 0 , 1 );"
            + "-fx-text-fill: white;";

    private Stage primaryStage;
    private Scene weatherScene;
    private Scene geoScene;
    private Scene favoriteScene;
    private Scene forecastScene;


    private EmailSender emailSender;
    private ArrayList<City> favoriteCities;
    private String previousScene = "geoScene";
    private String chart = "rain";

    //Geo Scene Fields so can refresh
    private VBox resultBoxGeo;
    private TextField searchFieldGeo;
    private Rectangle resultRectangleGeo;
    private GeoCities geoCities;

    public StageDisplay() {
        favoriteCities = new ArrayList<>();
        loadFavoriteCitiesFromFile();
    }

    private void loadFavoriteCitiesFromFile() {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader("favoriteCities.json"));
            City[] fav = gson.fromJson(reader, City[].class);

            for (City city : fav) {
                favoriteCities.add(city);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        emailSender = new EmailSender();
        createStage();
        createFavoriteScene();
        createGeoScene();

        primaryStage.setOnCloseRequest(event -> {
            saveFavoriteCitiesToFile();
        });

    }

    private void saveFavoriteCitiesToFile() {
        try {
            Gson gson1 = new Gson();
            String json = gson1.toJson(favoriteCities);
            json = json.replace("},", "},\n");
            FileWriter writer = new FileWriter("favoriteCities.json");
            writer.write(json);
            writer.close();
            System.out.println("Saved to file, now closing application...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createStage() {
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setTitle("Weather Application");
        primaryStage.show();
    }

    //CREATE GEO SCENE
    private void createGeoScene() {
        VBox vBox = new VBox(15);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(20, 20, 0, 20));
        vBox.setStyle("-fx-background-color: " + BACKGROUND_COLOUR + "");

        StackPane searchAndEnterStack = new StackPane();
        //add search label and enter button
        HBox searchAndEnterBox = new HBox(20);
        searchAndEnterBox.setAlignment(Pos.CENTER);
        searchAndEnterBox.setPrefSize(WIDTH, 120);
        searchAndEnterBox.setMaxSize(WIDTH, 120);

        searchAndEnterStack.getChildren().addAll(createOpaqueRectangle(WIDTH, searchAndEnterBox.getPrefHeight() + 20),
                searchAndEnterBox);

        //add vbox of results when enter button is clicked
        StackPane resultStack = new StackPane();

        resultBoxGeo = new VBox(30);
        resultBoxGeo.setAlignment(Pos.CENTER);
        resultBoxGeo.setPadding(new Insets(15));

        resultRectangleGeo = createOpaqueRectangle(WIDTH - 20, resultBoxGeo.getHeight());
        resultStack.getChildren().addAll(resultRectangleGeo, resultBoxGeo);

        searchFieldGeo = new TextField();
        searchFieldGeo.setFocusTraversable(false); // set focus traversable false.
        searchFieldGeo.setPromptText("Enter name of city");
        searchFieldGeo.setFont(Font.font("Futura", FontPosture.ITALIC, 25));
        searchFieldGeo.setMaxSize(600, 80);
        searchFieldGeo.setPrefSize(600, 80);
        Button enterButton = new Button("Enter", new ImageView(processButtonImage("search")));
        customizeButton(enterButton);
        searchAndEnterBox.getChildren().addAll(searchFieldGeo, enterButton);

        enterButton.setOnMouseClicked(e -> {
            createCityOptions();
        });
        searchFieldGeo.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                enterButton.setStyle(HOVERED_BUTTON_STYLE);
                createCityOptions();
            }
        });
        searchFieldGeo.setOnKeyReleased(e -> {
            enterButton.setStyle(IDLE_BUTTON_STYLE);
        });

        Button favoriteButton = new Button("Favorites", new ImageView(processButtonImage("heart")));
        favoriteButton = customizeButton(favoriteButton);
        favoriteButton.setOnMouseClicked(e -> {
            previousScene = "favoriteScene";
            createFavoriteScene();
        });

        vBox.getChildren().addAll(searchAndEnterStack, resultStack, favoriteButton);
        geoScene = new Scene(vBox, WIDTH, HEIGHT);
        primaryStage.setScene(geoScene);
    }

    //USE GEOBIT API
    private void createCityOptions() {

        String prefix = searchFieldGeo.getText().replace(" ", "+");
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

            String weatherString = result.toString();
            System.out.println(toPrettyFormat(weatherString));

            Gson gson = new Gson();
            geoCities = gson.fromJson(weatherString, GeoCities.class);

            resultBoxGeo.getChildren().clear();

            updateGeoSceneResultBox();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateGeoSceneResultBox() {
        resultBoxGeo.getChildren().clear();
        if (geoCities == null) {
            //do nothing
        } else if (geoCities.getCities().size() == 0) {
            Label noResultFoundArea = new Label();
            noResultFoundArea.setText("Sorry no cities were found");
            noResultFoundArea.setAlignment(Pos.CENTER);
            noResultFoundArea.setPrefSize(800, 100);
            noResultFoundArea.setMaxSize(800, 100);
            noResultFoundArea.setFont(Font.font("Futura", 25));
            noResultFoundArea.setStyle(GEO_IDLE_TEXTAREA + "-fx-text-fill: white;");
            resultBoxGeo.getChildren().add(noResultFoundArea);
            resultRectangleGeo.setHeight(120);
        } else {
            for (City c : geoCities.getCities()) {

                HBox cityResultAndAddFavoriteBox = new HBox(10);
                cityResultAndAddFavoriteBox.setAlignment(Pos.CENTER);

                Label cityResultTextArea = new Label();
                cityResultTextArea.setPrefSize(740, 100);
                cityResultTextArea.setMaxSize(740, 100);
                cityResultTextArea.setMinSize(740, 100);
                cityResultTextArea.setFont(Font.font("Futura", 25));
                cityResultTextArea.setAlignment(Pos.CENTER);
                cityResultTextArea.setStyle(GEO_IDLE_TEXTAREA);
                cityResultTextArea.setTextFill(Color.WHITE);


                if (c.getRegion() != null) {
                    cityResultTextArea.setText(c.getName() + ", " + c.getRegion() + ", " + c.getCountry());
                } else {
                    cityResultTextArea.setText(c.getName() + ", " + c.getCountry());
                }

                interactWithSingleCityResult(cityResultTextArea, c);

                cityResultAndAddFavoriteBox.getChildren().addAll(cityResultTextArea, createFavoriteIcon(c));

                resultBoxGeo.getChildren().add(cityResultAndAddFavoriteBox);
                resultRectangleGeo.setHeight(geoCities.getCities().size() * cityResultTextArea.getPrefHeight()
                        + (geoCities.getCities().size() - 1) * resultBoxGeo.getSpacing()
                        + 40);
            }
        }
    }

    //resets the order and always puts it into the list last based on insertion order
    private Node createFavoriteIcon(City c) {
        final int iconHeight = 40;
        final int iconWidth = 40;

        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(iconWidth, iconHeight);
        stackPane.setMinSize(iconWidth, iconHeight);
        stackPane.setMaxSize(iconWidth, iconHeight);

        ImageView imageView = new ImageView(processHeartIcon("emptyheart", iconWidth, iconHeight));

        if (favoriteCities.contains(c)) {
            imageView.setImage(processHeartIcon("favoriteheart", iconWidth, iconHeight));
        } else {
            imageView.setImage(processHeartIcon("emptyheart", iconWidth, iconHeight));
        }

        stackPane.setOnMouseEntered(e -> {
            if (favoriteCities.contains(c)) {
                imageView.setImage(processHeartIcon("favoriteheart", iconWidth, iconHeight - 5));
            } else {
                imageView.setImage(processHeartIcon("emptyheart", iconWidth, iconHeight - 5));
            }

        });

        stackPane.setOnMouseExited(e -> {
            if (favoriteCities.contains(c)) {
                imageView.setImage(processHeartIcon("favoriteheart", iconWidth, iconHeight));
            } else {
                imageView.setImage(processHeartIcon("emptyheart", iconWidth, iconHeight));
            }
        });

        stackPane.setOnMouseClicked(e -> {
            if (!favoriteCities.contains(c)) {
                imageView.setImage(processHeartIcon("favoriteheart", iconWidth, iconHeight));
                Notifications removeNotification = Notifications.create()
                        .title("Favorite City Added")
                        .text("City: '" + c.getName() + "' was added to favorites"
                                + "\nClick here to undo action")
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_RIGHT);

                removeNotification.onAction(event -> {
                    favoriteCities.remove(c);
                    updateGeoSceneResultBox();
                });

                favoriteCities.add(c);

                removeNotification.darkStyle();
                removeNotification.showInformation();
            } else {
                imageView.setImage(processHeartIcon("emptyheart", iconWidth, iconHeight));
                Notifications removeNotification = Notifications.create()
                        .title("Favorite City Removed")
                        .text("City: '" + c.getName() + "' was removed from favorites"
                                + "\nClick here to undo action")
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_RIGHT);

                removeNotification.onAction(event -> {
                    favoriteCities.add(c);
                    updateGeoSceneResultBox();
                });

                favoriteCities.remove(c);

                removeNotification.darkStyle();
                removeNotification.showInformation();
            }
        });

        stackPane.getChildren().add(imageView);
        return stackPane;
    }

    private void interactWithSingleCityResult(Label cityResultTextArea, City c) {


        cityResultTextArea.setOnMouseClicked(e -> {
            cityResultTextArea.setStyle(GEO_HOVERED_TEXTAREA);
            createWeatherScene(c);
        });

        cityResultTextArea.setOnMouseEntered(e -> {
            cityResultTextArea.setStyle(GEO_HOVERED_TEXTAREA);
        });

        cityResultTextArea.setOnMouseExited(e -> {
            cityResultTextArea.setStyle(GEO_IDLE_TEXTAREA);
        });
    }

    //CREATE FAVORITE SCENE
    private void createFavoriteScene() {
        VBox vBox = new VBox(30);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(20, 20, 0, 20));
        vBox.setStyle("-fx-background-color: " + BACKGROUND_COLOUR + "");

        StackPane titleStack = new StackPane();

        Label sceneTitleLabel = new Label();
        sceneTitleLabel.setText("Favorite Cities");
        sceneTitleLabel.setStyle("-fx-font-size: 50px; -fx-text-fill: white;");
        sceneTitleLabel.setPrefSize(WIDTH - 60, 90);
        sceneTitleLabel.setMaxSize(WIDTH - 60, 90);
        sceneTitleLabel.setAlignment(Pos.CENTER);

        titleStack.getChildren().addAll(createOpaqueRectangle(WIDTH, 110), sceneTitleLabel);

        final int favoriteCitiesBoxHeight = 620;
        VBox favoriteCitiesBox = new VBox(10);
        favoriteCitiesBox.setPrefSize(WIDTH, favoriteCitiesBoxHeight);
        favoriteCitiesBox.setMaxSize(WIDTH, favoriteCitiesBoxHeight);
        favoriteCitiesBox.setAlignment(Pos.TOP_CENTER);
        favoriteCitiesBox.setPadding(new Insets(favoriteCitiesBox.getSpacing(), 0, favoriteCitiesBox.getSpacing(), 0));
        favoriteCitiesBox.setStyle("-fx-background-color: #50779D");

        ScrollPane scrollPane = new ScrollPane(favoriteCitiesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(WIDTH - 60, favoriteCitiesBoxHeight);
        scrollPane.setMaxSize(WIDTH - 60, favoriteCitiesBoxHeight);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        StackPane favoriteCitiesStack = new StackPane();
        Rectangle resultRectangle = createOpaqueRectangle(WIDTH, favoriteCitiesBoxHeight + 40);
        favoriteCitiesStack.getChildren().addAll(resultRectangle, scrollPane);

        createFavoriteCityOptions(favoriteCitiesBoxHeight, favoriteCitiesBox, resultRectangle);

        Button backButton = new Button("Menu", new ImageView(processButtonImage("menu")));
        backButton = customizeButton(backButton);
        backButton.setOnMouseClicked(e -> {
            previousScene = "geoScene";
            updateGeoSceneResultBox();
            primaryStage.setScene(geoScene);
        });

        vBox.getChildren().addAll(titleStack, favoriteCitiesStack, backButton);
        favoriteScene = new Scene(vBox, WIDTH, HEIGHT);
        primaryStage.setScene(favoriteScene);
    }

    private void createFavoriteCityOptions(int favoriteCitiesBoxHeight, VBox favoriteCitiesBox, Rectangle resultRectangle) {
        for (City c : favoriteCities) {

            HBox cityResultAndRemoveIconBox = new HBox(15);
            cityResultAndRemoveIconBox.setPrefSize(800, 110);
            cityResultAndRemoveIconBox.setMaxSize(800, 110);
            cityResultAndRemoveIconBox.setMinSize(800, 110);
            cityResultAndRemoveIconBox.setAlignment(Pos.CENTER);

            Label cityResultTextArea = new Label();
            cityResultTextArea.setPrefSize(740, 100);
            cityResultTextArea.setMinSize(740, 100);
            cityResultTextArea.setMaxSize(740, 100);
            cityResultTextArea.setFont(Font.font("Futura", 25));
            cityResultTextArea.setAlignment(Pos.CENTER);
            cityResultTextArea.setStyle(GEO_IDLE_TEXTAREA);
            cityResultTextArea.setTextFill(Color.WHITE);


            if (c.getRegion() != null) {
                cityResultTextArea.setText(c.getName() + ", " + c.getRegion() + ", " + c.getCountry());
            } else {
                cityResultTextArea.setText(c.getName() + ", " + c.getCountry());
            }

            interactWithSingleCityResult(cityResultTextArea, c);

            cityResultAndRemoveIconBox.getChildren().addAll(cityResultTextArea, createRemoveIcon(c));
            favoriteCitiesBox.getChildren().add(cityResultAndRemoveIconBox);
            resultRectangle.setHeight(favoriteCitiesBoxHeight + 20);
        }
    }

    private Node createRemoveIcon(City c) {
        final int iconHeight = 40;
        final int iconWidth = 40;

        StackPane removePane = new StackPane();
        removePane.setMaxSize(iconWidth, iconHeight);
        removePane.setMinSize(iconWidth, iconHeight);
        removePane.setPrefSize(iconWidth, iconHeight);
        removePane.setAlignment(Pos.CENTER);

        Circle circle = new Circle();
        circle.setRadius(iconWidth / 2);
        circle.setStyle("-fx-fill: linear-gradient(to bottom, #ff9999 0%, #ff6666 51% #ff0000 60%);");
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(1);
        //-fx-fill: linear-gradient(to bottom, #ff9999 0%, #ff6666 15%, #ff4c4c 51%, #ff6666 86%, #ff9999 100%

        Rectangle minusSign = new Rectangle();
        minusSign.setWidth(iconWidth - 18);
        minusSign.setHeight(iconHeight / 6);
        minusSign.setStyle("-fx-fill: linear-gradient(to right, #E9E9E9 0%, #FFFFFF 51%, #E9E9E9 100%);");


        removePane.setOnMouseEntered(e -> {
            circle.setStyle("-fx-fill: linear-gradient(to bottom,#ffb2b2 0%, #ff9999 51% #ff0000 60%);");
            //-fx-fill: linear-gradient(to bottom, #ffb2b2 0%, #ff9999 15%, #ff6666 51%, #ff9999 86%, #ffb2b2 100%
            circle.setRadius(circle.getRadius() - 3);
            minusSign.setHeight(iconHeight / 7);
            minusSign.setWidth(iconWidth - 22);
        });

        removePane.setOnMouseExited(e -> {
            circle.setStyle("-fx-fill: linear-gradient(to bottom, #ff9999 0%, #ff6666 51% #ff3232 100%);");
            circle.setRadius(iconWidth / 2);
            minusSign.setHeight(iconHeight / 6);
            minusSign.setWidth(iconWidth - 18);

        });

        removePane.setOnMouseClicked(e -> {
            if (favoriteCities.contains(c)) {

                int position = favoriteCities.indexOf(c);
                favoriteCities.remove(c);
                createFavoriteScene();

                Notifications removeNotification = Notifications.create()
                        .title("Favorite City Removed")
                        .text("City: '" + c.getName() + "' was removed from favorites"
                                + "\nClick here to undo action")
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.TOP_RIGHT);

                removeNotification.onAction(event -> {
                    favoriteCities.add(position, c);
                    createFavoriteScene();
                });

                removeNotification.darkStyle();
                removeNotification.showWarning();

            }
        });

        removePane.getChildren().addAll(circle, minusSign);

        return removePane;
    }

    //CREATE WEATHER SCENE
    private void createWeatherScene(City c) {
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: " + BACKGROUND_COLOUR + "");
        vBox.setAlignment(Pos.TOP_CENTER);

        Data data = getWeatherDataWithLonLat(c.getLatitude(), c.getLongitude());

        ImageView imv = new ImageView();
        Image image = processImage(data.getWeather().getIcon());
        imv.setImage(image);

        Label cityCountrylabel = new Label();
        if (c.getRegion() != null) {
            cityCountrylabel.setText(c.getCity() + ", " + c.getRegion() + ", " + data.getCountryCode());
        } else {
            cityCountrylabel.setText(c.getCity() + ", " + data.getCountryCode());
        }
        cityCountrylabel.setPrefSize(WIDTH, 90);
        cityCountrylabel.setMaxSize(WIDTH, 90);
        cityCountrylabel.setAlignment(Pos.BOTTOM_CENTER);
        cityCountrylabel.setStyle("-fx-background-color:" + BACKGROUND_COLOUR + "; -fx-text-fill: #FFFFFF; "
                + "-fx-font-size: 45px;");

        Label dateLabel = new Label();
        dateLabel.setPrefSize(WIDTH, 40);
        dateLabel.setMaxSize(WIDTH, 40);
        dateLabel.setAlignment(Pos.CENTER);
        dateLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 23px");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EE MMMM dd, YYYY hh:mm a");
        LocalDateTime now = LocalDateTime.now(ZoneId.of(data.getTimezone().replace("\\", "")));
        dateLabel.setText(dtf.format(now));

        StackPane iconAndTemperatureStack = new StackPane();

        HBox iconAndTemperatureBox = new HBox(10);
        iconAndTemperatureBox.setPrefSize(WIDTH, 90);
        iconAndTemperatureBox.setMaxSize(WIDTH, 90);
        iconAndTemperatureBox.setAlignment(Pos.CENTER);

        iconAndTemperatureStack.getChildren().addAll(createOpaqueRectangle(WIDTH, 100), iconAndTemperatureBox);

        Label temperatureLabel = new Label();
        temperatureLabel.setText(data.getTemp() + "°C");
        temperatureLabel.setStyle("-fx-font-size: 90px;-fx-text-fill: white");

        iconAndTemperatureBox.getChildren().addAll(imv, temperatureLabel);

        Label feelsLikeAndDescriptionLabel = new Label();
        feelsLikeAndDescriptionLabel.setAlignment(Pos.CENTER);
        feelsLikeAndDescriptionLabel.setTextAlignment(TextAlignment.CENTER);
        feelsLikeAndDescriptionLabel.setPrefSize(WIDTH, 75);
        feelsLikeAndDescriptionLabel.setMaxSize(WIDTH, 75);
        feelsLikeAndDescriptionLabel.setStyle("-fx-background-color: " + BACKGROUND_COLOUR + "; -fx-font-size: 23px; -fx-text-fill: white;");
        feelsLikeAndDescriptionLabel.setText("Feels like: " + data.getAppTemp() + "°C"
                + "\n" + data.getWeather().getDescription());

        HBox precipiationAndUVIndexBox = new HBox(30);
        precipiationAndUVIndexBox.setAlignment(Pos.TOP_CENTER);
        precipiationAndUVIndexBox.setStyle("-fx-background-color: " + BACKGROUND_COLOUR + "");
        precipiationAndUVIndexBox.setPrefSize(WIDTH, 100);
        precipiationAndUVIndexBox.setMaxSize(WIDTH, 100);
        precipiationAndUVIndexBox.setAlignment(Pos.CENTER);

        ImageView rainIMV = new ImageView();
        Image rainImage = processSecondaryImage("raindroplet");
        rainIMV.setImage(rainImage);

        ImageView sunIMV = new ImageView();
        Image sunImage = processSecondaryImage("sun");
        sunIMV.setImage(sunImage);

        Label precipiationLabel = new Label();
        precipiationLabel.setText("Precipiation: \n" + data.getPrecip() + " mm/hr");
        precipiationLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        Rectangle verticalBar = new Rectangle();
        verticalBar.setHeight(60);
        verticalBar.setWidth(3);
        verticalBar.setArcWidth(3);
        verticalBar.setArcHeight(3);
        verticalBar.setFill(Color.WHITE);

        Label uvLabel = new Label();
        uvLabel.setText("UV Index: \n" + data.getUv());
        uvLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        precipiationAndUVIndexBox.getChildren().addAll(rainIMV, precipiationLabel, verticalBar, sunIMV, uvLabel);

        VBox detailsBox = new VBox(20);
        detailsBox.setPrefSize(WIDTH - 20, 380);
        detailsBox.setMaxSize(WIDTH - 20, 380);
        detailsBox.setAlignment(Pos.TOP_CENTER);
        detailsBox.setStyle("-fx-background-color: #50779D");

        HBox sunriseBox = new HBox(20);
        formatHBoxInDetailsBox(sunriseBox);
        ImageView sunRiseIMV = new ImageView();
        Image sunRiseImage = processSecondaryImage("sunrise");
        sunRiseIMV.setImage(sunRiseImage);
        Label sunRiseLabel = new Label();
        sunRiseLabel.setText("Sunrise: ");
        formatDetailsLabel(sunRiseLabel);
        Label sunRiseResultLabel = new Label();
        sunRiseResultLabel.setText(data.getSunrise() + " AM");
        formatDetailsResultLabel(sunRiseResultLabel);
        sunriseBox.getChildren().addAll(sunRiseIMV, sunRiseLabel, sunRiseResultLabel);

        HBox sunsetBox = new HBox(20);
        formatHBoxInDetailsBox(sunsetBox);
        ImageView sunSetIMV = new ImageView();
        Image sunSetImage = processSecondaryImage("sunset");
        sunSetIMV.setImage(sunSetImage);
        Label sunsetLabel = new Label();
        sunsetLabel.setText("Sunset: ");
        formatDetailsLabel(sunsetLabel);
        Label sunSetResultLabel = new Label();
        sunSetResultLabel.setText(data.getSunset() + " PM");
        formatDetailsResultLabel(sunSetResultLabel);
        sunsetBox.getChildren().addAll(sunSetIMV, sunsetLabel, sunSetResultLabel);

        HBox humidityBox = new HBox(20);
        formatHBoxInDetailsBox(humidityBox);
        ImageView humidityIMV = new ImageView();
        Image humidityImage = processSecondaryImage("humidity");
        humidityIMV.setImage(humidityImage);
        Label humidityLabel = new Label();
        humidityLabel.setText("Relative Humidity: ");
        formatDetailsLabel(humidityLabel);
        Label humidityResultLabel = new Label();
        humidityResultLabel.setText(data.getRh() + "%");
        formatDetailsResultLabel(humidityResultLabel);
        humidityBox.getChildren().addAll(humidityIMV, humidityLabel, humidityResultLabel);

        HBox snowBox = new HBox(20);
        formatHBoxInDetailsBox(snowBox);
        ImageView snowIMV = new ImageView();
        Image snowImage = processSecondaryImage("snow");
        snowIMV.setImage(snowImage);
        Label snowLabel = new Label();
        snowLabel.setText("Snow: ");
        formatDetailsLabel(snowLabel);
        Label snowResultLabel = new Label();
        snowResultLabel.setText(data.getSnow() + " mm/hr");
        formatDetailsResultLabel(snowResultLabel);
        snowBox.getChildren().addAll(snowIMV, snowLabel, snowResultLabel);

        HBox latlonBox = new HBox(20);
        formatHBoxInDetailsBox(latlonBox);
        ImageView latlonIMV = new ImageView();
        Image latlonImage = processSecondaryImage("latlon");
        latlonIMV.setImage(latlonImage);
        Label latlonLabel = new Label();
        latlonLabel.setText("Latitude & Longitude: ");
        formatDetailsLabel(latlonLabel);
        Label latlonResultLabel = new Label();
        latlonResultLabel.setText(data.getLat() + ", " + data.getLon());
        formatDetailsResultLabel(latlonResultLabel);
        latlonBox.getChildren().addAll(latlonIMV, latlonLabel, latlonResultLabel);

        HBox windBox = new HBox(20);
        formatHBoxInDetailsBox(windBox);
        ImageView windIMV = new ImageView();
        Image windImage = processSecondaryImage("wind");
        windIMV.setImage(windImage);
        Label windLabel = new Label();
        windLabel.setText("Wind Speed: ");
        formatDetailsLabel(windLabel);
        Label windResultLabel = new Label();
        windResultLabel.setText(data.getWindSpd() + " m/s " + data.getWindCdirFull().substring(0, 1).toUpperCase() + data.getWindCdirFull().substring(1));
        formatDetailsResultLabel(windResultLabel);
        windBox.getChildren().addAll(windIMV, windLabel, windResultLabel);

        HBox airQualityBox = new HBox(20);
        formatHBoxInDetailsBox(airQualityBox);
        ImageView airQualityIMV = new ImageView();
        Image airQualityImage = processSecondaryImage("airquality");
        airQualityIMV.setImage(airQualityImage);
        Label airQualityLabel = new Label();
        airQualityLabel.setText("Air Quality: ");
        formatDetailsLabel(airQualityLabel);
        Label airQualityResultLabel = new Label();
        airQualityResultLabel.setText(data.getAqi() + " (EPA Standard)");
        formatDetailsResultLabel(airQualityResultLabel);
        airQualityBox.getChildren().addAll(airQualityIMV, airQualityLabel, airQualityResultLabel);

        detailsBox.getChildren().addAll(createRectangle(),
                sunriseBox, createRectangle(),
                sunsetBox, createRectangle(),
                humidityBox, createRectangle(),
                snowBox, createRectangle(),
                latlonBox, createRectangle(),
                windBox, createRectangle(),
                airQualityBox, createRectangle());

        ScrollPane scrollPane = new ScrollPane(detailsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(WIDTH - 50, 380);
        scrollPane.setMaxSize(WIDTH - 50, 380);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        StackPane detailsBoxStack = new StackPane();


        //detailsBoxStack.getChildren().addAll(scrollPane, createOpaqueRectangle(WIDTH, 400));
        detailsBoxStack.getChildren().addAll(createOpaqueRectangle(WIDTH, 400), scrollPane);

        HBox buttonBox = new HBox(20);
        buttonBox.setPrefSize(WIDTH, 100);
        buttonBox.setMaxSize(WIDTH, 100);
        buttonBox.setAlignment(Pos.CENTER);

        Button sendButton = new Button("Send Email", new ImageView(processButtonImage("email")));
        sendButton = customizeButton(sendButton);
        Button finalSendButton = sendButton;
        sendButton.setOnMouseClicked(event -> {
            finalSendButton.setText("Sending Email...");
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Enter email address below");
            td.setContentText("Email: ");
            td.setTitle("Send Email");
            td.setResizable(true);
            td.getDialogPane().setPrefSize(600, 300);
            Optional<String> result = td.showAndWait();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(600, 200);

            if (result.isPresent()) {
                String text = result.get();
                if (text.contains(" ")) {
                    alert.setHeaderText("Invalid email address given");
                    alert.setContentText("The given email '" + text + "' contains a space");
                    alert.show();
                    finalSendButton.setText("Send Email");
                } else if (!(text.endsWith(".com") || text.endsWith(".ca"))) {
                    alert.setHeaderText("Invalid email address given");
                    alert.setContentText("The given email '" + text + "' contains does not end with .com or .ca");
                    alert.show();
                    finalSendButton.setText("Send Email");
                } else {
                    try {
                        emailSender.sendEmail(data, c, text);
                        finalSendButton.setDisable(true);
                        finalSendButton.setText("Email Sent!");

                        Notifications emailNotification = Notifications.create()
                                .title("Email Notification")
                                .text("Email to '" + text + "' was successfully sent")
                                .hideAfter(Duration.seconds(2))
                                .position(Pos.TOP_RIGHT);

                        emailNotification.showConfirm();
                        emailNotification.darkStyle();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                finalSendButton.setText("Send Email");
            }
        });

        Button backButton = new Button("Go Back", new ImageView(processButtonImage("back")));
        backButton = customizeButton(backButton);
        backButton.setOnMouseClicked(e -> {
            if (previousScene.equals("geoScene")) {
                primaryStage.setScene(geoScene);
            } else {
                primaryStage.setScene(favoriteScene);
            }
        });

        Button refreshButton = new Button("Refresh", new ImageView(processButtonImage("refresh")));
        refreshButton = customizeButton(refreshButton);
        refreshButton.setOnMouseClicked(e -> {
            createWeatherScene(c);
        });

        Button statButton = new Button("Statistics");
        statButton = customizeButton(statButton);
        statButton.setOnMouseClicked(e -> createNewWindow(c));

        buttonBox.getChildren().addAll(sendButton, backButton, refreshButton, statButton);

        vBox.getChildren().addAll(cityCountrylabel, dateLabel, iconAndTemperatureStack, feelsLikeAndDescriptionLabel,
                precipiationAndUVIndexBox, detailsBoxStack, buttonBox);

        weatherScene = new Scene(vBox, WIDTH, HEIGHT);
        primaryStage.setScene(weatherScene);
    }

    private Rectangle createRectangle() {
        Rectangle detailsBoxRectangle = new Rectangle();
        detailsBoxRectangle.setWidth(WIDTH - 200);
        detailsBoxRectangle.setHeight(2.5);
        detailsBoxRectangle.setArcWidth(8);
        detailsBoxRectangle.setArcHeight(5);
        detailsBoxRectangle.setFill(Color.WHITE);
        return detailsBoxRectangle;
    }

    private Rectangle createOpaqueRectangle(double width, double height) {
        Rectangle rectangle = new Rectangle();
        rectangle.setMouseTransparent(true);
        rectangle.setWidth(width - 40);
        rectangle.setHeight(height);
        rectangle.setArcHeight(30);
        rectangle.setArcWidth(30);
        rectangle.setOpacity(0.35);
        rectangle.setFill(Color.web(LIGHTER_BACKGROUND_COLOUR));
        return rectangle;
    }

    private void formatHBoxInDetailsBox(HBox sunriseBox) {
        sunriseBox.setMaxSize(WIDTH - 40, 80);
        sunriseBox.setPrefSize(WIDTH - 40, 80);
        sunriseBox.setAlignment(Pos.CENTER);
    }

    private void formatDetailsResultLabel(Label resultLabel) {
        resultLabel.setAlignment(Pos.CENTER_RIGHT);
        //resultLabel.setStyle("-fx-background-color: #AA3643; -fx-font-size: 20px; -fx-text-fill: white");
        resultLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white");
        resultLabel.setPrefSize(320, 60);
        resultLabel.setMaxSize(320, 60);
    }

    private void formatDetailsLabel(Label infoLabel) {
        infoLabel.setAlignment(Pos.CENTER_LEFT);
        //infoLabel.setStyle("-fx-background-color: #D1A4A9; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white");
        infoLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white");
        infoLabel.setPrefSize(240, 60);
        infoLabel.setMaxSize(240, 60);
        infoLabel.setPadding(new Insets(0, 0, 0, 20));
    }

    //PROCESS IMAGES AND BUTTONS
    private Image processImage(String string) {
        Image image = null;
        try {
            image = new Image(
                    new FileInputStream("data/" + string + ".png"),
                    IMAGE_WIDTH, IMAGE_HEIGHT, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return image;
    }

    private Image processSecondaryImage(String string) {
        Image image = null;
        try {
            image = new Image(
                    new FileInputStream("data/" + string + ".png"),
                    IMAGE_WIDTH - 50, IMAGE_HEIGHT - 50, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return image;
    }

    private Image processHeartIcon(String string, int iconWidth, int iconHeight) {
        Image image = null;
        try {
            image = new Image(
                    new FileInputStream("data/" + string + ".png"),
                    iconWidth, iconHeight, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return image;
    }

    private Image processButtonImage(String string) {
        Image image = null;
        try {
            image = new Image(
                    new FileInputStream("data/" + string + ".png"),
                    BUTTON_ICON_WIDTH, BUTTON_ICON_HEIGHT, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return image;
    }

    private Button customizeButton(Button button) {
        button.setPrefSize(200, 80);
        button.setAlignment(Pos.CENTER);
        button.setGraphicTextGap(11);
        button.setStyle(IDLE_BUTTON_STYLE);
        button.setFont(Font.font("Franklin Gothic Medium", 25));
        button.setOnMouseEntered(event -> {
            button.setStyle(HOVERED_BUTTON_STYLE);
            //button.setFont(Font.font("Franklin Gothic Medium", 25));
        });
        button.setOnMouseExited(event -> {
            button.setStyle(IDLE_BUTTON_STYLE);
            //button.setFont(Font.font("Franklin Gothic Medium", 25));
        });
        return button;
    }

    //USE WEATHERBIT API
    private Data getWeatherDataWithLonLat(Double latitude, Double longitude) {
        Data data = null;
        try {
            String apikey = "bceeb7cc0b1f48749deb6c60773fed72";
            String coordinates = "lat=" + latitude + "&lon=" + longitude;
            String weatherquery = "https://api.weatherbit.io/v2.0/current?" + coordinates + "&units=M&key=";
            String theURL = weatherquery + apikey;

            StringBuilder result = new StringBuilder();
            URL url = new URL(theURL);
            BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();

            String weatherString = result.toString();
            System.out.println(toPrettyFormat(weatherString));

            Gson gson = new Gson();
            WeatherData weatherObject = gson.fromJson(weatherString, WeatherData.class);

            data = weatherObject.getData().get(0);

            return data;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String toPrettyFormat(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    // CREATE BAR CHART FORECAST SCENE

    private void createNewWindow(City c) {
        VBox forecastBox = new VBox(10);
        forecastBox.setPadding(new Insets(10));
        forecastBox.setPrefSize(forecastWidth, forecastHeight);
        forecastBox.setMaxSize(forecastWidth, forecastHeight);
        forecastBox.setAlignment(Pos.CENTER);
        forecastBox.setStyle("-fx-background-color: " + LIGHTER_BACKGROUND_COLOUR + ";");

        forecastScene = new Scene(forecastBox, forecastWidth, forecastHeight);
        Stage questionStage = new Stage();

        questionStage.initModality(Modality.WINDOW_MODAL);
        questionStage.initOwner(primaryStage);

        questionStage.setTitle("Statistics for Future Forecast");
        questionStage.setScene(forecastScene);
        questionStage.show();

        ForecastData forecastData = getForecastDataWithLonLat(c.getLongitude(), c.getLatitude());

        Button rainChartButton = new Button("Rain Statistics");
        rainChartButton.setPrefSize(300, 50);
        rainChartButton.setAlignment(Pos.CENTER);
        rainChartButton.setGraphicTextGap(11);
        rainChartButton.setStyle(IDLE_BUTTON_STYLE);
        rainChartButton.setFont(Font.font("Franklin Gothic Medium", 25));
        rainChartButton.setOnMouseEntered(event -> {
            rainChartButton.setStyle(HOVERED_BUTTON_STYLE);
        });
        rainChartButton.setOnMouseExited(event -> {
            rainChartButton.setStyle(IDLE_BUTTON_STYLE);
        });

        rainChartButton.setOnMouseClicked(e -> {
            if (chart.equals("rain")) {
                forecastBox.getChildren().remove(0);
                chart = "notrain";
                rainChartButton.setText("Temperature Statistics");
                forecastBox.getChildren().add(0, createRainChart(forecastData, c));
            } else {
                forecastBox.getChildren().remove(0);
                chart = "rain";
                rainChartButton.setText("Rain Statistics");
                forecastBox.getChildren().add(0, createTemperatureChart(forecastData, c));
            }
        });

        forecastBox.getChildren().addAll(createTemperatureChart(forecastData, c), rainChartButton);
    }

    private ForecastData getForecastDataWithLonLat(Double longitude, Double latitude) {
        String apikey = "bceeb7cc0b1f48749deb6c60773fed72";

        String coordinates = "lat=" + latitude + "&lon=" + longitude;
        String weatherquery = "https://api.weatherbit.io/v2.0/forecast/daily?" + coordinates + "&days=16&units=M&key=";

        String theURL = weatherquery + apikey;

        ForecastData weatherObject = null;
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

            Gson gson = new Gson();
            weatherObject = gson.fromJson(weatherString, ForecastData.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherObject;
    }

    private Node createRainChart(ForecastData forecastData, City c) {
        CategoryAxis xAxis = new CategoryAxis();

        ObservableList<String> dates = FXCollections.observableArrayList();

        for (ForecastData.Datum datum : forecastData.getData()) {
            dates.add(datum.getValidDate());
        }

        xAxis.setCategories(dates);


        xAxis.setLabel("Date");

        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Precipitation (mm)");


        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Comparison of precipitation vs various dates in: " +getCityCountry(c, forecastData));
        barChart.setPrefSize(forecastWidth, forecastHeight);
        barChart.setMaxSize(forecastWidth, forecastHeight);

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Precipitation");
        for (ForecastData.Datum datum : forecastData.getData()) {
            series1.getData().add(new XYChart.Data<>(datum.getValidDate(), datum.getPrecip()));
        }

        barChart.getData().addAll(series1);
        barChart.setBarGap(2);
        barChart.setCategoryGap(5);
        barChart.setHorizontalGridLinesVisible(true);
        barChart.setLegendSide(Side.RIGHT);

        return barChart;
    }

    private BarChart<String, Number> createTemperatureChart(ForecastData forecastData, City c) {
        CategoryAxis xAxis = new CategoryAxis();

        ObservableList<String> dates = FXCollections.observableArrayList();

        for (ForecastData.Datum datum : forecastData.getData()) {
            dates.add(datum.getValidDate());
        }

        xAxis.setCategories(dates);


        xAxis.setLabel("Date");

        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temperature (°C)");


        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Comparison of temperature vs various dates in: " + getCityCountry(c, forecastData));
        barChart.setPrefSize(forecastWidth, forecastHeight);
        barChart.setMaxSize(forecastWidth, forecastHeight);

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Minimum Temperature");
        for (ForecastData.Datum datum : forecastData.getData()) {
            series1.getData().add(new XYChart.Data<>(datum.getValidDate(), datum.getMinTemp()));
        }

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Temperature");
        for (ForecastData.Datum datum : forecastData.getData()) {
            series2.getData().add(new XYChart.Data<>(datum.getValidDate(), datum.getTemp()));
        }

        XYChart.Series<String, Number> series3 = new XYChart.Series<>();
        series3.setName("Maximum Temperature");
        for (ForecastData.Datum datum : forecastData.getData()) {
            series3.getData().add(new XYChart.Data<>(datum.getValidDate(), datum.getMaxTemp()));
        }

        barChart.getData().addAll(series1, series2, series3);
        barChart.setBarGap(2);
        barChart.setCategoryGap(5);
        barChart.setHorizontalGridLinesVisible(true);
        barChart.setLegendSide(Side.RIGHT);

        return barChart;
    }

    public String getCityCountry(City c, ForecastData forecastData) {
        if (c.getRegion() != null) {
            return c.getCity() + ", " + c.getRegion() + ", " + forecastData.getCountryCode();
        } else {
            return c.getCity() + ", " + forecastData.getCountryCode();
        }
    }
}

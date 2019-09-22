package ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;

public class BarChartTest extends Application{

    private String chart = "rain";


    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setPrefHeight(900);
        vBox.setMaxHeight(900);

        vBox.setAlignment(Pos.CENTER);

        Button rainChartButton = new Button("Rain Statistics");
        rainChartButton.setOnMouseClicked(e ->{
           if(chart.equals("rain")){
               vBox.getChildren().remove(0);
               chart = "notrain";
               rainChartButton.setText("Temperature Statistics");
               vBox.getChildren().add(0, createRainChart());
           } else {
               vBox.getChildren().remove(0);
               chart = "rain";
               rainChartButton.setText("Rain Statistics");
               vBox.getChildren().add(0, createTemperatureChart());
           }
        });

        vBox.getChildren().addAll(createTemperatureChart(), rainChartButton);

        Scene scene = new Scene(vBox, 1500, 900);
        scene.setFill(Color.AQUA);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node createRainChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(
                "09/03/19", "09/04/19", "09/05/19","09/06/19","09/07/19","09/08/19","09/09/19","09/10/19")));
        xAxis.setLabel("Date");

        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Rain (mm/hr)");


        BarChart<String, Number> rainChart = new BarChart<>(xAxis, yAxis);
        rainChart.setTitle("Comparison of precipitation vs various dates in Richmond, BC");
        rainChart.setPrefSize(1500, 900);
        rainChart.setMaxSize(1500, 900);

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Precipitation");
        series1.getData().add(new XYChart.Data<>("09/03/19", 0.03));
        series1.getData().add(new XYChart.Data<>("09/04/19", 0.4489));
        series1.getData().add(new XYChart.Data<>("09/05/19", 0.6868));
        series1.getData().add(new XYChart.Data<>("09/06/19", 0.25));
        series1.getData().add(new XYChart.Data<>("09/07/19", 0.9));
        series1.getData().add(new XYChart.Data<>("09/08/19", 0.1234));
        series1.getData().add(new XYChart.Data<>("09/09/19", 0.876));
        series1.getData().add(new XYChart.Data<>("09/10/19", 0.7111));



        rainChart.getData().addAll(series1);
        rainChart.setBarGap(2);
        rainChart.setCategoryGap(5);
        rainChart.setHorizontalGridLinesVisible(true);
        rainChart.setLegendSide(Side.RIGHT);

        return rainChart;
    }

    private BarChart<String, Number> createTemperatureChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(
                "09/03/19", "09/04/19", "09/05/19")));
        xAxis.setLabel("Date");

        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Temperature (°C)");


        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Comparison of temperature vs various dates in Richmond, BC");
        barChart.setPrefSize(1500, 900);
        barChart.setMaxSize(1500, 900);

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Temperature");
        series1.getData().add(new XYChart.Data<>("09/03/19", 29.3));
        series1.getData().add(new XYChart.Data<>("09/04/19", 18.2));
        series1.getData().add(new XYChart.Data<>("09/05/19", 39.8));

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Apparent Temperature");
        series2.getData().add(new XYChart.Data<>("09/03/19", 28.5));
        series2.getData().add(new XYChart.Data<>("09/04/19", 20.2));
        series2.getData().add(new XYChart.Data<>("09/05/19", 34.33));

        barChart.getData().addAll(series1, series2);
        barChart.setBarGap(2);
        barChart.setCategoryGap(5);
        barChart.setHorizontalGridLinesVisible(true);
        barChart.setLegendSide(Side.RIGHT);

        return barChart;
    }

}

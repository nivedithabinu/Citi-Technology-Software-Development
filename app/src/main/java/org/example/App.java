package org.example;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Date;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class App extends Application {
    private Queue<StockData> queue = new LinkedList<>();

    static class StockData {
        Date timestamp;
        BigDecimal price;

        StockData(Date timestamp, BigDecimal price) {
            this.timestamp = timestamp;
            this.price = price;
        }

        @Override
        public String toString() {
            return "Time: " + timestamp + " | Price: " + price;
        }
    }

    public void runLogic() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
        System.out.println("Fetching live data: ");

        for (int i = 1; i <= 5; i++) {
            try {
                Stock stock = YahooFinance.get("^DJI");

                if (stock != null && stock.getQuote() != null && stock.getQuote().getPrice() != null) {
                    BigDecimal price = stock.getQuote().getPrice();
                    StockData data = new StockData(new Date(), price);
                    
                    queue.add(data);
                    
                    System.out.println("Record " + i + ": " + data);
                } 
                
                else {
                    System.out.println("Unable to fetch stock data.");
                }
                
                Thread.sleep(5000);
            } 
            
            catch (Exception e) {
                System.out.println("Unable to fetch stock data.");
                System.out.println("Using sample data instead.");
                
                BigDecimal samplePrice = BigDecimal.valueOf(42000 + Math.random() * 1000);
                StockData data = new StockData(new Date(), samplePrice);
                
                queue.add(data);

                System.out.println(data);
            }
        }

        System.out.println("Stored Queue Data:");

        for (StockData data : queue) {
            System.out.println(data);
        }
    }

    @Override 
    public void start(Stage stage) {
        runLogic();

        stage.setTitle("Dow Jones Market Monitoring");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time");
        yAxis.setLabel("Stock Price");

        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);  
        lineChart.setTitle("Real-Time Data");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Dow Jones Industrial Average");

        int i = 1;
        
        for (StockData data : queue) {
            series.getData().add(new XYChart.Data(i, data.price.doubleValue()));
            i++;
        }
        
        lineChart.getData().add(series);
        
        Scene scene = new Scene(lineChart, 800, 600);

        lineChart.getData().add(series);
       
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
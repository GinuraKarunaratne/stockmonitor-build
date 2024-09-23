package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App extends Application {

    private final Queue<StockData> stockDataQueue = new LinkedList<>();
    private LineChart<Number, Number> lineChart;
    private XYChart.Series<Number, Number> series;
    private ScheduledExecutorService executor;
    private Random random = new Random(); // For generating mock stock prices

    @Override
    public void start(Stage primaryStage) {
        // Create axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (ms)");
        yAxis.setLabel("Stock Price ($)");

        // Create line chart
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Dow Jones Industrial Average");

        // Create series for data
        series = new XYChart.Series<>();
        series.setName("DJIA");

        // Add series to chart
        lineChart.getData().add(series);

        // Create scene with chart
        Scene scene = new Scene(lineChart, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Stock Price Tracker");
        primaryStage.show();

        // Schedule task to fetch mock stock data every 5 seconds
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::fetchMockStockData, 0, 5, TimeUnit.SECONDS);
    }

    private void fetchMockStockData() {
        try {
            // Simulate fetching stock data
            double price = 25000 + (random.nextDouble() * 100); // Simulated price between 25000 and 25100
            long timestamp = System.currentTimeMillis();

            // Store stock price and timestamp in the queue
            StockData data = new StockData(timestamp, price);
            stockDataQueue.add(data);

            // Update series with new data point
            Platform.runLater(() -> {
                series.getData().add(new XYChart.Data<>(timestamp, price));
            });

            // Remove old data points to keep the chart clean (optional)
            if (series.getData().size() > 10) {
                series.getData().remove(0);
            }

            // Print the latest stock data (optional)
            System.out.println("Mock Dow Jones Industrial Average: $" + price + " at " + timestamp);
        } catch (Exception e) {
            System.err.println("Error fetching stock data: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        // Shutdown executor on application exit
        if (executor != null) {
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class StockData {
        private final long timestamp;
        private final double price;

        public StockData(long timestamp, double price) {
            this.timestamp = timestamp;
            this.price = price;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public double getPrice() {
            return price;
        }
    }
}

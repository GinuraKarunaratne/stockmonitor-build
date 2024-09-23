package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class App {

    // Queue to store stock prices and timestamps
    private static Queue<String> stockDataQueue = new LinkedList<>();

    public static void main(String[] args) throws InterruptedException {

        // Run the stock data retrieval every 5 seconds
        while (true) {
            try {
                // Fetch DJIA stock data from Yahoo Finance
                Stock dowJones = YahooFinance.get("^DJI");
                if (dowJones != null) {
                    double price = dowJones.getQuote().getPrice().doubleValue();
                    long timestamp = System.currentTimeMillis();

                    // Store stock price and timestamp in the queue
                    stockDataQueue.add("Price: " + price + ", Timestamp: " + timestamp);

                    // Print the latest stock data
                    System.out.println("Dow Jones Industrial Average: " + price + " at " + timestamp);
                }
            } catch (Exception e) {
                System.err.println("Error fetching stock data: " + e.getMessage());
            }

            // Sleep for 5 seconds
            TimeUnit.SECONDS.sleep(5);
        }
    }
}

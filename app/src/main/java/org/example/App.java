package org.example;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class App {

    private static final String API_KEY = "EBEKZVO5UOK7O0MX"; // Replace with your Alpha Vantage API key
    private static final String SYMBOL = "IBM"; // Stock symbol (e.g., IBM)

    public static void main(String[] args) throws InterruptedException {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        while (true) {
            try {
                System.out.println("Fetching stock data...");
                String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + SYMBOL + "&apikey=" + API_KEY;
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                // Log the response status
                System.out.println("Response status: " + response.code());

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);

                    // Check for API rate limit and error messages
                    if (jsonObject.has("Note")) {
                        System.err.println("API call limit reached. Please wait before making more requests.");
                        break; // Exit the loop to avoid continuous calls
                    }

                    if (jsonObject.has("Error Message")) {
                        System.err.println("Error: " + jsonObject.get("Error Message").getAsString());
                        break; // Exit the loop on error
                    }

                    // Extract the daily time series data
                    JsonObject timeSeries = jsonObject.getAsJsonObject("Time Series (Daily)");
                    if (timeSeries == null) {
                        System.err.println("No time series data found in the response.");
                        break; // Exit the loop on empty response
                    }

                    // Get the latest date from the time series data
                    String latestDate = timeSeries.keySet().iterator().next();
                    JsonObject dailyData = timeSeries.getAsJsonObject(latestDate);
                    double openPrice = dailyData.get("1. open").getAsDouble();
                    double highPrice = dailyData.get("2. high").getAsDouble();
                    double lowPrice = dailyData.get("3. low").getAsDouble();
                    double closePrice = dailyData.get("4. close").getAsDouble();
                    long volume = dailyData.get("5. volume").getAsLong();

                    // Print the daily stock data
                    System.out.printf("Date: %s, Open: %.4f, High: %.4f, Low: %.4f, Close: %.4f, Volume: %d%n",
                            latestDate, openPrice, highPrice, lowPrice, closePrice, volume);
                } else {
                    System.err.println("Error fetching stock data: " + response.message());
                }

            } catch (IOException e) {
                System.err.println("Error fetching stock data: " + e.getMessage());
            }

            TimeUnit.SECONDS.sleep(5); // Wait for 5 seconds before the next update
        }
    }
}

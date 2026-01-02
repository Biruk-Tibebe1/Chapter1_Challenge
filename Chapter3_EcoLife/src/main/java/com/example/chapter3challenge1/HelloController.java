package com.example.chapter3challenge1;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HelloController {
    @FXML
    private Label cityLabel;
    @FXML
    private TextField cityTextField;
    @FXML
    private Button refreshButton;
    @FXML
    private Label tempLabel;
    @FXML
    private Label conditionLabel;
    @FXML
    private SVGPath ecoIcon;
    @FXML
    private Label gardeningTipLabel;
    @FXML
    private Label airQualityLabel;

    private static final String API_KEY = "744107c18264b8f97f0b80501842a240";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    @FXML
    public void initialize() {
        // Disable Refresh button if city TextField is empty
        refreshButton.disableProperty().bind(cityTextField.textProperty().isEmpty());

        // Initial animation for the eco icon
        animateEcoIcon();
    }

    @FXML
    protected void onRefreshButtonClick() {
        String city = cityTextField.getText();
        if (city != null && !city.isBlank()) {
            cityLabel.setText(city);
            fetchWeatherData(city);
            animateEcoIcon();
        }
    }

    private void fetchWeatherData(String city) {
        // Replace spaces with %20 for URL safety
        String encodedCity = city.replace(" ", "%20");
        String url = String.format(API_URL, encodedCity, API_KEY);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseAndDisplayWeather)
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        conditionLabel.setText("Connection Error");
                        tempLabel.setText("--");
                        e.printStackTrace();
                    });
                    return null;
                });
    }

    private void parseAndDisplayWeather(String responseBody) {
        Platform.runLater(() -> {
            try {
                // Debug: Print response to console to help diagnose issues
                System.out.println("API Response: " + responseBody);

                // Check for success code (200). OpenWeatherMap can return int or string for cod.
                if (!responseBody.contains("\"cod\":200") && !responseBody.contains("\"cod\":\"200\"")) {
                    String msg = extractString(responseBody, "\"message\":");
                    if (msg.isEmpty()) msg = "City not found or API error";
                    conditionLabel.setText(msg);
                    tempLabel.setText("--");
                    return;
                }

                double temp = extractDouble(responseBody, "\"temp\":");
                String description = extractString(responseBody, "\"description\":");
                String mainCondition = extractString(responseBody, "\"main\":");

                if (description.isEmpty()) {
                    description = "Unknown";
                }

                tempLabel.setText(String.format("%.1f°C", temp));
                
                // Capitalize first letter safely
                if (description.length() > 0) {
                    conditionLabel.setText(description.substring(0, 1).toUpperCase() + description.substring(1));
                } else {
                    conditionLabel.setText(description);
                }

                updateEcoSuggestions(temp, mainCondition);

            } catch (Exception e) {
                conditionLabel.setText("Error parsing data");
                e.printStackTrace();
            }
        });
    }

    // Helper to extract a double value from JSON string
    private double extractDouble(String json, String key) {
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) return 0.0;
        
        // Value starts after the key. It might be :12.3 or : 12.3
        int startVal = keyIndex + key.length();
        // Find the first digit or minus sign
        while (startVal < json.length() && !Character.isDigit(json.charAt(startVal)) && json.charAt(startVal) != '-') {
            startVal++;
        }
        
        if (startVal >= json.length()) return 0.0;
        
        int endVal = startVal;
        while (endVal < json.length() && (Character.isDigit(json.charAt(endVal)) || json.charAt(endVal) == '.')) {
            endVal++;
        }
        
        try {
            String value = json.substring(startVal, endVal);
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Helper to extract a string value from JSON string
    private String extractString(String json, String key) {
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) return "";
        
        // Find the starting quote of the value
        int startQuote = json.indexOf("\"", keyIndex + key.length());
        if (startQuote == -1) return "";
        
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return "";
        
        return json.substring(startQuote + 1, endQuote);
    }

    private void updateEcoSuggestions(double temp, String condition) {
        // Logic to update UI based on REAL weather data
        if (condition.equalsIgnoreCase("Rain") || condition.equalsIgnoreCase("Drizzle") || condition.equalsIgnoreCase("Thunderstorm")) {
            gardeningTipLabel.setText("Nature is watering your plants! Collect rainwater if you can.");
            airQualityLabel.setText("AQI: Good (Rain clears the air)");
            ecoIcon.setFill(javafx.scene.paint.Color.web("#4682B4")); // SteelBlue
        } else if (temp > 25) {
            gardeningTipLabel.setText("Water plants early morning or late evening to save water.");
            airQualityLabel.setText("AQI: Check local reports (Heat may increase ozone)");
            ecoIcon.setFill(javafx.scene.paint.Color.web("#FFD700")); // Gold
        } else if (temp < 10) {
            gardeningTipLabel.setText("Protect sensitive plants from the cold.");
            airQualityLabel.setText("AQI: Good");
            ecoIcon.setFill(javafx.scene.paint.Color.web("#87CEEB")); // SkyBlue
        } else {
            gardeningTipLabel.setText("Great conditions for planting or weeding.");
            airQualityLabel.setText("AQI: Good");
            ecoIcon.setFill(javafx.scene.paint.Color.web("#2E8B57")); // ForestGreen
        }
    }

    private void animateEcoIcon() {
        ScaleTransition st = new ScaleTransition(Duration.millis(1000), ecoIcon);
        st.setByX(0.2f);
        st.setByY(0.2f);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
}

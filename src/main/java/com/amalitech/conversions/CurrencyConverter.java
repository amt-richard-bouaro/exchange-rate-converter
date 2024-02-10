package com.amalitech.conversions;

import com.amalitech.extensions.APIConnection;
import com.amalitech.extensions.HttpRequestMethods;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * CurrencyConverter class responsible for handling currency conversions.
 * Leverages external API for real-time currency rates.
 *
 * @author amalitech
 * @version 1.0
 */
public class CurrencyConverter {
    
    /**
     * API connection object for handling API interactions.
     */
    private final APIConnection apiConnection;
    /**
     * API key required for authorized API access.
     */
    private final String apiKey;
    
    /**
     * Base URL for the currency exchange API.
     */
    private String baserApiUrl = "https://v6.exchangerate-api.com/v6";
    
    /**
     * Gson instance for JSON parsing.
     */
    Gson gson = new Gson();
    
    /**
     * Constructs a new CurrencyConverter instance.
     *
     * @param apiConnection API connection object.
     * @param apiKey        API key for authorized API access.
     */
    public CurrencyConverter(
            APIConnection apiConnection,
            String apiKey
    ) {
        this.apiConnection = apiConnection;
        this.apiKey = apiKey;
    }
    
    /**
     * Fetches a list of supported currency codes from the external API.
     *
     * @return String array of supported currency codes.
     * @throws IOException if an error occurs while fetching data.
     */
    public String[] getSupportedCurrencyCodes() throws IOException {
        try {
            StringBuilder httpResponse = getExchangeRatesResponse("AED");
            JsonObject rates = getConversionRates(httpResponse);
            List<String> keys = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : rates.entrySet()) {
                keys.add(entry.getKey());
            }
            return keys.toArray(new String[0]);
        } catch (IOException e) {
            throw new IOException("Failed to fetch supported currencies: " + e.getMessage());
        }
        
    }
    
    /**
     * Checks if a given currency code is valid and supported by the API.
     *
     * @param currency the currency code to validate.
     * @return true if the currency is valid, false otherwise.
     * @throws IOException if an error occurs while fetching data.
     */
    public boolean isCurrencyValid(String currency) throws IOException {
        try {
            return Arrays.asList(getSupportedCurrencyCodes()).contains(currency);
        } catch (IOException e) {
            throw new IOException("Failed to validate currency: " + e.getMessage());
        }
    }
    
    /**
     * Converts a given amount from one currency to another using the external API.
     *
     * @param baseCurrencyCode the code of the currency to convert from.
     * @param quoteCurrencyCode the code of the currency to convert to.
     * @param amount the amount to convert (double).
     * @return the converted amount (double).
     * @throws IOException if an error occurs while fetching data or invalid currencies are provided.
     * @throws IllegalArgumentException if the amount is negative.
     */
    public double getExchangeAmount(
            String baseCurrencyCode,
            String quoteCurrencyCode,
            double amount) throws IOException {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        if (!isCurrencyValid(baseCurrencyCode) || !isCurrencyValid(quoteCurrencyCode)) {
            throw new IOException("Invalid base or quote currency.");
        }
        
        try {
            StringBuilder exchangeRatesResponse = getExchangeRatesResponse(baseCurrencyCode);
            JsonObject conversionRates = getConversionRates(exchangeRatesResponse);
            double baseToQuoteRate = conversionRates.get(quoteCurrencyCode).getAsDouble();
            return baseToQuoteRate * amount;
        } catch (IOException e) {
            throw new IOException("Failed to retrieve exchange rate: " + e.getMessage());
        }
    }
    
    /**
     * Fetches the exchange rates response from the external API for a given base currency.
     *
     * @param baseCurrencyCode the base currency code.
     * @return StringBuilder containing the API response.
     * @throws IOException if an error occurs during the API request.
     */
    private StringBuilder getExchangeRatesResponse(String baseCurrencyCode) throws IOException {
        try {
            URL exchangeRateApiUrl = apiConnection.createExchangeRateApiUrl(baserApiUrl, apiKey, "latest/" + baseCurrencyCode);
            HttpURLConnection connection = apiConnection.establishConnection(exchangeRateApiUrl, HttpRequestMethods.GET);
            
            // Handle specific HTTP errors with informative messages
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw new IOException("Unauthorized API access: Invalid API key?");
                } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    throw new IOException("API endpoint not found: Check the base currency code.");
                } else {
                    throw new IOException("API request failed with status code: " + connection.getResponseCode());
                }
            }
            
            return apiConnection.getResponse(connection);
        } catch (IOException e) {
            throw new IOException("Failed to fetch exchange rates: " + e.getMessage());
        } finally {
            apiConnection.close();
        }
    }
    
    
    /**
     * Extracts the conversion rates from the API response.
     *
     * @param httpResponse the API response as a StringBuilder.
     * @return JsonObject containing the conversion rates.
     * @throws JsonSyntaxException if the JSON parsing fails.
     */
    private JsonObject getConversionRates(StringBuilder httpResponse) throws JsonSyntaxException {
        JsonObject jsonResponse = gson.fromJson(httpResponse.toString(), JsonObject.class);
        return jsonResponse.getAsJsonObject("conversion_rates");
    }
    
}

package com.amalitech.conversions;

import com.amalitech.extensions.APIConnection;
import com.amalitech.extensions.HttpRequestMethods;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencyConverter {
    
    private final APIConnection apiConnection;
    private final String apiKey;
    
    public CurrencyConverter(APIConnection apiConnection, String apiKey) {
        this.apiConnection = apiConnection;
        this.apiKey = apiKey;
    }
    
  
    Gson gson = new Gson();
    private String baserApiUrl = "https://v6.exchangerate-api.com/v6";
    
    private StringBuilder getExchangeRatesResponse(String baseCurrencyCode) throws IOException {
        
        URL exchangeRateApiUrl = apiConnection.createExchangeRateApiUrl(baserApiUrl, apiKey,
                "latest/%s".formatted(baseCurrencyCode));
        
        HttpURLConnection connection = apiConnection.establishConnection(exchangeRateApiUrl, HttpRequestMethods.GET);
        
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                
                throw new ConnectException("");
            }
            
            return apiConnection.getResponse(connection);
        } catch (Exception e) {
            throw new ConnectException("ConnectionError: Connection of https://v6.exchangerate-api.com/v6 failed. %s".formatted(
                    connection.getResponseMessage()));
        } finally {
            apiConnection.close();
        }
        
    }
    
    
    private JsonObject getConversionRates(StringBuilder httpResponse) {
        
        JsonObject jsonResponse = gson.fromJson(httpResponse.toString(), JsonObject.class);
        
        return jsonResponse.getAsJsonObject("conversion_rates");
    }
    
    /**
     * Retrieves a list of supported currencies.
     * @return
     * This method returns string array of currencies support by this library
     * @throws IOException
     * If there is an issue fetching the exchange rates (e.g.,  unable to establish connection)
     * **/
    public  String[] getSupportedCurrencyCodes() throws IOException {
        JsonObject rates = getConversionRates(getExchangeRatesResponse("AED"));
        
        List<String> keys = new ArrayList<>();
        
        for (Map.Entry<String, JsonElement> entry : rates.entrySet()) {
            
            keys.add(entry.getKey());
        }
        
        return keys.toArray(new String[0]);
        
    }
    
    public boolean isCurrencyValid(String currency) throws IOException {
        
        List<String> currencies = List.of(getSupportedCurrencyCodes());
        
        return currencies.contains(currency);
        
    }
    
    /**
     * @param baseCurrencyCode the currency to convert from
     * @param quoteCurrencyCode the currency to convert to
     * @param amount an amount to convert
     * @return amount of type double
     * @throws IOException
    * */
    public double getExchangeAmount(
            String baseCurrencyCode,
            String quoteCurrencyCode,
            double amount
    ) throws IOException {
        
        if (!isCurrencyValid(baseCurrencyCode) || !isCurrencyValid(quoteCurrencyCode)) throw new IOException();
        
        StringBuilder exchangeRatesResponse = getExchangeRatesResponse(baseCurrencyCode);
        JsonObject conversionRates = getConversionRates(exchangeRatesResponse);
        double baseCurrencyToQuoteCurrency = conversionRates.get(quoteCurrencyCode)
                                                            .getAsDouble();
        return baseCurrencyToQuoteCurrency * amount;
    }
    
    
}

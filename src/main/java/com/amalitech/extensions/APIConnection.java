package com.amalitech.extensions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * APIConnection class responsible for managing HTTP connections to external APIs.
 */
public class APIConnection implements  AutoCloseable {
    
    /**
     * The current active HTTP connection.
     */
    private HttpURLConnection conn;
    
    /**
     * Establishes a connection to a specified URL using the given HTTP request method.
     *
     * @param url the URL to connect to.
     * @param httpRequestMethod the HTTP request method (GET, POST, etc.).
     * @return the established HttpURLConnection.
     * @throws IOException if an error occurs during connection establishment.
     */
    public  HttpURLConnection establishConnection(
            URL url,
            HttpRequestMethods httpRequestMethod
    ) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(httpRequestMethod.name());
        
        this.conn = connection;
        
        return connection;
    }
    
    
    /**
     * Creates a URL for the exchange rate API with the given parameters.
     *
     * @param apiBaseUrl the base URL of the exchange rate API.
     * @param apiKey the API key for authorized access.
     * @param baseCurrencyCode the base currency code.
     * @return the constructed URL.
     * @throws MalformedURLException if the URL is invalid.
     */
    public  URL createExchangeRateApiUrl(
            String apiBaseUrl,
            String apiKey,
            String baseCurrencyCode
    ) throws MalformedURLException {
        
        String url = String.format("%s/%s/%s", apiBaseUrl, apiKey, baseCurrencyCode);
        
        return URI.create(url)
                  .toURL();
        
    }
    
    /**
     * Retrieves the response body from a given HttpURLConnection as a StringBuilder.
     *
     * @param connection the HttpURLConnection to read the response from.
     * @return StringBuilder containing the response body.
     * @throws IOException if an error occurs during response reading.
     */
    
    public  StringBuilder getResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response;
        }
    }
    
    /**
     * Closes the active connection, if any.
     */
    @Override
    public void close() {
        if (conn != null) {
            conn.disconnect();
        }
    }

}

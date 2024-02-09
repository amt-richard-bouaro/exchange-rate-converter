package com.amalitech.extensions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class APIConnection implements  AutoCloseable {
    HttpURLConnection conn;
    
    public  HttpURLConnection establishConnection(
            URL url,
            HttpRequestMethods httpRequestMethod
    ) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(httpRequestMethod.name());
        
        this.conn = connection;
        
        return connection;
    }
    
    public  URL createExchangeRateApiUrl(
            String apiBaseUrl,
            String apiKey,
            String baseCurrencyCode
    ) throws MalformedURLException {
        
        String url = String.format("%s/%s/%s", apiBaseUrl, apiKey, baseCurrencyCode);
        
        return URI.create(url)
                  .toURL();
        
    }
    
    public  StringBuilder getResponse(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        
        StringBuilder response = new StringBuilder();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response;
    }
    
    @Override
    public void close(){
    conn.disconnect();
    }

}

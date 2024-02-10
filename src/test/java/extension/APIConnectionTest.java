package extension;

import com.amalitech.extensions.APIConnection;
import com.amalitech.extensions.GlobalAPIConnection;
import com.amalitech.extensions.HttpRequestMethods;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@ExtendWith(MockitoExtension.class)
public class APIConnectionTest {
    
    private APIConnection apiConnection ;
    
    @Before
    public void setUp() {
        apiConnection = GlobalAPIConnection.getInstance();
    }
    
    @After
    public void tearDown() throws Exception {
        apiConnection.close();
    }
    
    @Test
    public void testEstablishConnection() throws IOException {
        URL url = new URL("https://v6.exchangerate-api.com/v6/5776r77065575799/latest/USD");
        //Beacuase apiKey (5776r77065575799) is fake, connection will return status forbidden 403
        HttpURLConnection connection = apiConnection.establishConnection(url, HttpRequestMethods.GET);
        Assert.assertNotNull(connection);
        Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, connection.getResponseCode());
    }
    
    @Test(expected = MalformedURLException.class)
    public void testEstablishConnectionWithInvalidURL() throws IOException {
        URL url = new URL("invalid://url");
        apiConnection.establishConnection(url, HttpRequestMethods.GET);
    }
    
    @Test
    public void testCreateExchangeRateApiUrl() throws MalformedURLException {
        String apiBaseUrl = "https://v6.exchangerate-api.com/v6";
        String apiKey = "api_key";
        String baseCurrencyCode = "USD";
        URL expectedUrl = new URL("https://v6.exchangerate-api.com/v6/api_key/USD");
        URL actualUrl = apiConnection.createExchangeRateApiUrl(apiBaseUrl, apiKey, baseCurrencyCode);
        Assert.assertEquals(expectedUrl, actualUrl);
    }
    
    @Test
    public void testGetResponse() throws IOException {
        HttpURLConnection mockConnection = Mockito.mock(HttpURLConnection.class);
        Mockito.when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream("Test Response".getBytes()));
        StringBuilder response = apiConnection.getResponse(mockConnection);
        Assert.assertEquals("Test Response", response.toString());
    }
    
    @Test
    public void testClose() throws IOException {
        HttpURLConnection mockConnection = Mockito.mock(HttpURLConnection.class);
        URL url = new URL("https://v6.exchangerate-api.com/v6");
//       mockConnection = apiConnection.establishConnection(url, HttpRequestMethods.GET);
        mockConnection.disconnect();
        Mockito.verify(mockConnection).disconnect();
    }
}

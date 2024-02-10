package converter;

import com.amalitech.conversions.CurrencyConverter;
import com.amalitech.extensions.APIConnection;
import com.amalitech.extensions.GlobalAPIConnection;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Tests for the CurrencyConverter class, using a real API connection.
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test.properties")
@TestComponent
public class CurrencyConverterTest {
    
    /**
     * The CurrencyConverter instance to be tested.
     */
    private CurrencyConverter converter;
    
    /**
     * Provides access to test configuration properties.
     */
    @Autowired
    private Environment env;
    
    /**
     * Sets up the test environment before each test method.
     */
    @Before
    public void setUp() {
        String testApiKey = env.getProperty("test.api.key");
        APIConnection apiConnection = GlobalAPIConnection.getInstance();
        converter = new CurrencyConverter(apiConnection, testApiKey);
    }
    
    /**
     * Tests that the converter can retrieve a list of supported currency codes.
     */
    @Test
    public void testGetSupportedCurrencyCodes() throws IOException {
        String[] currencies = converter.getSupportedCurrencyCodes();
        Assert.assertTrue(currencies.length > 0);
    }
    
    /**
     * Tests that the converter correctly validates a valid currency code.
     */
    @Test
    public void testIsCurrencyValid_ValidCurrency() throws IOException {
        String[] currencies = converter.getSupportedCurrencyCodes();
        String validCurrency = currencies[0];
        Assert.assertTrue(converter.isCurrencyValid(validCurrency));
    }
    
    /**
     * Tests that the converter can convert an amount between valid currencies.
     */
    @Test
    public void testGetExchangeAmount_ValidCurrencies() throws IOException {
        String baseCurrency = "USD";
        String quoteCurrency = "EUR";
        double amount = 100.0;
        double convertedAmount = converter.getExchangeAmount(baseCurrency, quoteCurrency, amount);
        Assert.assertTrue(convertedAmount > 0);
    }
}

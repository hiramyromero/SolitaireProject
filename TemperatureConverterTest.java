// importing what I need for JUnit
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TemperatureConverterTest {

    // testing Celsius to Fahrenheit conversion
    @Test
    void celsiusToFahrenheit_returnsCorrectValue() {
        TemperatureConverter converter = new TemperatureConverter();
        assertEquals(32.0, converter.celsiusToFahrenheit(0));
    }

    // testing Fahrenheit to Celsius conversion
    @Test
    void fahrenheitToCelsius_returnsCorrectValue() {
        TemperatureConverter converter = new TemperatureConverter();
        assertEquals(0.0, converter.fahrenheitToCelsius(32));
    }
}

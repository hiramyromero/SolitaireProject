// simple class to convert temperatures
public class TemperatureConverter {

    // converts Celsius to Fahrenheit
    public double celsiusToFahrenheit(double c) {
        return (c * 9 / 5) + 32;
    }

    // converts Fahrenheit to Celsius
    public double fahrenheitToCelsius(double f) {
        return (f - 32) * 5 / 9;
    }
}

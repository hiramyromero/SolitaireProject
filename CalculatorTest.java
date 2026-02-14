// importing what I need for JUnit 5 testing
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    // testing that add() actually returns the correct sum
    @Test
    void add_returnsCorrectSum() {
        Calculator calc = new Calculator();
        assertEquals(5, calc.add(2, 3));
    }

    // testing that subtract() gives the right difference
    @Test
    void subtract_returnsCorrectDifference() {
        Calculator calc = new Calculator();
        assertEquals(2, calc.subtract(5, 3));
    }
}

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    @Test
    void add_returnsCorrectSum() {
        Calculator calc = new Calculator();
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void subtract_returnsCorrectDifference() {
        Calculator calc = new Calculator();
        assertEquals(2, calc.subtract(5, 3));
    }
}

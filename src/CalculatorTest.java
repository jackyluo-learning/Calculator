import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class CalculatorTest {

    private final Calculator calculator = new Calculator();
    @Test
    public void normalTest() {
        calculator.setNewNum(new BigDecimal(3));
        calculator.setCurOperator("+");
        calculator.setNewNum(new BigDecimal(5));
        calculator.calc();
        Assert.assertEquals("8.00", calculator.display());
        calculator.setCurOperator("*");
        calculator.setNewNum(new BigDecimal(2));
        calculator.calc();
        Assert.assertEquals("16.00", calculator.display());
    }

    @Test
    public void undoRedoTest() {
        calculator.setNewNum(new BigDecimal(3));
        calculator.setCurOperator("+");
        calculator.setNewNum(new BigDecimal(5));
        calculator.calc();
        calculator.setCurOperator("*");
        calculator.setNewNum(new BigDecimal(2));
        calculator.calc();
        calculator.undo();
        Assert.assertEquals("8.00", calculator.display());
        calculator.setCurOperator("+");
        calculator.setNewNum(new BigDecimal(2));
        calculator.calc();
        Assert.assertEquals("10.00", calculator.display());
        calculator.undo();
        Assert.assertEquals("8.00", calculator.display());
        calculator.undo();
        Assert.assertEquals("3.00", calculator.display());
        calculator.redo();
        Assert.assertEquals("8.00", calculator.display());
        calculator.redo();
        Assert.assertEquals("10.00", calculator.display());
        calculator.redo();
        Assert.assertEquals("10.00", calculator.display());
        calculator.redo();
        Assert.assertEquals("10.00", calculator.display());
    }
}

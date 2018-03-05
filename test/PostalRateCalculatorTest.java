import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class PostalRateCalculatorTest {
	/*Input Variables:From: Postal Code
	 * •To: Postal Code•Length: CM
	 * •Width: CM
	 * •Height: CM
	 * •Weight: KG
	 * •Post Type: [Regular, Xpress, Priority]
	 */
	@Before
	public void setup(){}

	@After
    public void tearDown(){
    }
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	@Test
	//Test 1
	public void noArgs() {
	    PostalRate.main(null);
	    String expected = "Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\n";
        assertEquals(expected , systemOutRule.getLogWithNormalizedLineSeparator());
	}
	
	@Test
    //Test 2
	public void lessArgs(){
		String args[] = new String[]{"10"};
		PostalRate.main(args);
		String expected = "Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\n";
		assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
	}

	@Test
    //Test 3
	public void manyArgs(){
        String args[] = new String[]{"10", "10", "10", "10", "10", "10", "10", "10"};
        PostalRate.main(args);
        String expected = "Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
	}

    @Test
    //Test 4
    public void outOfRangeHigh() {
        String args[] = new String[]{"201","201","201","201","201","201","201"};
        PostalRate.main(args);
        String expected = "Numbers must be at most 200\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

	@Test
    //Test 5
	public void outOfRangeLow() {
        String args[] = new String[]{"0.0001","0.0001","0.0001","0.0001","0.0001","0.0001","0.0001"};
        PostalRate.main(args);
        String expected = "Numbers must be at least 0.001\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
	}

	@Test
    //Test 6
    public void addressTooLarge() {
        String args[] = new String[]{"A1A1A1A","A1A1A1A","10","10","10","10","10"};
        PostalRate.main(args);
        String expected = "Input a valid address: X#X#X#\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 7
    public void addressTooSmall(){
          String args[] = new String[]{"A1A","A1A","10","10","10","10","10"};
        PostalRate.main(args);
        String expected = "Input a valid address: X#X#X#\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 8
    public void addressOnlyNumbers(){
        String args[] = new String[]{"111111","111111","10","10","10","10","10"};
        PostalRate.main(args);
        String expected = "Input a valid address: X#X#X#\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 9
    public void addressOnlyLetters(){
        String args[] = new String[]{"AAAAAA","AAAAAA","10","10","10","10","10"};
        PostalRate.main(args);
        String expected = "Input a valid address: X#X#X#\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 10
    public void addressLowerCase(){
        String args[] = new String[]{"a1a1a1","a1a1a1","10","10","10","10","Xpresspost"};
        PostalRate.main(args);
        assertEquals("Input a valid address: X#X#X#\n", systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 11
    public void invalidPostType(){
        String args[] = new String[]{"A1A1A1","A1A1A1","10","10","10","10","aaa"};
        PostalRate.main(args);
        String expected = "Post types available: “Xpresspost”, “Regular”, “Priority”\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 12
    public void weightPrecision(){
        String args[] = new String[]{"A1A1A1","A1A1A1","10","10","10","25.0001","Xpresspost"};
        PostalRate.main(args);
        String expected = "Weight must have at must 3 decimals\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 13
    public void largeWeight(){
        String args[] = new String[]{"A1A1A1","A1A1A1","10","10","10","30.01","Xpresspost"};
        PostalRate.main(args);
        String expected = "Weight must be at most 30.00 kg\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 14
    public void dimensionPrecision(){
        String args[] = new String[]{"A1A1A1","A1A1A1","0.001","10.002","30.004","30","Xpresspost"};
        PostalRate.main(args);
        String expected = "Dimensions must have at most 2 decimals\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 15
    public void dimensionsOverLimit(){
        String args[] = new String[]{"A1A1A1","A1A1A1","100","75","75","10","Xpresspost"};
        PostalRate.main(args);
        String expected = "Length + 2(Width + Height) must be at most 300 cm\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 16
    public void dimensionsTooSmall(){
        String args[] = new String[]{"A1A1A1","A1A1A1","0.0099","0.009","0.001","10","Xpresspost"};
        PostalRate.main(args);
        String expected = "Dimensions must have at most 2 decimals\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 17
    public void unassociatedAddress(){
        String args[] = new String[]{"D1D1D1","D1D1D1","10","10","10","10","Xpresspost"};
        PostalRate.main(args);
        String expected = "Invalid Canadian Postal Code\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 18
    public void nunavutPostalType(){
        String args[] = new String[]{"X0A0H0","X0A0H0","10","10","10","10","Priority"};
        PostalRate.main(args);
        String expected = "Nunavut and Northern Territories do not have Priority shipping\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 19
    public void validRegularRate(){
        String args[] = new String[]{"T6G2R3", "H9W6C3", "10", "10", "10", "10", "Regular"};
        PostalRate.main(args);
        String expected = "Price: $30.94\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 20
    public void validXpressRate(){
        String args[] = new String[]{"T6G2R3", "H9W6C3", "10", "10", "10", "10", "Xpresspost"};
        PostalRate.main(args);
        String expected = "Price: $81.83\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 21
    public void validPriorityRate(){
        String args[] = new String[]{"T6G2R3", "H9W6C3", "10", "10", "10", "10", "Priority"};
        PostalRate.main(args);
        String expected = "Price: $108.29\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    //Test 22
    public void apiExceptions(){
        String args[] = new String[]{"X1D1D1", "X1D1D1", "10", "10", "10", "10", "Regular"};
        PostalRate.main(args);
        String expected = "The Postal Code is invalid.\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }
}

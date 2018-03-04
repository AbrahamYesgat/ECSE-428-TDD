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
	//Expected String as follows: Postal code source, destination, width length height weight postal type"
	public void noArgs() {
	    PostalRate.main(null);
	    String expected = "Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\n";
        assertEquals(expected , systemOutRule.getLogWithNormalizedLineSeparator());
	}
	
	@Test
	public void lessArgs(){
		String args[] = new String[]{"10"};
		PostalRate.main(args);
		String expected = "Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\n";
		assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
	}

	@Test
	public void manyArgs(){
        String args[] = new String[]{"10", "10", "10", "10", "10", "10", "10", "10"};
        PostalRate.main(args);
        String expected = "Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
	}

    @Test
    public void outOfRangeHigh() {
        String args[] = new String[]{"201","201","201","201","201","201","201"};
        PostalRate.main(args);
        String expected = "Numbers must be at most 200\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

	@Test
	public void outOfRangeLow() {
        String args[] = new String[]{"0.0001","0.0001","0.0001","0.0001","0.0001","0.0001","0.0001"};
        PostalRate.main(args);
        String expected = "Numbers must be at least 0.001\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
	}

	@Test
    public void addressTooLarge() {
        String args[] = new String[]{"A1A1A1A","A1A1A1A","10","10","10","10","10"};
        PostalRate.main(args);
        String expected = "Input a valid address: X#X#X#\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void addressTooSmall(){
          String args[] = new String[]{"A1A","A1A","10","10","10","10","10"};
        PostalRate.main(args);
        String expected = "Input a valid address: X#X#X#\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void addressOnlyNumbers(){
        String args[] = new String[]{"111111","111111","10","10","10","10","10"};
        PostalRate.main(args);
        String expected = "Input a valid address: X#X#X#\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void addressOnlyLetters(){
        String args[] = new String[]{"AAAAAA","AAAAAA","10","10","10","10","10"};
        PostalRate.main(args);
        String expected = "Input a valid address: X#X#X#\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void addressLowerCase(){
        String args[] = new String[]{"a1a1a1","a1a1a1","10","10","10","10","xpress"};
        PostalRate.main(args);
        assertEquals("", systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void invalidPostType(){
        String args[] = new String[]{"A1A1A1","A1A1A1","10","10","10","10","aaa"};
        PostalRate.main(args);
        String expected = "Post types available: “Xpress”, “Regular”, “Priority”\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void weightPrecision(){
        String args[] = new String[]{"A1A1A1","A1A1A1","10","10","10","25.0001","xpress"};
        PostalRate.main(args);
        String expected = "Weight must have at must 3 decimals\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void largeWeight(){
        String args[] = new String[]{"A1A1A1","A1A1A1","10","10","10","30.01","xpress"};
        PostalRate.main(args);
        String expected = "Weight must be at most 30.00 kg\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void dimensionPrecision(){
        String args[] = new String[]{"A1A1A1","A1A1A1","0.001","10.002","30.004","30","xpress"};
        PostalRate.main(args);
        String expected = "Dimensions must have at most 2 decimals\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void dimensionsOverLimit(){
        String args[] = new String[]{"A1A1A1","A1A1A1","100","75","75","10","xpress"};
        PostalRate.main(args);
        String expected = "Length + 2(Width + Height) must be at most 300 cm\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

    @Test
    public void dimensionsTooSmall(){
        String args[] = new String[]{"A1A1A1","A1A1A1","0.0099","0.009","0.001","10","xpress"};
        PostalRate.main(args);
        String expected = "Dimensions must have at most 2 decimals\n";
        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
    }

//    @Test
//    public void unassociatedAddress(){
//        String args[] = new String[]{"D1D1D1","D1D1D1","10","10","10","10","xpress"};
//        PostalRate.main(args);
//        String expected = "Invalid Canadian Postal Code\n";
//        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
//    }
//
//    @Test
//    public void nunavutPostalType(){
//        String args[] = new String[]{"D1D1D1","D1D1D1","10","10","10","10","priority"};
//        PostalRate.main(args);
//        String expected = "Nunavut and Northern Territories do not have Priority shipping\n";
//        assertEquals(expected, systemOutRule.getLogWithNormalizedLineSeparator());
//    }
//
//    @Test
//    public void validRegularRate(){
//
//    }
//
//    @Test
//    public void validXpressRate(){
//
//    }
//
//    @Test
//    public void validPriorityRate(){
//
//    }
}

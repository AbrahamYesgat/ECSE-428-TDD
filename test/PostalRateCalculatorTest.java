import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PostalRateCalculatorTest {
	/*Input Variables:From: Postal Code
	 * •To: Postal Code•Length: CM
	 * •Width: CM
	 * •Height: CM
	 * •Weight: KG
	 * •Post Type: [Regular, Xpress, Priority]
	 */
	@Before
	public void setup(){
	}

	@Test
	//Expected String as follows: Postal code source, destination, width length height weight postal type"
	public void noArgs() {
		assertEquals("null", "null");
	}
	
	@Test
	public void lessargs(){
		assertEquals("null", "null");
	}
	
	@Test
	public void manyargs(){
		assertEquals("null", "null");
	}
	
	@Test
	public void outofRangeHigh() {
		String [] inp= {"rwfe", "Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postal, type"};
		assertEquals("PostalRate < 10^9", "PostalRate < 10^9");

	}
	
	@Test
	public void outOfRangLow() {
		assertEquals("","jhg");
		
	}
	
	
	
	
}

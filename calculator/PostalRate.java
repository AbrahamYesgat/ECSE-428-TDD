
public class PostalRate {
	public double length, width, height, weight;
	public String destPostalCode;
	public String sourcePostalCode;
	public PostType postType;
	public Destination dest;

	public enum Destination {
		CANADA, USA, INTERNATIONAL
	}
	public enum PostType {
		REGULAR, EXPRESS, PRIORITY
	}

	public static void main (String args[]) {
		if((args==null) || (args.length != 8)) {
			System.out.println("Usage: postalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\")");
			return;
		}
		String postalRate= args[0];
		String sourcePostalCode= args[1];
		String destPostalCode=args[2];
		float width, weight, height, length;
		width= Float.valueOf(args[3]);
		weight=Float.valueOf(args[4]);
		height=Float.valueOf(args[5]);
		length=Float.valueOf(args[6]);
		String postalType=args[7];
		System.out.println("Usage: postalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype");
		
	}
	
}

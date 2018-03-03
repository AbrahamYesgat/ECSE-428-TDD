
public class PostalRate {
	public double length, width, height, weight;
	public String destPostalCode;
	public String sourcePostalCode;
	public PostType postType;
	public Destination dest;

	public enum Destination {
        AB, BC, MB, NB, NL, NS, NT, NU, ON, PE, QC, SK, YT
	}
	public enum PostType {
		REGULAR, EXPRESS, PRIORITY
	}

	public static void main (String args[]) {
		if(args==null || args.length != 7) {
			System.out.print("Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\n");
		}
		else {
		    boolean numeberInput = false;
		    boolean numberPrecision = false;
		    boolean invalidAddress = false;
		    for(int i = 2; i < 6; i++) {
                if (Float.valueOf(args[i]) > 200) {
                    numeberInput = true;
                } else if (Float.valueOf(args[i]) < 0.001) {
                    numberPrecision = true;
                }
            }
            for(int i = 0; i<2; i++){
		        if(args[i].length() != 6){
		            invalidAddress = true;
                }
                else {
		            if(!args[i].matches("^([A-Z]|[a-z]){1}\\d{1}([A-Z]|[a-z]){1}\\d{1}([A-Z]|[a-z]){1}\\d{1}")){
		               invalidAddress = true;
                    }
                }
            }
            if(numeberInput){
                System.out.print("Numbers must be at most 200\n");
            }
            else if(numberPrecision){
                System.out.print("Numbers must be at least 0.001\n");
            }
            else if(invalidAddress){
                System.out.print("Input a valid address: X#X#X#\n");
            }
            else if(!(args[6].equalsIgnoreCase("Xpress")) &&!(args[6].equalsIgnoreCase("Regular"))&&!(args[6].equalsIgnoreCase("Priority"))){
		        System.out.print("Post types available: “Xpress”, “Regular”, “Priority”\n");
            }
            else if(Float.valueOf(args[5]) > 30.01){
		        System.out.print("Weight must be at most 30.00 kg\n");
            }
        }
	}

	public static boolean validDimensions(float length, float width, float height){
	    boolean validDimensions = false;
	    return validDimensions;
    }
}

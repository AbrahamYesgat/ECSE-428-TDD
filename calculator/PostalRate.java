
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
            else if(weightPrecision(args[5]) > 3){
		        System.out.print("Weight must have at must 3 decimals\n");
            }
            else if(weightPrecision(args[2])>2||weightPrecision(args[3])>2||weightPrecision(args[4])>2){
		        System.out.print("Dimensions must have at most 2 decimals\n");
            }
            else if(!validDimensions(args[2], args[3], args[4])){
		        System.out.print("Sum of dimensions must be at most 200 cm\n");
            }
        }
	}
    public static boolean validDimensions(String length, String width, String height){
        boolean validDimensions = false;
        float l = Float.valueOf(length);
        float w = Float.valueOf(width);
        float h = Float.valueOf(height);
        if(l+w+h<=200.00){
            validDimensions = true;
        }
        return validDimensions;
    }
	public static int weightPrecision(String weight){
        int integerPlaces = weight.indexOf('.');
        if(integerPlaces == -1) {
            return 0;
        }
        int decimalPlaces = weight.length() - integerPlaces - 1;
        return decimalPlaces;
    }
}

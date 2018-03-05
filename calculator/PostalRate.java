import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.bind.JAXBContext;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import ca.canadapost.cpcdp.rating.generated.messages.Messages;
import ca.canadapost.cpcdp.rating.generated.rating.MailingScenario;
import ca.canadapost.cpcdp.rating.generated.rating.PriceQuotes;
import ca.canadapost.cpcdp.rating.generated.rating.MailingScenario.Destination;
import ca.canadapost.cpcdp.rating.generated.rating.MailingScenario.Destination.Domestic;
import ca.canadapost.cpcdp.rating.generated.rating.MailingScenario.ParcelCharacteristics.Dimensions;

/**
 * The postal rate class is class that calculates the price of shipping based off input
 * passed by the user from the command line.
 * Specifically, it requires a source postal code, destination postal code, width of the
 * package, length , height, weight and the postal type method they wish to ship it with.
 * The class takes advantage of Canada Post's price calculating API in order to get the price.
 * NOTE: For some reason, there is a discount being applied to the API we are using, so the
 * prices being given are slightly less than the actual values.
 * @author Christos Panaritis (260739936) and Abraham Yesgat (260740998)
 */
public class PostalRate {
	public static float length, width, height, weight;
	private Client aClient;
	private static final String LINK = "https://ct.soa-gw.canadapost.ca/rs/ship/price";

    /**
     * Runs the whole program
     * @param args 7 inputs that correspond to the following:
     *             sourcePostalCode, destPostalCode, width, length, height, weight, postaltype
     */
	public static void main (String args[]) {
	    //Test 1 & Test 2 & Test 3
		if(args==null || args.length != 7) {
			System.out.print("Usage: PostalRate sourcePostalCode, destPostalCode, width, length, height, weight, postaltype\n");
		}
		else {
		    boolean numeberInput = false;
		    boolean numberPrecision = false;
		    boolean invalidAddress = false;
		    for(int i = 2; i < 6; i++) {
		        //Test 4
                if (Float.valueOf(args[i]) > 200) {
                    numeberInput = true;
                }
                //Test 5
                else if (Float.valueOf(args[i]) < 0.001) {
                    numberPrecision = true;
                }
            }
            for(int i = 0; i<2; i++){
		        //Test 6 & Test 7
		        if(args[i].length() != 6){
		            invalidAddress = true;
                }
                else {
		            //Test 8 & Test 9 & Test 10
		            if(!args[i].matches("^([A-Z]){1}\\d{1}([A-Z]){1}\\d{1}([A-Z]){1}\\d{1}")){
		               invalidAddress = true;
                    }
                }
            }
            //Test 4
            if(numeberInput){
                System.out.print("Numbers must be at most 200\n");
            }
            //Test 5
            else if(numberPrecision){
                System.out.print("Numbers must be at least 0.001\n");
            }
            //Test 6, 7, 8, 9, 10
            else if(invalidAddress){
                System.out.print("Input a valid address: X#X#X#\n");
            }
            //Test 11
            else if(!(args[6].equals("Xpresspost")) &&!(args[6].equals("Regular"))&&!(args[6].equals("Priority"))){
		        System.out.print("Post types available: “Xpresspost”, “Regular”, “Priority”\n");
            }
            //Test 12
            else if(Float.valueOf(args[5]) > 30.01){
		        System.out.print("Weight must be at most 30.00 kg\n");
            }
            //Test 13
            else if(decimalPrecision(args[5]) > 3){
		        System.out.print("Weight must have at must 3 decimals\n");
            }
            //Test 14 & Test 16
            else if(decimalPrecision(args[2])>2||decimalPrecision(args[3])>2||decimalPrecision(args[4])>2){
		        System.out.print("Dimensions must have at most 2 decimals\n");
            }
            //Test 15
            else if(!validDimensions(args[2], args[3], args[4])){
		        System.out.print("Length + 2(Width + Height) must be at most 300 cm\n");
            }
            //Test 17
            else if(!getProvinceAddress(args[0]) && !getProvinceAddress(args[1])){
                System.out.print("Invalid Canadian Postal Code\n");
            }
            //Test 18
            else if((args[0].startsWith("X")||args[1].startsWith("X")) && args[6].equalsIgnoreCase("Priority")){
		        System.out.print("Nunavut and Northern Territories do not have Priority shipping\n");
            }
            //Test 19, 20, 21
            else {
                String sourcePC = args[0].toUpperCase();
                String destPC = args[1].toUpperCase();
                String postType = args[6];
                width = Float.valueOf(args[2]);
                length = Float.valueOf(args[3]);
                height = Float.valueOf(args[4]);
                weight = Float.valueOf(args[5]);

                // Your username, password and customer number are imported from the following file
                // CPCWS_Rating_Java_Samples/user.properties
                Properties userProps = new Properties();
                FileInputStream propInputStream;
                try {
                    propInputStream = new FileInputStream("user.properties");
                    userProps.load(propInputStream);
                    propInputStream.close(); // better in finally block
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace(System.out);
                    return;
                }
                String username = userProps.getProperty("username");
                String password = userProps.getProperty("password");
                String mailedBy = userProps.getProperty("mailedBy");

                // Create GetRates XML Request Object
                MailingScenario mailingScenario = new MailingScenario();
                mailingScenario.setCustomerNumber(mailedBy);
                MailingScenario.ParcelCharacteristics parcelCharacteristics = new MailingScenario.ParcelCharacteristics();
                parcelCharacteristics.setWeight(new BigDecimal(weight));
                Dimensions dim = new Dimensions();
                dim.setHeight(new BigDecimal(height));
                dim.setLength(new BigDecimal(length));
                dim.setWidth(new BigDecimal(width));
                parcelCharacteristics.setDimensions(dim);
                mailingScenario.setOriginPostalCode(sourcePC);
                Domestic domestic = new Domestic();
                domestic.setPostalCode(destPC);
                Destination destination = new Destination();
                destination.setDomestic(domestic);
                mailingScenario.setDestination(destination);
                mailingScenario.setParcelCharacteristics(parcelCharacteristics);
                // Execute GetRates Request
                PostalRate myClient = new PostalRate(username, password);
                ClientResponse resp = myClient.createMailingScenario(mailingScenario);
                InputStream respIS = resp.getEntityInputStream();

                // Example of using JAXB to parse xml response
                JAXBContext jc;
                try {
                    jc = JAXBContext.newInstance(PriceQuotes.class, Messages.class);
                    Object entity = jc.createUnmarshaller().unmarshal(respIS);
                    // Determine whether response data matches GetRatesInfo schema.
                    if (entity instanceof PriceQuotes) {
                        PriceQuotes priceQuotes = (PriceQuotes) entity;
                        for (Iterator<PriceQuotes.PriceQuote> iter = priceQuotes.getPriceQuotes().iterator(); iter.hasNext(); ) {
                            PriceQuotes.PriceQuote aPriceQuote = (PriceQuotes.PriceQuote) iter.next();
                            if (aPriceQuote.getServiceName().equalsIgnoreCase(postType) || ((aPriceQuote.getServiceName().equalsIgnoreCase("Regular Parcel") && postType.equals("Regular")))) {
                                System.out.print("Price: $" + aPriceQuote.getPriceDetails().getDue() + "\n");
                            }
                        }
                    }
                    //Test 22
                    else {
                        // Assume Error Schema
                        Messages messageData = (Messages) entity;
                        for (Iterator<Messages.Message> iter = messageData.getMessage().iterator(); iter.hasNext(); ) {
                            Messages.Message aMessage = (Messages.Message) iter.next();
                            System.out.print(aMessage.getDescription()+"\n");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }

                myClient.close();
            }
        }
	}

    /**
     * This methods checks if the dimensions of the parcel are within the limits imposed by Canada Post
     * @param length String that corresponds to the length of the parcel in cm.
     * @param width String that corresponds to the width of the parcel in cm.
     * @param height Sttring that corresponds to the height of the parcel in cm.
     * @return True if the dimensions are within the limit or false if they are above.
     */
    public static boolean validDimensions(String width, String length, String height){
        boolean validDimensions = false;
        float l = Float.valueOf(length);
        float w = Float.valueOf(width);
        float h = Float.valueOf(height);
        //Checking the dimension limit
        if(l+(w+h)*2<=300.00){
            validDimensions = true;
        }
        return validDimensions;
    }

    /**
     * This method checks the decimal precision of a number (that is written in a string).
     * @param number A number written as a string.
     * @return The amount of digits that are after the decimal point.
     */
	public static int decimalPrecision(String number){
        int integerPlaces = number.indexOf('.');
        //Returns 0 if the number does not contain a decimal.
        if(integerPlaces == -1) {
            return 0;
        }
        int decimalPlaces = number.length() - integerPlaces - 1;
        return decimalPlaces;
    }

    /**
     * This method checks whether the address is a valid provincial postal code.
     * @param PostalCode A string that represents a 6 character postal code.
     * @return True if it is a valid postal code and false when it does not.
     */
    public static boolean getProvinceAddress(String PostalCode) {
	    boolean dest = false;
	    if(PostalCode.startsWith("A")) dest=true;
	    if(PostalCode.startsWith("B")) dest=true;
	    if(PostalCode.startsWith("C")) dest=true;
	    if(PostalCode.startsWith("E")) dest=true;
	    if(PostalCode.startsWith("G") || PostalCode.startsWith("H") || PostalCode.startsWith("J")) dest=true;
	    if(PostalCode.startsWith("K") || PostalCode.startsWith("L") || PostalCode.startsWith("M") || PostalCode.startsWith("N")|| PostalCode.startsWith("P")) dest=true;
	    if(PostalCode.startsWith("R")) dest=true;
	    if(PostalCode.startsWith("S")) dest=true;
	    if(PostalCode.startsWith("T")) dest=true;
	    if(PostalCode.startsWith("V")) dest=true;
	    if(PostalCode.startsWith("X")) dest=true;
	    if(PostalCode.startsWith("Y")) dest=true;
	    return dest;
	}

    /**
     * Method that configures the API based off the credentials we pass to it.
     * @param username
     * @param password
     */
    public PostalRate(String username, String password) {
        ClientConfig config = new DefaultClientConfig();
        aClient = Client.create(config);
        aClient.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(username, password));
    }

    /**
     * Creates a way for the API to communicate with the program.
     */
    public ClientResponse createMailingScenario(Object xml) throws UniformInterfaceException {
        WebResource aWebResource = aClient.resource(LINK);
        return aWebResource.accept("application/vnd.cpc.ship.rate-v3+xml").header("Content-Type", "application/vnd.cpc.ship.rate-v3+xml").acceptLanguage("en-CA").post(ClientResponse.class, xml);
    }

    /**
     * Deletes the client object.
     */
    public void close() {
        aClient.destroy();
    }
}
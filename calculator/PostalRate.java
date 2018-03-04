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

public class PostalRate {
	public static float length, width, height, weight;
	public double distRate,packageRate,totalRate;
	public String destPostalCode;
	public String sourcePostalCode;
	public PostType postType;
	public static Destination destination;
	private Client aClient;
	private static final String LINK = "https://ct.soa-gw.canadapost.ca/rs/ship/price";
	
//	public enum Destination {
//        AB, BC, MB, NB, NL, NS, NT, NU, ON, PE, QC, SK, YT
//	}
	public enum PostType {
		RegularParcel, Xpresspost, Priority
	}

	public static void main (String args[]) {
		String sourcePC=args[0].toUpperCase();
		String destPC=args[1].toUpperCase();
		String postType = args[6];
		width=Float.valueOf(args[2]);
		length=Float.valueOf(args[3]);
		height=Float.valueOf(args[4]);
		weight=Float.valueOf(args[5]);
		
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
		        System.out.print("Length + 2(Width + Height) must be at most 300 cm\n");
            }
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
            
            System.out.println("HTTP Response Status: " + resp.getStatus() + " " + resp.getClientResponseStatus());

            // Example of using JAXB to parse xml response
            JAXBContext jc;
            try {
            	jc = JAXBContext.newInstance(PriceQuotes.class, Messages.class);
                Object entity = jc.createUnmarshaller().unmarshal(respIS);
                // Determine whether response data matches GetRatesInfo schema.
                if (entity instanceof PriceQuotes) {
                	PriceQuotes priceQuotes = (PriceQuotes) entity;
                    for (Iterator<PriceQuotes.PriceQuote> iter = priceQuotes.getPriceQuotes().iterator(); iter.hasNext();) { 
                    	PriceQuotes.PriceQuote aPriceQuote = (PriceQuotes.PriceQuote) iter.next();   
                    		if(aPriceQuote.getServiceName().equalsIgnoreCase(postType) || ((aPriceQuote.getServiceName().equalsIgnoreCase("Regular Parcel") && postType.equals("Regular") ) )) {
                    			System.out.println("Service Name: " + aPriceQuote.getServiceName());
                    			System.out.println("Price: $" + aPriceQuote.getPriceDetails().getDue() + "\n");
                    		}
                    }
                } else {
                    // Assume Error Schema
                    Messages messageData = (Messages) entity;
                    for (Iterator<Messages.Message> iter = messageData.getMessage().iterator(); iter.hasNext();) {
                        Messages.Message aMessage = (Messages.Message) iter.next();
                        System.out.println("Error Code: " + aMessage.getCode());
                        System.out.println("Error Msg: " + aMessage.getDescription());
                    }
                }
            } catch (Exception e) {
            	e.printStackTrace(System.out);
            }

            myClient.close();   
        }
	}

	
    public static boolean validDimensions(String length, String width, String height){
        boolean validDimensions = false;
        float l = Float.valueOf(length);
        float w = Float.valueOf(width);
        float h = Float.valueOf(height);
        if(l+(w+h)*2<=300.00){
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

    public PostalRate(String username, String password) {
        ClientConfig config = new DefaultClientConfig();
        aClient = Client.create(config);
        aClient.addFilter(new com.sun.jersey.api.client.filter.HTTPBasicAuthFilter(username, password));
    }

    public ClientResponse createMailingScenario(Object xml) throws UniformInterfaceException {
        WebResource aWebResource = aClient.resource(LINK);
        return aWebResource.accept("application/vnd.cpc.ship.rate-v3+xml").header("Content-Type", "application/vnd.cpc.ship.rate-v3+xml").acceptLanguage("en-CA").post(ClientResponse.class, xml);
    }

    public void close() {
        aClient.destroy();
    }
}
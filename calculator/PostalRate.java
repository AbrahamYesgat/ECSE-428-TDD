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
		REGULAR, XPRESS, PRIORITY
	}

	public static void main (String args[]) {
		String sourcePC=args[1];
		String destPC=args[2];
//		Destination SRC=getProvince(sourcePC);
//		Destination DEST=getProvince(destPC);
		String postType = args[8];
		PostType PT=getPostType(postType);
		width=Float.valueOf(args[3]);
		length=Float.valueOf(args[4]);
		height=Float.valueOf(args[5]);
		float multiplier = getMultiplier(height, length, width, weight, PT);
	
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
//        	FileInputStream propInputStream;
//    		try {
//    			propInputStream = new FileInputStream("user.properties");
//    			userProps.load(propInputStream);
//    			propInputStream.close(); // better in finally block
//    		} catch (Exception e) {
//    			// TODO Auto-generated catch block
//    			e.printStackTrace(System.out);
//    			return;
//    		}
        	System.out.println("trgernbgrgnergerlign");
        	String username = userProps.getProperty("ECSE428B");
        	String password = userProps.getProperty("Ecse-428");
        	String mailedBy = userProps.getProperty("0008688906"); 
    		
    		// Create GetRates XML Request Object
    		MailingScenario mailingScenario = new MailingScenario();
    		
    		mailingScenario.setCustomerNumber(mailedBy);

    		MailingScenario.ParcelCharacteristics parcelCharacteristics = new MailingScenario.ParcelCharacteristics();
    		parcelCharacteristics.setWeight(new BigDecimal(weight));
    		Dimensions dim = new Dimensions();
    		dim.setHeight(new BigDecimal(height));
    		dim.setLength(new BigDecimal(weight));
    		dim.setWidth(new BigDecimal(length));
    		parcelCharacteristics.setDimensions(dim);
    		mailingScenario.setParcelCharacteristics(parcelCharacteristics);
    		mailingScenario.setOriginPostalCode("T6G2R3");
    		Domestic domestic = new Domestic();
    		domestic.setPostalCode("H9W6C3");		
    		Destination destination = new Destination();
    		destination.setDomestic(domestic);
    		mailingScenario.setDestination(destination);

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
    	                System.out.println("Service Name: " + aPriceQuote.getServiceName());
    	                System.out.println("Price: $" + aPriceQuote.getPriceDetails().getDue() + "\n");
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
	
	/*This method calculates the rate for the corresponding postal codes
	 * Postal codes beginning with:
	 * A=NL; B=NS; C=PE; E=NB;
	 * G,H,J=QC; K,L,M,N,P=ON;
	 * R=MB; S=SK; T=AB; V=BC; X=NU/NTY=YT.
	 */
//	public static Destination getProvince(String PostalCode) {
//		PostalCode.toUpperCase();
//		Destination dest = null;
//		if(PostalCode.startsWith("A")) dest=Destination.NL;
//		if(PostalCode.startsWith("B")) dest=Destination.NS;
//		if(PostalCode.startsWith("C")) dest=Destination.PE;
//		if(PostalCode.startsWith("E")) dest=Destination.NB;
//		if(PostalCode.startsWith("G") || PostalCode.startsWith("H") || PostalCode.startsWith("J")) dest=Destination.QC;
//		if(PostalCode.startsWith("K") || PostalCode.startsWith("L") || PostalCode.startsWith("M") || PostalCode.startsWith("N")|| PostalCode.startsWith("P")) dest=Destination.ON;
//		if(PostalCode.startsWith("R")) dest=Destination.MB;
//		if(PostalCode.startsWith("S")) dest=Destination.SK;
//		if(PostalCode.startsWith("T")) dest=Destination.AB;
//		if(PostalCode.startsWith("V")) dest=Destination.BC;
//		if(PostalCode.startsWith("X")) dest=Destination.NU;
//		if(PostalCode.startsWith("Y")) dest=Destination.YT;
//		return dest;
//	}
	public static float getMultiplier(float h, float l, float w, float weight, PostType PT) {
		float densityFactor=0f;
		float volEquiv;
		float vol=h*l*w;
		if(PT== PostType.REGULAR) densityFactor=5000;
		else if (PT== PostType.PRIORITY || PT==PostType.XPRESS) densityFactor=6000;
		volEquiv=vol/densityFactor;
		if(volEquiv>weight) return volEquiv;
		else return weight;
	}
	
	public static PostType getPostType(String PT) {
		PostType Regular=PostType.REGULAR;
		PostType Xpress=PostType.XPRESS;
		PostType Priority=PostType.PRIORITY;
		if(PT.equalsIgnoreCase("Regular")) return Regular;
		else if(PT.equalsIgnoreCase("Express")) return Xpress;
		else if(PT.equalsIgnoreCase("Priority")) return Priority;
		else return null;
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
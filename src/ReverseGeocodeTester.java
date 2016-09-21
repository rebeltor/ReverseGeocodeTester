import java.io.*;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import com.google.gson.Gson;

public class ReverseGeocodeTester {

	public static void main(String[] args) {
		//
		// define input and output files
		//
		String inputFile = "/Users/torrancejones/Documents/input.txt";
		String outputFile = "/Users/torrancejones/Documents/output.txt";
		
		//
		// visit the Google Maps API website and sign up for an API key
		// keep this key secret so at to prevent key hijacking
		// as of September 2016, basic free accounts allow for 2500 lookups per day
		//
		String apiKey = "xxxxxxxxxxxxxxx";
		
		System.out.println("Reverse Geocode Tester Program");
		System.out.println("============================================================");
		System.out.println("This program takes a file of input (ticket data) and runs");
		System.out.println("data through Google Maps API to see if the GPS Lat-Long");
		System.out.println("matches with what was created during issuance");
		System.out.println("============================================================");
		System.out.println("Input File:" + inputFile);
		System.out.println("Output File:" + outputFile);

		// error check, be sure that the file exists
		if (!new File(inputFile).isFile())
		{
			System.err.println("Input file does not exist: " + inputFile);
			System.exit(1);
		}
		
		//
		// open the input file
		// for each line in the file
		// ensure there are enough data fields to do a lookup (location/lat/long)
		// query the maps API to obtain a json string
		// parse the json string and write an entry in the output file
		//
		try {
			FileInputStream fstream = new FileInputStream(inputFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			Boolean firstLine = true;
			
			// read each line in the file
			while ((strLine = br.readLine()) != null) {
				// skip the header
				if (firstLine == true) { 
					firstLine = false; 
					continue;
				}
				
				String[] items = strLine.split("\\t");
				
				// data check to ensure the line has enough elements
				// TICKET_NUMBER,DIRECTION,BLOCK,LOCATION_DESC,LATITUDE,LONGITUDE
				if (items.length == 6) {
					// process the query through the API
					String ticketNumber = items[0];
					String direction = items[1];
					String block = items[2];
					String locationDesc = items[3];
					String latitude = items[4];
					String longitude = items[5];
					
					System.out.println("Query " + ticketNumber + " " + locationDesc);
					String result = ReverseGeoCode(latitude, longitude, apiKey);
					System.out.println("\t" + "Found: " + result);
					String outline = ticketNumber + "\t" + direction + "\t" + block + "\t" + locationDesc + "\t" + latitude + "\t" + longitude + "\t" + result;
					
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)))) {
					    out.println(outline);
					}catch (IOException e) {
					    System.err.println(e);
					}
					
					// wait so that information written to screen scrolls slow enough to read - debug
					Thread.sleep(25);
					
				} else {
					// error, invalid data, skip and move on
					System.out.println("Invalid input line, not enough elements");
					continue;
				}
			}
			//Close the input stream
			in.close();
		} catch (Exception e) {
			// catch exceptions: file permission, etc
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
		
	}
	
	public static boolean isNullOrBlank(String param) { 
	    return param == null || param.trim().length() == 0;
	}
	
	public static String QueryURL(String url)
	{
		try {
			URL u = new URL(url);
			HttpsURLConnection connection = (HttpsURLConnection)u.openConnection();
			InputStream input = connection.getInputStream();
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
	
		    String strLine;
		    String json = "";
		    while ((strLine = br.readLine()) != null)
		    {
		      if (!isNullOrBlank(strLine)) {
		    	  json = json + strLine + System.lineSeparator();
		      }
		    }
		    
		    input.close();
		    
		    return json;
		} catch (Exception e) {
			return "";
		}
		
	}

	public static String ReverseGeoCode(String latitude, String longitude, String apiKey)
	{
		try
		{
			String queryUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=" + apiKey;
			String json = QueryURL(queryUrl);
			
			//
			// translate the json string to a java class using gson library
			// https://github.com/google/gson/blob/master/UserGuide.md
			//
			Gson gson = new Gson();
			GoogleGeoCodeResponse r = gson.fromJson(json, GoogleGeoCodeResponse.class);
			
			if (r.status.equals("OK"))
			{
				String block = r.results[0].address_components[0].long_name;
				String street = r.results[0].address_components[1].short_name;
				return block + " " + street;
			}
			else
			{
				return "NOT_FOUND";
			}
		} catch(Exception e) {
			return e.getMessage();
		}
	}
	
	//
	// define the return classes for gson
	//
	public class GoogleGeoCodeResponse {
		public results[] results;
		public String status;
	    public GoogleGeoCodeResponse() { }
	}

	public class results {
		public address_component[] address_components;
		public String formatted_address;
        public geometry geometry;
        public String place_id;
        public String[] types;
    }

    public class geometry{
    	public location location;
    	public String location_type;
    	public bounds viewport;
    	//public bounds bounds;
    }

    public class bounds {
         public location northeast;
         public location southwest;
    }

    public class location{
        public String lat;
        public String lng;
    }

    public class address_component{
        public String long_name;
        public String short_name;
        public String[] types;
    }
	    
}





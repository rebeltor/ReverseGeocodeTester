#ReverseGeocodeTester
Purpose: to double check the gps locations returned from Android against the Google Reverse Geocode Street Address

To use: 
- Get your API Key from Google
- Gather Lat,Long from a pre existing data source to use as a baseline, in my case, I used parking ticket data
- Format the file of data into a tab delimited file with columns: Ticket#, direction, block, location, lat, long
- Run the program
- Post analysis done independently of this program

Requirements:
- gson 2.7 (https://github.com/google/gson)

Development Environment
- MacOS Yosemite
- Eclipse Neon (https://eclipse.org/downloads/)
- Java JDK 8u101 (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

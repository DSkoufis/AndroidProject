package mobile.uom.gr.androidproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/*
 * Class that fills the ListView and shows the API response
 */

public class FlightsActivity extends Activity {

    String departure_airport;
    String destination_airport;
    String adults;
    String children;
    String infants;
    String departure_date;
    String returning_date;
    String seat_type;
    String isDirect;

    CustomAdapter myAdapter;
    ListView myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_flights);
        setTitle(R.string.activity_flights);

        myList = (ListView) findViewById(R.id.listView_flights);

        // getting the extra data from Intent
        Intent intent = getIntent();
        departure_airport = intent.getStringExtra("ORIGIN_AIRPORT");
        destination_airport = intent.getStringExtra("DESTINATION_AIRPORT");
        adults = intent.getStringExtra("ADULTS");
        children = intent.getStringExtra("CHILDREN");
        infants = intent.getStringExtra("INFANTS");
        departure_date = intent.getStringExtra("DEPARTURE_DATE");
        returning_date = intent.getStringExtra("RETURN_DATE");
        seat_type = intent.getStringExtra("SEAT_TYPE").toUpperCase();
        isDirect = intent.getStringExtra("IS_DIRECT");

        // calling the Amadeus
        GetFlightsTask flightsTask = new GetFlightsTask();
        flightsTask.execute();
    }

    // this AsyncTask gets the data we want from the vars in FlightsActivity class and returns the JSON response
    public class GetFlightsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // These two need to be declared outside the try/catch, so that they can be closed in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String responseJsonStr = null; // this will hold the raw JSON response as a String

            try {
                // Constructing the parameters of the URL for the Amadeus Airport Autocomplete query
                // see https://sandbox.amadeus.com/travel-innovation-sandbox/apis/get/flights/low-fare-search
                final String baseUrl = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
                final String apiKeyParam = "apikey";
                final String originParam = "origin";
                final String destinationParam = "destination";
                final String departureParam = "departure_date";
                final String returnParam = "return_date";
                final String adultsParam = "adults";
                final String childrenParam = "children";
                final String infantsParam = "infants";
                final String nonstopParam = "nonstop";
                final String currencyParam = "currency";
                final String seatParam = "travel_class";

                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(apiKeyParam, BuildConfig.AMADEUS_API_KEY) // getting the API key
                        .appendQueryParameter(originParam, departure_airport) // getting the origin airport
                        .appendQueryParameter(destinationParam, destination_airport) // getting the destination airport
                        .appendQueryParameter(departureParam, departure_date) // getting the departure date
                        .appendQueryParameter(returnParam, returning_date) // getting the return date
                        .appendQueryParameter(adultsParam, adults) // setting the passengers
                        .appendQueryParameter(childrenParam, children)
                        .appendQueryParameter(infantsParam, infants)
                        .appendQueryParameter(nonstopParam, isDirect) // telling if it is a direct flight or not
                        .appendQueryParameter(currencyParam, "EUR") // setting the price currency
                        .appendQueryParameter(seatParam, seat_type) // setting the preferred seat type
                        .build(); // build the URL

                URL url = new URL(builtUri.toString());

                Log.v("Built URI: ", builtUri.toString()); // see if it builded correctly

                // Create the request to Amadeus, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                responseJsonStr = buffer.toString();
                Log.v("Flights JSON String: ", responseJsonStr);

            } catch (Exception e) {
                Log.e("Error ", String.valueOf(e));
                // If the code didn't successfully get the flights data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Error closing stream ", String.valueOf(e));
                    }
                }
            }
            return responseJsonStr;
        }

        @Override
        protected void onPostExecute(String responseJson) {
            printingData(responseJson); // calling this method to print our data with the help from custom adapter
        }
    }

    public void printingData(String responseJsonStr) {

        if (responseJsonStr == null) { // if there are no results
            // show an error Toast
            Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_LONG).show();
            // and start new SearchFlights Activity
            Intent intent = new Intent(this, SearchFlightsActivity.class);
            startActivity(intent);
            return; // we need this to terminate this Activity from executing further code
        }

        Log.i("JSON response till now", "ALL FINE");

        ArrayList<FlightModel> flight_list = new ArrayList<FlightModel>();

        // These are the names of JSON objects we want to extract
        final String RESULTS = "results"; // (ARRAY) it holds objects of all the flights results
        final String ITINERARIES = "itineraries"; // it holds the details of a single trip (outbound - inbound)
        final String FARE = "fare"; // it holds the fare details
        final String TOTAL_PRICE = "total_price"; // it holds the total price of the flight
        final String OUTBOUND = "outbound"; // it holds the outbound flight
        final String INBOUND = "inbound"; // it holds the inbound flight
        final String FLIGHTS = "flights"; // (ARRAY) it holds the objects of flights of the trip
        final String DEPART = "departs_at"; // it holds the time of departure in ISO 8601 format
        final String ORIGIN = "origin"; // it holds the origin airport
        final String DESTINATION = "destination"; // it holds the destination airport

        try {

            // turning String into JSON object
            JSONObject resultJson = new JSONObject(responseJsonStr);
            JSONArray resultsArray = resultJson.getJSONArray(RESULTS);

            // getting all results one by one
            for(int i = 0; i < resultsArray.length(); i++) {
                // these will be used to hold the Strings that we later set into TextViews
                String an_outbound_flight = "";
                String an_outbound_flight_time = "";
                String an_inbound_flight = "";
                String an_inbound_flight_time = "";
                String a_price = "";

                JSONObject flightJson = resultsArray.getJSONObject(i);

                // getting the total price
                JSONObject fareJson = flightJson.getJSONObject(FARE); // first we need to enter the "fare" object
                a_price = fareJson.getString(TOTAL_PRICE); // so now we have a String with the total price of trip

                // entering the "itineraries" ARRAY to get the flight's details
                JSONArray itinerariesArray = flightJson.getJSONArray(ITINERARIES);

                /* NOTE: "itineraries" array usually holds more that one objects
                 *       unfortunately I don't know what is the difference from the first object
                 *       so we are using only the first (0) object to show flights */
                JSONObject flightDetailsJson = itinerariesArray.getJSONObject(0);

                // first we are working on outbound flight
                JSONObject out_flightsJson = flightDetailsJson.getJSONObject(OUTBOUND);
                JSONArray out_flightsArray = out_flightsJson.getJSONArray(FLIGHTS);
                for (int x = 0; x < out_flightsArray.length(); x++) { // getting all flights one by one
                    JSONObject an_out_flightJson = out_flightsArray.getJSONObject(x);
                    if (x == 0) { // only for the first flight
                        an_outbound_flight_time = an_out_flightJson.getString(DEPART);
                        an_outbound_flight = an_out_flightJson.getJSONObject(ORIGIN).getString("airport") +
                                "-" + an_out_flightJson.getJSONObject(DESTINATION).getString("airport");
                    } else {
                        an_outbound_flight += "-" + an_out_flightJson.getJSONObject(DESTINATION).getString("airport");
                    }
                }

                // second we are working on inbound flight
                JSONObject in_flightsJson = flightDetailsJson.getJSONObject(INBOUND);
                JSONArray in_flightsArray = in_flightsJson.getJSONArray(FLIGHTS);
                for (int x = 0; x < in_flightsArray.length(); x++) {
                    JSONObject an_in_flightJson = in_flightsArray.getJSONObject(x);
                    if (x == 0) { // only for the first loop
                        an_inbound_flight_time = an_in_flightJson.getString(DEPART);
                        an_inbound_flight = an_in_flightJson.getJSONObject(ORIGIN).getString("airport") +
                                "-" + an_in_flightJson.getJSONObject(DESTINATION).getString("airport");
                    } else {
                        an_inbound_flight += "-" + an_in_flightJson.getJSONObject(DESTINATION).getString("airport");
                    }
                }

                //TODO: give better appearance at displayed time (now its yy-MM-ddThh-mm ISO 8601 format)

                FlightModel aFlight = new FlightModel(an_outbound_flight, an_outbound_flight_time,
                        an_inbound_flight, an_inbound_flight_time, a_price);
                flight_list.add(aFlight);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Creating the custom adapter
        myAdapter = new CustomAdapter(this, getResources(), flight_list);
        myList.setAdapter(myAdapter);
    }
}

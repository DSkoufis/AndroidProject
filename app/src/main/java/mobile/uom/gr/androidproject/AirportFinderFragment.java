package mobile.uom.gr.androidproject;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that shows the airport search in
 * DestinationActivity.java and OriginActivity.java
 */

public class AirportFinderFragment extends Fragment {

    ArrayAdapter<String> adapter_airports;
    private EditText editText_city;
    private Button button_search;

    /* ------------------------------------------------------------------------------------------------ */
    // interface to communicate with parent activity (solution found on stackoverflow)
    public interface OnDataPass {
        void onDataPass(String data);
    }
    OnDataPass someData;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            someData = (OnDataPass) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }
    /* ------------------------------------------------------------------------------------------------ */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<String> airports = new ArrayList<String>(); // The objects to represent in the ListView

        // creating the ArrayAdapter
        adapter_airports = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_airports_fragment, // The name of the layout ID
                R.id.list_item_airport, // The ID of the textview to populate
                airports);

        View view = inflater.inflate(R.layout.airports_fragment, container, false);

        // initializing editText where we take the input from user
        editText_city = (EditText) view.findViewById(R.id.editText_city);
        button_search = (Button) view.findViewById(R.id.button_select_city);

        // Getting the reference to the ListView of the fragment and setting ArrayAdapter
        ListView listView = (ListView) view.findViewById(R.id.listView_airports);
        listView.setAdapter(adapter_airports);

        //
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this when "Search" button is pressed
                String city = editText_city.getText().toString();

                while (city.equals("")) { // if there is no name show a Toast until user writes something
                    Toast.makeText(getActivity().getApplicationContext(), "You must enter a city name first", Toast.LENGTH_SHORT).show();
                    return;
                }

                GetAirportsTask airportsTask = new GetAirportsTask();
                airportsTask.execute(city);
            }
        });

        // setting the code for when an item(airport) is pressed, it returned to the previous
        // activity which is SearchFlightsActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String airport = adapter_airports.getItem(position).toString();
                someData.onDataPass(airport); // passing the string with the data to container Activity to return to parent
            }
        });

        return view;
    }


    public class GetAirportsTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... city) {
            // if there is no city, there is nothing to do
            if (city.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch, so that they can be closed in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String responseJsonStr = null; // this will hold the raw JSON response as a String

            try {
                // Constructing the parameters of the URL for the Amadeus Airport Autocomplete query
                // see https://sandbox.amadeus.com/travel-innovation-sandbox/apis/get/airports/autocomplete
                final String baseUrl = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?";
                        // "apikey=123456789abcdefgh&term=lon"
                final String apiKeyParam = "apikey";
                final String termParam = "term";

                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(apiKeyParam, BuildConfig.AMADEUS_API_KEY) // getting the API key
                        .appendQueryParameter(termParam, city[0]) // getting the city name
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
                Log.v("Airport JSON String: ", responseJsonStr);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("Error ", String.valueOf(e));
                // If the code didn't successfully get the weather data, there's no point in attempting
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
            try {
                return getDataFromJson(responseJsonStr); // try to return the Json in the format we want
                                                        // by calling getDataFromJson (see bellow for the method)
            } catch (JSONException e) {
                Log.e(e.getMessage(), String.valueOf(e));
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // add the results to adapter
            if (result != null) {
                adapter_airports.clear();
                for (String airport : result) {
                    adapter_airports.add(airport);
                }
            }
        }

        private String[] getDataFromJson (String responseJsonStr) throws JSONException {

            // These are the values of the JSON object that need to be extracted
            final String AMADEUS_VALUE = "value";
            final String AMADEUS_LABEL = "label";

            // Response is always an Array
            JSONArray responseArray = new JSONArray(responseJsonStr);

            // this way we can fing how many airports it returned
            String[] resultStrs = new String[responseArray.length()];

            for (int i = 0; i < responseArray.length(); i++) {
                // We are using the format AirportCode, Aiport
                String value;
                String label;

                // getting the Objects in order from the Array
                JSONObject airport = responseArray.getJSONObject(i);

                // getting code(value) and airport(label) from JSON Object
                value = airport.getString(AMADEUS_VALUE);
                label = airport.getString(AMADEUS_LABEL);

                // put them in the results String in format as said above
                resultStrs[i] = value + " - " + label;
            }
            // logging the data for no reason but why not?
            for (String s : resultStrs) {
                Log.v("Airport: ", s);
            }

            return resultStrs;
        }
    }
}




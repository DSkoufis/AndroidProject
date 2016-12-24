package mobile.uom.gr.androidproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Class that gathers all data from user selections
 * and calls the API to show the returned flights in FlightsActivity.java
 */

public class SearchFlightsActivity extends AppCompatActivity {

    private static TextView location_tv; //textView that shows the city that user selected in OriginActivity.java
    private static TextView destination_tv; //textView that shows the city that user selected in DestinationActivity.java
    private static TextView adults_tv; //textView that shows the number of adults that the user selected in PassengerSelectionActivity
    private static TextView children_tv; //textView that shows the number of children that the user selected in PassengerSelectionActivity
    private static TextView infants_tv; //textView that shows the number of infants that the user selected in PassengerSelectionActivity

    //strings that holds the name of the airports in IATA code format
    private String origin_airport;
    private String destination_airport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);
        this.setTitle("Select your search criteria");


        /*
         * This class have a spinner that allows the user to select the desirable seat type
         * See string.xml for:
         *   <string-array name="seats_array">
         *       <item>Economy</item>
         *       <item>Premium economy</item>
         *       <item>Business class</item>
         *       <item>First class</item>
         *   </string-array>
         */
        Spinner spinner = (Spinner) findViewById(R.id.seats_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.seats_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        location_tv = (TextView) findViewById(R.id.origin_text);
        destination_tv = (TextView) findViewById(R.id.destination_text);
        adults_tv = (TextView) findViewById(R.id.adults_text);
        children_tv = (TextView) findViewById(R.id.children_text);
        infants_tv = (TextView) findViewById(R.id.infants_text);


    }

    public void setLocation(View view) {
        //this when 'Select your origin' btn is pressed
        Intent intent = new Intent(this, OriginActivity.class);
        startActivity(intent);
    }

    public void setDestination(View view) {
        //this when 'Select your destination' btn is pressed
        Intent intent = new Intent(this, DestinationActivity.class);
        startActivity(intent);
    }

    public void showFlights(View view) {
        //this when 'Search for flights' btn is pressed
        Intent intent = new Intent(this, FlightsActivity.class);
        startActivity(intent);
    }

    public void showNumberPicker(View view) {
        //this when 'Set passengers' btn is pressed
        Intent intent = new Intent(this, PassengerSelectorActivity.class);
        intent.putExtra("ADULTS", adults_tv.getText());     //we must give the previous numbers from TextViews
        intent.putExtra("CHILDREN", children_tv.getText());
        intent.putExtra("INFANTS", infants_tv.getText());
        startActivityForResult(intent, 1);
    }

    @Override
    //This method is when we finish() the children method
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { //code==1 when we change data in PassengerSelectorActivity
            adults_tv.setText(data.getStringExtra("ADULTS"));
            children_tv.setText(data.getStringExtra("CHILDREN"));
            infants_tv.setText(data.getStringExtra("INFANTS"));
        }
        else if (requestCode == 2) { //code==2 when we cancel in PassengerSelectorActivity
            adults_tv.setText(data.getStringExtra("ADULTS"));
            children_tv.setText(data.getStringExtra("CHILDREN"));
            infants_tv.setText(data.getStringExtra("INFANTS"));
        }
    }
}

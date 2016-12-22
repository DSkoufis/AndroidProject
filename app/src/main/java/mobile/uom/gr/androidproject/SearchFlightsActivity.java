package mobile.uom.gr.androidproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchFlightsActivity extends AppCompatActivity {

    private static TextView location_tv;
    private static TextView destination_tv;
    private static TextView adults_tv;
    private static TextView children_tv;
    private static TextView infants_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);
        this.setTitle("Select your search criteria");
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

        Button location_but = (Button) findViewById(R.id.set_origin);
        Button destination_but = (Button) findViewById(R.id.set_destination);
        location_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation(v);
            }
        });
        destination_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDestination(v);
            }
        });

        Button passengers_but = (Button) findViewById(R.id.select_passengers_button);
        passengers_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker();
            }
        });

    }

    public void setLocation(View view) {
        Intent intent = new Intent(this, OriginActivity.class);
        startActivity(intent);
    }

    public void setDestination(View view) {
        Intent intent = new Intent(this, DestinationActivity.class);
        startActivity(intent);
    }

    public void showNumberPicker() {

    }


}

package mobile.uom.gr.androidproject;

import android.app.Activity;
import android.os.Bundle;

/*
 * Class that fills the ListView and shows the API response
 */

public class FlightsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_flights);
        setTitle(R.string.activity_flights);

        //TODO: make API call and build a custom adapter to fill the list view with ListItems

    }
}

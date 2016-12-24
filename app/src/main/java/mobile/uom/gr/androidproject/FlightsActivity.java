package mobile.uom.gr.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Class that shows the returned flights from API
 */

public class FlightsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_flights);
    }
}
